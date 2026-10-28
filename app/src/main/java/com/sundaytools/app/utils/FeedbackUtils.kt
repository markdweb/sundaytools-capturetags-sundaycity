package com.sundaytools.app.utils

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object FeedbackUtils {

    /** Short vibration confirming a tag was captured. */
    fun vibrate(context: Context) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(60, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(60)
        }
    }

    /** Short confirmation tone (no audio files required, keeps the app lightweight). */
    fun playScanSound() {
        val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 120)
        toneGenerator.release()
    }
}
