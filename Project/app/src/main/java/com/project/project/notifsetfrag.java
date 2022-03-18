package com.project.project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class notifsetfrag extends Fragment {
    public notifsetfrag(){}
    View fragmentviews;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        fragmentviews = inflater.inflate(R.layout.notificationsettingfragment, container, false);
        return fragmentviews;

    }

    @Override
    public void onStart() {
        super.onStart();
        Switch s = (Switch) fragmentviews.findViewById(R.id.notifswitch);
        SharedPreferences shared = getActivity().getSharedPreferences("notif", Context.MODE_PRIVATE);
        SharedPreferences.Editor e = shared.edit();

        String notif = shared.getString("notif", "null");

        if(notif.equals("true"))
            s.setChecked(true);
        else if(notif.equals("false"))
            s.setChecked(false);

        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String notif;

                if(isChecked == true)
                    notif = "true";
                else
                    notif = "false";

                e.putString("notif", notif);
                e.commit();
            }
        });
    }
}
