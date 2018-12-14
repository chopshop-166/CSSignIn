package com.chopshop166.cssignin

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.annotation.ColorInt
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.synthetic.main.activity_main.*
import java.util.EnumMap

class MainActivity : AppCompatActivity() {

    private val prefChanged = SharedPreferences.OnSharedPreferenceChangeListener { _, _ -> genQR() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        prefs.registerOnSharedPreferenceChangeListener (prefChanged)
        genQR()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_actions, menu)
        return true
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
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chopshop166.com/calendar-Team166"))
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
        val firstName = prefs.getString("firstname_text", "")
        val lastName = prefs.getString("lastname_text", "")
        val isMentor = prefs.getBoolean("mentor_switch", false)

        val prefix = if (isMentor) "Mentor - " else ""
        val qrText = "$prefix$lastName, $firstName"

        val writer = QRCodeWriter()
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hints[EncodeHintType.MARGIN] = 2

        val qrWidth = 512
        val qrHeight = 512
        val qrData = writer.encode(qrText, BarcodeFormat.QR_CODE, qrWidth, qrHeight, hints)

        val qrDataAndroid = qrToAndroid(qrData, qrWidth, qrHeight)

        qrCodeImage.setImageBitmap(qrDataAndroid)
    }

    private fun qrToAndroid(bits : BitMatrix, qrWidth : Int, qrHeight : Int) : Bitmap {
        val image = Bitmap.createBitmap(qrWidth, qrHeight, Bitmap.Config.ARGB_8888)
        for(x in 0..(qrWidth-1)) {
            for(y in 0..(qrHeight-1)) {
                val color = colorFor(x, y, bits[x, y], qrWidth, qrHeight)
                image.setPixel(x, y, color)
            }
        }
        return image
    }

    @ColorInt
    private fun colorFor(x : Int, y : Int, value : Boolean, width : Int, height : Int) : Int {
        @ColorInt val chopShopBlue = Color.rgb(0x0F, 0x2B, 0x8E)
        return if(value) {
            val centerX = width / 2.0
            val centerY = height / 2.0
            val hypotDist = Math.hypot(centerX, centerY)
            gradientColor(Math.hypot(x - centerX, y - centerY), 0.0, hypotDist, Color.BLACK, chopShopBlue)
        } else { Color.TRANSPARENT }
    }

    private fun gradientColor(x: Double, minX: Double, maxX: Double,
                              @ColorInt from : Int, @ColorInt to : Int): Int {
        val range = maxX - minX
        val p = Math.pow((x - minX) / range, 2.0)

        return Color.rgb(
            (Color.red(from) * p + Color.red(to) * (1 - p)).toInt(),
            (Color.green(from) * p + Color.green(to) * (1 - p)).toInt(),
            (Color.blue(from) * p + Color.blue(to) * (1 - p)).toInt()
        )
    }
}
