package com.example.marianoperez.buscachinos_v_10;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marianoperez.buscachinos_v_10.Entidades.ChinoSQLite;
import com.example.marianoperez.buscachinos_v_10.Utilidades.Utilidades;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Main5Activity extends AppCompatActivity {


    DatabaseReference databaseChinosFirebase;
    List<Chino> chinoList;

    ArrayList<ChinoSQLite> listaChinosSQL;

    ConexionSQL myDB;

    int idFragment = R.id.navigation_maps;
    int idFragmentCargado = 0;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            idFragmentCargado = idFragment;

            idFragment = item.getItemId();

            if (idFragmentCargado != idFragment) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                switch (item.getItemId()) {
                    case R.id.navigation_maps:
                        transaction.replace(R.id.content, MapasFragment.newInstance(listaChinosSQL)).commit();
                        return true;
                    case R.id.navigation_list:
                        transaction.replace(R.id.content, ListadoFragment.newInstance(listaChinosSQL)).commit();
                        return true;
                    case R.id.navigation_perfil:
                        transaction.replace(R.id.content, PerfilFragment.newInstance()).commit();
                        return true;
                }

            }

            return false;

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        setContentView(R.layout.activity_main5);

        databaseChinosFirebase = FirebaseDatabase.getInstance().getReference("CHINOS");
        chinoList = new ArrayList<>();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }

    @Override
    protected void onStart() {
        super.onStart();

        ConexionSQL connection = new ConexionSQL(this);

        final SQLiteDatabase db = connection.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS tabla_chinos");
        db.execSQL(Utilidades.CREAR_TABLA_USUARIO);


        databaseChinosFirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chinoList.clear();
                for (DataSnapshot chinoSnapshot : dataSnapshot.getChildren()) {
                    Chino chino = chinoSnapshot.getValue(Chino.class);
                    chinoList.add(chino);

                    ContentValues values = new ContentValues();
                    values.put(Utilidades.CAMPO_ID, chinoSnapshot.getKey());
                    values.put(Utilidades.CAMPO_NOMBRE, chino.nombre);
                    values.put(Utilidades.CAMPO_LONGITUD, chino.longitud);
                    values.put(Utilidades.CAMPO_LATITUD, chino.latitud);

                    //Long idResult = db.insert(Utilidades.TABLA_CHINOS, null, values);
                    db.insert(Utilidades.TABLA_CHINOS, null, values);
                }
                db.close();
                consultarListaChinosSQL();

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.content, MapasFragment.newInstance(listaChinosSQL)).commit();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void consultarListaChinosSQL() {

        ConexionSQL connection = new ConexionSQL(this);

        SQLiteDatabase db = connection.getReadableDatabase();

        ChinoSQLite chino = null;
        listaChinosSQL = new ArrayList<ChinoSQLite>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidades.TABLA_CHINOS, null);
        listaChinosSQL.clear();
        while (cursor.moveToNext()) {
            chino = new ChinoSQLite();
            chino.setId(cursor.getString(0));
            chino.setNombre(cursor.getString(1));
            chino.setLongitud(cursor.getFloat(2));
            chino.setLatitud(cursor.getFloat(3));

            listaChinosSQL.add(chino);
        }

        db.close();

    }

    @Override
    public void onBackPressed() {

    }
}
