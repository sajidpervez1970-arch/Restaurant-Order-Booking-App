package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.* // Import all Room entities and sample data
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PakFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
            .border(
                1.dp,
                if (selected) Color.Transparent else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f),
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppMainScreen(
    viewModel: AppViewModel,
    onLaunchWhatsApp: (WhatsAppIntentData) -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    val allBookings by viewModel.allBookings.collectAsStateWithLifecycle()
    val selectedRestaurant by viewModel.selectedRestaurant.collectAsStateWithLifecycle()
    val filteredRestaurants by viewModel.filteredRestaurants.collectAsStateWithLifecycle()
    val selectedCity by viewModel.selectedCity.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val activeOrder by viewModel.activeOrder.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    var showLiveTrackerDialog by remember { mutableStateOf(false) }

    // When an active order is placed, automatically trigger the Live Countdown Tracker!
    LaunchedEffect(activeOrder?.orderId) {
        if (activeOrder != null && !activeOrder!!.isCancelled) {
            showLiveTrackerDialog = true
        }
    }

    // Listen to WhatsApp events
    LaunchedEffect(Unit) {
        viewModel.whatsAppAction.collect { action ->
            onLaunchWhatsApp(action)
        }
    }

    // Navigation State
    var currentTab by remember { mutableStateOf("restaurants") }
    var isLocalMenuViewerActive by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            if (selectedRestaurant == null && !isLocalMenuViewerActive) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 0.dp,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    NavigationBarItem(
                        selected = currentTab == "restaurants",
                        onClick = { currentTab = "restaurants" },
                        icon = { Icon(Icons.Default.Restaurant, contentDescription = "Restaurants") },
                        label = { Text("Restaurants") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.tertiary
                        ),
                        modifier = Modifier.testTag("nav_restaurants")
                    )
                    NavigationBarItem(
                        selected = currentTab == "cart",
                        onClick = { currentTab = "cart" },
                        icon = {
                            BadgedBox(badge = {
                                if (cartItems.isNotEmpty()) {
                                    Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                        Text(cartItems.sumOf { it.quantity }.toString())
                                    }
                                }
                            }) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                            }
                        },
                        label = { Text("Cart") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.tertiary
                        ),
                        modifier = Modifier.testTag("nav_cart")
                    )
                    NavigationBarItem(
                        selected = currentTab == "bookings",
                        onClick = { currentTab = "bookings" },
                        icon = { Icon(Icons.Default.CalendarToday, contentDescription = "Bookings") },
                        label = { Text("Bookings") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.tertiary
                        ),
                        modifier = Modifier.testTag("nav_bookings")
                    )
                    NavigationBarItem(
                        selected = currentTab == "orders",
                        onClick = { currentTab = "orders" },
                        icon = { Icon(Icons.Default.History, contentDescription = "Orders") },
                        label = { Text("Orders") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.tertiary
                        ),
                        modifier = Modifier.testTag("nav_orders")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (selectedRestaurant == null && !isLocalMenuViewerActive) innerPadding else PaddingValues(0.dp))
        ) {
            if (selectedRestaurant != null) {
                RestaurantDetailScreen(
                    restaurant = selectedRestaurant!!,
                    viewModel = viewModel,
                    onBack = { viewModel.selectRestaurant(null) }
                )
            } else if (isLocalMenuViewerActive) {
                LocalMenuViewerScreen(
                    viewModel = viewModel,
                    onBack = { isLocalMenuViewerActive = false }
                )
            } else {
                when (currentTab) {
                    "restaurants" -> RestaurantsTab(
                        restaurants = filteredRestaurants,
                        selectedCity = selectedCity,
                        searchQuery = searchQuery,
                        themeMode = themeMode,
                        onToggleTheme = { viewModel.toggleTheme() },
                        onCitySelect = { viewModel.selectCity(it) },
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onRestaurantSelect = { viewModel.selectRestaurant(it) },
                        onOpenLocalMenu = { isLocalMenuViewerActive = true }
                    )
                    "cart" -> CartTab(
                        cartItems = cartItems,
                        viewModel = viewModel,
                        onBrowseClicked = { currentTab = "restaurants" }
                    )
                    "bookings" -> BookingsTab(
                        bookings = allBookings,
                        onCancelBooking = { viewModel.cancelTableBooking(it) }
                    )
                    "orders" -> OrdersTab(
                        orders = allOrders,
                        activeOrder = activeOrder,
                        onActiveOrderClick = { showLiveTrackerDialog = true },
                        onBrowseClicked = { currentTab = "restaurants" }
                    )
                }
            }

            // Floating Active Order overlay at the bottom center of the active screen
            if (activeOrder != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = if (selectedRestaurant == null && !isLocalMenuViewerActive) 8.dp else 16.dp)
                ) {
                    FloatingActiveOrderCard(
                        activeOrder = activeOrder!!,
                        onClick = { showLiveTrackerDialog = true }
                    )
                }
            }
        }

        // Live Order Status Tracker Dialog
        if (showLiveTrackerDialog && activeOrder != null) {
            LiveOrderTrackerDialog(
                activeOrder = activeOrder!!,
                onDismiss = { showLiveTrackerDialog = false },
                onFastForward = { viewModel.fastForwardActiveOrder(it) },
                onCancel = { viewModel.cancelActiveOrder() }
            )
        }
    }
}

@Composable
fun RestaurantsTab(
    restaurants: List<Restaurant>,
    selectedCity: String,
    searchQuery: String,
    themeMode: String,
    onToggleTheme: () -> Unit,
    onCitySelect: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onRestaurantSelect: (Restaurant) -> Unit,
    onOpenLocalMenu: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        // App Header Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Pak Food Delivery",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = (-0.5).sp
                    )
                )
                Text(
                    text = "Savor Pakistan's finest flavors",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
            // Theme toggle and Flag decoration Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Theme Toggle Button
                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        .testTag("theme_toggle_btn")
                ) {
                    val icon = when (themeMode) {
                        "light" -> Icons.Default.LightMode
                        "dark" -> Icons.Default.DarkMode
                        else -> Icons.Default.BrightnessAuto
                    }
                    val description = when (themeMode) {
                        "light" -> "Light Mode (Tap for Dark)"
                        "dark" -> "Dark Mode (Tap for System)"
                        else -> "System Default Theme (Tap for Light)"
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = description,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Flag decoration
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.tertiary) // Beautiful soft sage
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🇵🇰", fontSize = 20.sp)
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search biryani, tandoori, karahi...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .testTag("search_bar"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f),
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            singleLine = true
        )

        // City Filter Chips
        val cities = listOf("All", "Karachi", "Lahore", "Islamabad")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            cities.forEach { city ->
                val isSelected = selectedCity == city
                PakFilterChip(
                    selected = isSelected,
                    onClick = { onCitySelect(city) },
                    label = city,
                    modifier = Modifier.testTag("city_chip_$city")
                )
            }
        }

        // Local Mock Menu Card Trigger
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable(onClick = onOpenLocalMenu)
                .testTag("local_mock_menu_trigger"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.MenuBook,
                        contentDescription = "Menu Book",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Browse Mock Specials",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Loaded dynamically from a local JSON mock",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Arrow Forward",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Restaurant List
        if (restaurants.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.Info,
                        contentDescription = "Empty",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No restaurants found in $selectedCity",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Try searching for another dish or city!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(restaurants) { restaurant ->
                    RestaurantCard(restaurant = restaurant, onClick = { onRestaurantSelect(restaurant) })
                }
            }
        }
    }
}

@Composable
fun RestaurantCard(restaurant: Restaurant, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .testTag("restaurant_card_${restaurant.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Image(
                    painter = painterResource(id = restaurant.bannerImageId),
                    contentDescription = restaurant.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Linear Gradient Overlay for deep aesthetic contrast
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                                startY = 100f
                            )
                        )
                )
                // City and Rating badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = restaurant.city,
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = restaurant.rating.toString(),
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Name overlay on Banner
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = restaurant.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = restaurant.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.DirectionsRun,
                            contentDescription = "Delivery Time",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = restaurant.deliveryTime,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.DeliveryDining,
                            contentDescription = "Delivery Fee",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Deliv: Rs. ${restaurant.deliveryFee.toInt()}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.ShoppingBag,
                            contentDescription = "Min Order",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Min: Rs. ${restaurant.minimumOrder.toInt()}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RestaurantDetailScreen(
    restaurant: Restaurant,
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    var showBookingDialog by remember { mutableStateOf(false) }
    var selectedMenuItemForCart by remember { mutableStateOf<MenuItem?>(null) }
    var menuSearchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Banner Header Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            Image(
                painter = painterResource(id = restaurant.bannerImageId),
                contentDescription = restaurant.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 0f
                        )
                    )
            )

            // Overlaid Back and Book buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(alpha = 0.5f)),
                    modifier = Modifier.testTag("back_button")
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                IconButton(
                    onClick = { showBookingDialog = true },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .shadow(4.dp, CircleShape)
                        .testTag("book_table_button")
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = "Book Table", tint = Color.White)
                }
            }

            // Bottom Name / Rating Overlay on Banner
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = restaurant.name,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = restaurant.rating.toString(),
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Address",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = restaurant.address,
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Quick Table Booking CTA Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(2.dp, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Planning to dine-in?",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Reserve a premium table with direct WhatsApp confirmation.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
                Button(
                    onClick = { showBookingDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Book Table", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Search Bar for Menu Items
        OutlinedTextField(
            value = menuSearchQuery,
            onValueChange = { menuSearchQuery = it },
            placeholder = { Text("Search dishes in this restaurant...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Menu") },
            trailingIcon = {
                if (menuSearchQuery.isNotEmpty()) {
                    IconButton(onClick = { menuSearchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear Search")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("menu_search_bar"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f),
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            singleLine = true
        )

        // Category Tabs Row
        if (menuSearchQuery.isBlank()) {
            ScrollableTabRow(
                selectedTabIndex = selectedCategoryIndex,
                containerColor = MaterialTheme.colorScheme.background,
                edgePadding = 16.dp,
                divider = {}
            ) {
                restaurant.categories.forEachIndexed { index, category ->
                    Tab(
                        selected = selectedCategoryIndex == index,
                        onClick = { selectedCategoryIndex = index },
                        text = {
                            Text(
                                text = category.name,
                                fontWeight = if (selectedCategoryIndex == index) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Search Results",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Clear filter",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { menuSearchQuery = "" }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Selected Category Menu Items / Filtered Search Results
        if (menuSearchQuery.isBlank()) {
            val selectedCategory = restaurant.categories.getOrNull(selectedCategoryIndex)
            if (selectedCategory != null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(selectedCategory.items) { item ->
                        MenuItemRow(item = item, onAddClick = { selectedMenuItemForCart = item })
                    }
                }
            }
        } else {
            val allMatchedItems = remember(restaurant, menuSearchQuery) {
                restaurant.categories.flatMap { category ->
                    category.items.filter { item ->
                        item.name.contains(menuSearchQuery, ignoreCase = true) ||
                        item.description.contains(menuSearchQuery, ignoreCase = true)
                    }
                }
            }

            if (allMatchedItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "❌ No matching dishes found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try searching with other keywords",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(allMatchedItems) { item ->
                        MenuItemRow(item = item, onAddClick = { selectedMenuItemForCart = item })
                    }
                }
            }
        }
    }

    // Add To Cart Dialog / Sheet
    if (selectedMenuItemForCart != null) {
        AddToCartDialog(
            item = selectedMenuItemForCart!!,
            onDismiss = { selectedMenuItemForCart = null },
            onConfirm = { quantity, customization ->
                viewModel.addToCart(restaurant, selectedMenuItemForCart!!, quantity, customization)
                selectedMenuItemForCart = null
            }
        )
    }

    // Booking Dialog
    if (showBookingDialog) {
        BookTableDialog(
            restaurant = restaurant,
            onDismiss = { showBookingDialog = false },
            onConfirm = { date, time, guests, name, phone, requests ->
                viewModel.submitTableBooking(restaurant, date, time, guests, name, phone, requests)
                showBookingDialog = false
            }
        )
    }
}

@Composable
fun MenuItemRow(item: MenuItem, onAddClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(12.dp))
            .testTag("menu_item_${item.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (item.isPopular) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "POPULAR",
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                    if (item.isSpicy) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("🌶️", fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Rs. ${item.price.toInt()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("add_to_cart_btn_${item.id}")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Add", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun AddToCartDialog(
    item: MenuItem,
    onDismiss: () -> Unit,
    onConfirm: (quantity: Int, customization: String) -> Unit
) {
    var quantity by remember { mutableStateOf(1) }
    var customization by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Add to Cart",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Quantity selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Select Quantity:", fontWeight = FontWeight.Bold)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Minus", tint = MaterialTheme.colorScheme.primary)
                        }
                        Text(
                            text = quantity.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { quantity++ },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Plus", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Customization / Special Instructions
                Text("Special Instructions:", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = customization,
                    onValueChange = { customization = it },
                    placeholder = { Text("e.g., Spicy, Extra Naan, No Onions...") },
                    modifier = Modifier.fillMaxWidth().testTag("cart_customization_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = { onConfirm(quantity, customization) },
                        modifier = Modifier.weight(1f).testTag("confirm_add_to_cart"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Add (Rs. ${(item.price * quantity).toInt()})")
                    }
                }
            }
        }
    }
}

@Composable
fun BookTableDialog(
    restaurant: Restaurant,
    onDismiss: () -> Unit,
    onConfirm: (date: String, time: String, guests: Int, name: String, phone: String, requests: String) -> Unit
) {
    var date by remember { mutableStateOf("Tomorrow") }
    var time by remember { mutableStateOf("08:30 PM") }
    var guests by remember { mutableStateOf(4) }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var requests by remember { mutableStateOf("") }

    var step by remember { mutableStateOf(1) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Book a Table",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = restaurant.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (step == 1) {
                    // Date picker simulation
                    Text("Select Date & Time:", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val dates = listOf("Today", "Tomorrow", "Sun, Jul 5")
                        dates.forEach { d ->
                            val isSel = date == d
                            PakFilterChip(
                                selected = isSel,
                                onClick = { date = d },
                                label = d,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val times = listOf("07:30 PM", "08:30 PM", "09:30 PM")
                        times.forEach { t ->
                            val isSel = time == t
                            PakFilterChip(
                                selected = isSel,
                                onClick = { time = t },
                                label = t,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Guests count selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("No. of Guests:", fontWeight = FontWeight.Bold)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            IconButton(onClick = { if (guests > 1) guests-- }) {
                                Icon(Icons.Default.Remove, contentDescription = "Minus")
                            }
                            Text(text = "$guests", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { guests++ }) {
                                Icon(Icons.Default.Add, contentDescription = "Plus")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { step = 2 },
                        modifier = Modifier.fillMaxWidth().testTag("booking_next_step"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Next Step")
                    }
                } else {
                    Text("Contact Details:", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Your Name") },
                        modifier = Modifier.fillMaxWidth().testTag("booking_name_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("WhatsApp Phone Number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth().testTag("booking_phone_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = requests,
                        onValueChange = { requests = it },
                        label = { Text("Special Request (Optional)") },
                        placeholder = { Text("e.g. Pillar table, high-chair...") },
                        modifier = Modifier.fillMaxWidth().testTag("booking_requests_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { step = 1 },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Back")
                        }
                        Button(
                            onClick = { onConfirm(date, time, guests, name, phone, requests) },
                            enabled = name.isNotBlank() && phone.isNotBlank(),
                            modifier = Modifier.weight(1f).testTag("booking_submit_btn"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Book (WhatsApp)")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartTab(
    cartItems: List<CartItem>,
    viewModel: AppViewModel,
    onBrowseClicked: () -> Unit
) {
    var showCheckoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Your Cart",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
            modifier = Modifier.padding(vertical = 12.dp)
        )

        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.ShoppingCart,
                        contentDescription = "Empty Cart",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your cart is empty",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Add Pakistan's mouth-watering cuisines first!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onBrowseClicked,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("browse_btn")
                    ) {
                        Text("Browse Restaurants", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            val restaurantName = cartItems[0].restaurantName
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Storefront, contentDescription = "Store", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ordering from: $restaurantName",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(cartItems) { item ->
                    CartItemRow(item = item, viewModel = viewModel)
                }
            }

            // Calculation Panel
            val subtotal = cartItems.sumOf { it.itemPrice * it.quantity }
            val restaurant = RestaurantData.sampleRestaurants.find { it.id == cartItems[0].restaurantId }
            val deliveryFee = restaurant?.deliveryFee ?: 150.0
            val gstTax = subtotal * 0.13 // 13% GST
            val total = subtotal + deliveryFee + gstTax

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .shadow(2.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Text("Rs. ${subtotal.toInt()}", fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Delivery Fee", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Text("Rs. ${deliveryFee.toInt()}", fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Sales Tax GST (13%)", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Text("Rs. ${gstTax.toInt()}", fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Amount", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Rs. ${total.toInt()}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showCheckoutDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("checkout_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Checkout")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Confirm & Order via WhatsApp", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }

    if (showCheckoutDialog) {
        CheckoutDialog(
            cartItems = cartItems,
            onDismiss = { showCheckoutDialog = false },
            onConfirm = { name, phone, address, riderNote ->
                viewModel.submitOrder(name, phone, address, riderNote)
                showCheckoutDialog = false
            }
        )
    }
}

@Composable
fun CartItemRow(item: CartItem, viewModel: AppViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.itemName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (item.customization.isNotBlank()) {
                    Text(
                        text = "Note: ${item.customization}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Rs. ${item.itemPrice.toInt()} each",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.updateCartItemQuantity(item, item.quantity - 1) }) {
                    Icon(Icons.Default.Remove, contentDescription = "Minus")
                }
                Text(
                    text = item.quantity.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                IconButton(onClick = { viewModel.updateCartItemQuantity(item, item.quantity + 1) }) {
                    Icon(Icons.Default.Add, contentDescription = "Plus")
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { viewModel.removeFromCart(item) },
                    colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Composable
fun CheckoutDialog(
    cartItems: List<CartItem>,
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String, address: String, riderNote: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var riderNote by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Text(
                    text = "Delivery Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Your Full Name") },
                    modifier = Modifier.fillMaxWidth().testTag("checkout_name"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Contact Phone") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth().testTag("checkout_phone"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Delivery Address") },
                    placeholder = { Text("e.g. Apartment A-4, Street 10, DHA...") },
                    modifier = Modifier.fillMaxWidth().testTag("checkout_address"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = riderNote,
                    onValueChange = { riderNote = it },
                    label = { Text("Rider Instruction (Optional)") },
                    placeholder = { Text("e.g. Ring bell, bring Rs. 1000 change...") },
                    modifier = Modifier.fillMaxWidth().testTag("checkout_note"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // WhatsApp Pre-filled Message Live Preview
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Message,
                        contentDescription = "Message Preview",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "WhatsApp Pre-filled Message",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                val subtotal = cartItems.sumOf { it.itemPrice * it.quantity }
                val restaurant = RestaurantData.sampleRestaurants.find { it.id == cartItems.firstOrNull()?.restaurantId }
                val deliveryFee = restaurant?.deliveryFee ?: 150.0
                val gstTax = subtotal * 0.13
                val total = subtotal + deliveryFee + gstTax
                val restaurantName = cartItems.firstOrNull()?.restaurantName ?: ""
                val restaurantPhone = cartItems.firstOrNull()?.restaurantPhone ?: ""

                val itemLines = cartItems.joinToString("\n") { item ->
                    val custText = if (item.customization.isNotBlank()) " (${item.customization})" else ""
                    "• ${item.quantity}x ${item.itemName} - Rs. ${(item.itemPrice * item.quantity).toInt()}$custText"
                }

                val previewMessage = """
                    *🇵🇰 PAK FOOD DELIVERY - ORDER CONFIRMATION*
                    ----------------------------------------
                    *Restaurant:* $restaurantName
                    *WhatsApp Order Line:* $restaurantPhone
                    
                    *Customer Details:*
                    • *Name:* ${name.ifBlank { "[Your Name]" }}
                    • *Phone:* ${phone.ifBlank { "[Your Phone]" }}
                    • *Delivery Address:* ${address.ifBlank { "[Your Address]" }}
                    ${if (riderNote.isNotBlank()) "• *Rider Note:* $riderNote\n" else ""}
                    *Order Items:*
                    $itemLines
                    
                    *Price Breakdown:*
                    • *Subtotal:* Rs. ${subtotal.toInt()}
                    • *Delivery Fee:* Rs. ${deliveryFee.toInt()}
                    • *GST (13%):* Rs. ${gstTax.toInt()}
                    ----------------------------------------
                    *TOTAL AMOUNT:* Rs. ${total.toInt()}
                """.trimIndent()

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Recipient Line: $restaurantName ($restaurantPhone)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Text(
                            text = previewMessage,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                fontSize = 10.sp,
                                lineHeight = 14.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = { onConfirm(name, phone, address, riderNote) },
                        enabled = name.isNotBlank() && phone.isNotBlank() && address.isNotBlank(),
                        modifier = Modifier.weight(1f).testTag("checkout_submit_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Confirm Order via WhatsApp", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp), textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

@Composable
fun BookingsTab(
    bookings: List<TableBooking>,
    onCancelBooking: (TableBooking) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Your Bookings",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
            modifier = Modifier.padding(vertical = 12.dp)
        )

        if (bookings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.CalendarToday,
                        contentDescription = "No Booking",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No table bookings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Plan a dine-out with friends and family!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(bookings) { booking ->
                    BookingCard(booking = booking, onCancel = { onCancelBooking(booking) })
                }
            }
        }
    }
}

@Composable
fun BookingCard(booking: TableBooking, onCancel: () -> Unit) {
    var showCancelDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = booking.restaurantName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = booking.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column {
                    Text("DATE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Text(booking.bookingDate, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("TIME", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Text(booking.bookingTime, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("GUESTS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Text("${booking.guestsCount} Pax", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
            Spacer(modifier = Modifier.height(12.dp))

            Text("NAME", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            Text(booking.contactName, fontWeight = FontWeight.SemiBold)

            if (booking.specialRequests.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text("SPECIAL REQUEST", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                Text(booking.specialRequests, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { showCancelDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Cancel Reservation")
            }
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Reservation?") },
            text = { Text("Are you sure you want to cancel your table booking at ${booking.restaurantName}?") },
            confirmButton = {
                TextButton(onClick = {
                    onCancel()
                    showCancelDialog = false
                }) {
                    Text("Yes, Cancel", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Dismiss")
                }
            }
        )
    }
}

@Composable
fun OrdersTab(
    orders: List<OrderHistory>,
    activeOrder: ActiveOrderState?,
    onActiveOrderClick: () -> Unit,
    onBrowseClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Your Orders",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
            modifier = Modifier.padding(vertical = 12.dp)
        )

        if (activeOrder != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .clickable(onClick = onActiveOrderClick)
                    .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .testTag("orders_tab_active_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.DirectionsBike,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Active Delivery in Progress",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Order from ${activeOrder.restaurantName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Track",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        if (orders.isEmpty()) {
            if (activeOrder == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.History,
                            contentDescription = "No History",
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No past orders",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Order delicious food and have it delivered now!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = onBrowseClicked,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Order Food Now")
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(orders) { order ->
                    OrderCard(order = order)
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: OrderHistory) {
    val formatter = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    val dateString = remember(order.timestamp) { formatter.format(Date(order.timestamp)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = order.restaurantName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(text = dateString, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = order.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("ITEMS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            Text(order.itemsSummary, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Text("DELIVERED TO", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            Text(order.deliveryAddress, style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Order ID: #${order.orderId}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                Text(
                    text = "Rs. ${order.totalAmount.toInt()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun LocalMenuViewerScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var mockItems by remember { mutableStateOf<List<MockMenuItem>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedItemForCart by remember { mutableStateOf<MockMenuItem?>(null) }

    // Fetch the restaurant items from the local JSON mock
    LaunchedEffect(Unit) {
        mockItems = LocalMenuLoader.loadMenuFromAsset(context)
    }

    // Filter items based on category and search query
    val categories = remember(mockItems) {
        listOf("All") + mockItems.map { it.category }.distinct()
    }

    val filteredItems = remember(mockItems, selectedCategory, searchQuery) {
        mockItems.filter { item ->
            val matchesCategory = selectedCategory == "All" || item.category == selectedCategory
            val matchesSearch = item.name.contains(searchQuery, ignoreCase = true) ||
                    item.description.contains(searchQuery, ignoreCase = true) ||
                    item.restaurantName.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.testTag("local_menu_back_button")
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Local Mock Menu",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Loaded dynamically from JSON asset",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search local mock items...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("local_menu_search_bar"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            singleLine = true
        )

        // Categories list
        if (categories.size > 1) {
            ScrollableTabRow(
                selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
                containerColor = MaterialTheme.colorScheme.background,
                edgePadding = 16.dp,
                divider = {}
            ) {
                categories.forEach { category ->
                    Tab(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        text = {
                            Text(
                                text = category,
                                fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Menu items list
        if (filteredItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Restaurant,
                        contentDescription = "No items",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No mock items found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredItems) { item ->
                    MockMenuItemRow(
                        item = item,
                        onAddClick = { selectedItemForCart = item }
                    )
                }
            }
        }
    }

    // Custom AddToCartDialog for our local mock item
    if (selectedItemForCart != null) {
        AddToCartDialog(
            item = selectedItemForCart!!.toMenuItem(),
            onDismiss = { selectedItemForCart = null },
            onConfirm = { quantity, customization ->
                // Map this item back to the actual restaurant
                val restaurant = RestaurantData.sampleRestaurants.find { it.id == selectedItemForCart!!.restaurantId }
                if (restaurant != null) {
                    viewModel.addToCart(restaurant, selectedItemForCart!!.toMenuItem(), quantity, customization)
                    Toast.makeText(context, "Added ${selectedItemForCart!!.name} to Cart!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Error: Restaurant not found", Toast.LENGTH_SHORT).show()
                }
                selectedItemForCart = null
            }
        )
    }
}

@Composable
fun MockMenuItemRow(item: MockMenuItem, onAddClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(12.dp))
            .testTag("mock_menu_item_${item.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (item.isPopular) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "POPULAR",
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                    if (item.isSpicy) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("🌶️", fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                // Tag showing which restaurant serves this
                Surface(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "Served by: ${item.restaurantName}",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Rs. ${item.price.toInt()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("add_mock_item_btn_${item.id}")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Add", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveOrderTrackerDialog(
    activeOrder: ActiveOrderState,
    onDismiss: () -> Unit,
    onFastForward: (Int) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    var ticks by remember { mutableStateOf(0) }

    // Run a ticker to force recomposition every second so the timer updates!
    LaunchedEffect(activeOrder) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            ticks++
        }
    }

    // Calculations
    val elapsedMillis = System.currentTimeMillis() - activeOrder.startTimeMillis + activeOrder.timeOffsetMillis
    val totalSeconds = activeOrder.totalTimeMinutes * 60
    val elapsedSeconds = elapsedMillis / 1000
    val remainingSeconds = (totalSeconds - elapsedSeconds).coerceAtLeast(0)
    val minutesLeft = remainingSeconds / 60
    val secondsLeft = remainingSeconds % 60

    // Stages calculations
    val elapsedMins = elapsedSeconds / 60
    val isPrepCompleted = elapsedMins >= activeOrder.prepTimeMinutes
    val isArrived = remainingSeconds == 0L

    val currentStage = when {
        activeOrder.isCancelled -> "Cancelled"
        isArrived -> "Arrived"
        isPrepCompleted -> "Out for Delivery"
        elapsedMins < 1 -> "Order Confirmed"
        else -> "Kitchen Preparing"
    }

    val stageProgress = when (currentStage) {
        "Order Confirmed" -> 0.15f
        "Kitchen Preparing" -> 0.15f + (elapsedMins.toFloat() / activeOrder.prepTimeMinutes) * 0.45f
        "Out for Delivery" -> 0.60f + ((elapsedMins - activeOrder.prepTimeMinutes).toFloat() / (activeOrder.totalTimeMinutes - activeOrder.prepTimeMinutes)) * 0.35f
        "Arrived" -> 1.0f
        else -> 0.0f
    }.coerceIn(0f, 1f)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .shadow(24.dp, RoundedCornerShape(24.dp))
                .testTag("live_order_tracker_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with custom green theme (earthy organic/Pakistan food)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Live Order Tracking",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Order ID: #${activeOrder.orderId}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_tracker_btn")) {
                        Icon(Icons.Default.Close, contentDescription = "Close Tracker")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pulsing Ticking Circular Timer Component
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(170.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        )
                ) {
                    // Outer track circle
                    CircularProgressIndicator(
                        progress = { 1.0f },
                        modifier = Modifier.size(150.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                        strokeWidth = 8.dp,
                    )
                    // Live progress circle
                    CircularProgressIndicator(
                        progress = { if (activeOrder.isCancelled) 0f else stageProgress },
                        modifier = Modifier.size(150.dp),
                        color = if (activeOrder.isCancelled) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        strokeWidth = 8.dp,
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (activeOrder.isCancelled) {
                            Text(
                                text = "CANCELLED",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else if (isArrived) {
                            Text(
                                "🎁",
                                fontSize = 32.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = "Arrived!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Text(
                                text = String.format("%02d:%02d", minutesLeft, secondsLeft),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Minutes Left",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Beautiful Visual Step-by-Step Progress Bar
                HorizontalStepProgressBar(
                    activeOrder = activeOrder,
                    elapsedMins = elapsedMins,
                    isPrepCompleted = isPrepCompleted,
                    isArrived = isArrived,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Status Banner Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            activeOrder.isCancelled -> MaterialTheme.colorScheme.errorContainer
                            isArrived -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.tertiary
                        }
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        activeOrder.isCancelled -> MaterialTheme.colorScheme.error
                                        isArrived -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when {
                                    activeOrder.isCancelled -> Icons.Default.Cancel
                                    isArrived -> Icons.Default.Home
                                    currentStage == "Out for Delivery" -> Icons.Default.DeliveryDining
                                    currentStage == "Kitchen Preparing" -> Icons.Default.Restaurant
                                    else -> Icons.Default.CheckCircle
                                },
                                contentDescription = "Status Icon",
                                tint = if (isArrived || activeOrder.isCancelled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = when {
                                    activeOrder.isCancelled -> "Order Cancelled"
                                    isArrived -> "Delicious Food Arrived!"
                                    currentStage == "Out for Delivery" -> "Out for Delivery"
                                    currentStage == "Kitchen Preparing" -> "Preparing Fresh Food"
                                    else -> "Order Confirmed"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    activeOrder.isCancelled -> MaterialTheme.colorScheme.onErrorContainer
                                    isArrived -> MaterialTheme.colorScheme.onPrimaryContainer
                                    else -> MaterialTheme.colorScheme.primary
                                }
                            )
                            Text(
                                text = when {
                                    activeOrder.isCancelled -> "This active order tracking is cancelled."
                                    isArrived -> "Your rider is at your doorstep. Shadaab Raheye!"
                                    currentStage == "Out for Delivery" -> "Rider is heading to your address on motorcycle."
                                    currentStage == "Kitchen Preparing" -> "Average preparation speed: Fast (~${activeOrder.prepTimeMinutes} mins prep)."
                                    else -> "Restaurant received your order summary."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Detailed Steps Vertical Tracker (Real-time highlight!)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val steps = listOf(
                        Triple("Order Confirmed", "Restaurant received your order on WhatsApp", Icons.Default.CheckCircle),
                        Triple("Kitchen Preparing", "Food is being freshly cooked (~${activeOrder.prepTimeMinutes} mins speed)", Icons.Default.Restaurant),
                        Triple("Out for Delivery", "Rider dispatched via motorcycle to your address", Icons.Default.DeliveryDining),
                        Triple("Arrived", "Rider arrived at your doorstep", Icons.Default.Home)
                    )

                    steps.forEachIndexed { index, step ->
                        val stepName = step.first
                        val stepDesc = step.second
                        val stepIcon = step.third

                        val isStepCompleted = when (stepName) {
                            "Order Confirmed" -> true
                            "Kitchen Preparing" -> elapsedMins >= 1
                            "Out for Delivery" -> isPrepCompleted
                            "Arrived" -> isArrived
                            else -> false
                        }

                        val isStepActive = when (stepName) {
                            "Order Confirmed" -> elapsedMins < 1
                            "Kitchen Preparing" -> elapsedMins >= 1 && !isPrepCompleted
                            "Out for Delivery" -> isPrepCompleted && !isArrived
                            "Arrived" -> isArrived
                            else -> false
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Circle step icon
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            activeOrder.isCancelled -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                            isStepCompleted -> MaterialTheme.colorScheme.primary
                                            isStepActive -> MaterialTheme.colorScheme.tertiary
                                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    stepIcon,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = when {
                                        isStepCompleted && !activeOrder.isCancelled -> MaterialTheme.colorScheme.onPrimary
                                        isStepActive && !activeOrder.isCancelled -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stepName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isStepActive) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isStepActive && !activeOrder.isCancelled) MaterialTheme.colorScheme.primary else if (isStepCompleted && !activeOrder.isCancelled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                )
                                Text(
                                    text = stepDesc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isStepActive && !activeOrder.isCancelled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Fast Help Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val intent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                                data = android.net.Uri.parse("tel:${activeOrder.restaurantPhone}")
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = "Call", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Call Shop", fontSize = 12.sp)
                    }

                    if (!activeOrder.isCancelled && !isArrived) {
                        Button(
                            onClick = onCancel,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.Cancel, contentDescription = "Cancel", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cancel Order", fontSize = 11.sp)
                        }
                    } else {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Dismiss", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(12.dp))

                // Interactive Demo/Review Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = "⚙️ Demo Simulation Console",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { onFastForward(5) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(32.dp).testTag("sim_ff_5_mins"),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+5 Mins", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onFastForward(activeOrder.prepTimeMinutes) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(32.dp).testTag("sim_skip_to_transit"),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Dispatched", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onFastForward(activeOrder.totalTimeMinutes) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(32.dp).testTag("sim_arrive_now"),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Arrive!", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FloatingActiveOrderCard(
    activeOrder: ActiveOrderState,
    onClick: () -> Unit
) {
    var ticks by remember { mutableStateOf(0) }

    LaunchedEffect(activeOrder) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            ticks++
        }
    }

    val elapsedMillis = System.currentTimeMillis() - activeOrder.startTimeMillis + activeOrder.timeOffsetMillis
    val totalSeconds = activeOrder.totalTimeMinutes * 60
    val elapsedSeconds = elapsedMillis / 1000
    val remainingSeconds = (totalSeconds - elapsedSeconds).coerceAtLeast(0)
    val minutesLeft = remainingSeconds / 60

    val elapsedMins = elapsedSeconds / 60
    val isPrepCompleted = elapsedMins >= activeOrder.prepTimeMinutes
    val isArrived = remainingSeconds == 0L

    val statusText = when {
        activeOrder.isCancelled -> "Cancelled"
        isArrived -> "Arrived!"
        isPrepCompleted -> "Out for Delivery"
        elapsedMins < 1 -> "Confirmed"
        else -> "Preparing (${activeOrder.prepTimeMinutes} mins)"
    }

    val progressValue = when {
        activeOrder.isCancelled -> 0f
        isArrived -> 1f
        isPrepCompleted -> 0.6f + ((elapsedMins - activeOrder.prepTimeMinutes).toFloat() / (activeOrder.totalTimeMinutes - activeOrder.prepTimeMinutes)) * 0.4f
        else -> 0.1f + (elapsedMins.toFloat() / activeOrder.prepTimeMinutes) * 0.5f
    }.coerceIn(0f, 1f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("floating_active_order_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPrepCompleted) Icons.Default.DirectionsBike else Icons.Default.Restaurant,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Tracking Order: ${activeOrder.restaurantName}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = if (activeOrder.isCancelled) "Cancelled" else if (isArrived) "Arrived at doorstep!" else "Status: $statusText",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }

                if (!isArrived && !activeOrder.isCancelled) {
                    Text(
                        text = "${minutesLeft}m left",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else if (isArrived) {
                    Text(
                        text = "Arrived!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progressValue },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = if (activeOrder.isCancelled) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Tap to open details & simulation dashboard",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun HorizontalStepProgressBar(
    activeOrder: ActiveOrderState,
    elapsedMins: Long,
    isPrepCompleted: Boolean,
    isArrived: Boolean,
    modifier: Modifier = Modifier
) {
    // Current step index calculation:
    // 0 = Kitchen Preparing
    // 1 = Out for Delivery
    // 2 = Delivered (Arrived)
    val currentStepIndex = when {
        activeOrder.isCancelled -> -1
        isArrived -> 2
        isPrepCompleted -> 1
        else -> 0
    }

    val steps = listOf(
        Pair("Preparing", Icons.Default.Restaurant),
        Pair("Out for Delivery", Icons.Default.DeliveryDining),
        Pair("Delivered", Icons.Default.Home)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        steps.forEachIndexed { index, step ->
            val stepName = step.first
            val stepIcon = step.second

            val isCompleted = !activeOrder.isCancelled && index < currentStepIndex
            val isActive = !activeOrder.isCancelled && index == currentStepIndex
            val isUpcoming = activeOrder.isCancelled || index > currentStepIndex

            // Step Indicator Circle & Text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                activeOrder.isCancelled -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                                isCompleted -> MaterialTheme.colorScheme.primary
                                isActive -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                            }
                        )
                        .border(
                            width = 2.dp,
                            color = when {
                                activeOrder.isCancelled -> MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                                isCompleted -> MaterialTheme.colorScheme.primary
                                isActive -> MaterialTheme.colorScheme.primary
                                else -> Color.Transparent
                            },
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Default.Check else stepIcon,
                        contentDescription = stepName,
                        tint = when {
                            activeOrder.isCancelled -> MaterialTheme.colorScheme.onErrorContainer
                            isCompleted -> MaterialTheme.colorScheme.onPrimary
                            isActive -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stepName,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.SemiBold,
                    color = when {
                        activeOrder.isCancelled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        isActive -> MaterialTheme.colorScheme.primary
                        isCompleted -> MaterialTheme.colorScheme.onSurface
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    },
                    textAlign = TextAlign.Center
                )
            }

            // Connector Line (Only between steps)
            if (index < steps.lastIndex) {
                val lineColor = when {
                    activeOrder.isCancelled -> MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                    index < currentStepIndex -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                }
                Box(
                    modifier = Modifier
                        .weight(0.6f)
                        .height(4.dp)
                        .offset(y = (-10).dp) // Offset to align horizontally with circle centers
                        .clip(RoundedCornerShape(2.dp))
                        .background(lineColor)
                )
            }
        }
    }
}

