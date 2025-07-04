package com.scspd.backend.controllers;

import com.scspd.backend.models.Asignacion;
import com.scspd.backend.services.AsignacionService;
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
@CrossOrigin(origins = {"http://localhost:4200", "https://seseaz-controldeequipos.vercel.app"})
@RequestMapping("/api/asignaciones")
public class AsignacionController {
    @Autowired
    private AsignacionService asignacionService;

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTOR', 'MODERADOR')")
    @GetMapping
    public List<Asignacion> obtenerTodasLasAsignaciones() {
        return asignacionService.obtenerTodasLasAsignaciones();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTOR', 'MODERADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<Asignacion> obtenerAsignacionPorId(@PathVariable ObjectId id) {
        Optional<Asignacion> asignacion = asignacionService.obtenerAsignacionPorId(id);
        return asignacion.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Asignacion> crearAsignacion(@Valid @RequestBody Asignacion asignacion) {
        Asignacion nuevaAsignacion = asignacionService.guardarAsignacion(asignacion);
        return new ResponseEntity<>(nuevaAsignacion, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<Asignacion> actualizarAsignacion(
            @PathVariable ObjectId id,
            @Valid @RequestBody Asignacion asignacion) {
        if(!asignacionService.existeAsignacionConId(id) ){
            return ResponseEntity.notFound().build();


        }


        return new ResponseEntity<>(asignacionService.guardarAsignacion(asignacion), HttpStatus.OK);


    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAsignacion(@PathVariable ObjectId id) {
        if (!asignacionService.existeAsignacionConId(id)) {
            return ResponseEntity.notFound().build();
        }
        asignacionService.eliminarAsignacionPorId(id);
        return ResponseEntity.noContent().build();
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'LECTOR', 'MODERADOR')")
    @GetMapping("/buscar")
    public ResponseEntity<List<Asignacion>> buscarPorNumeroSerie(@RequestParam String numeroSerie) {
        List<Asignacion> asignaciones = asignacionService.buscarAsignacionesPorNumeroSerie(numeroSerie);

        if (asignaciones.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(asignaciones);
        }

        return ResponseEntity.ok(asignaciones);
    }



}
