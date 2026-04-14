package com.example.readyroutine

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// --- 1. THEME DEFINITION (ORANGE DARK MODE) ---
val OrangePrimary = Color(0xFFFF8C00)
val DarkBg = Color(0xFF121212)
val DarkSurface = Color(0xFF1E1E1E)

@Composable
fun ReadyRoutineTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(primary = OrangePrimary, background = DarkBg, surface = DarkSurface)
    } else {
        lightColorScheme(primary = OrangePrimary, background = Color.White, surface = Color(0xFFF8F8F8))
    }
    MaterialTheme(colorScheme = colorScheme, content = content)
}

// --- 2. DATA MODELS ---
data class Product(val id: Int, val name: String, val price: Int, val imageUrl: String, val weight: String = "1 unit")
enum class Screen { Login, Register, Home, Cart, Profile }
enum class ProfileSub { Main, Orders, Address, Notifications, EditProfile }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkMode by remember { mutableStateOf(true) }
            ReadyRoutineTheme(darkTheme = isDarkMode) {
                var currentScreen by remember { mutableStateOf(Screen.Login) }
                var registeredEmail by remember { mutableStateOf("") }
                var registeredPassword by remember { mutableStateOf("") }
                var registeredName by remember { mutableStateOf("User Name") }
                val cartItems = remember { mutableStateListOf<Product>() }

                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    when (currentScreen) {
                        Screen.Login -> LoginScreen(
                            onNavigateToRegister = { currentScreen = Screen.Register },
                            onLoginSuccess = { currentScreen = Screen.Home },
                            validEmail = registeredEmail,
                            validPassword = registeredPassword
                        )
                        Screen.Register -> RegisterScreen(
                            onRegisterSuccess = { name, email, pass ->
                                registeredName = name; registeredEmail = email; registeredPassword = pass
                                currentScreen = Screen.Login
                            },
                            onNavigateToLogin = { currentScreen = Screen.Login }
                        )
                        else -> MainDashboard(
                            currentTab = currentScreen,
                            onTabSelected = { currentScreen = it },
                            cartItems = cartItems,
                            userName = registeredName,
                            userEmail = registeredEmail,
                            isDarkMode = isDarkMode,
                            onToggleDarkMode = { isDarkMode = !isDarkMode },
                            onLogout = { currentScreen = Screen.Login },
                            onUpdateProfile = { registeredName = it }
                        )
                    }
                }
            }
        }
    }
}

// --- 3. AUTH SCREENS ---
@Composable
fun LoginScreen(onNavigateToRegister: () -> Unit, onLoginSuccess: () -> Unit, validEmail: String, validPassword: String) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("ReadyRoutine", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = OrangePrimary)
        Spacer(Modifier.height(32.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = password, onValueChange = { password = it }, label = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                }
            }, modifier = Modifier.fillMaxWidth()
        )
        if (errorMessage.isNotEmpty()) Text(errorMessage, color = Color.Red, modifier = Modifier.padding(8.dp))
        Spacer(Modifier.height(24.dp))
        Button(onClick = {
            if (email == validEmail && password == validPassword) onLoginSuccess() else errorMessage = "Invalid Credentials"
        }, modifier = Modifier.fillMaxWidth().height(50.dp)) { Text("Login") }
        TextButton(onClick = onNavigateToRegister) { Text("Register Now", color = OrangePrimary) }
    }
}

@Composable
fun RegisterScreen(onRegisterSuccess: (String, String, String) -> Unit, onNavigateToLogin: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("Create Account", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = pass, onValueChange = { pass = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation())
        Spacer(Modifier.height(24.dp))
        Button(onClick = { onRegisterSuccess(name, email, pass) }, modifier = Modifier.fillMaxWidth().height(50.dp)) { Text("Register") }
        TextButton(onClick = onNavigateToLogin) { Text("Back to Login", color = OrangePrimary) }
    }
}

// --- 4. DASHBOARD ---
@Composable
fun MainDashboard(
    currentTab: Screen, onTabSelected: (Screen) -> Unit, cartItems: MutableList<Product>,
    userName: String, userEmail: String, isDarkMode: Boolean, onToggleDarkMode: () -> Unit,
    onLogout: () -> Unit, onUpdateProfile: (String) -> Unit
) {
    Scaffold(
        topBar = {
            if (currentTab == Screen.Home) {
                Column { TopSearchBar(); FilterTopBar(isDarkMode, onToggleDarkMode) }
            }
        },
        bottomBar = { BottomNavBar(currentTab, onTabSelected) }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            when (currentTab) {
                Screen.Home -> HomeScreen(cartItems)
                Screen.Cart -> CartScreen(cartItems)
                Screen.Profile -> ProfileScreen(userName, userEmail, onLogout, onUpdateProfile)
                else -> {}
            }
        }
    }
}

@Composable
fun TopSearchBar() {
    Column(Modifier.background(MaterialTheme.colorScheme.surface).padding(horizontal = 16.dp, vertical = 8.dp).statusBarsPadding()) {
        Text("Delivery in 10 mins", fontWeight = FontWeight.Bold, color = OrangePrimary)
        Text("Home - Sector 7, HSR Layout", fontSize = 12.sp, color = Color.Gray)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = "", onValueChange = {}, placeholder = { Text("Search Products") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun FilterTopBar(isDarkMode: Boolean, onToggleDarkMode: () -> Unit) {
    val filters = listOf("All", "Fast Delivery", "Rating 4.0+", "Offers")
    var selectedFilter by remember { mutableStateOf("All") }

    Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onToggleDarkMode) {
            Icon(if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode, null, tint = OrangePrimary)
        }
        LazyRow(contentPadding = PaddingValues(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filters) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangePrimary)
                )
            }
        }
    }
}

@Composable
fun HomeScreen(cartItems: MutableList<Product>) {
    // EXPANDED BESTSELLERS LIST
    val bestSellers = listOf(
        Product(101, "Fresh Milk", 33, "https://www.countrydelight.in/media/products/milk_900_cow_1.png", "500 ml"),
        Product(102, "Bread", 45, "https://images.unsplash.com/photo-1509440159596-0249088772ff?q=80&w=1000", "400g"),
        Product(103, "Organic Bananas", 60, "https://cdn-icons-png.flaticon.com/512/2909/2909808.png", "1 kg"),
        Product(104, "Coca Cola", 40, "https://cdn-icons-png.flaticon.com/512/2722/2722527.png", "750 ml"),
        Product(105, "Potato Chips", 20, "https://cdn-icons-png.flaticon.com/512/2553/2553691.png", "50 g"),
        Product(106, "Eggs Pack", 72, "https://cdn-icons-png.flaticon.com/512/3014/3014502.png", "6 units"),
        Product(107, "Apples", 120, "https://cdn-icons-png.flaticon.com/512/415/415733.png", "1 kg")
    )

    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        item {
            AsyncImage(
                model = "https://img.freepik.com/free-vector/grocery-delivery-service-banner-template_23-2148496417.jpg",
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(24.dp))
            Text("Bestsellers", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text("Popular items near you", fontSize = 12.sp, color = Color.Gray)
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(vertical = 12.dp)) {
                items(bestSellers) { product ->
                    ProductCard(product) { cartItems.add(product) }
                }
            }
        }
        item {
            Spacer(Modifier.height(16.dp))
            Text("Shop More", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        // Additional vertical list of products
        items(bestSellers.reversed()) { product ->
            HorizontalProductItem(product) { cartItems.add(product) }
        }
    }
}

@Composable
fun ProductCard(product: Product, onAdd: () -> Unit) {
    Card(Modifier.width(160.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(8.dp)) {
            AsyncImage(model = product.imageUrl, contentDescription = null, Modifier.height(100.dp).fillMaxWidth(), contentScale = ContentScale.Fit)
            Text(product.name, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(product.weight, fontSize = 11.sp, color = Color.Gray)
            Text("₹${product.price}", color = OrangePrimary, fontWeight = FontWeight.Bold)
            Button(onClick = onAdd, modifier = Modifier.fillMaxWidth().height(35.dp), shape = RoundedCornerShape(4.dp)) {
                Text("ADD", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun HorizontalProductItem(product: Product, onAdd: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(model = product.imageUrl, contentDescription = null, modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)))
        Column(Modifier.weight(1f).padding(start = 12.dp)) {
            Text(product.name, fontWeight = FontWeight.Bold)
            Text(product.weight, fontSize = 12.sp, color = Color.Gray)
            Text("₹${product.price}", color = OrangePrimary, fontWeight = FontWeight.Bold)
        }
        Button(onClick = onAdd, shape = RoundedCornerShape(4.dp)) {
            Text("ADD")
        }
    }
}

// --- 5. CART & CHECKOUT ---
@Composable
fun CartScreen(cartItems: MutableList<Product>) {
    var isCheckingOut by remember { mutableStateOf(false) }
    var address by remember { mutableStateOf("") }
    val paymentOptions = listOf("UPI", "Netbanking", "Cash on Delivery")
    var selectedPayment by remember { mutableStateOf(paymentOptions[0]) }
    val total = cartItems.sumOf { it.price }
    val context = LocalContext.current

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(if (isCheckingOut) "Checkout" else "Your Cart", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        if (cartItems.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Cart Empty") }
        } else if (!isCheckingOut) {
            LazyColumn(Modifier.weight(1f)) {
                items(cartItems) { item ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(item.name); IconButton(onClick = { cartItems.remove(item) }) { Icon(Icons.Default.Delete, null, tint = Color.Red) }
                    }
                }
            }
            Button(onClick = { isCheckingOut = true }, Modifier.fillMaxWidth()) { Text("Pay ₹$total") }
        } else {
            Text("Delivery Address", fontWeight = FontWeight.Bold)
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Full Address") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            Text("Select Payment Method", fontWeight = FontWeight.Bold)
            paymentOptions.forEach { option ->
                Row(Modifier.fillMaxWidth().clickable { selectedPayment = option }.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = selectedPayment == option, onClick = { selectedPayment = option }, colors = RadioButtonDefaults.colors(selectedColor = OrangePrimary))
                    Text(option, Modifier.padding(start = 8.dp))
                }
            }
            Spacer(Modifier.height(24.dp))
            Button(onClick = {
                if (address.isNotBlank()) {
                    Toast.makeText(context, "Ordered via $selectedPayment!", Toast.LENGTH_SHORT).show()
                    cartItems.clear(); isCheckingOut = false
                } else Toast.makeText(context, "Enter Address", Toast.LENGTH_SHORT).show()
            }, Modifier.fillMaxWidth()) { Text("Confirm Order") }
        }
    }
}

// --- 6. PROFILE & FUNCTIONAL SUB-PAGES ---
@Composable
fun ProfileScreen(name: String, email: String, onLogout: () -> Unit, onUpdateProfile: (String) -> Unit) {
    var subScreen by remember { mutableStateOf(ProfileSub.Main) }
    var userAddress by remember { mutableStateOf("HSR Layout, Sector 7, Bangalore") }

    when (subScreen) {
        ProfileSub.Main -> ProfileMainView(name, email, onLogout) { subScreen = it }
        ProfileSub.Orders -> OrderHistoryView { subScreen = ProfileSub.Main }
        ProfileSub.Address -> EditAddressView(userAddress) { userAddress = it; subScreen = ProfileSub.Main }
        ProfileSub.EditProfile -> EditProfileView(name) { onUpdateProfile(it); subScreen = ProfileSub.Main }
        ProfileSub.Notifications -> NotificationView { subScreen = ProfileSub.Main }
    }
}

@Composable
fun ProfileMainView(name: String, email: String, onLogout: () -> Unit, onNavigate: (ProfileSub) -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(80.dp).clip(CircleShape).background(OrangePrimary), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Person, null, Modifier.size(40.dp), tint = Color.Black)
        }
        Text(name, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
        Text(email, color = Color.Gray)
        TextButton(onClick = { onNavigate(ProfileSub.EditProfile) }) { Text("Edit Profile", color = OrangePrimary) }
        Spacer(Modifier.height(24.dp))
        ProfileItem(Icons.Default.History, "Order History") { onNavigate(ProfileSub.Orders) }
        ProfileItem(Icons.Default.LocationOn, "Edit Address") { onNavigate(ProfileSub.Address) }
        ProfileItem(Icons.Default.Notifications, "Notifications") { onNavigate(ProfileSub.Notifications) }
        Spacer(Modifier.weight(1f))
        Button(onClick = onLogout, colors = ButtonDefaults.buttonColors(containerColor = Color.Red), modifier = Modifier.fillMaxWidth()) { Text("Logout") }
    }
}

@Composable
fun EditProfileView(currentName: String, onSave: (String) -> Unit) {
    var name by remember { mutableStateOf(currentName) }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onSave(currentName) }) { Icon(Icons.Default.ArrowBack, null) }
            Text("Edit Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Update Name") }, modifier = Modifier.fillMaxWidth().padding(top = 16.dp))
        Button(onClick = { onSave(name) }, modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) { Text("Save Changes") }
    }
}

@Composable
fun OrderHistoryView(onBack: () -> Unit) {
    val orders = listOf("Order #101 - ₹330 (Delivered)", "Order #102 - ₹150 (Delivered)")
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
            Text("Order History", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        orders.forEach { order -> Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) { Text(order, Modifier.padding(16.dp)) } }
    }
}

@Composable
fun EditAddressView(currentAddress: String, onSave: (String) -> Unit) {
    var address by remember { mutableStateOf(currentAddress) }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onSave(currentAddress) }) { Icon(Icons.Default.ArrowBack, null) }
            Text("Edit Address", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth().padding(top = 16.dp), minLines = 3)
        Button(onClick = { onSave(address) }, modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) { Text("Update Address") }
    }
}

@Composable
fun NotificationView(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
            Text("Notifications", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Text("You have no new notifications", Modifier.padding(top = 16.dp), color = Color.Gray)
    }
}

@Composable
fun ProfileItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = OrangePrimary)
        Text(label, Modifier.padding(start = 16.dp))
        Spacer(Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
    }
}

@Composable
fun BottomNavBar(current: Screen, onSelect: (Screen) -> Unit) {
    NavigationBar {
        NavigationBarItem(selected = current == Screen.Home, onClick = { onSelect(Screen.Home) }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
        NavigationBarItem(selected = current == Screen.Cart, onClick = { onSelect(Screen.Cart) }, icon = { Icon(Icons.Default.ShoppingCart, null) }, label = { Text("Cart") })
        NavigationBarItem(selected = current == Screen.Profile, onClick = { onSelect(Screen.Profile) }, icon = { Icon(Icons.Default.AccountCircle, null) }, label = { Text("Profile") })
    }
}