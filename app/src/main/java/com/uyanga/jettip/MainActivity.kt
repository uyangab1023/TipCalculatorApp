package com.uyanga.jettip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.uyanga.jettip.ui.theme.JetTipTheme
import kotlin.math.roundToInt


@OptIn(ExperimentalComposeUiApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetTipTheme {
                MyApp {
                    TipCalculator()
                }
            }
        }
    }
}

@Composable
fun MyApp( content: @Composable () -> Unit){
    JetTipTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            content()
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun TipCalculator() {
    Surface(modifier = Modifier.padding(12.dp)) {
        Column {
            MainContent()
        }
    }
}


@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 0.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(12.dp)
            .clip(shape = CircleShape.copy(all = CornerSize(12.dp))),
        color = Color(0xFFe9d7f7)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.bodySmall
            )
            val total =  "%.2f".format(totalPerPerson)
            Text(
                text = "$$total",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(onValChange: (Double, Int, Int) -> Unit) {
    val totalBillState = rememberSaveable { mutableStateOf("") }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val splitByState = remember { mutableStateOf(1) }
    val sliderPositionState = remember { mutableStateOf(0f) }
    val tipPercentage = (sliderPositionState.value * 100).roundToInt()
    val tipAmountState = remember { mutableStateOf(0.0) }

    Surface(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth(),
        shape = CircleShape.copy(all = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            InputField(
                valueState = totalBillState,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    keyboardController?.hide()
                    onValChange(
                        totalBillState.value.toDoubleOrNull() ?: 0.0,
                        tipAmountState.value.roundToInt(),
                        splitByState.value
                    )
                }
            )

            if (validState) {
                Row(
                    modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Split",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(
                        modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        CustomButton(signLabel = "-") {
                            splitByState.value =
                                if (splitByState.value > 1) splitByState.value - 1 else 1
                            onValChange(
                                totalBillState.value.toDouble(),
                                tipAmountState.value.roundToInt(),
                                splitByState.value
                            )
                        }
                        Text(
                            text = "${splitByState.value}",
                            modifier = Modifier
                                .align(alignment = Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp)
                        )
                        CustomButton(signLabel = "+") {
                            splitByState.value += 1
                            onValChange(
                                totalBillState.value.toDouble(),
                                tipAmountState.value.roundToInt(),
                                splitByState.value
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = 3.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Tip",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(200.dp))
                    Text(
                        text = "$${"%.2f".format(tipAmountState.value)}",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "$tipPercentage%")
                    Spacer(modifier = Modifier.height(14.dp))
                    Slider(
                        value = sliderPositionState.value,
                        onValueChange = { newVal ->
                            sliderPositionState.value = newVal
                            tipAmountState.value = calculateTotalTip(
                                totalBill = totalBillState.value.toDouble(),
                                tipPercent = (newVal * totalBillState.value.toDouble()).roundToInt(),
                            )
                            onValChange(
                                totalBillState.value.toDouble(),
                                tipAmountState.value.roundToInt(),
                                splitByState.value
                            )
                        },
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        steps = 5
                    )
                }
            }
        }
    }
}

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    signLabel: String = "+",
    onClickButton: () -> Unit = {},
) {
    Button(
        onClick = onClickButton,
                modifier = modifier
                .width(56.dp)
                .height(48.dp)
    ) {
        Text(
            text = signLabel,
            style = TextStyle(fontWeight = FontWeight.ExtraBold),
            color = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CustomButton_Preview() {
    CustomButton(signLabel = "-")
}

@Composable
fun InputField(
    modifier: Modifier = Modifier,
    valueState: MutableState<String>,
    labelId: String,
    enabled: Boolean,
    isSingleLine: Boolean,
    keyboardType: KeyboardType = KeyboardType.Number,
    imeAction: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = valueState.value,
        onValueChange = { valueState.value = it },
        label = { Text(text = labelId) },
        leadingIcon = { Text("$") },
        modifier = modifier
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth(),
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = onAction,
        singleLine = isSingleLine
    )
}

fun calculateTotalTip(totalBill: Double, tipPercent: Int): Double {
    return if (totalBill > 1 && totalBill.toString().isNotEmpty())
        (totalBill * tipPercent) / 100
    else
        0.0
}

fun calculateTotalPerPerson(totalBill: Double, splitBy: Int, tipPercent: Int): Double {
    val bill= calculateTotalTip(totalBill, tipPercent) + totalBill
    return (bill/splitBy)

}

@ExperimentalComposeUiApi
@Preview
@Composable
fun MainContent() {
    val totalPerPerson = remember { mutableStateOf(0.0) }

    Column {
        TopHeader(totalPerPerson = totalPerPerson.value)
        BillForm { billAmt, tipPercent, splitBy ->
            totalPerPerson.value = calculateTotalPerPerson(billAmt, splitBy, tipPercent)
        }
    }
}


//@Composable
//fun OutlinedTextField(modifier: Modifier = Modifier, label: String ="lala", onValChange: (String) -> Unit = {}) {
//    var text by remember { mutableStateOf("") }
//
//
//    OutlinedTextField(
//        value = text,
//        onValueChange = { onValChange(it); text = it},
//        label = { Text(label) },
//        leadingIcon = { Icon(imageVector = Icons.Default.Money , contentDescription = "lala")},
//        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//        keyboardActions = KeyboardActions(onDone = {
//            this.defaultKeyboardAction(imeAction = ImeAction.Done)}),
//
//
//        )
//}

@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApp {
        TipCalculator()
    }
}