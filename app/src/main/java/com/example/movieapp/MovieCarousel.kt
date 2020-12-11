package com.example.movieapp

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.rememberScrollableController
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonConstants
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ContentDrawScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.DrawModifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.AmbientConfiguration
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.InspectorValueInfo
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.chrisbanes.accompanist.coil.CoilImage
import java.util.*


val posterAspectRatio = .674f

@Composable
fun Screen() {
    val configuration = AmbientConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp


    var offset by remember { mutableStateOf(0f) }

    val ctrlr = rememberScrollableController {
        offset = offset.plus(it)
        it
    }
    /** Box (previously called Stack) is a layout that puts elements on top of each other */
    Box(
        Modifier
            .background(Color.Black)
            .fillMaxSize()
            .scrollable(
                Orientation.Horizontal,
                ctrlr
            )
    )
    {
        movies.forEachIndexed { index, movie ->
            /** white background image */
            CoilImage(
                data = movie.bgUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(posterAspectRatio)

            )
        }
        /** White gradient in the background */
        Spacer(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.3f) //fills 30% of the screen
                .background(VerticalGradient(
                    0f to Color.White,
                         screenHeight.value to Color.Transparent,
                          startY = screenHeight.value,
                          endY = 0f))
        )

        movies.forEachIndexed { index, movie ->
            MoviePoster(
                movie = movie,
                modifier = Modifier
                    .offset(getX = { offset.dp + (screenWidth * index) }, getY = { 0.dp })
                    .width(screenWidth * 0.75f)
            )
        }

    }
}

@Composable
fun MoviePoster(movie: Movie, modifier: Modifier = Modifier) {

    Column(
        modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(20.dp)
    ) {
        CoilImage(
            movie.posterUrl,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .aspectRatio(posterAspectRatio)
        )
        Text(
            movie.title,
            fontSize = 24.sp,
            color = Color.Black
        )
        Row {
            for (chip in movie.chips)
                Chip(chip)

        }
        StarRating(9.0f)
//        Spacer(Modifier.height(30.dp))
//        BuyTicketButton(onClick = { })
    }
}

@Composable
fun BuyTicketButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonConstants.defaultButtonColors(Color.DarkGray),
        elevation = ButtonConstants.defaultElevation(0.dp),
        modifier = Modifier
            .fillMaxWidth()

    ) {
        Text(
            text = "Buy ticket".toUpperCase(Locale.ROOT),
        )
    }
}

@Composable
fun StarRating(rating: Float) {

}

@Composable
fun Chip(label: String, modifier: Modifier = Modifier) {
    Text(
        label,
        fontSize = 9.sp,
        color = Color.Gray,
        modifier = modifier
            .border(1.dp, Color.Gray, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 2.dp)
    )

}


fun Modifier.offset(
    getX: () -> Dp,
    getY: () -> Dp,
    rtlAware: Boolean = false,
): Modifier = this then object : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            if (rtlAware) {
                placeable.placeRelativeWithLayer(getX().toIntPx(), getY().toIntPx())
            } else {
                placeable.placeWithLayer(getX().toIntPx(), getY().toIntPx())
            }
        }
    }
}







