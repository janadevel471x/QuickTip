package com.example.quicktip

import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quicktip.components.InputField
import com.example.quicktip.ui.theme.QuickTipTheme
import com.example.quicktip.util.calculateTotalPerPerson
import com.example.quicktip.util.calculateTotalTip
import com.example.quicktip.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApp {

            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {

    QuickTipTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            BillFrom()
        }
    }
}

@Preview
@Composable
fun TopHeader(totalPerson: Double = 134.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(all = 25.dp)
            .clip(shape = CircleShape.copy(all = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7)
//            .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp)))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerson)
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "\u20B9 $total",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )

        }
    }

}


@Preview
@Composable
fun MainContent() {
    BillFrom() { billAmt ->

        Log.d("Values", "MainContent: $billAmt")
    }
}

@Composable
fun BillFrom(
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = {}
) {


    val totalBillState = remember {
        mutableStateOf("")
    }

    val validate = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }

    val personCount = remember {
        mutableStateOf(1)
    }

    val sliderPositionState = remember {
        mutableStateOf(0f)
    }

    val tipAmountState = remember {
        mutableStateOf(0.0)
    }

    val totalPersonState = remember {
        mutableStateOf(0.0)
    }

    val tipPercentage = (sliderPositionState.value*100).toInt()

    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = Modifier.fillMaxSize()) {
        TopHeader(totalPerson = totalPersonState.value)
        Surface(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(corner = CornerSize(8.dp)),
            border = BorderStroke(width = 1.dp, color = Color.LightGray)
        ) {
            Column(
                modifier = Modifier.padding(6.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                InputField(
                    valuesState = totalBillState,
                    labelId = "Enter Bill",
                    enable = true,
                    isSingleLine = true,
                    onAction = KeyboardActions {
                        if (!validate) return@KeyboardActions
                        onValueChange(totalBillState.value.trim())

                        keyboardController?.hide()
                    }
                )
                if (validate) {
                    Row(
                        modifier = Modifier.padding(3.dp),
                        horizontalArrangement = Arrangement.Start
                    )
                    {
                        Text(
                            text = "Split",
                            modifier = Modifier.align(alignment = Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(120.dp))
                        Spacer(modifier = Modifier.height(60.dp))

                        Row(
                            modifier = Modifier.padding(horizontal = 3.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RoundIconButton(
                                modifier = Modifier,
                                imageVector = Icons.Default.Remove,
                                onclick = {
                                    if (personCount.value > 1) {
                                        personCount.value -= 1
                                    }
                                   totalPersonState.value =  calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(),
                                       splitBy = personCount.value,
                                       tipPercentage = tipPercentage)
                                })

                            Text(
                                text = "${personCount.value}",
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 9.dp, end = 9.dp)
                            )

                            RoundIconButton(
                                modifier = Modifier,
                                imageVector = Icons.Default.Add,
                                onclick = {
                                    personCount.value += 1

                                    totalPersonState.value =  calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(),
                                        splitBy = personCount.value,
                                        tipPercentage = tipPercentage)
                                })
                        }
                    }

                    Row(
                        modifier = Modifier
                            .padding(3.dp)
                            .align(Alignment.Start)
                    ) {

                        Text(
                            text = "Tip",
                            modifier = Modifier.align(alignment = Alignment.CenterVertically)
                        )

                        Spacer(modifier = Modifier.width(200.dp))

                        Text(text = "\u20B9 ${tipAmountState.value}")
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(text = "$tipPercentage %")

                        Spacer(modifier = Modifier.height(14.dp))

                        //slider
                        Slider(value = sliderPositionState.value,
                            onValueChange = { newVal ->
                                sliderPositionState.value = newVal

                            },
                            modifier = Modifier.padding(start = 16.dp,
                                end = 16.dp),
                            steps = 5,
                            onValueChangeFinished = {
                            tipAmountState.value = calculateTotalTip(totalBill = totalBillState.value.toDouble(), tipPercentage)

                                totalPersonState.value =
                                    calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(),
                                        splitBy = personCount.value,
                                        tipPercentage = tipPercentage)
                            }
                        )
                    }

                } else {

                }
            }
        }
    }
}


@Preview(showBackground = true, name = "screen", showSystemUi = true, device = "id:pixel_8_pro")
@Composable
fun GreetingPreview() {
    QuickTipTheme {
        MyApp {
            Text("Hi there how are you ?")
        }
    }
}