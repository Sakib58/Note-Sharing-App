package com.example.notesharingapp.ui.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.notesharingapp.CreateGroupDialog;
import com.example.notesharingapp.Group;
import com.example.notesharingapp.GroupListAdapter;
import com.example.notesharingapp.HomeActivity;
import com.example.notesharingapp.MainActivity;
import com.example.notesharingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    List<String> groupNames;

    FirebaseFirestore db;
    ProgressDialog progressDialog;
    String currentUserEmail = "";
    Context context;
    ArrayList<Group> list;
    List<String> allUsers;
    FloatingActionButton createGroup;
    SwipeRefreshLayout swipeRefreshLayout;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        ListView listView = root.findViewById(R.id.lv_group_list);
        db = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(getContext());
        context = new MainActivity();
        list = new ArrayList<Group>();
        groupNames = new ArrayList<>();
        allUsers = new ArrayList<String>();
        createGroup = root.findViewById(R.id.floatingActionButton5);
        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().startActivity(getActivity().getIntent());
                getActivity().finish();
                getActivity().overridePendingTransition(0, 0);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGroupCreateDialog();

            }
        });


        progressDialog.show();
        
        getCurrentUser();

        db.collection("groups")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()){
                    //Toast.makeText(getContext(), currentUserEmail, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        List<String> members = (List<String>) document.getData().get("members");
                        Group newGroup = new Group(document.getData().get("name").toString(),document.getData().get("description").toString(), (List<String>) document.getData().get("members"));
                        newGroup.setKey(document.getId());
                        if (members.contains(currentUserEmail)){
                            list.add(newGroup);
                            groupNames.add(document.getData().get("name").toString());
                        }

                    }
                    GroupListAdapter groupListAdapter = new GroupListAdapter(list,getContext());
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                            getActivity(), android.R.layout.simple_list_item_1,groupNames
                    );

                    listView.setAdapter(groupListAdapter);
                }else {
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                allUsers.add(document.getId());
                            }
                        }
                    }
                });





        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        return root;
    }

    private void openGroupCreateDialog() {
        CreateGroupDialog createGroupDialog = new CreateGroupDialog();
        createGroupDialog.show(getParentFragmentManager(),"create group");
    }

    public void getCurrentUser(){
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(MainActivity.SHARED_PREFS,0);
        currentUserEmail = sharedPreferences.getString(MainActivity.EMAIL,"Not found");

    }


}