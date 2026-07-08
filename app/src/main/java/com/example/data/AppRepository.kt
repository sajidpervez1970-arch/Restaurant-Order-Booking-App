package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val cartDao: CartDao,
    private val orderDao: OrderDao,
    private val bookingDao: BookingDao
) {
    val cartItems: Flow<List<CartItem>> = cartDao.getCartItems()
    val allOrders: Flow<List<OrderHistory>> = orderDao.getAllOrders()
    val allBookings: Flow<List<TableBooking>> = bookingDao.getAllBookings()

    suspend fun addToCart(item: CartItem) {
        cartDao.insertCartItem(item)
    }

    suspend fun updateCartItem(item: CartItem) {
        cartDao.updateCartItem(item)
    }

    suspend fun deleteFromCart(item: CartItem) {
        cartDao.deleteCartItem(item)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }

    suspend fun clearCartForRestaurant(restaurantId: String) {
        cartDao.clearCartForRestaurant(restaurantId)
    }

    suspend fun saveOrder(order: OrderHistory) {
        orderDao.insertOrder(order)
    }

    suspend fun saveBooking(booking: TableBooking) {
        bookingDao.insertBooking(booking)
    }

    suspend fun cancelBooking(booking: TableBooking) {
        bookingDao.deleteBooking(booking)
    }
}
