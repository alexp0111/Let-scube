package com.example.lbar.fragments.mainMenuFragments.accountFragments;

import android.annotation.SuppressLint;
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
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.lbar.adapter.StatusAdapter;
import com.example.lbar.MainActivity;
import com.example.lbar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.lbar.MainActivity.dp_height;
import static com.example.lbar.MainActivity.dp_width;

public class LogInFragment extends Fragment {

    private FirebaseAuth mAuth;

    private TextInputEditText txt_mail, txt_pass;
    private TextInputLayout layout_txt_mail, layout_txt_pass;
    private TextView txt_you_can_also;

    private static com.google.android.material.button.MaterialButton btn_reg, btn_enter, btn_res_pass;
    private com.google.android.material.progressindicator.LinearProgressIndicator progressBar;

    @Nullable
    @SuppressLint("SimpleDateFormat")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        AppCompatActivity main_activity = (MainActivity) getActivity();

        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_in_log_in);
        setToolbarSettings(toolbar, activity, main_activity);

        initItems(view);

        btn_reg.setOnClickListener(view1 -> {                       // Регитсрация
            try {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.from_bottom, R.anim.alpha_to_low)
                        .replace(R.id.fragment_container, new RegistrationFragment()).commit();
            } catch (Exception D) {
                Toast.makeText(getContext(), R.string.sww, Toast.LENGTH_SHORT).show();
            }
        });

        btn_enter.setOnClickListener(view12 -> {
            progressBar.setVisibility(View.VISIBLE);
            loginUser();                                            // Входим в аккаунт
        });

        btn_res_pass.setOnClickListener(view13 -> {                 // Изменяем пароль
            progressBar.setVisibility(View.VISIBLE);
            resetPassword();
        });

        setItemAnimations();

        return view;
    }

    private void initItems(View v) {
        progressBar = v.findViewById(R.id.prog_bar_log_in);
        // Поля ввода
        txt_mail = v.findViewById(R.id.et_mail);
        layout_txt_mail = v.findViewById(R.id.textField_name);
        txt_pass = v.findViewById(R.id.et_pass);
        layout_txt_pass = v.findViewById(R.id.textField_pass);
        // Кнопки входа и регистрации
        btn_reg = v.findViewById(R.id.btregistr);
        btn_enter = v.findViewById(R.id.btenter);
        btn_res_pass = v.findViewById(R.id.btreset_pass);
        txt_you_can_also = v.findViewById(R.id.txt_you_can_also);
    }

    private void setToolbarSettings(Toolbar tbar, AppCompatActivity activity, AppCompatActivity main_activity) {
        if (tbar != null) {
            activity.setSupportActionBar(tbar);
            tbar.setTitle(R.string.title_login);

            DrawerLayout drawer = main_activity.findViewById(R.id.drawer_layout);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer, tbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);

            toggle.syncState();
            drawer.addDrawerListener(toggle);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    private void setItemAnimations() {
        layout_txt_mail.setTranslationX(dp_width);
        layout_txt_pass.setTranslationX(dp_width);
        btn_enter.setTranslationX(dp_width);

        txt_you_can_also.setTranslationY(dp_height);
        btn_reg.setTranslationY(dp_height);
        btn_res_pass.setTranslationY(dp_height);


        layout_txt_mail.setAlpha(1);
        layout_txt_pass.setAlpha(1);
        btn_enter.setAlpha(1);

        txt_you_can_also.setAlpha(1);
        btn_reg.setAlpha(1);
        btn_res_pass.setAlpha(1);


        layout_txt_mail.animate().translationX(0).alpha(1).setDuration(400).setStartDelay(300).start();
        layout_txt_pass.animate().translationX(0).alpha(1).setDuration(400).setStartDelay(400).start();
        btn_enter.animate().translationX(0).alpha(1).setDuration(400).setStartDelay(500).start();

        txt_you_can_also.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(700).start();
        btn_reg.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(800).start();
        btn_res_pass.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(900).start();
    }

    private void loginUser() {
        String mail, pass;

        mail = txt_mail.getText().toString();
        pass = txt_pass.getText().toString();

        if (TextUtils.isEmpty(mail)) {                          // Обработчик пустоты
            txt_mail.setError(getString(R.string.em_field));
            txt_mail.requestFocus();
            progressBar.setVisibility(View.GONE);
        } else if (TextUtils.isEmpty(pass)) {                   // Обработчик пустоты
            txt_pass.setError(getString(R.string.em_field));
            txt_pass.requestFocus();
            progressBar.setVisibility(View.GONE);
        } else {
            mAuth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        try {                                    // Летим в фрагмент "Вход выполнен"
                            StatusAdapter adapter = new StatusAdapter();
                            adapter.setUs_status("online");
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new ProfileFragment()).commit();
                            progressBar.setVisibility(View.GONE);
                        } catch (Exception D) {
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

    private void resetPassword() {
        String mail, pass;

        mail = txt_mail.getText().toString();
        pass = txt_pass.getText().toString();

        if ((TextUtils.isEmpty(pass)) && (!TextUtils.isEmpty(mail))) {                          // Обработчик пустоты
            mAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("reset_things", "Success");
                        Toast.makeText(getContext(), R.string.cye, Toast.LENGTH_LONG).show();
                    } else {
                        Log.d("reset_things", "smth went wrong t1");
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            Log.d("reset_things", "smth went wrong t2");
            Toast.makeText(getContext(), R.string.instr_to_reset, Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    }
}
