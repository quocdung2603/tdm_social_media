package com.example.tdm_social_media;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyEmailActivity extends AppCompatActivity {
    public static final int REQ_EMAIL_VERIFIED_CODE = 114;
    public static final int RES_EMAIL_VERIFIED_CODE = 115;
    public static final int time_see_notify_verify = 5000;

    Button btnVerifyEmail, btnBack;
    TextView tv;

    String str_email;
    Bundle bundle_data;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_email);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_verifyEmail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initWidget();
        initData();
        initOnclick();

    }

    private void initWidget() {
        btnVerifyEmail = findViewById(R.id.btn_verifyNow);
        btnBack = findViewById(R.id.btn_back);
        tv = findViewById(R.id.txt_label_verifyEmail);
    }

    private void initData() {
        intent = getIntent();
        bundle_data = intent.getBundleExtra("data");

        str_email = bundle_data.getString("email");
    }

    private void initOnclick() {
        btnVerifyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyEmail();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });
    }

    private void verifyEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(this,new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Intent goToIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(goToIntent);
                    Toast.makeText(getApplicationContext(), "Register successfully, please check your email" + "\"" + str_email + "\" "  + "to verify account!", Toast.LENGTH_LONG).show();
                } else {

                    Toast.makeText(getApplicationContext(), "Could not send to this email!!", Toast.LENGTH_LONG).show();
                }
                finish();
            }


        });
    }


}