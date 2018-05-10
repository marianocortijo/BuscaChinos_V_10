package com.example.marianoperez.buscachinos_v_10;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.webkit.WebView;

import com.example.marianoperez.buscachinos_v_10.Utilidades.Utilidades;

import javax.xml.transform.Result;

/**
 * Created by mariano.perez on 01/03/2018.
 */

public class ConexionSQL extends SQLiteOpenHelper {

    private static final  String DATABASE_NAME = "chinosBD";
    private static final  String TABLA_CHINOS = "tabla_chinos";

    private static final  String CAMPO_ID = "id";
    private static final  String CAMPO_NOMBRE = "nombre";
    private static final  String CAMPO_LONGITUD = "longitud";
    private static final  String CAMPO_LATITUD = "latitud";

    private static final String CREAR_TABLA_USUARIO =
            "CREATE TABLE " +TABLA_CHINOS+"  ("+CAMPO_ID+" STRING, "+CAMPO_NOMBRE+" STRING, "+CAMPO_LONGITUD+" FLOAT, "+CAMPO_LATITUD+" FLOAT)";


    public ConexionSQL(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREAR_TABLA_USUARIO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+TABLA_CHINOS);
        onCreate(db);
    }
}
