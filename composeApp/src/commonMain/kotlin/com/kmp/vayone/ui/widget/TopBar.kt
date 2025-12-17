package com.kmp.vayone.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.vayone.data.Strings
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_2B2621
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.back

@Composable
fun TopBar(
    title: String,
    tintColor: Color = C_2B2621,
    modifier: Modifier = Modifier.fillMaxWidth().height(44.dp).background(white),
    rightText: String = "",
    onBackClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.padding(horizontal = 16.dp).size(24.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.back),
                contentDescription = "back",
                tint = tintColor,
                modifier = Modifier.fillMaxSize()
            )
        }
        AutoSizeText(
            text = title,
            color = tintColor,
            maxFontSize = 20.sp,
            minFontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f).padding(end = 16.dp),
        )
        if (rightText.isNotBlank()) {
            Text(
                text = rightText,
                fontSize = 20.sp,
                color = C_2B2621,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    }
}

@Composable
fun rememberKeyboardVisible(): Boolean {
    val ime = WindowInsets.ime
    return ime.getBottom(LocalDensity.current) > 0
}

@Preview
@Composable
fun PreTopBar() {
    TopBar(Strings["privacy_agree1_blue"]) {}
}