package com.chopshop166.cssignin

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.edit
import androidx.core.text.HtmlCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_name.view.*
import java.util.EnumMap
import kotlin.math.*

class MainActivity : AppCompatActivity() {

    private val qrWidth = 512
    private val qrHeight = 512

    private val prefChanged = SharedPreferences.OnSharedPreferenceChangeListener { _, _ -> genQR() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        prefs.registerOnSharedPreferenceChangeListener (prefChanged)
        if(prefs.getString("firstname_text", "") == "" ||
            prefs.getString("lastname_text", "") == "") {
            val dialogView = layoutInflater.inflate(R.layout.dialog_name, findViewById(R.id.content))
            val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme)).apply {
                setView(dialogView)
                setTitle(HtmlCompat.fromHtml("<font color='#000000'>Enter name</font>", HtmlCompat.FROM_HTML_MODE_COMPACT))
            }
            val dialog = builder.show()
            dialogView.dialogOkBtn.setOnClickListener {
                dialog.dismiss()
                prefs.edit {
                    putString("firstname_text", dialogView.dialogFirstNameEt.text.toString())
                    putString("lastname_text", dialogView.dialogLastNameEt.text.toString())
                }
            }
        }
        genQR()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_actions, menu)
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
        hints[EncodeHintType.MARGIN] = 0

        val qrData = writer.encode(qrText, BarcodeFormat.QR_CODE, qrWidth, qrHeight, hints)

        val qrDataAndroid = qrToAndroid(qrData)

        qrCodeImage.setImageBitmap(qrDataAndroid)
    }

    private fun qrToAndroid(bits : BitMatrix) : Bitmap {
        val image = Bitmap.createBitmap(qrWidth, qrHeight, Bitmap.Config.ARGB_8888)
        for(x in 0 until qrWidth) {
            for(y in 0 until qrHeight) {
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
            val hDist = hypot(centerX, centerY)
            gradientColor(hypot(x - centerX, y - centerY), hDist, chopShopBlue)
        } else { Color.TRANSPARENT }
    }

    @ColorInt
    private fun gradientColor(x: Double, maxX: Double, @ColorInt to : Int): Int {
        val p = (x / maxX).pow(2.0)

        return Color.rgb(
            (Color.red(Color.BLACK) * p + Color.red(to) * (1 - p)).toInt(),
            (Color.green(Color.BLACK) * p + Color.green(to) * (1 - p)).toInt(),
            (Color.blue(Color.BLACK) * p + Color.blue(to) * (1 - p)).toInt()
        )
    }
}
