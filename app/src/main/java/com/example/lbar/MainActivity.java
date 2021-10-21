package com.example.lbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lbar.Adapter.StatusAdapter;
import com.example.lbar.database.User;
import com.example.lbar.fragments.FriendsFragment;
import com.example.lbar.fragments.MessageFragment;
import com.example.lbar.fragments.OnLoggedFragment;
import com.example.lbar.fragments.ProfileFragment;
import com.example.lbar.fragments.RegistrationFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

// Ближайшие микроцели:
// 1) Сделать проверку через почту                                                              (completed)
// 2) Подцепить к аккаунту имя и прочие характеристики (при желании) через realtime DB          (name, age completed, status)
// 3) Оформить фрагмент с кнопкой выхода                                                        (started)
// 4) Подцепить фото к акканту                                                                  (completed)
// 5) Сделать возможным ресет паролей                                                           (completed)

// Следим за values string-ru
// Для списка людей и сообщений посмотри dividers на сайте MD

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View headerView;

    private static CircleImageView nav_img;
    private TextView nav_name_text, nav_status_text;

    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private String userID;
    public static int dp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance(getString(R.string.fdb_inst)).getReference("Users");
        try {
            userID = mAuth.getCurrentUser().getUid();
        } catch (Exception e) {
            Log.d("start_user_id", "not logged");
        }

        drawer = findViewById(R.id.drawer_layout);

        // Navigation
        navigationView = findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        //->
        nav_name_text = headerView.findViewById(R.id.name_nav);
        nav_status_text = headerView.findViewById(R.id.status_nav);
        nav_img = headerView.findViewById(R.id.nav_header_img);
        //->
        downloadProfileIntoMenu();

        // Start activity
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new MessageFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_message);
        }


        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        dp = displayMetrics.widthPixels;
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
                        nav_name_text.setText(name);
                        nav_status_text.setText(status);
                        nav_status_text.setTextColor(Color.parseColor("#FFC107"));
                        //->
                        Glide.with(headerView).load(urll).into(nav_img);
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
                            new ProfileFragment()).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new OnLoggedFragment()).commit();
                }
                break;
            case R.id.nav_share:
                Toast.makeText(this, "Sorry.\nThe action is currently unavailable", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_estimate:
                Toast.makeText(this, "Sorry.\nThe action is currently unavailable", Toast.LENGTH_SHORT).show();
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
                    new ProfileFragment()).commit();
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