package org.catrobat.catroid.apkbuildV3.neoui

import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable

data class NeoUiBackground(
    var type: String = "color",
    var color: String = "#3A3A3C",
    var asset: String? = null
) : Serializable {
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("type", type)
            put("color", color)
            if (asset != null) put("asset", asset)
        }
    }

    companion object {
        fun fromJson(json: JSONObject): NeoUiBackground {
            return NeoUiBackground(
                type = json.optString("type", "color"),
                color = json.optString("color", "#3A3A3C"),
                asset = if (json.has("asset")) json.optString("asset") else null
            )
        }
    }
}

data class NeoUiProgressBar(
    var visible: Boolean = true,
    var x: Float = 0.1f,
    var y: Float = 0.85f,
    var width: Float = 0.8f,
    var height: Float = 0.02f,
    var color: String = "#A8DFF4"
) : Serializable {
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("visible", visible)
            put("x", x.toDouble())
            put("y", y.toDouble())
            put("width", width.toDouble())
            put("height", height.toDouble())
            put("color", color)
        }
    }

    companion object {
        fun fromJson(json: JSONObject): NeoUiProgressBar {
            return NeoUiProgressBar(
                visible = json.optBoolean("visible", true),
                x = json.optDouble("x", 0.1).toFloat(),
                y = json.optDouble("y", 0.85).toFloat(),
                width = json.optDouble("width", 0.8).toFloat(),
                height = json.optDouble("height", 0.02).toFloat(),
                color = json.optString("color", "#A8DFF4")
            )
        }
    }
}

data class NeoUiText(
    val id: Long = System.currentTimeMillis(),
    var content: String = "Loading…",
    var x: Float = 0.1f,
    var y: Float = 0.75f,
    var width: Float = 0.8f,
    var height: Float = 0.06f,
    var fontSize: Float = 16f,
    var color: String = "#FFFFFF",
    var fontFamily: String = "default",
    var rotation: Float = 0f
) : Serializable {
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("content", content)
            put("x", x.toDouble())
            put("y", y.toDouble())
            put("width", width.toDouble())
            put("height", height.toDouble())
            put("fontSize", fontSize.toDouble())
            put("color", color)
            put("fontFamily", fontFamily)
            put("rotation", rotation.toDouble())
        }
    }

    companion object {
        fun fromJson(json: JSONObject): NeoUiText {
            return NeoUiText(
                id = json.optLong("id", System.currentTimeMillis()),
                content = json.optString("content", "Loading…"),
                x = json.optDouble("x", 0.1).toFloat(),
                y = json.optDouble("y", 0.75).toFloat(),
                width = json.optDouble("width", 0.8).toFloat(),
                height = json.optDouble("height", 0.06).toFloat(),
                fontSize = json.optDouble("fontSize", 16.0).toFloat(),
                color = json.optString("color", "#FFFFFF"),
                fontFamily = json.optString("fontFamily", "default"),
                rotation = json.optDouble("rotation", 0.0).toFloat()
            )
        }
    }
}

data class NeoUiLayout(
    var version: Int = 1,
    var background: NeoUiBackground = NeoUiBackground(),
    var progressBar: NeoUiProgressBar = NeoUiProgressBar(),
    val texts: MutableList<NeoUiText> = mutableListOf()
) : Serializable {
    fun requireValid() {
        require(version == 1)
        require(texts.size <= 20)
        require(background.color.matches(Regex("#[0-9A-Fa-f]{6}([0-9A-Fa-f]{2})?")))
        require(progressBar.color.matches(Regex("#[0-9A-Fa-f]{6}([0-9A-Fa-f]{2})?")))
        require(progressBar.x.isFinite() && progressBar.y.isFinite() && progressBar.width.isFinite() && progressBar.height.isFinite())
        require(progressBar.x in -1f..2f && progressBar.y in -1f..2f && progressBar.width in 0.005f..2f && progressBar.height in 0.005f..2f)
        texts.forEach { text ->
            require(text.content.length <= 512)
            require(text.color.matches(Regex("#[0-9A-Fa-f]{6}([0-9A-Fa-f]{2})?")))
            require(text.x.isFinite() && text.y.isFinite() && text.width.isFinite() && text.height.isFinite() && text.fontSize.isFinite())
            require(text.x in -1f..2f && text.y in -1f..2f && text.width in 0.01f..2f && text.height in 0.01f..2f && text.fontSize in 6f..128f)
            require(text.rotation.isFinite() && text.rotation in -360f..360f)
        }
    }

    fun toJson(): JSONObject {
        val json = JSONObject()
        json.put("version", version)
        json.put("background", background.toJson())
        json.put("progressBar", progressBar.toJson())
        val textsArray = JSONArray()
        for (text in texts) {
            textsArray.put(text.toJson())
        }
        json.put("texts", textsArray)
        return json
    }

    fun toJsonString(): String = toJson().toString(2)

    companion object {
        fun fromJson(json: JSONObject): NeoUiLayout {
            val version = json.optInt("version", 1)
            val bg = if (json.has("background")) NeoUiBackground.fromJson(json.getJSONObject("background")) else NeoUiBackground()
            val pb = if (json.has("progressBar")) NeoUiProgressBar.fromJson(json.getJSONObject("progressBar")) else NeoUiProgressBar()
            val textsList = mutableListOf<NeoUiText>()
            if (json.has("texts")) {
                val array = json.getJSONArray("texts")
                for (i in 0 until array.length()) {
                    textsList.add(NeoUiText.fromJson(array.getJSONObject(i)))
                }
            }
            return NeoUiLayout(version, bg, pb, textsList)
        }

        fun fromJsonString(jsonStr: String): NeoUiLayout {
            return fromJson(JSONObject(jsonStr))
        }

        fun createDefault(themeBgColor: String = "#3A3A3C", themeAccentColor: String = "#A8DFF4"): NeoUiLayout {
            return NeoUiLayout(
                version = 1,
                background = NeoUiBackground(type = "color", color = themeBgColor),
                progressBar = NeoUiProgressBar(visible = true, x = 0.1f, y = 0.52f, width = 0.8f, height = 0.01f, color = themeAccentColor),
                texts = mutableListOf(
                    NeoUiText(
                        id = 1L,
                        content = "Loading…",
                        x = 0.1f,
                        y = 0.44f,
                        width = 0.8f,
                        height = 0.06f,
                        fontSize = 22f,
                        color = themeAccentColor
                    )
                )
            )
        }

        fun createDefaultCompilation(themeAccentColor: String = "#FFFFFF"): NeoUiLayout {
            return NeoUiLayout(
                version = 1,
                background = NeoUiBackground(type = "color", color = "#555555"),
                progressBar = NeoUiProgressBar(visible = true, x = 0.3f, y = 0.54f, width = 0.4f, height = 0.008f, color = themeAccentColor),
                texts = mutableListOf(
                    NeoUiText(
                        id = 2L,
                        content = "Compiling scene: {scene}",
                        x = 0.1f,
                        y = 0.44f,
                        width = 0.8f,
                        height = 0.06f,
                        fontSize = 20f,
                        color = "#FFFFFF"
                    )
                )
            )
        }
    }
}

data class NeoUiManifest(
    var neoui_version: Int = 1,
    var created_at: String = "",
    var author: String = "",
    var project_name: String = "",
    var description: String = ""
) : Serializable {
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("neoui_version", neoui_version)
            put("created_at", created_at)
            put("author", author)
            put("project_name", project_name)
            put("description", description)
        }
    }

    companion object {
        fun fromJson(json: JSONObject): NeoUiManifest {
            return NeoUiManifest(
                neoui_version = json.optInt("neoui_version", 1),
                created_at = json.optString("created_at", ""),
                author = json.optString("author", ""),
                project_name = json.optString("project_name", ""),
                description = json.optString("description", "")
            )
        }
    }
}
