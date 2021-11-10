package com.example.lbar.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.lbar.adapter.StatusAdapter;
import com.example.lbar.MainActivity;
import com.example.lbar.R;
import com.example.lbar.database.User;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.example.lbar.MainActivity.dp_height;
import static com.example.lbar.MainActivity.reference;
import static com.example.lbar.MainActivity.storageReferenceAvatar;

public class ProfileFragment extends Fragment {

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

    private com.google.android.material.button.MaterialButton btn_logg_out, btn_ver;
    private com.google.android.material.textview.MaterialTextView txt_on_log_email, txt_on_log_birthday;
    private CircleImageView pr_img, nav_img;
    private TextView txt_ver, txt_on_log_hi, nav_name_text, nav_status_text;
    private LinearLayout image_layout;

    private com.google.android.material.progressindicator.LinearProgressIndicator progressBar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        AppCompatActivity main_activity = (MainActivity) getActivity();

        toolbar = (Toolbar) view.findViewById(R.id.toolbar_in_profile);
        if (toolbar != null){
            activity.setSupportActionBar(toolbar);
            toolbar.setTitle("Profile");

            drawer = main_activity.findViewById(R.id.drawer_layout);
            //Objects.requireNonNull(activity.getSupportActionBar()).setDisplayShowTitleEnabled(false);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer, toolbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);

            toggle.syncState();
            drawer.addDrawerListener(toggle);
        }

        navigationView = getActivity().findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);

        btn_logg_out = view.findViewById(R.id.bt_logg_out);                 // Кнопка выхода из аккаунта
        btn_ver = view.findViewById(R.id.ver_btn);                          // Кнопка верификации почты
        txt_ver = view.findViewById(R.id.ver_txtv);                         // сообщение о верификации

        pr_img = view.findViewById(R.id.prof_img);                          // фото профиля
        image_layout = view.findViewById(R.id.layout_user_img);

        ViewGroup.LayoutParams params = image_layout.getLayoutParams();
        params.height = dp_height/4;
        params.width = dp_height/4;

        //-> left menu bar
        nav_name_text = headerView.findViewById(R.id.name_nav);
        nav_status_text = headerView.findViewById(R.id.status_nav);
        nav_img = headerView.findViewById(R.id.nav_header_img);
        //-> left menu bar

        txt_on_log_email = view.findViewById(R.id.on_log_email);            // Поля информации
        txt_on_log_birthday = view.findViewById(R.id.on_log_birthday);      // Поля информации
        txt_on_log_hi = view.findViewById(R.id.on_log_text_hi);             // Поле приветствия

        progressBar = view.findViewById(R.id.prog_bar_onlog);
        progressBar.setVisibility(View.VISIBLE);

        ///////
        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        userID = fUser.getUid();
        ///////

        pr_img.setOnClickListener(view1 -> {
            if (mUploadTask != null && mUploadTask.isInProgress()) {
                Snackbar.make(getView(), "wait for a few", Snackbar.LENGTH_SHORT).show();
            } else {
                choosePictureFromAlbum();
            }
        });

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

        if (!fUser.isEmailVerified()) {

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
            progressBar.setVisibility(View.GONE);

        } else {
            reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User profile = dataSnapshot.getValue(User.class);

                    if (profile != null) {
                        String name, email, birthday, urll, status;

                        name = profile.getUs_name();
                        email = profile.getUs_email();
                        birthday = profile.getUs_birthday();
                        urll = profile.getImage();
                        status = profile.getUs_status();


                        //txt_on_log_name.setText("Name:     " + name);
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

                   progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("profile", "sww");
                    progressBar.setVisibility(View.GONE);
                }
            });
        }


        // Листенер
        btn_logg_out.setOnClickListener(view13 -> {

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
        });

        return view;
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
}
