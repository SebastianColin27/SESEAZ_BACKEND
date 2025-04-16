package com.scspd.backend.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

@Document(value = "licencias")
public class Licencia {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String tipoSw;
    private String nombreLicencia;
    private String numeroSerie;
    private int numeroUsuarios;
    private String subcripcion;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date fechaVencimiento;
    private String usuario;
    private String contrasena;
    private boolean esPermanente;
}
