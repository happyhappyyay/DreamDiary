package com.happyhappyyay.android.dreamdiary.speech

import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log

class JournalAudioListener(val stoppedCallback: (isError: Boolean?, text: String?)->Unit) : RecognitionListener {
    override fun onReadyForSpeech(p0: Bundle?) {
    }

    override fun onRmsChanged(p0: Float) {
    }

    override fun onBufferReceived(p0: ByteArray?) {
    }

    override fun onPartialResults(partials: Bundle?) {
    }

    override fun onEvent(p0: Int, p1: Bundle?) {
    }

    override fun onBeginningOfSpeech() {
    }

    override fun onEndOfSpeech() {
    }

    override fun onError(error: Int) {
        Log.d("JournalAudioListener","error called")
       val message = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No match"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
            SpeechRecognizer.ERROR_SERVER -> "error from server"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Didn't understand, please try again."
        }
            stoppedCallback(true,message)
    }

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        matches?.apply {
            Log.d("JournalAudioListener","results")
            stoppedCallback(null,matches[0])
        }
    }
}