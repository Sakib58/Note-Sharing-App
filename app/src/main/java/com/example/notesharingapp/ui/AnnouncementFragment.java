package com.example.notesharingapp.ui;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.notesharingapp.MainActivity;
import com.example.notesharingapp.Post;
import com.example.notesharingapp.PostListAdapter;
import com.example.notesharingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnnouncementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnnouncementFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String groupKey;
    String currentUserName;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ListView postView;
    FirebaseFirestore db;
    ProgressDialog progressDialog;

    Button postAnnouncement;
    EditText writeAnnouncement;

    public AnnouncementFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AnnouncementFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AnnouncementFragment newInstance(String param1, String param2) {
        AnnouncementFragment fragment = new AnnouncementFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            groupKey = this.getArguments().getString("group_key");
            //Toast.makeText(getContext(), "First param of announcement:"+this.getArguments().getString("group_key"), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_announcement, container, false);

        postView = view.findViewById(R.id.lv_post_view);
        db = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(getContext());
        groupKey = this.getArguments().getString("group_key");
        writeAnnouncement = view.findViewById(R.id.et_write_announcement);
        postAnnouncement = view.findViewById(R.id.btn_post_announcement);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS,0);
        currentUserName = sharedPreferences.getString(MainActivity.NAME,"Un named");

        postAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Post post = new Post(currentUserName,writeAnnouncement.getText().toString(),groupKey, Timestamp.now());
                db.collection("posts")
                        .add(post);
            }
        });

        db.collection("posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<Post> posts = new ArrayList<Post>();
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot doc : task.getResult()){
                                if(doc.getData().get("key").toString().equals(groupKey) ){

                                    Post post = new Post(doc.getData().get("author").toString(),doc.getData().get("body").toString(),groupKey,doc.getTimestamp("time"));
                                    post.setKey(groupKey);
                                    posts.add(post);
                                }
                            }
                        }
                        PostListAdapter postListAdapter = new PostListAdapter(posts,getContext());
                        postView.setAdapter(postListAdapter);
                    }
                });

        return view;
    }
}