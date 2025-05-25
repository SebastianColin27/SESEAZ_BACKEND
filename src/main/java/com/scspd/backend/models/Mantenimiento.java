package com.scspd.backend.models;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document("mantenimientos")
public class Mantenimiento {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date fecha;
    private String actividadRealizada;
    private String evidencia;

    @DBRef(lazy = false)
    private Personal personal;
    @DBRef(lazy = false)
    private Equipo equipo;
}
