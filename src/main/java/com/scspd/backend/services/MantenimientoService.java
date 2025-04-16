package com.scspd.backend.services;
import com.scspd.backend.models.Asignacion;
import com.scspd.backend.models.Equipo;
import com.scspd.backend.models.Mantenimiento;
import com.scspd.backend.repositories.AsignacionRepository;
import com.scspd.backend.repositories.EquipoRepository;
import com.scspd.backend.repositories.MantenimientoRepository;
import com.scspd.backend.repositories.PersonalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import org.bson.types.ObjectId;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;

@Service
public class MantenimientoService {
    @Autowired
    private MantenimientoRepository mantenimientoRepository;

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private AsignacionRepository asignacionRepository;

    @Autowired
    private PersonalRepository personalRepository;

    public List<Mantenimiento> obtenerTodosLosMantenimientos() {
        return mantenimientoRepository.findAll();
    }

    public Optional<Mantenimiento> obtenerMantenimientoPorId(ObjectId id) {
        return mantenimientoRepository.findById(id);
    }
    public Mantenimiento guardarMantenimiento(Mantenimiento mantenimiento) {

        // Validar Asignacion
        if (mantenimiento.getAsignacion() != null) {
            if (mantenimiento.getAsignacion().getId() == null) {
                // Si el ID de Asignacion es nulo, no se puede relacionar el mantenimiento
                // En este caso, puedes optar por:
                // 1. Lanzar una excepción, indicando que se requiere un ID de Asignacion válido
                // 2. Crear una nueva Asignacion (si tiene sentido en tu lógica de negocio)
                // 3. Simplemente no establecer la asignación (dejar mantenimiento.setAsignacion(null))
                // Aquí optaremos por lanzar una excepción para asegurar que se proporcione un ID de Asignacion existente
                throw new IllegalArgumentException("Se requiere un ID de Asignacion válido para registrar el mantenimiento.");
            } else {
                // Si el ID de Asignacion no es nulo, verificar que exista en la base de datos
                Optional<Asignacion> asignacionOptional = asignacionRepository.findById(mantenimiento.getAsignacion().getId());
                if (asignacionOptional.isEmpty()) {
                    throw new EntityNotFoundException("No se encontró la Asignacion con el ID proporcionado.");
                }
                mantenimiento.setAsignacion(asignacionOptional.get());
            }
        } else {
            // Si el objeto Asignacion es nulo, puedes optar por:
            // 1. Lanzar una excepción, indicando que se requiere una Asignacion
            // 2. Permitir mantenimientos sin Asignacion (si tiene sentido)
            // Aquí optaremos por lanzar una excepción para asegurar que se proporcione una Asignacion
            throw new IllegalArgumentException("Se requiere una Asignacion para registrar el mantenimiento.");
        }

        return mantenimientoRepository.save(mantenimiento);
    }


    public boolean existeMantenimientoPorId(ObjectId id) {
        return mantenimientoRepository.existsById(id);
    }

    public void eliminarMantenimiento(ObjectId id) {
        mantenimientoRepository.deleteById(id);
    }




    public List<Mantenimiento> obtenerMantenimientosPorAsignacion(ObjectId asignacionId) {
        return mantenimientoRepository.findByAsignacionId(asignacionId);
    }

    public List<Mantenimiento> obtenerMantenimientosPorEquipo(ObjectId equipoId) {
        List<Mantenimiento> mantenimientosResult = new ArrayList<>();

        Optional<Equipo> equipoOptional = equipoRepository.findById(equipoId);
        if (equipoOptional.isPresent()) {
            List<Asignacion> asignaciones = asignacionRepository.findByEquipoId(equipoId);

            if (!asignaciones.isEmpty()) {
                for (Asignacion asignacion : asignaciones) {
                    List<Mantenimiento> mantenimientosForAsignacion = mantenimientoRepository.findByAsignacionId(asignacion.getId());
                    mantenimientosResult.addAll(mantenimientosForAsignacion);
                }
            }
        }

        return mantenimientosResult;
    }

}
