package com.scspd.backend.repositories;

import com.scspd.backend.models.Asignacion;
import com.scspd.backend.models.Equipo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
public interface AsignacionRepository extends MongoRepository<Asignacion, ObjectId>  {
    //Obtener el historial de personal y equipo


    List<Asignacion> findByEquipoId(ObjectId equipoId);

    List<Asignacion> findByPersonalId(ObjectId personalId);

    List<Asignacion> findByEquipo(Equipo equipo);

    List<Asignacion> findByEquipoNumeroSerieContainingIgnoreCase(String numeroSerie);

}
