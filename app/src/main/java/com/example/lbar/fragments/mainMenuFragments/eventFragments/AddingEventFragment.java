package com.example.lbar.fragments.mainMenuFragments.eventFragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.lbar.MainActivity;
import com.example.lbar.R;
import com.example.lbar.helpClasses.Event;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;
import static com.example.lbar.MainActivity.storage;

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
    //picture block
    private MaterialCardView cdPictureAdding;
    private TextView txtPictureAdding;
    private ImageView imgPictureAdding;

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
        createLauncherForChoosingRomAlbum(view);

        fabExtended.startAnimation(startExtendedFabAnimation);

        fabExtended.setOnClickListener(view1 -> {
            startFabExtended();
        });

        cdPictureAdding.setOnClickListener(view14 -> {
            if (imageUri == null){
                choosePictureFromAlbum();
            } else {
                txtPictureAdding.setText(R.string.add_picture);

                imgPictureAdding.setVisibility(View.GONE);
                txtPictureAdding.setVisibility(View.VISIBLE);

                imageUri = null;
            }
        });

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

    private void startFabExtended() {
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

    private void initItems(View v) {
        fabExtended = v.findViewById(R.id.event_push);
        fabApply = v.findViewById(R.id.event_apply);
        fabDisable = v.findViewById(R.id.event_disable);

        etHeader = v.findViewById(R.id.event_adding_header_txt);
        etText = v.findViewById(R.id.event_adding_description_txt);
        cbAccessibility = v.findViewById(R.id.event_adding_friends_checkBox);
        //picture block
        cdPictureAdding = v.findViewById(R.id.event_adding_picture_cdv);
        txtPictureAdding = v.findViewById(R.id.event_adding_picture_cdv_txt);
        imgPictureAdding = v.findViewById(R.id.event_adding_picture_cdv_img);
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
                closeFragment();
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

    private void closeFragment() {
        MainEventFragment fragment = new MainEventFragment();
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

    private void createLauncherForChoosingRomAlbum(View view) {
        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();

                    assert data != null;
                    if (data.getData() != null && result.getResultCode() == RESULT_OK) {
                        imageUri = data.getData();

                        /*
                        File file = new File(imageUri.getPath());
                        Log.d("imageUri ", imageUri.getPath());
                        Log.d("imageUri ", file.canRead() + "");
                        try {
                            File compressed = new Compressor(getContext())
                                    .compressToFile(file);
                            //imageUri = Uri.fromFile(compressed);
                            Log.d("imageUri ", imageUri.getPath());
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d("imageUri ", "errrrror");
                        }
                         */


                        // TODO: Compressing - didn't get it

                        txtPictureAdding.setVisibility(View.GONE);
                        imgPictureAdding.setVisibility(View.VISIBLE);
                        Glide.with(view).load(imageUri).into(imgPictureAdding);
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startFabExtended();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }
}
