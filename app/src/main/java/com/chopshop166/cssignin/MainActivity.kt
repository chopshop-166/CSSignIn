package com.chopshop166.cssignin

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.annotation.ColorInt
import android.view.View
import android.widget.ImageView
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter

class MainActivity : AppCompatActivity() {

    private val prefChanged = SharedPreferences.OnSharedPreferenceChangeListener { _, _ -> genQR() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        prefs.registerOnSharedPreferenceChangeListener (prefChanged)
        genQR()
    }

    /** Called when the user taps the Settings button */
    fun goToSettings(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    /** Called when the user taps the Website button */
    fun goToWebsite(view: View) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.chopshop166.com"))
        startActivity(browserIntent)
    }

    /** Called when the user taps the Website button */
    fun goToCalendar(view: View) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chopshop166.com/calendar-Team166"))
        startActivity(browserIntent)
    }

    /** Called when the user taps the Help button */
    fun goToHelp(view: View) {
        val intent = Intent(this, HelpActivity::class.java)
        startActivity(intent)
    }

    private fun genQR() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val firstName = prefs.getString("firstname_text", "")
        val lastName = prefs.getString("lastname_text", "")
        val isMentor = prefs.getBoolean("mentor_switch", false)

        val prefix = if (isMentor) "Mentor - " else ""
        val qrText = "$prefix$lastName, $firstName"

        val writer = QRCodeWriter()
        val qrData = writer.encode(qrText, BarcodeFormat.QR_CODE, qrWidth, qrHeight)

        val qrDataAndroid = qrToAndroid(qrData)

        val view = findViewById<ImageView>(R.id.qrCodeImage)
        view.setImageBitmap(qrDataAndroid)
    }

    private val qrWidth = 256
    private val qrHeight = 256

    private fun qrToAndroid(bits : BitMatrix) : Bitmap {
        val image = Bitmap.createBitmap(qrWidth, qrHeight, Bitmap.Config.ARGB_8888)
        for(x in 0..(qrWidth-1)) {
            for(y in 0..(qrHeight-1)) {
                val color = colorFor(x, y, bits[x, y])
                image.setPixel(x, y, color)
            }
        }
        return image
    }

    @ColorInt
    private fun colorFor(x : Int, y : Int, value : Boolean) : Int {
        @ColorInt val chopShopBlue = Color.rgb(0x0F, 0x2B, 0x8E)
        return if(value) {
            val centerX = qrWidth / 2.0
            val centerY = qrHeight / 2.0
            gradientColor(Math.hypot(x - centerX, y - centerY), 0.0, centerX, Color.BLACK, chopShopBlue)
        } else { Color.TRANSPARENT }
    }

    private fun gradientColor(x: Double, minX: Double, maxX: Double,
                              @ColorInt from : Int, @ColorInt to : Int): Int {
        val range = maxX - minX
        val p = (x - minX) / range

        return Color.rgb(
            (Color.red(from) * p + Color.red(to) * (1 - p)).toInt(),
            (Color.green(from) * p + Color.green(to) * (1 - p)).toInt(),
            (Color.blue(from) * p + Color.blue(to) * (1 - p)).toInt()
        )
    }
}
