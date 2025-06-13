package com.scspd.backend.repositories;
import com.scspd.backend.models.Licencia;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
public interface LicenciaRepository extends MongoRepository<Licencia, ObjectId> {

    //Buscar licencia por nombre
    List<Licencia> findByNombreLicenciaContainingIgnoreCase(String nombreLicencia);
}
