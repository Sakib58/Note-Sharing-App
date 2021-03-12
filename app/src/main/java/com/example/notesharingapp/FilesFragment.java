package com.example.notesharingapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FilesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilesFragment extends Fragment{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    boolean bool1=false,boo2=false,bool3=false;
    List<String> name,link;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String groupKey;

    FloatingActionButton btnUploadFile;
    private Uri filepath;

    private final int PICK_PDF_CODE = 2342;
    FirebaseStorage storage;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    ArrayList<File> groupInfo;
    ListView fileListView;

    Button checkBtn,createFile;

    public FilesFragment() {
        // Required empty public constructor
    }




    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FilesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FilesFragment newInstance(String param1, String param2) {
        FilesFragment fragment = new FilesFragment();
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
        }
        name=new ArrayList<>();
        link = new ArrayList<>();
        groupInfo = new ArrayList<File>();
        getFiles();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_files, container, false);
        fileListView = view.findViewById(R.id.lv_file_list);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        btnUploadFile = view.findViewById(R.id.fab_upload_file);
        checkBtn = view.findViewById(R.id.btn_check_btn);
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setGroupInfo();
            }
        });
        btnUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseDoc();
                //Toast.makeText(view.getContext(), "Upload file is clicked", Toast.LENGTH_SHORT).show();
            }
        });
        createFile = view.findViewById(R.id.btn_create_file);
        groupKey = this.getArguments().getString("group_key");
        createFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),CreateFileActivity.class);
                intent.putExtra("group_key",groupKey);
                startActivity(intent);
            }
        });
        return view;
    }

    public void getFiles(){
        groupInfo = new ArrayList<File>();
        name = new ArrayList<String>();
        link = new ArrayList<String>();
        storage = FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        groupKey = this.getArguments().getString("group_key");
        //Toast.makeText(getContext(), "First param of files:"+this.getArguments().getString("group_key"), Toast.LENGTH_SHORT).show();
        StorageReference sref = storage.getReference().child(groupKey);

                sref.listAll()
                        .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                            @Override
                            public void onSuccess(ListResult listResult) {

                                for (StorageReference item : listResult.getItems()) {

                                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            link.add(uri.toString());
                                        }
                                    });
                                    item.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                                        @Override
                                        public void onSuccess(StorageMetadata storageMetadata) {
                                            name.add(storageMetadata.getName());

                                        }
                                    });
                                }
                                //progressDialog.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println("Some error occur");
                            }
                        });

        progressDialog.dismiss();
    }

    private void setGroupInfo() {
        groupInfo = new ArrayList<File>();

            for(int i=0;i<name.size();i++){
                groupInfo.add(new File(name.get(i),link.get(i)));
            }
            FileListAdapter fileListAdapter = new FileListAdapter(groupInfo,getContext());
            fileListView.setAdapter(fileListAdapter);
            progressDialog.dismiss();

    }


    private void chooseDoc() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Document"), PICK_PDF_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (data.getData() != null) {
                filepath=data.getData();
                //System.out.println(filepath);
                AlertDialog.Builder alert4 = new AlertDialog.Builder(getContext());
                final EditText out4 = new EditText(getContext());
                out4.setHint("Enter filename to save");
                alert4.setTitle("Upload file");
                alert4.setView(out4);

                alert4.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        UploadFile(out4.getText().toString());
                    }
                });

                alert4.show();
            } else
                Toast.makeText(getContext(), "NO FILE CHOSEN", Toast.LENGTH_SHORT).show();
        }
    }

    private String formatDate(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

    private void UploadFile(String filename) {
        if (filepath != null) {

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SS");
            String strDate = sdf.format(cal.getTime());
            System.out.println("Current date in String Format: "+strDate);


            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference sref = storageReference.child(groupKey+"/"+filename + strDate + ".pdf");

            sref.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Uploaded Succesfully", Toast.LENGTH_SHORT).show();
                            //groupInfo = new ArrayList<File>();
                            getFiles();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() )/ taskSnapshot
                                    .getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });


        }
    }

}