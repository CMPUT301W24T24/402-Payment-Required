

package com.example.qrcheckin.core;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRCodeGenerator {

    /**
     * A method to generate a QR code
     * @param data the data to be encoded
     * @param width the width of the QR code
     * @param height the height of the QR code
     * @return the QR code bitmap
     * Reference: https://ihilalahmadd.medium.com/how-to-generate-qr-code-in-android-5a2a7edf11c Hilal Ahmad. Accessed 2024-03-06
     * Reference: https://stackoverflow.com/questions/8800919/how-to-generate-a-qr-code-for-an-android-application Stefano. Accessed 2024-03-06
     */
    public static Bitmap generateQRCode(String data, int width, int height) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, width, height);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            return barcodeEncoder.createBitmap(bitMatrix);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
