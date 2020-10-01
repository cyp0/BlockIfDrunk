package com.example.byd.aplication.ui.home.startEngine;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.byd.R;
import com.example.byd.aplication.ui.home.startEngine.devicesList.DevicesFragment;
import com.example.byd.databinding.StartCartFragmentBinding;

public class StartCartFragment extends Fragment {

    private StartCartViewModel mViewModel;
    private Button startButton;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    public static StartCartFragment newInstance() {
        return new StartCartFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        StartCartFragmentBinding binding = StartCartFragmentBinding.inflate(inflater, container, false);
        startButton = binding.startCartButton;
        fragmentManager = getParentFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction.replace(R.id.containerOfFragments, new DevicesFragment(),"start").addToBackStack("start");
                fragmentTransaction.commit();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(StartCartViewModel.class);
        // TODO: Use the ViewModel
    }

}
