package ru.mpei.earth.ui

import android.app.AlertDialog
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.magnifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import ru.mpei.earth.EquationSystem
import kotlin.math.abs
import kotlin.math.min


@Composable
fun Chart(viewModel: ChartViewModel) {

    val state = rememberLazyListState()

    var alertDialogState by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }

    val speed by viewModel.speed.collectAsState(initial = EquationSystem.SPEED)
    val angle by viewModel.angle.collectAsState(initial = EquationSystem.ANGLE)
    val height by viewModel.height.collectAsState(initial = EquationSystem.HEIGHT)
    val windSpeed by viewModel.windSpeed.collectAsState(initial = EquationSystem.WIND_SPEED)
    val c by viewModel.c.collectAsState(initial = EquationSystem.C)
    val m by viewModel.m.collectAsState(initial = EquationSystem.M)
    val s by viewModel.s.collectAsState(initial = EquationSystem.S)

    ChartAlertDialog(alertDialogState = alertDialogState, errorText = errorText) {
        alertDialogState = false
    }

    LazyColumn(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        state = state,
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            ListChart(viewModel = viewModel)
        }

        item {
            Parameters(
                speed = speed,
                onSpeedChanged = viewModel::onSpeedChanged,
                angle = angle,
                onAngleChanged = viewModel::onAngleChanged,
                height = height,
                onHeightChanged = viewModel::onHeightChanged,
                windSpeed = windSpeed,
                onWindSpeedChanged = viewModel::onWindSpeedChanged,
                c = c,
                onCChanged = viewModel::onCChanged,
                m = m,
                onMChanged = viewModel::onMChanged,
                s = s,
                onSChanged = viewModel::onSChanged,
                solve = viewModel::solve
            ) { text ->
                errorText = text
                alertDialogState = true
            }
        }
    }
}

@Composable
private fun ChartAlertDialog(
    alertDialogState: Boolean,
    errorText: String,
    closeAlertDialog: () -> Unit
) {
    if (alertDialogState) {
        AlertDialog(
            onDismissRequest = closeAlertDialog,
            title = {
                Text(
                    text = "Ошибка!",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            text = { Text(text = errorText) },
            buttons = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    Button(
                        onClick = closeAlertDialog,
                        modifier = Modifier.padding(all = 16.dp)
                    ) {
                        Text("OK", fontSize = 22.sp)
                    }
                }
            },
            shape = RoundedCornerShape(size = 8.dp)
        )
    }
}

@Composable
fun ListChart(viewModel: ChartViewModel) {

    val scrollState = rememberLazyListState()

    val configuration = LocalConfiguration.current

    val coroutineScope = rememberCoroutineScope()

    val result by viewModel.result.collectAsState()

    LaunchedEffect(true) {
        viewModel.solve()
    }

    LazyRow(
        state = scrollState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {

        if (result.isNotEmpty()) {

            item {

                LineChart(
                    chartName = "Зависимость x от y",
                    points = result.map { it.x to it.y },
                    xSteps = viewModel.steps.stepsX,
                    ySteps = viewModel.steps.stepsY
                )

                SnapHelper(scrollState = scrollState, coroutineScope = coroutineScope)
            }

            item {

                LineChart(
                    chartName = "Зависимость x от скорости",
                    points = result.map { it.x to it.v },
                    xSteps = viewModel.steps.stepsX,
                    ySteps = viewModel.steps.stepsV
                )

                SnapHelper(scrollState = scrollState, coroutineScope = coroutineScope)
            }

            item {

                LineChart(
                    chartName = "Зависимость y от скорости",
                    points = result.map { it.y to it.v },
                    xSteps = viewModel.steps.stepsY,
                    ySteps = viewModel.steps.stepsV
                )

                SnapHelper(scrollState = scrollState, coroutineScope = coroutineScope)
            }

            item {

                LineChart(
                    chartName = "Зависимость скорости от угла",
                    points = result.map { it.v to it.u },
                    xSteps = viewModel.steps.stepsV,
                    ySteps = viewModel.steps.stepsU
                )

                SnapHelper(scrollState = scrollState, coroutineScope = coroutineScope)
            }
        }
    }
}

@Composable
private fun SnapHelper(scrollState: LazyListState, coroutineScope: CoroutineScope) {
    if (!scrollState.isScrollInProgress) {
        if (scrollState.isHalfPastItemLeft())
            coroutineScope.scrollBasic(scrollState, left = true)
        else
            coroutineScope.scrollBasic(scrollState)

        if (scrollState.isHalfPastItemRight())
            coroutineScope.scrollBasic(scrollState)
        else
            coroutineScope.scrollBasic(scrollState, left = true)
    }
}

private fun CoroutineScope.scrollBasic(listState: LazyListState, left: Boolean = false) {
    launch {
        val pos =
            if (left) listState.firstVisibleItemIndex else listState.firstVisibleItemIndex + 1
        listState.animateScrollToItem(pos)
    }
}

private const val HALF = 500

@Composable
private fun LazyListState.isHalfPastItemRight(): Boolean {
    return remember { derivedStateOf { firstVisibleItemScrollOffset } }.value > HALF
}

@Composable
private fun LazyListState.isHalfPastItemLeft(): Boolean {
    return remember { derivedStateOf { firstVisibleItemScrollOffset } }.value <= HALF
}


@Composable
fun Parameters(
    speed: Float,
    onSpeedChanged: (Float) -> Unit,
    angle: Float,
    onAngleChanged: (Float) -> Unit,
    height: Float,
    onHeightChanged: (Float) -> Unit,
    windSpeed: Float,
    onWindSpeedChanged: (Float) -> Unit,
    c: Float,
    onCChanged: (Float) -> Unit,
    m: Float,
    onMChanged: (Float) -> Unit,
    s: Float,
    onSChanged: (Float) -> Unit,
    solve: () -> Unit,
    onError: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Parameter(
            name = "Начальная скорость",
            value = speed,
            minValue = 0f,
            maxValue = 1000f,
            onValueChange = onSpeedChanged,
            onError = onError,
            solve = solve
        )
        Separator()
        Parameter(
            name = "Угол броска (в градусах)",
            value = angle,
            minValue = 0f,
            maxValue = 90f,
            onValueChange = onAngleChanged,
            onError = onError,
            solve = solve
        )
        Separator()
        Parameter(
            name = "Начальная высота",
            value = height,
            minValue = 0f,
            maxValue = 1000f,
            onValueChange = onHeightChanged,
            onError = onError,
            solve = solve
        )
        Separator()
        Parameter(
            name = "Скорость ветра",
            value = windSpeed,
            minValue = 0f,
            maxValue = 30f,
            onValueChange = onWindSpeedChanged,
            onError = onError,
            solve = solve
        )
        Separator()
        Parameter(
            name = "Коэффициент сопротивления",
            value = c,
            minValue = 0f,
            maxValue = 1f,
            onValueChange = onCChanged,
            onError = onError,
            solve = solve
        )
        Separator()
        Parameter(
            name = "Масса тела",
            value = m,
            minValue = 0f,
            maxValue = 1000f,
            onValueChange = onMChanged,
            onError = onError,
            solve = solve
        )
        Separator()
        Parameter(
            name = "Площадь поперечного сечения",
            value = s,
            minValue = 0f,
            maxValue = 100f,
            onValueChange = onSChanged,
            onError = onError,
            solve = solve
        )
    }
}

@Composable
fun Parameter(
    name: String,
    value: Float,
    minValue: Float,
    maxValue: Float,
    onValueChange: (Float) -> Unit,
    onError: (String) -> Unit,
    solve: () -> Unit
) {

    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {

        val (parameterName, parameterSlider, minValueText, maxValueText, valueTextField) = createRefs()

        Text(text = name, modifier = Modifier.constrainAs(parameterName) {
            top.linkTo(anchor = parent.top, margin = 16.dp)
            start.linkTo(anchor = parent.start)
            end.linkTo(anchor = parent.end)
        })

        ParameterValueText(
            value = minValue.toString(),
            modifier = Modifier.constrainAs(minValueText) {
                top.linkTo(anchor = parameterName.bottom, margin = 32.dp)
                start.linkTo(parent.start, margin = 16.dp)
                bottom.linkTo(parent.bottom)
            })

        ParameterValueText(
            value = maxValue.toString(),
            modifier = Modifier.constrainAs(maxValueText) {
                top.linkTo(anchor = parameterName.bottom, margin = 32.dp)
                end.linkTo(parent.end, margin = 16.dp)
                bottom.linkTo(parent.bottom)
            })

        TextFieldValue(
            value = value,
            onValueChange = onValueChange,
            minValue = minValue,
            maxValue = maxValue,
            onError = onError,
            solve = solve,
            modifier = Modifier.constrainAs(valueTextField) {
                top.linkTo(anchor = parameterName.bottom, margin = 32.dp)
                start.linkTo(minValueText.end)
                end.linkTo(maxValueText.start)
                bottom.linkTo(parent.bottom)
            })

        createHorizontalChain(
            minValueText, valueTextField, maxValueText, chainStyle = ChainStyle.SpreadInside
        )

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = minValue..maxValue,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .constrainAs(parameterSlider) {
                    top.linkTo(anchor = parameterName.bottom, margin = 4.dp)
                    start.linkTo(anchor = parent.start)
                    end.linkTo(anchor = parent.end)
                },
            onValueChangeFinished = solve
        )
    }
}

@Composable
fun TextFieldValue(
    value: Float,
    minValue: Float,
    maxValue: Float,
    modifier: Modifier,
    onValueChange: (Float) -> Unit,
    onError: (String) -> Unit,
    solve: () -> Unit
) {

    val stringValue = "%.2f".format(value)

    val (textValue, onTextValueChanged) = remember { mutableStateOf(stringValue) }

    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = value) {
        onTextValueChanged(stringValue)
    }

    TextField(
        value = textValue,
        onValueChange = {
            onTextValueChanged(it)

        },
        singleLine = true,
        textStyle = TextStyle(
            color = Color.Black, fontSize = 12.sp, textAlign = TextAlign.Center
        ),
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.Black,
            backgroundColor = Color.White,
            cursorColor = Color.Black,
            focusedIndicatorColor = Color.White,
            unfocusedIndicatorColor = Color.White
        ),
        modifier = modifier,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            autoCorrect = false,
            capitalization = KeyboardCapitalization.None,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
            try {
                val count = textValue.toFloat()
                when {
                    count < minValue -> {
                        onError("Введённое число меньше допустимого!")
                        onTextValueChanged(stringValue)
                    }
                    count > maxValue -> {
                        onError("Введённое число больше допустимого!")
                        onTextValueChanged(stringValue)
                    }
                    else -> {
                        onValueChange(count)
                        solve()
                    }
                }
                focusManager.clearFocus()
            } catch (e: Exception) {
                onError("Введённое значение не является числом с плавающей точкой!")
                onTextValueChanged(stringValue)
                focusManager.clearFocus()
            }
        })
    )
}

@Composable
private fun Separator() {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(color = Color.LightGray)
            .height(height = 1.dp)
            .fillMaxWidth()
    )
}

@Composable
fun ParameterValueText(value: String, modifier: Modifier) {
    Text(text = value, color = Color.Gray, fontSize = 12.sp, modifier = modifier)
}