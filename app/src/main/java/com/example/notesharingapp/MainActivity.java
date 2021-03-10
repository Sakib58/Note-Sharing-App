package com.example.notesharingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnSignIn;
    private TextView btnSignUp;
    EditText etEmail,etPassword;

    //SharedPrefs datas
    public static final String SHARED_PREFS = "SharedPrefs";
    public static final String EMAIL = "Email";
    public static final String NAME = "Name";
    public static final String SID = "Sid";
    public static final String PHONE = "Phone";

    String emailAddress,name,sid;
    String phone;

    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn = findViewById(R.id.btn_signin);
        btnSignUp = findViewById(R.id.tv_signup);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);

        btnSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);

        mAuth=FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);

    }

    public void storeCurrentUserInfo(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(EMAIL,emailAddress);
        editor.putString(SID,sid);
        editor.putString(NAME,name);
        editor.putString(PHONE,phone);
        editor.apply();
        Toast.makeText(this, "Data stored", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this,HomeActivity.class));
    }

    public void saveCurrentUserData(String email){
        progressDialog.show();
        db.collection("users")
                .document(email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()){
                            emailAddress = email;
                            phone = task.getResult().get("phone").toString();
                            name = task.getResult().get("name").toString();
                            sid = task.getResult().get("sid").toString();
                            storeCurrentUserInfo();
                        }
                    }
                });
    }

    private void signIn(){
        mAuth.signInWithEmailAndPassword(etEmail.getText().toString(),etPassword.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()){
                            saveCurrentUserData(etEmail.getText().toString());
                            Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                            System.out.println("Login success");

                        }else {
                            Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {

        if (btnSignIn.equals(view)) {
            progressDialog.show();
            signIn();
        } else if (btnSignUp.equals(view)) {
            Toast.makeText(this, "Please consult with admin of your university", Toast.LENGTH_SHORT).show();
        }
    }
}