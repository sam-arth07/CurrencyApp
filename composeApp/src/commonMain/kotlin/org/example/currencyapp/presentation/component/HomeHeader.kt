package org.example.currencyapp.presentation.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.compose.headerColor
import com.example.compose.staleColor
import currencyapp.composeapp.generated.resources.Res
import currencyapp.composeapp.generated.resources.exchange_illustration
import currencyapp.composeapp.generated.resources.refresh_ic
import currencyapp.composeapp.generated.resources.switch_ic
import org.example.currencyapp.domain.model.Currency
import org.example.currencyapp.domain.model.CurrencyCode
import org.example.currencyapp.domain.model.CurrencyType
import org.example.currencyapp.domain.model.DisplayResult
import org.example.currencyapp.domain.model.RateStatus
import org.example.currencyapp.domain.model.RequestState
import org.example.currencyapp.util.displayCurrentDateTime
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeHeader(
    status: RateStatus,
    source: RequestState<Currency>,
    target: RequestState<Currency>,
    amount: Double,
    onAmountChanged: (Double) -> Unit,
    onSwitchClicked: () -> Unit,
    onRatesRefresh: () -> Unit,
    onCurrencyTypeSelect: (CurrencyType) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
            .background(headerColor)
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        RateStatus(
            status = status, onRatesRefresh = onRatesRefresh
        )
        Spacer(modifier = Modifier.height(24.dp))
        CurrencyInputs(
            source = source,
            target = target,
            onSwitchClicked = onSwitchClicked,
            onCurrencyTypeSelect = onCurrencyTypeSelect
        )
        Spacer(modifier = Modifier.height(24.dp))
        AmountInput(
            amount = amount,
            onAmountChanged = onAmountChanged
        )
    }
}

@Composable
fun RateStatus(status: RateStatus, onRatesRefresh: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Image(
                modifier = Modifier.size(50.dp),
                painter = painterResource(Res.drawable.exchange_illustration),
                contentDescription = "Exchange Rate Illustration"
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = displayCurrentDateTime(), color = Color.White)
                Text(
                    text = status.title,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    color = status.color
                )
            }
        }
        if (status == RateStatus.Stale) {
            IconButton(onClick = onRatesRefresh) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(Res.drawable.refresh_ic),
                    contentDescription = "Refresh icon",
                    tint = staleColor
                )
            }
        }
    }
}

@Composable
fun CurrencyInputs(
    source: RequestState<Currency>,
    target: RequestState<Currency>,
    onSwitchClicked: () -> Unit,
    onCurrencyTypeSelect: (CurrencyType) -> Unit
) {
    var animationStarted by remember { mutableStateOf(false) }
    val animatedRotation by animateFloatAsState(
        targetValue = if (animationStarted) 180f else 0f,
        animationSpec = tween(durationMillis = 500)
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CurrencyView(
            placeholder = "From",
            currency = source,
            onClick = {
                if (source.isSuccess()) {
                    println("HomeView: $source")
                    onCurrencyTypeSelect(
                        CurrencyType.Source(
                            currencyCode = CurrencyCode.valueOf(source.getSuccessData().code)
                        )
                    )
                    println("HomeView: ${source.getSuccessData().code}")
                }
            },
        )
        Spacer(modifier = Modifier.height(14.dp))
        IconButton(
            modifier = Modifier.padding(top = 24.dp)
                .graphicsLayer {
                    rotationY = animatedRotation
                },
            onClick = {
                animationStarted = !animationStarted
                onSwitchClicked()
            }
        ) {
            Icon(
                painter = painterResource(Res.drawable.switch_ic),
                contentDescription = "Switch Button",
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        CurrencyView(
            placeholder = "To",
            currency = target,
            onClick = {
                if (target.isSuccess()) {
                    onCurrencyTypeSelect(
                        CurrencyType.Target(
                            currencyCode = CurrencyCode.valueOf(target.getSuccessData().code)
                        )
                    )
                }
            },
        )
    }
}

@Composable
fun RowScope.CurrencyView(
    placeholder: String,
    currency: RequestState<Currency>,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.weight(1f)) {
        Text(
            text = placeholder,
            modifier = Modifier.padding(start = 12.dp),
            fontSize = MaterialTheme.typography.bodySmall.fontSize,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(size = 8.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .height(54.dp)
                .clickable { onClick() }
        ) {
            currency.DisplayResult(
                onSuccess = { data ->
                    Icon(
                        painter = painterResource(
                            CurrencyCode.valueOf(
                                data.code
                            ).flag
                        ),
                        tint = Color.Unspecified,
                        modifier = Modifier.size(24.dp),
                        contentDescription = "Country Flag"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = CurrencyCode.valueOf(data.code).name,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize
                    )
                }
            )
        }
    }
}

@Composable
fun AmountInput(
    amount: Double,
    onAmountChanged: (Double) -> Unit
) {
    var textState by remember {
        mutableStateOf(
            if (amount == 0.0) "" else amount.toString()
        )
    }
    TextField(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(size = 8.dp))
            .animateContentSize()
            .height(64.dp),
        value = textState,
        onValueChange = { newText ->
            val filteredText = if (newText.count { it == '.' } > 1) {
                if (newText.endsWith('.') && textState.contains('.')) {
                    textState
                } else {
                    newText.filterIndexed { index, char ->
                        char.isDigit() || (char == '.' && newText.indexOf('.') == index)
                    }
                }
            } else {
                newText
            }
            if (filteredText.isEmpty() || filteredText == "." || filteredText.toDoubleOrNull() != null ||
                (filteredText.endsWith('.') && filteredText.dropLast(1).toDoubleOrNull() != null)
            ) {
                textState = filteredText
                val newAmount = filteredText.toDoubleOrNull()
                if (newAmount != null) {
                    onAmountChanged(newAmount)
                } else if (filteredText.isEmpty()) {
                    onAmountChanged(0.0)
                }
            }
        },
        interactionSource = remember { MutableInteractionSource() },
        placeholder = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Enter Amount",
                color = Color.White.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                fontSize = MaterialTheme.typography.titleLarge.fontSize
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White.copy(alpha = 0.05f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
            disabledContainerColor = Color.White.copy(alpha = 0.05f),
            errorContainerColor = Color.White.copy(alpha = 0.05f),
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color.White,
        ),
        textStyle = TextStyle(
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            textAlign = TextAlign.Center
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal
        )
    )
}