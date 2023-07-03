package com.example.dangerousmapv10



import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PermissionDialog(
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    onGoToAppSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(onDismissRequest = onDismiss,
        confirmButton = {
                        if (isPermanentlyDeclined){
                            Text(
                                text = "Grant Permission",
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onGoToAppSettingsClick() }
                                    .padding(16.dp))
                        }
        },
        dismissButton = {
            if (!isPermanentlyDeclined){
                Text(
                    text = "OK",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOkClick() }
                        .padding(16.dp))
            }

        }
        , title ={
                 Text(text = "Permission required")

        },
        text={
             Text(text = permissionTextProvider.getDescription(isPermanentlyDeclined))

        },
        modifier=modifier)
}
interface PermissionTextProvider{
        fun getDescription(isPermanentlyDeclined: Boolean): String
}
class CameraPermissionTextProvider:PermissionTextProvider{
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined){
            "You permanently declined camera permission you can go to app settings to grant it"
        }else{
            "This app require camera permission to get a photo of any danger point request you submit"
        }
    }
}
class LocationPermissionTextProvider:PermissionTextProvider{
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined){
            "You permanently declined Location permission please go to app settings to grant it"
        }else{
            "This app require location permission to work appropriately "
        }
    }
}