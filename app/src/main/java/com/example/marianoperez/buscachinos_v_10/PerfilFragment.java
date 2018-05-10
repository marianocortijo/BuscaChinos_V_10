package com.example.marianoperez.buscachinos_v_10;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class PerfilFragment extends Fragment implements View.OnClickListener {


    private static final String TAG = "TAG";

    int flagPantalla = 0;

    @BindView(R.id.editTextNuevoPerfil) EditText editTextNuevoPerfil;
    @BindView(R.id.textViewPerfil) TextView textViewPerfil;

    @BindView(R.id.btnCambioNombrePerfil) Button btnCambioNombrePerfil;
    @BindView(R.id.btnCambioPWD) Button btnCambioPWD;
    //@BindView(R.id.btnCambioFoto) Button btnCambioFoto;
    @BindView(R.id.btnDeleteUser) Button btnDeleteUser;
    @BindView(R.id.btnLogOut) Button btnLogOut;
    @BindView(R.id.btnBack) Button btnBack;


    // [START declare_auth]
    private FirebaseAuth fAutenticador;
    // [END declare_auth]

    //private ProgressDialog progressDialog = new ProgressDialog(this);

    private Unbinder unbinder;

    public PerfilFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        unbinder = ButterKnife.bind(this,view);

        btnCambioNombrePerfil.setOnClickListener(this);
        btnCambioPWD.setOnClickListener(this);
        //btnCambioFoto.setOnClickListener(this);
        btnDeleteUser.setOnClickListener(this);
        btnLogOut.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        editTextNuevoPerfil.setVisibility(View.GONE);
        btnBack.setVisibility(View.GONE);
        //btnCambioFoto.setVisibility(View.GONE);

        // [START initialize_auth]
        fAutenticador = FirebaseAuth.getInstance();
        // [END initialize_auth]

        getInfo();

        return view;
    }


    private void getInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
                String providerId = profile.getProviderId();

                // UID specific to the provider
                String uid = profile.getUid();

                // Name, email address, and profile photo Url
                String name = profile.getDisplayName();
                textViewPerfil.setText(profile.getDisplayName());
                String email = profile.getEmail();
                Uri photoUrl = profile.getPhotoUrl();
            }
        }
    }

    private void actInfo(String apodo){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(apodo)
                //.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });
    }

    private void signOut() {
        fAutenticador.signOut();
        startActivity(new Intent(PerfilFragment.this.getContext(), Main2Activity.class));
    }

    private void deleteUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User account deleted.");
                            Toast.makeText(PerfilFragment.this.getContext(), "Usuario eliminado satisfactoriamente",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PerfilFragment.this.getContext(), Main2Activity.class));
                        }
                    }
                });
    }

    private void reserPWD(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String emailAddress = user.getEmail();

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });
        Toast.makeText(PerfilFragment.this.getContext(), "Enviado correo de reinicio de contraseña a: " + user.getEmail(),
                Toast.LENGTH_SHORT).show();
    }


    private void updateUI(FirebaseUser user) {

        if (flagPantalla == 1){
            //btnCambioFoto.setVisibility(View.GONE);
            btnCambioPWD.setVisibility(View.GONE);
            btnDeleteUser.setVisibility(View.GONE);
            btnLogOut.setVisibility(View.GONE);
            btnBack.setVisibility(View.VISIBLE);
            editTextNuevoPerfil.setVisibility(View.VISIBLE);
            btnBack.setVisibility(View.VISIBLE);


        } else {
            //btnCambioFoto.setVisibility(View.GONE);
            btnCambioPWD.setVisibility(View.VISIBLE);
            btnDeleteUser.setVisibility(View.VISIBLE);
            btnLogOut.setVisibility(View.VISIBLE);
            btnBack.setVisibility(View.GONE);
            editTextNuevoPerfil.setVisibility(View.GONE);

        }

    }


        @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.btnLogOut) {
            signOut();
        } else if (i == R.id.btnDeleteUser) {
            deleteUser();
       }  else if (i == R.id.btnCambioNombrePerfil) {
            if(flagPantalla == 0){
                flagPantalla = 1;
                updateUI(null);
            }else{
                String apodo = editTextNuevoPerfil.getText().toString();
                String cmp ="";
                if(apodo.equals(cmp)){
                    Toast.makeText(PerfilFragment.this.getContext(), "Nombre no válido.",
                            Toast.LENGTH_SHORT).show();}
                else {
                    actInfo(apodo);
                    flagPantalla = 0;
                    getInfo();
                    updateUI(null);
                    Toast.makeText(PerfilFragment.this.getContext(), "El cambio será visible en el próximo acceso al perfil.",
                            Toast.LENGTH_SHORT).show();
                }
            }

        }   else if (i == R.id.btnCambioPWD) {
        reserPWD();
        }   else if (i == R.id.btnBack) {
            //btnCambioFoto.setVisibility(View.GONE);
            btnCambioPWD.setVisibility(View.VISIBLE);
            btnDeleteUser.setVisibility(View.VISIBLE);
            editTextNuevoPerfil.setVisibility(View.GONE);
            btnLogOut.setVisibility(View.VISIBLE);
            btnBack.setVisibility(View.GONE);
            flagPantalla = 0;
        }
    }


    public static Fragment newInstance() {
        PerfilFragment fragment = new PerfilFragment();
        return fragment;
    }
}
