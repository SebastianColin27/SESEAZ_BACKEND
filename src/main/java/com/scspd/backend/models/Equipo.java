package com.scspd.backend.models;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.Column;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document("equipos")
public class Equipo {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    @Column(unique = true)
    private String numeroSerie;
    private String numeroInventario;
    private String tipo;
    private String marca;
    private String color;
    private String modelo;
    private String procesador;
    private int ram;
    private double HDD;
    private double SDD;
    private Puertos  puertos;
    private String estado;

    private String imagenGridFsId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date fechaCompra;


    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Puertos {
        private int usb;
        private int ethernet;
        private int hdmi;
        private int tipoC;
        private int jack_35;
        private int vga;
        private int sd;
    }
}
