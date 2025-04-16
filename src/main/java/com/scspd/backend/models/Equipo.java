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
    private String numeroSerie;
    private String tipo;
    private String marca;
    private String color;
    private String modelo;
    private String procesador;
    private int ram;
    private double almacenamiento;
    private Puertos  puertos;
    private String estado;
    private String imagenUrl; // Guardar la URL de la imagen


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date fechaCompra;


    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
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
