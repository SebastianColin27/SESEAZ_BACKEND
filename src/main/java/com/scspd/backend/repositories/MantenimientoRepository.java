package com.scspd.backend.repositories;

import com.scspd.backend.models.Equipo;
import com.scspd.backend.models.Mantenimiento;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MantenimientoRepository extends MongoRepository<Mantenimiento, ObjectId> {


    List<Mantenimiento> findByEquipoId(ObjectId equipoId);


    List<Mantenimiento> findByEquipoNumeroSerieContainingIgnoreCase(String numeroSerie);
    List<Mantenimiento> findByEquipo(Equipo equipo);

}
