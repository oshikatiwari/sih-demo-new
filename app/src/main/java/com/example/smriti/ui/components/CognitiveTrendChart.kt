package com.example.smriti.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smriti.model.GameSessionRecord
import com.example.smriti.ui.theme.ForestGreen
import com.example.smriti.ui.theme.MintPastel
import com.example.smriti.ui.theme.PineGreen

@Composable
fun CognitiveTrendChart(
    sessions: List<GameSessionRecord>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
            .testTag("cognitive_trend_chart"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Cognitive Trajectory (CPS)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                    Text(
                        text = "Real-time longitudinal session progression",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MintPastel)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    val avgCps = if (sessions.isNotEmpty()) sessions.map { it.cps }.average().toInt() else 0
                    Text(
                        text = "Avg: $avgCps",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PineGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (sessions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No session records to graph yet", color = Color.Gray, fontSize = 14.sp)
                }
            } else {
                // Interactive Mini Line Chart
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        val width = size.width
                        val height = size.height
                        val paddingBottom = 16f

                        // Draw reference guide gridlines (25, 50, 75, 100)
                        val gridLines = listOf(0.25f, 0.50f, 0.75f, 1.0f)
                        gridLines.forEach { pct ->
                            val y = height - (pct * (height - paddingBottom))
                            drawLine(
                                color = Color(0xFFF1F5F9),
                                start = Offset(0f, y),
                                end = Offset(width, y),
                                strokeWidth = 1.5f
                            )
                        }

                        val n = sessions.size
                        val stepX = if (n > 1) width / (n - 1) else width / 2f

                        val points = sessions.mapIndexed { index, sess ->
                            val x = if (n > 1) index * stepX else width / 2f
                            val normalized = (sess.cps / 100.0).coerceIn(0.0, 1.0).toFloat()
                            val y = height - paddingBottom - (normalized * (height - paddingBottom - 10f))
                            Offset(x, y)
                        }

                        // Path connecting points
                        if (points.size > 1) {
                            val path = Path()
                            path.moveTo(points.first().x, points.first().y)
                            for (i in 1 until points.size) {
                                path.lineTo(points[i].x, points[i].y)
                            }
                            drawPath(
                                path = path,
                                color = PineGreen,
                                style = Stroke(width = 3.5f)
                            )
                        }

                        // Draw point circles
                        points.forEachIndexed { i, pt ->
                            val isAlert = sessions[i].cps < 60.0
                            val dotColor = if (isAlert) Color(0xFFDC2626) else PineGreen

                            // Outer glow
                            drawCircle(
                                color = dotColor.copy(alpha = 0.25f),
                                radius = 7f,
                                center = pt
                            )
                            // Inner solid
                            drawCircle(
                                color = dotColor,
                                radius = 4.5f,
                                center = pt
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Chart Legend / Labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    sessions.forEachIndexed { idx, sess ->
                        Text(
                            text = "#${idx + 1}",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Clinical Domain Performance Breakdown
            Text(
                text = "Clinical Multi-Vector Weighting",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                VectorBar("Accuracy (Precision)", "30%", 0.30f, PineGreen)
                VectorBar("Processing Speed (1.5s - 8s)", "20%", 0.20f, Color(0xFF2E7D32))
                VectorBar("Session Completion (Perseverance)", "20%", 0.20f, Color(0xFF00796B))
                VectorBar("Consistency (Std Dev Stability)", "15%", 0.15f, Color(0xFF4C1D95))
                VectorBar("Working Memory (Recall Accuracy)", "15%", 0.15f, Color(0xFFD97706))
            }
        }
    }
}

@Composable
private fun VectorBar(label: String, weightText: String, weightPct: Float, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontSize = 12.sp, color = Color(0xFF4A5568), modifier = Modifier.weight(1f))
        Text(text = weightText, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
    }
}
