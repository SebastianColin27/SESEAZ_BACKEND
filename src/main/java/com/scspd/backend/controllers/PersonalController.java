package com.scspd.backend.controllers;
import com.scspd.backend.models.Personal;
import com.scspd.backend.services.PersonalService;
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
@CrossOrigin(origins = {"http://localhost:4200", "https://seseaz-frontend.vercel.app"})
@RequestMapping("/api/personal")
public class PersonalController {
    @Autowired
    private PersonalService personalService;

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTOR', 'MODERADOR')")
    @GetMapping
    public List<Personal> obtenerTodoElPersonal() {
        return personalService.obtenerTodoElPersonal();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTOR', 'MODERADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<Personal> obtenerPersonalPorId(@PathVariable ObjectId id) {
        return personalService.obtenerPersonalPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Personal> crearPersonal(@Valid @RequestBody Personal personal) {
        Personal nuevoPersonal = personalService.crearPersonal(personal);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPersonal);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<Personal> actualizarPersonal(@PathVariable ObjectId id, @Valid @RequestBody Personal personal) {
        if (!personalService.existePersonalPorId(id)) {
            return ResponseEntity.notFound().build();
        }
        personal.setId(id);
        Personal personalActualizado = personalService.actualizarPersonal(personal);
        return ResponseEntity.ok(personalActualizado);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPersonal(@PathVariable ObjectId id) {
        if (!personalService.existePersonalPorId(id)) {
            return ResponseEntity.notFound().build();
        }
        personalService.eliminarPersonal(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LECTOR', 'MODERADOR')")
    @GetMapping("/buscar")
    public ResponseEntity<List<Personal>> buscarPersonalPorNombre(@RequestParam String nombre) {
        List<Personal> lista = personalService.buscarPersonalPorNombre(nombre);
        return ResponseEntity.ok(lista);
    }
}
