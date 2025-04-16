package com.scspd.backend.repositories;

import com.scspd.backend.models.Equipo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
public interface EquipoRepository extends MongoRepository<Equipo, ObjectId>{
    // Búsqueda exacta por número de serie (suponiendo que es único)
    Equipo findByNumeroSerie(String numeroSerie);

    // Búsqueda parcial e insensible a mayúsculas/minúsculas por marca
    List<Equipo> findByMarcaContainingIgnoreCase(String marca);

    // Búsqueda parcial e insensible a mayúsculas/minúsculas por modelo
    List<Equipo> findByModeloContainingIgnoreCase(String modelo);
}
