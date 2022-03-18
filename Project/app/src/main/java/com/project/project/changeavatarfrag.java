package com.project.project;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class changeavatarfrag extends Fragment {
    public changeavatarfrag(){}
    View fragmentviews;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    DataSnapshot d;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        fragmentviews = inflater.inflate(R.layout.changeavatarfragment, container, false);
        return fragmentviews;
    }

    @Override
    public void onStart() {
        super.onStart();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("Users Updated.");
                d = dataSnapshot;
                SharedPreferences shared = getActivity().getSharedPreferences("username", Context.MODE_PRIVATE);
                String username = shared.getString("username", "null");

                Button b = (Button) fragmentviews.findViewById(R.id.avatarsave);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Spinner s = fragmentviews.findViewById(R.id.avatarspinner);
                        String avatar = s.getSelectedItem().toString();
                        if(avatar.equals("Sally"))
                            ref.child("user").child(username).child("avatar").setValue("1");
                        else if(avatar.equals("Mila"))
                            ref.child("user").child(username).child("avatar").setValue("2");
                        else if(avatar.equals("Issam"))
                            ref.child("user").child(username).child("avatar").setValue("3");
                        else if(avatar.equals("Ahmad"))
                            ref.child("user").child(username).child("avatar").setValue("4");
                        else if(avatar.equals("Ajaipal"))
                            ref.child("user").child(username).child("avatar").setValue("5");
                        else if(avatar.equals("Flora"))
                            ref.child("user").child(username).child("avatar").setValue("6");

                        //getActivity().onBackPressed();
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                System.out.println("Failed to read value.");
            }
        });
    }
}
