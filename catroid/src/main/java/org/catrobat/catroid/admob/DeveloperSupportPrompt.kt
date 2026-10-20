package org.catrobat.catroid.admob

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object DeveloperSupportPrompt {
    private const val PREFERENCES_NAME = "developer_support_prompt"
    private const val LAST_REWARDED_AT = "last_rewarded_at"
    private const val REWARD_COOLDOWN_MS = 48L * 60 * 60 * 1000

    private var loadAttemptedThisProcess = false
    private var loadFinishedThisProcess = false
    private var promptDisplayedThisProcess = false
    private var rewardedAd: RewardedAd? = null
    private var pendingAdLoadedCallback: (() -> Unit)? = null

    fun request(activity: Activity, rewardedAdUnitId: String, onAdLoaded: () -> Unit) {
        if (promptDisplayedThisProcess) return

        if (loadAttemptedThisProcess) {
            if (rewardedAd != null) {
                onAdLoaded()
            } else if (!loadFinishedThisProcess) {
                pendingAdLoadedCallback = onAdLoaded
            }
            return
        }
        loadAttemptedThisProcess = true

        val preferences = activity.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val lastRewardedAt = preferences.getLong(LAST_REWARDED_AT, 0L)
        if (lastRewardedAt > 0L && System.currentTimeMillis() < lastRewardedAt + REWARD_COOLDOWN_MS) {
            loadFinishedThisProcess = true
            return
        }

        pendingAdLoadedCallback = onAdLoaded
        val appContext = activity.applicationContext
        AdMobManager.initialize(
            activity,
            onInitialized = {
                try {
                    RewardedAd.load(
                        appContext,
                        rewardedAdUnitId,
                        AdRequest.Builder().build(),
                        object : RewardedAdLoadCallback() {
                            override fun onAdLoaded(ad: RewardedAd) {
                                rewardedAd = ad
                                loadFinishedThisProcess = true
                                val callback = pendingAdLoadedCallback
                                pendingAdLoadedCallback = null
                                callback?.invoke()
                            }

                            override fun onAdFailedToLoad(error: LoadAdError) {
                                rewardedAd = null
                                loadFinishedThisProcess = true
                                pendingAdLoadedCallback = null
                            }
                        }
                    )
                } catch (_: Exception) {
                    loadFinishedThisProcess = true
                    rewardedAd = null
                    pendingAdLoadedCallback = null
                }
            },
            onInitializationFailed = {
                loadFinishedThisProcess = true
                pendingAdLoadedCallback = null
            }
        )
    }

    fun markPromptDisplayed() {
        promptDisplayedThisProcess = true
    }

    fun show(activity: Activity) {
        val ad = rewardedAd ?: return
        rewardedAd = null
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                rewardedAd = null
            }

            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
            }
        }
        try {
            ad.show(activity) {
                activity.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putLong(LAST_REWARDED_AT, System.currentTimeMillis())
                    .apply()
            }
        } catch (_: Exception) {
            rewardedAd = null
        }
    }
}
