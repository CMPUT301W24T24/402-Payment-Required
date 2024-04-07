package com.example.qrcheckin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import android.graphics.Bitmap;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.qrcheckin.core.QRCodeGenerator;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)


public class QRCodeGeneratorTest {

    private Bitmap mockQRCode() {
        return QRCodeGenerator.generateQRCode("nhH4GxzOVjXuKK6vDiK9", 800, 800);
    }
    @Test
    public void testGenerateQRCode() {
        Bitmap qrCode = QRCodeGenerator.generateQRCode("nhH4GxzOVjXuKK6vDiK9", 800, 800);
        assertNotNull(qrCode);
        assertTrue(qrCode instanceof Bitmap);
        Bitmap qrCode1 = QRCodeGenerator.generateQRCode("JdAnY3sDMK1FWI0qIEDj", 800, 800);
        assertNotNull(qrCode1);
        assertTrue(qrCode1 instanceof Bitmap);
    }


    @Test
    public void testGetQRCodeData() {
        String qrString = QRCodeGenerator.getQRCodeData(mockQRCode());
        assertEquals("nhH4GxzOVjXuKK6vDiK9", qrString);
        Bitmap qrCode1 = QRCodeGenerator.generateQRCode("JdAnY3sDMK1FWI0qIEDj", 800, 800);
        String qrString1 = QRCodeGenerator.getQRCodeData(qrCode1);
        assertEquals("JdAnY3sDMK1FWI0qIEDj", qrString1);

    }
}
