package com.scspd.backend.services;

import com.scspd.backend.models.Licencia;
import com.scspd.backend.repositories.LicenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.bson.types.ObjectId;
import java.util.List;
import java.util.Optional;

@Service
public class LicenciaService {
    @Autowired
    private LicenciaRepository licenciaRepository;


    public List<Licencia> obtenerTodasLasLicencias() {
        return licenciaRepository.findAll();
    }


    public Optional<Licencia> obtenerLicenciaPorId(ObjectId id) {


        return licenciaRepository.findById(id);
    }


    public Licencia guardarLicencia(Licencia licencia) {
        return licenciaRepository.save(licencia);
    }


    public boolean existeLicenciaConId(ObjectId id) {
        return licenciaRepository.existsById(id);
    }


    public void eliminarLicenciaPorId(ObjectId id) {
        licenciaRepository.deleteById(id);
    }


    public List<Licencia> buscarLicenciasPorNombre(String nombre) {
        return licenciaRepository.findByNombreLicenciaContainingIgnoreCase(nombre);


    }
}
