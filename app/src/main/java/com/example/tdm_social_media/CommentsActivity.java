package com.example.tdm_social_media;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tdm_social_media.Adapter.CommentAdapter;
import com.example.tdm_social_media.Model.Comment;
import com.example.tdm_social_media.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    EditText add_comment;
    ImageView image_profile;
    TextView post;

    String postid;
    String publisherid;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comments);

        Toolbar toolbar  = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        postid  = intent.getStringExtra("postid");
        publisherid = intent.getStringExtra("publisherid");

        recyclerView =findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList, postid);
        recyclerView.setAdapter(commentAdapter);

        add_comment = findViewById(R.id.add_comment);
        image_profile = findViewById(R.id.image_profile);
        post = findViewById(R.id.post);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (add_comment.getText().toString().equals("")) {
                    Toast.makeText(CommentsActivity.this, "You can't send empty comment", Toast.LENGTH_SHORT).show();
                } else {
                    addComment();
                }
            }
        });

        getImage();
        readComments();
    }

    public void addComment() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);

        String commentid = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment",add_comment.getText().toString());
        hashMap.put("publisher", firebaseUser.getUid());
        hashMap.put("commentid", commentid);
        hashMap.put("create_at", System.currentTimeMillis());

        reference.child(commentid).setValue(hashMap);
        addNotifications();
        add_comment.setText("");

    }

    private void addNotifications() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(publisherid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "commented: " + add_comment.getText().toString());
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);

        reference.push().setValue(hashMap);

        // send notification
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                String message = user.getFullname() + " liked your post";
                FCMNotificationSender sender = new FCMNotificationSender(publisherid, "New notification", message, getApplicationContext());
                sender.sendNotificationToDevice();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getImage() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageurl()).into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void  readComments() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for(DataSnapshot snapshot1: snapshot.getChildren()) {
                    Comment comment = snapshot1.getValue(Comment.class);
                    commentList.add(comment);
                }

                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}