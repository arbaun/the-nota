package com.arbadev.thenota.utils

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class AnalisaQrCode(
    private val throtle: Long = 2000,
    private val onQrCodeScanned: (ByteArray) -> Unit) : ImageAnalysis.Analyzer {
    private val scanner = BarcodeScanning.getClient()
    private var lastScannedTimeStamp = 0L

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(image: ImageProxy) {
        val curenttime= System.currentTimeMillis()
        if(curenttime-lastScannedTimeStamp<throtle){
            image.close()
            return
        }
        val mediaImage = image.image
        if(mediaImage!=null){
            val img = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
            scanner.process(img).addOnSuccessListener { barcodes ->
                for (barcode in barcodes){
                    barcode.rawBytes?.let {
                        lastScannedTimeStamp= curenttime
                        onQrCodeScanned(it)
                    }
                }
            }.addOnCompleteListener {
                image.close()
            }
        }
    }
}