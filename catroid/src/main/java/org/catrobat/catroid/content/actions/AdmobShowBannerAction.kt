package org.catrobat.catroid.content.actions

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.admob.AdMobManager
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.stage.StageActivity

class AdmobShowBannerAction : TemporalAction() {
    var scope: Scope? = null
    var position: Formula? = null

    override fun update(percent: Float) {
        if (scope == null) return
        val activity = StageActivity.activeStageActivity?.get() ?: return
        val posStr = position?.interpretString(scope)?.trim() ?: "0"
        val isBottom = posStr.equals("1", ignoreCase = true) || posStr.equals("bottom", ignoreCase = true)
        AdMobManager.bannerPosition = if (isBottom) AdMobManager.BannerPosition.BOTTOM else AdMobManager.BannerPosition.TOP
        AdMobManager.showBanner(activity)
    }
}
