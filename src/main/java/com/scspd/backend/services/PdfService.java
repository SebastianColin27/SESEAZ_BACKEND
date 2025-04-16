package com.scspd.backend.services;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.scspd.backend.models.Asignacion;
import com.scspd.backend.models.Mantenimiento;
import com.scspd.backend.models.Personal;
import com.scspd.backend.repositories.AsignacionRepository;
import com.scspd.backend.repositories.MantenimientoRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PdfService {

    @Autowired
    private AsignacionRepository asignacionRepository;
    @Autowired
    private MantenimientoRepository mantenimientoRepository;

    public void exportPdfAsignaciones(HttpServletResponse response) throws Exception {
        List<Asignacion> asignaciones = asignacionRepository.findAll();

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        document.add(new Paragraph("REPORTE DE ASIGNACIONES"));
        document.add(new Paragraph(" ")); // Espacio

        // Tabla con 6 columnas
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 3, 3, 4, 4, 5}); // Ajusta según el contenido

        // Encabezados
        table.addCell("No. Serie");
        table.addCell("Fecha Asignación");
        table.addCell("Fecha Fin Asignación");
        table.addCell("Nombre Equipo");
        table.addCell("Personal Asignado");
        table.addCell("Comentarios");

        for (Asignacion asignacion : asignaciones) {
            String numeroSerie = asignacion.getEquipo() != null ? asignacion.getEquipo().getNumeroSerie() : "N/A";
            String fechaAsignacion = asignacion.getFechaAsignacion() != null ? asignacion.getFechaAsignacion().toString() : "N/A";
            String fechaFinAsignacion = asignacion.getFechaFinAsignacion() != null ? asignacion.getFechaFinAsignacion().toString() : "N/A";
            String nombreEquipo = asignacion.getNombreEquipo() != null ? asignacion.getNombreEquipo() : "N/A";

            String nombresPersonal = asignacion.getPersonal() != null
                    ? asignacion.getPersonal().stream()
                    .map(Personal::getNombre)
                    .collect(Collectors.joining(", "))
                    : "N/A";

            String comentarios = asignacion.getComentarios() != null ? asignacion.getComentarios() : "";

            table.addCell(numeroSerie);
            table.addCell(fechaAsignacion);
            table.addCell(fechaFinAsignacion);
            table.addCell(nombreEquipo);
            table.addCell(nombresPersonal);
            table.addCell(comentarios);
        }

        document.add(table);
        document.close();
    }


    public void exportPdfMantenimientos(HttpServletResponse response) throws Exception {
        List<Mantenimiento> mantenimientos = mantenimientoRepository.findAll();

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        document.add(new Paragraph("REPORTE DE MANTENIMIENTOS"));
        document.add(new Paragraph(" ")); // Espacio

        // Corregido: Tabla de 5 columnas
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 6, 4, 4, 4});

        // Encabezados
        table.addCell("Fecha");
        table.addCell("Actividad Realizada");
        table.addCell("Evidencia");
        table.addCell("Equipo");
        table.addCell("No. Serie");

        for (Mantenimiento mantenimiento : mantenimientos) {
            String fecha = mantenimiento.getFecha() != null ? mantenimiento.getFecha().toString() : "N/A";
            String actividad = mantenimiento.getActividadRealizada() != null ? mantenimiento.getActividadRealizada() : "";
            String evidencia = mantenimiento.getEvidencia() != null ? mantenimiento.getEvidencia() : "";

            Asignacion asignacion = mantenimiento.getAsignacion();
            String nombreEquipo = (asignacion != null && asignacion.getNombreEquipo() != null)
                    ? asignacion.getNombreEquipo() : "N/A";
            String numeroSerie = (asignacion != null && asignacion.getEquipo() != null)
                    ? asignacion.getEquipo().getNumeroSerie() : "N/A";

            // Celdas alineadas correctamente con los encabezados
            table.addCell(fecha);
            table.addCell(actividad);
            table.addCell(evidencia);
            table.addCell(nombreEquipo);
            table.addCell(numeroSerie);
        }

        document.add(table);
        document.close();
    }

}
