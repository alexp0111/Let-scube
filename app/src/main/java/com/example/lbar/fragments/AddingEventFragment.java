package com.example.lbar.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.lbar.MainActivity;
import com.example.lbar.R;
import com.example.lbar.fragments.mainMenuFragments.EventFragment;
import com.example.lbar.helpClasses.Event;
import com.example.lbar.helpClasses.Message;
import com.example.lbar.helpClasses.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;
import static com.example.lbar.MainActivity.reference;
import static com.example.lbar.MainActivity.storage;
import static com.example.lbar.MainActivity.storageReferenceAvatar;

public class AddingEventFragment extends Fragment {

    // Добавить отображение фотографии
    // Поставить ограничения по объёму фотографий
    // Добавить возможность удалить добавленное фото (и возможно заменить)
    // Попроверять на возможность утечек\багов\захламления бд.
    // Добавить прогресс бар!
    // Перепроверить защиту от дурака

    private FirebaseUser fUser;
    private StorageTask mUploadTask;

    private FloatingActionButton fabExtended;
    private FloatingActionButton fabApply;
    private FloatingActionButton fabDisable;

    private TextInputEditText etHeader;
    private TextInputEditText etText;
    private CheckBox cbAccessibility;
    private MaterialButton btPictureAdditing;

    private String textHeader;
    private String textText;
    private int accessibility;
    private String picture = "none";
    private Uri imageUri = null;

    private Animation startExtendedFabAnimation;
    private Animation miniUnExplosionAnimation;
    private Animation rotateExtendedFabAnimation;
    private Animation rotateBackExtendedFabAnimation;
    private Animation startApplyFabAnimation;
    private Animation startDisableFabAnimation;
    private Animation closeApplyFabAnimation;
    private Animation closeDisableFabAnimation;

    private ActivityResultLauncher<Intent> launcher;

    private boolean fabFlag = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adding_event, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        AppCompatActivity main_activity = (MainActivity) getActivity();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();

        initItems(view);
        setItemAnimations();
        createLauncherForChoosingRomAlbum();

        fabExtended.startAnimation(startExtendedFabAnimation);

        fabExtended.setOnClickListener(view1 -> {
            if (!fabFlag) {
                fabExtended.startAnimation(rotateExtendedFabAnimation);
                fabApply.setVisibility(View.VISIBLE);
                fabDisable.setVisibility(View.VISIBLE);

                fabApply.startAnimation(startApplyFabAnimation);
                fabDisable.startAnimation(startDisableFabAnimation);

                fabFlag = true;
            } else {
                fabExtended.startAnimation(rotateBackExtendedFabAnimation);
                fabApply.startAnimation(closeApplyFabAnimation);
                fabDisable.startAnimation(closeDisableFabAnimation);

                fabApply.setVisibility(View.INVISIBLE);
                fabDisable.setVisibility(View.INVISIBLE);

                fabFlag = false;
            }
        });

        btPictureAdditing.setOnClickListener(view14 -> choosePictureFromAlbum());

        fabApply.setOnClickListener(view12 -> {
            packAndSendAllDataToDB(view);
        });

        fabDisable.setOnClickListener(view13 -> {
            etText.setText("");
            etHeader.setText("");
            getBackAnimationsStart();
        });

        return view;
    }

    private void packAndSendAllDataToDB(View v) {
        textHeader = etHeader.getText().toString();
        textText = etText.getText().toString();
        accessibility = cbAccessibility.isChecked() ? 1 : 0;

        if (fUser == null){
            Snackbar.make(v, "It looks like you haven't entered your account. It is necessary, to make an event.", Snackbar.LENGTH_SHORT).show();
        } else {
            if (textHeader == null || textText == null || textHeader.equals("") || textText.equals("")){
                Snackbar.make(v, "Please, do not let <Header> or <Text> fields empty.", Snackbar.LENGTH_SHORT).show();
            } else {
                DatabaseReference reference = FirebaseDatabase.getInstance(getString(R.string.fdb_inst)).getReference();

                if (imageUri != null){
                    uploadPicture(v);
                } else {
                    picture = "none";
                    Event event = new Event(fUser.getUid(), textHeader, textText, picture, 0, accessibility);
                    reference.child("Events").push().setValue(event);
                    getBackAnimationsStart();
                }
            }
        }
    }

    private void initItems(View view) {
        fabExtended = view.findViewById(R.id.event_push);
        fabApply = view.findViewById(R.id.event_apply);
        fabDisable = view.findViewById(R.id.event_disable);

        etHeader = view.findViewById(R.id.event_adding_header_txt);
        etText = view.findViewById(R.id.event_adding_text_txt);
        cbAccessibility = view.findViewById(R.id.event_adding_friends_checkBox);
        btPictureAdditing = view.findViewById(R.id.event_adding_picture_button);
    }

    private void setItemAnimations() {
        startExtendedFabAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.from_bottom);
        miniUnExplosionAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.mini_circle_unexplosion);

        rotateExtendedFabAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
        rotateBackExtendedFabAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_back);

        startApplyFabAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fab_from_bottom_lvl2);
        startDisableFabAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fab_from_bottom_lvl1);

        closeApplyFabAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fab_to_bottom_lvl2);
        closeDisableFabAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fab_to_bottom_lvl1);

        rotateExtendedFabAnimation.setDuration(200);
        rotateBackExtendedFabAnimation.setDuration(200);
        miniUnExplosionAnimation.setDuration(200);

        miniUnExplosionAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                closeFrgament();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void getBackAnimationsStart() {
        fabApply.startAnimation(miniUnExplosionAnimation);
        fabDisable.startAnimation(miniUnExplosionAnimation);
        fabExtended.startAnimation(miniUnExplosionAnimation);

        fabApply.setVisibility(View.INVISIBLE);
        fabDisable.setVisibility(View.INVISIBLE);
        fabExtended.setVisibility(View.INVISIBLE);

        fabFlag = false;
    }

    private void closeFrgament() {
        EventFragment fragment = new EventFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("circle_anim", true);
        fragment.setArguments(bundle);

        try {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    fragment).commit();
        } catch (Exception D) {
            Toast.makeText(getContext(), R.string.sww, Toast.LENGTH_SHORT).show();
        }
    }

    private void choosePictureFromAlbum() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        launcher.launch(intent);
    }

    private void createLauncherForChoosingRomAlbum() {
        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();

                    if (data.getData() != null && result.getResultCode() == RESULT_OK) {
                        imageUri = data.getData();
                    } else {
                        Log.d("imageUri", "error");
                    }
                });
    }

    private void uploadPicture(View v) {
        StorageReference riversRef = storage.getReference("EventsImages").child(fUser.getUid()).child(imageUri.getLastPathSegment());
        mUploadTask = riversRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            Snackbar.make(v, "image added!", Snackbar.LENGTH_LONG).show();

            Task<Uri> downloadURl = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(task -> {
                picture = task.getResult().toString();
                DatabaseReference reference = FirebaseDatabase.getInstance(getString(R.string.fdb_inst)).getReference();
                Event event = new Event(fUser.getUid(), textHeader, textText, picture, 0, accessibility);
                reference.child("Events").push().setValue(event);
                getBackAnimationsStart();
            });
        }).addOnFailureListener(e -> {
            Log.d("uploading", "f");
        });
    }


}
