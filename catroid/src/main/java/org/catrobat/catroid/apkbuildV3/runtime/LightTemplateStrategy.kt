package org.catrobat.catroid.apkbuildV3.runtime

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Scene
import java.io.File

class LightTemplateStrategy(private val context: Context) {
    private val tag = "LightTemplateStrategy"
    private val cache by lazy {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val totalRam = am?.let {
            val memInfo = ActivityManager.MemoryInfo()
            it.getMemoryInfo(memInfo)
            memInfo.totalMem
        } ?: (2L * 1024 * 1024 * 1024)
        MemoryAwareCache.forDevice(totalRam / (1024 * 1024))
    }

    private var metadata: ProjectLoaderV4.ProjectMetadata? = null
    private val loadedScenes = mutableSetOf<String>()

    @Volatile
    var lastError: String? = null
        private set

    fun initialize(cacheDir: File, onProgress: ((Float) -> Unit)? = null): Boolean {
        lastError = null
        return try {
            onProgress?.invoke(0f)

            val loader = ProjectLoaderV4(context)
            val meta = loader.loadLight(cacheDir) ?: run {
                lastError = loader.lastError ?: "unknown loader failure"
                return false
            }
            metadata = meta
            onProgress?.invoke(0.5f)

            val firstScene = meta.project.defaultScene
            if (firstScene != null) {
                loadScene(firstScene.name, cacheDir)
            }

            ProjectManager.getInstance().currentProject = meta.project

            onProgress?.invoke(1f)
            Log.i(tag, "Light template initialized: ${meta.project.name} " +
                    "(preloaded: ${firstScene?.name})")

            true
        } catch (t: Throwable) {
            lastError = "strategy: ${t.javaClass.simpleName}: ${t.message}"
            Log.e(tag, "Light template initialization failed", t)
            false
        }
    }

    fun loadScene(sceneName: String, cacheDir: File): Scene? {
        val meta = metadata ?: return null

        cache.get(sceneName)?.let { return it as? Scene }

        val scene = meta.project.getSceneByName(sceneName) ?: return null

        return try {
            scene.setProject(meta.project)

            val sceneSize = estimateSceneSize(scene)
            cache.put(sceneName, scene, sceneSize)

            loadedScenes.add(sceneName)
            Log.d(tag, "Scene '$sceneName' loaded on demand (~${sceneSize / (1024 * 1024)} MB)")

            scene
        } catch (e: Exception) {
            Log.e(tag, "Failed to load scene '$sceneName' on demand", e)
            null
        }
    }

    fun preloadInitialScenes(cacheDir: File, count: Int = 3) {
        val meta = metadata ?: return
        val scenes = meta.project.sceneList.take(count)
        for (scene in scenes) {
            if (scene.name !in loadedScenes) {
                loadScene(scene.name, cacheDir)
            }
        }
    }

    fun evictScene(sceneName: String) {
        cache.remove(sceneName)
        loadedScenes.remove(sceneName)
        Log.d(tag, "Scene '$sceneName' evicted from cache")
    }

    fun clearCache() {
        cache.clear()
        loadedScenes.clear()
        Log.d(tag, "Scene cache cleared")
    }

    val memoryUsageBytes: Long get() = cache.approximateMemoryBytes()

    val activeScenes: Set<String> get() = loadedScenes.toSet()

    private fun estimateSceneSize(scene: Scene): Long {
        var total = 0L
        for (sprite in scene.spriteList) {
            total += sprite.lookList.sumOf { look ->
                look.file?.length() ?: 0L
            }
            total += sprite.soundList.sumOf { sound ->
                sound.file?.length() ?: 0L
            }
        }
        return total.coerceAtLeast(1024L * 1024L)
    }
}
