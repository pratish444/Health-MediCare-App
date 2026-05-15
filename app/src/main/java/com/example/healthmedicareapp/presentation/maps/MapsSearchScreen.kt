package com.example.healthmedicareapp.presentation.maps

import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.healthmedicareapp.BuildConfig
import com.example.healthmedicareapp.presentation.components.MediCareTopBar
import com.maptiler.maptilersdk.MTConfig
import com.maptiler.maptilersdk.annotations.MTMarker
import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.MTMapView
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.options.MTCameraOptions
import com.maptiler.maptilersdk.map.options.MTFlyToOptions
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder

// ── Data model ────────────────────────────────────────────────────────────────
data class PlaceResult(
    val name: String,
    val lngLat: LngLat,
)

// ── Geocoding helper ──────────────────────────────────────────────────────────
private suspend fun searchPlaces(
    query: String,
    apiKey: String,
    userLat: Double,
    userLng: Double,
): List<PlaceResult> = withContext(Dispatchers.IO) {
    try {
        val encoded = URLEncoder.encode(query, "UTF-8")
        val url = "https://api.maptiler.com/geocoding/$encoded.json" +
                "?key=$apiKey&proximity=$userLng,$userLat&limit=10"
        val response = URL(url).readText()
        val features = JSONObject(response).getJSONArray("features")
        (0 until features.length()).map { i ->
            val feature   = features.getJSONObject(i)
            val coords    = feature.getJSONObject("geometry").getJSONArray("coordinates")
            val placeName = feature.optString("place_name", "Unknown").split(",").first()
            PlaceResult(
                name   = placeName,
                lngLat = LngLat(coords.getDouble(0), coords.getDouble(1)),
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}

// ── Helper: check if location permission is granted ───────────────────────────
private fun hasLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context, android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
}

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun MapsSearchScreen(
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val scope   = rememberCoroutineScope()

    var searchQuery      by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Hospitals") }
    var isSearching      by remember { mutableStateOf(false) }
    var statusMessage    by remember { mutableStateOf("Locating you…") }
    var userLocation     by remember { mutableStateOf<LngLat?>(null) }
    var permissionGranted by remember { mutableStateOf(hasLocationPermission(context)) }

    val activeMarkers = remember { mutableListOf<MTMarker>() }

    MTConfig.apiKey = BuildConfig.MAPS_API_KEY
    val controller  = remember { MTMapViewController(context) }

    // ── Permission launcher ───────────────────────────────────────────────────
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true
                || permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (!permissionGranted) {
            statusMessage = "Location permission denied. Enable it in Settings."
        }
    }

    // ── Marker helpers ────────────────────────────────────────────────────────
    fun updateMarkers(results: List<PlaceResult>) {
        activeMarkers.forEach { controller.style?.removeMarker(it) }
        activeMarkers.clear()
        results.forEach { place ->
            val marker = MTMarker(
                coordinates = place.lngLat,
                color       = android.graphics.Color.RED,
            )
            controller.style?.addMarker(marker)
            activeMarkers.add(marker)
        }
        statusMessage = if (results.isEmpty()) "No results found nearby"
        else "${results.size} results found nearby"
    }

    fun runSearch(query: String) {
        val loc = userLocation ?: run {
            statusMessage = "Enable GPS to search nearby places"
            return
        }
        scope.launch {
            isSearching   = true
            statusMessage = "Searching for $query…"
            val results   = searchPlaces(query, BuildConfig.MAPS_API_KEY, loc.lat, loc.lng)
            updateMarkers(results)
            isSearching   = false
        }
    }

    // ── Launch: request permission if needed, then get location ───────────────
    LaunchedEffect(Unit) {
        if (!hasLocationPermission(context)) {
            permissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
            return@LaunchedEffect
        }
        loadLocation(context, controller, onLocationReady = { lngLat ->
            userLocation = lngLat
        }, onStatusUpdate = { msg ->
            statusMessage = msg
        }, onResults = { results ->
            updateMarkers(results)
        }, apiKey = BuildConfig.MAPS_API_KEY, scope = scope)
    }

    // ── Re-run location fetch once permission is granted ──────────────────────
    LaunchedEffect(permissionGranted) {
        if (permissionGranted && userLocation == null) {
            loadLocation(context, controller, onLocationReady = { lngLat ->
                userLocation = lngLat
            }, onStatusUpdate = { msg ->
                statusMessage = msg
            }, onResults = { results ->
                updateMarkers(results)
            }, apiKey = BuildConfig.MAPS_API_KEY, scope = scope)
        }
    }

    // ── UI ────────────────────────────────────────────────────────────────────
    Scaffold(
        topBar = { MediCareTopBar(title = "Find Hospital", onBack = onNavigateBack) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            MTMapView(
                referenceStyle = MTMapReferenceStyle.STREETS,
                options        = MTMapOptions(),
                controller     = controller,
                modifier       = Modifier.fillMaxSize(),
                styleVariant   = null,
            )

            // ── Search bar ────────────────────────────────────────────────
            Card(
                modifier  = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .align(Alignment.TopCenter),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(6.dp),
            ) {
                OutlinedTextField(
                    value         = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder   = { Text("Search hospitals, clinics…") },
                    leadingIcon   = {
                        Icon(Icons.Default.Search, null, tint = Color(0xFF1565C0))
                    },
                    trailingIcon  = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                searchQuery = ""
                                runSearch(selectedCategory.lowercase())
                            }) {
                                Icon(Icons.Default.Clear, null, tint = Color.Gray)
                            }
                        }
                    },
                    modifier      = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape         = RoundedCornerShape(12.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = Color(0xFF1565C0),
                        unfocusedBorderColor = Color.Transparent,
                    ),
                    singleLine    = true,
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                        onSearch = {
                            if (searchQuery.isNotBlank()) runSearch(searchQuery)
                        }
                    ),
                )
            }

            // ── My Location FAB ───────────────────────────────────────────
            FloatingActionButton(
                onClick = {
                    userLocation?.let { loc ->
                        controller.flyTo(
                            MTCameraOptions(center = loc),
                            flyToOptions = MTFlyToOptions(
                                curve       = 1.42,
                                minZoom     = 0.0,
                                speed       = 1.2,
                                screenSpeed = 1.2,
                                maxDuration = 0.0
                            )
                        )
                        controller.setZoom(13.0)
                    }
                },
                modifier       = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 180.dp),
                containerColor = Color.White,
                contentColor   = Color(0xFF1565C0),
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "My location")
            }

            // ── Bottom card ───────────────────────────────────────────────
            Card(
                modifier  = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .align(Alignment.BottomCenter),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(6.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier              = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            "Nearby Healthcare",
                            fontWeight = FontWeight.Bold,
                            color      = Color(0xFF1A1A2E),
                        )
                        if (isSearching) {
                            CircularProgressIndicator(
                                modifier    = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color       = Color(0xFF1565C0),
                            )
                        }
                    }

                    Text(statusMessage, fontSize = 12.sp, color = Color.Gray)
                    Spacer(Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            Triple("Hospitals", "🏥", "hospital"),
                            Triple("Pharmacy",  "💊", "pharmacy"),
                            Triple("Clinic",    "🩺", "clinic"),
                        ).forEach { (label, emoji, query) ->
                            FilterChip(
                                selected = selectedCategory == label,
                                onClick  = {
                                    selectedCategory = label
                                    searchQuery      = ""
                                    runSearch(query)
                                },
                                label  = { Text("$emoji $label") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF1565C0),
                                    selectedLabelColor     = Color.White,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Extracted location + search logic (called after permission granted) ────────
@Suppress("MissingPermission")
private fun loadLocation(
    context: Context,
    controller: MTMapViewController,
    onLocationReady: (LngLat) -> Unit,
    onStatusUpdate: (String) -> Unit,
    onResults: (List<PlaceResult>) -> Unit,
    apiKey: String,
    scope: kotlinx.coroutines.CoroutineScope,
) {
    val lm  = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

    if (loc != null) {
        val lngLat = LngLat(loc.longitude, loc.latitude)
        onLocationReady(lngLat)

        scope.launch {
            delay(1500L)
            controller.flyTo(
                MTCameraOptions(center = lngLat),
                flyToOptions = MTFlyToOptions(
                    curve       = 1.42,
                    minZoom     = 0.0,
                    speed       = 1.2,
                    screenSpeed = 1.2,
                    maxDuration = 0.0
                )
            )
            controller.setZoom(13.0)

            onStatusUpdate("Finding hospitals near you…")
            val results = searchPlaces("hospital", apiKey, loc.latitude, loc.longitude)
            onResults(results)
        }
    } else {
        onStatusUpdate("Enable GPS and reopen to see nearby places")
    }
}