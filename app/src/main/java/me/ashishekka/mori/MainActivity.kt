package me.ashishekka.mori

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
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
import me.ashishekka.mori.engine.renderer.DebugPulseRenderer
import me.ashishekka.mori.engine.renderer.LayerManager
import me.ashishekka.mori.engine.renderer.StaticFallbackRenderer
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
        setContent {
            val worldState by stateManager.state.collectAsState()
            var showGallery by remember { mutableStateOf(false) }
            
            DisposableEffect(lifecycleManager) {
                lifecycleManager.onStart()
                onDispose {
                    lifecycleManager.onStop()
                }
            }

            if (showGallery) {
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
                LauncherScreen(
                    worldState = worldState,
                    onOpenGallery = { showGallery = true }
                )
            }
        }
    }

    @Composable
    private fun LauncherScreen(
        worldState: me.ashishekka.mori.persona.state.WorldState,
        onOpenGallery: () -> Unit
    ) {
        val context = LocalContext.current

        PulseTheme(worldState) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Black
            ) {
                PulseBackdrop(
                    worldState = worldState,
                    layerManager = remember { 
                        LayerManager().apply { 
                            addEffect(StaticFallbackRenderer(0xFF1A1A1A.toInt()))
                            addEffect(DebugPulseRenderer()) 
                        } 
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(64.dp)) // Safe area top

                        // TITLE (Anchored Top)
                        Text(
                            text = "MORI",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 8.sp,
                            color = PulseTheme.colors.onSurface
                        )
                        Text(
                            text = "LIVING ATMOSPHERE",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 2.sp,
                            color = PulseTheme.colors.accent
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // ACTIONS (Anchored Bottom)
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
                                thermalStress = worldState.energyThermalStress
                            ) {
                                Text("SET WALLPAPER", fontWeight = FontWeight.Bold)
                            }

                            PulseButton(
                                onClick = onOpenGallery,
                                modifier = Modifier.fillMaxWidth(),
                                thermalStress = worldState.energyThermalStress
                            ) {
                                Text("DESIGN LAB", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
