package com.example.lbar.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.example.lbar.R;
import com.example.lbar.database.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RegistrationFragment extends Fragment {

    private TextInputEditText txt_new_mail, txt_new_pass, txt_new_pass_check, txt_new_name, txt_new_birthday;
    private static com.google.android.material.button.MaterialButton btn_new_reg;
    private com.google.android.material.progressindicator.LinearProgressIndicator progressBar;

    private FirebaseAuth mAuth;
    private FirebaseUser fUser;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        mAuth = FirebaseAuth.getInstance();                                 // Цепляем БД
        storage = FirebaseStorage.getInstance("gs://lbar-messenger.appspot.com");
        storageReference = storage.getReference("camera.png");

        txt_new_mail = view.findViewById(R.id.et_new_mail);                 // Находим edittext
        txt_new_pass = view.findViewById(R.id.et_new_pass);                 // Находим edittext
        txt_new_pass_check = view.findViewById(R.id.et_new_pass_check);     // Находим edittext
        txt_new_name = view.findViewById(R.id.et_new_name);                 // Находим edittext
        txt_new_birthday = view.findViewById(R.id.et_new_birthday);         // Находим edittext

        btn_new_reg = view.findViewById(R.id.bt_new_registr);               // Находим button регистрации
        progressBar = view.findViewById(R.id.prog_bar_reg);

        btn_new_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                createNewUser();                                            // Создаём аккаунт
            }
        });
        return view;
    }

    private void createNewUser() {
        final String str_mail, str_pass, str_pass_check, str_name, str_birthday, str_img, str_name_toLowerCase;

        str_name = txt_new_name.getText().toString();
        str_name_toLowerCase = str_name.toLowerCase();
        str_mail = txt_new_mail.getText().toString();
        str_pass = txt_new_pass.getText().toString();
        str_pass_check = txt_new_pass_check.getText().toString();
        str_birthday = txt_new_birthday.getText().toString();
        str_img = getString(R.string.add_url);

        if (TextUtils.isEmpty(str_mail)) {                          // Проверяем на пустоту
            txt_new_mail.setError(getString(R.string.em_field));
            txt_new_mail.requestFocus();
            progressBar.setVisibility(View.GONE);
        } else if (TextUtils.isEmpty(str_name)) {                   // Проверяем на пустоту
            txt_new_pass.setError(getString(R.string.em_field));
            txt_new_pass.requestFocus();
            progressBar.setVisibility(View.GONE);
        } else if (TextUtils.isEmpty(str_birthday)) {                   // Проверяем на пустоту
            txt_new_pass.setError(getString(R.string.em_field));
            txt_new_pass.requestFocus();
            progressBar.setVisibility(View.GONE);
        } else if (TextUtils.isEmpty(str_pass)) {                   // Проверяем на пустоту
            txt_new_pass.setError(getString(R.string.em_field));
            txt_new_pass.requestFocus();
            progressBar.setVisibility(View.GONE);
        } else if (TextUtils.isEmpty(str_pass_check)) {             // Проверяем на пустоту
            txt_new_pass_check.setError(getString(R.string.em_field));
            txt_new_pass_check.requestFocus();
            progressBar.setVisibility(View.GONE);
        } else if (!(str_pass.equals(str_pass_check))) {            // Пароли одинаковы?
            txt_new_pass_check.setError(getString(R.string.comp_pass));
            txt_new_pass_check.requestFocus();
            progressBar.setVisibility(View.GONE);
        } else {
            mAuth.createUserWithEmailAndPassword(str_mail, str_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                        // Верификация через email          begin
                        fUser = FirebaseAuth.getInstance().getCurrentUser();
                        fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("email_ver", "success");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("email_ver", "failure");
                            }
                        });
                        // Верификация через email          end

                        Toast.makeText(getContext(), R.string.user_registered, Toast.LENGTH_SHORT).show();


                        //Делаем запись в realtimeDB
                        User user = new User(str_name, str_mail, str_birthday, str_img, str_name_toLowerCase, "offline");
                        FirebaseDatabase.getInstance("https://lbar-messenger-default-rtdb.firebaseio.com/").getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()){
                                        Log.d("create_user_in_realtimeDB", "success");
                                    } else {
                                        Log.d("create_user_in_realtimeDB", "failure");
                                    }
                                });
                        progressBar.setVisibility(View.GONE);
                        //Делаем запись в realtimeDB


                        try { // Уходим на фрагмент входа в аккаунт
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new ProfileFragment()).commit();
                        } catch (Exception D){
                            Toast.makeText(getContext(), R.string.sww, Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    } else {
                        Toast.makeText(getContext(), R.string.sww, Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }

    }
}
