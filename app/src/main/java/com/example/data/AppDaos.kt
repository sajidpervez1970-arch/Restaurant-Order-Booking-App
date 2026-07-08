package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun getCartItems(): Flow<List<CartItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItem)

    @Update
    suspend fun updateCartItem(item: CartItem)

    @Delete
    suspend fun deleteCartItem(item: CartItem)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    @Query("DELETE FROM cart_items WHERE restaurantId = :restaurantId")
    suspend fun clearCartForRestaurant(restaurantId: String)
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM order_history ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<OrderHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderHistory)
}

@Dao
interface BookingDao {
    @Query("SELECT * FROM table_bookings ORDER BY timestamp DESC")
    fun getAllBookings(): Flow<List<TableBooking>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: TableBooking)

    @Delete
    suspend fun deleteBooking(booking: TableBooking)
}
