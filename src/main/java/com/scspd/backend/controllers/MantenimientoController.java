package com.scspd.backend.controllers;

import com.scspd.backend.models.Mantenimiento;
import com.scspd.backend.repositories.EquipoRepository;
import com.scspd.backend.repositories.MantenimientoRepository;
import com.scspd.backend.services.MantenimientoService;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = {"http://localhost:4200", "https://seseaz-controldeequipos.vercel.app"})
@RequestMapping("/api/mantenimientos")
public class MantenimientoController {
    @Autowired
    private MantenimientoService mantenimientoService;


    @PreAuthorize("hasAnyRole('ADMIN', 'LECTOR', 'MODERADOR')")
    @GetMapping
    public ResponseEntity<List<Mantenimiento>> obtenerTodosLosMantenimientos() {
        return ResponseEntity.ok(mantenimientoService.obtenerTodosLosMantenimientos());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTOR', 'MODERADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<Mantenimiento> obtenerMantenimientoPorId(@PathVariable ObjectId id) {
        Optional<Mantenimiento> mantenimiento = mantenimientoService.obtenerMantenimientoPorId(id);
        return mantenimiento.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Mantenimiento> crearMantenimiento(@Valid @RequestBody Mantenimiento mantenimiento) {
        try {
            Mantenimiento nuevoMantenimiento = mantenimientoService.guardarMantenimiento(mantenimiento);
            return new ResponseEntity<>(nuevoMantenimiento, HttpStatus.CREATED);
        } catch (Exception e) {
            // Maneja la excepción de forma adecuada (log, mensaje de error, etc.)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<Mantenimiento> actualizarMantenimiento(@PathVariable ObjectId id, @Valid @RequestBody Mantenimiento mantenimiento) {
        if (!mantenimientoService.existeMantenimientoPorId(id)) {
            return ResponseEntity.notFound().build();
        }
        mantenimiento.setId(id);
        Mantenimiento mantenimientoActualizado = mantenimientoService.guardarMantenimiento(mantenimiento);
        return ResponseEntity.ok(mantenimientoActualizado);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMantenimiento(@PathVariable ObjectId id) {
        if (!mantenimientoService.existeMantenimientoPorId(id)) {
            return ResponseEntity.notFound().build();
        }
        mantenimientoService.eliminarMantenimiento(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTOR', 'MODERADOR')")
    @GetMapping("/buscar")
    public ResponseEntity<List<Mantenimiento>> buscarPorNumeroSerie(@RequestParam String numeroSerie) {
        List<Mantenimiento> mantenimientos = mantenimientoService.buscarMantenimientosPorNumeroSerie(numeroSerie);

        if (mantenimientos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mantenimientos);
        }

        return ResponseEntity.ok(mantenimientos);
    }


}
