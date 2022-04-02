package com.example.lbar.fragments.mainMenuFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.lbar.MainActivity;
import com.example.lbar.R;
import com.example.lbar.helpClasses.Cube;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.lbar.MainActivity.SWIPE_THRESHOLD;
import static com.example.lbar.MainActivity.SWIPE_VELOCITY_THRESHOLD;

public class CollectionFragment extends Fragment implements GestureDetector.OnGestureListener {

    private DatabaseReference collectionReference;
    private String userID;

    private ArrayList<Cube> allCubesArray = new ArrayList<Cube>(11);

    private DrawerLayout drawer;
    private ScrollView sv;

    private static MaterialCardView cubeType0;
    private static MaterialCardView cubeType1;
    private static MaterialCardView cubeType2;
    private static MaterialCardView cubeType3;
    private static MaterialCardView cubeType4;
    private static MaterialCardView cubeType5;
    private static MaterialCardView cubePyraminx;
    private static MaterialCardView cubeSqube;
    private static MaterialCardView cubeClock;
    private static MaterialCardView cubeMegaminx;
    private static MaterialCardView cubeSquare1;

    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;
    private TextView txt_avg;
    private TextView txt_best;
    private TextView tv;
    private ImageView deleteInfo;

    private MaterialAlertDialogBuilder mdBuilder;
    private View rl;
    private TextInputEditText et;

    private List<MaterialCardView> mcvList;

    private GestureDetector gestureDetector;

    private com.google.android.material.progressindicator.LinearProgressIndicator progressBar;
    private SwipeRefreshLayout srl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);
        rl = inflater.inflate(R.layout.dialog_renaming, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        AppCompatActivity main_activity = (MainActivity) getActivity();

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_in_collection);
        setToolbarSettings(toolbar, activity, main_activity);

        initItems(view);

        srl.setOnRefreshListener(() -> {
            Snackbar.make(view, "All is up do date", BaseTransientBottomBar.LENGTH_SHORT).show();
            srl.setRefreshing(false);
        });

        realiseClickListenerOnCards();
        //setCollectionValueEventListener(); TODO: Разобраться с свайпрефрешлайаутами. (создают новый листенер, хотя он и так риалтайме)

        gestureDetector = new GestureDetector(getContext(), this);

        SwipeMenuOpenerControl(sv);

        return view;
    }

    private void initItems(View v) {
        progressBar = v.findViewById(R.id.prog_bar_collection);
        srl = v.findViewById(R.id.pull_to_refresh_collection);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        collectionReference = FirebaseDatabase.getInstance("https://lbar-messenger-default-rtdb.firebaseio.com/")
                .getReference("Users").child(userID).child("Collection");

        sv = v.findViewById(R.id.scrollView_in_collection);
        et = rl.findViewById(R.id.et_corrected_name);

        cubeType0 = v.findViewById(R.id.collection_card_type0);
        cubeType1 = v.findViewById(R.id.collection_card_type1);
        cubeType2 = v.findViewById(R.id.collection_card_type2);
        cubeType3 = v.findViewById(R.id.collection_card_type3);
        cubeType4 = v.findViewById(R.id.collection_card_type4);
        cubeType5 = v.findViewById(R.id.collection_card_type5);

        cubePyraminx = v.findViewById(R.id.collection_card_pyraminx);
        cubeSqube = v.findViewById(R.id.collection_card_sqube);
        cubeClock = v.findViewById(R.id.collection_card_clock);
        cubeMegaminx = v.findViewById(R.id.collection_card_megaminx);
        cubeSquare1 = v.findViewById(R.id.collection_card_square1);

        mcvList = Arrays.asList(cubeType0, cubeType1, cubeType2, cubeType3, cubeType4,
                cubeType5, cubePyraminx, cubeSqube, cubeClock, cubeMegaminx, cubeSquare1);

        bottomSheetDialog = new BottomSheetDialog(
                requireContext(), R.style.BottomSheetDialogTheme
        );
        bottomSheetView = LayoutInflater.from(getContext()).inflate(
                R.layout.bottom_dialog_in_cubes, v.findViewById(R.id.bottom_dialog_container)
        );

        tv = bottomSheetView.findViewById(R.id.text_test);
        txt_avg = bottomSheetView.findViewById(R.id.info_in_collection_avg);
        txt_best = bottomSheetView.findViewById(R.id.info_in_collection_best);
        deleteInfo = bottomSheetView.findViewById(R.id.delete_puzzle_info);
    }

    private void realiseClickListenerOnCards() {
        for (int i = 0; i < mcvList.size(); i++) {
            int finalI = i;
            mcvList.get(i).setOnClickListener(view1 -> {
                progressBar.setVisibility(View.VISIBLE);
                collectionReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        allCubesArray.clear();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Cube cube = snapshot.getValue(Cube.class);
                            if (cube != null) {
                                allCubesArray.add(cube);
                            }
                        }
                        showDialog(finalI);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Snackbar.make(getView(), "Похоже, отсутствует подключение к интернету",
                                BaseTransientBottomBar.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            });
        }
    }

    private void showDialog(int n) {
        try {
            tv.setText(allCubesArray.get(n).getCube_name());
            txt_avg.setText(allCubesArray.get(n).avgInfo());
            txt_best.setText(allCubesArray.get(n).bestInfo());
        } catch (IndexOutOfBoundsException e) {
            tv.setText(R.string.sww);
        }

        tv.setOnClickListener(view1 -> {
            mdBuilder = new MaterialAlertDialogBuilder(getContext());

            et.setText(tv.getText());
            et.setSelection(et.getText().length());

            mdBuilder.setTitle("Cube name");
            mdBuilder.setMessage("Here you can set the name of your puzzle");
            mdBuilder.setBackground(getResources().getDrawable(R.drawable.dialog_drawable, null));

            if (rl.getParent() != null) {
                ((ViewGroup) rl.getParent()).removeView(rl);
            }
            mdBuilder.setView(rl);

            mdBuilder.setPositiveButton("APPlY", (dialogInterface, i) -> {
                progressBar.setVisibility(View.VISIBLE);

                tv.setText(et.getText().toString());
                collectionReference.child(String.valueOf(n)).child("cube_name").setValue(et.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            });

            mdBuilder.show();
        });

        deleteInfo.setOnClickListener(view2 -> {
            mdBuilder = new MaterialAlertDialogBuilder(getContext());

            mdBuilder.setTitle("Delete statistic");
            mdBuilder.setMessage("Are you sure you want to delete all statistics for this puzzle?");
            mdBuilder.setBackground(getResources().getDrawable(R.drawable.dialog_drawable, null));

            mdBuilder.setNegativeButton("Yes, i'm sure", (dialogInterface, i) -> {
                clearPuzzleStatisic(n);
                txt_avg.setText("Not enough info\nNot enough info\nNot enough info\nNot enough info\nNot enough info");
                txt_best.setText("Not enough info\nNot enough info\nNot enough info\nNot enough info\nNot enough info");
            });

            mdBuilder.show();
        });

        bottomSheetDialog.setContentView(bottomSheetView);

        bottomSheetDialog.show();
    }

    private void clearPuzzleStatisic(int n) {
        ArrayList<Long> arrBest = new ArrayList<Long>(5);
        ArrayList<Long> arrAvg = new ArrayList<Long>(100);

        for (int i = 0; i < 100; i++) {
            arrAvg.add(-1L);
        }
        for (int i = 0; i < 5; i++) {
            arrBest.add(-1L);
        }

        FirebaseDatabase.getInstance("https://lbar-messenger-default-rtdb.firebaseio.com/")
                .getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Collection").child(String.valueOf(n)).child("puzzle_build_avg_statistics")
                .setValue(arrAvg)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("clear_avg_statistics", "success");
                    } else {
                        Log.d("clear_avg_statistics", "failure");
                    }
                });
        FirebaseDatabase.getInstance("https://lbar-messenger-default-rtdb.firebaseio.com/")
                .getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Collection").child(String.valueOf(n)).child("puzzle_build_pb_statistics")
                .setValue(arrBest)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("clear_pb_statistics", "success");
                    } else {
                        Log.d("clear_pb_statistics", "failure");
                    }
                });
    }

    private void SwipeMenuOpenerControl(View v) {
        v.setOnTouchListener((view1, motionEvent) -> {
            view1.performClick();
            gestureDetector.onTouchEvent(motionEvent);
            return false;
        });
    }

    private void setToolbarSettings(Toolbar tbar, AppCompatActivity activity, AppCompatActivity main_activity) {
        if (tbar != null) {
            activity.setSupportActionBar(tbar);
            tbar.setTitle(R.string.title_collection);

            drawer = main_activity.findViewById(R.id.drawer_layout);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer, tbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);

            toggle.syncState();
            drawer.addDrawerListener(toggle);
        }
    }

    @Override
    public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
        boolean res = false;
        float diffY = 0;
        float diffX = 0;
        try {
            diffY = moveEvent.getY() - downEvent.getY();
            diffX = moveEvent.getX() - downEvent.getX();
        } catch (Exception e) {
            Log.d("Collection", e.toString());
        }

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
}
