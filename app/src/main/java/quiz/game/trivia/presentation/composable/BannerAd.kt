package quiz.game.trivia.presentation.composable

import android.annotation.SuppressLint
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import quiz.game.trivia.BuildConfig
import quiz.game.trivia.R

@SuppressLint("VisibleForTests")
@Composable
fun BannerAdView(
    modifier: Modifier = Modifier,
    isTest: Boolean = BuildConfig.DEBUG,
) {

    val unitId = if (isTest) {
        stringResource(id = R.string.ad_mob_test_banner_id)
    } else {
        stringResource(id = R.string.ad_mob_banner_id)
    }

    val configuration = LocalConfiguration.current

    AndroidView(
        modifier = modifier,
        factory = { context ->
            AdView(context).apply {
                val adSize =AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context,configuration.screenWidthDp)
                setAdSize(adSize)
                adUnitId = unitId
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}