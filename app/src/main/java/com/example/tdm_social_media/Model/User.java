package com.example.tdm_social_media.Model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class User {
    private String id;
    private String username;
    private String fullname;
    private String imageurl;
    private String bio;

    public User(String id, String username, String fullname, String imageurl, String bio) {
        this.id = id;
        this.username = username;
        this.fullname = fullname;
        this.imageurl = imageurl;
        this.bio = bio;
    }

    public User() {

    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullname() {
        return fullname;
    }

    public String getImageurl() {
        return imageurl;
    }

    public String getBio() {
        return bio;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }


    public void removeUser(String uid) {

        // Step 1: Remove user data from Firestore
        DatabaseReference userDocRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

//        userDocRef.
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        Log.d("UserDelete", "User data removed from Firestore.");
//
//                        // Step 2: Remove user from Firebase Authentication
//                        FirebaseUser user = auth.getCurrentUser();
//                        if (user != null && user.getUid().equals(uid)) {
//                            user.delete()
//                                    .addOnCompleteListener(deleteTask -> {
//                                        if (deleteTask.isSuccessful()) {
//                                            Log.d("UserDelete", "User deleted from Firebase Authentication.");
//                                            // Optionally, redirect user to a different activity or show a success message
//                                        } else {
//                                            Log.e("UserDelete", "Failed to delete user from Firebase Authentication.", deleteTask.getException());
//                                        }
//                                    });
//                        }
//                    } else {
//                        Log.e("UserDelete", "Failed to remove user data from Firestore.", task.getException());
//                    }
//                });
    }
}
