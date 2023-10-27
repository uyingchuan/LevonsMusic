package com.example.levonsmusic

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.levonsmusic.ui.page.login.LoginPage
import com.example.levonsmusic.ui.page.splash.SplashPage

object ScreenPaths {
    const val splash = "splash"
    const val login = "login"
}

object AppNav {
    lateinit var instance: NavHostController
}

@Composable
fun AppNavGraph() {
    AppNav.instance = rememberNavController()
    NavHost(navController = AppNav.instance, startDestination = ScreenPaths.splash) {
        composable(ScreenPaths.splash) {
            SplashPage()
        }
        composable(ScreenPaths.login) {
            LoginPage()
        }
    }
}