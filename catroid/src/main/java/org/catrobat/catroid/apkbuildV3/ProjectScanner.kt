package org.catrobat.catroid.apkbuildV3

import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.CompositeBrick

object ProjectScanner {

    fun detectPermissions(project: Project?): List<String> {
        val detected = mutableSetOf<String>()

        detected.add("android.permission.INTERNET")
        detected.add("android.permission.ACCESS_NETWORK_STATE")
        detected.add("android.permission.WAKE_LOCK")

        if (project == null) {
            return detected.toList()
        }

        val allBricks = mutableListOf<Brick>()
        val scenes = project.sceneList.toMutableList()
        val globalScene: Scene? = project.globalScene
        if (globalScene != null && scenes.none { it === globalScene }) {
            scenes.add(globalScene)
        }
        for (scene in scenes) {
            for (sprite in scene.spriteList) {
                for (script in sprite.scriptList) {
                    for (brick in script.brickList) {
                        collectBrickTree(brick, allBricks)
                    }
                }
            }
        }

        for (brick in allBricks) {
            val name = brick.javaClass.simpleName

            if (name.contains("Camera", ignoreCase = true)) {
                detected.add("android.permission.CAMERA")
            }

            if (name.contains("Record", ignoreCase = true) ||
                name.contains("Audio", ignoreCase = true) ||
                name.contains("Speech", ignoreCase = true) ||
                name.contains("Microphone", ignoreCase = true) ||
                name.contains("Gemini", ignoreCase = true)
            ) {
                detected.add("android.permission.RECORD_AUDIO")
            }

            if (name.contains("Vibrate", ignoreCase = true) || name.contains("Vibration", ignoreCase = true)) {
                detected.add("android.permission.VIBRATE")
            }

            if (name.contains("File", ignoreCase = true) ||
                name.contains("Folder", ignoreCase = true) ||
                name.contains("Zip", ignoreCase = true) ||
                name.contains("Save", ignoreCase = true) ||
                name.contains("Export", ignoreCase = true) ||
                name.contains("Write", ignoreCase = true)
            ) {
                detected.add("android.permission.READ_EXTERNAL_STORAGE")
                detected.add("android.permission.WRITE_EXTERNAL_STORAGE")
            }

            if (name.contains("Location", ignoreCase = true) ||
                name.contains("Gps", ignoreCase = true) ||
                name.contains("Latitude", ignoreCase = true) ||
                name.contains("Longitude", ignoreCase = true)
            ) {
                detected.add("android.permission.ACCESS_FINE_LOCATION")
                detected.add("android.permission.ACCESS_COARSE_LOCATION")
            }

            if (name.contains("Bluetooth", ignoreCase = true) ||
                name.contains("Lego", ignoreCase = true) ||
                name.contains("Ev3", ignoreCase = true) ||
                name.contains("Nxt", ignoreCase = true)
            ) {
                detected.add("android.permission.BLUETOOTH")
                detected.add("android.permission.BLUETOOTH_ADMIN")
                detected.add("android.permission.BLUETOOTH_CONNECT")
            }

            if (name.contains("Nfc", ignoreCase = true)) {
                detected.add("android.permission.NFC")
            }

            if (name.contains("Notification", ignoreCase = true)) {
                detected.add("android.permission.POST_NOTIFICATIONS")
            }
        }

        return detected.toList()
    }

    private fun collectBrickTree(brick: Brick, out: MutableList<Brick>) {
        brick.addToFlatList(out)
        if (brick is CompositeBrick) {
            for (nested in brick.nestedBricks) {
                nested.addToFlatList(out)
            }
        }
    }
}
