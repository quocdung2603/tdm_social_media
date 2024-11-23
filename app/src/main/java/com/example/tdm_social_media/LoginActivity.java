package com.example.tdm_social_media;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {

    EditText password, email;
    Button login;
    TextView txt_signup;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        requestNotificationPermission(this);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        txt_signup = findViewById(R.id.txt_signup);

        auth = FirebaseAuth.getInstance();

        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog pd = new ProgressDialog(LoginActivity.this);
                pd.setMessage("Please wait ...");
                pd.show();

                String str_email = email.getText().toString();
                String str_password = password.getText().toString();

                if(TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)) {
                    Toast.makeText(LoginActivity.this, "All fields are required!!!", Toast.LENGTH_SHORT).show();
                } else {
                    auth.signInWithEmailAndPassword(str_email,str_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid());

                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        pd.dismiss();
//                                        Log.d("DATA", Boolean.toString(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()));
                                        if(!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                                            Intent goToVerifyEmail = new Intent(getApplicationContext(), VerifyEmailActivity.class);
                                            Bundle bundleEmail = new Bundle();
                                            bundleEmail.putString("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());

                                            goToVerifyEmail.putExtra("data", bundleEmail);

                                            startActivity(goToVerifyEmail);
                                            finish();
                                            return;
                                        }

                                        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());
                                        if (status != ConnectionResult.SUCCESS) {
                                            Log.e("FCM", "Google Play Services not available: " + status);
                                        }


                                        FirebaseMessaging.getInstance().getToken()
                                                .addOnCompleteListener(task -> {
                                                    if (!task.isSuccessful()) {

                                                        Log.e("TOKEN", "Fetching FCM token failed: " + task.getException());
                                                        return;
                                                    }

                                                    String token = task.getResult();
                                                    String userId = FirebaseAuth.getInstance().getUid(); // Lấy userId hiện tại
                                                    // Lưu token vào Firebase Realtime Database
                                                    DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users");
                                                    database.child(userId).child("token").setValue(token)
                                                            .addOnCompleteListener(task1 -> {
                                                                if (task1.isSuccessful()) {
                                                                    Log.d("TOKEN", "Token saved successfully.");
                                                                } else {
                                                                    Log.e("TOKEN", "Failed to save token: " + task1.getException());
                                                                }
                                                            });
                                                });

                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        pd.dismiss();
                                    }
                                });
                            } else {
                                pd.dismiss();
                                Toast.makeText(LoginActivity.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    public void requestNotificationPermission(Activity activity) {
        // Chỉ cần yêu cầu quyền trên Android 13 trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Kiểm tra nếu quyền chưa được cấp
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Yêu cầu quyền từ người dùng
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        1); // "1" là requestCode, có thể thay đổi theo nhu cầu
            } else {
                // Quyền đã được cấp
                Log.d("Notification", "Notification permission already granted");
            }
        } else {
            // Không cần yêu cầu quyền trên các phiên bản Android thấp hơn
            Log.d("Notification", "Notification permission not required for this Android version");
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) { // Kiểm tra requestCode để xác định quyền nào được xử lý
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền đã được cấp
                Log.d("Notification", "Notification permission granted");
            } else {
                // Quyền bị từ chối
                Log.e("Notification", "Notification permission denied");
            }
        }
    }
}