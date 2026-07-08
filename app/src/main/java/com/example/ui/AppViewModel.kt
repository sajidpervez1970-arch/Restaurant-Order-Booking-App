package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.data.CartItem
import com.example.data.MenuItem
import com.example.data.OrderHistory
import com.example.data.Restaurant
import com.example.data.RestaurantData
import com.example.data.TableBooking
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class WhatsAppIntentData(
    val phoneNumber: String,
    val message: String
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository
    private val prefs = application.getSharedPreferences("app_theme_prefs", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(prefs.getString("theme_mode", "system") ?: "system")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    fun toggleTheme() {
        val current = _themeMode.value
        val next = when (current) {
            "light" -> "dark"
            "dark" -> "system"
            else -> "light"
        }
        prefs.edit().putString("theme_mode", next).apply()
        _themeMode.value = next
    }

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AppRepository(
            cartDao = database.cartDao(),
            orderDao = database.orderDao(),
            bookingDao = database.bookingDao()
        )
    }

    // Database flows
    val cartItems: StateFlow<List<CartItem>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<OrderHistory>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBookings: StateFlow<List<TableBooking>> = repository.allBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI State
    private val _selectedRestaurant = MutableStateFlow<Restaurant?>(null)
    val selectedRestaurant: StateFlow<Restaurant?> = _selectedRestaurant.asStateFlow()

    private val _selectedCity = MutableStateFlow("All")
    val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Shared flow to emit WhatsApp actions that the Activity/UI should trigger
    private val _whatsAppAction = MutableSharedFlow<WhatsAppIntentData>()
    val whatsAppAction: SharedFlow<WhatsAppIntentData> = _whatsAppAction.asSharedFlow()

    // Active Order tracking for Countdown Timer
    private val _activeOrder = MutableStateFlow<ActiveOrderState?>(null)
    val activeOrder: StateFlow<ActiveOrderState?> = _activeOrder.asStateFlow()

    fun clearActiveOrder() {
        _activeOrder.value = null
    }

    fun cancelActiveOrder() {
        _activeOrder.value = _activeOrder.value?.copy(isCancelled = true)
    }

    fun fastForwardActiveOrder(minutes: Int) {
        _activeOrder.value = _activeOrder.value?.let { current ->
            current.copy(timeOffsetMillis = current.timeOffsetMillis + (minutes * 60 * 1000L))
        }
    }

    // Filtered restaurants
    val filteredRestaurants: StateFlow<List<Restaurant>> = combine(
        _selectedCity,
        _searchQuery
    ) { city, query ->
        RestaurantData.sampleRestaurants.filter { restaurant ->
            val matchesCity = city == "All" || restaurant.city.equals(city, ignoreCase = true)
            val matchesSearch = restaurant.name.contains(query, ignoreCase = true) ||
                    restaurant.description.contains(query, ignoreCase = true) ||
                    restaurant.city.contains(query, ignoreCase = true)
            matchesCity && matchesSearch
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RestaurantData.sampleRestaurants)

    fun selectRestaurant(restaurant: Restaurant?) {
        _selectedRestaurant.value = restaurant
    }

    fun selectCity(city: String) {
        _selectedCity.value = city
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Cart Operations
    fun addToCart(restaurant: Restaurant, item: MenuItem, quantity: Int, customization: String = "") {
        viewModelScope.launch {
            // Check if cart already has items from another restaurant
            val currentItems = cartItems.value
            if (currentItems.isNotEmpty() && currentItems.any { it.restaurantId != restaurant.id }) {
                // Clear cart for previous restaurant before adding from new one
                repository.clearCart()
            }

            // Check if item already exists in cart, then update quantity
            val existingItem = currentItems.find { it.itemId == item.id && it.customization == customization }
            if (existingItem != null) {
                repository.updateCartItem(
                    existingItem.copy(quantity = existingItem.quantity + quantity)
                )
            } else {
                repository.addToCart(
                    CartItem(
                        restaurantId = restaurant.id,
                        restaurantName = restaurant.name,
                        restaurantPhone = restaurant.phone,
                        itemId = item.id,
                        itemName = item.name,
                        itemPrice = item.price,
                        quantity = quantity,
                        customization = customization
                    )
                )
            }
        }
    }

    fun updateCartItemQuantity(cartItem: CartItem, newQuantity: Int) {
        viewModelScope.launch {
            if (newQuantity <= 0) {
                repository.deleteFromCart(cartItem)
            } else {
                repository.updateCartItem(cartItem.copy(quantity = newQuantity))
            }
        }
    }

    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            repository.deleteFromCart(cartItem)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    // Order Submission via WhatsApp
    fun submitOrder(customerName: String, phone: String, address: String, deliveryNote: String) {
        val items = cartItems.value
        if (items.isEmpty()) return

        val restaurantName = items[0].restaurantName
        val restaurantPhone = items[0].restaurantPhone
        val subtotal = items.sumOf { it.itemPrice * it.quantity }
        val restaurant = RestaurantData.sampleRestaurants.find { it.id == items[0].restaurantId }
        val deliveryFee = restaurant?.deliveryFee ?: 150.0
        val gstTax = subtotal * 0.13 // Standard 13% GST in Pakistan
        val total = subtotal + deliveryFee + gstTax

        val orderId = "PAK-ORD-${UUID.randomUUID().toString().take(6).uppercase(Locale.ROOT)}"

        viewModelScope.launch {
            // 1. Create formatted summary of items
            val itemLines = items.joinToString("\n") { item ->
                val custText = if (item.customization.isNotBlank()) " (${item.customization})" else ""
                "• ${item.quantity}x ${item.itemName} - Rs. ${(item.itemPrice * item.quantity).toInt()}$custText"
            }

            // 2. Format a gorgeous WhatsApp checkout message
            val receiptMessage = """
                *🇵🇰 PAK FOOD DELIVERY - ORDER CONFIRMATION*
                ----------------------------------------
                *Order ID:* #$orderId
                *Restaurant:* $restaurantName
                
                *Customer Details:*
                • *Name:* $customerName
                • *Phone:* $phone
                • *Delivery Address:* $address
                ${if (deliveryNote.isNotBlank()) "• *Rider Note:* $deliveryNote\n" else ""}
                *Order Items:*
                $itemLines
                
                *Price Breakdown:*
                • *Subtotal:* Rs. ${subtotal.toInt()}
                • *Delivery Fee:* Rs. ${deliveryFee.toInt()}
                • *GST (13%):* Rs. ${gstTax.toInt()}
                ----------------------------------------
                *TOTAL AMOUNT:* Rs. ${total.toInt()}
                ----------------------------------------
                _Thank you for ordering! Please reply to confirm availability and dispatch your rider._
            """.trimIndent()

            // 3. Save order to history
            val orderHistory = OrderHistory(
                orderId = orderId,
                restaurantName = restaurantName,
                itemsSummary = items.joinToString(", ") { "${it.quantity}x ${it.itemName}" },
                totalAmount = total,
                deliveryAddress = address,
                status = "Dispatched via WhatsApp"
            )
            repository.saveOrder(orderHistory)

            // Calculate estimated preparation and total delivery times based on the restaurant's metadata
            val deliveryTimeStr = restaurant?.deliveryTime ?: "30-40 mins"
            val regex = "(\\d+)-(\\d+)".toRegex()
            val matchResult = regex.find(deliveryTimeStr)
            val (minTime, maxTime) = if (matchResult != null) {
                val min = matchResult.groupValues[1].toIntOrNull() ?: 30
                val max = matchResult.groupValues[2].toIntOrNull() ?: 40
                Pair(min, max)
            } else {
                Pair(30, 40)
            }
            val avgTotalMinutes = (minTime + maxTime) / 2
            // Kitchen preparation time is around 40% of the total delivery time (min 10 mins)
            val prepTimeMinutes = (avgTotalMinutes * 0.4).toInt().coerceAtLeast(10)

            _activeOrder.value = ActiveOrderState(
                orderId = orderId,
                restaurantId = restaurant?.id ?: "",
                restaurantName = restaurantName,
                restaurantPhone = restaurantPhone,
                totalAmount = total,
                startTimeMillis = System.currentTimeMillis(),
                prepTimeMinutes = prepTimeMinutes,
                totalTimeMinutes = avgTotalMinutes
            )

            // 4. Clear shopping cart
            repository.clearCart()

            // 5. Emit WhatsApp action
            _whatsAppAction.emit(WhatsAppIntentData(restaurantPhone, receiptMessage))
        }
    }

    // Table Booking via WhatsApp
    fun submitTableBooking(
        restaurant: Restaurant,
        date: String,
        time: String,
        guests: Int,
        contactName: String,
        contactPhone: String,
        specialRequests: String
    ) {
        val bookingId = "PAK-RSV-${UUID.randomUUID().toString().take(6).uppercase(Locale.ROOT)}"

        viewModelScope.launch {
            // 1. Format WhatsApp booking request
            val bookingMessage = """
                *🇵🇰 PAK FOOD DELIVERY - TABLE BOOKING REQUEST*
                ----------------------------------------
                *Booking ID:* #$bookingId
                *Restaurant:* ${restaurant.name}
                
                *Reservation Details:*
                • *Date:* $date
                • *Time:* $time
                • *No. of Guests:* $guests Persons
                
                *Contact Information:*
                • *Name:* $contactName
                • *Phone:* $contactPhone
                ${if (specialRequests.isNotBlank()) "• *Special Request:* $specialRequests\n" else ""}
                ----------------------------------------
                _Please reply with 'CONFIRMED' to secure this table booking. Shadaab Raheye!_
            """.trimIndent()

            // 2. Save booking to local history
            val booking = TableBooking(
                bookingId = bookingId,
                restaurantId = restaurant.id,
                restaurantName = restaurant.name,
                restaurantPhone = restaurant.phone,
                bookingDate = date,
                bookingTime = time,
                guestsCount = guests,
                contactName = contactName,
                contactPhone = contactPhone,
                specialRequests = specialRequests,
                status = "Sent via WhatsApp"
            )
            repository.saveBooking(booking)

            // 3. Emit WhatsApp action
            _whatsAppAction.emit(WhatsAppIntentData(restaurant.phone, bookingMessage))
        }
    }

    fun cancelTableBooking(booking: TableBooking) {
        viewModelScope.launch {
            repository.cancelBooking(booking)
        }
    }
}

class AppViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class ActiveOrderState(
    val orderId: String,
    val restaurantId: String,
    val restaurantName: String,
    val restaurantPhone: String,
    val totalAmount: Double,
    val startTimeMillis: Long,
    val prepTimeMinutes: Int,
    val totalTimeMinutes: Int,
    val timeOffsetMillis: Long = 0L,
    val isCancelled: Boolean = false
)
