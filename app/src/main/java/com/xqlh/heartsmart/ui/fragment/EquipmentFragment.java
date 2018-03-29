package com.xqlh.heartsmart.ui.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xqlh.heartsmart.R;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class EquipmentFragment extends SupportFragment {


    public static EquipmentFragment newInstance() {
        // Required empty public constructor
        Bundle args = new Bundle();
        EquipmentFragment fragment = new EquipmentFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_equipment, container, false);
    }

}