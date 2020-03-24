package com.htphtp.tools.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import androidx.fragment.app.Fragment
import com.htphtp.tools.PermissionManagerActivity

/**
 *
 * Create by htp on 2019/3/8
 */
open class TakePhoto(captureStrategy: CaptureStrategy, val activity: Activity, val fragment: Fragment?) {

    private val mediaStoreCompat: MediaStoreCompat = MediaStoreCompat(activity, fragment).apply {
        setCaptureStrategy(captureStrategy)
    }

    private var requestCode = REQUEST_CODE

    fun turnOnCamera(requestCode: Int = REQUEST_CODE) {
        this.requestCode = requestCode

        PermissionManagerActivity.requestPermissions(
                activity,
                object : PermissionManagerActivity.OnPermissionManagerEvent {
                    override fun onPermissionSuccess() {
                        mediaStoreCompat.dispatchCaptureIntent(fragment?.context
                                ?: activity, requestCode)
                    }

                    override fun onPermissionFailure() {

                    }

                    override fun onSettingDialogClickExit() {

                    }
                },
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    fun getCurrentPhotoUri(): Uri {
        return mediaStoreCompat.currentPhotoUri
    }

    fun getCurrentPhotoPath(): String {
        return mediaStoreCompat.currentPhotoPath
    }

    fun isEqualsRequestCode(requestCode: Int) = this.requestCode == requestCode


    companion object {
        const val REQUEST_CODE = 2000

        fun scanImageFile(
                context: Context,
                imageFilePath: String,
                callback: (path: String, uri: Uri) -> Unit = { _, _ -> }) {
            MediaScannerConnection.scanFile(
                    context,
                    arrayOf(imageFilePath),
                    arrayOf("image/jpeg"),
                    callback
            )
        }
    }
}