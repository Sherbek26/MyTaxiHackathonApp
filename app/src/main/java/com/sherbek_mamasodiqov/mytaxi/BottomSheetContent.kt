package com.sherbek_mamasodiqov.mytaxi

import androidx.annotation.ColorLong
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sherbek_mamasodiqov.mytaxi.ui.theme.MyTaxiTheme

@Preview(showBackground = true)
@Composable
fun BottomSheetContent(
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if(isSystemInDarkTheme()) colorResource(id = R.color.bottom_sheet_items_container_color_dark) else colorResource(
                id = R.color.bottom_sheet_items_container_color_light
            )
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 12.dp, top = 12.dp, bottom = 16.dp, end = 12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            BottomSheetItemContainer(
                icon = R.drawable.tariff,
                name = "Tarif",
                description = "6/8"
            )
            BottomSheetItemContainer(
                icon = R.drawable.order_,
                name = "Buyurtmalar",
                description = "0"
            )
            Divider(
                color = if (isSystemInDarkTheme()) colorResource(id = R.color.item_divider_color_dark)else colorResource(
                    id = R.color.item_divider_color_light
                ),
                thickness = 1.dp
            )
            BottomSheetItemContainer(
                icon = R.drawable.rocket,
                name = "Bordur",
                description = null
            )
        }
    }
}

@Composable
fun BottomSheetItemContainer(
    modifier: Modifier = Modifier,
    @DrawableRes icon : Int,
    name : String,
    description : String?
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "icon",
            tint = if (isSystemInDarkTheme()) colorResource(id = R.color.item_divider_color_dark)else colorResource(
                id = R.color.items_and_text_color_light
            ),
            modifier = Modifier.size(24.dp)
            )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = name,
            fontSize = 18.sp,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
            )
        Text(text = description ?: "",
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            color = if (isSystemInDarkTheme()) colorResource(id = R.color.item_divider_color_dark)else colorResource(
                id = R.color.items_and_text_color_light
            ),
            )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(imageVector = Icons.Rounded.KeyboardArrowRight,
            modifier = Modifier.size(24.dp),
            tint = if (isSystemInDarkTheme()) colorResource(id = R.color.arrow_icon_dark_color) else colorResource(
                id = R.color.arrow_icon_light_color
            ),
            contentDescription = null)
    }
}

@Preview(backgroundColor = 0XF5F6F9, showBackground = true)
@Composable
fun BottomSheetItemPreview(){
    MyTaxiTheme {
        BottomSheetItemContainer(icon = R.drawable.order_, name = "Tarif", description = "6/8")
    }
}
