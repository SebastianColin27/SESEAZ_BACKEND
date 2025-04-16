package com.scspd.backend.services;
import com.scspd.backend.models.Equipo;
import com.scspd.backend.repositories.EquipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.bson.types.ObjectId;
import java.util.List;
import java.util.Optional;

@Service
public class EquipoService {
    @Autowired
    private EquipoRepository equipoRepository;


    public List<Equipo> obtenerTodosLosEquipos() {
        return equipoRepository.findAll();
    }


    public Optional<Equipo> obtenerEquipoPorId(ObjectId id) {
        return equipoRepository.findById(id);
    }



    public Equipo crearEquipo(Equipo equipo) {


        return equipoRepository.save(equipo);


    }


    public Equipo actualizarEquipo(Equipo equipo) {


        return equipoRepository.save(equipo);


    }

    public void eliminarEquipo(ObjectId id) {
        equipoRepository.deleteById(id);
    }


    public boolean existeEquipoPorId(ObjectId id) {
        return equipoRepository.existsById(id);
    }


    public Equipo buscarEquipoPorNumeroDeSerie(String numeroSerie) {
        return equipoRepository.findByNumeroSerie(numeroSerie);
    }


    public List<Equipo> buscarEquiposPorMarca(String marca) {
        return equipoRepository.findByMarcaContainingIgnoreCase(marca);
    }

    public List<Equipo> buscarEquiposPorModelo(String modelo) {
        return equipoRepository.findByModeloContainingIgnoreCase(modelo);
    }



}
