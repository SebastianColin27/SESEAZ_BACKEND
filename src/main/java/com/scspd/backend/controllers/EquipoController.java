package com.scspd.backend.controllers;
import com.scspd.backend.models.Equipo;
import com.scspd.backend.services.EquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.bson.types.ObjectId;


import java.util.*;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
//@CrossOrigin(origins = "http://localhost:4200")
@CrossOrigin(origins = {"http://seseaz-frontend.vercel.app"})
@RequestMapping("/api/equipos")
public class EquipoController {
    @Autowired
    private EquipoService equipoService;


    @PreAuthorize("hasAnyRole('ADMIN', 'LECTOR', 'MODERADOR')")
    @GetMapping
    public ResponseEntity<List<Equipo>> obtenerTodosLosEquipos() {
        return ResponseEntity.ok(equipoService.obtenerTodosLosEquipos());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTOR', 'MODERADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<Equipo> obtenerEquipoPorId(@PathVariable ObjectId id) {
        Optional<Equipo> equipo = equipoService.obtenerEquipoPorId(id);
        return equipo.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Equipo> crearEquipo(@Valid @RequestBody Equipo equipo) {
        Equipo nuevoEquipo = equipoService.crearEquipo(equipo);
        return new ResponseEntity<>(nuevoEquipo, HttpStatus.CREATED);

    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<Equipo> actualizarEquipo(@PathVariable ObjectId id, @Valid @RequestBody Equipo equipo) {
        if (!equipoService.existeEquipoPorId(id)) {
            return ResponseEntity.notFound().build();
        }

        equipo.setId(id);
        Equipo equipoActualizado = equipoService.actualizarEquipo(equipo);
        return ResponseEntity.ok(equipoActualizado);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEquipo(@PathVariable ObjectId id) {
        if (!equipoService.existeEquipoPorId(id)) {
            return ResponseEntity.notFound().build();
        }
        equipoService.eliminarEquipo(id);
        return ResponseEntity.noContent().build();

    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTOR', 'MODERADOR')")
    @GetMapping("/buscarSerie")
    public ResponseEntity<Equipo> buscarEquipoPorNumeroDeSerie(@RequestParam("serieEquipo") String numeroSerie) {
        Equipo equipo = equipoService.buscarEquipoPorNumeroDeSerie(numeroSerie);
        if (equipo != null) {
            return ResponseEntity.ok(equipo);
        } else {
            return ResponseEntity.notFound().build(); // 404 si no se encuentra
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTOR', 'MODERADOR')")
    @GetMapping("/buscarMarca")
    public ResponseEntity<List<Equipo>> buscarEquiposPorMarca(@RequestParam("marcaEquipo") String marcaEquipo) {
        List<Equipo> equipos = equipoService.buscarEquiposPorMarca(marcaEquipo);
        if (equipos.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 si no hay resultados
        }
        return ResponseEntity.ok(equipos);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTOR', 'MODERADOR')")
    @GetMapping("/buscarModelo")
    public ResponseEntity<List<Equipo>> buscarEquiposPorModelo(@RequestParam("modeloEquipo") String modeloEquipo) {
        List<Equipo> equipos = equipoService.buscarEquiposPorModelo(modeloEquipo);
        if (equipos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(equipos);
    }



}
