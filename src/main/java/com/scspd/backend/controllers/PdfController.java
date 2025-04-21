package com.scspd.backend.controllers;


import com.scspd.backend.services.PdfService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



@RestController
//@CrossOrigin(origins = "http://localhost:4200")
@CrossOrigin(origins = {"http://seseaz-frontend.vercel.app"})
@RequestMapping("/api/pdf")

public class PdfController {
    @Autowired
    private PdfService pdfAsignacionesService;

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTOR', 'MODERADOR')")
    @GetMapping("/asignaciones")
    public void generarPdfAsignaciones(HttpServletResponse response) {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=asignaciones.pdf");

        try {
            pdfAsignacionesService.exportPdfAsignaciones(response);
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
            pdfAsignacionesService.exportPdfMantenimientos(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
