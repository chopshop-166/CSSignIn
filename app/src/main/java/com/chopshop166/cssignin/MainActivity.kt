package com.chopshop166.cssignin

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.chopshop166.cssignin.databinding.ActivityMainBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import java.util.*
import kotlin.math.hypot
import kotlin.math.pow

class MainActivity : AppCompatActivity() {

    companion object {
        const val PREF_NAME_FIRST = "firstname_text"
        const val PREF_NAME_LAST = "lastname_text"

        @ColorInt
        val CHOPSHOP_BLUE = Color.rgb(0x0F, 0x2B, 0x8E)
    }

    private lateinit var binding: ActivityMainBinding

    private val qrWidth = 512
    private val qrHeight = 512

    private val prefChanged = SharedPreferences.OnSharedPreferenceChangeListener { _, _ -> genQR() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        prefs.registerOnSharedPreferenceChangeListener(prefChanged)
        if (prefs.getString(PREF_NAME_FIRST, "") == "" ||
            prefs.getString(PREF_NAME_LAST, "") == ""
        ) {
            val dialogFragment = AskNameFragment()
            dialogFragment.show(supportFragmentManager, "names")
        }
        genQR()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_actions, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            // User chose the "Settings" item, show the app settings UI...
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            true
        }

        R.id.action_web -> {
            // User chose the "Website" action, go to the website
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.chopshop166.com"))
            startActivity(browserIntent)
            true
        }

        R.id.action_calendar -> {
            // User chose the "Calendar" action, go to the calendar website...
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://chopshop166.com/calendar-Team166"))
            startActivity(browserIntent)
            true
        }

        R.id.action_github -> {
            // User chose the "Calendar" action, go to the calendar website...
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/chopshop-166"))
            startActivity(browserIntent)
            true
        }

        R.id.action_help -> {
            // User chose the "Help" action, go to the about page...
            val intent = Intent(this, HelpActivity::class.java)
            startActivity(intent)
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    private fun genQR() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val firstName = prefs.getString(PREF_NAME_FIRST, "")
        val lastName = prefs.getString(PREF_NAME_LAST, "")
        val isMentor = prefs.getBoolean("mentor_switch", false)

        val prefix = if (isMentor) "Mentor - " else ""
        val qrText = "$prefix$lastName, $firstName"

        val writer = QRCodeWriter()
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hints[EncodeHintType.MARGIN] = 0

        val qrData = writer.encode(qrText, BarcodeFormat.QR_CODE, qrWidth, qrHeight, hints)

        val qrDataAndroid = qrToAndroid(qrData)

        binding.qrCodeImage.setImageBitmap(qrDataAndroid)
    }

    private fun qrToAndroid(bits: BitMatrix): Bitmap {
        val image = Bitmap.createBitmap(qrWidth, qrHeight, Bitmap.Config.ARGB_8888)
        for (x in 0 until qrWidth) {
            for (y in 0 until qrHeight) {
                val color = colorFor(x, y, bits[x, y])
                image.setPixel(x, y, color)
            }
        }
        return image
    }

    @ColorInt
    private fun colorFor(x: Int, y: Int, value: Boolean): Int {
        return if (value) {
            val centerX = qrWidth / 2.0
            val centerY = qrHeight / 2.0
            val hDist = hypot(centerX, centerY)
            gradientColor(hypot(x - centerX, y - centerY), hDist, CHOPSHOP_BLUE)
        } else {
            Color.TRANSPARENT
        }
    }

    @ColorInt
    private fun gradientColor(x: Double, maxX: Double, @ColorInt to: Int): Int {
        val p = (x / maxX).pow(2.0)

        return Color.rgb(
            (Color.red(Color.BLACK) * p + Color.red(to) * (1 - p)).toInt(),
            (Color.green(Color.BLACK) * p + Color.green(to) * (1 - p)).toInt(),
            (Color.blue(Color.BLACK) * p + Color.blue(to) * (1 - p)).toInt()
        )
    }
}
