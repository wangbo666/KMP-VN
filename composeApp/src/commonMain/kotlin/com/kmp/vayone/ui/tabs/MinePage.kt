package com.kmp.vayone.ui.tabs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.vayone.data.Strings
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_FC7700
import theme.C_FFBB48
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.avatar
import vayone.composeapp.generated.resources.avatar_bg
import vayone.composeapp.generated.resources.mine_icon


@Composable
fun MinePage() {
    Column(
        modifier = Modifier.fillMaxSize()
            .background(white)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .height(298.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(C_FFBB48, C_FC7700)
                    ),
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
        ) {
            Image(
                modifier = Modifier.padding(top = 42.dp).size(174.dp).align(Alignment.TopEnd),
                contentDescription = null,
                alignment = Alignment.TopEnd,
                painter = painterResource(Res.drawable.mine_icon)
            )
            Box(modifier = Modifier.padding(top = 86.dp, start = 19.dp).size(66.dp)) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = null,
                    painter = painterResource(Res.drawable.avatar_bg)
                )
                Image(
                    modifier = Modifier.size(60.dp).align(Alignment.Center),
                    contentDescription = null,
                    painter = painterResource(Res.drawable.avatar)
                )
            }
            Text(
                text = Strings["welcome_str"],
                color = white,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.wrapContentSize().align(Alignment.TopStart)
                    .padding(start = 92.dp, top = 91.dp)
            )
            Text(
                text = "",
                color = white,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.wrapContentSize().align(Alignment.TopStart)
                    .padding(start = 92.dp, top = 116.dp)
            )
            Row(modifier = Modifier.fillMaxWidth()
                .height(125.dp)
                .align(Alignment.BottomCenter)
                .background(color = white.copy(0.3f), shape = RoundedCornerShape(24.dp))) {

            }
        }
    }
}

@Preview
@Composable
fun PreMine() {
    MinePage()
}