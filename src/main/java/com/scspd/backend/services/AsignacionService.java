package com.scspd.backend.services;
import com.scspd.backend.models.Asignacion;
import com.scspd.backend.models.Equipo;
import com.scspd.backend.models.Licencia;
import com.scspd.backend.models.Personal;
import com.scspd.backend.repositories.AsignacionRepository;
import com.scspd.backend.repositories.EquipoRepository;
import com.scspd.backend.repositories.LicenciaRepository;
import com.scspd.backend.repositories.PersonalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;

@Service
public class AsignacionService {
    @Autowired
    private AsignacionRepository asignacionRepository;
    @Autowired
    private EquipoRepository equipoRepository;
    @Autowired
    private PersonalRepository personalRepository;  //Inyecta el repositorio de personal
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

//  Verificar y guardar personal (LISTA DE PERSONAL)
        if (asignacion.getPersonal() != null && !asignacion.getPersonal().isEmpty()) {
            List<Personal> personalList = new ArrayList<>();
            for (Personal personal : asignacion.getPersonal()) {
                if (personal.getId() == null) {
                    // Si el ID es nulo, es una persona nueva que hay que persistir
                    personalList.add(personalRepository.save(personal));
                } else {
                    // Si el ID no es nulo, hay que verificar que exista en la base de datos
                    Optional<Personal> personalOptional = personalRepository.findById(personal.getId());
                    if (personalOptional.isEmpty()) {
                        throw new EntityNotFoundException("No se encontró personal con el ID proporcionado: " + personal.getId());
                    }
                    personalList.add(personalOptional.get()); // Add the found Personal object
                }
            }
            asignacion.setPersonal(personalList); // Set the list of processed Personal objects
        } else {
            asignacion.setPersonal(null); // Handle the case where personal list is null or empty, setting it to null in Asignacion
        }

        // Verificar y guardar licencias
        if (asignacion.getLicencias() != null && !asignacion.getLicencias().isEmpty()) {
            List<ObjectId> licenciasIds = asignacion.getLicencias().stream()
                    .filter(licencia -> licencia.getId() != null) //Filtra licencias con ID no nulo
                    .map(Licencia::getId)
                    .toList();

            // Busca las licencias existentes solo si hay IDs válidos
            if (!licenciasIds.isEmpty()) {
                List<Licencia> licenciasExistentes = licenciaRepository.findAllById(licenciasIds);

                // Si no se encontraron todas las licencias, lanza la excepción
                if (licenciasExistentes.size() != licenciasIds.size()) {
                    throw new EntityNotFoundException("No se encontraron todas las licencias con los IDs proporcionados");
                }

                // Establece las licencias existentes en la asignación
                asignacion.setLicencias(licenciasExistentes);
            } else {
                // Si todos los IDs de licencia son nulos, puedes lanzar una excepción o simplemente ignorar la lista de licencias
                // En este caso, simplemente estableceremos la lista de licencias en null para evitar problemas
                asignacion.setLicencias(null);
            }
        }

        // ... (Lógica para manejar la fecha de fin de asignación) ...

        return asignacionRepository.save(asignacion);
    }


    public boolean existeAsignacionConId(ObjectId id) {
        return asignacionRepository.existsById(id);
    }


    public void eliminarAsignacionPorId(ObjectId id) {
        asignacionRepository.deleteById(id);
    }


    public List<Asignacion> obtenerAsignacionesPorPersona(ObjectId personalId) {
        return asignacionRepository.findByPersonalId(personalId);
    }






}
