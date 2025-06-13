package com.scspd.backend.services;

import com.scspd.backend.models.Asignacion;
import com.scspd.backend.models.Equipo;
import com.scspd.backend.models.Licencia;
import com.scspd.backend.models.Personal;
import com.scspd.backend.repositories.AsignacionRepository;
import com.scspd.backend.repositories.EquipoRepository;
import com.scspd.backend.repositories.LicenciaRepository;
import com.scspd.backend.repositories.PersonalRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AsignacionService {
    @Autowired
    private AsignacionRepository asignacionRepository;
    @Autowired
    private EquipoRepository equipoRepository;
    @Autowired
    private PersonalRepository personalRepository;
    @Autowired
    private LicenciaRepository licenciaRepository;



    public List<Asignacion> obtenerTodasLasAsignaciones() {
        return asignacionRepository.findAll();
    }


    public Optional<Asignacion> obtenerAsignacionPorId(ObjectId id) {
        return asignacionRepository.findById(id);
    }


    public Asignacion guardarAsignacion(Asignacion asignacion) {

        // Verificar y guardar equipo
        if (asignacion.getEquipo() != null) {
            if (asignacion.getEquipo().getId() == null) {
                asignacion.setEquipo(equipoRepository.save(asignacion.getEquipo()));
            } else {
                Optional<Equipo> equipoOptional = equipoRepository.findById(asignacion.getEquipo().getId());
                if (equipoOptional.isEmpty()) {
                    throw new EntityNotFoundException("No se encontró un equipo con el ID proporcionado");
                }
                asignacion.setEquipo(equipoOptional.get());
            }
        }


if (asignacion.getPersonal() != null) {
    Personal personal = asignacion.getPersonal();

    if (personal.getId() == null) {

        personal = personalRepository.save(personal);
    } else {
       // Si el ID no es nulo, verificar que exista en la base de datos
       Optional<Personal> personalOptional = personalRepository.findById(personal.getId());
        if (personalOptional.isEmpty()) {
            throw new EntityNotFoundException("No se encontró personal con el ID proporcionado: " + personal.getId());
        }
        personal = personalOptional.get();
   }

    asignacion.setPersonal(personal);
} else {
    asignacion.setPersonal(null);
}

        if (asignacion.getLicencias() != null && !asignacion.getLicencias().isEmpty()) {
            List<ObjectId> licenciasIds = asignacion.getLicencias().stream()
                    .filter(licencia -> licencia.getId() != null) //Filtra licencias con ID no nulo
                    .map(Licencia::getId)
                    .toList();

            if (!licenciasIds.isEmpty()) {
                List<Licencia> licenciasExistentes = licenciaRepository.findAllById(licenciasIds);

                if (licenciasExistentes.size() != licenciasIds.size()) {
                    throw new EntityNotFoundException("No se encontraron todas las licencias con los IDs proporcionados");
                }


                asignacion.setLicencias(licenciasExistentes);
            } else {

                asignacion.setLicencias(null);
            }
        }


        return asignacionRepository.save(asignacion);
    }


    public boolean existeAsignacionConId(ObjectId id) {
        return asignacionRepository.existsById(id);
    }


    public void eliminarAsignacionPorId(ObjectId id) {
        asignacionRepository.deleteById(id);
    }


    public List<Asignacion> obtenerAsignacionesPorEquipoId(ObjectId equipoId) {

        if (!equipoRepository.existsById(equipoId)) {
            throw new EntityNotFoundException("No se encontró el equipo con el ID proporcionado.");
        }
        return asignacionRepository.findByEquipoId(equipoId);
    }
    public List<Asignacion> obtenerAsignacionesPorPersonalId(ObjectId personalId) {
        return asignacionRepository.findByPersonalId(personalId);
    }


  public List<Asignacion> buscarAsignacionesPorNumeroSerie(String numeroSerie) {
      List<Equipo> equipos = equipoRepository.findByNumeroSerieContainingIgnoreCase(numeroSerie);

      if (equipos.isEmpty()) {
          return new ArrayList<>();
      }


      List<Asignacion> asignaciones = new ArrayList<>();
      for (Equipo equipo : equipos) {
          List<Asignacion> asignacionesDeEquipo = asignacionRepository.findByEquipo(equipo);
          asignaciones.addAll(asignacionesDeEquipo);
      }

      return asignaciones;
  }


}
