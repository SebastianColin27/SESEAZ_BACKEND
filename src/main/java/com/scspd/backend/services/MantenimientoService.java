package com.scspd.backend.services;

import com.scspd.backend.models.Equipo;
import com.scspd.backend.models.Mantenimiento;
import com.scspd.backend.repositories.AsignacionRepository;
import com.scspd.backend.repositories.EquipoRepository;
import com.scspd.backend.repositories.MantenimientoRepository;
import com.scspd.backend.repositories.PersonalRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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


        if (mantenimiento.getEquipo() != null) {
            if (mantenimiento.getEquipo().getId() == null) {
                throw new IllegalArgumentException("Se requiere un ID de Equipo válido para registrar el mantenimiento.");
            } else {
                Optional<Equipo> equipoOptional = equipoRepository.findById(mantenimiento.getEquipo().getId());
                if (equipoOptional.isEmpty()) {
                    throw new EntityNotFoundException("No se encontró el Equipo con el ID proporcionado.");
                }
                mantenimiento.setEquipo(equipoOptional.get());
            }
        } else {
            throw new IllegalArgumentException("Se requiere un Equipo para registrar el mantenimiento.");
        }

        return mantenimientoRepository.save(mantenimiento);
    }



    public boolean existeMantenimientoPorId(ObjectId id) {
        return mantenimientoRepository.existsById(id);
    }

    public void eliminarMantenimiento(ObjectId id) {
        mantenimientoRepository.deleteById(id);
    }


    public List<Mantenimiento> obtenerMantenimientosPorEquipoId(ObjectId equipoId) {

        if (!equipoRepository.existsById(equipoId)) {
            throw new EntityNotFoundException("No se encontró el equipo con el ID proporcionado.");
        }
        return mantenimientoRepository.findByEquipoId(equipoId);
    }



    public List<Mantenimiento> buscarMantenimientosPorNumeroSerie(String numeroSerie) {
        List<Equipo> equipos = equipoRepository.findByNumeroSerieContainingIgnoreCase(numeroSerie);

        if (equipos.isEmpty()) {
            return new ArrayList<>();
        }


        List<Mantenimiento> mantenimientos = new ArrayList<>();
        for (Equipo equipo : equipos) {
            List<Mantenimiento> mantenimientoDeEquipo = mantenimientoRepository.findByEquipo(equipo);
            mantenimientos.addAll(mantenimientoDeEquipo);
        }

        return mantenimientos;
    }




}
