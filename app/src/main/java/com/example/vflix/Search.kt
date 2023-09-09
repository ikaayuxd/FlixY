package com.example.vflix

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.vflix.ui.theme.sans_bold
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException
import java.net.URLEncoder

data class SearchTitle (
    val id: String,
    val title: String,
    @com.google.gson.annotations.SerializedName("poster_url") val poster: String?,
)

data class TrendingTitle (
    val id: String,
    val title: String,
    val poster: Poster?,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPannel(nav: NavHostController) {
    var searchValue by remember { mutableStateOf("") }
    val moviesState = remember { mutableStateOf<List<SearchTitle>>(emptyList()) }
    val actionState = remember { mutableStateOf<List<SearchTitle>>(emptyList()) }
    val romanceState = remember { mutableStateOf<List<SearchTitle>>(emptyList()) }
    val horrorState = remember { mutableStateOf<List<SearchTitle>>(emptyList()) }
    val familyState = remember { mutableStateOf<List<SearchTitle>>(emptyList()) }
    val showProgress = remember { mutableStateOf(true) }
    val showNoResult = remember { mutableStateOf(false) }
    val displayTrending = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        fetchTopSearchResult(moviesState, showProgress)
    }
    val movies = moviesState.value
    var scrollState = rememberLazyListState()

    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .paint(
                painter =
                painterResource(
                    R.drawable
                        .photo_background_green_textured_wall_rolling_floor_studio_photography_background_illuminated
                ),
                contentScale = ContentScale.Crop,
            )

    ) {
        Row {
            Image(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Mm",
                modifier = Modifier
                    .height(64.dp)
                    .padding(horizontal = 12.dp, vertical = 15.dp)
                    .clickable { nav.navigate("homePage") },
                contentScale = ContentScale.Crop,
                colorFilter =
                ColorFilter.lighting(
                    add = Color(0xFFFFFFFF),
                    multiply = Color.White
                )
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 2.dp,
                )
        ) {
            OutlinedTextField(
                value = searchValue,
                onValueChange = {
                    searchValue = it
                    if (searchValue.length % 2 == 0 && searchValue.length > 3) {
                        displayTrending.value = false
                        showProgress.value = true
                        searchTitle(moviesState, showProgress, searchValue, showNoResult, "all")
                    }
                },
                placeholder = { Text("Search for titles, genres or people") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.White,
                    containerColor = Color.DarkGray,
                    placeholderColor = Color.Gray,
                    focusedBorderColor = Color.Transparent,
                ),
                textStyle = TextStyle(
                    fontSize = 18.sp,
                ),
                leadingIcon = {
                    Image(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Mm",
                        modifier = Modifier
                            .height(64.dp)
                            .padding(horizontal = 12.dp, vertical = 15.dp)
                            .clickable { nav.navigate("homePage") },
                        contentScale = ContentScale.Crop,
                        colorFilter =
                        ColorFilter.lighting(
                            add = Color(0xFFFFFFFF),
                            multiply = Color.White
                        )
                    )
                },
            )
        }
        if (displayTrending.value) {
            Row {
                Text(
                    text = "Top Searches",
                    modifier = Modifier
                        .padding(
                            vertical = 6.dp,
                            horizontal = 12.dp
                        )
                        .fillMaxWidth(),
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = Color.White,
                        fontFamily = sans_bold
                    )
                )
            }
            Row(
                modifier =
                Modifier
                    .clip(RoundedCornerShape(12.dp)),

                ) {
                if (showProgress.value) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .height(50.dp)
                                .width(50.dp),
                            color = Color.Red,
                            strokeWidth = 6.dp
                        )
                    }
                }

                val lazyListState = rememberLazyListState()

                var pageSize = movies.size / 3
                if (pageSize == 0) {
                    pageSize = 1
                }
                LazyColumn(
                    modifier =
                    Modifier
                        .padding(
                            vertical = 6.dp,
                            horizontal = 12.dp
                        )
                        .clip(RoundedCornerShape(12.dp))
                        .fillMaxWidth(),
                    state = lazyListState,
                ) {
                    itemsIndexed(movies) { index, item ->
                        val pageIndex = index / pageSize
                        val pageStartIndex = pageIndex * pageSize
                        val pageEndIndex = (pageIndex + 1) * pageSize - 1

                        if (index in pageStartIndex..pageEndIndex) {
                            if ((item.poster?.length ?: 0) > 0) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model =
                                            ImageRequest.Builder(LocalContext.current)//"https://image.tmdb.org/t/p/w1280"
                                                .data(item.poster)//item.poster?.url ?: TEST_IMAGE_URLS[0])
                                                .crossfade(true)
                                                .build(),
                                            placeholder = painterResource(R.drawable.ic_launcher_background),
                                            contentDescription = "Movie Poster",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .height(90.dp)
                                                .width(160.dp)
                                                .clip(RoundedCornerShape(5.dp)),
                                            error = painterResource(R.drawable.ic_launcher_background)
                                        )
                                        Text(
                                            text = item.title,
                                            modifier = Modifier
                                                .padding(
                                                    vertical = 6.dp,
                                                    horizontal = 12.dp
                                                )
                                                .fillMaxWidth(),
                                            style = TextStyle(
                                                fontSize = 15.sp,
                                                color = Color.White,
                                                fontFamily = sans_bold
                                            )
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }
            }
        } else {
            Column (
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Text(
                        text = "Top Results",
                        modifier = Modifier
                            .padding(
                                vertical = 2.dp,
                                horizontal = 12.dp
                            )
                            .fillMaxWidth(),
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = Color.White,
                            fontFamily = sans_bold
                        )
                    )
                }
                if (showNoResult.value) {
                    Row (
                        modifier = Modifier.padding(top = 12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ){
                        Text(
                            text = "No Results",
                            modifier = Modifier
                                .padding(
                                    vertical = 6.dp,
                                    horizontal = 12.dp
                                )
                                .fillMaxWidth(),
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color.Gray,
                                fontFamily = sans_bold
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Row() {
                    val lazyListState = rememberLazyListState()

                    var pageSize = movies.size / 3
                    if (pageSize == 0) {
                        pageSize = 1
                    }
                    LazyRow(
                        modifier =
                        Modifier
                            .padding(
                                vertical = 2.dp,
                                horizontal = 12.dp
                            )
                            .clip(RoundedCornerShape(12.dp))
                            .fillMaxWidth(),
                        state = lazyListState,
                    ) {
                        itemsIndexed(movies) { index, item ->
                            val pageIndex = index / pageSize
                            val pageStartIndex = pageIndex * pageSize
                            val pageEndIndex = (pageIndex + 1) * pageSize - 1

                            if (index in pageStartIndex..pageEndIndex) {
                                if ((item.poster?.length ?: 0) > 0) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(2.dp)
                                    ) {
                                        val showShimmer = remember { mutableStateOf(true) }
                                        Row(
                                        ) {
                                            AsyncImage(
                                                model =
                                                ImageRequest.Builder(LocalContext.current)//"https://image.tmdb.org/t/p/w1280"
                                                    .data(
                                                        (item.poster?.split(".jpg")?.get(0)
                                                            ?: "") + "QL75_UX380_CR0,0,380,562.jpg"
                                                    )//item.poster?.url ?: TEST_IMAGE_URLS[0]
                                                    .crossfade(true)
                                                    .build(),
                                                contentDescription = "Movie Poster",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .height(158.dp)
                                                    .width(110.dp)
                                                    .clip(
                                                        RoundedCornerShape(
                                                            5.dp
                                                        )
                                                    )
                                                    .background(
                                                        shimmerBrush(
                                                            targetValue = 1300f,
                                                            showShimmer = showShimmer.value
                                                        )
                                                    ),
                                                onSuccess = { showShimmer.value = false },
                                            )
                                        }
                                    }
                                    Spacer(
                                        modifier = Modifier.width(
                                            4.dp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    searchTitle(actionState, showProgress, searchValue, showNoResult, "Action")
                    searchTitle(romanceState, showProgress, searchValue, showNoResult, "Romance")
                    searchTitle(horrorState, showProgress, searchValue, showNoResult, "Horror")
                    searchTitle(familyState, showProgress, searchValue, showNoResult, "Family")
                }

                val actionOnly = actionState.value
                val romanceOnly = romanceState.value
                val horrorOnly = horrorState.value
                val familyOnly = familyState.value

                if (actionOnly.isNotEmpty()) {
                    Row(
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Text(
                            text = "Top Action",
                            modifier = Modifier
                                .padding(
                                    vertical = 2.dp,
                                    horizontal = 12.dp
                                )
                                .fillMaxWidth(),
                            style = TextStyle(
                                fontSize = 18.sp,
                                color = Color.White,
                                fontFamily = sans_bold
                            )
                        )
                    }
                    Row() {
                        val lazyListState = rememberLazyListState()

                        var pageSize = actionOnly.size / 3
                        if (pageSize == 0) {
                            pageSize = 1
                        }
                        LazyRow(
                            modifier =
                            Modifier
                                .padding(
                                    vertical = 2.dp,
                                    horizontal = 12.dp
                                )
                                .clip(RoundedCornerShape(12.dp))
                                .fillMaxWidth(),
                            state = lazyListState,
                        ) {
                            itemsIndexed(actionOnly) { index, item ->
                                val pageIndex = index / pageSize
                                val pageStartIndex = pageIndex * pageSize
                                val pageEndIndex = (pageIndex + 1) * pageSize - 1

                                if (index in pageStartIndex..pageEndIndex) {
                                    if ((item.poster?.length ?: 0) > 0) {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceEvenly,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(2.dp)
                                        ) {
                                            val showShimmer = remember { mutableStateOf(true) }
                                            Row(
                                            ) {
                                                AsyncImage(
                                                    model =
                                                    ImageRequest.Builder(LocalContext.current)//"https://image.tmdb.org/t/p/w1280"
                                                        .data(
                                                            (item.poster?.split(".jpg")?.get(0)
                                                                ?: "") + "QL75_UX380_CR0,0,380,562.jpg"
                                                        )//item.poster?.url ?: TEST_IMAGE_URLS[0]
                                                        .crossfade(true)
                                                        .build(),
                                                    contentDescription = "Movie Poster",
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier
                                                        .height(158.dp)
                                                        .width(110.dp)
                                                        .clip(
                                                            RoundedCornerShape(
                                                                5.dp
                                                            )
                                                        )
                                                        .background(
                                                            shimmerBrush(
                                                                targetValue = 1300f,
                                                                showShimmer = showShimmer.value
                                                            )
                                                        ),
                                                    onSuccess = {
                                                        showShimmer.value = false
                                                        println("Success")
                                                    },
                                                )

                                            }
                                        }
                                        Spacer(
                                            modifier = Modifier.width(
                                                4.dp
                                            )
                                        )
                                    }
                                }

                            }

                        }
                    }
                }

                if (romanceOnly.isNotEmpty()) {
                    Row(
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Text(
                            text = "Top Romance",
                            modifier = Modifier
                                .padding(
                                    vertical = 2.dp,
                                    horizontal = 12.dp
                                )
                                .fillMaxWidth(),
                            style = TextStyle(
                                fontSize = 18.sp,
                                color = Color.White,
                                fontFamily = sans_bold
                            )
                        )
                    }
                    Row() {
                        val lazyListState = rememberLazyListState()
                        var pageSize = romanceOnly.size / 3
                        if (pageSize == 0) {
                            pageSize = 1
                        }
                        LazyRow(
                            modifier =
                            Modifier
                                .padding(
                                    vertical = 2.dp,
                                    horizontal = 12.dp
                                )
                                .clip(RoundedCornerShape(12.dp))
                                .fillMaxWidth(),
                            state = lazyListState,
                        ) {
                            itemsIndexed(romanceOnly) { index, item ->
                                val pageIndex = index / pageSize
                                val pageStartIndex = pageIndex * pageSize
                                val pageEndIndex = (pageIndex + 1) * pageSize - 1

                                if (index in pageStartIndex..pageEndIndex) {
                                    if ((item.poster?.length ?: 0) > 0) {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceEvenly,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(2.dp)
                                        ) {
                                            val showShimmer = remember { mutableStateOf(true) }
                                            Row(
                                            ) {
                                                AsyncImage(
                                                    model =
                                                    ImageRequest.Builder(LocalContext.current)//"https://image.tmdb.org/t/p/w1280"
                                                        .data(
                                                            (item.poster?.split(".jpg")?.get(0)
                                                                ?: "") + "QL75_UX380_CR0,0,380,562.jpg"
                                                        )//item.poster?.url ?: TEST_IMAGE_URLS[0]
                                                        .crossfade(true)
                                                        .build(),
                                                    contentDescription = "Movie Poster",
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier
                                                        .height(158.dp)
                                                        .width(110.dp)
                                                        .clip(
                                                            RoundedCornerShape(
                                                                5.dp
                                                            )
                                                        )
                                                        .background(
                                                            shimmerBrush(
                                                                targetValue = 1300f,
                                                                showShimmer = showShimmer.value
                                                            )
                                                        ),
                                                    onSuccess = {
                                                        showShimmer.value = false
                                                    },
                                                )

                                            }
                                        }
                                        Spacer(
                                            modifier = Modifier.width(
                                                4.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                }
                if (horrorOnly.isNotEmpty()) {
                    Row(
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Text(
                            text = "Top Horror",
                            modifier = Modifier
                                .padding(
                                    vertical = 2.dp,
                                    horizontal = 12.dp
                                )
                                .fillMaxWidth(),
                            style = TextStyle(
                                fontSize = 18.sp,
                                color = Color.White,
                                fontFamily = sans_bold
                            )
                        )
                    }
                    Row() {
                        val lazyListState = rememberLazyListState()
                        var pageSize = horrorOnly.size / 3
                        if (pageSize == 0) {
                            pageSize = 1
                        }
                        LazyRow(
                            modifier =
                            Modifier
                                .padding(
                                    vertical = 2.dp,
                                    horizontal = 12.dp
                                )
                                .clip(RoundedCornerShape(12.dp))
                                .fillMaxWidth(),
                            state = lazyListState,
                        ) {
                            itemsIndexed(horrorOnly) { index, item ->
                                val pageIndex = index / pageSize
                                val pageStartIndex = pageIndex * pageSize
                                val pageEndIndex = (pageIndex + 1) * pageSize - 1

                                if (index in pageStartIndex..pageEndIndex) {
                                    if ((item.poster?.length ?: 0) > 0) {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceEvenly,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(2.dp)
                                        ) {
                                            val showShimmer = remember { mutableStateOf(true) }
                                            Row(
                                            ) {
                                                AsyncImage(
                                                    model =
                                                    ImageRequest.Builder(LocalContext.current)//"https://image.tmdb.org/t/p/w1280"
                                                        .data(
                                                            (item.poster?.split(".jpg")?.get(0)
                                                                ?: "") + "QL75_UX380_CR0,0,380,562.jpg"
                                                        )//item.poster?.url ?: TEST_IMAGE_URLS[0]
                                                        .crossfade(true)
                                                        .build(),
                                                    contentDescription = "Movie Poster",
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier
                                                        .height(158.dp)
                                                        .width(110.dp)
                                                        .clip(
                                                            RoundedCornerShape(
                                                                5.dp
                                                            )
                                                        )
                                                        .background(
                                                            shimmerBrush(
                                                                targetValue = 1300f,
                                                                showShimmer = showShimmer.value
                                                            )
                                                        ),
                                                    onSuccess = {
                                                        showShimmer.value = false
                                                    },
                                                )

                                            }
                                        }
                                        Spacer(
                                            modifier = Modifier.width(
                                                4.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                }
                if (familyOnly.isNotEmpty()) {
                    Row(
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Text(
                            text = "Top Family",
                            modifier = Modifier
                                .padding(
                                    vertical = 2.dp,
                                    horizontal = 12.dp
                                )
                                .fillMaxWidth(),
                            style = TextStyle(
                                fontSize = 18.sp,
                                color = Color.White,
                                fontFamily = sans_bold
                            )
                        )
                    }
                    Row() {
                        val lazyListState = rememberLazyListState()
                        var pageSize = familyOnly.size / 3
                        if (pageSize == 0) {
                            pageSize = 1
                        }
                        LazyRow(
                            modifier =
                            Modifier
                                .padding(
                                    vertical = 2.dp,
                                    horizontal = 12.dp
                                )
                                .clip(RoundedCornerShape(12.dp))
                                .fillMaxWidth(),
                            state = lazyListState,
                        ) {
                            itemsIndexed(familyOnly) { index, item ->
                                val pageIndex = index / pageSize
                                val pageStartIndex = pageIndex * pageSize
                                val pageEndIndex = (pageIndex + 1) * pageSize - 1

                                if (index in pageStartIndex..pageEndIndex) {
                                    if ((item.poster?.length ?: 0) > 0) {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceEvenly,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(2.dp)
                                        ) {
                                            val showShimmer = remember { mutableStateOf(true) }
                                            Row(
                                            ) {
                                                AsyncImage(
                                                    model =
                                                    ImageRequest.Builder(LocalContext.current)//"https://image.tmdb.org/t/p/w1280"
                                                        .data(
                                                            (item.poster?.split(".jpg")?.get(0)
                                                                ?: "") + "QL75_UX380_CR0,0,380,562.jpg"
                                                        )//item.poster?.url ?: TEST_IMAGE_URLS[0]
                                                        .crossfade(true)
                                                        .build(),
                                                    contentDescription = "Movie Poster",
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier
                                                        .height(158.dp)
                                                        .width(110.dp)
                                                        .clip(
                                                            RoundedCornerShape(
                                                                5.dp
                                                            )
                                                        )
                                                        .background(
                                                            shimmerBrush(
                                                                targetValue = 1300f,
                                                                showShimmer = showShimmer.value
                                                            )
                                                        ),
                                                    onSuccess = {
                                                        showShimmer.value = false
                                                    },
                                                )

                                            }
                                        }
                                        Spacer(
                                            modifier = Modifier.width(
                                                4.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                }
            }

        }
    }
}


@Composable
fun shimmerBrush(showShimmer: Boolean = true,targetValue:Float = 1000f): Brush {
    return if (showShimmer) {
        val shimmerColors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.6f),
        )

        val transition = rememberInfiniteTransition(label = "")
        val translateAnimation = transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(800), repeatMode = RepeatMode.Reverse
            ), label = ""
        )
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnimation.value, y = translateAnimation.value)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent,Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }
}

fun fetchTopSearchResult(l: MutableState<List<SearchTitle>>, showProgress: MutableState<Boolean>) {
    // val url = "https://api.themoviedb.org/3/search/tv?api_key=d56e51fb77b081a9cb5192eaaa7823ad&query=Sex"
    //val url = "https://api.themoviedb.org/3/trending/all/day?api_key=d56e51fb77b081a9cb5192eaaa7823ad&page=$page"
    val url = "https://a.ztorr.me/api/imdb?trending=1&limit=30"
    val client = OkHttpClient()
    val request = okhttp3.Request.Builder()
        .url(url)
        .build()

    client
        .newCall(request)
        .enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Error, ${e.message}")
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    val json = response.body?.string()
                    val movies = Gson().fromJson(json, Array<TrendingTitle>::class.java)
                    val moviesObj = movies.map { SearchTitle(it.id, it.title, it.poster?.url) }
                    l.value = moviesObj.toList()
                    showProgress.value = false
                }
            }
        )
}

fun searchTitle(l: MutableState<List<SearchTitle>>, showProgress: MutableState<Boolean>, q: String, noResult: MutableState<Boolean>, type : String) {
    var url = "https://a.ztorr.me/api/imdb?q=${URLEncoder.encode(q, "UTF-8")}"
    var isAddon = false
    if (type != "all") {
        url = "https://a.ztorr.me/api/imdb?genre=$type"
        isAddon = true
    }
    val client = OkHttpClient()
    val request = okhttp3.Request.Builder()
        .url(url)
        .build()

    client
        .newCall(request)
        .enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Error, ${e.message}")
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    val json = response.body?.string()
                    val movies = Gson().fromJson(json, Array<SearchTitle>::class.java)
                    println("Query: $q")
                    if (movies.isNullOrEmpty()) {
                        l.value = emptyList()
                        if (!isAddon) {
                            noResult.value = true
                        }
                    } else {
                        if (isAddon) {
                            val shuffled = movies.toList()
                            l.value = shuffled
                        } else {
                            println("MoviesQ: $movies")
                            l.value = movies.toList()
                        }
                        if (!isAddon) {
                            noResult.value = false
                        }
                    }
                    showProgress.value = false
                }
            }
        )
}