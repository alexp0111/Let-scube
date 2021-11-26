package com.example.lbar.fragments.mainMenuFragments.accountFragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.lbar.R;
import com.example.lbar.database.User;
import com.example.lbar.fragments.mainMenuFragments.accountFragments.LogInFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.lbar.MainActivity.dp_height;
import static com.example.lbar.MainActivity.dp_width;

public class RegistrationFragment extends Fragment {

    private FirebaseUser fUser;
    private FirebaseAuth mAuth;
    private String userID;

    private TextInputEditText txt_new_mail, txt_new_pass, txt_new_pass_check, txt_new_name, txt_new_birthday;
    private TextInputLayout l_txt_new_mail, l_txt_new_pass, l_txt_new_pass_check, l_txt_new_name, l_txt_new_birthday;
    private TextView textView2;
    private com.google.android.material.button.MaterialButton btn_new_reg;
    private com.google.android.material.progressindicator.LinearProgressIndicator progressBar;
    private Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        mAuth = FirebaseAuth.getInstance();

        toolbar = (Toolbar) view.findViewById(R.id.toolbar_in_registr);
        setToolbarSettings(toolbar);

        initItems(view);

        btn_new_reg.setOnClickListener(view1 -> {
            progressBar.setVisibility(View.VISIBLE);
            createNewUser();                                            // Создаём аккаунт
        });

        setItemAnimations();

        return view;
    }

    private void initItems(View v) {
        txt_new_mail = v.findViewById(R.id.et_new_mail);                       // Находим
        txt_new_pass = v.findViewById(R.id.et_new_pass);                       // edittext
        txt_new_pass_check = v.findViewById(R.id.et_new_pass_check);           //
        txt_new_name = v.findViewById(R.id.et_new_name);                       //
        txt_new_birthday = v.findViewById(R.id.et_new_birthday);               //

        l_txt_new_mail = v.findViewById(R.id.textField_email);                 // Находим
        l_txt_new_pass = v.findViewById(R.id.textField_pass);                  // edittext
        l_txt_new_pass_check = v.findViewById(R.id.textField_pass_check);      // layouts
        l_txt_new_name = v.findViewById(R.id.textField_name);                  //
        l_txt_new_birthday = v.findViewById(R.id.textField_birthday);          //

        btn_new_reg = v.findViewById(R.id.bt_new_registr);                     // button
        progressBar = v.findViewById(R.id.prog_bar_reg);

        textView2 = v.findViewById(R.id.textView2);                            // textView
    }

    private void setToolbarSettings(Toolbar tbar) {
        if (tbar != null) {

            tbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24);
            tbar.setTitle("Registration");

            tbar.setNavigationOnClickListener(view1 -> {
                try {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.alpha_to_high_1000, R.anim.to_top)
                            .replace(R.id.fragment_container,
                                    new LogInFragment()).commit();
                } catch (Exception D) {
                    Toast.makeText(getContext(), R.string.sww, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setItemAnimations() {
        l_txt_new_mail.setTranslationX(dp_width);
        l_txt_new_pass.setTranslationX(dp_width);
        l_txt_new_pass_check.setTranslationX(dp_width);
        l_txt_new_name.setTranslationX(dp_width);
        l_txt_new_birthday.setTranslationX(dp_width);

        btn_new_reg.setTranslationY(dp_height);
        textView2.setTranslationY(dp_height);


        l_txt_new_mail.setAlpha(1);
        l_txt_new_pass.setAlpha(1);
        l_txt_new_pass_check.setAlpha(1);
        l_txt_new_name.setAlpha(1);
        l_txt_new_birthday.setAlpha(1);

        btn_new_reg.setAlpha(1);
        textView2.setAlpha(1);


        l_txt_new_name.animate().translationX(0).alpha(1).setDuration(400).setStartDelay(300).start();
        l_txt_new_birthday.animate().translationX(0).alpha(1).setDuration(400).setStartDelay(350).start();
        l_txt_new_mail.animate().translationX(0).alpha(1).setDuration(400).setStartDelay(400).start();
        l_txt_new_pass.animate().translationX(0).alpha(1).setDuration(400).setStartDelay(450).start();
        l_txt_new_pass_check.animate().translationX(0).alpha(1).setDuration(400).setStartDelay(500).start();

        btn_new_reg.animate().translationY(0).alpha(1).setDuration(500).setStartDelay(700).start();
        textView2.animate().translationY(0).alpha(1).setDuration(500).setStartDelay(750).start();
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

        final String[] regItems = {str_mail, str_name, str_birthday, str_pass, str_pass_check};

        if (!emptyItemCheck(regItems)) {
            mAuth.createUserWithEmailAndPassword(str_mail, str_pass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Верификация через email          begin

                    fUser = FirebaseAuth.getInstance().getCurrentUser();
                    fUser.sendEmailVerification().addOnSuccessListener(aVoid ->
                            Log.d("email_ver", "success"))
                            .addOnFailureListener(e -> Log.d("email_ver", "failure"));

                    // Верификация через email          end

                    Toast.makeText(getContext(), R.string.user_registered, Toast.LENGTH_SHORT).show();

                    //Делаем запись в realtimeDB        begin

                    userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    User user = new User(str_name, str_mail, str_birthday, str_img, str_name_toLowerCase, "offline", userID);
                    FirebaseDatabase.getInstance("https://lbar-messenger-default-rtdb.firebaseio.com/")
                            .getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Log.d("create_user_in_realtimeDB", "success");
                        } else {
                            Log.d("create_user_in_realtimeDB", "failure");
                        }
                    });
                    progressBar.setVisibility(View.GONE);

                    //Делаем запись в realtimeDB        end


                    try { // Уходим на фрагмент входа в аккаунт
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new LogInFragment()).commit();
                    } catch (Exception D) {
                        Toast.makeText(getContext(), R.string.sww, Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }

                } else {
                    Toast.makeText(getContext(), R.string.sww, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private boolean emptyItemCheck(String[] items) {
        if (TextUtils.isEmpty(items[0])) {
            txt_new_mail.setError(getString(R.string.em_field));
            txt_new_mail.requestFocus();
            progressBar.setVisibility(View.GONE);
            return true;
        }
        if (TextUtils.isEmpty(items[1])) {
            txt_new_name.setError(getString(R.string.em_field));
            txt_new_name.requestFocus();
            progressBar.setVisibility(View.GONE);
            return true;
        }
        if (TextUtils.isEmpty(items[2])) {
            txt_new_birthday.setError(getString(R.string.em_field));
            txt_new_birthday.requestFocus();
            progressBar.setVisibility(View.GONE);
            return true;
        }
        if (TextUtils.isEmpty(items[3])) {
            txt_new_pass.setError(getString(R.string.em_field));
            txt_new_pass.requestFocus();
            progressBar.setVisibility(View.GONE);
            return true;
        }
        if (TextUtils.isEmpty(items[4])) {
            txt_new_pass_check.setError(getString(R.string.em_field));
            txt_new_pass_check.requestFocus();
            progressBar.setVisibility(View.GONE);
            return true;
        }
        if (!(items[3].equals(items[4]))) {
            txt_new_pass_check.setError(getString(R.string.comp_pass));
            txt_new_pass_check.requestFocus();
            progressBar.setVisibility(View.GONE);
            return true;
        }
        return false;
    }
}