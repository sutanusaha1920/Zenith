package sutanu.apps.zenith.presentation.settings.settings_ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import sutanu.apps.zenith.R
import sutanu.apps.zenith.presentation.settings.ChangePinViewModel
import sutanu.apps.zenith.presentation.settings.uistate.ChangePinUiState
import sutanu.apps.zenith.presentation.ui.theme.AlertPrimary
import sutanu.apps.zenith.presentation.ui.theme.BackgroundPrimary
import sutanu.apps.zenith.presentation.ui.theme.ControlDark
import sutanu.apps.zenith.presentation.ui.theme.InfoPrimary
import sutanu.apps.zenith.presentation.ui.theme.InputBg
import sutanu.apps.zenith.presentation.ui.theme.Poppins
import sutanu.apps.zenith.presentation.ui.theme.SurfacePrimary
import sutanu.apps.zenith.presentation.ui.theme.TextPrimary
import sutanu.apps.zenith.presentation.ui.theme.TextSecondary

@Composable
fun ChangePinScreen(
    viewModel: ChangePinViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onNavigateBack()
        }
    }

    ChangePinContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onCurrentPinChange = viewModel::onCurrentPinChange,
        onNewPinChange = viewModel::onNewPinChange,
        onConfirmPinChange = viewModel::onConfirmPinChange,
        toggleCurrentPinVisibility = viewModel::toggleCurrentPinVisibility,
        toggleNewPinVisibility = viewModel::toggleNewPinVisibility,
        toggleConfirmPinVisibility = viewModel::toggleConfirmPinVisibility,
        onUpdatePin = viewModel::onUpdatePinClick
    )
}

@Composable
fun ChangePinContent(
    uiState: ChangePinUiState,
    onNavigateBack: () -> Unit,
    onCurrentPinChange: (String) -> Unit,
    onNewPinChange: (String) -> Unit,
    onConfirmPinChange: (String) -> Unit,
    toggleCurrentPinVisibility: () -> Unit,
    toggleNewPinVisibility: () -> Unit,
    toggleConfirmPinVisibility: () -> Unit,
    onUpdatePin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header (title and back button)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            // Back button
            Row(
                modifier = Modifier.clickable { onNavigateBack() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = InfoPrimary
                )

                Spacer(modifier = Modifier.width(2.dp))

                Text(
                    text = "Back",
                    color = InfoPrimary,
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Title
            Text(
                text = "Change PIN",
                color = TextPrimary,
                fontSize = 24.sp,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfacePrimary),
            shape = RoundedCornerShape(16.dp)
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Current PIN
                PinInputField(
                    label = "Current PIN",
                    value = uiState.currentPin,
                    onValueChange = onCurrentPinChange,
                    isVisible = uiState.isCurrentPinVisible,
                    onToggleVisibilityClick = toggleCurrentPinVisibility
                )

                Spacer(modifier = Modifier.height(12.dp))

                // New PIN
                PinInputField(
                    label = "New PIN",
                    value = uiState.newPin,
                    onValueChange = onNewPinChange,
                    isVisible = uiState.isNewPinVisible,
                    onToggleVisibilityClick = toggleNewPinVisibility
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Confirm PIN
                PinInputField(
                    label = "Confirm New PIN",
                    value = uiState.confirmPin,
                    onValueChange = onConfirmPinChange,
                    isVisible = uiState.isConfirmPinVisible,
                    onToggleVisibilityClick = toggleConfirmPinVisibility
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Error Message(shown in red if validation fails)
                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage,
                        color = AlertPrimary,
                        fontSize = 12.sp,
                        fontFamily = Poppins,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                Button(
                    onClick = onUpdatePin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = InfoPrimary)
                ) {
                    Text(
                        text = "Update PIN",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Poppins
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfacePrimary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Use a PIN you can remember but others can't guess. This PIN protects the app and is required to uninstall it from the device.",
                color = TextSecondary,
                fontSize = 12.sp,
                fontFamily = Poppins,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun PinInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isVisible: Boolean,
    onToggleVisibilityClick: () -> Unit,
) {
    Column {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Enter PIN", color = TextSecondary.copy(alpha = 0.5f)) },
            shape = RoundedCornerShape(16.dp),
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            trailingIcon = {
                IconButton(onClick = onToggleVisibilityClick) {
                    Image(
                        painter = painterResource(
                            if (isVisible) R.drawable.ic_pin_visibility_on
                            else R.drawable.ic_pin_visibility_off
                        ),
                        contentDescription = "Toggle Pin Visibility",
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = InputBg,
                unfocusedContainerColor = InputBg,
                focusedBorderColor = InfoPrimary,
                unfocusedBorderColor = ControlDark,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )
    }
}

@Preview
@Composable
private fun ChangePinScreenPreview() {
    ChangePinContent(
        uiState = ChangePinUiState(),
        onNavigateBack = {},
        onCurrentPinChange = {},
        onNewPinChange = {},
        onConfirmPinChange = {},
        toggleCurrentPinVisibility = {},
        toggleNewPinVisibility = {},
        toggleConfirmPinVisibility = {},
        onUpdatePin = {}
    )
}