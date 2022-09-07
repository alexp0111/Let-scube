package com.example.lbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.SharedPreferences;
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
import com.example.lbar.fragments.mainMenuFragments.eventFragments.MainEventFragment;
import com.example.lbar.fragments.mainMenuFragments.roomsFragments.RoomsStartFragment;
import com.example.lbar.helpClasses.User;
import com.example.lbar.fragments.mainMenuFragments.CollectionFragment;
import com.example.lbar.fragments.mainMenuFragments.peopleFragments.PeopleFragment;
import com.example.lbar.fragments.mainMenuFragments.messageFragments.MessageFragment;
import com.example.lbar.fragments.mainMenuFragments.accountFragments.ProfileFragment;
import com.example.lbar.fragments.mainMenuFragments.accountFragments.LogInFragment;
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
                        String name, urll;

                        name = profile.getUs_name();
                        urll = profile.getImage();
                        //->
                        try {
                            nav_name_text.setText(name);
                            Glide.with(headerView).load(urll).into(nav_img);
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
            case R.id.nav_news:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MainEventFragment()).commit();
                break;
            case R.id.nav_message:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MessageFragment()).commit();
                break;
            case R.id.nav_listOfUsers:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PeopleFragment()).commit();
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
            case R.id.nav_rooms:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new RoomsStartFragment()).commit();
                break;
            case R.id.nav_collection:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new CollectionFragment()).commit();
                break;
            //////////////////
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

                    boolean value = false;
                    final SharedPreferences preferences = getSharedPreferences("isChecked", 0);

                    value = preferences.getBoolean("isChecked", value);
                    switchMaterial.setChecked(value);

                    // Doubled code
                    if (switchMaterial.isChecked()){
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }

                    // Doubled code
                    switchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            preferences.edit().putBoolean("isChecked", true).apply();
                        } else {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            preferences.edit().putBoolean("isChecked", false).apply();
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

        NavigationView navigationView =  findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        TextView nav_status_text = headerView.findViewById(R.id.status_nav);

        nav_status_text.setText("online");
        nav_status_text.setTextColor(Color.parseColor("#FFC107"));

        StatusAdapter adapter = new StatusAdapter();
        adapter.setUs_status("online");
    }

    @Override
    public void onPause() {
        super.onPause();

        NavigationView navigationView =  findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        TextView nav_status_text = headerView.findViewById(R.id.status_nav);

        nav_status_text.setText("offline");
        nav_status_text.setTextColor(Color.parseColor("#BDBDBD"));

        StatusAdapter adapter = new StatusAdapter();
        adapter.setUs_status("offline");
    }
}