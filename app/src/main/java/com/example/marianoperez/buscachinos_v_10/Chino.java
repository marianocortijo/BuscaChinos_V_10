package com.example.marianoperez.buscachinos_v_10;

import java.lang.ref.SoftReference;

/**
 * Created by mariano.perez on 23/02/2018.
 */



public class Chino {

    String nombre;
    Float longitud;
    Float latitud;

    public  Chino(){

    }

    public Chino(String nombre, Float longitud, Float latitud) {
        this.nombre = nombre;
        this.longitud = longitud;
        this.latitud = latitud;
    }

    public String getNombre() {
        return nombre;
    }

    public Float getLongitud() {
        return longitud;
    }

    public Float getLatitud() {return latitud; }
}