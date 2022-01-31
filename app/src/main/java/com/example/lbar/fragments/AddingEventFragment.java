package com.example.lbar.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.lbar.MainActivity;
import com.example.lbar.R;
import com.example.lbar.fragments.mainMenuFragments.EventFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddingEventFragment extends Fragment {

    private FloatingActionButton fabExtended;
    private FloatingActionButton fabApply;
    private FloatingActionButton fabDisable;

    private boolean fabFlag = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adding_event, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        AppCompatActivity main_activity = (MainActivity) getActivity();

        fabExtended = view.findViewById(R.id.event_push);
        fabApply = view.findViewById(R.id.event_apply);
        fabDisable = view.findViewById(R.id.event_disable);

        Animation startExtendedFabAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.from_bottom);
        Animation closeExtendedFabAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.to_top);
        fabExtended.startAnimation(startExtendedFabAnimation);

        closeExtendedFabAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                //
                // Небольшой буг - при простом сворачивании менюшки происходит выход из фрагмента
                //

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

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });



        Animation rotateExtendedFabAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
        Animation rotateBackExtendedFabAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_back);


        Animation startApplyFabAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fab_from_bottom_lvl2);
        Animation startDisableFabAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fab_from_bottom_lvl1);

        Animation closeApplyFabAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fab_to_bottom_lvl2);
        Animation closeDisableFabAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fab_to_bottom_lvl1);

        rotateExtendedFabAnimation.setDuration(200);
        rotateBackExtendedFabAnimation.setDuration(200);

        rotateBackExtendedFabAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fabExtended.startAnimation(closeExtendedFabAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fabExtended.setOnClickListener(view1 -> {
            if (!fabFlag){
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

        fabApply.setOnClickListener(view12 -> {
            fabExtended.startAnimation(rotateBackExtendedFabAnimation);

            fabApply.startAnimation(closeApplyFabAnimation);
            fabDisable.startAnimation(closeDisableFabAnimation);

            fabApply.setVisibility(View.INVISIBLE);
            fabDisable.setVisibility(View.INVISIBLE);

            fabFlag = false;
        });

        fabDisable.setOnClickListener(view13 -> {
            fabExtended.startAnimation(rotateBackExtendedFabAnimation);

            fabApply.startAnimation(closeApplyFabAnimation);
            fabDisable.startAnimation(closeDisableFabAnimation);

            fabApply.setVisibility(View.INVISIBLE);
            fabDisable.setVisibility(View.INVISIBLE);

            fabFlag = false;
        });

        return view;
    }
}
