package com.example.levonsmusic

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.levonsmusic.model.PlaylistBean
import com.example.levonsmusic.ui.page.home.HomePage
import com.example.levonsmusic.ui.page.login.CaptchaLoginPage
import com.example.levonsmusic.ui.page.login.QRLoginPage
import com.example.levonsmusic.ui.page.playlist.PlaylistPage
import com.example.levonsmusic.ui.page.splash.SplashPage

object ScreenPaths {
    const val splash = "splash"
    const val qrLogin = "qrLogin"
    const val captchaLogin = "captchaLogin"
    const val home = "home"
    const val playlist = "playlist"
}

object AppNav {
    @SuppressLint("StaticFieldLeak")
    lateinit var instance: NavHostController
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AppNavGraph() {
    AppNav.instance = rememberNavController()
    NavHost(
        navController = AppNav.instance,
        startDestination = ScreenPaths.splash,
        modifier = Modifier.background(Color.Transparent)
    ) {
        composable(ScreenPaths.splash) {
            SplashPage()
        }
        composable(ScreenPaths.qrLogin) {
            QRLoginPage()
        }
        composable(ScreenPaths.captchaLogin) {
            CaptchaLoginPage()
        }
        composable(ScreenPaths.home) {
            HomePage()
        }
        composable(
            ScreenPaths.playlist,
            arguments = listOf(navArgument("playlistBean") {
                type = NavType.ParcelableType(PlaylistBean::class.java)
            })
        ) {
            PlaylistPage(it.arguments!!.getParcelable("playlistBean", PlaylistBean::class.java)!!)
        }
    }
}
