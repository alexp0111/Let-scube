package com.example.lbar.fragments.mainMenuFragments.accountFragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.lbar.adapter.StatusAdapter;
import com.example.lbar.MainActivity;
import com.example.lbar.R;
import com.example.lbar.helpClasses.Cube;
import com.example.lbar.helpClasses.User;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.example.lbar.MainActivity.SWIPE_THRESHOLD;
import static com.example.lbar.MainActivity.SWIPE_VELOCITY_THRESHOLD;
import static com.example.lbar.MainActivity.dp_height;
import static com.example.lbar.MainActivity.reference;
import static com.example.lbar.MainActivity.storageReferenceAvatar;

public class ProfileFragment extends Fragment implements GestureDetector.OnGestureListener {

    private FirebaseAuth mAuth;
    private FirebaseUser fUser;
    private StorageTask mUploadTask;
    private String userID;

    private Uri imageUri;
    private ActivityResultLauncher<Intent> launcher;

    private NavigationView navigationView;
    private View headerView;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private GestureDetector gestureDetector;

    private com.google.android.material.button.MaterialButton btn_logg_out, btn_ver;
    private com.google.android.material.textview.MaterialTextView txt_on_log_email, txt_on_log_birthday;
    private CircleImageView pr_img, nav_img;
    private TextView txt_ver, txt_on_log_hi, nav_name_text, nav_status_text;
    private LinearLayout image_layout;

    private com.google.android.material.progressindicator.LinearProgressIndicator progressBar;
    private SwipeRefreshLayout srl;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        AppCompatActivity main_activity = (MainActivity) getActivity();

        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        userID = fUser.getUid();

        toolbar = (Toolbar) view.findViewById(R.id.toolbar_in_profile);
        setToolbarSettings(toolbar, activity, main_activity);

        initItems(view);
        SwipeMenuOpenerControl(srl);

        srl.setOnRefreshListener(() -> {
            progressBar.show();
            getProfileInfoToDownload("after srl");
            progressBar.hide();
            srl.setRefreshing(false);
        });

        ViewGroup.LayoutParams params = image_layout.getLayoutParams();
        params.height = dp_height / 4;
        params.width = dp_height / 4;

        createLauncherForChoosingRomAlbum();

        getProfileInfoToDownload("first");

        pr_img.setOnClickListener(view1 -> {
            if (mUploadTask != null && mUploadTask.isInProgress()) {
                Snackbar.make(getView(), "wait for a few", Snackbar.LENGTH_SHORT).show();
            } else {
                choosePictureFromAlbum();
            }
        });

        btn_logg_out.setOnClickListener(view13 -> {
            loggingOutProfile();
        });

        return view;
    }

    private void getProfileInfoToDownload(String tmp) {
        if (!fUser.isEmailVerified()) {
            downloadNotVerifiedItems();
            progressBar.setVisibility(View.GONE);

        } else {
            reference.child(userID).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d("TAGTAG", tmp);
                            User profile = dataSnapshot.getValue(User.class);
                            if (profile != null) {
                                downloadProfileInfo(profile);
                            }
                            progressBar.setVisibility(View.GONE);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("profile", "sww");
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        }
    }

    private void initItems(View v) {
        navigationView = getActivity().findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        gestureDetector = new GestureDetector(getContext(), this);

        btn_logg_out = v.findViewById(R.id.bt_logg_out);                 // Кнопка выхода из аккаунта
        btn_ver = v.findViewById(R.id.ver_btn);                          // Кнопка верификации почты
        txt_ver = v.findViewById(R.id.ver_txtv);                         // сообщение о верификации

        pr_img = v.findViewById(R.id.prof_img);                          // фото профиля
        image_layout = v.findViewById(R.id.layout_user_img);

        //-> left menu bar
        nav_name_text = headerView.findViewById(R.id.name_nav);
        nav_status_text = headerView.findViewById(R.id.status_nav);
        nav_img = headerView.findViewById(R.id.nav_header_img);
        //-> left menu bar

        txt_on_log_email = v.findViewById(R.id.on_log_email);            // Поля информации
        txt_on_log_birthday = v.findViewById(R.id.on_log_birthday);      // Поля информации
        txt_on_log_hi = v.findViewById(R.id.on_log_text_hi);             // Поле приветствия

        progressBar = v.findViewById(R.id.prog_bar_onlog);
        srl = v.findViewById(R.id.pull_to_refresh_profile);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setToolbarSettings(Toolbar tbar, AppCompatActivity activity, AppCompatActivity main_activity) {
        if (tbar != null) {
            activity.setSupportActionBar(tbar);
            tbar.setTitle(R.string.title_profile);

            drawer = main_activity.findViewById(R.id.drawer_layout);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer, tbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);

            toggle.syncState();
            drawer.addDrawerListener(toggle);
        }
    }

    private void loggingOutProfile() {
        StatusAdapter adapter = new StatusAdapter();
        adapter.setUs_status("offline");

        //->
        nav_name_text.setText("User name");
        nav_status_text.setText("User status");
        nav_status_text.setTextColor(Color.parseColor("#BDBDBD"));

        Glide.with(headerView).load(R.drawable.ic_add_photo).into(nav_img);
        //->

        mAuth.signOut();                                            // Выход
        try {                                                       // Летим  в фрагмент входа
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new LogInFragment()).commit();
        } catch (Exception D) {
            Toast.makeText(getContext(), R.string.sww, Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadNotVerifiedItems() {
        pr_img.setVisibility(View.GONE);
        btn_ver.setVisibility(View.VISIBLE);
        txt_ver.setVisibility(View.VISIBLE);

        btn_ver.setOnClickListener(view12 -> {
            // Верификация через email          begin
            fUser = FirebaseAuth.getInstance().getCurrentUser();

            fUser.sendEmailVerification()
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(getContext(), R.string.ver_mail_has_sent,
                                    Toast.LENGTH_LONG).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), R.string.sww,
                                    Toast.LENGTH_SHORT).show());
            // Верификация через email          end
        });
    }

    private void downloadProfileInfo(User profile) {
        String name, email, birthday, urll, status;

        name = profile.getUs_name();
        email = profile.getUs_email();
        birthday = profile.getUs_birthday();
        urll = profile.getImage();
        status = profile.getUs_status();


        txt_on_log_email.setText("email:     " + email);
        txt_on_log_birthday.setText("Birthday:     " + birthday);

        //->
        nav_name_text.setText(name);
        nav_status_text.setText(status);
        nav_status_text.setTextColor(Color.parseColor("#FFC107"));
        //->

        txt_on_log_hi.setText("Здравствуйте,\n" + name + "!");

        Glide.with(ProfileFragment.this).load(urll).into(pr_img);
        Glide.with(headerView).load(urll).into(nav_img);
    }

    private void createLauncherForChoosingRomAlbum() {
        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();

                    if (data.getData() != null && result.getResultCode() == RESULT_OK) {
                        imageUri = data.getData();
                        pr_img.setImageURI(imageUri);
                        uploadPicture();
                    } else {
                        Log.d("imageUri", "error");
                    }
                });
    }

    private void uploadPicture() {
        StorageReference riversRef = storageReferenceAvatar.child(userID).child(imageUri.getLastPathSegment());
        mUploadTask = riversRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            Snackbar.make(getView(), "image uploaded", Snackbar.LENGTH_LONG).show();

            Task<Uri> downloadURl = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(task -> {
                reference.child(userID).child("image").setValue(task.getResult().toString());
                progressBar.setVisibility(View.GONE);
            });
        }).addOnFailureListener(e -> {
            Log.d("uploading", "f");
            progressBar.setVisibility(View.GONE);
        });
    }

    private void choosePictureFromAlbum() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        launcher.launch(intent);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void SwipeMenuOpenerControl(View v) {
        v.setOnTouchListener((view1, motionEvent) -> {
            view1.performClick();
            gestureDetector.onTouchEvent(motionEvent);
            return false;
        });
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
        boolean res = false;
        float diffY = moveEvent.getY() - downEvent.getY();
        float diffX = moveEvent.getX() - downEvent.getX();

        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    onSwipeRight();
                }
                res = true;
            }
        }
        return res;
    }

    private void onSwipeRight() {
        drawer.openDrawer(GravityCompat.START);
    }
}
