

package com.example.qrcheckin.core;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

/**
 * A class that generates qr codes
 */
public class QRCodeGenerator {

    /**
     * A method to generate a QR code
     * Reference: https://ihilalahmadd.medium.com/how-to-generate-qr-code-in-android-5a2a7edf11c Hilal Ahmad. Accessed 2024-03-06
     * Reference: https://stackoverflow.com/questions/8800919/how-to-generate-a-qr-code-for-an-android-application Stefano. Accessed 2024-03-06
     * @param data the data to be encoded
     * @param width the width of the QR code
     * @param height the height of the QR code
     * @return the QR code bitmap
     */
    public static Bitmap generateQRCode(String data, int width, int height) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, width, height);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            return barcodeEncoder.createBitmap(bitMatrix);
        } catch (Exception e) {
            Log.e("QRCode", "Error encoding QR code", e);
        }
        return null;
    }

    /**
     * A method to get the data from a QR code
     * Reference: https://stackoverflow.com/questions/32120988/qr-code-decoding-images-using-zxing-android Bhoomika Brahmbhatt. Accessed 2024-03-06
     * @param bitmap the QR code bitmap
     * @return the data from the QR code
     */
    public static String getQRCodeData(Bitmap bitmap){
        QRCodeReader reader = new QRCodeReader();
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), pixels)));
        try {
            // Decode the QR code
            return reader.decode(binaryBitmap).getText();
        } catch (Exception e) {
            Log.e("QRCode", "Error decoding QR code", e);
        }
        return null;
    }
}
