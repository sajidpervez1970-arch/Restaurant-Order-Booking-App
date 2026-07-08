package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val restaurantId: String,
    val restaurantName: String,
    val restaurantPhone: String, // WhatsApp number to send the order to
    val itemId: String,
    val itemName: String,
    val itemPrice: Double,
    val quantity: Int,
    val customization: String = ""
) : Serializable

@Entity(tableName = "order_history")
data class OrderHistory(
    @PrimaryKey val orderId: String,
    val restaurantName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val itemsSummary: String,
    val totalAmount: Double,
    val deliveryAddress: String,
    val status: String = "Ordered"
) : Serializable

@Entity(tableName = "table_bookings")
data class TableBooking(
    @PrimaryKey val bookingId: String,
    val restaurantId: String,
    val restaurantName: String,
    val restaurantPhone: String, // WhatsApp number to send the booking to
    val bookingDate: String,
    val bookingTime: String,
    val guestsCount: Int,
    val contactName: String,
    val contactPhone: String,
    val specialRequests: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Requested"
) : Serializable
