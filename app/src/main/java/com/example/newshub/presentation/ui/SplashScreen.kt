package com.example.newshub.presentation.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    // Animations
    var startAnimation by remember { mutableStateOf(false) }
    val scaleAnimation by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing)
    )
    val alphaAnimation by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(800, delayMillis = 200)
    )
    val slideAnimation by animateIntAsState(
        targetValue = if (startAnimation) 0 else 100,
        animationSpec = tween(800, delayMillis = 400)
    )

    // Background animation
    val infiniteTransition = rememberInfiniteTransition()
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2500)
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF008577),
                        Color(0xFF00574B)
                    ),
                    radius = screenWidth.value * 1.2f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Animated background waves
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val center = center
            val radius = size.minDimension / 2f

            repeat(3) { index ->
                val animatedRadius = radius * (1f + (waveOffset + index * 120f) / 360f * 0.3f)
                val alpha = 0.1f - (index * 0.03f)

                drawCircle(
                    color = Color.White.copy(alpha = alpha),
                    radius = animatedRadius,
                    center = center
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App logo with animation
            Card(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scaleAnimation),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // News icon
                    Canvas(
                        modifier = Modifier.size(80.dp)
                    ) {
                        val paint = Paint().asFrameworkPaint().apply {
                            isAntiAlias = true
                            textSize = 48.sp.toPx()
                            color = android.graphics.Color.parseColor("#008577")
                            typeface = android.graphics.Typeface.DEFAULT_BOLD
                        }

                        drawIntoCanvas { canvas ->
                            canvas.nativeCanvas.drawText(
                                "N",
                                size.width / 2f - 12.dp.toPx(),
                                size.height / 2f + 16.dp.toPx(),
                                paint
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App name with slide animation
            Text(
                text = "NewsHub",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                modifier = Modifier
                    .alpha(alphaAnimation)
                    .offset(y = slideAnimation.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tagline with delayed animation
            AnimatedVisibility(
                visible = startAnimation,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 800)) +
                        slideInVertically(
                            animationSpec = tween(600, delayMillis = 800),
                            initialOffsetY = { 50 }
                        )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Stay Updated • Stay Informed",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "🌍 News from around the world",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Loading indicator with animation
            AnimatedVisibility(
                visible = startAnimation,
                enter = fadeIn(animationSpec = tween(400, delayMillis = 1200))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .width(200.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Loading latest stories...",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Floating particles animation
        repeat(10) { index ->
            val particleAnimation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween((2000 + index * 200), easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )

            val startX = (index * 0.1f) * screenWidth.value
            val endX = startX + 50f
            val startY = screenHeight.value
            val endY = -100f

            val currentX = startX + (endX - startX) * particleAnimation
            val currentY = startY + (endY - startY) * particleAnimation

            if (startAnimation) {
                Box(
                    modifier = Modifier
                        .offset(x = currentX.dp, y = currentY.dp)
                        .size(4.dp)
                        .background(
                            Color.White.copy(alpha = 0.6f),
                            RoundedCornerShape(2.dp)
                        )
                )
            }
        }
    }
}
