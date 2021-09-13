package com.ovais.odroidhelper.helpers

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.net.*
import android.os.Build
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.ovais.odroidhelper.R
import com.ovais.odroidhelper.utils.Constants.TEXT_PLAIN
import com.ovais.odroidhelper.utils.Constants.isNetworkConnected
import com.vend.movanos.customer.data.geocoding.geocoding.AddressComponent
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.CharacterIterator
import java.text.StringCharacterIterator
import java.util.HashMap
import kotlin.math.roundToInt

class ODroidHelper {

    fun getSecondaryAddress(addressComponents: List<AddressComponent>): String {
        var secondaryAddress = ""
        try {
            secondaryAddress = "${addressComponents[1].longName},${addressComponents[2].longName},${addressComponents[3].longName}"
        } catch (exception: java.lang.Exception) {
            Timber.e(exception)
        }
        return  secondaryAddress
    }

    fun hasLocationPermissions(context: Context): Boolean {
        return if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun bitmapDescriptorFromVector(
        context: Context?,
        @DrawableRes vectorDrawableResourceId: Int
    ): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context!!, vectorDrawableResourceId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun startNetworkCallback(context: Context) {
        val cm: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val builder: NetworkRequest.Builder = NetworkRequest.Builder()

        cm.registerNetworkCallback(
            builder.build(),
            object : ConnectivityManager.NetworkCallback() {

                override fun onAvailable(network: Network) {
                    isNetworkConnected = true
                }

                override fun onLost(network: Network) {
                    isNetworkConnected = false
                }
            })
    }

    fun shareApp(requireActivity: AppCompatActivity,@StringRes message:Int) {
        Intent(Intent.ACTION_SEND).apply {
            type = TEXT_PLAIN
            putExtra(Intent.EXTRA_TEXT,requireActivity.getString(message))
            resolveActivity(requireActivity.packageManager)?.let {
                requireActivity.startActivity(Intent.createChooser(this,requireActivity.getString(R.string.choose_one)))
            }
        }
    }

    fun getKeyByValue(hashMap: HashMap<String, String>, value: String): String {
        val filtered = hashMap.filterValues {
            it == value
        }.keys
        if (filtered.toString().replace("[", "").replace("]", "").isBlank()){
            return "-1"
        }
        return filtered.toString().replace("[", "").replace("]", "")
    }

    fun getContentType(file: File): String {
        val fileType = getFileType(file)
        return if (fileType == "pdf") {
            "application/pdf"
        } else {
            "image"
        }
    }

    private fun getFileType(file: File): String? =
        MimeTypeMap.getFileExtensionFromUrl(file.toString())

    fun getFileName(file: File): String {
        return file.name.replace(".pdf", "")
    }

    fun ContentResolver.getBitmapFromUri(uri: Uri): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            bitmap = if (Build.VERSION.SDK_INT < 30) {
                MediaStore.Images.Media.getBitmap(this, uri)
            } else {
                val source: ImageDecoder.Source = ImageDecoder.createSource(this, uri)
                ImageDecoder.decodeBitmap(source)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        var resizedByWidth: Bitmap? = null
        var image: Bitmap? = null
        bitmap?.let {
            resizedByWidth = getResizedBitmap(it, 250)
            image = resizedByWidth?.resizeByHeight(250)

        }

        return image
    }

    private fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    private fun Bitmap.resizeByHeight(height: Int): Bitmap {
        val ratio: Float = this.height.toFloat() / this.width.toFloat()
        val width: Int = (height / ratio).roundToInt()
        return Bitmap.createScaledBitmap(this, width, height, false)
    }

    @SuppressLint("DefaultLocale")
    fun humanReadableByteCountSI(bytes: Long): String? {

        var bytes = bytes
        if (-1000 < bytes && bytes < 1000) {
            return "$bytes B"
        }
        val ci: CharacterIterator = StringCharacterIterator("kMGTPE")
        while (bytes <= -999950 || bytes >= 999950) {
            bytes /= 1000
            ci.next()
        }

        if (java.lang.String.format("%.1f %cB", bytes / 1000.0, ci.current()).contains("k")) {
            return "4.0"
        }
        return java.lang.String.format("%.1f %cB", bytes / 1000.0, ci.current()).replace("MB", "")
            .replace("kB", "")
    }

    @Suppress("DEPRECATION")
    fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri{
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path.toString())
    }

}

