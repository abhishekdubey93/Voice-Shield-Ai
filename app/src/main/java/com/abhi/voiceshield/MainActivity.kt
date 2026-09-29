package com.abhi.voiceshield

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import android.os.Handler
import android.os.Looper
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import androidx.compose.runtime.DisposableEffect
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                VoiceShieldApp()
            }
        }
    }
}

@Composable
fun VoiceShieldApp() {
    var risk by remember { mutableStateOf(12) }
    var showAlert by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val socket = VoiceShieldWebSocket.connect { newRisk ->
            risk = newRisk
            showAlert = newRisk >= 61
        }

        VoiceShieldWebSocket.sendRisk(socket, 90)

        onDispose {
            socket.close(1000, "VoiceShield screen closed")
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F7FB)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "VoiceShield",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF172033)
                    )

                    Text(
                        text = "AI Call Protection",
                        fontSize = 14.sp,
                        color = Color(0xFF697386)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🛡",
                        fontSize = 23.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Protection status
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF172033)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50))
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Protection Active",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "VoiceShield is ready to protect your calls",
                            color = Color(0xFFB9C1D0),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Riskometer
            Text(
                text = "Current Riskometer",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF172033)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    risk <= 30 -> Color(0xFFE8F5E9)
                                    risk <= 60 -> Color(0xFFFFF3CD)
                                    risk <= 80 -> Color(0xFFFFE0B2)
                                    else -> Color(0xFFFFCDD2)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = risk.toString(),
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    risk <= 30 -> Color(0xFF2E7D32)
                                    risk <= 60 -> Color(0xFFF57C00)
                                    risk <= 80 -> Color(0xFFE65100)
                                    else -> Color(0xFFC62828)
                                }
                            )

                            Text(
                                text = when {
                                    risk <= 30 -> "LOW RISK"
                                    risk <= 60 -> "MEDIUM RISK"
                                    risk <= 80 -> "HIGH RISK"
                                    else -> "CRITICAL"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "No active threats detected",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4B5563)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            if (showAlert) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = if (risk >= 81) {
                                "🚨 CRITICAL RISK"
                            } else {
                                "⚠️ HIGH RISK"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (risk >= 81) {
                                "Potential impersonation attack detected."
                            } else {
                                "Suspicious conversation detected."
                            }
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (risk >= 81) {
                                "Recommended action: BLOCK / ESCALATE"
                            } else {
                                "Recommended action: MFA / CALLBACK"
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            // AI analysis
            Text(
                text = "AI Analysis",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF172033)
            )

            Spacer(modifier = Modifier.height(10.dp))

            AnalysisCard(
                title = "Voice Authenticity",
                value = "94%",
                subtitle = "Likely genuine"
            )

            Spacer(modifier = Modifier.height(8.dp))

            AnalysisCard(
                title = "Conversation Risk",
                value = "08%",
                subtitle = "No suspicious intent"
            )

            Spacer(modifier = Modifier.height(8.dp))

            AnalysisCard(
                title = "Caller Trust",
                value = "86%",
                subtitle = "Trusted caller"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Recent Calls",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF172033)
            )

            Spacer(modifier = Modifier.height(10.dp))

            CallItem(
                name = "Rahul Sharma",
                number = "+91 XXXXX XXXXX",
                risk = "LOW"
            )

            Spacer(modifier = Modifier.height(8.dp))

            CallItem(
                name = "Unknown Caller",
                number = "+91 XXXXX XXXXX",
                risk = "HIGH"
            )
        }
    }
}

@Composable
fun AnalysisCard(
    title: String,
    value: String,
    subtitle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF273142)
                )

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF7B8494)
                )
            }

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
        }
    }
}

@Composable
fun CallItem(
    name: String,
    number: String,
    risk: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE9EEF8)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "☎",
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Text(
                    text = number,
                    fontSize = 12.sp,
                    color = Color(0xFF7B8494)
                )
            }

            Text(
                text = risk,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (risk == "HIGH")
                    Color(0xFFC62828)
                else
                    Color(0xFF2E7D32)
            )
        }
    }
}
suspend fun getRiskFromBackend(): Int? {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL("http://10.0.2.2:8000/risk")

            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "POST"
            connection.setRequestProperty(
                "Content-Type",
                "application/json"
            )
            connection.setRequestProperty(
                "Accept",
                "application/json"
            )

            connection.doOutput = true

            val requestBody = """
                {
                    "call_id": "android_test",
                    "caller_number": "+919999999999",
                    "risk": 85
                }
            """.trimIndent()

            connection.outputStream.use { output ->
                output.write(requestBody.toByteArray())
            }

            if (connection.responseCode == 200) {
                val response = connection.inputStream
                    .bufferedReader()
                    .use { it.readText() }

                val json = JSONObject(response)

                json.getInt("risk")
            } else {
                null
            }

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
class RiskWebSocketListener(
    private val onRiskUpdate: (Int) -> Unit
) : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        println("✅ VoiceShield WebSocket connected")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        try {
            val json = JSONObject(text)
            val risk = json.getInt("risk")

            Handler(Looper.getMainLooper()).post {
                onRiskUpdate(risk)
            }

            println("📊 VoiceShield Risk: $risk")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onFailure(
        webSocket: WebSocket,
        t: Throwable,
        response: Response?
    ) {
        println("❌ WebSocket error: ${t.message}")
    }
}

object VoiceShieldWebSocket {

    private val client = OkHttpClient()

    fun connect(
        onRiskUpdate: (Int) -> Unit
    ): WebSocket {

        val request = Request.Builder()
            .url("ws://10.0.2.2:8000/ws/risk")
            .build()

        return client.newWebSocket(
            request,
            RiskWebSocketListener(onRiskUpdate)
        )
    }

    fun sendRisk(
        webSocket: WebSocket,
        risk: Int
    ) {
        val json = JSONObject().apply {
            put("risk", risk)
        }

        webSocket.send(json.toString())
    }
}

