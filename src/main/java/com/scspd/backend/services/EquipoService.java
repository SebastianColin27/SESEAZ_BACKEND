package com.scspd.backend.services;
import com.lowagie.text.Row;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.scspd.backend.models.Equipo;
import com.scspd.backend.repositories.EquipoRepository;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.bson.types.ObjectId;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
public class EquipoService {
    @Autowired
    private EquipoRepository equipoRepository;
    @Autowired
    private GridFsTemplate gridFsTemplate;


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


    public List<Equipo> buscarEquipoPorNumeroDeSerie(String numeroSerie) {
        return equipoRepository.findByNumeroSerieContainingIgnoreCase(numeroSerie);
    }




    public List<Equipo> buscarEquiposPorMarca(String marca) {
        return equipoRepository.findByMarcaContainingIgnoreCase(marca);
    }

    public List<Equipo> buscarEquiposPorModelo(String modelo) {
        return equipoRepository.findByModeloContainingIgnoreCase(modelo);
    }
/*--*/
    private ObjectId getObjectId(String id) {
        try {
            return new ObjectId(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ObjectId string: " + id, e);
        }
    }

    public Equipo guardarImagenEquipo(String equipoId, MultipartFile file) throws IOException {
        ObjectId objectId = getObjectId(equipoId);
        Optional<Equipo> equipoOptional = equipoRepository.findById(objectId);

        if (!equipoOptional.isPresent()) {
            throw new IllegalArgumentException("Equipo not found with ID: " + equipoId);
        }

        Equipo equipo = equipoOptional.get();

        // If an old image exists, delete it from GridFS
        if (equipo.getImagenGridFsId() != null) {
            try {
                gridFsTemplate.delete(new Query(Criteria.where("_id").is(new ObjectId(equipo.getImagenGridFsId()))));
                System.out.println("Deleted old image: " + equipo.getImagenGridFsId());
            } catch (Exception e) {
                System.err.println("Error deleting old image " + equipo.getImagenGridFsId() + ": " + e.getMessage());
                // Decide if you want to throw here or just log and continue
            }
        }

        // Store the new file in GridFS
        ObjectId fileId = gridFsTemplate.store(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType() // Store content type
        );

        // Update the Equipo document with the new GridFS file ID
        equipo.setImagenGridFsId(fileId.toHexString());

        // Save the updated Equipo document
        return equipoRepository.save(equipo);
    }

    public GridFSFile obtenerImagenGridFsFile(String equipoId) {
        ObjectId objectId = getObjectId(equipoId);
        Optional<Equipo> equipoOptional = equipoRepository.findById(objectId);

        if (!equipoOptional.isPresent() || equipoOptional.get().getImagenGridFsId() == null) {
            return null; // Equipo not found or no image associated
        }

        String gridFsId = equipoOptional.get().getImagenGridFsId();
        try {
            return gridFsTemplate.findOne(new Query(Criteria.where("_id").is(new ObjectId(gridFsId))));
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid GridFS ID stored for equipo " + equipoId + ": " + gridFsId);
            return null;
        }
    }

    public InputStream obtenerImagenInputStream(String equipoId) {
        GridFSFile gridFSFile = obtenerImagenGridFsFile(equipoId);
        if (gridFSFile != null) {
            // You can use GridFsTemplate.getResource or GridFSBucket
            // Option 1: Using GridFsTemplate.getResource
            GridFsResource resource = gridFsTemplate.getResource(gridFSFile);
            try {
                return resource.getInputStream();
            } catch (IOException e) {
                System.err.println("Error getting InputStream for GridFS file " + gridFSFile.getId() + ": " + e.getMessage());
                return null;
            }
            // Option 2: Using GridFSBucket (requires @Autowired GridFSBucket)
            // return gridFsBucket.openDownloadStream(gridFSFile.getObjectId());
        }
        return null;
    }

    public String obtenerImagenContentType(String equipoId) {
        GridFSFile gridFSFile = obtenerImagenGridFsFile(equipoId);
        return gridFSFile != null ? gridFSFile.getMetadata().getString("_contentType") : null;
    }

    public GridFsResource getImagenResourceByGridFsId(String gridFsId) {
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(new ObjectId(gridFsId))));
        return file != null ? gridFsTemplate.getResource(file) : null;
    }

    public List<Equipo> obtenerEquiposPorEstado(String estado) {
        return equipoRepository.findByEstadoIgnoreCase(estado);
    }

/*excel*/

}
