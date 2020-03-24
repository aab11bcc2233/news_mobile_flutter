//package com.newboomutils.tools.image
//
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.graphics.Matrix
//import android.media.ExifInterface
//import android.support.media.ExifInterface
//import android.util.Log
//import java.io.IOException
//
//class ImageUtils private constructor(){
//    companion object {
//        fun shouldRotate(imageFilePath: String): Boolean {
//            val degree = getImageDegree(imageFilePath)
//            return degree != 0 && degree != 180
//        }
//
//        fun getImageDegree(imageFilePath: String): Int {
//            var degree = 0
//            try {
//                // 从指定路径下读取图片，并获取其EXIF信息
//                val exifInterface = ExifInterface(imageFilePath);
//                // 获取图片的旋转信息
//                val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
//                    ExifInterface.ORIENTATION_NORMAL);
//                degree = when (orientation) {
//                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
//                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
//                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
//                    else -> 0
//                }
//            } catch (e: IOException) {
//                e.printStackTrace();
//                val tag = ImageUtils::class.java.simpleName
//                Log.e(tag, " could not read exif info of the image: " + imageFilePath)
//            }
//            return degree
//        }
//
//        fun rotateBitmapByDegree(bitmap: Bitmap, degree: Int): Bitmap {
//            var returnBitmap: Bitmap? = null;
//
//            // 根据旋转角度，生成旋转矩阵
//            val matrix = Matrix()
//            matrix.postRotate(degree.toFloat());
//            try {
//                // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
//                returnBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true);
//            } catch (e: OutOfMemoryError) {
//            }
//            if (returnBitmap == null) {
//                returnBitmap = bitmap
//            }
//
//            if (bitmap != returnBitmap) {
//                bitmap.recycle()
//            }
//
//            return returnBitmap
//        }
//
//        @Throws(IllegalArgumentException::class)
//        fun getImageSize(imageFilePath: String): Pair<Int, Int> {
//            val options = BitmapFactory.Options()
//            options.inJustDecodeBounds = true
//            BitmapFactory.decodeFile(imageFilePath, options)
//            return Pair(options.outWidth, options.outHeight)
//        }
//
//        fun getImageSizeCheckRotate(imageFilePath: String): Pair<Int, Int> {
//            var (w, h) = getImageSize(imageFilePath)
//            if (shouldRotate(imageFilePath)) {
//                val temp = w
//                w = h
//                h = temp
//            }
//
//            return Pair(w, h)
//        }
//
//        fun getScaleImageSize(imageSize: Pair<Int, Int>, screenSize: Pair<Float, Float>): Pair<Int, Int> {
//            val (w, h) = imageSize
//            val (screenWidth, screenHeight) = screenSize
//
//            val widthScale = (screenWidth) / w
//            val heightScale = (screenHeight) / h
//
//            val overrideW = (widthScale * w).toInt()
//            val overrideH = (heightScale * h).toInt()
//            return Pair(overrideW, overrideH)
//        }
//    }
//}