package com.example.schedule

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CalendarContract
import android.speech.RecognizerIntent
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var micButton: FloatingActionButton
    private lateinit var btnSummarize: MaterialButton
    private lateinit var btnAddToCalendar: MaterialButton
    private lateinit var btnShare: MaterialButton
    private lateinit var inputEditText: TextInputEditText
    private lateinit var tvTranscription: TextView
    private lateinit var tvSummary: TextView

    private val RECORD_AUDIO_PERMISSION_CODE = 1
    private var lastTranscription: String = ""
    private var lastSummary: String = ""

    // Number words + ordinals + slang (expand as needed)
    private val mkNumberWords = mapOf(
        // Cardinal
        "нула" to 0, "еден" to 1, "една" to 1, "две" to 2, "два" to 2, "три" to 3, "четири" to 4, "пет" to 5, "шест" to 6, "седум" to 7, "осум" to 8, "девет" to 9, "десет" to 10,
        "единаесет" to 11, "дванаесет" to 12, "тринаесет" to 13, "четиринаесет" to 14, "петнаесет" to 15, "шеснаесет" to 16, "седумнаесет" to 17, "осумнаесет" to 18, "деветнаесет" to 19,
        "дваесет" to 20, "триесет" to 30, "четириесет" to 40, "педесет" to 50,
        // Compound (spoken as "дваесет и три")
        "дваесет и еден" to 21, "дваесет и два" to 22, "дваесет и три" to 23, "дваесет и четири" to 24, "дваесет и пет" to 25, "дваесет и шест" to 26, "дваесет и седум" to 27, "дваесет и осум" to 28, "дваесет и девет" to 29,
        "триесет и еден" to 31, "триесет и два" to 32, "триесет и три" to 33, "триесет и четири" to 34, "триесет и пет" to 35, "триесет и шест" to 36, "триесет и седум" to 37, "триесет и осум" to 38, "триесет и девет" to 39,
        // Ordinals
        "први" to 1, "втори" to 2, "трети" to 3, "четврти" to 4, "петти" to 5, "шести" to 6, "седми" to 7, "осми" to 8, "деветти" to 9, "десетти" to 10, "единаесетти" to 11, "дванаесетти" to 12, "тринаесетти" to 13,
        "четиринаесетти" to 14, "петнаесетти" to 15, "шеснаесетти" to 16, "седумнаесетти" to 17, "осумнаесетти" to 18, "деветнаесетти" to 19, "дваесетти" to 20,
        "дваесет и први" to 21, "дваесет и втори" to 22, "дваесет и трети" to 23, "дваесет и четврти" to 24, "дваесет и петти" to 25, "дваесет и шести" to 26, "дваесет и седми" to 27, "дваесет и осми" to 28, "дваесет и деветти" to 29,
        "триесетти" to 30, "триесет и први" to 31,
        // Slang/short
        "саат" to -1, "саатот" to -1, "час" to -1, "часот" to -1, "и" to -1, "минут" to -1, "минути" to -1, "во" to -1
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Modern UI: find views
        micButton = findViewById<FloatingActionButton>(R.id.micButton)
        btnSummarize = findViewById(R.id.btnSummarize)
        btnAddToCalendar = findViewById(R.id.btnAddToCalendar)
        btnShare = findViewById(R.id.btnShare)
        inputEditText = findViewById(R.id.inputEditText)
        tvTranscription = findViewById(R.id.tvTranscription)
        tvSummary = findViewById(R.id.tvSummary)

        // Voice button triggers speech-to-text
        micButton.setOnClickListener { startSpeechToText() }
        btnSummarize.setOnClickListener { summarizeText() }
        btnAddToCalendar.setOnClickListener {
            val input = inputEditText.text?.toString()?.takeIf { it.isNotBlank() } ?: lastTranscription
            addToCalendar(input)
        }
        btnShare.setOnClickListener { shareEvent() }

        requestAudioPermission()
    }

    private fun requestAudioPermission() {
        val needed = listOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
        )
        val toRequest = needed.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (toRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, toRequest.toTypedArray(), RECORD_AUDIO_PERMISSION_CODE)
        }
    }

    private fun startSpeechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "mk-MK")
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Зборувајте за настанот...")

        try {
            speechResultLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Говорното препознавање не е достапно.", Toast.LENGTH_SHORT).show()
        }
    }

    private val speechResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val matches = result.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                lastTranscription = matches[0]
                tvTranscription.text = lastTranscription
                val parsedDateMillis = extractDateTimeFromCommand(lastTranscription)
                if (parsedDateMillis != null) {
                    Toast.makeText(this, "Датумот е препознаен и настанот ќе се отвори.", Toast.LENGTH_SHORT).show()
                    launchCalendarEvent(parsedDateMillis, lastTranscription)
                } else {
                    Toast.makeText(
                        this,
                        "Не можам да го препознаам датумот и времето. Пример: \"направи настан во календарот за дваесет и втори септември 2025 во петнаесет часот и петнаесет минути\"",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun summarizeText() {
        val input = inputEditText.text?.toString()?.takeIf { it.isNotBlank() } ?: lastTranscription
        lastSummary = "Ова е пример за сумирање на: $input"
        tvSummary.text = lastSummary
    }

    private fun addToCalendar(command: String) {
        val parsedDateMillis = extractDateTimeFromCommand(command)
        if (parsedDateMillis == null) {
            Toast.makeText(
                this,
                "Ве молиме кажете или внесете команда како: \"направи настан во календарот за дваесет и втори септември 2025 во петнаесет часот и петнаесет минути\"",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        launchCalendarEvent(parsedDateMillis, command)
    }

    private fun launchCalendarEvent(startMillis: Long, description: String) {
        val endMillis = startMillis + 60 * 60 * 1000 // 1 hour event
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
            putExtra(CalendarContract.Events.TITLE, "Нов настан")
            putExtra(CalendarContract.Events.DESCRIPTION, description)
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Не можам да отворам календар.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareEvent() {
        val textToShare = "Настан: $lastTranscription\nСумирано: $lastSummary"
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, textToShare)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, "Сподели преку..."))
    }

    // Converts Macedonian number word or ordinal to int.
    private fun mkNumberWordToInt(word: String): Int {
        val clean = word.lowercase(Locale.getDefault())
            .replace("минут(и|а|)", "")
            .replace("саат(от|)", "")
            .replace("час(от|)", "")
            .trim()
        mkNumberWords[clean]?.let { if (it >= 0) return it }
        // Try parsing composite (e.g. "дваесет и втори")
        val tokens = clean.split("и").map { it.trim() }
        if (tokens.size == 2) {
            val left = mkNumberWords[tokens[0]] ?: 0
            val right = mkNumberWords[tokens[1]] ?: 0
            if (left > 0 && right > 0) return left + right
            if (left > 0 && right == 0) return left
            if (left == 0 && right > 0) return right
        }
        // Try digit
        return clean.toIntOrNull() ?: 0
    }

    // --- STRONGEST recognition: digits, word forms, ordinals, slang, optional "во", "и", etc. ---
    private fun extractDateTimeFromCommand(command: String): Long? {
        val text = command.lowercase(Locale.getDefault())
            .replace("[,\\.]".toRegex(), " ")
            .replace("\\s+".toRegex(), " ").trim()

        // 1. Try: "за 20 септември 2025 во 15:30" or "... 15.30"
        val digitRegex = Regex("""за\s+(\S+)\s+([а-ш]+)\s+(\d{4})(?:\s+во|\s+и)?\s+(\d{1,2})[:\.](\d{2})""")
        val digitMatch = digitRegex.find(text)
        if (digitMatch != null) {
            val dayStr = digitMatch.groupValues[1]
            val day = mkNumberWordToInt(dayStr).toString()
            val month = digitMatch.groupValues[2]
            val year = digitMatch.groupValues[3]
            val hour = digitMatch.groupValues[4]
            val minute = digitMatch.groupValues[5]
            return parseMacedonianDate(day, month, year, hour, minute)
        }

        // 2. "за дваесет и втори септември 2025 во петнаесет часот и петнаесет минути"
        val wordHourMinuteRegex = Regex("""за\s+(.+?)\s+([а-ш]+)\s+(\d{4})(?:\s+во|\s+и)?\s+([а-ш ]+)\s+(?:часот|саатот|час|саат)\s+и\s+([а-ш ]+)\s+минут""")
        val wordMatch = wordHourMinuteRegex.find(text)
        if (wordMatch != null) {
            val dayStr = wordMatch.groupValues[1]
            val day = mkNumberWordToInt(dayStr).toString()
            val month = wordMatch.groupValues[2]
            val year = wordMatch.groupValues[3]
            val hourWord = wordMatch.groupValues[4]
            val minuteWord = wordMatch.groupValues[5]
            val hour = mkNumberWordToInt(hourWord).toString().padStart(2, '0')
            val minute = mkNumberWordToInt(minuteWord).toString().padStart(2, '0')
            return parseMacedonianDate(day, month, year, hour, minute)
        }

        // 3. "за дваесет и втори септември 2025 во петнаесет часот"
        val wordHourOnlyRegex = Regex("""за\s+(.+?)\s+([а-ш]+)\s+(\d{4})(?:\s+во|\s+и)?\s+([а-ш ]+)\s+(?:часот|саатот|час|саат)""")
        val wordHourOnlyMatch = wordHourOnlyRegex.find(text)
        if (wordHourOnlyMatch != null) {
            val dayStr = wordHourOnlyMatch.groupValues[1]
            val day = mkNumberWordToInt(dayStr).toString()
            val month = wordHourOnlyMatch.groupValues[2]
            val year = wordHourOnlyMatch.groupValues[3]
            val hourWord = wordHourOnlyMatch.groupValues[4]
            val hour = mkNumberWordToInt(hourWord).toString().padStart(2, '0')
            return parseMacedonianDate(day, month, year, hour, "00")
        }

        // 4. "за 20 септември 2025 во петнаесет и петнаесет"
        val hybridRegex = Regex("""за\s+(\S+)\s+([а-ш]+)\s+(\d{4})(?:\s+во|\s+и)?\s+([а-ш ]+)\s+и\s+([а-ш ]+)""")
        val hybridMatch = hybridRegex.find(text)
        if (hybridMatch != null) {
            val dayStr = hybridMatch.groupValues[1]
            val day = mkNumberWordToInt(dayStr).toString()
            val month = hybridMatch.groupValues[2]
            val year = hybridMatch.groupValues[3]
            val hourWord = hybridMatch.groupValues[4]
            val minuteWord = hybridMatch.groupValues[5]
            val hour = mkNumberWordToInt(hourWord).toString().padStart(2, '0')
            val minute = mkNumberWordToInt(minuteWord).toString().padStart(2, '0')
            return parseMacedonianDate(day, month, year, hour, minute)
        }

        // 5. "за дваесет и втори септември 2025" (no time, default 12:00)
        val justDateRegex = Regex("""за\s+(.+?)\s+([а-ш]+)\s+(\d{4})""")
        val justDateMatch = justDateRegex.find(text)
        if (justDateMatch != null) {
            val dayStr = justDateMatch.groupValues[1]
            val day = mkNumberWordToInt(dayStr).toString()
            val month = justDateMatch.groupValues[2]
            val year = justDateMatch.groupValues[3]
            return parseMacedonianDate(day, month, year, "12", "00")
        }

        // Fallback: try to pick up anything that looks like a date and time
        val fallback = Regex("""(\d{1,2})\s+([а-ш]+)\s+(\d{4}).*?(\d{1,2})[:\.](\d{2})""")
        val fallbackMatch = fallback.find(text)
        if (fallbackMatch != null) {
            val day = fallbackMatch.groupValues[1]
            val month = fallbackMatch.groupValues[2]
            val year = fallbackMatch.groupValues[3]
            val hour = fallbackMatch.groupValues[4]
            val minute = fallbackMatch.groupValues[5]
            return parseMacedonianDate(day, month, year, hour, minute)
        }

        return null
    }

    private fun parseMacedonianDate(day: String, month: String, year: String, hour: String, minute: String): Long? {
        val months = mapOf(
            "јануари" to "01", "февруари" to "02", "март" to "03", "април" to "04",
            "мај" to "05", "јуни" to "06", "јули" to "07", "август" to "08",
            "септември" to "09", "октомври" to "10", "ноември" to "11", "декември" to "12"
        )
        val m = months[month] ?: return null
        val format = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
        return try {
            format.parse("${day.padStart(2, '0')}-$m-$year ${hour.padStart(2, '0')}:${minute.padStart(2, '0')}")?.time
        } catch (e: Exception) {
            null
        }
    }
}