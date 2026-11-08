/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.actions

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula

class CrtScreenShaderAction : TemporalAction() {
    var scope: Scope? = null
    var intensityFormula: Formula? = null

    override fun update(percent: Float) {
        val s = scope ?: return
        val lookData = s.sprite.look.lookData ?: return
        val source = sourcePixmap(lookData) ?: return
        val w = source.width
        val h = source.height
        if (w <= 0 || h <= 0) return

        val intensity = ((intensityFormula?.interpretFloat(s) ?: DEFAULT_INTENSITY) / 100f).coerceIn(0f, 1f)
        val dst = Pixmap(w, h, Pixmap.Format.RGBA8888)
        val color = Color()

        val dw = (w - 1).coerceAtLeast(1).toFloat()
        val dh = (h - 1).coerceAtLeast(1).toFloat()
        val scanline = SCANLINE_STRENGTH * intensity
        val mask = MASK_STRENGTH * intensity
        val vignette = VIGNETTE_STRENGTH * intensity
        val grain = GRAIN_STRENGTH * intensity

        val maskR = FloatArray(w)
        val maskG = FloatArray(w)
        val maskB = FloatArray(w)
        val distanceX = FloatArray(w)
        for (x in 0 until w) {
            val phase = x % 3
            maskR[x] = channelFactor(phase, 0, mask)
            maskG[x] = channelFactor(phase, 1, mask)
            maskB[x] = channelFactor(phase, 2, mask)
            val nx = (x / dw - 0.5f) * 2f
            distanceX[x] = nx * nx
        }

        for (y in 0 until h) {
            val ny = (y / dh - 0.5f) * 2f
            val scan = if (y % 3 == 2) 1f - scanline else 1f
            val distanceY = ny * ny
            for (x in 0 until w) {
                Color.rgba8888ToColor(color, source.getPixel(x, y))
                val vignetteFactor = (1f - vignette * (distanceX[x] + distanceY) * 0.5f).coerceAtLeast(0f)
                val grainOffset = (hash(x, y) - 0.5f) * grain
                color.r = (color.r * scan * maskR[x] * vignetteFactor + grainOffset).coerceIn(0f, 1f)
                color.g = (color.g * scan * maskG[x] * vignetteFactor + grainOffset).coerceIn(0f, 1f)
                color.b = (color.b * scan * maskB[x] * vignetteFactor + grainOffset).coerceIn(0f, 1f)
                dst.drawPixel(x, y, Color.rgba8888(color))
            }
        }

        val oldTexture = lookData.textureRegion
        lookData.setPixmap(dst)
        lookData.setShaderResultPixmap(dst)
        lookData.setTextureRegion(TextureRegion(Texture(dst)))
        s.sprite.look.refreshTextures(true)
        if (oldTexture != null) oldTexture.texture.dispose()
    }

    private fun sourcePixmap(lookData: LookData): Pixmap? {
        val current = lookData.pixmap ?: return null
        val cached = lookData.shaderSourcePixmap
        if (cached != null && current === lookData.shaderResultPixmap) {
            return cached
        }
        val copy = Pixmap(current.width, current.height, Pixmap.Format.RGBA8888)
        copy.drawPixmap(current, 0, 0)
        cached?.dispose()
        lookData.setShaderSourcePixmap(copy)
        lookData.setShaderResultPixmap(null)
        return copy
    }

    private fun channelFactor(phase: Int, channel: Int, strength: Float): Float =
        if (phase == channel) 1f + strength else 1f - strength * 0.5f

    private fun hash(x: Int, y: Int): Float {
        var value = x * 374761393 + y * 668265263
        value = (value xor (value shr 13)) * 1274126177
        value = value xor (value shr 16)
        return (value and 0xFFFF) / 65535f
    }

    companion object {
        private const val DEFAULT_INTENSITY = 80f
        private const val SCANLINE_STRENGTH = 0.4f
        private const val MASK_STRENGTH = 0.25f
        private const val VIGNETTE_STRENGTH = 0.55f
        private const val GRAIN_STRENGTH = 0.12f
    }
}
