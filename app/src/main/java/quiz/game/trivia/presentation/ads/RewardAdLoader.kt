package quiz.game.trivia.presentation.ads

import android.content.Context
import android.widget.Toast
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import quiz.game.trivia.BuildConfig
import quiz.game.trivia.R
import quiz.game.trivia.presentation.util.findActivity

private var mRewardAd: RewardedAd? = null
private var isLoadingAd = false

fun loadRewardAd(context: Context, isTest: Boolean = BuildConfig.DEBUG) {
    if (isLoadingAd) return

    val unitId = if (isTest) {
        context.getString(R.string.ad_mob_test_reward_id)
    } else {
        context.getString(R.string.ad_mob_reward_id)
    }

    isLoadingAd = true
    RewardedAd.load(
        context,
        unitId,
        AdRequest.Builder().build(),
        object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mRewardAd = null
                isLoadingAd = false
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                mRewardAd = rewardedAd
                isLoadingAd = false
            }
        }
    )
}

fun showRewardAd(context: Context, execute: () -> Unit) {
    val activity = context.findActivity()

    var rewardGranted = false
    if (mRewardAd != null && activity != null) {
        mRewardAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(e: AdError) {
                mRewardAd = null
                Toast.makeText(context, "No ads found at the moment.", Toast.LENGTH_SHORT).show()
            }

            override fun onAdDismissedFullScreenContent() {
                mRewardAd = null
                loadRewardAd(context)
                if (rewardGranted) {
                    execute()
                }
            }
        }
        mRewardAd?.show(activity) {
            rewardGranted = true
            Toast.makeText(context, "Access granted.", Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(context, "No ads found at the moment.", Toast.LENGTH_SHORT).show()
    }
}

fun removeRewardAd() {
    mRewardAd?.fullScreenContentCallback = null
    mRewardAd = null
    isLoadingAd = false
}