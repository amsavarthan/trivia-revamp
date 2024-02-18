package quiz.game.trivia.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.games.AuthenticationResult
import com.google.android.gms.games.PlayGames
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import quiz.game.trivia.data.receivers.BootCompleteReceiver
import quiz.game.trivia.domain.CHANNEL_ID
import quiz.game.trivia.domain.MAX_ENERGY
import quiz.game.trivia.domain.models.UserData
import quiz.game.trivia.presentation.ads.loadEnergyRewardAd
import quiz.game.trivia.presentation.ads.loadInterstitial
import quiz.game.trivia.presentation.ads.loadRewardAd
import quiz.game.trivia.presentation.ads.removeEnergyRewardAd
import quiz.game.trivia.presentation.ads.removeInterstitial
import quiz.game.trivia.presentation.ads.removeRewardAd
import quiz.game.trivia.presentation.composable.BannerAdView
import quiz.game.trivia.presentation.navigation.AppScreen.CASUAL_MODE
import quiz.game.trivia.presentation.navigation.AppScreen.CHOOSE_MODE
import quiz.game.trivia.presentation.navigation.AppScreen.CONNECT_GOOGLE_PLAY
import quiz.game.trivia.presentation.navigation.AppScreen.COUNT_DOWN
import quiz.game.trivia.presentation.navigation.AppScreen.GAME
import quiz.game.trivia.presentation.navigation.AppScreen.HOME
import quiz.game.trivia.presentation.navigation.AppScreen.QUICK_MODE
import quiz.game.trivia.presentation.navigation.AppScreen.RESULT
import quiz.game.trivia.presentation.navigation.AppScreen.SETTINGS
import quiz.game.trivia.presentation.screens.casual_mode.CasualModeScreen
import quiz.game.trivia.presentation.screens.choose_mode.ChooseModeScreen
import quiz.game.trivia.presentation.screens.connect_play.ConnectGPlayScreen
import quiz.game.trivia.presentation.screens.count_down.CountDownScreen
import quiz.game.trivia.presentation.screens.game.GameScreen
import quiz.game.trivia.presentation.screens.home.HomeScreen
import quiz.game.trivia.presentation.screens.quick_mode.QuickModeScreen
import quiz.game.trivia.presentation.screens.result.ResultScreen
import quiz.game.trivia.presentation.screens.settings.SettingsScreen
import quiz.game.trivia.presentation.theme.TriviaTheme
import quiz.game.trivia.presentation.util.isTablet
import quiz.game.trivia.presentation.util.setAlarm

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private val gamesSignInClient by lazy {
        PlayGames.getGamesSignInClient(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        requestedOrientation = if (isTablet()) {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        val receiver = ComponentName(applicationContext, BootCompleteReceiver::class.java)

        applicationContext.packageManager?.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        var isMobileAdsInitialized by mutableStateOf(false)
        splashScreen.setKeepOnScreenCondition {
            !isMobileAdsInitialized
        }

        setContent {
            TriviaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        val navController = rememberNavController()
                        val uiState by viewModel.uiState.collectAsState()

                        var initialSignInCompleted by rememberSaveable {
                            mutableStateOf(false)
                        }

                        val singlePermissionLauncher = rememberLauncherForActivityResult(
                            ActivityResultContracts.RequestPermission()
                        ) { isGranted ->
                            if (isGranted) createNotificationChannel()
                        }

                        //To set alarm again once energy decreases since the alarm
                        //gets canceled once filled to MAX
                        LaunchedEffect(uiState.energyCount) {
                            if (uiState.energyCount == MAX_ENERGY - 1) setAlarm()
                        }

                        //To ask notification permission only when not connecting
                        //Google play games
                        LaunchedEffect(uiState.isConnectingToGooglePlayGames) {
                            if (uiState.isConnectingToGooglePlayGames || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return@LaunchedEffect
                            singlePermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        }

                        //To listen to google play games auth only on initial startup
                        LaunchedEffect(Unit) {
                            if (initialSignInCompleted) return@LaunchedEffect
                            gamesSignInClient.isAuthenticated.handleResult()

                            if (!uiState.isGooglePlayGamesConnected) {
                                //simulated delay for ux
                                delay(1000)
                                navController.navigate(CONNECT_GOOGLE_PLAY.destination) {
                                    launchSingleTop = true
                                }
                            }
                            initialSignInCompleted = true
                        }


                        NavHost(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            navController = navController,
                            startDestination = HOME.destination,
                            enterTransition = { fadeIn(animationSpec = tween(300)) },
                            exitTransition = { fadeOut(animationSpec = tween(300)) }
                        ) {
                            composable(CONNECT_GOOGLE_PLAY.destination) {
                                ConnectGPlayScreen(
                                    navController = navController,
                                    isGooglePlayConnected = uiState.isGooglePlayGamesConnected,
                                    isConnecting = uiState.isConnectingToGooglePlayGames,
                                    connectToGooglePlayGames = {
                                        gamesSignInClient.signIn().handleResult()
                                    }
                                )
                            }
                            composable(
                                route = HOME.destination,
                                arguments = listOf(
                                    navArgument("askReview") {
                                        type = NavType.BoolType
                                    },
                                    navArgument("refreshScore") {
                                        type = NavType.BoolType
                                    },
                                )
                            ) {
                                HomeScreen(
                                    navController = navController,
                                    userData = uiState.userData,
                                    askReview = { inAppReview() },
                                )
                            }
                            composable(SETTINGS.destination) {
                                SettingsScreen(
                                    navController = navController,
                                    connectToGooglePlayGames = {
                                        gamesSignInClient.signIn().handleResult()
                                    }
                                )
                            }
                            composable(CHOOSE_MODE.destination) {
                                ChooseModeScreen(navController = navController)
                            }
                            composable(QUICK_MODE.destination) {
                                QuickModeScreen(navController = navController)
                            }
                            composable(CASUAL_MODE.destination) {
                                CasualModeScreen(navController = navController)
                            }
                            composable(
                                route = COUNT_DOWN.destination,
                                arguments = listOf(
                                    navArgument("category") {
                                        type = NavType.IntType
                                    }
                                )
                            ) { entry ->
                                val categoryIndex = entry.arguments?.getInt("category")
                                    ?: return@composable

                                CountDownScreen(
                                    navController = navController,
                                    categoryIndex = categoryIndex
                                )
                            }
                            composable(
                                route = GAME.destination,
                                arguments = listOf(
                                    navArgument("category") {
                                        type = NavType.IntType
                                    }
                                )
                            ) {
                                GameScreen(navController = navController)
                            }
                            composable(
                                route = RESULT.destination,
                                arguments = listOf(
                                    navArgument("answers") {
                                        type = NavType.StringType
                                    }
                                )) {
                                ResultScreen(navController = navController)
                            }
                        }

                        BannerAdView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.End)
                        )
                    }
                }
            }
        }

        if (!isMobileAdsInitialized) {
            MobileAds.initialize(this) {
                loadRewardAd(this)
                loadEnergyRewardAd(this)
                loadInterstitial(this)
                isMobileAdsInitialized = true
            }
        }

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Energy Refill Alert"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            // Register the channel with the system
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeRewardAd()
        removeEnergyRewardAd()
        removeInterstitial()
    }

    private fun inAppReview() {
        val reviewManager = ReviewManagerFactory.create(this)
        val requestReviewFlow = reviewManager.requestReviewFlow()
        requestReviewFlow.addOnCompleteListener { request ->
            if (request.isSuccessful) {
                val reviewInfo = request.result
                val flow = reviewManager.launchReviewFlow(this, reviewInfo)
                flow.addOnCompleteListener(this) {}
            }
        }
    }

    private fun Task<AuthenticationResult>.handleResult() {
        viewModel.updateGooglePlayConnectingStatus(true)
        addOnCompleteListener(this@MainActivity) { task ->

            val isConnected = task.isSuccessful && task.result.isAuthenticated
            if (!isConnected) {
                viewModel.updateGooglePlayConnectedStatus(false)
                viewModel.updateGooglePlayConnectingStatus(false)
                viewModel.updateGooglePlayConnectedAccountData(UserData())
                return@addOnCompleteListener
            }

            PlayGames.getPlayersClient(this@MainActivity)
                .currentPlayer
                .addOnSuccessListener { player ->
                    val userData = UserData(
                        name = player.displayName,
                        profilePic = player.hiResImageUri
                    )
                    viewModel.updateGooglePlayConnectedAccountData(userData)
                    viewModel.updateGooglePlayConnectedStatus(true)
                    viewModel.updateGooglePlayConnectingStatus(false)
                }

        }
    }


}