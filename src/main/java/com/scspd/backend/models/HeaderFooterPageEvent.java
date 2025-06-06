package com.scspd.backend.models;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.commons.io.IOUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class HeaderFooterPageEvent extends PdfPageEventHelper {
    private Image headerImage;
    private Image footerImage;


    public HeaderFooterPageEvent() {
        try {
            // Cargar imagen del encabezado desde el classpath
            InputStream headerStream = getClass().getClassLoader().getResourceAsStream("static/images/seseaz_logo.jpg");
            if (headerStream != null) {
                byte[] headerBytes = IOUtils.toByteArray(headerStream);
                headerImage = Image.getInstance(headerBytes);
                headerImage.scaleToFit(150, 150);
            } else {
                System.err.println("No se encontró la imagen del encabezado");
            }

            // Cargar imagen del pie de página desde el classpath
            InputStream footerStream = getClass().getClassLoader().getResourceAsStream("static/images/seseazFooter.jpg");
            if (footerStream != null) {
                byte[] footerBytes = IOUtils.toByteArray(footerStream);
                footerImage = Image.getInstance(footerBytes);
                footerImage.scaleToFit(450, 450);
            } else {
                System.err.println("No se encontró la imagen del pie de página");
            }

        } catch (IOException | BadElementException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte canvas = writer.getDirectContentUnder();
        float y = document.top() + 10;

        try {
            // Encabezado
            if (headerImage != null) {
                float headerX = document.left(); // puedes ajustar si quieres centrar
                float headerY = document.top()  - 30; // ligeramente encima del margen superior
                headerImage.setAbsolutePosition(headerX, headerY);
                canvas.addImage(headerImage);

                // Línea justo debajo del logo
                float lineY = y - headerImage.getScaledHeight() + 20; // 5 puntos de espacio debajo del logo
                canvas.setLineWidth(1f); // Grosor de la línea
                canvas.setColorStroke(Color.GRAY); // Color de la línea
                canvas.moveTo(document.left(), lineY);
                canvas.lineTo(document.right(), lineY);
                canvas.stroke();

            }

            // Pie de página
            if (footerImage != null) {
                float footerX = (document.left() + document.right() - footerImage.getScaledWidth()) / 2;
                float footerY = document.bottom() - footerImage.getScaledHeight(); // justo debajo del margen inferior
                footerImage.setAbsolutePosition(footerX, footerY);
                canvas.addImage(footerImage);
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}
