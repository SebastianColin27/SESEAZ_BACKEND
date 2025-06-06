package com.scspd.backend.models;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.*;
import java.io.File;
import java.io.IOException;
public class HeaderFooterPageEvent extends PdfPageEventHelper {
    private Image headerImage;
    private Image footerImage;


    public HeaderFooterPageEvent() {
        try {
            // Ruta a la imagen del encabezado
            String headerPath = new File("src/main/resources/static/seseaz_logo.jpg").getAbsolutePath();
            headerImage = Image.getInstance(headerPath);
            headerImage.scaleToFit(150, 150);

            // Ruta a la imagen del pie de página
            String footerPath = new File("src/main/resources/static/seseazFooter.jpg").getAbsolutePath();
            footerImage = Image.getInstance(footerPath);
            footerImage.scaleToFit(450, 450);
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
