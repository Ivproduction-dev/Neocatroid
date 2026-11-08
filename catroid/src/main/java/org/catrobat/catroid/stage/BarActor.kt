package org.catrobat.catroid.stage

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor

class BarActor(private val state: BarState) : Actor() {

    val barId: String = state.id

    override fun draw(batch: Batch, parentAlpha: Float) {
        val gdxStage = stage ?: return
        val stageW = gdxStage.width
        val stageH = gdxStage.height
        if (stageW <= 0f || stageH <= 0f) return

        val w = stageW * state.widthPct / 100f
        val h = (stageH * state.heightPct / 100f).coerceAtLeast(3f)
        val x = stageW * state.xPct / 100f - w / 2f
        val y = stageH * (1f - state.yPct / 100f) - h / 2f

        val tex = sharedTexture()
        val oldColor = batch.color

        batch.setColor(
            (state.trackColor shr 16 and 0xFF) / 255f,
            (state.trackColor shr 8 and 0xFF) / 255f,
            (state.trackColor and 0xFF) / 255f,
            1f
        )
        batch.draw(tex, x, y, w, h)

        val fraction = state.fraction()
        if (fraction > 0f) {
            batch.setColor(
                (state.fillColor shr 16 and 0xFF) / 255f,
                (state.fillColor shr 8 and 0xFF) / 255f,
                (state.fillColor and 0xFF) / 255f,
                1f
            )
            batch.draw(tex, x, y, w * fraction, h)
        }

        batch.setColor(oldColor)
    }

    companion object {
        private var shared: Texture? = null

        @Synchronized
        private fun sharedTexture(): Texture {
            shared?.let { return it }
            val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
            pixmap.setColor(1f, 1f, 1f, 1f)
            pixmap.fill()
            val texture = Texture(pixmap)
            pixmap.dispose()
            shared = texture
            return texture
        }
    }
}
