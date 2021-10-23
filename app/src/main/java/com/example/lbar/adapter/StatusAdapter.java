package com.example.lbar.adapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

import static com.example.lbar.MainActivity.reference;

public class StatusAdapter {
    private FirebaseUser fUser;
    private DatabaseReference fStatusRef;
    private FirebaseAuth mAuth;

    public StatusAdapter() {
        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        if (fUser != null)
            fStatusRef = reference.child(fUser.getUid());
    }

    public void setUs_status(String us_status) {

        if (fUser != null) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("us_status", us_status);
            fStatusRef.updateChildren(hashMap);
        }
    }
}
