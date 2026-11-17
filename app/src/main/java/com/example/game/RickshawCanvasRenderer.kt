package com.example.game

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.example.model.CanopyStyle
import com.example.model.DecorationStyle
import com.example.model.PassengerPersona
import com.example.model.RickshawDesign
import com.example.model.WeatherType
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

object RickshawCanvasRenderer {

    private val billboardTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private val billboardSubPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    fun DrawScope.renderWorld(
        engine: GameEngine,
        design: RickshawDesign,
        canopy: CanopyStyle,
        decoration: DecorationStyle
    ) {
        val w = size.width
        val h = size.height

        val shakeX = if (engine.shakeTimer > 0f) sin(engine.shakeTimer * 70f) * w * 0.012f else 0f
        val shakeY = if (engine.shakeTimer > 0f) cos(engine.shakeTimer * 55f) * h * 0.008f else 0f

        withTransform({
            translate(shakeX, shakeY)
        }) {
            // 1. 2D Dhaka Sidewalks, Asphalt Road, Curbs & Dashed Lanes
            drawDhakaRoad2D(engine, w, h)

            // 2. Roadside 2D Buildings & Krishnachura Trees
            for (b in engine.buildings) {
                drawBuildingOrTree2D(b, engine, w, h)
            }

            // 3. Roadside Dhaka Billboards & Posters
            for (bb in engine.billboards) {
                drawBillboard2D(bb, engine, w, h)
            }

            // 4. Potholes
            for (p in engine.potholes) {
                val px = p.x * w
                val py = p.y * h
                val rw = w * 0.052f
                val rh = h * 0.022f
                drawOval(Color(0xFF181C24), Offset(px - rw, py - rh), Size(rw * 2f, rh * 2f))
                drawOval(Color(0xFF475569), Offset(px - rw * 0.8f, py - rh * 0.7f), Size(rw * 1.6f, rh * 1.4f), style = Stroke(2.5f))
            }

            // 5. Passenger Stop Zones
            for (z in engine.stopZones) {
                drawStopZone2D(z, engine, w, h)
            }

            // 6. Spinning Coins
            for (c in engine.coins) {
                drawCoin2D(c, engine, w, h)
            }

            // 7. 2D Moving Traffic (Buses, Cars, Motorcycles, Pushcarts, Pedestrians)
            for (t in engine.traffic) {
                drawTraffic2D(t, engine, w, h)
            }

            // 8. Player 2D Traditional 3-Wheel Cycle Rickshaw
            val bounceY = if (engine.passengerBounceTimer > 0f) {
                -abs(sin(engine.passengerBounceTimer * 24f)) * h * 0.012f
            } else {
                0f
            }
            val flashAlpha = if (engine.invulnerableTimer > 0f && ((engine.invulnerableTimer * 14).toInt() % 2 == 0)) 0.45f else 1f
            drawPlayerCycleRickshaw2D(
                cx = engine.playerX * w,
                cy = engine.playerY * h + bounceY,
                w = w,
                h = h,
                tiltDeg = engine.playerTiltDeg,
                design = design,
                canopy = canopy,
                decoration = decoration,
                wheelAngle = engine.wheelAngle,
                pedalPhase = engine.pedalPhase,
                hasPassenger = engine.hasPassenger,
                passengerPersona = engine.currentPassengerPersona,
                passengerStyle = engine.currentPassengerStyle,
                isNightOrRain = engine.weather != WeatherType.DAY,
                bellRing = engine.bellRingTimer > 0f,
                alpha = flashAlpha
            )

            // 9. Visible 2D Passenger Boarding / Alighting Animation
            engine.passengerTransition?.let { tr ->
                drawPassengerTransition2D(tr, engine, w, h)
            }

            // 10. Burst Coins on Passenger Delivery
            for (bc in engine.burstCoins) {
                val r = w * 0.016f * bc.life.coerceIn(0.4f, 1f)
                drawCircle(Color(0xFFF59E0B), r, Offset(bc.x * w, bc.y * h))
                drawCircle(Color(0xFFFDE047), r * 0.65f, Offset(bc.x * w, bc.y * h))
            }

            // 11. Weather Overlay (Night Dimming / Raindrops / Lightning)
            drawWeatherEffects(engine, w, h)
        }
    }

    private fun DrawScope.drawDhakaRoad2D(engine: GameEngine, w: Float, h: Float) {
        val isNight = engine.weather == WeatherType.NIGHT
        val isRain = engine.weather == WeatherType.RAIN

        val sidewalkColor = when {
            isNight -> Color(0xFF182032)
            isRain -> Color(0xFF475569)
            else -> Color(0xFFD6C2A5)
        }
        drawRect(sidewalkColor, Offset.Zero, Size(w, h))

        val rl = engine.roadLeft * w
        val rr = engine.roadRight * w
        val rw = rr - rl

        val asphalt = when {
            isNight -> Color(0xFF141A26)
            isRain -> Color(0xFF283244)
            else -> Color(0xFF373D47)
        }
        drawRect(asphalt, Offset(rl, 0f), Size(rw, h))

        // Subtle asphalt lane shading
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color(0x22000000), Color.Transparent, Color.Transparent, Color(0x22000000)),
                startX = rl,
                endX = rr
            ),
            topLeft = Offset(rl, 0f),
            size = Size(rw, h)
        )

        // Scrolling Red & White Curbs + Dashed Lane Lines
        val segLen = h * 0.08f
        val cycleLen = segLen * 2f
        val offsetPx = (engine.roadScrollOffset * h) % cycleLen
        val curbW = w * 0.018f
        val curbRed = if (isNight) Color(0xFF7F1D1D) else Color(0xFFDC2626)
        val curbWhite = if (isNight) Color(0xFF64748B) else Color(0xFFFFF8E1)
        val dashColor = if (isNight) Color(0x9994A3B8) else Color(0xFFFDE047)

        var y = -cycleLen + offsetPx
        var idx = 0
        while (y < h + segLen) {
            val c = if (idx % 2 == 0) curbRed else curbWhite
            drawRect(c, Offset(rl - curbW, y), Size(curbW, segLen + 1f))
            drawRect(c, Offset(rr, y), Size(curbW, segLen + 1f))

            if (idx % 2 == 0) {
                for (div in floatArrayOf(0.325f, 0.50f, 0.675f)) {
                    val dx = div * w
                    drawRoundRect(
                        color = dashColor,
                        topLeft = Offset(dx - 3f, y + segLen * 0.15f),
                        size = Size(6f, segLen * 1.15f),
                        cornerRadius = CornerRadius(3f, 3f)
                    )
                }
            }
            y += segLen
            idx++
        }
    }

    private fun DrawScope.drawBuildingOrTree2D(
        b: RoadsideBuilding,
        engine: GameEngine,
        w: Float,
        h: Float
    ) {
        val isNight = engine.weather == WeatherType.NIGHT
        val by = b.y * h
        val bh = b.heightNorm * h

        if (b.isTree) {
            val cx = if (b.onLeftSide) w * 0.068f else w * 0.932f
            val r = w * 0.046f
            drawCircle(Color(0x33000000), r * 0.95f, Offset(cx + 3f, by + 4f))
            drawCircle(if (isNight) Color(0xFF064E3B) else Color(0xFF15803D), r, Offset(cx, by))
            drawCircle(if (isNight) Color(0xFF14532D) else Color(0xFF22C55E), r * 0.72f, Offset(cx - r * 0.2f, by - r * 0.2f))
            // Krishnachura blossoms
            drawCircle(if (isNight) Color(0xFF7F1D1D) else Color(0xFFFF5722), r * 0.34f, Offset(cx + r * 0.28f, by - r * 0.22f))
            drawCircle(if (isNight) Color(0xFF991B1B) else Color(0xFFFB923C), r * 0.24f, Offset(cx - r * 0.3f, by + r * 0.2f))

            if (isNight) {
                val lampX = if (b.onLeftSide) w * 0.118f else w * 0.882f
                drawCircle(Color(0x66FDE047), w * 0.048f, Offset(lampX, by))
                drawCircle(Color(0xFFFEF08A), w * 0.012f, Offset(lampX, by))
            }
        } else {
            val bw = w * 0.105f
            val bx = if (b.onLeftSide) w * 0.008f else w - w * 0.008f - bw
            val wall = Color(b.wallColor)
            val roof = Color(b.roofColor)
            drawRoundRect(
                color = if (isNight) wall.copy(alpha = 0.45f) else wall,
                topLeft = Offset(bx, by),
                size = Size(bw, bh),
                cornerRadius = CornerRadius(6f, 6f)
            )
            // Rooftop terrace border
            drawRoundRect(
                color = if (isNight) Color(0xFF0F172A) else roof,
                topLeft = Offset(bx + bw * 0.1f, by + bh * 0.1f),
                size = Size(bw * 0.8f, bh * 0.75f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            // Windows
            val winColor = if (isNight) Color(0xFFFDE047) else Color(0xFF1E293B).copy(alpha = 0.4f)
            drawRect(winColor, Offset(bx + bw * 0.22f, by + bh * 0.25f), Size(bw * 0.22f, bh * 0.18f))
            drawRect(winColor, Offset(bx + bw * 0.56f, by + bh * 0.25f), Size(bw * 0.22f, bh * 0.18f))
            drawRect(winColor, Offset(bx + bw * 0.22f, by + bh * 0.55f), Size(bw * 0.22f, bh * 0.18f))
            drawRect(winColor, Offset(bx + bw * 0.56f, by + bh * 0.55f), Size(bw * 0.22f, bh * 0.18f))
        }
    }

    private fun DrawScope.drawBillboard2D(
        bb: RoadsideBillboard,
        engine: GameEngine,
        w: Float,
        h: Float
    ) {
        val isNight = engine.weather == WeatherType.NIGHT
        val bw = w * 0.23f
        val bh = h * 0.062f
        val bx = if (bb.onLeftSide) w * 0.014f else w - w * 0.014f - bw
        val by = bb.y * h

        val bg = Color(bb.bgColor)
        val accent = Color(bb.accentColor)

        if (isNight) {
            drawRoundRect(
                color = accent.copy(alpha = 0.26f),
                topLeft = Offset(bx - 4f, by - 4f),
                size = Size(bw + 8f, bh + 8f),
                cornerRadius = CornerRadius(12f, 12f)
            )
        }

        // Mounting bracket to curb
        val curbAnchorX = if (bb.onLeftSide) w * 0.135f else w * 0.865f
        drawLine(
            color = Color(0xFF475569),
            start = Offset(curbAnchorX, by + bh * 0.5f),
            end = Offset(bx + bw * 0.5f, by + bh * 0.5f),
            strokeWidth = 5f
        )

        drawRoundRect(
            color = Color(0x44000000),
            topLeft = Offset(bx + 3f, by + 4f),
            size = Size(bw, bh),
            cornerRadius = CornerRadius(10f, 10f)
        )
        drawRoundRect(
            color = bg,
            topLeft = Offset(bx, by),
            size = Size(bw, bh),
            cornerRadius = CornerRadius(10f, 10f)
        )
        drawRoundRect(
            color = accent,
            topLeft = Offset(bx, by),
            size = Size(bw, bh),
            cornerRadius = CornerRadius(10f, 10f),
            style = Stroke(width = 3f)
        )

        billboardTitlePaint.color = accent.toArgb()
        billboardTitlePaint.textSize = (w * 0.025f).coerceIn(14f, 24f)
        billboardSubPaint.color = Color.White.toArgb()
        billboardSubPaint.textSize = (w * 0.018f).coerceIn(11f, 18f)

        val textX = bx + bw * 0.5f
        drawContext.canvas.nativeCanvas.drawText(bb.titleBn, textX, by + bh * 0.44f, billboardTitlePaint)
        drawContext.canvas.nativeCanvas.drawText(bb.subtitleBn, textX, by + bh * 0.78f, billboardSubPaint)
    }

    private fun DrawScope.drawStopZone2D(
        z: PassengerStopZone,
        engine: GameEngine,
        w: Float,
        h: Float
    ) {
        val isPickup = z.type == StopZoneType.PICKUP
        val zoneColor = if (isPickup) Color(0xFF10B981) else Color(0xFFF59E0B)
        val pulse = (sin(engine.elapsedSeconds * 7f) * 0.14f + 0.86f)

        val zy = z.y * h
        val padW = w * 0.19f
        val padH = h * 0.105f
        val padX = if (z.onLeftSide) engine.roadLeft * w + w * 0.01f else engine.roadRight * w - padW - w * 0.01f

        drawRoundRect(
            color = zoneColor.copy(alpha = 0.34f * pulse),
            topLeft = Offset(padX, zy - padH * 0.5f),
            size = Size(padW, padH),
            cornerRadius = CornerRadius(14f, 14f)
        )
        drawRoundRect(
            color = zoneColor,
            topLeft = Offset(padX, zy - padH * 0.5f),
            size = Size(padW, padH),
            cornerRadius = CornerRadius(14f, 14f),
            style = Stroke(width = 3.5f)
        )

        labelPaint.color = Color.White.toArgb()
        labelPaint.textSize = (w * 0.027f).coerceIn(16f, 26f)
        drawContext.canvas.nativeCanvas.drawText(
            if (isPickup) "যাত্রী তুলুন" else "এখানে নামান",
            padX + padW * 0.5f,
            zy + 8f,
            labelPaint
        )

        // Waiting 2D Person on curb or Stop Signpost
        val curbX = if (z.onLeftSide) w * 0.078f else w * 0.922f
        if (isPickup) {
            val wave = sin(engine.elapsedSeconds * 11f) * h * 0.010f
            drawPerson2D(
                cx = curbX,
                cy = zy,
                scalePx = w * 0.055f,
                persona = z.persona,
                styleIndex = z.passengerStyleIndex,
                walkPhase = 0f,
                waveArmOffset = wave,
                facingLeft = !z.onLeftSide
            )
            labelPaint.color = Color(0xFFFFF176).toArgb()
            labelPaint.textSize = (w * 0.023f).coerceIn(14f, 22f)
            drawContext.canvas.nativeCanvas.drawText("এই যে!", curbX, zy - w * 0.055f, labelPaint)
        } else {
            drawCircle(Color(0xFFF59E0B), w * 0.032f, Offset(curbX, zy))
            drawCircle(Color.White, w * 0.032f, Offset(curbX, zy), style = Stroke(3f))
            labelPaint.color = Color(0xFF1E1B18).toArgb()
            labelPaint.textSize = (w * 0.022f).coerceIn(14f, 20f)
            drawContext.canvas.nativeCanvas.drawText("স্টপ", curbX, zy + 7f, labelPaint)
        }
    }

    private fun DrawScope.drawPassengerTransition2D(
        tr: PassengerTransitionState,
        engine: GameEngine,
        w: Float,
        h: Float
    ) {
        val t = tr.progress.coerceIn(0f, 1f)
        val seatX = engine.playerX * w
        val seatY = engine.playerY * h + h * 0.01f
        val curbX = tr.curbX * w
        val curbY = tr.anchorY * h

        val (px, py) = if (tr.phase == TransitionPhase.BOARDING) {
            val x = curbX + (seatX - curbX) * t
            val y = curbY + (seatY - curbY) * t - sin(t * PI).toFloat() * h * 0.035f
            x to y
        } else {
            val x = seatX + (curbX - seatX) * t
            val y = seatY + (curbY - seatY) * t - sin(t * PI).toFloat() * h * 0.035f
            x to y
        }

        drawCircle(
            color = if (tr.phase == TransitionPhase.BOARDING) Color(0x6610B981) else Color(0x66FBBF24),
            radius = w * 0.052f,
            center = Offset(px, py)
        )

        drawPerson2D(
            cx = px,
            cy = py,
            scalePx = w * 0.058f,
            persona = tr.persona,
            styleIndex = tr.styleIndex,
            walkPhase = t * 18f,
            waveArmOffset = if (tr.phase == TransitionPhase.ALIGHTING) sin(t * 14f) * h * 0.01f else 0f,
            facingLeft = if (tr.phase == TransitionPhase.BOARDING) !tr.onLeftSide else tr.onLeftSide
        )

        labelPaint.color = Color.White.toArgb()
        labelPaint.textSize = (w * 0.026f).coerceIn(16f, 24f)
        drawContext.canvas.nativeCanvas.drawText(
            if (tr.phase == TransitionPhase.BOARDING) "উঠছে..." else "নামছে! 👋",
            px,
            py - w * 0.065f,
            labelPaint
        )
    }

    private fun DrawScope.drawPerson2D(
        cx: Float,
        cy: Float,
        scalePx: Float,
        persona: PassengerPersona,
        styleIndex: Int,
        walkPhase: Float,
        waveArmOffset: Float,
        facingLeft: Boolean
    ) {
        val hScale = if (persona == PassengerPersona.CHILD) 0.78f else 1.0f
        val s = scalePx * hScale
        val outfit = when (persona) {
            PassengerPersona.MALE -> listOf(Color(0xFF0284C7), Color(0xFF16A34A), Color(0xFF4F46E5), Color(0xFFD97706))[styleIndex % 4]
            PassengerPersona.FEMALE -> listOf(Color(0xFFDB2777), Color(0xFF9333EA), Color(0xFFDC2626), Color(0xFF0D9488))[styleIndex % 4]
            PassengerPersona.CHILD -> listOf(Color(0xFFF59E0B), Color(0xFF0EA5E9), Color(0xFF22C55E), Color(0xFFEC4899))[styleIndex % 4]
        }

        drawOval(Color(0x44000000), Offset(cx - s * 0.45f, cy + s * 0.35f), Size(s * 0.9f, s * 0.32f))

        // Walking legs
        val legSwing = sin(walkPhase) * s * 0.25f
        drawLine(Color(0xFF1E293B), Offset(cx - s * 0.15f, cy + s * 0.1f), Offset(cx - s * 0.15f + legSwing, cy + s * 0.45f), strokeWidth = s * 0.16f, cap = StrokeCap.Round)
        drawLine(Color(0xFF1E293B), Offset(cx + s * 0.15f, cy + s * 0.1f), Offset(cx + s * 0.15f - legSwing, cy + s * 0.45f), strokeWidth = s * 0.16f, cap = StrokeCap.Round)

        // Torso
        drawRoundRect(
            color = outfit,
            topLeft = Offset(cx - s * 0.34f, cy - s * 0.28f),
            size = Size(s * 0.68f, s * 0.48f),
            cornerRadius = CornerRadius(s * 0.22f, s * 0.22f)
        )

        if (persona == PassengerPersona.FEMALE) {
            drawLine(
                color = Color(0xFFFDE047),
                start = Offset(cx - s * 0.28f, cy - s * 0.22f),
                end = Offset(cx + s * 0.26f, cy + s * 0.16f),
                strokeWidth = s * 0.12f,
                cap = StrokeCap.Round
            )
        } else if (persona == PassengerPersona.CHILD) {
            drawRoundRect(
                color = Color(0xFFEF4444),
                topLeft = Offset(cx - s * 0.42f, cy - s * 0.18f),
                size = Size(s * 0.18f, s * 0.30f),
                cornerRadius = CornerRadius(4f, 4f)
            )
        }

        // Waving arm
        val armDir = if (facingLeft) -1f else 1f
        drawLine(
            color = Color(0xFFD49A6A),
            start = Offset(cx + armDir * s * 0.26f, cy - s * 0.16f),
            end = Offset(cx + armDir * s * 0.56f, cy - s * 0.44f + waveArmOffset),
            strokeWidth = s * 0.13f,
            cap = StrokeCap.Round
        )

        // Head & Hair/Orna
        drawCircle(Color(0xFFD49A6A), s * 0.25f, Offset(cx, cy - s * 0.45f))
        drawArc(
            color = if (persona == PassengerPersona.FEMALE) outfit else Color(0xFF1E293B),
            startAngle = 175f,
            sweepAngle = 190f,
            useCenter = true,
            topLeft = Offset(cx - s * 0.26f, cy - s * 0.72f),
            size = Size(s * 0.52f, s * 0.36f)
        )
    }

    private fun DrawScope.drawCoin2D(c: CoinEntity, engine: GameEngine, w: Float, h: Float) {
        val spin = (cos(engine.elapsedSeconds * 6f + c.id) * 0.42f + 0.58f).coerceIn(0.32f, 1f)
        val cx = c.x * w
        val cy = c.y * h + sin(engine.elapsedSeconds * 5f + c.id) * 4f
        val r = w * 0.026f

        drawOval(Color(0x33000000), Offset(cx - r * 0.7f, cy + r * 0.85f), Size(r * 1.4f, r * 0.45f))
        drawOval(Color(0xFFD97706), Offset(cx - r * spin, cy - r), Size(r * 2f * spin, r * 2f))
        drawOval(Color(0xFFFDE047), Offset(cx - r * 0.72f * spin, cy - r * 0.72f), Size(r * 1.44f * spin, r * 1.44f))
    }

    private fun DrawScope.drawTraffic2D(t: TrafficEntity, engine: GameEngine, w: Float, h: Float) {
        val isNight = engine.weather != WeatherType.DAY
        val cx = t.x * w
        val cy = t.y * h
        val halfW = t.type.halfWidthNorm * w
        val halfH = t.type.halfHeightNorm * h

        when (t.type) {
            TrafficType.BUS -> {
                val colors = listOf(Color(0xFF0284C7), Color(0xFFDC2626), Color(0xFF059669), Color(0xFFD97706), Color(0xFF7C3AED))
                val c = colors[t.colorIndex % colors.size]
                drawRoundRect(Color(0x44000000), Offset(cx - halfW + 5f, cy - halfH + 7f), Size(halfW * 2f, halfH * 2f), CornerRadius(16f, 16f))
                drawRoundRect(c, Offset(cx - halfW, cy - halfH), Size(halfW * 2f, halfH * 2f), CornerRadius(14f, 14f))
                // Roof & Stripe
                drawRoundRect(Color(0xFFF1F5F9), Offset(cx - halfW * 0.78f, cy - halfH * 0.72f), Size(halfW * 1.56f, halfH * 1.28f), CornerRadius(10f, 10f))
                drawRoundRect(Color(0xFF0F172A), Offset(cx - halfW * 0.80f, cy + halfH * 0.62f), Size(halfW * 1.60f, halfH * 0.24f), CornerRadius(6f, 6f))
                // Taillights & Headlight glow
                drawCircle(Color(0xFFEF4444), 5f, Offset(cx - halfW * 0.68f, cy + halfH * 0.90f))
                drawCircle(Color(0xFFEF4444), 5f, Offset(cx + halfW * 0.68f, cy + halfH * 0.90f))
                if (isNight) {
                    drawOval(Color(0x44FDE047), Offset(cx - halfW * 0.9f, cy - halfH * 1.45f), Size(halfW * 1.8f, halfH * 0.55f))
                }
            }
            TrafficType.CAR -> {
                val colors = listOf(Color(0xFFE11D48), Color(0xFF2563EB), Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFF64748B))
                val c = colors[t.colorIndex % colors.size]
                drawRoundRect(Color(0x44000000), Offset(cx - halfW + 4f, cy - halfH + 6f), Size(halfW * 2f, halfH * 2f), CornerRadius(16f, 16f))
                drawRoundRect(c, Offset(cx - halfW, cy - halfH), Size(halfW * 2f, halfH * 2f), CornerRadius(16f, 16f))
                drawRoundRect(Color(0xFF0F172A), Offset(cx - halfW * 0.72f, cy - halfH * 0.35f), Size(halfW * 1.44f, halfH * 0.92f), CornerRadius(10f, 10f))
                drawRoundRect(c.copy(alpha = 0.95f), Offset(cx - halfW * 0.62f, cy - halfH * 0.16f), Size(halfW * 1.24f, halfH * 0.52f), CornerRadius(8f, 8f))
                drawCircle(Color(0xFFEF4444), 4f, Offset(cx - halfW * 0.68f, cy + halfH * 0.88f))
                drawCircle(Color(0xFFEF4444), 4f, Offset(cx + halfW * 0.68f, cy + halfH * 0.88f))
            }
            TrafficType.MOTORCYCLE -> {
                drawRoundRect(Color(0x44000000), Offset(cx - halfW + 3f, cy - halfH + 4f), Size(halfW * 2f, halfH * 2f), CornerRadius(12f, 12f))
                drawRoundRect(Color(0xFF111827), Offset(cx - halfW * 0.38f, cy - halfH), Size(halfW * 0.76f, halfH * 2f), CornerRadius(8f, 8f))
                drawRoundRect(Color(0xFFEF4444), Offset(cx - halfW * 0.7f, cy - halfH * 0.45f), Size(halfW * 1.4f, halfH * 0.9f), CornerRadius(10f, 10f))
                drawLine(Color(0xFFCBD5E1), Offset(cx - halfW * 1.1f, cy - halfH * 0.4f), Offset(cx + halfW * 1.1f, cy - halfH * 0.4f), strokeWidth = 5f, cap = StrokeCap.Round)
                drawCircle(Color(0xFF1E293B), halfW * 0.58f, Offset(cx, cy + halfH * 0.1f))
            }
            TrafficType.PUSHCART -> {
                drawRoundRect(Color(0x44000000), Offset(cx - halfW + 4f, cy - halfH + 5f), Size(halfW * 2f, halfH * 2f), CornerRadius(8f, 8f))
                drawRoundRect(Color(0xFF78350F), Offset(cx - halfW, cy - halfH), Size(halfW * 2f, halfH * 1.6f), CornerRadius(8f, 8f))
                drawRoundRect(Color(0xFF92400E), Offset(cx - halfW * 0.85f, cy - halfH * 0.85f), Size(halfW * 1.7f, halfH * 1.3f), CornerRadius(6f, 6f))
                drawCircle(Color(0xFF16A34A), halfW * 0.34f, Offset(cx - halfW * 0.35f, cy - halfH * 0.2f))
                drawCircle(Color(0xFFCA8A04), halfW * 0.36f, Offset(cx + halfW * 0.3f, cy - halfH * 0.1f))
                drawCircle(Color(0xFFEF4444), halfW * 0.28f, Offset(cx, cy - halfH * 0.5f))
                drawCircle(Color(0xFF1E293B), halfW * 0.25f, Offset(cx, cy + halfH * 0.78f))
            }
            TrafficType.PEDESTRIAN -> {
                drawPerson2D(
                    cx = cx,
                    cy = cy,
                    scalePx = w * 0.044f,
                    persona = if (t.colorIndex % 2 == 0) PassengerPersona.MALE else PassengerPersona.FEMALE,
                    styleIndex = t.colorIndex,
                    walkPhase = t.wobblePhase,
                    waveArmOffset = if (t.honkedReactionTimer > 0f) -h * 0.008f else 0f,
                    facingLeft = cx > w * 0.5f
                )
            }
        }
    }

    /**
     * Renders the traditional 3-wheel pedal-powered Dhaka Cycle Rickshaw in rich 2D cartoon top-down/2.5D view!
     */
    private fun DrawScope.drawPlayerCycleRickshaw2D(
        cx: Float,
        cy: Float,
        w: Float,
        h: Float,
        tiltDeg: Float,
        design: RickshawDesign,
        canopy: CanopyStyle,
        decoration: DecorationStyle,
        wheelAngle: Float,
        pedalPhase: Float,
        hasPassenger: Boolean,
        passengerPersona: PassengerPersona,
        passengerStyle: Int,
        isNightOrRain: Boolean,
        bellRing: Boolean,
        alpha: Float
    ) {
        val rw = w * 0.095f
        val rh = h * 0.112f

        withTransform({
            rotate(tiltDeg, pivot = Offset(cx, cy))
        }) {
            // Soft Ground Shadow
            drawOval(
                color = Color(0x44000000),
                topLeft = Offset(cx - rw * 0.85f, cy - rh * 0.86f),
                size = Size(rw * 1.7f, rh * 1.8f)
            )

            // Night / Rain Headlight Beam ahead of front bicycle wheel
            if (isNightOrRain) {
                val beam = Path().apply {
                    moveTo(cx, cy - rh * 0.96f)
                    lineTo(cx - rw * 1.7f, cy - rh * 3.0f)
                    lineTo(cx + rw * 1.7f, cy - rh * 3.0f)
                    close()
                }
                drawPath(
                    beam,
                    Brush.verticalGradient(
                        colors = listOf(Color(0x00FFF59D), Color(0x55FFF59D)),
                        startY = cy - rh * 3.0f,
                        endY = cy - rh * 0.96f
                    )
                )
            }

            // 1. FRONT BICYCLE WHEEL + MUDGUARD + HANDLEBARS + HEADLIGHT + BELL
            val fWheelTop = cy - rh * 1.05f
            val fWheelBot = cy - rh * 0.62f
            drawRoundRect(
                color = Color(0xFF111827).copy(alpha = alpha),
                topLeft = Offset(cx - rw * 0.10f, fWheelTop),
                size = Size(rw * 0.20f, fWheelBot - fWheelTop),
                cornerRadius = CornerRadius(10f, 10f)
            )
            // Front wheel spoke glint
            val fSpokeY = fWheelTop + ((sin(wheelAngle * 1.3f) * 0.5f + 0.5f) * (fWheelBot - fWheelTop) * 0.7f) + 4f
            drawLine(Color(0xFFE4E4E7).copy(alpha = alpha), Offset(cx - rw * 0.08f, fSpokeY), Offset(cx + rw * 0.08f, fSpokeY), strokeWidth = 2.5f)

            // Bicycle Frame tube connecting front fork to rear cabin
            drawLine(
                color = design.bodySecondary.copy(alpha = alpha),
                start = Offset(cx, cy - rh * 0.72f),
                end = Offset(cx, cy - rh * 0.05f),
                strokeWidth = 6f,
                cap = StrokeCap.Round
            )

            // Handlebars
            val barY = cy - rh * 0.66f
            drawLine(
                color = Color(0xFFE4E4E7).copy(alpha = alpha),
                start = Offset(cx - rw * 0.42f, barY + 4f),
                end = Offset(cx + rw * 0.42f, barY + 4f),
                strokeWidth = 5.5f,
                cap = StrokeCap.Round
            )
            // Handle grips
            drawLine(design.bodyPrimary.copy(alpha = alpha), Offset(cx - rw * 0.44f, barY + 5f), Offset(cx - rw * 0.30f, barY + 4f), strokeWidth = 8f, cap = StrokeCap.Round)
            drawLine(design.bodyPrimary.copy(alpha = alpha), Offset(cx + rw * 0.30f, barY + 4f), Offset(cx + rw * 0.44f, barY + 5f), strokeWidth = 8f, cap = StrokeCap.Round)

            // Front Headlight
            drawCircle(Color(0xFFFFF176).copy(alpha = alpha), rw * 0.11f, Offset(cx, cy - rh * 0.78f))
            // Brass Cycle Bell on right handlebar
            val bellPos = Offset(cx + rw * 0.28f, barY)
            drawCircle(if (bellRing) Color(0xFFFFEA00) else Color(0xFFFBBF24), rw * (if (bellRing) 0.13f else 0.09f), bellPos)
            if (bellRing) {
                drawCircle(Color(0xAAFFEB3B), rw * 0.24f, bellPos, style = Stroke(3f))
            }

            if (decoration.hasMarigoldGarland) {
                for (g in -3..3) {
                    val gx = cx + g * rw * 0.11f
                    val gy = barY + 8f + (1f - abs(g) / 4f) * 6f
                    drawCircle(if (g % 2 == 0) Color(0xFFFF9800) else Color(0xFFFFEB3B), rw * 0.065f, Offset(gx, gy))
                }
            }

            // 2. ANIMATED PEDALING DRIVER (Lungi, Shirt, Gamcha, Moving Pedals & Legs)
            val lPedalY = cy - rh * 0.38f + sin(pedalPhase) * rh * 0.12f
            val rPedalY = cy - rh * 0.38f - sin(pedalPhase) * rh * 0.12f
            // Pedals & crank
            drawLine(Color(0xFF94A3B8).copy(alpha = alpha), Offset(cx - rw * 0.24f, lPedalY), Offset(cx - rw * 0.12f, lPedalY), strokeWidth = 5f, cap = StrokeCap.Round)
            drawLine(Color(0xFF94A3B8).copy(alpha = alpha), Offset(cx + rw * 0.12f, rPedalY), Offset(cx + rw * 0.24f, rPedalY), strokeWidth = 5f, cap = StrokeCap.Round)
            // Legs in lungi
            drawLine(design.lungiColor.copy(alpha = alpha), Offset(cx - rw * 0.12f, cy - rh * 0.22f), Offset(cx - rw * 0.18f, lPedalY), strokeWidth = rw * 0.18f, cap = StrokeCap.Round)
            drawLine(design.lungiColor.copy(alpha = alpha), Offset(cx + rw * 0.12f, cy - rh * 0.22f), Offset(cx + rw * 0.18f, rPedalY), strokeWidth = rw * 0.18f, cap = StrokeCap.Round)
            // Driver arms reaching handlebars
            val shoulderY = cy - rh * 0.42f
            drawLine(Color(0xFFC68652).copy(alpha = alpha), Offset(cx - rw * 0.26f, shoulderY), Offset(cx - rw * 0.38f, barY + 4f), strokeWidth = rw * 0.11f, cap = StrokeCap.Round)
            drawLine(Color(0xFFC68652).copy(alpha = alpha), Offset(cx + rw * 0.26f, shoulderY), Offset(cx + rw * 0.38f, barY + 4f), strokeWidth = rw * 0.11f, cap = StrokeCap.Round)
            // Driver Torso
            drawRoundRect(
                color = design.shirtColor.copy(alpha = alpha),
                topLeft = Offset(cx - rw * 0.30f, shoulderY - rh * 0.06f),
                size = Size(rw * 0.60f, rh * 0.28f),
                cornerRadius = CornerRadius(12f, 12f)
            )
            // Driver Head + Gamcha headband
            val headY = shoulderY + rh * 0.04f
            drawCircle(Color(0xFF292524).copy(alpha = alpha), rw * 0.21f, Offset(cx, headY))
            drawLine(Color(0xFFEF4444).copy(alpha = alpha), Offset(cx - rw * 0.20f, headY), Offset(cx + rw * 0.20f, headY), strokeWidth = 6f)

            // 3. TWO REAR WHEELS + GOLD MUDGUARDS
            val rWheelTop = cy + rh * 0.14f
            val rWheelH = rh * 0.56f
            for (side in floatArrayOf(-1f, 1f)) {
                val wx = cx + side * rw * 0.78f
                drawRoundRect(
                    color = Color(0xFF18181B).copy(alpha = alpha),
                    topLeft = Offset(wx - rw * 0.11f, rWheelTop),
                    size = Size(rw * 0.22f, rWheelH),
                    cornerRadius = CornerRadius(8f, 8f)
                )
                val spY = rWheelTop + ((sin(wheelAngle) * 0.5f + 0.5f) * rWheelH * 0.72f) + 6f
                drawLine(Color(0xFFE4E4E7).copy(alpha = alpha), Offset(wx - rw * 0.09f, spY), Offset(wx + rw * 0.09f, spY), strokeWidth = 3f)
                drawRoundRect(
                    color = design.trimGold.copy(alpha = alpha),
                    topLeft = Offset(wx - rw * 0.14f, rWheelTop - 3f),
                    size = Size(rw * 0.28f, rWheelH * 0.35f),
                    cornerRadius = CornerRadius(6f, 6f)
                )
            }

            // 4. REAR PASSENGER CABIN & SEAT
            val cabinLeft = cx - rw * 0.66f
            val cabinTop = cy - rh * 0.02f
            val cabinW = rw * 1.32f
            val cabinH = rh * 0.76f
            drawRoundRect(
                color = design.bodySecondary.copy(alpha = alpha),
                topLeft = Offset(cabinLeft, cabinTop),
                size = Size(cabinW, cabinH),
                cornerRadius = CornerRadius(14f, 14f)
            )
            drawRoundRect(
                color = design.seatColor.copy(alpha = alpha),
                topLeft = Offset(cabinLeft + rw * 0.12f, cabinTop + rh * 0.08f),
                size = Size(cabinW - rw * 0.24f, cabinH * 0.52f),
                cornerRadius = CornerRadius(10f, 10f)
            )

            // Visible Passenger seated inside
            if (hasPassenger) {
                val pScale = if (passengerPersona == PassengerPersona.CHILD) 0.78f else 1.0f
                val pOutfit = when (passengerPersona) {
                    PassengerPersona.MALE -> listOf(Color(0xFF0284C7), Color(0xFF16A34A), Color(0xFF4F46E5), Color(0xFFD97706))[passengerStyle % 4]
                    PassengerPersona.FEMALE -> listOf(Color(0xFFDB2777), Color(0xFF9333EA), Color(0xFFDC2626), Color(0xFF0D9488))[passengerStyle % 4]
                    PassengerPersona.CHILD -> listOf(Color(0xFFF59E0B), Color(0xFF0EA5E9), Color(0xFF22C55E), Color(0xFFEC4899))[passengerStyle % 4]
                }
                val pY = cabinTop + rh * 0.26f
                drawRoundRect(
                    color = pOutfit.copy(alpha = alpha),
                    topLeft = Offset(cx - rw * 0.34f * pScale, pY - rh * 0.08f * pScale),
                    size = Size(rw * 0.68f * pScale, rh * 0.26f * pScale),
                    cornerRadius = CornerRadius(10f, 10f)
                )
                if (passengerPersona == PassengerPersona.FEMALE) {
                    drawLine(
                        color = Color(0xFFFDE047).copy(alpha = alpha),
                        start = Offset(cx - rw * 0.26f, pY - rh * 0.04f),
                        end = Offset(cx + rw * 0.26f, pY + rh * 0.14f),
                        strokeWidth = 5f,
                        cap = StrokeCap.Round
                    )
                }
                drawCircle(Color(0xFFD49A6A).copy(alpha = alpha), rw * 0.19f * pScale, Offset(cx, pY))
                drawCircle(
                    color = if (passengerPersona == PassengerPersona.FEMALE) pOutfit else Color(0xFF1E293B),
                    radius = rw * 0.16f * pScale,
                    center = Offset(cx, pY + 2f)
                )
            }

            // 5. DECORATIVE CANOPY & HAND-PAINTED FOLK-ART BACK PANEL
            val canopyTop = cabinTop + rh * 0.16f
            val canopyH = rh * 0.42f
            drawRoundRect(
                color = design.canopyColor.copy(alpha = 0.94f * alpha),
                topLeft = Offset(cabinLeft - rw * 0.04f, canopyTop),
                size = Size(cabinW + rw * 0.08f, canopyH),
                cornerRadius = CornerRadius(14f, 14f)
            )
            if (canopy.hasFloralArch) {
                drawRoundRect(
                    color = Color(0xFFFDE047).copy(alpha = alpha),
                    topLeft = Offset(cabinLeft, canopyTop + 4f),
                    size = Size(cabinW, canopyH - 8f),
                    cornerRadius = CornerRadius(12f, 12f),
                    style = Stroke(width = 3f)
                )
            } else {
                drawRoundRect(
                    color = design.trimGold.copy(alpha = alpha),
                    topLeft = Offset(cabinLeft - rw * 0.04f, canopyTop),
                    size = Size(cabinW + rw * 0.08f, canopyH),
                    cornerRadius = CornerRadius(14f, 14f),
                    style = Stroke(width = 3f)
                )
            }

            // Folk-Art Back Panel (Rickshaw Art)
            val backTop = canopyTop + canopyH
            val backH = rh * 0.28f
            drawRoundRect(
                color = design.bodyPrimary.copy(alpha = alpha),
                topLeft = Offset(cabinLeft, backTop),
                size = Size(cabinW, backH),
                cornerRadius = CornerRadius(8f, 8f)
            )
            drawRoundRect(
                color = design.trimGold.copy(alpha = alpha),
                topLeft = Offset(cabinLeft, backTop),
                size = Size(cabinW, backH),
                cornerRadius = CornerRadius(8f, 8f),
                style = Stroke(width = 3f)
            )
            // Center lotus motif
            val lotusC = Offset(cx, backTop + backH * 0.5f)
            drawCircle(Color(0xFFFFEB3B).copy(alpha = alpha), backH * 0.34f, lotusC)
            drawCircle(design.bodySecondary.copy(alpha = alpha), backH * 0.20f, lotusC)

            if (canopy.hasRainShield) {
                drawRoundRect(
                    color = Color(0x55BAE6FD),
                    topLeft = Offset(cabinLeft, cabinTop + 2f),
                    size = Size(cabinW, rh * 0.16f),
                    cornerRadius = CornerRadius(6f, 6f)
                )
            }

            if (canopy.hasTassels) {
                val sway = sin(wheelAngle * 1.4f) * rw * 0.06f
                for (i in -3..3) {
                    val tx = cx + i * rw * 0.20f
                    drawLine(
                        color = if (i % 2 == 0) design.trimGold else Color(0xFFEC4899),
                        start = Offset(tx, backTop + backH),
                        end = Offset(tx + sway, backTop + backH + rh * 0.10f),
                        strokeWidth = 3.5f,
                        cap = StrokeCap.Round
                    )
                }
            }

            if (decoration.hasFestivalLights) {
                val lights = listOf(Color(0xFF00E5FF), Color(0xFFFFEA00), Color(0xFFFF4081), Color(0xFF69F0AE))
                for (i in 0..4) {
                    val lx = cabinLeft + cabinW * (0.12f + i * 0.19f)
                    val pulse = ((wheelAngle.toInt() + i) % 2 == 0)
                    val lc = lights[i % lights.size]
                    drawCircle(if (pulse) lc else lc.copy(alpha = 0.4f), rw * 0.06f, Offset(lx, canopyTop + 4f))
                }
            }
        }
    }

    /**
     * Classic 2D Side-View Showcase of the Traditional 3-Wheel Cycle Rickshaw for the GarageScreen.
     */
    fun DrawScope.drawShowcaseCycleRickshaw(
        design: RickshawDesign,
        canopy: CanopyStyle,
        decoration: DecorationStyle,
        animPhase: Float
    ) {
        val w = size.width
        val h = size.height

        // 2D Asphalt ground strip
        val groundY = h * 0.80f
        drawRect(Color(0xFF475569), Offset(0f, groundY), Size(w, h - groundY))
        drawLine(Color(0xFFFDE047), Offset(0f, groundY + 4f), Offset(w, groundY + 4f), strokeWidth = 3f)

        val scale = minOf(w * 0.82f, h * 1.35f)
        val cx = w * 0.50f
        val wheelR = scale * 0.145f
        val rearX = cx - scale * 0.18f
        val frontX = cx + scale * 0.26f
        val rearY = groundY - wheelR
        val frontY = groundY - wheelR * 0.94f

        fun drawSpokedWheel(wx: Float, wy: Float, r: Float) {
            drawCircle(Color(0xFF1E293B), r, Offset(wx, wy), style = Stroke(width = 8f))
            drawCircle(design.trimGold, r * 0.82f, Offset(wx, wy), style = Stroke(width = 2.5f))
            for (i in 0 until 8) {
                val a = animPhase + i * (PI.toFloat() / 4f)
                drawLine(
                    color = Color(0xFFCBD5E1),
                    start = Offset(wx, wy),
                    end = Offset(wx + cos(a) * r * 0.80f, wy + sin(a) * r * 0.80f),
                    strokeWidth = 2f
                )
            }
            drawCircle(design.bodyPrimary, r * 0.18f, Offset(wx, wy))
        }

        // Front & Rear wheels (plus peek of second rear wheel for 3-wheel depth)
        drawSpokedWheel(rearX - 10f, rearY - 3f, wheelR * 0.96f)
        drawSpokedWheel(rearX, rearY, wheelR)
        drawSpokedWheel(frontX, frontY, wheelR * 0.94f)

        // Bicycle Frame + Crank + Fork + Handlebars
        val crankX = cx + scale * 0.04f
        val crankY = groundY - wheelR * 0.95f
        val saddleX = cx + scale * 0.01f
        val saddleY = groundY - wheelR * 2.15f
        val stemX = frontX - scale * 0.06f
        val stemY = groundY - wheelR * 2.25f

        drawLine(design.bodySecondary, Offset(rearX, rearY), Offset(crankX, crankY), strokeWidth = 6f, cap = StrokeCap.Round)
        drawLine(design.bodySecondary, Offset(crankX, crankY), Offset(saddleX, saddleY), strokeWidth = 6f, cap = StrokeCap.Round)
        drawLine(design.bodySecondary, Offset(saddleX, saddleY), Offset(stemX, stemY), strokeWidth = 6f, cap = StrokeCap.Round)
        drawLine(design.bodySecondary, Offset(crankX, crankY), Offset(stemX, stemY), strokeWidth = 5f, cap = StrokeCap.Round)
        drawLine(Color(0xFF94A3B8), Offset(stemX, stemY), Offset(frontX, frontY), strokeWidth = 5.5f, cap = StrokeCap.Round)
        // Handlebar & Brass Bell
        val barEndX = stemX - scale * 0.03f
        val barEndY = stemY - wheelR * 0.36f
        drawLine(Color(0xFFCBD5E1), Offset(stemX, stemY), Offset(barEndX, barEndY), strokeWidth = 5f, cap = StrokeCap.Round)
        drawCircle(design.bodyPrimary, 5f, Offset(barEndX, barEndY))
        drawCircle(Color(0xFFFBBF24), 5.5f, Offset(barEndX + 8f, barEndY - 5f))

        // Driver pedaling
        val pedOffX = cos(animPhase) * wheelR * 0.30f
        val pedOffY = sin(animPhase) * wheelR * 0.30f
        drawLine(design.lungiColor, Offset(saddleX, saddleY + 6f), Offset(crankX + pedOffX, crankY + pedOffY), strokeWidth = 9f, cap = StrokeCap.Round)
        drawLine(design.lungiColor, Offset(saddleX + 4f, saddleY + 6f), Offset(crankX - pedOffX, crankY - pedOffY), strokeWidth = 9f, cap = StrokeCap.Round)
        drawLine(design.shirtColor, Offset(saddleX, saddleY), Offset(saddleX + scale * 0.04f, saddleY - wheelR * 0.95f), strokeWidth = 15f, cap = StrokeCap.Round)
        drawLine(Color(0xFFC68652), Offset(saddleX + scale * 0.04f, saddleY - wheelR * 0.85f), Offset(barEndX, barEndY), strokeWidth = 6f, cap = StrokeCap.Round)
        val dHead = Offset(saddleX + scale * 0.05f, saddleY - wheelR * 1.32f)
        drawCircle(Color(0xFFC68652), wheelR * 0.30f, dHead)
        drawLine(Color(0xFFEF4444), Offset(dHead.x - wheelR * 0.28f, dHead.y - 3f), Offset(dHead.x + wheelR * 0.28f, dHead.y - 3f), strokeWidth = 4.5f)

        // Rear Passenger Cabin (Side Folk Art Panel + Seat + Hood/Canopy)
        val cabinLeft = rearX - scale * 0.14f
        val cabinTop = groundY - wheelR * 2.10f
        val cabinW = scale * 0.26f
        val cabinH = wheelR * 1.35f

        drawRoundRect(
            color = design.bodyPrimary,
            topLeft = Offset(cabinLeft, cabinTop),
            size = Size(cabinW, cabinH),
            cornerRadius = CornerRadius(12f, 12f)
        )
        drawRoundRect(
            color = design.trimGold,
            topLeft = Offset(cabinLeft, cabinTop),
            size = Size(cabinW, cabinH),
            cornerRadius = CornerRadius(12f, 12f),
            style = Stroke(width = 3.5f)
        )
        // Seat cushion edge
        drawRoundRect(
            color = design.seatColor,
            topLeft = Offset(cabinLeft + cabinW * 0.15f, cabinTop + cabinH * 0.36f),
            size = Size(cabinW * 0.85f, cabinH * 0.22f),
            cornerRadius = CornerRadius(6f, 6f)
        )
        // Folk art flower circle on side panel
        val artC = Offset(cabinLeft + cabinW * 0.45f, cabinTop + cabinH * 0.72f)
        drawCircle(Color(0xFFFFEB3B), wheelR * 0.22f, artC)
        drawCircle(design.bodySecondary, wheelR * 0.12f, artC)

        // Folded/Arched Canopy Hood
        val hoodPath = Path().apply {
            moveTo(cabinLeft, cabinTop + 4f)
            lineTo(cabinLeft - scale * 0.02f, cabinTop - wheelR * 1.45f)
            lineTo(cabinLeft + cabinW * 1.15f, cabinTop - wheelR * 1.32f)
            lineTo(cabinLeft + cabinW * 0.92f, cabinTop + 4f)
            close()
        }
        drawPath(hoodPath, design.canopyColor)
        drawPath(hoodPath, design.trimGold, style = Stroke(width = 3.5f))

        if (canopy.hasTassels) {
            val sway = sin(animPhase * 1.8f) * 4f
            for (i in 0..4) {
                val tx = cabinLeft + cabinW * (0.15f + i * 0.22f)
                val ty = cabinTop - wheelR * 1.32f
                drawLine(
                    color = if (i % 2 == 0) design.trimGold else Color(0xFFEC4899),
                    start = Offset(tx, ty),
                    end = Offset(tx + sway, ty + wheelR * 0.42f),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )
            }
        }
        if (decoration.hasMarigoldGarland) {
            for (i in -2..2) {
                drawCircle(
                    color = if (i % 2 == 0) Color(0xFFFF9800) else Color(0xFFFFEB3B),
                    radius = 4.5f,
                    center = Offset(barEndX + i * 5f, barEndY + 8f + abs(i) * 2f)
                )
            }
        }
        if (decoration.hasFestivalLights) {
            val lights = listOf(Color(0xFF00E5FF), Color(0xFFFFEA00), Color(0xFFFF4081), Color(0xFF69F0AE))
            for (i in 0..4) {
                val lx = cabinLeft + cabinW * (0.08f + i * 0.24f)
                drawCircle(lights[i % lights.size], 4.5f, Offset(lx, cabinTop - wheelR * 1.36f))
            }
        }
    }

    private fun DrawScope.drawWeatherEffects(engine: GameEngine, w: Float, h: Float) {
        if (engine.weather == WeatherType.NIGHT) {
            drawRect(color = Color(0x2E050A18), size = Size(w, h))
        } else if (engine.weather == WeatherType.RAIN) {
            drawRect(color = Color(0x281E293B), size = Size(w, h))
            for (drop in engine.rainDrops) {
                val x1 = drop.x * w
                val y1 = drop.y * h
                drawLine(
                    color = Color(0x99BAE6FD),
                    start = Offset(x1, y1),
                    end = Offset(x1 - w * 0.016f, y1 + drop.length * h),
                    strokeWidth = 2.4f,
                    cap = StrokeCap.Round
                )
            }
            if (engine.lightningAlpha > 0f) {
                drawRect(color = Color.White.copy(alpha = engine.lightningAlpha), size = Size(w, h))
            }
        }
    }
}
