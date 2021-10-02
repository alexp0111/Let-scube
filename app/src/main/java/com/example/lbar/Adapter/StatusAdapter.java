package com.example.lbar.Adapter;

import androidx.annotation.NonNull;

import com.example.lbar.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class StatusAdapter {
    private FirebaseAuth mAuth;
    private DatabaseReference reference, fStatusRef;
    private FirebaseUser fUser;

    public StatusAdapter() {
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance("https://lbar-messenger-default-rtdb.firebaseio.com/").getReference("Users");
        fUser = mAuth.getCurrentUser();
    }

    public void setUs_status(String us_status) {

        if (fUser != null) {
            fStatusRef = reference.child(fUser.getUid());

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("us_status", us_status);

            fStatusRef.updateChildren(hashMap);
        }
    }
}
