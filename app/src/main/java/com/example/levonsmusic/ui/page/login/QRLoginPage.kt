package com.example.levonsmusic.ui.page.login

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levonsmusic.AppNav
import com.example.levonsmusic.R
import com.example.levonsmusic.ScreenPaths
import com.example.levonsmusic.component.LoadingComponent
import com.example.levonsmusic.extension.dp
import com.example.levonsmusic.extension.sp
import com.example.levonsmusic.extension.toPx
import com.example.levonsmusic.network.MusicApiService
import com.example.levonsmusic.ui.theme.LocalColors
import com.example.levonsmusic.util.QRCodeUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun QRLoginPage() {
    val viewModel = hiltViewModel<QRLoginViewModel>()

    // 第一次进入执行
    LaunchedEffect(UInt) {
        viewModel.checkAuth()
    }

    // authStatus变化时校验是否是验证码过期
    LaunchedEffect(viewModel.authStatus) {
        if (viewModel.authStatus == 800) viewModel.checkAuth()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColors.current.primary),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Box(
                modifier = Modifier
                    .padding(top = 205.dp, start = 5.dp)
                    .size(190.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White)
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_splash_logo),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 200.dp)
                    .size(200.dp)
                    .clip(RoundedCornerShape(50)),
                tint = LocalColors.current.primaryVariant,
            )
        }

        Column(
            modifier = Modifier
                .padding(top = 100.dp)
                .width(540.dp)
                .background(LocalColors.current.pure, shape = RoundedCornerShape(16.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "扫码登录体验",
                fontSize = 40.sp,
                color = LocalColors.current.firstText,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 32.dp, bottom = 16.dp)
            )

            if (viewModel.authStatus == 801 || viewModel.authStatus == 802) {
                Image(
                    bitmap = viewModel.qrCodeBitmap!!.asImageBitmap(),
                    modifier = Modifier.size(400.dp),
                    contentDescription = "登录二维码"
                )
            } else {
                Box(modifier = Modifier.size(400.dp), contentAlignment = Alignment.Center) {
                    LoadingComponent(width = 90.dp, height = 75.dp)
                }
            }

            val tip = when (viewModel.authStatus) {
                801, 802 -> "请使用网易云音乐app扫码授权登录"
                803 -> "正在获取用户信息..."
                else -> "正在加载二维码"
            }

            Text(
                text = tip,
                fontSize = 28.sp,
                textAlign = TextAlign.Center,
                color = LocalColors.current.secondIcon,
                modifier = Modifier.padding(top = 16.dp, start = 32.dp, end = 32.dp)
            )
            Text(
                text = "(仅供学习使用)",
                fontSize = 28.sp,
                textAlign = TextAlign.Center,
                color = LocalColors.current.secondIcon,
                modifier = Modifier.padding(bottom = 32.dp, start = 32.dp, end = 32.dp)
            )
        }
    }
}

@HiltViewModel
class QRLoginViewModel @Inject constructor(private val api: MusicApiService) : ViewModel() {

    // 验证状态
    var authStatus by mutableStateOf<Int?>(null)

    // 验证码
    var qrCodeBitmap by mutableStateOf<Bitmap?>(null)

    // 持有监听扫码状态的Job，重置监听任务时取消上次的任务
    var authJob: Job? = null

    fun checkAuth() {
        authJob?.cancel()
        authStatus = null
        authJob = viewModelScope.launch {
            val qrCodeKeyResult = api.getLoginQRCodeKey()
            val qrCodeValueResult = api.getLoginQRCodeValue(qrCodeKeyResult.data.unikey)
            qrCodeBitmap = QRCodeUtil.createQRCodeBitmap(
                qrCodeValueResult.data.qrurl,
                360.dp.toPx.toInt(),
                360.dp.toPx.toInt()
            )
            var authResult = api.checkQRCodeAuthStatus(qrCodeKeyResult.data.unikey)
            authStatus = authResult.code
            // 轮询验证码状态
            while (authJob?.isActive != false) {
                delay(4000)
                authResult = api.checkQRCodeAuthStatus(qrCodeKeyResult.data.unikey)
                authStatus = authResult.code
                if (authResult.successful()) getAccountInfo(authResult.cookie)
            }
        }
    }

    private suspend fun getAccountInfo(cookie: String) {
        val accountInfoResult = api.getAccountInfo(cookie)
        if (accountInfoResult.successful()) {
            AppNav.instance.popBackStack()
            AppNav.instance.navigate(ScreenPaths.home)
        }
    }
}