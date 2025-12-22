package com.SmartEvent.SmartEvent.Service;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;

@Service
public class PdfService {

    /**
     * Creates a circular clipped version of an image
     */
    private byte[] createCircularImage(String imagePath) throws Exception {
        BufferedImage original = ImageIO.read(new File(imagePath));
        int size = Math.min(original.getWidth(), original.getHeight());

        // Create circular image with transparency
        BufferedImage circularImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = circularImage.createGraphics();

        // Enable anti-aliasing for smooth edges
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Clip to circle
        g2.setClip(new Ellipse2D.Float(0, 0, size, size));

        // Center the original image
        int x = (size - original.getWidth()) / 2;
        int y = (size - original.getHeight()) / 2;
        g2.drawImage(original, x, y, original.getWidth(), original.getHeight(), null);
        g2.dispose();

        // Convert to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(circularImage, "png", baos);
        return baos.toByteArray();
    }


    public byte[] generateInvitationPdf(String eventTitle, String inviteName, String logoPath, String qrCodePath) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(out);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Set margins for a badge-like appearance
            document.setMargins(50, 40, 50, 40);

            // Add a decorative colored bar at the top
            Color brandColor = new DeviceRgb(41, 128, 185); // Professional blue

            // Add initial content to create the first page
            document.add(new Paragraph("\n"));

            // Now we can safely access the first page
            PdfCanvas canvas = new PdfCanvas(pdf.getFirstPage());
            canvas.setFillColor(brandColor);
            canvas.rectangle(0, pdf.getFirstPage().getPageSize().getHeight() - 60,
                    pdf.getFirstPage().getPageSize().getWidth(), 60);
            canvas.fill();

            // ---- LOGO (small, rounded, centered) ----
            if (logoPath != null && !logoPath.isEmpty()) {
                try {
                    String cleanPath = logoPath.startsWith("/") ? logoPath.substring(1) : logoPath;

                    // Create circular version of logo
                    byte[] circularLogoBytes = createCircularImage(cleanPath);
                    ImageData logoData = ImageDataFactory.create(circularLogoBytes);
                    Image logo = new Image(logoData);

                    // Small logo size (not full page)
                    logo.setWidth(100);
                    logo.setHeight(100);
                    logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

                    document.add(logo);
                } catch (Exception e) {
                    System.err.println("Could not load logo: " + e.getMessage());
                }
            }

            // Spacing after logo
            document.add(new Paragraph("\n"));

            // ---- "BADGE" TITLE ----
            Paragraph badgeTitle = new Paragraph("EVENT BADGE")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(24)
                    .setBold()
                    .setFontColor(brandColor)
                    .setMarginTop(10)
                    .setMarginBottom(5);

            document.add(badgeTitle);

            // Decorative line
            Paragraph line = new Paragraph("━━━━━━━━━━━━━━")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(14)
                    .setFontColor(ColorConstants.LIGHT_GRAY)
                    .setMarginBottom(15);

            document.add(line);

            // ---- INVITEE NAME (prominent) ----
            Paragraph name = new Paragraph(inviteName)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(28)
                    .setBold()
                    .setFontColor(ColorConstants.BLACK)
                    .setMarginBottom(10);

            document.add(name);

            // ---- EVENT TITLE ----
            Paragraph eventLabel = new Paragraph("Event")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(12)
                    .setFontColor(ColorConstants.GRAY)
                    .setMarginBottom(3);

            document.add(eventLabel);

            Paragraph event = new Paragraph(eventTitle)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20)
                    .setBold()
                    .setFontColor(new DeviceRgb(52, 73, 94))
                    .setMarginBottom(20);

            document.add(event);

            // ---- QR CODE (centered, reasonable size) ----
            if (qrCodePath != null && !qrCodePath.isEmpty()) {
                try {
                    String cleanQrPath = qrCodePath.startsWith("/") ? qrCodePath.substring(1) : qrCodePath;
                    ImageData qrData = ImageDataFactory.create(cleanQrPath);
                    Image qr = new Image(qrData);

                    qr.setWidth(230);
                    qr.setHeight(230);
                    qr.setHorizontalAlignment(HorizontalAlignment.CENTER);
                    qr.setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 2));
                    qr.setMarginTop(10);

                    document.add(qr);
                } catch (Exception e) {
                    System.err.println("Could not load QR code: " + e.getMessage());
                }
            }

            document.add(new Paragraph("\n"));

            // ---- FOOTER INSTRUCTION ----
            Paragraph footer = new Paragraph("Please present this badge at the entrance")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(11)
                    .setItalic()
                    .setFontColor(ColorConstants.GRAY)
                    .setMarginTop(15);

            document.add(footer);

            // Add scan instruction
            Paragraph scanInstruction = new Paragraph("Scan QR code for verification")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(10)
                    .setFontColor(ColorConstants.LIGHT_GRAY);

            document.add(scanInstruction);

        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed: " + e.getMessage(), e);
        }

        return out.toByteArray();
    }
}