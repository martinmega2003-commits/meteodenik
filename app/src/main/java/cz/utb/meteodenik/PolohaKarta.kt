package cz.utb.meteodenik

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

private const val OPRAVNENI_POLOHA = Manifest.permission.ACCESS_COARSE_LOCATION

enum class StavOpravneni { NEZNAMY, ODMITNUTO, ODMITNUTO_NATRVALO, UDELENO }

@Composable
fun PolohaKarta(
    modifier: Modifier = Modifier,
    onPolohaPovolena: () -> Unit = {},
) {
    val context = LocalContext.current
    val activity = LocalActivity.current

    var stav by rememberSaveable {
        mutableStateOf(
            if (maOpravneni(context)) StavOpravneni.UDELENO else StavOpravneni.NEZNAMY
        )
    }
    var zobrazitVysvetleni by rememberSaveable { mutableStateOf(false) }

    // Spouštěč systémového dialogu – vytváří se v těle composable, ne v onClick.
    val spoustec = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { povoleno ->
        // Trvalé zamítnutí poznáme až tady, po odpovědi na žádost.
        stav = when {
            povoleno -> StavOpravneni.UDELENO
            activity != null && ActivityCompat
                .shouldShowRequestPermissionRationale(activity, OPRAVNENI_POLOHA)
                -> StavOpravneni.ODMITNUTO
            else -> StavOpravneni.ODMITNUTO_NATRVALO
        }
        if (povoleno) onPolohaPovolena()
    }

    // Návrat z Nastavení: uživatel tam mohl polohu povolit nebo odebrat.
    val aktualniOnPolohaPovolena by rememberUpdatedState(onPolohaPovolena)
    val lifecycle = (activity as? LifecycleOwner)?.lifecycle
    DisposableEffect(lifecycle) {
        val pozorovatel = LifecycleEventObserver { _, udalost ->
            if (udalost == Lifecycle.Event.ON_RESUME) {
                val povoleno = maOpravneni(context)
                if (povoleno && stav != StavOpravneni.UDELENO) {
                    stav = StavOpravneni.UDELENO
                    aktualniOnPolohaPovolena()
                } else if (!povoleno && stav == StavOpravneni.UDELENO) {
                    stav = StavOpravneni.NEZNAMY
                }
            }
        }
        lifecycle?.addObserver(pozorovatel)
        onDispose { lifecycle?.removeObserver(pozorovatel) }
    }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.poloha_titulek),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))

            when (stav) {
                StavOpravneni.UDELENO -> PolohaSouradnice()

                StavOpravneni.ODMITNUTO_NATRVALO -> {
                    Text(
                        text = stringResource(R.string.poloha_bez_opravneni),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = {
                        context.startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", context.packageName, null)
                            )
                        )
                    }) { Text(stringResource(R.string.otevrit_nastaveni)) }
                }

                else -> if (zobrazitVysvetleni) {
                    Text(
                        text = stringResource(R.string.poloha_vysvetleni),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = {
                        zobrazitVysvetleni = false
                        spoustec.launch(OPRAVNENI_POLOHA)
                    }) { Text(stringResource(R.string.poloha_pokracovat)) }
                } else {
                    if (stav == StavOpravneni.ODMITNUTO) {
                        Text(
                            text = stringResource(R.string.poloha_odmitnuto),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    Button(onClick = {
                        val potrebaVysvetlit = activity != null && ActivityCompat
                            .shouldShowRequestPermissionRationale(activity, OPRAVNENI_POLOHA)
                        if (potrebaVysvetlit) zobrazitVysvetleni = true
                        else spoustec.launch(OPRAVNENI_POLOHA)
                    }) { Text(stringResource(R.string.poloha_zjistit)) }
                }
            }
        }
    }
}

@Composable
private fun PolohaSouradnice() {
    val context = LocalContext.current
    var pokus by remember { mutableIntStateOf(0) }
    val poloha: Location? = remember(pokus) { nactiPosledniPolohu(context) }

    Text(
        text = stringResource(R.string.poloha_povolena),
        style = MaterialTheme.typography.labelLarge
    )
    if (poloha != null) {
        Text(
            text = stringResource(R.string.poloha_souradnice, poloha.latitude, poloha.longitude),
            style = MaterialTheme.typography.bodyLarge
        )
    } else {
        Text(
            text = stringResource(R.string.poloha_neznama),
            style = MaterialTheme.typography.bodyMedium
        )
    }
    Spacer(Modifier.height(8.dp))
    OutlinedButton(onClick = { pokus++ }) {
        Text(stringResource(R.string.poloha_obnovit))
    }
}

private fun maOpravneni(context: Context): Boolean =
    ContextCompat.checkSelfPermission(context, OPRAVNENI_POLOHA) ==
            PackageManager.PERMISSION_GRANTED

// Oprávnění kontrolujeme před KAŽDÝM čtením polohy.
private fun nactiPosledniPolohu(context: Context): Location? {
    if (!maOpravneni(context)) return null
    val locationManager = context.getSystemService(LocationManager::class.java) ?: return null
    return try {
        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
    } catch (e: SecurityException) {
        null
    } catch (e: IllegalArgumentException) {
        null
    }
}