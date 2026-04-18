package com.pluto.momears.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import com.pluto.momears.data.DefaultDataRepository
import com.pluto.momears.theme.MyApplicationTheme
import com.pluto.momears.AudioStreamer

@Composable
fun MainScreen(
  onItemClick: (NavKey) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: MainScreenViewModel = viewModel { MainScreenViewModel(DefaultDataRepository()) },
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  when (state) {
    MainScreenUiState.Loading -> {
      // Blank
    }
    is MainScreenUiState.Success -> {
      MainScreen(data = (state as MainScreenUiState.Success).data, modifier = modifier)
    }
    is MainScreenUiState.Error -> {
      Text("Error loading data: ${(state as MainScreenUiState.Error).throwable.message}")
    }
  }
}

@Composable
internal fun MainScreen(data: List<String>, modifier: Modifier = Modifier) {
  var isRecording by remember { mutableStateOf(false) }
  val streamer = remember { AudioStreamer("10.0.2.2", 5000) }

  DisposableEffect(Unit) {
    onDispose {
      streamer.stop()
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    Column(modifier) { data.forEach { Greeting(it) } }

    Button(
      onClick = {
        if (isRecording) {
          streamer.stop()
        } else {
          streamer.start()
        }
        isRecording = !isRecording
      },
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 32.dp)
    ) {
      Text(if (isRecording) "Stop Streaming" else "Record and Stream")
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
  MyApplicationTheme { MainScreen(listOf("Android")) }
}

@Preview(showBackground = true, widthDp = 340)
@Composable
fun MainScreenPortraitPreview() {
  MyApplicationTheme { MainScreen(listOf("Android")) }
}
