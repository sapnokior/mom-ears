package com.example.momears

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import java.io.OutputStream
import java.net.Socket
import kotlin.concurrent.thread

class AudioStreamer(private val host: String, private val port: Int) {
  private var audioRecord: AudioRecord? = null
  private var isStreaming = false
  private val sampleRate = 44100
  private val channelConfig = AudioFormat.CHANNEL_IN_MONO
  private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
  private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

  @SuppressLint("MissingPermission")
  fun start() {
    if (isStreaming) return
    isStreaming = true

    audioRecord = AudioRecord(
      MediaRecorder.AudioSource.MIC,
      sampleRate,
      channelConfig,
      audioFormat,
      bufferSize
    )

    thread {
      var socket: Socket? = null
      var outputStream: OutputStream? = null
      try {
        socket = Socket(host, port)
        outputStream = socket.getOutputStream()
        audioRecord?.startRecording()

        val buffer = ByteArray(bufferSize)
        while (isStreaming) {
          val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
          if (read > 0) {
            outputStream.write(buffer, 0, read)
            outputStream.flush()
          }
        }
      } catch (e: Exception) {
        Log.e("AudioStreamer", "Error streaming audio", e)
      } finally {
        isStreaming = false
        try {
          audioRecord?.stop()
          audioRecord?.release()
        } catch (e: Exception) {}
        try {
          outputStream?.close()
          socket?.close()
        } catch (e: Exception) {}
      }
    }
  }

  fun stop() {
    isStreaming = false
  }
}
