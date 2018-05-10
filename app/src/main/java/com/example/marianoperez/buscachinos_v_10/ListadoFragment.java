package com.example.marianoperez.buscachinos_v_10;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.marianoperez.buscachinos_v_10.Entidades.ChinoSQLite;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link ListadoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class ListadoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "listaChinosSQL";
    //private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    //private String mParam1;
    //private String mParam2;

    ArrayList<ChinoSQLite> listaChinosList;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    RequestQueue rq;

    LatLng currentLatLng = null;

    RecyclerViewAdapter  adapter;

    int contador = 0;

    public ListadoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     //* @param param1 Parameter 1.
     //* @param param2 Parameter 2.
     * @return A new instance of fragment ListadoFragment.
     */
    // TODO: Rename and change types and number of parameters
    /*public static ListadoFragment newInstance(String param1, String param2) {
        ListadoFragment fragment = new ListadoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/
    public static ListadoFragment newInstance(ArrayList<ChinoSQLite> param1) {
        ListadoFragment fragment = new ListadoFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            listaChinosList = getArguments().getParcelableArrayList(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mFusedLocationProviderClient =
                getFusedLocationProviderClient(getActivity());

        rq = Volley.newRequestQueue(this.getContext());

        miUbicacion();


        //obtenerInformacion();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_listado, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new RecyclerViewAdapter(listaChinosList);
        //recyclerView.setAdapter(new RecyclerViewAdapter(listaChinosList));
        recyclerView.setAdapter(adapter);


        //Collections.sort(listaChinosList, new CustomComparator());

        return view;
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder{
        private CardView mCardView;
        private TextView mTextView;
        private TextView mTextView2;
        private TextView mTextView3;

        public RecyclerViewHolder(View itemView){
            super(itemView);
        }

        public RecyclerViewHolder(LayoutInflater inflater, ViewGroup container){
            super(inflater.inflate(R.layout.card_view,container,false));
            mCardView = itemView.findViewById(R.id.card_container);
            mTextView = itemView.findViewById(R.id.nombre);
            mTextView2 = itemView.findViewById(R.id.direccion);
            mTextView3 = itemView.findViewById(R.id.info);
        }
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

        private ArrayList<ChinoSQLite> mList;


        public RecyclerViewAdapter(ArrayList<ChinoSQLite> listaChinosList) {
            this.mList = listaChinosList;
        }

        @NonNull
        @Override
        public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());

            return new RecyclerViewHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

            //holder.mTextView.setText(mList.get(position).getNombre());
            //if(listaChinosList.get(listaChinosList.size()-1).getDistancia()!= null){
            //Collections.sort(listaChinosList, new CustomComparator());}

            if(listaChinosList.get(listaChinosList.size()-1).getDistancia()!= null){
            holder.mTextView.setText(mList.get(position).getNombre());
            holder.mTextView2.setText(mList.get(position).getDireccion());
            holder.mTextView3.setText("Estas a: " + mList.get(position).getDistancia() + " - " + mList.get(position).getTiempo());}
            else holder.mTextView.setText("CARGANDO...");

            /*if (mList.get(position).getDistancia() == null){
                holder.mTextView.setText(mList.get(position).getNombre());
                holder.mTextView2.setText("Desliza hacia abajo");
                holder.mTextView3.setText("para más información...");
            }else {
                //Collections.sort(listaChinosList, new CustomComparator());
                holder.mTextView.setText(mList.get(position).getNombre());
                holder.mTextView2.setText(mList.get(position).getDireccion());
                holder.mTextView3.setText("Estas a: " + mList.get(position).getDistancia() + " - " + mList.get(position).getTiempo());
            }*/
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

    }

        public void miUbicacion(){
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {

                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        Location location = task.getResult();
                        if (location != null) {
                            currentLatLng = new LatLng(location.getLatitude(),
                                    location.getLongitude());

                            obtenerInformacion();

                        } else {
                            Toast.makeText(getContext(), "Por Favor activa tu ubicación para poder mostrarla en el mapa", Toast.LENGTH_SHORT).show();
                            currentLatLng = new LatLng(40.4167278,
                                    -3.7033387);
                        }
                        }
                }});
            if(currentLatLng == null){ currentLatLng = new LatLng(40.4167278,
                    -3.7033387);}
        }

    public void sendjsonrequest(String url, final ChinoSQLite chino){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            chino.setDireccion(response.getString("destination_addresses"));
                            chino.setDistancia(response.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").
                                    getJSONObject(0).getJSONObject("distance").getString("text"));
                            chino.setTiempo(response.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").
                                    getJSONObject(0).getJSONObject("duration").getString("text"));

                            contador++;

                            if(contador == listaChinosList.size()){
                                buscanull();
                            }

                            //if(listaChinosList.get(listaChinosList.size()-1).getDistancia()!= null){
                            //    buscanull();
                            //}

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });
        rq.add(jsonObjectRequest);
    }

    public void buscanull(){
        for (int x = 0; x<listaChinosList.size(); x++){
            if(listaChinosList.get(x).getDistancia() == null){
                buscanull();
            }
                }
        Collections.sort(listaChinosList, new CustomComparator());
        adapter.notifyDataSetChanged();
    }


    private String getUrlDistance(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origins=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destinations=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        //Trip Mode
        String tripMode = "mode=walking";

        //Units
        String units=  "units=metric";

        //Language
        String language=  "language=es";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + tripMode + "&" + units + "&" + language ;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/distancematrix/" + output + "?" + parameters;


        return url;
    }

    public void obtenerInformacion(){

        //miUbicacion();

        for (int x = 0; x<listaChinosList.size(); x++){
            ChinoSQLite chino = listaChinosList.get(x);
        LatLng ubicacionCard = new LatLng(chino.getLatitud(),
                chino.getLongitud());
        String urlDistance = getUrlDistance(currentLatLng,ubicacionCard);
        sendjsonrequest(urlDistance, chino);}
    }

    public class CustomComparator implements Comparator<ChinoSQLite> {
        @Override
        public int compare(ChinoSQLite o1, ChinoSQLite o2) {

            String string = o1.getDistancia();
            String[] parts = string.split("k");
            String part1 = parts[0];
            part1 = part1.trim();
            part1 = part1.replace(',','.');

            String string2 = o2.getDistancia();
            String[] parts2 = string2.split("k");
            String part2 = parts2[0];
            part2 = part2.trim();
            part2 = part2.replace(',','.');


            Float f1 = Float.parseFloat(part1);
            Float f2 = Float.parseFloat(part2);

            return f1 < f2 ? -1
                    : f1 > f2 ? 1
                    : 0;

            //return o1.getDistancia().compareTo(o2.getDistancia());
        }

    }

}

