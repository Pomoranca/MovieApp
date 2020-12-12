package com.example.movieapp

import androidx.compose.animation.asDisposableClock
import androidx.compose.animation.core.TargetAnimation
import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.animation.FlingConfig
import androidx.compose.foundation.animation.defaultFlingConfig
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.ScrollableController
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonConstants
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import dev.chrisbanes.accompanist.coil.CoilImage
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt
import com.google.android.material.math.MathUtils.lerp


val posterAspectRatio = .674f

@Composable
fun Screen() {
    val configuration = AmbientConfiguration.current
    val density = AmbientDensity.current

    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthPx = with(density) { screenWidth.toPx() }

    val screenHeight = configuration.screenHeightDp.dp
    val screenHeightPx = with(density) { screenHeight.toPx() }


    var offset by remember { mutableStateOf(0f) }

    val posterWidthDp = screenWidth * 0.6f
    val posterSpacingPx = with(density) { posterWidthDp.toPx() + 20.dp.toPx() }

    val flingConfig = defaultFlingConfig {
        TargetAnimation((it / posterSpacingPx).roundToInt() * posterSpacingPx)
    }
    val indexFraction = -1 * offset / posterSpacingPx

    /** Scroll controller */
    val upperBound = 0f
    val lowerBound = -1 * movies.size * posterSpacingPx
    val ctrlr = rememberScrollableController(flingConfig) {
        val target = offset + it
        when {
            target in lowerBound..upperBound -> {
                offset = target
                it
            }
            target > upperBound -> {
                val consumed = upperBound - offset
                offset = upperBound
                consumed
            }
            target < lowerBound -> {
                val consumed = lowerBound - offset
                offset = lowerBound
                consumed

            }
            else -> {
                offset = target
                it
            }
        }

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

            /** make image visible or not regarding which card is shown on the screen
            if index fraction is a whole number we will be rendering 3 images that are shown on screen
             */
            val isInRange = (index >= indexFraction - 1 && indexFraction + 1 >= index)
            val opacity = if (isInRange) 1f else 0f
//            val fraction = when {
//
//            }
            //create a shape which is what draw layer will clip based on
            val shape = when {
                !isInRange -> RectangleShape //if it's out the bounds return a rectangle shape
                index <= indexFraction -> {
                    val fraction = indexFraction - index
                    FractionalRectangleShape(fraction, 1f)
                }

                else -> {
                    val fraction = indexFraction - index + 1
                    FractionalRectangleShape(0f, fraction)
                }

            }


            /** Background image */


            CoilImage(
                data = movie.bgUrl,
                modifier = Modifier
                    .graphicsLayer(
                        alpha = opacity,
                        shape = shape,
                        clip = true
                    )
                    .fillMaxWidth()
                    .aspectRatio(posterAspectRatio)

            )
        }
        /** White gradient in the background */
        Spacer(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxWidth()
                .fillMaxHeight(0.6f) //fills 70% of the screen
                .background(
                    VerticalGradient(
                        0.6f to Color.White,
                        1f to Color.Transparent,
                        startY = screenHeight.value,
                        endY = 0f
                    )
                )
        )

        movies.forEachIndexed { index, movie ->

            /** adjust Y position based on how far integer multiple of screen width it is
             * when poster is screenWidthPx * index then poster will be centered
             * 0 is when the first poster is centered
             * */
            val center = posterSpacingPx * index

            val distFromCenter =
                abs(offset + center) / posterSpacingPx //returns how many screen widths away card is from being centered
            MoviePoster(
                movie = movie,
                modifier = Modifier
                    .offset(getX = { offset + (center) }, getY = { lerp(0f, 50f, distFromCenter) })
                    .width(posterWidthDp)
                    .align(Alignment.BottomCenter)
            )
        }

    }
}

//fun lerp(start: Float, stop: Float, fraction: Float) : Float {
//    return (1 - fraction) * start + fraction * stop
//}

/** Card that represends movie poster*/
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
    getX: () -> Float,
    getY: () -> Float,
    rtlAware: Boolean = false,
): Modifier = this then object : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            if (rtlAware) {
                placeable.placeRelativeWithLayer(getX().roundToInt(), getY().roundToInt())
            } else {
                placeable.placeWithLayer(getX().roundToInt(), getY().roundToInt())
            }
        }
    }
}

fun FractionalRectangleShape(startFracton: Float, endFraction: Float) = object : Shape {
    override fun createOutline(size: Size, density: Density) =
        Outline.Rectangle(
            Rect(
                top = 0f,
                left = startFracton * size.width,
                bottom = size.height,
                right = endFraction * size.width

            )
        )
}

@Composable
fun rememberScrollableController(
    flingConfig: FlingConfig = defaultFlingConfig(),
    interactionState: InteractionState? = null,
    consumeScrollDelta: (Float) -> Float
): ScrollableController {
    val clocks = AmbientAnimationClock.current.asDisposableClock()
    return remember(clocks, flingConfig, interactionState) {
        ScrollableController(consumeScrollDelta, flingConfig, clocks, interactionState)
    }
}






