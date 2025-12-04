package com.SmartEvent.SmartEvent.Utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class QrCodeGenerator {

    public static String generateQRCode(String text, String uploadDir, String fileName) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 250, 250);

            Path path = FileSystems.getDefault().getPath(uploadDir + "/" + fileName);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
            return path.toString();
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Error generating QR code", e);
        }
    }
}
