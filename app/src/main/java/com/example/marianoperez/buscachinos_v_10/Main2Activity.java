package com.example.marianoperez.buscachinos_v_10;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "EmailPassword";

    int flagPantalla = 0;

    @BindView(R.id.editTextRegEmailGoogle) EditText editTextRegEmailGoogle;
    @BindView(R.id.editTextRegPwdGoogle) EditText editTextRegPwdGoogle;
    //@BindView(R.id.textViewDetalle) TextView textViewDetalle;
    @BindView(R.id.textViewEstado) TextView textViewEstado;

    @BindView(R.id.buttonLogInGoogle) Button buttonLogInGoogle;
    @BindView(R.id.buttonForgotPWDGoogle) Button buttonForgotPWDGoogle;
    @BindView(R.id.buttonRegGoogle) Button buttonRegGoogle;
    @BindView(R.id.buttonEnvioEmailGoogle) Button buttonEnvioEmailGoogle;
    @BindView(R.id.btnBack) Button btnBack;

    // [START declare_auth]
    private FirebaseAuth fAutenticador;
    // [END declare_auth]

    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        setContentView(R.layout.activity_main2);

        ButterKnife.bind(this);


        buttonLogInGoogle.setOnClickListener(this);
        buttonForgotPWDGoogle.setOnClickListener(this);
        buttonRegGoogle.setOnClickListener(this);
        buttonEnvioEmailGoogle.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        // [START initialize_auth]
        fAutenticador = FirebaseAuth.getInstance();
        // [END initialize_auth]

        progressDialog = new ProgressDialog(this);

    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = fAutenticador.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]


    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validarRegistro()) {
            return;
        }

        // [START create_user_with_email]

        progressDialog.setMessage("Registrando Nuevo Usuario");
        progressDialog.show();

        fAutenticador.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = fAutenticador.getCurrentUser();
                            sendEmailVerification();

                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Main2Activity.this, "Registro erroneo, por favor inténtalo de nuevo.",
                                    Toast.LENGTH_SHORT).show();
                            flagPantalla = 0;
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        progressDialog.hide();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]

}

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validarRegistro()) {
            return;
        }

        progressDialog.setMessage("Logueando Usuario");
        progressDialog.show();


        fAutenticador.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = fAutenticador.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Main2Activity.this, "Error al iniciar sesión, por favor inténtelo de nuevo.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            textViewEstado.setText(R.string.auth_failed);
                        }
                        progressDialog.hide();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }


    private void forgotPWD() {
        String email = editTextRegEmailGoogle.getText().toString();
        fAutenticador.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Main2Activity.this, "Email enviado correctamente, por favor revise su bandeja de entrada.",
                                    Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Email enviado.");
                        }else {
                            Toast.makeText(Main2Activity.this, "Email no encontrado, por favor inténtelo de nuevo.",
                                    Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Email no enviado.");
                        }
                    }
                });
            updateUI(null);
    }



    private void sendEmailVerification() {

        // Disable button
        //buttonSentEmailGoogle.setEnabled(false);

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = fAutenticador.getCurrentUser();
        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button

                        //buttonSentEmailGoogle.setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(Main2Activity.this,
                                    "Email de verificación enviado a: " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(Main2Activity.this,
                                    "Error al enviar el email de verificación.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }


    private boolean validarRegistro() {
        boolean valid = true;

        String email = editTextRegEmailGoogle.getText().toString();
        if (TextUtils.isEmpty(email)) {
            editTextRegEmailGoogle.setError("Obligatorio.");
            Toast.makeText(this, "Por favor rellena el campo email de manera correcta", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            editTextRegEmailGoogle.setError(null);
        }

        String password = editTextRegPwdGoogle.getText().toString();
        if (TextUtils.isEmpty(password) || (password.length() < 6)) {
            editTextRegPwdGoogle.setError("Obligatorio.");
            Toast.makeText(this, "La contraseña ha de tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            editTextRegPwdGoogle.setError(null);
        }
        return valid;
    }


    private void updateUI(FirebaseUser user) {
        ButterKnife.bind(this);
        boolean emailVerified;

        if(user == null){
            emailVerified = false;
            }
        else {
        emailVerified = user.isEmailVerified();
        }
        //progressDialog.hide();
        if (user != null && !emailVerified) {
            progressDialog.setMessage("Usuario registrado con éxito, por favor valide su correo electrónico para acceder a la aplicación");

            //textViewEstado.setText(getString(R.string.emailpassword_status_fmt, user.getEmail(), user.isEmailVerified()));
            textViewEstado.setText("A la espera de la verificación del email, cuando lo valide inicie sesión");
            //textViewDetalle.setText(getString(R.string.firebase_status_fmt, user.getUid()));
            //findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
            //findViewById(R.id.email_password_fields).setVisibility(View.GONE);
            buttonRegGoogle.setVisibility(View.GONE);
            buttonLogInGoogle.setVisibility(View.VISIBLE);
            editTextRegEmailGoogle.setVisibility(View.VISIBLE);
            editTextRegPwdGoogle.setVisibility(View.VISIBLE);

            //findViewById(R.id.signed_in_buttons).setVisibility(View.VISIBLE);

            buttonForgotPWDGoogle.setVisibility(View.GONE);

            buttonEnvioEmailGoogle.setVisibility(View.GONE);

            btnBack.setVisibility(View.VISIBLE);

            //findViewById(R.id.verify_email_button).setEnabled(!user.isEmailVerified());

            //buttonSentEmailGoogle.setEnabled(!user.isEmailVerified());
        }else if  (user != null && emailVerified) {
                startActivity(new Intent(Main2Activity.this,Main5Activity.class));
        }
        else if(user == null && flagPantalla == 1){

            editTextRegEmailGoogle.setVisibility(View.VISIBLE);
            editTextRegPwdGoogle.setVisibility(View.GONE);

            buttonForgotPWDGoogle.setVisibility(View.GONE);
            buttonEnvioEmailGoogle.setVisibility(View.VISIBLE);
            buttonRegGoogle.setVisibility(View.GONE);
            buttonLogInGoogle.setVisibility(View.GONE);
            btnBack.setVisibility(View.VISIBLE);

            textViewEstado.setVisibility(View.GONE);
            //textViewDetalle.setVisibility(View.VISIBLE);


        }
        else {
            //textViewEstado.setText(R.string.signed_out);

            editTextRegEmailGoogle.setVisibility(View.VISIBLE);
            editTextRegPwdGoogle.setVisibility(View.VISIBLE);

            //findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
            //findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);

            buttonRegGoogle.setVisibility(View.VISIBLE);
            buttonLogInGoogle.setVisibility(View.VISIBLE);
            buttonForgotPWDGoogle.setVisibility(View.VISIBLE);
            buttonEnvioEmailGoogle.setVisibility(View.GONE);
            //textViewDetalle.setVisibility(View.GONE);
            btnBack.setVisibility(View.GONE);

            textViewEstado.setText("Por Favor Regístrate o Inicia Sesión");
           // textViewDetalle.setText(null);


            //findViewById(R.id.signed_in_buttons).setVisibility(View.GONE);



        }
    }

    @Override
    public void onClick(View v) {
        ButterKnife.bind(this);

        int i = v.getId();
        if (i == R.id.buttonRegGoogle) {
            createAccount(editTextRegEmailGoogle.getText().toString(), editTextRegPwdGoogle.getText().toString());
        } else if (i == R.id.buttonLogInGoogle) {
            signIn(editTextRegEmailGoogle.getText().toString(), editTextRegPwdGoogle.getText().toString());
        } else if (i == R.id.buttonForgotPWDGoogle) {
            String email = editTextRegEmailGoogle.getText().toString();
            String cmp ="";
            if(email.equals(cmp)){
            flagPantalla=1;
            updateUI(null);}
            else {
                flagPantalla=0;
                forgotPWD();
            }
        } else if (i == R.id.buttonEnvioEmailGoogle) {
            String email = editTextRegEmailGoogle.getText().toString();
            String cmp ="";
            if(email.equals(cmp)){
                Toast.makeText(this, "Por Favor introduzca un email válido", Toast.LENGTH_SHORT).show();}
            else {
                forgotPWD();
            }
        }
        else if (i == R.id.btnBack) {
            //textViewEstado.setText(R.string.signed_out);

            editTextRegEmailGoogle.setVisibility(View.VISIBLE);
            editTextRegPwdGoogle.setVisibility(View.VISIBLE);

            //findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
            //findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);

            buttonRegGoogle.setVisibility(View.VISIBLE);
            buttonLogInGoogle.setVisibility(View.VISIBLE);
            buttonForgotPWDGoogle.setVisibility(View.VISIBLE);
            buttonEnvioEmailGoogle.setVisibility(View.GONE);
            //textViewDetalle.setVisibility(View.GONE);
            btnBack.setVisibility(View.GONE);

            textViewEstado.setVisibility(View.VISIBLE);

            textViewEstado.setText("Por Favor Regístrate o Inicia Sesión");
            //textViewDetalle.setText(null);


            //findViewById(R.id.signed_in_buttons).setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {

    }
}