package quiz.game.trivia.presentation.ads

import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import quiz.game.trivia.BuildConfig
import quiz.game.trivia.R
import quiz.game.trivia.presentation.util.findActivity

private var mInterstitialAd: InterstitialAd? = null
private var isLoadingAd = false

fun loadInterstitial(context: Context, isTest: Boolean = BuildConfig.DEBUG) {
    if (isLoadingAd) return

    val unitId = if (isTest) {
        context.getString(R.string.ad_mob_test_inter_id)
    } else {
        context.getString(R.string.ad_mob_inter_id)
    }

    isLoadingAd = true
    InterstitialAd.load(
        context,
        unitId,
        AdRequest.Builder().build(),
        object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
                isLoadingAd = false
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
                isLoadingAd = false
            }
        }
    )
}

fun showInterstitial(context: Context, execute: (() -> Unit)? = null) {
    val activity = context.findActivity()

    if (mInterstitialAd != null && activity != null) {
        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(e: AdError) {
                mInterstitialAd = null
                execute?.invoke()
            }

            override fun onAdDismissedFullScreenContent() {
                mInterstitialAd = null
                loadInterstitial(context)
                execute?.invoke()
            }
        }
        mInterstitialAd?.show(activity)
    } else {
        execute?.invoke()
    }
}

fun removeInterstitial() {
    mInterstitialAd?.fullScreenContentCallback = null
    mInterstitialAd = null
    isLoadingAd = false
}