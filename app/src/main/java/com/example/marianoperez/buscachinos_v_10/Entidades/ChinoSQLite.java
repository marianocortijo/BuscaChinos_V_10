package com.example.marianoperez.buscachinos_v_10.Entidades;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mariano.perez on 01/03/2018.
 */

public class ChinoSQLite implements Parcelable {
    private String id;
    private String nombre;
    private Float longitud;
    private Float latitud;
    private String direccion;
    private String distancia;
    private String tiempo;


    public ChinoSQLite() {
        this.id = id;
        this.nombre = nombre;
        this.longitud = longitud;
        this.latitud = latitud;
        this.direccion = direccion;
        this.distancia = distancia;
        this.tiempo = tiempo;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Float getLongitud() {
        return longitud;
    }

    public Float getLatitud() {
        return latitud;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getDistancia() {
        return distancia;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setLongitud(Float longitud) {
        this.longitud = longitud;
    }

    public void setLatitud(Float latitud) {
        this.latitud = latitud;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setDistancia(String distancia) {
        this.distancia = distancia;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.nombre);
        dest.writeValue(this.longitud);
        dest.writeValue(this.latitud);
        dest.writeValue(this.direccion);
        dest.writeValue(this.distancia);
        dest.writeValue(this.tiempo);
    }

    protected ChinoSQLite(Parcel in) {
        this.id = in.readString();
        this.nombre = in.readString();
        this.longitud = (Float) in.readValue(Float.class.getClassLoader());
        this.latitud = (Float) in.readValue(Float.class.getClassLoader());
        this.direccion = in.readString();
        this.distancia = in.readString();
        this.tiempo = in.readString();
    }

    public static final Parcelable.Creator<ChinoSQLite> CREATOR = new Parcelable.Creator<ChinoSQLite>() {
        @Override
        public ChinoSQLite createFromParcel(Parcel source) {
            return new ChinoSQLite(source);
        }

        @Override
        public ChinoSQLite[] newArray(int size) {
            return new ChinoSQLite[size];
        }
    };

}