package com.example.lbar.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.lbar.Adapter.StatusAdapter;
import com.example.lbar.MainActivity;
import com.example.lbar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class LogInFragment extends Fragment {

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;

    private TextInputEditText txt_mail, txt_pass;
    private Toolbar toolbar;
    private DrawerLayout drawer;

    private static com.google.android.material.button.MaterialButton btn_reg, btn_enter, btn_res_pass;
    private com.google.android.material.progressindicator.LinearProgressIndicator progressBar;

    @Nullable
    @SuppressLint("SimpleDateFormat")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        AppCompatActivity main_activity = (MainActivity)getActivity();

        toolbar = (Toolbar) view.findViewById(R.id.toolbar_in_log_in);
        if (toolbar != null){
            activity.setSupportActionBar(toolbar);
            toolbar.setTitle("Log in");

            drawer = main_activity.findViewById(R.id.drawer_layout);
            //Objects.requireNonNull(activity.getSupportActionBar()).setDisplayShowTitleEnabled(false);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer, toolbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);

            toggle.syncState();
            drawer.addDrawerListener(toggle);
        }
        //Объявление элементов firebase
        database = FirebaseDatabase.getInstance("https://lbar-messenger-default-rtdb.firebaseio.com/");
        mAuth = FirebaseAuth.getInstance();
        //
        progressBar = view.findViewById(R.id.prog_bar_log_in);
        // Поля ввода
        txt_mail = view.findViewById(R.id.et_mail);
        txt_pass = view.findViewById(R.id.et_pass);
        // Кнопки входа и регистрации
        btn_reg = view.findViewById(R.id.btregistr);
        btn_enter = view.findViewById(R.id.btenter);
        btn_res_pass = view.findViewById(R.id.btreset_pass);
        // Обработчики кнопок
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new RegistrationFragment()).commit();
                } catch (Exception D) {
                    Toast.makeText(getContext(), R.string.sww, Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                loginUser();                                            // Входим в аккаунт
            }
        });
        btn_res_pass.setOnClickListener(new View.OnClickListener() {    // Изменяем пароль
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                resetPassword();
            }
        });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                    if (task.isSuccessful()){
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
