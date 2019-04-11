package com.github.makiftutuncu.astroturf

import android.content.Context
import android.os.*
import android.support.wearable.activity.WearableActivity
import android.util.Log
import android.util.TypedValue
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : WearableActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setAmbientEnabled()
        initialize()
    }

    private val tag: String = "Astroturf"

    private val matchDuration: Int            = 60 * 60 * 1000 // 60 minutes
    private val goalKeeperChangeDuration: Int = 10 * 60 * 1000 // 10 minutes
    private val goalKeeperChngeOffset: Int    = 2  * 1000      // 2 seconds

    private val tick: Long = 1000L // 1 second

    private val longVibrationDuration: Long  = 750L // 750 milliseconds
    private val shortVibrationDuration: Long = 250L // 250 milliseconds
    private val vibrationAmplitude: Int      = 127  // 1: lowest, 255: highest

    private var scoreA: Int = 0
    private var scoreB: Int = 0

    private var isStarted: Boolean = false

    private val timer: CountDownTimer =
        object: CountDownTimer(matchDuration.toLong(), tick) {
            override fun onFinish() {
                isStarted = false
                updateStatus(getMatchResult(), R.dimen.statusSmallTextSize)
                vibrate(longVibrationDuration)
                btnStartStop.text = getString(R.string.rematch)
            }

            override fun onTick(millisUntilFinished: Long) {
                Log.d(tag, "Tick! Remaining: $millisUntilFinished ms")

                val remainder = millisUntilFinished % goalKeeperChangeDuration

                if (remainder < goalKeeperChngeOffset && millisUntilFinished > goalKeeperChangeDuration) {
                    updateStatus(getString(R.string.changeGoalKeeper), R.dimen.statusSmallTextSize)
                    vibrate(longVibrationDuration)
                } else {
                    updateStatus(getRemainingTime(millisUntilFinished), R.dimen.statusTextSize)
                }
            }
        }

    private val vibrator: Vibrator? by lazy {
        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    private fun initialize() {
        updateStatus(getRemainingTime(matchDuration.toLong()), R.dimen.statusTextSize)

        btnStartStop.setOnClickListener {
            toggleStartStop()
        }

        btnIncrementA.setOnClickListener {
            if (isStarted) {
                scoreA += 1
                updateScore(tvScoreA, scoreA)
            }
        }

        btnDecrementA.setOnClickListener {
            if (isStarted) {
                if (scoreA > 0) {
                    scoreA -= 1
                }
                updateScore(tvScoreA, scoreA)
            }
        }

        btnIncrementB.setOnClickListener {
            if (isStarted) {
                scoreB += 1
                updateScore(tvScoreB, scoreB)
            }
        }

        btnDecrementB.setOnClickListener {
            if (isStarted) {
                if (scoreB > 0) {
                    scoreB -= 1
                }
                updateScore(tvScoreB, scoreB)
            }
        }
    }

    private fun start() {
        Log.d(tag, "Kick off!")
        scoreA = 0
        scoreB = 0
        updateScore(tvScoreA, scoreA)
        updateScore(tvScoreB, scoreB)
        vibrate(longVibrationDuration)
        btnStartStop.text = getString(R.string.stop)
        timer.start()
    }

    private fun stop() {
        Log.d(tag, "Stop!")
        timer.cancel()
        updateStatus(getMatchResult(), R.dimen.statusSmallTextSize)
        btnStartStop.text = getString(R.string.rematch)
        vibrate(longVibrationDuration)
    }

    private fun updateStatus(message: String, textSizeResourceId: Int) {
        Log.d(tag, "Setting status to: $message")
        tvStatus.text = message
        tvStatus.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(textSizeResourceId))
    }

    private fun updateScore(view: TextView, score: Int) {
        Log.d(tag, "Updating score as $score")
        view.text = score.toString()
        vibrate(shortVibrationDuration)
    }

    private fun toggleStartStop() {
        isStarted = !isStarted

        if (isStarted) {
            start()
        } else {
            stop()
        }
    }

    private fun vibrate(duration: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(tag, "Vibrating for Oreo+")
            vibrator?.vibrate(VibrationEffect.createOneShot(duration, vibrationAmplitude))
        } else {
            Log.d(tag, "Vibrating for legacy")
            vibrator?.vibrate(duration)
        }
    }

    private fun getRemainingTime(millis: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes)
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun getMatchResult() =
        when {
            scoreA > scoreB -> getString(R.string.teamAWins)
            scoreB > scoreA -> getString(R.string.teamBWins)
            else            -> getString(R.string.draw)
        }
}
