package com.example.marianoperez.buscachinos_v_10.Utilidades;

/**
 * Created by mariano.perez on 01/03/2018.
 */

public class Utilidades {

    public static final  String DATABASE_NAME = "chinosBD";
    public static final  String TABLA_CHINOS = "tabla_chinos";
    public static final  String CAMPO_ID = "id";
    public static final  String CAMPO_NOMBRE = "nombre";
    public static final  String CAMPO_LONGITUD = "longitud";
    public static final  String CAMPO_LATITUD = "latitud";

    public static final String CREAR_TABLA_USUARIO =
            "CREATE TABLE " +TABLA_CHINOS+"  ("+CAMPO_ID+" STRING, "+CAMPO_NOMBRE+" STRING, "+CAMPO_LONGITUD+" FLOAT, "+CAMPO_LATITUD+" FLOAT)";


}
