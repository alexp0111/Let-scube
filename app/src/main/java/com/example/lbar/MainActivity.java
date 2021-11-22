package com.example.lbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lbar.adapter.StatusAdapter;
import com.example.lbar.database.User;
import com.example.lbar.fragments.FriendsFragment;
import com.example.lbar.fragments.MessageFragment;
import com.example.lbar.fragments.ProfileFragment;
import com.example.lbar.fragments.LogInFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int SWIPE_THRESHOLD = 100;
    public static final int SWIPE_VELOCITY_THRESHOLD = 100;

    public static DatabaseReference reference;
    public static FirebaseStorage storage;
    public static StorageReference storageReferenceAvatar, storageReferenceDefaultImg;
    private FirebaseAuth mAuth;
    private FirebaseUser fUser;
    private String userID;

    public static int dp_width;
    public static int dp_height;
    private static CircleImageView nav_img;


    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View headerView;

    private TextView nav_name_text, nav_status_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance(getString(R.string.fdb_inst)).getReference("Users");
        storage = FirebaseStorage.getInstance("gs://lbar-messenger.appspot.com");
        storageReferenceAvatar = storage.getReference("AvatarImages");
        storageReferenceDefaultImg = storage.getReference("camera.png");

        try {
            fUser = mAuth.getCurrentUser();
            userID = fUser.getUid();
        } catch (Exception e) {
            Log.d("start_user_id", "not logged");
        }

        // Navigation

        initItems();

        try {
            navigationView.setCheckedItem(R.id.nav_switch);
            navigationView.getMenu().performIdentifierAction(R.id.nav_switch, 0);
        } catch (Exception e){
            Toast.makeText(this, "Error in setCheckedItem() and getMenu()", Toast.LENGTH_SHORT).show();
        }

        downloadProfileIntoMenu();

        // Start activity
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new MessageFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_message);
        }


        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        dp_width = displayMetrics.widthPixels;
        dp_height = displayMetrics.heightPixels;
    }

    private void initItems() {
        drawer = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);

        headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        nav_name_text = headerView.findViewById(R.id.name_nav);
        nav_status_text = headerView.findViewById(R.id.status_nav);
        nav_img = headerView.findViewById(R.id.nav_header_img);
    }

    private void downloadProfileIntoMenu() {
        if (userID != null) {
            reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User profile = dataSnapshot.getValue(User.class);

                    if (profile != null) {
                        String name, urll, status;

                        name = profile.getUs_name();
                        status = profile.getUs_status();
                        urll = profile.getImage();
                        //->
                        try {
                            nav_name_text.setText(name);
                            nav_status_text.setText(status);
                            nav_status_text.setTextColor(Color.parseColor("#FFC107"));
                            //->
                            Glide.with(headerView).load(urll).into(nav_img);
                            Snackbar.make(findViewById(R.id.mainActivity_layout), "menu load", Snackbar.LENGTH_SHORT).show();
                        } catch (Exception e){
                            Snackbar.make(findViewById(R.id.mainActivity_layout), "Exception in menu load", Snackbar.LENGTH_SHORT).show();
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("profile", "sww");
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_message:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MessageFragment()).commit();
                break;
            case R.id.nav_friends:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FriendsFragment()).commit();
                break;
            case R.id.nav_profile:                                                                  // Фрагмент аккаунта!
                FirebaseUser user = mAuth.getCurrentUser();
                if (user == null) {
                    navigationView.setCheckedItem(R.id.nav_profile);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new LogInFragment()).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new ProfileFragment()).commit();
                }
                break;
            case R.id.nav_share:
                Toast.makeText(this, "Sorry.\nThe action is currently unavailable", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_estimate:
                Toast.makeText(this, "Sorry.\nThe action is currently unavailable", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_switch:
                try {
                    MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_switch);
                    SwitchMaterial switchMaterial = (SwitchMaterial) menuItem.getActionView().findViewById(R.id.nav_switch_id);
                    switchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        } else {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        }
                        if (mAuth.getCurrentUser() == null) {
                            navigationView.setCheckedItem(R.id.nav_profile);
                        }
                    });
                } catch (Exception e){
                    Toast.makeText(this, "Error in theme switch", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            navigationView.setCheckedItem(R.id.nav_profile);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new LogInFragment()).commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        StatusAdapter adapter = new StatusAdapter();
        adapter.setUs_status("online");
    }

    @Override
    public void onPause() {
        super.onPause();
        StatusAdapter adapter = new StatusAdapter();
        adapter.setUs_status("offline");
    }
}