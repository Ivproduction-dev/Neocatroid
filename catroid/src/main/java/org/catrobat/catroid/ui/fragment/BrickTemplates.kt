package org.catrobat.catroid.ui.fragment

import android.content.Context
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.AdmobAppOpenLoadedEventBrick
import org.catrobat.catroid.content.bricks.AdmobEnableTestModeBrick
import org.catrobat.catroid.content.bricks.AdmobInitializedEventBrick
import org.catrobat.catroid.content.bricks.AdmobInitializeBrick
import org.catrobat.catroid.content.bricks.AdmobInterstitialLoadedEventBrick
import org.catrobat.catroid.content.bricks.AdmobLoadAppOpenBrick
import org.catrobat.catroid.content.bricks.AdmobLoadBannerBrick
import org.catrobat.catroid.content.bricks.AdmobLoadInterstitialBrick
import org.catrobat.catroid.content.bricks.AdmobLoadRewardedBrick
import org.catrobat.catroid.content.bricks.AdmobRewardedLoadedEventBrick
import org.catrobat.catroid.content.bricks.AdmobSetAppIdBrick
import org.catrobat.catroid.content.bricks.AdmobSetAppOpenUnitIdBrick
import org.catrobat.catroid.content.bricks.AdmobSetBannerUnitIdBrick
import org.catrobat.catroid.content.bricks.AdmobSetInterstitialUnitIdBrick
import org.catrobat.catroid.content.bricks.AdmobSetRewardedUnitIdBrick
import org.catrobat.catroid.content.bricks.AdmobShowAppOpenBrick
import org.catrobat.catroid.content.bricks.AdmobShowBannerBrick
import org.catrobat.catroid.content.bricks.AdmobShowInterstitialBrick
import org.catrobat.catroid.content.bricks.AdmobShowRewardedBrick
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.NoteBrick

data class BrickTemplate(
    val id: String,
    val captionRes: Int,
    val descriptionRes: Int,
    val artLayoutRes: Int,
    val createScripts: () -> List<Script>
)

object BrickTemplates {

    private const val TEST_APP_ID = "ca-app-pub-3940256099942544~3347511713"
    private const val TEST_BANNER_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    private const val TEST_INTERSTITIAL_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    private const val TEST_REWARDED_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
    private const val TEST_APP_OPEN_UNIT_ID = "ca-app-pub-3940256099942544/9257390708"

    private fun startScript(vararg bricks: Brick): Script {
        val script = StartScript()
        bricks.forEach { script.addBrick(it) }
        return script
    }

    private fun admobInitializedScript(vararg bricks: Brick): Script {
        val script = AdmobInitializedEventBrick().script
        bricks.forEach { script.addBrick(it) }
        return script
    }

    private fun interstitialLoadedScript(vararg bricks: Brick): Script {
        val script = AdmobInterstitialLoadedEventBrick().script
        bricks.forEach { script.addBrick(it) }
        return script
    }

    private fun rewardedLoadedScript(vararg bricks: Brick): Script {
        val script = AdmobRewardedLoadedEventBrick().script
        bricks.forEach { script.addBrick(it) }
        return script
    }

    private fun appOpenLoadedScript(vararg bricks: Brick): Script {
        val script = AdmobAppOpenLoadedEventBrick().script
        bricks.forEach { script.addBrick(it) }
        return script
    }

    private fun admobBannerTemplate(): List<Script> = listOf(
        startScript(
            NoteBrick("шаблон 1 это как показать баннер рекламу при старте приложения"),
            NoteBrick("включаем тестовый режим фейковая реклама безопасно для проверки"),
            AdmobEnableTestModeBrick(),
            NoteBrick("тут тестовый id приложения от google поменяй на свой потом"),
            AdmobSetAppIdBrick(TEST_APP_ID),
            AdmobInitializeBrick(),
            NoteBrick("инит готов теперь id баннера тоже тестовый тоже поменяй на свой потом"),
            AdmobSetBannerUnitIdBrick(TEST_BANNER_UNIT_ID),
            NoteBrick("сначала загрузи и потом покажи иначе реклама не появится"),
            AdmobLoadBannerBrick(),
            AdmobShowBannerBrick(0)
        )
    )

    private fun admobInterstitialTemplate(): List<Script> = listOf(
        startScript(
            NoteBrick("шаблон 2 это как показать межстраничную рекламу на весь экран"),
            NoteBrick("включаем тестовый режим фейковая реклама безопасно для проверки"),
            AdmobEnableTestModeBrick(),
            NoteBrick("тут тестовые id от google поменяй на свои потом"),
            AdmobSetAppIdBrick(TEST_APP_ID),
            AdmobSetInterstitialUnitIdBrick(TEST_INTERSTITIAL_UNIT_ID),
            NoteBrick("запускаем admob и ждём его готовности в событии ниже"),
            AdmobInitializeBrick()
        ),
        admobInitializedScript(
            NoteBrick("admob готов теперь грузим межстраничную"),
            AdmobLoadInterstitialBrick()
        ),
        interstitialLoadedScript(
            NoteBrick("реклама загрузилась самое время показать"),
            AdmobShowInterstitialBrick(),
            NoteBrick("показывай между уровнями а не посреди игры")
        )
    )

    private fun admobRewardedTemplate(): List<Script> = listOf(
        startScript(
            NoteBrick("шаблон 3 это реклама за которую юзер получает награду"),
            NoteBrick("включаем тестовый режим фейковая реклама безопасно для проверки"),
            AdmobEnableTestModeBrick(),
            NoteBrick("тут тестовые id от google поменяй на свои потом"),
            AdmobSetAppIdBrick(TEST_APP_ID),
            AdmobSetRewardedUnitIdBrick(TEST_REWARDED_UNIT_ID),
            NoteBrick("запускаем admob и ждём его готовности в событии ниже"),
            AdmobInitializeBrick()
        ),
        admobInitializedScript(
            NoteBrick("admob готов теперь грузим рекламу с наградой"),
            AdmobLoadRewardedBrick()
        ),
        rewardedLoadedScript(
            NoteBrick("реклама загрузилась показывай когда юзер сам захочет"),
            AdmobShowRewardedBrick(),
            NoteBrick("награду юзер получит через событие награда в отдельном скрипте")
        )
    )

    private fun admobAppOpenTemplate(): List<Script> = listOf(
        startScript(
            NoteBrick("шаблон 4 это реклама когда юзер возвращается в приложение"),
            NoteBrick("включаем тестовый режим фейковая реклама безопасно для проверки"),
            AdmobEnableTestModeBrick(),
            NoteBrick("тут тестовые id от google поменяй на свои потом"),
            AdmobSetAppIdBrick(TEST_APP_ID),
            AdmobSetAppOpenUnitIdBrick(TEST_APP_OPEN_UNIT_ID),
            NoteBrick("запускаем admob и ждём его готовности в событии ниже"),
            AdmobInitializeBrick()
        ),
        admobInitializedScript(
            NoteBrick("admob готов теперь грузим app open"),
            AdmobLoadAppOpenBrick()
        ),
        appOpenLoadedScript(
            NoteBrick("реклама загрузилась самое время показать"),
            AdmobShowAppOpenBrick(),
            NoteBrick("показывай когда юзер вернулся в приложение а не при первом запуске")
        )
    )

    private val admobTemplates = listOf(
        BrickTemplate(
            id = "admob_banner_start",
            captionRes = R.string.template_admob_banner_caption,
            descriptionRes = R.string.template_admob_banner_description,
            artLayoutRes = R.layout.template_art_admob_banner,
            createScripts = ::admobBannerTemplate
        ),
        BrickTemplate(
            id = "admob_interstitial",
            captionRes = R.string.template_admob_interstitial_caption,
            descriptionRes = R.string.template_admob_interstitial_description,
            artLayoutRes = R.layout.template_art_admob_interstitial,
            createScripts = ::admobInterstitialTemplate
        ),
        BrickTemplate(
            id = "admob_rewarded",
            captionRes = R.string.template_admob_rewarded_caption,
            descriptionRes = R.string.template_admob_rewarded_description,
            artLayoutRes = R.layout.template_art_admob_rewarded,
            createScripts = ::admobRewardedTemplate
        ),
        BrickTemplate(
            id = "admob_appopen",
            captionRes = R.string.template_admob_appopen_caption,
            descriptionRes = R.string.template_admob_appopen_description,
            artLayoutRes = R.layout.template_art_admob_appopen,
            createScripts = ::admobAppOpenTemplate
        )
    )

    fun forCategory(category: String?, context: Context): List<BrickTemplate> {
        return when (category) {
            context.getString(R.string.category_admob) -> admobTemplates
            else -> emptyList()
        }
    }
}
