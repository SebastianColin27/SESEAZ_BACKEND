package com.scspd.backend.repositories;


import com.scspd.backend.models.Personal;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
public interface PersonalRepository extends MongoRepository<Personal, ObjectId> {
    //Para busquedas por nombre
    List<Personal> findByNombreContainingIgnoreCase(String nombre);

}
