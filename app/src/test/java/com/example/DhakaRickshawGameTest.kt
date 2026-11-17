package com.example

import com.example.data.BengaliDialogues
import com.example.data.DialogueSelector
import com.example.data.MissionCatalog
import com.example.game.GameEngine
import com.example.game.GameSoundEvent
import com.example.game.InputControls
import com.example.game.PassengerStopZone
import com.example.game.StopZoneType
import com.example.game.TrafficEntity
import com.example.game.TrafficType
import com.example.game.TransitionPhase
import com.example.model.DialogueCategory
import com.example.model.GameMode
import com.example.model.GameSettings
import com.example.model.PassengerPersona
import com.example.model.PlayerProgress
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DhakaRickshawGameTest {

    @Test
    fun dialoguesHaveAtLeast150UniqueLinesAcrossAll12Categories() {
        val lines = BengaliDialogues.allLines
        assertTrue("Expected at least 150 dialogues, got ${lines.size}", lines.size >= 150)

        val uniqueTexts = lines.map { it.textBn }.toSet()
        assertEquals("All dialogue lines must be unique", lines.size, uniqueTexts.size)

        val coveredCategories = lines.map { it.category }.toSet()
        assertEquals(DialogueCategory.entries.toSet(), coveredCategories)
    }

    @Test
    fun dialogueSelectorAvoidsImmediateRepeats() {
        val selector = DialogueSelector()
        val pickedIds = (1..12).map {
            selector.pick(DialogueCategory.RANDOM_CHAT, hasPassenger = true).id
        }
        assertEquals("Picked 12 random chat lines without repeating", 12, pickedIds.toSet().size)
    }

    @Test
    fun gameEngineHandles2DSteeringRelaxedSpeedBrakingAndCollisions() {
        val engine = GameEngine(
            mode = GameMode.ENDLESS,
            mission = null,
            challenge = null,
            progress = PlayerProgress(),
            settings = GameSettings()
        )
        assertTrue("Initial billboards should be populated", engine.billboards.isNotEmpty())
        assertTrue("Initial 2D buildings should be populated", engine.buildings.isNotEmpty())
        val startX = engine.playerX
        val startSpeed = engine.speedKmh
        assertTrue("Initial rickshaw speed should be relaxed", startSpeed <= 12f)

        // Pedal & steer right in 2D space
        repeat(20) {
            engine.step(0.03f, InputControls(steerRight = true, pedal = true))
        }
        assertTrue("Rickshaw should move right in 2D", engine.playerX > startX)
        assertTrue("Speed should increase while pedaling", engine.speedKmh > startSpeed)
        assertTrue("Top speed should remain relaxed and controllable", engine.speedKmh <= 25f)
        assertTrue("Distance should advance", engine.distanceMetersFloat > 0f)

        // Brake
        val peakSpeed = engine.speedKmh
        repeat(15) {
            engine.step(0.03f, InputControls(brake = true))
        }
        assertTrue("Speed should decrease while braking", engine.speedKmh < peakSpeed)

        // Distant ambient traffic horn check
        engine.traffic += TrafficEntity(
            id = 998,
            type = TrafficType.BUS,
            x = 0.76f,
            y = 0.16f,
            laneIndex = 3,
            speedKmh = 5f,
            colorIndex = 1
        )
        val hornEvents = engine.step(0.016f, InputControls())
        assertTrue("Approaching bus should emit DistantHorn event", hornEvents.any { it is GameSoundEvent.DistantHorn && it.isBus })

        // 2D Collision check
        val initialHearts = engine.hearts
        engine.traffic += TrafficEntity(
            id = 999,
            type = TrafficType.BUS,
            x = engine.playerX,
            y = engine.playerY,
            laneIndex = 1,
            speedKmh = 5f,
            colorIndex = 0
        )
        val events = engine.step(0.016f, InputControls())
        assertEquals("Crash should reduce 1 heart", initialHearts - 1, engine.hearts)
        assertTrue("Crash event should fire", events.any { it is GameSoundEvent.Crash })
    }

    @Test
    fun passengerBoardingAndAlightingTransitionsPlayVisiblyIn2D() {
        val engine = GameEngine(
            mode = GameMode.ENDLESS,
            mission = null,
            challenge = null,
            progress = PlayerProgress(),
            settings = GameSettings()
        )
        engine.hasPassenger = false
        engine.playerX = 0.24f
        engine.stopZones.clear()
        engine.stopZones += PassengerStopZone(
            id = 500,
            type = StopZoneType.PICKUP,
            onLeftSide = true,
            y = engine.playerY,
            passengerStyleIndex = 1,
            persona = PassengerPersona.FEMALE
        )

        engine.step(0.016f, InputControls())
        assertNotNull("Boarding transition should start", engine.passengerTransition)
        assertEquals(TransitionPhase.BOARDING, engine.passengerTransition?.phase)
        assertFalse("Passenger not seated until boarding animation finishes", engine.hasPassenger)

        repeat(45) {
            engine.step(0.025f, InputControls())
        }
        assertTrue("Passenger should now be seated", engine.hasPassenger)
        assertEquals(PassengerPersona.FEMALE, engine.currentPassengerPersona)

        // Now trigger dropoff
        engine.stopZones.clear()
        engine.stopZones += PassengerStopZone(
            id = 501,
            type = StopZoneType.DROPOFF,
            onLeftSide = true,
            y = engine.playerY,
            passengerStyleIndex = 1,
            persona = PassengerPersona.FEMALE
        )
        engine.step(0.016f, InputControls())
        assertEquals(TransitionPhase.ALIGHTING, engine.passengerTransition?.phase)
        assertEquals(1, engine.passengersDelivered)
        assertTrue("Burst coins should spawn on dropoff", engine.burstCoins.isNotEmpty())
    }

    @Test
    fun missionAndGarageCatalogsAreComplete() {
        assertEquals(8, MissionCatalog.missions.size)
        assertEquals(4, MissionCatalog.challenges.size)
        assertEquals(6, MissionCatalog.rickshawDesigns.size)
        assertEquals(4, MissionCatalog.canopyStyles.size)
        assertEquals(4, MissionCatalog.decorationStyles.size)
    }
}
