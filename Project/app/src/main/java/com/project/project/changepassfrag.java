package com.project.project;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class changepassfrag extends Fragment {
    public changepassfrag(){}
    View fragmentviews;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    DataSnapshot d;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        fragmentviews = inflater.inflate(R.layout.changepassfragment, container, false);
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
                ImageButton b = (ImageButton) fragmentviews.findViewById(R.id.changepassfinal);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences shared = getActivity().getSharedPreferences("username", Context.MODE_PRIVATE);
                        String username = shared.getString("username", "null");
                        EditText current = (EditText) fragmentviews.findViewById(R.id.CurrentPassword);
                        EditText newp = (EditText) fragmentviews.findViewById(R.id.NewPassword);
                        if(TextUtils.isEmpty(current.getText().toString()) | TextUtils.isEmpty(newp.getText().toString())){
                            Toast.makeText(getActivity(), "Please enter all the fields!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            String oldpass = d.child("user").child(username).child("password").getValue(String.class) + "";
                            if(oldpass.equals(current.getText().toString())){
                                String newpass = newp.getText().toString() + "";
                                ref.child("user").child(username).child("password").setValue(newpass);
                                Toast.makeText(getActivity(), "Password changed!", Toast.LENGTH_SHORT).show();
                                current.setText("");
                                newp.setText("");

                                getActivity().onBackPressed();
                            }
                            else{
                                Toast.makeText(getActivity(), "Wrong password!", Toast.LENGTH_SHORT).show();
                            }
                        }
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
