package com.scspd.backend.controllers;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.scspd.backend.models.Equipo;
import com.scspd.backend.repositories.EquipoRepository;
import com.scspd.backend.services.EquipoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
//@CrossOrigin(origins = "http://localhost:4200")
@CrossOrigin(origins = {"http://localhost:4200", "https://seseaz-frontend.vercel.app"})
@RequestMapping("/api/equipos")
public class EquipoController {
    @Autowired
    private EquipoService equipoService;
    @Autowired
    private EquipoRepository equipoRepository;


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
        equipo.setImagenGridFsId(null);
        Equipo nuevoEquipo = equipoService.crearEquipo(equipo);
        return new ResponseEntity<>(nuevoEquipo, HttpStatus.CREATED);

    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<Equipo> actualizarEquipo(@PathVariable String id, @Valid @RequestBody Equipo equipo) {
        try {
            ObjectId objectId = new ObjectId(id);
            if (!equipoService.existeEquipoPorId(objectId)) {
                return ResponseEntity.notFound().build();
            }

      /*  equipo.setId(id);
        Equipo equipoActualizado = equipoService.actualizarEquipo(equipo);
        return ResponseEntity.ok(equipoActualizado);*/
            Optional<Equipo> existingEquipoOpt = equipoService.obtenerEquipoPorId(objectId);
            if (!existingEquipoOpt.isPresent()) {
                return ResponseEntity.notFound().build(); // Should not happen based on existsById check, but safety
            }
            Equipo existingEquipo = existingEquipoOpt.get();

            // Update fields from the request body
            existingEquipo.setNumeroSerie(equipo.getNumeroSerie());
            existingEquipo.setTipo(equipo.getTipo());
            existingEquipo.setMarca(equipo.getMarca());
            existingEquipo.setColor(equipo.getColor());
            existingEquipo.setModelo(equipo.getModelo());
            existingEquipo.setProcesador(equipo.getProcesador());
            existingEquipo.setRam(equipo.getRam());
            existingEquipo.setHDD(equipo.getHDD());
            existingEquipo.setSDD(equipo.getSDD());
            existingEquipo.setPuertos(equipo.getPuertos()); // Make sure Puertos is correctly handled
            existingEquipo.setEstado(equipo.getEstado());
            existingEquipo.setFechaCompra(equipo.getFechaCompra());
            // existingEquipo.setImagenGridFsId() is NOT updated here from the request body

            Equipo equipoActualizado = equipoService.actualizarEquipo(existingEquipo); // Save the updated existing entity
            return ResponseEntity.ok(equipoActualizado);

        } catch (IllegalArgumentException e) {
            log.error("Invalid ID format: {}", id, e);
            return ResponseEntity.badRequest().build();
        }
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
    public ResponseEntity<List<Equipo>> buscarEquipoPorNumeroDeSerie(@RequestParam("serieEquipo") String numeroSerie) {
        List<Equipo> equipos = equipoService.buscarEquipoPorNumeroDeSerie(numeroSerie);

        if (equipos != null && !equipos.isEmpty()) {
            return ResponseEntity.ok(equipos);
        } else {
            return ResponseEntity.notFound().build(); // 404 si no hay resultados
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

    /*imagen*/


    @PostMapping("/{id}/imagen")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Equipo> uploadEquipoImagen(@PathVariable String id,
                                                     @RequestParam("imagen") MultipartFile file) {
        try {
            // Validation: Check if file is present and not empty
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(null); // Or a custom error response
            }

            // Validate file type if necessary (e.g., only images)
            if (!file.getContentType().startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(null);
            }


            Equipo equipoActualizado = equipoService.guardarImagenEquipo(id, file);
            return ResponseEntity.ok(equipoActualizado);
        } catch (IllegalArgumentException e) {
            log.error("Error uploading image: Invalid Equipo ID or Equipo not found.", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 404 if equipo not found
        } catch (IOException e) {
            log.error("Error reading image file.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 500 on IO error
        } catch (Exception e) {
            log.error("An unexpected error occurred during image upload.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Catch other potential errors
        }
    }

    @GetMapping("/{id}/imagen")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> getEquipoImagen(@PathVariable String id) {
        try {
            GridFSFile gridFSFile = equipoService.obtenerImagenGridFsFile(id);

            if (gridFSFile == null) {
                return ResponseEntity.notFound().build();
            }

            InputStream inputStream = equipoService.obtenerImagenInputStream(id);
            String contentType = equipoService.obtenerImagenContentType(id);
            byte[] imageBytes = IOUtils.toByteArray(inputStream); // usa commons-io

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType != null ? contentType : "image/jpeg"))
                    .body(imageBytes);

        } catch (IllegalArgumentException e) {
            log.error("Error getting image: Invalid Equipo ID.", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("An unexpected error occurred while retrieving image.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}






