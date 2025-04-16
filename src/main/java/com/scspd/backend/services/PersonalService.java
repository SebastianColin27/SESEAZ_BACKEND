package com.scspd.backend.services;
import com.scspd.backend.models.Personal;
import com.scspd.backend.repositories.PersonalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.bson.types.ObjectId;
import java.util.List;
import java.util.Optional;

@Service
public class PersonalService {
    @Autowired
    private PersonalRepository personalRepository;


    public List<Personal> obtenerTodoElPersonal() {
        return personalRepository.findAll();
    }


    public Optional<Personal> obtenerPersonalPorId(ObjectId id) {
        return personalRepository.findById(id);
    }


    public Personal crearPersonal(Personal personal) {
        return personalRepository.save(personal);
    }


    public Personal actualizarPersonal(Personal personal) {

        return personalRepository.save(personal);
    }


    public void eliminarPersonal(ObjectId id) {
        personalRepository.deleteById(id);
    }


    public boolean existePersonalPorId(ObjectId id) {
        return personalRepository.existsById(id);
    }

    public List<Personal> buscarPersonalPorNombre(String nombre) {
        return personalRepository.findByNombreContainingIgnoreCase(nombre);


    }
}
