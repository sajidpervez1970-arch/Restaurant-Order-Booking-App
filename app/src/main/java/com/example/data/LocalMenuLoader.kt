package com.example.data

import android.content.Context
import org.json.JSONArray

data class MockMenuItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val isSpicy: Boolean,
    val isPopular: Boolean,
    val isVegetarian: Boolean,
    val category: String,
    val restaurantId: String,
    val restaurantName: String
) {
    fun toMenuItem(): MenuItem {
        return MenuItem(
            id = id,
            name = name,
            description = description,
            price = price,
            isSpicy = isSpicy,
            isPopular = isPopular,
            isVegetarian = isVegetarian
        )
    }
}

object LocalMenuLoader {
    fun loadMenuFromAsset(context: Context, fileName: String = "restaurant_menu.json"): List<MockMenuItem> {
        val items = mutableListOf<MockMenuItem>()
        try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                items.add(
                    MockMenuItem(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        description = obj.getString("description"),
                        price = obj.getDouble("price"),
                        isSpicy = obj.optBoolean("isSpicy", false),
                        isPopular = obj.optBoolean("isPopular", false),
                        isVegetarian = obj.optBoolean("isVegetarian", false),
                        category = obj.getString("category"),
                        restaurantId = obj.getString("restaurantId"),
                        restaurantName = obj.getString("restaurantName")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return items
    }
}
