package com.chopshop166.chopshop166

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.registerOnSharedPreferenceChangeListener { _, _ -> genQR() }
        genQR()
    }

    /** Called when the user taps the Settings button */
    fun goToSettings(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun genQR() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val firstName = prefs.getString("firstname_text", "")
        val lastName = prefs.getString("lastname_text", "")
        val isMentor = prefs.getBoolean("mentor_switch", false)

        val builder = StringBuilder()
        if (isMentor) {
            builder.append("Mentor - ")
        }
        builder.append(lastName)
        builder.append(", ")
        builder.append(firstName)

        val qrText = builder.toString()
        val writer = QRCodeWriter()
        val qrData = writer.encode(qrText, BarcodeFormat.QR_CODE, 256, 256)

        val qrDataAndroid = qrToAndroid(qrData)

        val view = findViewById<ImageView>(R.id.qrCodeImage)
        view.setImageBitmap(qrDataAndroid)
    }

    private fun qrToAndroid(bits : BitMatrix) : Bitmap {
        val image = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888)
        for(x in 0..255) {
            for(y in 0..255) {
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
            gradientColor((x+y)/2.0f, 0.0f, 256.0f, chopShopBlue, Color.BLACK)
        } else { Color.WHITE }
    }

    private fun gradientColor(x: Float, minX: Float, maxX: Float,
                              @ColorInt from : Int, @ColorInt to : Int): Int {
        val range = maxX - minX
        val p : Float = (x - minX) / range

        return Color.rgb(
            (Color.red(from) * p + Color.red(to) * (1 - p)).toInt(),
            (Color.green(from) * p + Color.green(to) * (1 - p)).toInt(),
            (Color.blue(from) * p + Color.blue(to) * (1 - p)).toInt()
        )
    }
}
