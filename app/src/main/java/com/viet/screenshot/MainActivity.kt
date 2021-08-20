package com.viet.screenshot

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.viet.screenshot.databinding.ActivityMainBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE = 1
    private var path = ""

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermission()

        binding.btnScreenshot.setOnClickListener {
            val bitmap = screenShot(window.decorView.rootView)
            binding.imgShow.setImageBitmap(bitmap)
            saveImg(bitmap)
        }

        binding.btnShare.setOnClickListener {
            val share = Intent(Intent.ACTION_SEND)
            share.type = "image/*"
            Log.e("aaa", "${Uri.fromFile(File(path))}")
            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(path)))
            share.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(Intent.createChooser(share, "Share Highscore"))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Permission Granted", Toast.LENGTH_SHORT)
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE
                )
            }
        }
    }

    private fun saveImg(bitmap: Bitmap) {
        val date = Date()
        val format: CharSequence = SimpleDateFormat("yyyy-MM-dd_hh-mm-ss").format(date)

        try {
            val dirPath = Environment.getExternalStoragePublicDirectory("Screen").toString()
            val fileDir = File(dirPath)
            if (!fileDir.exists()) {
                val mkdir = fileDir.mkdir()
            }

            path = "$dirPath/ScreenShot-$format.jpeg"
            val imageFile = File(path)

            val fileOutputStream = FileOutputStream(imageFile)
            val quality = 100
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }


    }

    private fun screenShot(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.width,
            view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE
            )
        } else {
            Toast.makeText(this@MainActivity, "Permission Granted", Toast.LENGTH_SHORT)
        }
    }
}