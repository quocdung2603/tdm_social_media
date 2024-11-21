package com.example.tdm_social_media;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText username, fullname, password, email;
    Button register;
    TextView txt_login;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;

    String str_username, str_password, str_fullname, str_email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        fullname = findViewById(R.id.fullname);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        register = findViewById(R.id.register);
        txt_login = findViewById(R.id.txt_login);

        auth = FirebaseAuth.getInstance();

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Please wait ...");
                pd.show();

                str_username = username.getText().toString();
                str_fullname = fullname.getText().toString();
                str_email = email.getText().toString();
                str_password = password.getText().toString();

                if(TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname) || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)) {
                    Toast.makeText(RegisterActivity.this, "All fields are required!!!", Toast.LENGTH_SHORT).show();
                } else if(str_password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password must have 6 characters", Toast.LENGTH_SHORT).show();
                } else {
                    register();
                }
            }
        });

    }

    private void openVerifyEmailActivity() {
        Intent intentVerifyEmail = new Intent(getApplicationContext(), VerifyEmailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("email", str_email);

        intentVerifyEmail.putExtra("data", bundle);

        startActivity(intentVerifyEmail);
    }

    private void addUserData(String userId) {
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", userId);
        hashMap.put("username", str_username.toLowerCase());
        hashMap.put("fullname", str_fullname);
        hashMap.put("bio", "");
        hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/tdmsocialmedia-c0fcd.appspot.com/o/placeholder.png?alt=media&token=fd9099b3-ece5-412a-ab12-b33850b0140b");

        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    pd.dismiss();
                    finish();
//                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
                }
            }
        });
    }

    public void register() {
        auth.createUserWithEmailAndPassword(str_email, str_password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    openVerifyEmailActivity();
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    String userId = firebaseUser.getUid();
                    addUserData(userId);
                } else {
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



}