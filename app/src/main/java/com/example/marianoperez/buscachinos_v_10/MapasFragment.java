package com.example.marianoperez.buscachinos_v_10;


import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.marianoperez.buscachinos_v_10.Entidades.ChinoSQLite;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import permissions.dispatcher.NeedsPermission;

import permissions.dispatcher.RuntimePermissions;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@RuntimePermissions
public class MapasFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "listaChinosSQL";
    //private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    //private ArrayList<ChinoSQLite> mParam1;
    //private String mParam2;

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    ArrayList<ChinoSQLite> listaChinosMaps;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    LatLng currentLatLng;
    LatLng posicion;

    GoogleApiClient mGoogleApiClient;

    Polyline polylineLast = null;

    String Distancia;
    String Tiempo;

    RequestQueue rq;


    public MapasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     *               //* @param param2 Parameter 2.
     * @return A new instance of fragment MapasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapasFragment newInstance(ArrayList<ChinoSQLite> param1) {
        MapasFragment fragment = new MapasFragment();
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
            listaChinosMaps = getArguments().getParcelableArrayList(ARG_PARAM1);
            ///mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mFusedLocationProviderClient =
                getFusedLocationProviderClient(getActivity());

        rq = Volley.newRequestQueue(this.getContext());

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_mapas, container, false);

        View v = inflater.inflate(R.layout.fragment_mapas, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        MapasFragmentPermissionsDispatcher.miUbicacionWithPermissionCheck(MapasFragment.this);

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);


        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        float zoomlevel = 16;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,zoomlevel));*/

        addMarker(listaChinosMaps);

        mMap.setOnMarkerClickListener(this);

        mapaSinConexion();

    }

    boolean mapaSinConexion() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            Toast.makeText(getContext(), "Por favor permite el acceso tu ubicación para poder mostrarla en el mapa", Toast.LENGTH_SHORT).show();
            currentLatLng = new LatLng(40.4167278,
                    -3.7033387);

            // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
            mMap.setMinZoomPreference(5.0f);
            //mMap.setMaxZoomPreference(14.0f);
            mMap.setBuildingsEnabled(true);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(currentLatLng)      // Sets the center of the map to Mountain View
                    .zoom(15)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            return true;
        }
        return false;
    }

    @NeedsPermission({android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION})
    void miUbicacion() {
        // Enabling MyLocation Layer of Google Map
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
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
        getDeviceLocation();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MapasFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    public void addMarker(ArrayList<ChinoSQLite> arrayListChinoSQLite) {

        for (int i = 0; i < arrayListChinoSQLite.size(); i++) {
            // Add a marker in Sydney and move the camera
            String nombre = arrayListChinoSQLite.get(i).getNombre();
            LatLng posicion = new LatLng(arrayListChinoSQLite.get(i).getLatitud(), arrayListChinoSQLite.get(i).getLongitud());
            mMap.addMarker(new MarkerOptions().position(posicion).title("Pulsa para más info.").
                    icon(BitmapDescriptorFactory.fromResource(R.drawable.chineseemoticon)));
            //float zoomlevel = 10;
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicion, zoomlevel));
        }
    }

    private void getDeviceLocation() {
        try {
            if (mapaSinConexion() == true) {
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
                        } else {
                            Toast.makeText(getContext(), "Por favor activa tu ubicación para poder mostrarla en el mapa", Toast.LENGTH_SHORT).show();
                            currentLatLng = new LatLng(40.4167278,
                                    -3.7033387);
                        }

                        //CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLatLng,15);
                        //mMap.moveCamera(update);

                        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
                        mMap.setMinZoomPreference(5.0f);
                        //mMap.setMaxZoomPreference(14.0f);
                        mMap.setBuildingsEnabled(true);
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(currentLatLng)      // Sets the center of the map to Mountain View
                                .zoom(16)                   // Sets the zoom
                                .bearing(0)                // Sets the orientation of the camera to east
                                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                                .build();                   // Creates a CameraPosition from the builder
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }

                }
            });

        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /** Called when the user clicks a marker. */

    /**
     * Called when the user clicks a marker.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return false;
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
                    } else {
                        Toast.makeText(getContext(), "Por favor activa tu ubicación para poder mostrarla en el mapa", Toast.LENGTH_SHORT).show();
                        currentLatLng = new LatLng(40.4167278,
                                -3.7033387);
                    }
                }
            }});



        posicion = marker.getPosition();
        // Getting URL to the Google Directions API
        String url = getUrl(currentLatLng, posicion);
        Log.d("onMapClick", url.toString());
        FetchUrl FetchUrl = new FetchUrl();

        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);


       String urlDistance = getUrlDistance(currentLatLng,posicion);
        Log.d("onMapClick", urlDistance.toString());

        //FetchUrlDistance FetchUrlDistance = new FetchUrlDistance();

        // Start downloading json data from Google Directions API
        //FetchUrlDistance.execute(urlDistance);

         sendjsonrequest(urlDistance, marker);

        //if(Distancia != null && Tiempo != null) {
        //    marker.setTitle("Estas a: " + Distancia + " - " + Tiempo);
        //} else marker.setTitle("Info no disponible. Inténtalo de nuevo más tarde");

        return false;
    }



    public void sendjsonrequest(String url, final Marker marker){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Distancia = response.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").
                                    getJSONObject(0).getJSONObject("distance").getString("text");
                            Tiempo = response.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").
                                    getJSONObject(0).getJSONObject("duration").getString("text");
                            marker.setTitle("Estas a: " + Distancia + " - " + Tiempo);
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


    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        //Trip Mode
        String tripMode = "mode=walking";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + tripMode ;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }
        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.YELLOW);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");


            }


            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                Polyline polyline = mMap.addPolyline(lineOptions);

                if (polylineLast == null) {
                    polylineLast = polyline;
                }
                else{
                    polylineLast.remove();
                    polylineLast = polyline;
                }

            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this.getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
}