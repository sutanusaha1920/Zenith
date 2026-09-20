package sutanu.apps.zenith.presentation.sos.sos_ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import sutanu.apps.zenith.R
import sutanu.apps.zenith.data.local.db.entity.SosContactEntity
import sutanu.apps.zenith.domain.model.Sos
import sutanu.apps.zenith.presentation.sos.SosViewModel
import sutanu.apps.zenith.presentation.ui.theme.AlertPrimary
import sutanu.apps.zenith.presentation.ui.theme.BackgroundPrimary
import sutanu.apps.zenith.presentation.ui.theme.ControlDark
import sutanu.apps.zenith.presentation.ui.theme.InfoPrimary
import sutanu.apps.zenith.presentation.ui.theme.InputBg
import sutanu.apps.zenith.presentation.ui.theme.OuterCardStrokePrimary
import sutanu.apps.zenith.presentation.ui.theme.Poppins
import sutanu.apps.zenith.presentation.ui.theme.SurfacePrimary
import sutanu.apps.zenith.presentation.ui.theme.SurfaceSecondary
import sutanu.apps.zenith.presentation.ui.theme.TextPrimary
import sutanu.apps.zenith.presentation.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SosScreen(viewModel: SosViewModel) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val smsGranted = permissions[Manifest.permission.SEND_SMS] ?: false
        if (smsGranted) {
            viewModel.triggerEmergencySos(context)
        } else {
            Toast.makeText(
                context,
                "SMS permission is required to send emergency SOS alerts.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    SosScreenContent(
        state = state,
        onAddContactClick = { viewModel.showAddContactModal() },
        onDeleteContactClick = { viewModel.removeContact(it) },
        onAddContactConfirm = { name, phone -> viewModel.addNewContact(name, phone) },
        onDismissModal = { viewModel.dismissAddContactModal() },
        onTriggerSos = {
            val hasSmsPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED

            val hasFineLocation = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (hasSmsPermission && hasFineLocation) {
                viewModel.triggerEmergencySos(context)
            } else {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SosScreenContent(
    state: Sos,
    onAddContactClick: () -> Unit,
    onDeleteContactClick: (SosContactEntity) -> Unit,
    onAddContactConfirm: (String, String) -> Unit,
    onDismissModal: () -> Unit,
    onTriggerSos: () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Text(
                        text = "Location & SOS",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins
                    )

                    Text(
                        text = "Real-time location and distress alerts",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontFamily = Poppins,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Grid Map
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfacePrimary)
                        .border(
                            1.dp,
                            OuterCardStrokePrimary,
                            RoundedCornerShape(20.dp)
                        )
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        repeat(4) {
                            Spacer(modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .border(0.5.dp, ControlDark))
                        }
                    }

                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(InputBg)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(InfoPrimary)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = "Live",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Live Icon
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(InfoPrimary.copy(alpha = 0.2f))
                            .align(Alignment.Center),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = InfoPrimary, modifier = Modifier.size(24.dp))
                    }

                    // Location text and accuracy
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                            .background(SurfacePrimary.copy(alpha = 0.85f))
                            .padding(12.dp)
                    ) {
                        val locationText = if (state.lastKnownLocation.isEmpty()) "Searching for location..." else state.lastKnownLocation
                        Text(
                            text = locationText,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = state.gpsAccuracy,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            // SOS Button
            item {
                Button(
                    onClick = { onTriggerSos() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AlertPrimary),
                    shape = RoundedCornerShape(16.dp)
                ) {

                    Image(
                        painter = painterResource(R.drawable.ic_alert_triangle),
                        contentDescription = "SOS Icon",
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "SOS — Send Distress Alert",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins
                    )
                }

                Text(
                    text = "Works offline via SMS · Press power button 3× to trigger",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = Poppins,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .alpha(0.5f)
                )
            }

            // Contact List Header and Items Grouped
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(SurfacePrimary)
                        .border(
                            1.dp,
                            OuterCardStrokePrimary,
                            RoundedCornerShape(24.dp)
                        )
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.trusted_contacts),
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Poppins
                        )

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .clickable(
                                    onClickLabel = stringResource(R.string.add_trusted_contact)
                                ) { onAddContactClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.add_trusted_contact),
                                tint = InfoPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    if (state.trustedContacts.isEmpty()) {
                        Text(
                            text = "No trusted contacts added yet",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            fontFamily = Poppins,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        )
                    } else {
                        state.trustedContacts.forEach { contact ->
                            ContactItemRow(
                                contact = contact,
                                onDeleteClick = { onDeleteContactClick(contact) }
                            )

                            val index = state.trustedContacts.indexOf(contact)
                            if (index < state.trustedContacts.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp),
                                    thickness = 0.20.dp,
                                    color = TextSecondary.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Add Contact bottom sheet input layout modal
        if (state.showAddContactDialog) {
            ModalBottomSheet(
                onDismissRequest = { onDismissModal() },
                sheetState = sheetState,
                containerColor = SurfacePrimary
            ) {
                AddContactBottomSheetContent(
                    onAddClick = { name, phone -> onAddContactConfirm(name, phone) },
                    onCancelClick = { onDismissModal() }
                )
            }
        }
    }
}

@Composable
fun ContactItemRow(contact: SosContactEntity, onDeleteClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfacePrimary)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(InputBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.contactName.take(1).uppercase(),
                    color = InfoPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column() {
                Text(
                    text = contact.contactName,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins
                )

                Text(
                    text = contact.phoneNumber,
                    color = TextSecondary,
                    fontSize = 16.sp,
                    fontFamily = Poppins,
                )
            }
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable(
                    onClickLabel = stringResource(R.string.delete)
                ) { onDeleteClick() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_delete),
                contentDescription = stringResource(R.string.delete),
                alpha = 0.7f,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun AddContactBottomSheetContent(
    onAddClick: (String, String) -> Unit,
    onCancelClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Add Trusted Contact",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Contact Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextSecondary,
                focusedBorderColor = InfoPrimary,
                unfocusedBorderColor = OuterCardStrokePrimary,
                focusedContainerColor = SurfaceSecondary
            )
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextSecondary,
                focusedBorderColor = InfoPrimary,
                unfocusedBorderColor = OuterCardStrokePrimary,
                focusedContainerColor = SurfaceSecondary
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onCancelClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ControlDark),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Cancel",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins
                )
            }

            Button(
                onClick = { onAddClick(name, phone) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = InfoPrimary),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Add",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins
                )
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_7", showSystemUi = true)
@Composable
fun SosScreenPreview() {
    SosScreenContent(
        state = Sos(
            lastKnownLocation = "123, Salt Lake City, Sector V, Kolkata",
            gpsAccuracy = "Within 12 meters",
            trustedContacts = listOf(
                SosContactEntity(id = 1, contactName = "Mom", phoneNumber = "+91 9876543210"),
                SosContactEntity(id = 2, contactName = "Dad", phoneNumber = "+91 9876543211")
            )
        ),
        onAddContactClick = {},
        onDeleteContactClick = {},
        onAddContactConfirm = { _, _ -> },
        onDismissModal = {},
        onTriggerSos = {}
    )
}

@Preview(showBackground = true)
@Composable
fun AddContactBottomSheetPreview() {
    Box(modifier = Modifier.background(SurfacePrimary)) {
        AddContactBottomSheetContent(
            onAddClick = { _, _ -> },
            onCancelClick = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberModalSheetState() = rememberModalBottomSheetState()
