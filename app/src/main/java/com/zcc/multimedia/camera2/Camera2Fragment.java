package com.zcc.multimedia.camera2;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zcc.multimedia.R;
import com.zcc.multimedia.databinding.FragmentCamera2Binding;

public class Camera2Fragment extends Fragment {

    private FragmentCamera2Binding fragmentCamera2Binding;

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentCamera2Binding = DataBindingUtil.inflate(inflater, R.layout.fragment_camera2, container, false);
        return fragmentCamera2Binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
