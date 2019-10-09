package com.example.loginfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";

    private DatabaseReference database;
    private FirebaseAuth auth;
    private EditText edtEmail;
    private EditText edtPass;
    private Button btnMasuk;
    private Button btnDaftar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        edtEmail = (EditText)findViewById(R.id.tv_email);
        edtPass = (EditText)findViewById(R.id.tv_pass);
        btnMasuk = (Button) findViewById(R.id.btn_masuk);
        btnDaftar = (Button)findViewById(R.id.btn_daftar);

        btnMasuk.setOnClickListener(this);
        btnDaftar.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btn_daftar){
            signUp();
        }else if(i == R.id.btn_masuk){
            signIn();
        }
    }

    private void signIn(){
        Log.d(TAG,"SignIn");
        if (!validateForm()){
            return;
        }


        String email = edtEmail.getText().toString();
        String password = edtPass.getText().toString();

        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG,"signIn:onComplete:"+task.isSuccessful());

                        if (task.isSuccessful()){
                            onAuthSuccess(task.getResult().getUser());
                        }else {
                            Toast.makeText(MainActivity.this,"Sign In Failed",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void signUp(){
        Log.d(TAG, "SignUp");
        if (!validateForm()){
            return;
        }

        String email = edtEmail.getText().toString();
        String password = edtPass.getText().toString();

        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG,"createUser:onComplete:"+task.isSuccessful());

                        if (task.isSuccessful()){
                            onAuthSuccess(task.getResult().getUser());
                        }else {
                            Toast.makeText(MainActivity.this,"Sign Up Failed",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user){
        String username = usernameFromEmail(user.getEmail());

        writeNewAdmin(user.getUid(),username,user.getEmail());

        startActivity(new Intent(MainActivity.this,MenuActivity.class));
        finish();
    }

    private String usernameFromEmail(String email){
        if (email.contains("@")){
            return email.split("@")[0];
        }else {
            return email;
        }
    }

    private boolean validateForm() {
        boolean result = true;

        if (TextUtils.isEmpty(edtEmail.getText().toString())) {
            edtEmail.setError("Required");
            result = false;
        } else {
            edtEmail.setError(null);
        }

        if (TextUtils.isEmpty(edtPass.getText().toString())) {
            edtPass.setError("Required");
            result = false;
        } else {
            edtPass.setError(null);
        }

        return result;
    }

    private void writeNewAdmin (String userId,String name,String email){
        Admin admin = new Admin(name,email);

        database.child("admins").child(userId).setValue(admin);
    }
}
