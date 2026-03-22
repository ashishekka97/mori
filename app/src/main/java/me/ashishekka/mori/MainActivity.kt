package me.ashishekka.mori

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.ashishekka.mori.app.components.PulseBackdrop
import me.ashishekka.mori.persona.lifecycle.MoriLifecycleManager
import me.ashishekka.mori.persona.state.StateManager
import me.ashishekka.mori.ui.components.PulseButton
import me.ashishekka.mori.ui.gallery.PulseGallery
import me.ashishekka.mori.ui.theme.PulseTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val stateManager: StateManager by inject()
    private val lifecycleManager: MoriLifecycleManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val worldState by stateManager.state.collectAsState()
            var showGallery by remember { mutableStateOf(false) }
            
            DisposableEffect(lifecycleManager) {
                lifecycleManager.onStart()
                onDispose {
                    lifecycleManager.onStop()
                }
            }

            PulseTheme(worldState) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    // PERSISTENT BACKDROP: Uses the unified wallpaper spec
                    PulseBackdrop(worldState = worldState) {
                        AnimatedContent(
                            targetState = showGallery,
                            transitionSpec = {
                                if (targetState) {
                                    (slideInVertically { height -> height } + fadeIn())
                                        .togetherWith(fadeOut(animationSpec = tween(200)))
                                } else {
                                    fadeIn(animationSpec = tween(300))
                                        .togetherWith(slideOutVertically { height -> height } + fadeOut())
                                }
                            },
                            label = "NavigationTransition"
                        ) { isGalleryVisible ->
                            if (isGalleryVisible) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    BackHandler { showGallery = false }
                                    PulseGallery(modifier = Modifier.fillMaxSize())
                                    PulseButton(
                                        onClick = { showGallery = false },
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(24.dp)
                                    ) {
                                        Text("BACK", fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else {
                                LauncherContent(
                                    thermalStress = worldState.energyThermalStress,
                                    onOpenGallery = { showGallery = true }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun LauncherContent(
        thermalStress: Float,
        onOpenGallery: () -> Unit
    ) {
        val context = LocalContext.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            Text(
                text = "MORI",
                style = MaterialTheme.typography.headlineLarge,
                color = PulseTheme.colors.onSurface
            )
            Text(
                text = "LIVING ATMOSPHERE",
                style = MaterialTheme.typography.labelSmall,
                color = PulseTheme.colors.accent
            )

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(bottom = 48.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PulseButton(
                    onClick = {
                        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                            putExtra(
                                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                                ComponentName(context, MoriWallpaperService::class.java)
                            )
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    thermalStress = thermalStress
                ) {
                    Text("SET WALLPAPER", fontWeight = FontWeight.Bold)
                }

                PulseButton(
                    onClick = onOpenGallery,
                    modifier = Modifier.fillMaxWidth(),
                    thermalStress = thermalStress
                ) {
                    Text("DESIGN LAB", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
