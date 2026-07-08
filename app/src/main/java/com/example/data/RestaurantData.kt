package com.example.data

import com.example.R

data class MenuItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Double, // in PKR
    val isSpicy: Boolean = false,
    val isPopular: Boolean = false,
    val isVegetarian: Boolean = false
)

data class MenuCategory(
    val name: String,
    val items: List<MenuItem>
)

data class Restaurant(
    val id: String,
    val name: String,
    val description: String,
    val city: String,
    val address: String,
    val phone: String, // WhatsApp target number (e.g. +923001234567)
    val rating: Double,
    val deliveryTime: String,
    val deliveryFee: Double, // in PKR
    val minimumOrder: Double, // in PKR
    val bannerImageId: Int, // drawable resource ID
    val categories: List<MenuCategory>
)

object RestaurantData {
    val sampleRestaurants = listOf(
        Restaurant(
            id = "kolachi_karachi",
            name = "Kolachi Restaurant",
            description = "Iconic Seafront Dining. Experience Karachi's finest Peshawari Karahi, barbecue, and traditional Sajji.",
            city = "Karachi",
            address = "Do Darya, Phase 8, DHA, Karachi",
            phone = "+923001234567", // Simulated WhatsApp order line
            rating = 4.8,
            deliveryTime = "40-50 mins",
            deliveryFee = 250.0,
            minimumOrder = 1000.0,
            bannerImageId = R.drawable.img_pakistani_cuisine, // Using our beautifully generated image
            categories = listOf(
                MenuCategory(
                    name = "Mutton & Chicken Karahi",
                    items = listOf(
                        MenuItem(
                            id = "kolachi_m_karahi",
                            name = "Mutton Peshawari Karahi (Half KG)",
                            description = "Fresh mutton cooked in black pepper, tomatoes, and organic animal fat in traditional iron wok.",
                            price = 1850.0,
                            isSpicy = true,
                            isPopular = true
                        ),
                        MenuItem(
                            id = "kolachi_c_karahi",
                            name = "Chicken White Karahi (Full)",
                            description = "Succulent chicken cubes cooked in fresh cream, yogurt, white pepper, and green chilies.",
                            price = 2400.0,
                            isSpicy = false,
                            isPopular = true
                        )
                    )
                ),
                MenuCategory(
                    name = "Signature Charcoal Barbecue",
                    items = listOf(
                        MenuItem(
                            id = "kolachi_seekh_kabab",
                            name = "Beef Reshmi Seekh Kabab (4 Pcs)",
                            description = "Melt-in-mouth ground beef kebabs mixed with cream, herbs, and mild spices, charcoal-grilled.",
                            price = 950.0,
                            isPopular = true
                        ),
                        MenuItem(
                            id = "kolachi_malai_boti",
                            name = "Chicken Malai Boti (10 Pcs)",
                            description = "Boneless chicken cubes marinated in creamy yogurt and mild cardamom spices, charred to perfection.",
                            price = 1150.0,
                            isSpicy = false
                        ),
                        MenuItem(
                            id = "kolachi_sajji",
                            name = "Balochi Mutton Sajji (Half)",
                            description = "Traditional slow-roasted mutton leg rubbed with Balochi spices, served with fragrant rice.",
                            price = 1900.0
                        )
                    )
                ),
                MenuCategory(
                    name = "Traditional Naan & Sides",
                    items = listOf(
                        MenuItem(
                            id = "kolachi_roghni",
                            name = "Roghni Naan",
                            description = "Fluffy leavened tandoori bread sprinkled with sesame seeds and brushed with melted butter.",
                            price = 120.0
                        ),
                        MenuItem(
                            id = "kolachi_garlic_naan",
                            name = "Garlic Butter Naan",
                            description = "Tandoor-baked naan flavored with minced garlic, fresh cilantro, and ghee.",
                            price = 150.0
                        ),
                        MenuItem(
                            id = "kolachi_mint_chutney",
                            name = "Fresh Mint Raita",
                            description = "Cooling yogurt mixed with freshly ground mint and green coriander chutney.",
                            price = 100.0
                        )
                    )
                )
            )
        ),
        Restaurant(
            id = "savour_pulao",
            name = "Savour Foods",
            description = "The Pride of Islamabad. Legendary Chicken Pulao served with two crispy Shami Kababs and fresh salad.",
            city = "Islamabad",
            address = "Blue Area, Islamabad",
            phone = "+923119876543",
            rating = 4.7,
            deliveryTime = "25-35 mins",
            deliveryFee = 150.0,
            minimumOrder = 500.0,
            bannerImageId = R.drawable.img_pakistani_cuisine,
            categories = listOf(
                MenuCategory(
                    name = "Legendary Pulao Plates",
                    items = listOf(
                        MenuItem(
                            id = "savour_pulao_single",
                            name = "Savour Pulao Single",
                            description = "One plate aromatic basmati rice cooked in chicken yakhni broth, served with 1 piece of chicken and 2 shami kababs.",
                            price = 550.0,
                            isPopular = true
                        ),
                        MenuItem(
                            id = "savour_pulao_double",
                            name = "Savour Pulao Double",
                            description = "A large plate of pulao rice served with 2 chicken pieces, 2 shami kababs, fresh salad, and mint raita.",
                            price = 850.0,
                            isPopular = true
                        )
                    )
                ),
                MenuCategory(
                    name = "Sides & Desserts",
                    items = listOf(
                        MenuItem(
                            id = "savour_shami",
                            name = "Extra Shami Kabab",
                            description = "Traditional lentil and minced beef patty spiced with whole garam masala.",
                            price = 120.0
                        ),
                        MenuItem(
                            id = "savour_kheer",
                            name = "Savour Special Kheer",
                            description = "Rich, slow-cooked rice pudding infused with cardamom and topped with crushed almonds.",
                            price = 220.0,
                            isPopular = true
                        )
                    )
                )
            )
        ),
        Restaurant(
            id = "student_biryani",
            name = "Student Biryani",
            description = "Karachi's original spicy Biryani. Thick, flavorful masala, rich spices, and soft potatoes in basmati rice.",
            city = "Karachi",
            address = "Saddar, Karachi",
            phone = "+923214567890",
            rating = 4.5,
            deliveryTime = "30-40 mins",
            deliveryFee = 180.0,
            minimumOrder = 400.0,
            bannerImageId = R.drawable.img_pakistani_cuisine,
            categories = listOf(
                MenuCategory(
                    name = "Spicy Biryani",
                    items = listOf(
                        MenuItem(
                            id = "student_chicken_b",
                            name = "Student Chicken Biryani (Double)",
                            description = "Classic basmati rice with double chicken piece, spiced yogurt masala, and tender potato.",
                            price = 680.0,
                            isSpicy = true,
                            isPopular = true
                        ),
                        MenuItem(
                            id = "student_beef_b",
                            name = "Student Beef Biryani (Double)",
                            description = "Highly aromatic double-beef portion biryani cooked in local spices with special potato.",
                            price = 780.0,
                            isSpicy = true,
                            isPopular = true
                        )
                    )
                ),
                MenuCategory(
                    name = "Pakistani Curries",
                    items = listOf(
                        MenuItem(
                            id = "student_haleem",
                            name = "Shahi Beef Haleem",
                            description = "Slow-cooked stew of grains, lentils, and beef shredded to a paste, finished with caramelized onions and ginger.",
                            price = 650.0,
                            isSpicy = true
                        ),
                        MenuItem(
                            id = "student_qorma",
                            name = "Chicken Qorma",
                            description = "Rich onion and nut gravy cooked with tender chicken cuts, finished with fragrant kewra water.",
                            price = 700.0
                        )
                    )
                )
            )
        ),
        Restaurant(
            id = "butt_karahi",
            name = "Butt Karahi (Lahore)",
            description = "The Pride of Lakshmi Chowk. Original organic butter chicken and mutton karahi cooked in high flame with pure milk fats.",
            city = "Lahore",
            address = "Lakshmi Chowk, Lahore",
            phone = "+923335556677",
            rating = 4.7,
            deliveryTime = "35-45 mins",
            deliveryFee = 220.0,
            minimumOrder = 800.0,
            bannerImageId = R.drawable.img_pakistani_cuisine,
            categories = listOf(
                MenuCategory(
                    name = "Pure Butter Karahis",
                    items = listOf(
                        MenuItem(
                            id = "butt_m_butter",
                            name = "Mutton Butter Karahi (Half KG)",
                            description = "Mutton cooked in absolute fresh milk butter, ginger julienne, tomatoes, and organic local pepper.",
                            price = 2100.0,
                            isPopular = true
                        ),
                        MenuItem(
                            id = "butt_c_butter",
                            name = "Chicken Butter Karahi (Full KG)",
                            description = "Full chicken cooked exclusively in milk butter and tomatoes with fresh green chilies.",
                            price = 2800.0,
                            isSpicy = true,
                            isPopular = true
                        )
                    )
                ),
                MenuCategory(
                    name = "Barbecue & Clay Tandoor",
                    items = listOf(
                        MenuItem(
                            id = "butt_boti",
                            name = "Mutton Tikka Boti (6 Pcs)",
                            description = "Spiced mutton skewers roasted over a high-fire coal pit, extremely smoky.",
                            price = 1600.0
                        ),
                        MenuItem(
                            id = "butt_khamiri",
                            name = "Khamiri Naan",
                            description = "Thick, soft yeast-raised tandoori bread, perfect for dipping into rich buttery gravy.",
                            price = 80.0
                        )
                    )
                )
            )
        )
    )
}
