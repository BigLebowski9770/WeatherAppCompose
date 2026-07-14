package ru.umarsh.weatherappcompose.feature.splash

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinished: (useLocation: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var animationDone by remember { mutableStateOf(false) }

    val logoScale = remember { Animatable(0.6f) }
    val logoAlpha = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "sun")
    val sunRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "sunRotation",
    )

    LaunchedEffect(Unit) {
        logoAlpha.animateTo(1f, animationSpec = tween(600))
        logoScale.animateTo(1f, animationSpec = tween(800, easing = FastOutSlowInEasing))
        delay(SPLASH_HOLD_MS)
        animationDone = true
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        onFinished(granted)
    }

    LaunchedEffect(animationDone) {
        if (!animationDone) return@LaunchedEffect

        when (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        ) {
            PackageManager.PERMISSION_GRANTED -> onFinished(true)
            else -> permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(logoScale.value)
                .alpha(logoAlpha.value),
        ) {
            Icon(
                imageVector = Icons.Filled.WbSunny,
                contentDescription = null,
                modifier = Modifier
                    .size(96.dp)
                    .graphicsLayer { rotationZ = sunRotation },
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "WeatherApp",
                style = MaterialTheme.typography.headlineLarge,
            )
        }
    }
}

private const val SPLASH_HOLD_MS = 800L
