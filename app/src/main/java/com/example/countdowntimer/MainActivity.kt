package com.example.countdowntimer

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.countdowntimer.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var counter = 0
    private var isCounting = false
    private var timeRemaining = 0L
    private lateinit var countDownTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.changeThemeButton.setOnClickListener {
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }

        binding.timeSelectBar.addOnChangeListener { _, value, _ ->
            counter = value.toInt()
            binding.TimerCounter.text = counter.toString()
            binding.progressBarCircular.progress = counter
        }

        binding.startStopButton.setOnClickListener {
            if (!isCounting) {
                startCountdown()
            } else {
                stopCountdown()
            }
        }

        // Restore the state if available
        savedInstanceState?.let { restoreState(it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_COUNTER, counter)
        outState.putBoolean(KEY_IS_COUNTING, isCounting)
        outState.putLong(KEY_TIME_REMAINING, timeRemaining)
        outState.putInt(PROGRESS_CIRCULAR, binding.progressBarCircular.progress)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        restoreState(savedInstanceState)
    }

    private fun restoreState(savedInstanceState: Bundle) {
        counter = savedInstanceState.getInt(KEY_COUNTER, 0)
        isCounting = savedInstanceState.getBoolean(KEY_IS_COUNTING, false)
        timeRemaining = savedInstanceState.getLong(KEY_TIME_REMAINING, 0)
        binding.progressBarCircular.progress = savedInstanceState.getInt(PROGRESS_CIRCULAR, binding.progressBarCircular.progress)

        if (isCounting) {
            startCountdown()
        } else {
            updateUI()
        }
    }

    private fun startCountdown() {
        countDownTimer = object : CountDownTimer(
            (counter * 1000L) - timeRemaining,
            1000
        ) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                val secondsRemaining = (millisUntilFinished / 1000).toInt()
                binding.progressBarCircular.progress = secondsRemaining
                binding.TimerCounter.text = secondsRemaining.toString()
            }

            override fun onFinish() {
                isCounting = false
                updateUI()
                showToast("Отсчет закончен!")
            }
        }

        countDownTimer.start()
        isCounting = true
        updateUI()
        showToast("Отсчет пошел!")
    }

    private fun stopCountdown() {
        countDownTimer.cancel()
        isCounting = false
        timeRemaining = 0
        updateUI()
        showToast("Таймер остановлен!")
    }

    private fun updateUI() {
        binding.startStopButton.text =
            if (isCounting) "Стоп" else "Старт"
        binding.timeSelectBar.isEnabled = !isCounting
        binding.progressBarCircular.progress = counter
        binding.TimerCounter.text = counter.toString()
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val KEY_COUNTER = "counter"
        private const val KEY_IS_COUNTING = "is_counting"
        private const val KEY_TIME_REMAINING = "time_remaining"
        private const val PROGRESS_CIRCULAR = "progress_circular"
    }
}
