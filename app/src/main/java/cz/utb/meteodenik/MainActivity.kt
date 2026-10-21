package cz.utb.meteodenik

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.utb.meteodenik.ui.theme.MeteoDenikTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MeteoDenikTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = stringResource(R.string.autor),
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = stringResource(R.string.nadpis, name),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineSmall
        )
        Button(onClick = { /* funkci dostane v dalších lekcích */ }) {
            Text(text = stringResource(R.string.tlacitko_obnovit))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MeteoDenikTheme {
        Greeting(name = stringResource(R.string.autor))
    }
}