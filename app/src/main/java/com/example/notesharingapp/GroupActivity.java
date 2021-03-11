package com.example.notesharingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notesharingapp.ui.AnnouncementFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity {
    Intent intent;
    TabLayout tabLayout;
    ViewPager viewPager;
    TextView groupName;
    FirebaseFirestore db;
    List<String> allUserList;
    ProgressDialog progressDialog;
    List<String> groupMembersList;
    String currentUserEmail;
    boolean okLeave = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group2);
        intent = getIntent();
        //Toast.makeText(this, "Data: "+intent.getStringExtra("group_key"), Toast.LENGTH_SHORT).show();
        groupName = findViewById(R.id.tv_group_name);
        groupName.setText(intent.getStringExtra("group_name"));
        allUserList = new ArrayList<>();
        groupMembersList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS,0);
        currentUserEmail = sharedPreferences.getString(MainActivity.EMAIL,"");

        db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot doc : task.getResult()){
                                allUserList.add(doc.getId());
                            }
                        }
                    }
                });
        db.collection("groups")
                .document(intent.getStringExtra("group_key"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            groupMembersList = (ArrayList<String>) task.getResult().get("members");
                        }
                    }
                });

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        ArrayList<String> tabList = new ArrayList<>();
        tabList.add("Announcements");
        tabList.add("Discussion");
        tabList.add("Files");
        
        prepareViewPager(viewPager,tabList);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.item1:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                final EditText input = new EditText(this);
                input.setHint("Enter email");
                String s="your text";
                alert.setTitle("Add new member");

                alert.setView(input);
                alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString().trim();
                        //Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
                        if (allUserList.contains(value)){
                            progressDialog.show();

                            db.collection("groups")
                                    .document(intent.getStringExtra("group_key"))
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()){
                                                String groupName = task.getResult().get("name").toString();
                                                String groupDescription = task.getResult().get("description").toString();
                                                ArrayList<String> members = (ArrayList<String>) task.getResult().get("members");
                                                members.add(value);

                                                db.collection("groups")
                                                        .document(intent.getStringExtra("group_key"))
                                                        .set(new Group(groupName,groupDescription,members))
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    Toast.makeText(GroupActivity.this, "Added new user", Toast.LENGTH_SHORT).show();
                                                                    finish();
                                                                    startActivity(getIntent());
                                                                }
                                                                else {
                                                                    Toast.makeText(GroupActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                                }
                                                                progressDialog.dismiss();
                                                            }
                                                        });
                                            }else {
                                                Toast.makeText(GroupActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                            }
                                            progressDialog.dismiss();
                                        }
                                    });

                        }else {
                            Toast.makeText(GroupActivity.this, "This email does not have any account!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
                alert.show();
                //Toast.makeText(this, "Item 1 selected", Toast.LENGTH_SHORT).show();

                return true;
            case R.id.item2:
                AlertDialog.Builder alert2 = new AlertDialog.Builder(this);
                alert2.setTitle("Remove a member");
                final EditText input2 = new EditText(this);
                input2.setHint("Enter email");
                String s2="your text";

                alert2.setView(input2);
                alert2.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        progressDialog.show();
                        String value = input2.getText().toString().trim();

                        if(allUserList.contains(value)){
                            db.collection("groups")
                                    .document(intent.getStringExtra("group_key"))
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                String groupName = task.getResult().get("name").toString();
                                                String groupDescription = task.getResult().get("description").toString();
                                                ArrayList<String> members = (ArrayList<String>) task.getResult().get("members");
                                                //members.add(value);
                                                if (members.contains(value)) {
                                                    members.remove(value);
                                                    db.collection("groups")
                                                            .document(intent.getStringExtra("group_key"))
                                                            .set(new Group(groupName, groupDescription, members))
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(GroupActivity.this, "Removed this user", Toast.LENGTH_SHORT).show();
                                                                        finish();
                                                                        startActivity(getIntent());
                                                                    } else {
                                                                        Toast.makeText(GroupActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    progressDialog.dismiss();
                                                                }
                                                            });
                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(GroupActivity.this, "This user isn't a member of this group", Toast.LENGTH_SHORT).show();
                                                }


                                            } else {
                                                Toast.makeText(GroupActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                            }
                                            progressDialog.dismiss();
                                        }
                                    });
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(GroupActivity.this, "This email does not have any account!", Toast.LENGTH_SHORT).show();
                        }

                        //Toast.makeText(con, value, Toast.LENGTH_SHORT).show();
                    }
                });
                alert2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
                alert2.show();
                //Toast.makeText(getApplicationContext(),"Item 2 Selected",Toast.LENGTH_LONG).show();
                return true;
            case R.id.item3:

                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
                myAlertDialog.setTitle("Leave group");
                myAlertDialog.setMessage("Are you sure to leave this group?");
                myAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        // do something when the OK button is clicked
                        db.collection("groups")
                                .document(intent.getStringExtra("group_key"))
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            String groupName = task.getResult().get("name").toString();
                                            String groupDescription = task.getResult().get("description").toString();
                                            ArrayList<String> members = (ArrayList<String>) task.getResult().get("members");
                                            //members.add(value);
                                            if (members.contains(currentUserEmail)) {
                                                members.remove(currentUserEmail);
                                                db.collection("groups")
                                                        .document(intent.getStringExtra("group_key"))
                                                        .set(new Group(groupName, groupDescription, members))
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(GroupActivity.this, "Removed this user", Toast.LENGTH_SHORT).show();
                                                                    startActivity(new Intent(GroupActivity.this,HomeActivity.class));
                                                                } else {
                                                                    Toast.makeText(GroupActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                                }
                                                                progressDialog.dismiss();
                                                            }
                                                        });
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(GroupActivity.this, "This user isn't a member of this group", Toast.LENGTH_SHORT).show();
                                            }


                                        } else {
                                            Toast.makeText(GroupActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                        }
                                        progressDialog.dismiss();
                                    }
                                });

                    }});
                myAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        // do something when the Cancel button is clicked
                    }});
                myAlertDialog.show();


                return true;
            case R.id.item4:
                AlertDialog.Builder alert4 = new AlertDialog.Builder(this);
                final ListView out4 = new ListView(this);
                alert4.setTitle("Member list");
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        groupMembersList
                );
                out4.setAdapter(arrayAdapter);
                alert4.setView(out4);
                alert4.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                alert4.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void prepareViewPager(ViewPager viewPager, ArrayList<String> tabList) {
        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        AnnouncementFragment announcementFragment = new AnnouncementFragment();
        DiscussionFragment discussionFragment = new DiscussionFragment();
        FilesFragment filesFragment = new FilesFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putString("title",tabList.get(0));
        bundle1.putString("group_key",intent.getStringExtra("group_key"));
        Bundle bundle2 = new Bundle();
        bundle2.putString("title",tabList.get(1));
        bundle2.putString("group_key",intent.getStringExtra("group_key"));
        Bundle bundle3 = new Bundle();
        bundle3.putString("title",tabList.get(2));
        bundle3.putString("group_key",intent.getStringExtra("group_key"));

        announcementFragment.setArguments(bundle1);
        discussionFragment.setArguments(bundle2);
        filesFragment.setArguments(bundle3);

        tabAdapter.addFragment(announcementFragment,tabList.get(0));
        tabAdapter.addFragment(discussionFragment,tabList.get(1));
        tabAdapter.addFragment(filesFragment,tabList.get(2));

        announcementFragment = new AnnouncementFragment();
        discussionFragment = new DiscussionFragment();
        filesFragment = new FilesFragment();

        viewPager.setAdapter(tabAdapter);

        /*for (int i=0;i<tabList.size();i++){
            Bundle bundle = new Bundle();
            bundle.putString("title",tabList.get(i));
            announcementFragment.setArguments(bundle);
            tabAdapter.addFragment(announcementFragment,tabList.get(i));
            announcementFragment = new AnnouncementFragment();
        }
        viewPager.setAdapter(tabAdapter);*/
    }

    private class TabAdapter extends FragmentPagerAdapter {
        ArrayList<String> arrayList = new ArrayList<>();
        List<Fragment> fragmentList = new ArrayList<>();

        public void addFragment(Fragment fragment,String title){
            arrayList.add(title);
            fragmentList.add(fragment);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        public TabAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return arrayList.get(position);
        }
    }
}