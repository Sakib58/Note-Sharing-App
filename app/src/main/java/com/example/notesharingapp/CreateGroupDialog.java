package com.example.notesharingapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.notesharingapp.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CreateGroupDialog extends AppCompatDialogFragment {
    EditText groupName,groupDescription;
    FirebaseFirestore db;
    String currentUserEmail;
    List<String> members;
    ProgressDialog progressDialog;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.create_group,null);
        members = new ArrayList<String>();
        groupName = view.findViewById(R.id.et_group_name);
        groupDescription = view.findViewById(R.id.et_group_description);
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(MainActivity.SHARED_PREFS,0);
        currentUserEmail = sharedPreferences.getString(MainActivity.EMAIL,"");
        members.add(currentUserEmail);
        progressDialog = new ProgressDialog(getActivity());
        builder.setView(view)
                .setPositiveButton("Create Group", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Group newGroup = new Group(groupName.getText().toString(),groupDescription.getText().toString(), members);
                        db.collection("groups")
                                .add(newGroup)
                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if(task.isSuccessful()){

                                        }
                                    }
                                });
                        Toast.makeText(getContext(), "Name:" +groupName.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        return builder.create();
    }
}
