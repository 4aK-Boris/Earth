package ru.mpei.earth.ui

import android.graphics.Paint
import android.graphics.PointF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LineChart(
    chartName: String,
    points: List<Pair<Float, Float>>,
    xSteps: List<Float>,
    ySteps: List<Float>
) {

    val configuration = LocalConfiguration.current

    Column(
        modifier = Modifier.width(configuration.screenWidthDp.dp - 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Graph(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp),
            points = points,
            xSteps = xSteps,
            ySteps = ySteps
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = chartName, textAlign = TextAlign.Center)
    }
}

@Composable
fun Graph(
    modifier: Modifier,
    points: List<Pair<Float, Float>>,
    xSteps: List<Float>,
    ySteps: List<Float>
) {
    val density = LocalDensity.current
    val textPaint = remember(density) {
        Paint().apply {
            color = android.graphics.Color.BLACK
            textAlign = Paint.Align.CENTER
            textSize = density.run { 12.sp.toPx() }
        }
    }

    Box(
        modifier = modifier
            .background(Color.White)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        contentAlignment = Center
    ) {

        Canvas(
            modifier = Modifier.fillMaxSize(),
        ) {

            val width = size.width
            val height = size.height

            val xAxisSpace = width / xSteps.size
            val yAxisSpace = height / ySteps.size

            for (i in xSteps.indices) {
                drawContext.canvas.nativeCanvas.drawText(
                    "%.1f".format(xSteps[i]),
                    xAxisSpace * (i + 1),
                    size.height,
                    textPaint
                )
            }

            for (i in ySteps.indices) {
                drawContext.canvas.nativeCanvas.drawText(
                    "%.1f".format(ySteps[i]),
                    0f,
                    size.height - yAxisSpace * (i + 1),
                    textPaint
                )
            }

            drawLine(
                Color.Gray,
                Offset(x = xAxisSpace, y = 0f),
                Offset(x = xAxisSpace, y = height - yAxisSpace),
                strokeWidth = 4f
            )
            drawLine(
                Color.Gray,
                Offset(x = xAxisSpace, y = height - yAxisSpace),
                Offset(x = width, y = height - yAxisSpace),
                strokeWidth = 4f
            )

            val minX = points.filter { it.first < 0 }.minOfOrNull { it.first } ?: 0f
            val minY = points.filter { it.second < 0 }.minOfOrNull { it.second } ?: 0f

            println(minY)

            val maxX = points.maxOf { it.first }
            val maxY = points.maxOf { it.second }

            drawPoints(points = points.map { (x, y) ->
                val pointX = xAxisSpace + (x / maxX) * (width - xAxisSpace)
                val pointY = height - (y / maxY * (height - yAxisSpace)) - yAxisSpace
                Offset(x = pointX, y = pointY)
            }, pointMode = PointMode.Points, color = Color.Blue, strokeWidth = 3f)

        }
    }
}
