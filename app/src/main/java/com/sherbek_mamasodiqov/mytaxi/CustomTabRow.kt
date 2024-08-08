package com.sherbek_mamasodiqov.mytaxi
import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.zIndex
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex

@SuppressLint("RememberReturnType")
@Preview
@ExperimentalFoundationApi
@Composable
fun TabBar(
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableStateOf(1) }
    val pages = listOf("Band", "Faol")
    val indicator = @Composable { tabPositions: List<TabPosition> ->
        CustomIndicator(
            tabPositions, selectedTabIndex, color = if (selectedTabIndex == 0) colorResource(
                id = R.color.custome_red_color
            ) else
                colorResource(id = R.color.custom_green_color)
        )
    }

    Card(
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        TabRow(
            modifier = modifier.fillMaxHeight(),
            selectedTabIndex = selectedTabIndex,
            indicator = indicator,
            divider = { },
            containerColor = if(isSystemInDarkTheme()) colorResource(id = R.color.sheetScaffoldDarkColor) else Color.White
        ) {
            pages.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    modifier = Modifier
                        .clickable(
                            onClick = { selectedTabIndex = index },
                            indication = null,
                            interactionSource = remember {
                                MutableInteractionSource()
                            }
                        )
                        .zIndex(2f),
                    text = {
                        Text(
                            text = title,
                            style = when (index) {
                                0 -> TextStyle(
                                    color = if (selectedTabIndex == index) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 18.sp
                                )

                                1 -> TextStyle(
                                    color = if (selectedTabIndex == index) Color.Black else MaterialTheme.colorScheme.onBackground,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 18.sp
                                )

                                else -> TextStyle(
                                    color = Color.Black,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 18.sp
                                )
                            }
                        )
                    },
                )
            }
        }
    }
}


@Composable
private fun CustomIndicator(
    tabPositions: List<TabPosition>,
    selectedTabIndex : Int,
    color: Color
    ) {
    val transition = updateTransition(selectedTabIndex)
    val indicatorStart by transition.animateDp(
        transitionSpec = {
            if (initialState < targetState) {
                spring(dampingRatio = 1f, stiffness = 50f)
            } else {
                spring(dampingRatio = 1f, stiffness = 200f)
            }
        }, label = ""
    ) {
        tabPositions[it].left
    }

    val indicatorEnd by transition.animateDp(
        transitionSpec = {
            if (initialState < targetState) {
                spring(dampingRatio = 1f, stiffness = 200f)
            } else {
                spring(dampingRatio = 1f, stiffness = 50f)
            }
        }, label = ""
    ) {
        tabPositions[it].right
    }

    Box(
        Modifier
            .offset(x = indicatorStart)
            .wrapContentSize(align = Alignment.BottomStart)
            .width(indicatorEnd - indicatorStart)
            .padding(horizontal = 4.dp)
            .fillMaxSize()
            .background(color = color, RoundedCornerShape(10.dp))
            .zIndex(1f)
    )
}
