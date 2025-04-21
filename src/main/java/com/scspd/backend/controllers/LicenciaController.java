package com.scspd.backend.controllers;
import com.scspd.backend.models.Licencia;
import com.scspd.backend.services.LicenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.bson.types.ObjectId;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;


@RestController
//@CrossOrigin(origins = "http://localhost:4200")
@CrossOrigin(origins = {"https://seseaz-frontend.vercel.app"})
@RequestMapping("/api/licencias")
public class LicenciaController {
    @Autowired
    private LicenciaService licenciaService;

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTOR', 'MODERADOR')")
    @GetMapping
    public List<Licencia> obtenerTodasLasLicencias() {
        return licenciaService.obtenerTodasLasLicencias();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTOR', 'MODERADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<Licencia> obtenerLicenciaPorId(@PathVariable ObjectId id) {
        Optional<Licencia> licencia = licenciaService.obtenerLicenciaPorId(id);
        return licencia.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Licencia> crearLicencia(@RequestBody Licencia licencia) {
        return new ResponseEntity<>(licenciaService.guardarLicencia(licencia), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<Licencia> actualizarLicencia(@PathVariable ObjectId id, @RequestBody Licencia licencia) {

        if (!licenciaService.existeLicenciaConId(id)) {
            return ResponseEntity.notFound().build();
        }
        licencia.setId(id);
        return new ResponseEntity<>(licenciaService.guardarLicencia(licencia), HttpStatus.OK);


    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLicencia(@PathVariable ObjectId id) {
        if (!licenciaService.existeLicenciaConId(id)) {
            return ResponseEntity.notFound().build();
        }
        licenciaService.eliminarLicenciaPorId(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTOR', 'MODERADOR')")
    @GetMapping("/buscar")
    public ResponseEntity<List<Licencia>> buscarLicenciasPorNombre(@RequestParam("nombre") String nombre) {
        return ResponseEntity.ok(licenciaService.buscarLicenciasPorNombre(nombre));

    }
}
