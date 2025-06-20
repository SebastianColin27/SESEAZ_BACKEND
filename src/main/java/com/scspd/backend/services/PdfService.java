package com.scspd.backend.services;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.scspd.backend.models.*;
import com.scspd.backend.repositories.AsignacionRepository;
import com.scspd.backend.repositories.EquipoRepository;
import com.scspd.backend.repositories.MantenimientoRepository;
import com.scspd.backend.repositories.PersonalRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PdfService {

    @Autowired
    private AsignacionRepository asignacionRepository;
    @Autowired
    private MantenimientoRepository mantenimientoRepository;
    @Autowired
    private EquipoRepository equipoRepository;
    @Autowired
    private AsignacionService asignacionService;
    @Autowired
    private MantenimientoService mantenimientoService;
    @Autowired
    private PersonalRepository personalRepository;

    Font fontContenido = new Font(Font.HELVETICA, 10);

    public void exportPdfAsignaciones(HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=asignaciones_reporte_general.pdf");
        List<Asignacion> asignaciones = asignacionRepository.findAll();

        Document document = new Document(PageSize.A4, 36, 36, 54, 72);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        writer.setPageEvent(new HeaderFooterPageEvent());

        document.open();


        document.add(new Paragraph("\n\n\n"));
        document.add(new Paragraph("REPORTE DE ASIGNACIONES GENERAL "));
        document.add(Chunk.NEWLINE); // Espacio


        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 3, 3, 4, 4, 5});


        table.addCell("No. Serie");
        table.addCell("Fecha Asignación");
        table.addCell("Fecha Fin Asignación");
        table.addCell("Nombre Equipo");
        table.addCell("Personal Asignado");
        table.addCell("Comentarios");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Asignacion asignacion : asignaciones) {
            String numeroSerie = asignacion.getEquipo() != null ? asignacion.getEquipo().getNumeroSerie() : "N/A";

            String fechaAsignacion = asignacion.getFechaAsignacion() != null ? dateFormat.format(asignacion.getFechaAsignacion()) : "N/A";

            String fechaFinAsignacion = asignacion.getFechaFinAsignacion() != null ? dateFormat.format(asignacion.getFechaFinAsignacion()) : "Actual"; // Mostrar "Actual" si no ha finalizado
            String nombreEquipo = asignacion.getNombreEquipo() != null ? asignacion.getNombreEquipo() : "N/A";


            String nombrePersonal = asignacion.getPersonal() != null
                    ? asignacion.getPersonal().getNombre()
                    : "N/A";

            String comentarios = asignacion.getComentarios() != null ? asignacion.getComentarios() : "";

            table.addCell(new PdfPCell(new Phrase(numeroSerie, fontContenido)));
            table.addCell(new PdfPCell(new Phrase(fechaAsignacion, fontContenido)));
            table.addCell(new PdfPCell(new Phrase(fechaFinAsignacion, fontContenido)));
            table.addCell(new PdfPCell(new Phrase(nombreEquipo, fontContenido)));
            table.addCell(new PdfPCell(new Phrase(nombrePersonal, fontContenido)));
            table.addCell(new PdfPCell(new Phrase(comentarios, fontContenido)));
        }

        document.add(table);
        document.close();


    }


        public void exportPdfMantenimientos(HttpServletResponse response) throws Exception {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=mantenimientos_reporte_general.pdf");
        List<Mantenimiento> mantenimientos = mantenimientoRepository.findAll();

            Document document = new Document(PageSize.A4, 36, 36, 54, 72);
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
            writer.setPageEvent(new HeaderFooterPageEvent());
        document.open();

        document.add(new Paragraph("\n\n\n"));
        document.add(new Paragraph("REPORTE DE MANTENIMIENTOS GENERAL"));
        document.add(new Paragraph(" ")); // Espacio


        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 6, 5, 5, 5});

        table.addCell("Fecha");
        table.addCell("Actividad Realizada");
        table.addCell("Evidencia");
        table.addCell("Equipo");
        table.addCell("Personal Asignado");


            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Para formatear fechas

            for (Mantenimiento mantenimiento : mantenimientos) {
                String fecha = mantenimiento.getFecha() != null ? dateFormat.format(mantenimiento.getFecha()) : "N/A";
                String actividad = mantenimiento.getActividadRealizada() != null ? mantenimiento.getActividadRealizada() : "";
                String evidencia = mantenimiento.getEvidencia() != null ? mantenimiento.getEvidencia() : "";

                Equipo equipo = mantenimiento.getEquipo();
                String nombreEquipo = (equipo != null && equipo.getNumeroSerie() != null)
                        ? equipo.getNumeroSerie() : "N/A";

                String personal = "Sin asignar";
                if (mantenimiento.getEquipo() != null && mantenimiento.getFecha() != null) {
                    personal = obtenerNombreAsignado(mantenimiento.getEquipo().getId(), mantenimiento.getFecha());
                }




                table.addCell(new PdfPCell(new Phrase(fecha, fontContenido)));
                table.addCell(new PdfPCell(new Phrase(actividad, fontContenido)));
                table.addCell(new PdfPCell(new Phrase(evidencia, fontContenido)));
                table.addCell(new PdfPCell(new Phrase(nombreEquipo, fontContenido)));
                table.addCell(new PdfPCell(new Phrase(personal, fontContenido)));
            }


            document.add(table);
        document.close();
    }



    // Generar reporte PDF de Asignaciones por Equipo
    public void exportPdfAsignacionesPorEquipo(HttpServletResponse response, ObjectId equipoId) throws Exception {
        // 1. Obtener la información del equipo para el título del reporte
        Optional<Equipo> equipoOptional = equipoRepository.findById(equipoId);
        if (equipoOptional.isEmpty()) {
            throw new EntityNotFoundException("No se encontró el equipo con el ID proporcionado.");
        }
        Equipo equipo = equipoOptional.get();

        // 2. Obtener las asignaciones para este equipo
        // Si asignacionService.obtenerAsignacionesPorEquipoId() aún usa findByEquipoId, eso está bien.
        List<Asignacion> asignaciones = asignacionService.obtenerAsignacionesPorEquipoId(equipoId);

        // 3. Crear el documento PDF
        Document document = new Document(PageSize.A4, 36, 36, 54, 72);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        writer.setPageEvent(new HeaderFooterPageEvent());
        document.open();

        // Título del reporte
        document.add(new Paragraph("\n\n\n"));
        document.add(new Paragraph("REPORTE DE HISTORIAL DE ASIGNACIONES PARA EL EQUIPO: " + equipo.getNumeroSerie() + " (" + equipo.getModelo() + "-" + equipo.getTipo() + "-" + equipo.getColor() + ")"));
        document.add(new Paragraph(" ")); // Espacio

        // 4. Tabla con los datos de las asignaciones (Ajustada a 4 columnas si solo hay un Personal)
        // Columnas: Fecha Asignación, Fecha Fin Asignación, Personal Asignado, Licencias Asignadas
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 3, 4, 5, 5}); // Ajusta según el contenido

        // Encabezados de la tabla
        table.addCell("Fecha Asignación");
        table.addCell("Fecha Fin Asignación");
        table.addCell("Personal Asignado"); // Encabezado para el personal (singular)
        table.addCell("Licencias Asignadas");
        table.addCell("Comentarios");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Para formatear fechas

        for (Asignacion asignacion : asignaciones) {
            // Formatear fechas
            String fechaAsignacion = asignacion.getFechaAsignacion() != null ? dateFormat.format(asignacion.getFechaAsignacion()) : "N/A";
            String fechaFinAsignacion = asignacion.getFechaFinAsignacion() != null ? dateFormat.format(asignacion.getFechaFinAsignacion()) : "Actual"; // Mostrar "Actual" si no ha finalizado

            // Obtener nombre del personal (Asumimos que personal es un objeto simple)
            String nombrePersonal = (asignacion.getPersonal() != null && asignacion.getPersonal().getNombre() != null)
                    ? asignacion.getPersonal().getNombre()
                    : "Sin asignar";

            // Obtener nombres de licencias (sigue siendo una lista)
            String nombresLicencias = (asignacion.getLicencias() != null && !asignacion.getLicencias().isEmpty())
                    ? asignacion.getLicencias().stream()
                    .filter(l -> l != null && l.getNombreLicencia() != null)
                    .map(Licencia::getNombreLicencia)
                    .collect(Collectors.joining(", "))
                    : "Sin licencias";

            String comentarios = asignacion.getComentarios();
            String observaciones = (comentarios != null && !comentarios.isEmpty())
                    ? comentarios
                    : "Sin comentarios";

            table.addCell(new PdfPCell(new Phrase(fechaAsignacion, fontContenido)));
            table.addCell(new PdfPCell(new Phrase(fechaFinAsignacion, fontContenido)));
            table.addCell(new PdfPCell(new Phrase(nombrePersonal, fontContenido)));
            table.addCell(new PdfPCell(new Phrase(nombresLicencias, fontContenido)));
            table.addCell(new PdfPCell(new Phrase(observaciones, fontContenido)));
        }

        document.add(table);
        document.close();
    }

    //  Generar reporte PDF de Mantenimientos por Equipo
    public void exportPdfMantenimientosPorEquipo(HttpServletResponse response, ObjectId equipoId) throws Exception {
        // 1. Obtener la información del equipo para el título del reporte
        Optional<Equipo> equipoOptional = equipoRepository.findById(equipoId);
        if (equipoOptional.isEmpty()) {
            throw new EntityNotFoundException("No se encontró el equipo con el ID proporcionado.");
        }
        Equipo equipo = equipoOptional.get();

        // 2. Obtener los mantenimientos para este equipo
        List<Mantenimiento> mantenimientos = mantenimientoService.obtenerMantenimientosPorEquipoId(equipoId);

        // 3. Crear el documento PDF
        Document document = new Document(PageSize.A4, 36, 36, 54, 72);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        writer.setPageEvent(new HeaderFooterPageEvent());
        document.open();

        // Título del reporte
        document.add(new Paragraph("\n\n\n"));
        document.add(new Paragraph("REPORTE DE HISTORIAL DE MANTENIMIENTOS PARA EL EQUIPO: " + equipo.getNumeroSerie() + " (" + equipo.getModelo() + "-" + equipo.getColor() + ")"));
        document.add(new Paragraph(" ")); // Espacio

        // 4. Tabla con los datos de los mantenimientos (Ajustada - SIN Personal Asignado si no existe en Mantenimiento)
        // Columnas: Fecha, Actividad Realizada, Evidencia
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 6, 5, 5}); // Ajusta según el contenido

        // Encabezados de la tabla
        table.addCell("Fecha");
        table.addCell("Actividad Realizada");
        table.addCell("Evidencia");
        table.addCell("Asigando");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Para formatear fechas

        for (Mantenimiento mantenimiento : mantenimientos) {
            String fecha = mantenimiento.getFecha() != null ? dateFormat.format(mantenimiento.getFecha()) : "N/A";
            String actividad = mantenimiento.getActividadRealizada() != null ? mantenimiento.getActividadRealizada() : "";
            String evidencia = mantenimiento.getEvidencia() != null ? mantenimiento.getEvidencia() : "";


            String personal = "Sin asignar";
            if (mantenimiento.getEquipo() != null && mantenimiento.getFecha() != null) {
                personal = obtenerNombreAsignado(mantenimiento.getEquipo().getId(), mantenimiento.getFecha());
            }



            table.addCell(new PdfPCell(new Phrase(fecha, fontContenido)));
            table.addCell(new PdfPCell(new Phrase(actividad, fontContenido)));
            table.addCell(new PdfPCell(new Phrase(evidencia, fontContenido)));
            table.addCell(new PdfPCell(new Phrase(personal, fontContenido)));
        }


        document.add(table);
        document.close();
    }

    // Generar reporte PDF de Asignaciones por Personal
    public void exportPdfAsignacionesPorPersonal(HttpServletResponse response, ObjectId personalId) throws Exception {

        Optional<Personal> personalOptional = personalRepository.findById(personalId);
        if (personalOptional.isEmpty()) {
            throw new EntityNotFoundException("No se encontró el personal con el ID proporcionado.");
        }
        Personal personal = personalOptional.get();

        List<Asignacion> asignaciones = asignacionService.obtenerAsignacionesPorPersonalId(personalId);

        Document document = new Document(PageSize.A4, 36, 36, 54, 72);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        writer.setPageEvent(new HeaderFooterPageEvent());
        document.open();

        document.add(new Paragraph("\n\n\n"));
        document.add(new Paragraph("REPORTE DE HISTORIAL DE ASIGNACIONES DEL PERSONAL: " + personal.getNombre() + " (" + personal.getCargo() + ")"));
        document.add(new Paragraph(" ")); // Espacio


        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 3, 5, 5, 5}); // Ajusta según el contenido


        table.addCell("Fecha Asignación");
        table.addCell("Fecha Fin Asignación");
        table.addCell("Equipo Asignado");
        table.addCell("Licencias Asignadas");
        table.addCell("Comentarios");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Asignacion asignacion : asignaciones) {
            String fechaAsignacion = asignacion.getFechaAsignacion() != null ? dateFormat.format(asignacion.getFechaAsignacion()) : "N/A";
            String fechaFinAsignacion = asignacion.getFechaFinAsignacion() != null ? dateFormat.format(asignacion.getFechaFinAsignacion()) : "Actual";

            String equipoAsignado = (asignacion.getEquipo() != null && asignacion.getEquipo().getNumeroSerie() != null)
                    ? asignacion.getEquipo().getNumeroSerie() + " (" + asignacion.getEquipo().getModelo() + " - " + asignacion.getEquipo().getTipo()+ " - "  +asignacion.getEquipo().getColor() +")"
                    : "Sin equipo";

            String nombresLicencias = (asignacion.getLicencias() != null && !asignacion.getLicencias().isEmpty())
                    ? asignacion.getLicencias().stream()
                    .filter(l -> l != null && l.getNombreLicencia() != null)
                    .map(Licencia::getNombreLicencia)
                    .collect(Collectors.joining(", "))
                    : "Sin licencias";

            String comentarios = asignacion.getComentarios();
            String observaciones = (comentarios != null && !comentarios.isEmpty()) ? comentarios : "Sin comentarios";

            table.addCell(new PdfPCell(new Phrase(fechaAsignacion, fontContenido)));
            table.addCell(new PdfPCell(new Phrase(fechaFinAsignacion, fontContenido)));
            table.addCell(new PdfPCell(new Phrase(equipoAsignado, fontContenido)));
            table.addCell(new PdfPCell(new Phrase(nombresLicencias, fontContenido)));
            table.addCell(new PdfPCell(new Phrase(observaciones, fontContenido)));
        }

        document.add(table);
        document.close();
    }

//función auxiliar para encontrar el nombre del responsable en esa fecha del mantenimiento

    private String obtenerNombreAsignado(ObjectId equipoId, Date fechaMantenimiento) {
        List<Asignacion> asignaciones = asignacionRepository.findByEquipoId(equipoId);
        if (asignaciones == null || asignaciones.isEmpty()) {
            return "Sin asignar";
        }

        return asignaciones.stream()
                .filter(a -> {
                    Date inicio = a.getFechaAsignacion();
                    Date fin = a.getFechaFinAsignacion();
                    return inicio != null &&
                            !fechaMantenimiento.before(inicio) && // fechaMantenimiento >= inicio
                            (fin == null || fechaMantenimiento.before(fin)); // fechaMantenimiento < fin (si existe)
                })
                .max(Comparator.comparing(Asignacion::getFechaAsignacion)) // la más reciente válida
                .map(a -> a.getPersonal() != null ? a.getPersonal().getNombre() : "Sin asignar")
                .orElse("Sin asignar");
    }



}

