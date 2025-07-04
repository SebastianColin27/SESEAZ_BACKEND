package com.scspd.backend.controllers;


import com.scspd.backend.services.PdfService;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



@RestController
@CrossOrigin(origins = {"http://localhost:4200","https://seseaz-controldeequipos.vercel.app"})
@RequestMapping("/api/pdf")

public class PdfController {
    @Autowired
    private PdfService pdfService;

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTOR', 'MODERADOR')")
    @GetMapping("/asignaciones")
    public void generarPdfAsignaciones(HttpServletResponse response) {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=asignaciones.pdf");

        try {
            pdfService.exportPdfAsignaciones(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTOR', 'MODERADOR')")
    @GetMapping("/mantenimientos")
    public void generarPdfBitacora(HttpServletResponse response) {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=mantenimientos.pdf");

        try {
            pdfService.exportPdfMantenimientos(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTOR', 'MODERADOR')")
    @GetMapping("/asignaciones/equipo/{equipoId}")
    public void generarPdfAsignacionesPorEquipo(HttpServletResponse response, @PathVariable ObjectId equipoId) {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_asignaciones_equipo_" + equipoId.toHexString() + ".pdf");

        try {
            pdfService.exportPdfAsignacionesPorEquipo(response, equipoId);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTOR', 'MODERADOR')")
    @GetMapping("/mantenimientos/equipo/{equipoId}")
    public void generarPdfMantenimientosPorEquipo(HttpServletResponse response, @PathVariable ObjectId equipoId) {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_mantenimientos_equipo_" + equipoId.toHexString() + ".pdf");

        try {
            pdfService.exportPdfMantenimientosPorEquipo(response, equipoId);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTOR', 'MODERADOR')")
    @GetMapping("/asignaciones/personal/{personalId}")
    public void generarPdfAsignacionesPorPersonal(HttpServletResponse response, @PathVariable ObjectId personalId) {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_asignaciones_personal_" + personalId.toHexString() + ".pdf");

        try {
            pdfService.exportPdfAsignacionesPorPersonal(response, personalId);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }


}
