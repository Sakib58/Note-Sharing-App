package com.example.notesharingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static android.os.Environment.DIRECTORY_DOCUMENTS;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class CreateFileActivity extends AppCompatActivity {

    FloatingActionButton importText;

    EditText mResultsET;
    ImageView mPreviewIV;
    TextView pdfName;

    public String dataFormat = "Text";

    Uri image_uri;

    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=400;
    private static final int IMAGE_PICK_GALLERY_CODE=1000;
    private static final int IMAGE_PICK_CAMERA_CODE=1001;

    Uri imageUri;

    Document document;

    String cameraPermission[];
    String storagePermission[];

    Button addPdf,savePdf;

    SpeechRecognizer speechRecognizer;
    ImageButton btnMic;

    ArrayList<Integer> columnNum;
    ArrayList<Integer> rowNum;

    PdfPTable table;

    private static final String TAG = "MainActivity";
    private static final int requestPermissionID = 101;

    String filename;

    int count = 0;

    String groupKey,groupName;

    FirebaseStorage storage;
    StorageReference storageReference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_file);

        mResultsET=findViewById(R.id.resultEt);
        mPreviewIV=findViewById(R.id.imageIv);

        pdfName = findViewById(R.id.pdf_name);

        progressDialog = new ProgressDialog(this);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        SharedPreferences sharedPreferences = getSharedPreferences("group_info",0);
        groupName = sharedPreferences.getString("group_name"," ");
        groupKey = sharedPreferences.getString("group_key"," ");



        setPdfName();


        cameraPermission=new String[] {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        btnMic = findViewById(R.id.btn_mic);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(CreateFileActivity.this);
        Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count == 0){
                    btnMic.setImageDrawable(getDrawable(R.drawable.mic_on));
                    count = 1;
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
                else {
                    btnMic.setImageDrawable(getDrawable(R.drawable.mic_off));
                    count = 0;
                    speechRecognizer.stopListening();
                    btnMic.setVisibility(View.INVISIBLE);
                }
            }
        });

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(speechRecognizer.RESULTS_RECOGNITION);
                mResultsET.setText(data.get(0));

            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        addPdf = findViewById(R.id.btn_add_pdf);
        savePdf = findViewById(R.id.btn_save_pdf);

        addPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dataFormat.equals("Text")){
                    try {
                        document.add(new Paragraph(mResultsET.getText().toString()));
                    } catch (DocumentException e) {
                        Toast.makeText(CreateFileActivity.this, "Adding error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                else if (dataFormat.equals("Image")){
                    try {
                        Image image = Image.getInstance(imageUri.getPath());
                        image.scaleToFit(document.getPageSize().getWidth()-10,document.getPageSize().getHeight());
                        document.add(image);
                    } catch (BadElementException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                }
                else if (dataFormat.equals("Table")){
                    try {
                        document.add(table);
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                }

                mResultsET.setText("");
            }
        });

        savePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                document.close();
                Toast.makeText(CreateFileActivity.this, "PDF File: "+filename, Toast.LENGTH_SHORT).show();
                shareFile();
                //UploadFile();
            }
        });




        Toast.makeText(this, getIntent().getStringExtra("group_key"), Toast.LENGTH_SHORT).show();


    }

    private void shareFile() {
        UploadFile();
    }

    private void UploadFile() {
        Uri filepath = Uri.fromFile(new File(Environment.getExternalStorageDirectory().toString()+"/"+DIRECTORY_DOCUMENTS+"/"+filename));
        if (filepath != null) {

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SS");
            String strDate = sdf.format(cal.getTime());
            System.out.println("Current date in String Format: "+strDate);




            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference sref = storageReference.child(groupKey+"/"+filename);

            sref.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Uploaded Succesfully", Toast.LENGTH_SHORT).show();
                            //groupInfo = new ArrayList<File>();
                            //getFiles();
                            Intent intent = new Intent(CreateFileActivity.this,GroupActivity.class);
                            intent.putExtra("group_key",groupKey);
                            intent.putExtra("group_name",groupName);
                            startActivity(intent);
                            Toast.makeText(CreateFileActivity.this, "XXXXX:"+groupKey+"  YYY "+groupName, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
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

    private void setPdfName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText tv = new EditText(this);
        tv.setHint("Enter pdf file name");
        builder.setTitle("File name");
        builder.setView(tv);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                pdfName.setText(tv.getText().toString());
                openPdf();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                pdfName.setText("unnamed");
                openPdf();
            }
        });
        builder.show();
    }

    private void openPdf() {
        document = new Document();
        String mFilename = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        File path = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS);

        filename = pdfName.getText().toString()+mFilename+".pdf";

        File file = new File(path, filename);

        try {
            PdfWriter.getInstance(document,new FileOutputStream(file));

        } catch (DocumentException e) {
            Toast.makeText(this, "Document exception:"+e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "File not found:"+e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        document.open();
    }


    private void showPopup(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.import_text_options, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.import_text_options,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.scan_image:
                dataFormat = "Text";
                showImageImportDialog();
                return true;
            case R.id.scan_audio:
                mPreviewIV.setVisibility(View.INVISIBLE);
                dataFormat = "Text";
                btnMic.setVisibility(View.VISIBLE);
                return true;
            case R.id.import_image:
                dataFormat = "Image";
                showImageImportDialog();

                return true;
            case R.id.insert_table:
                dataFormat = "Table";
                columnNum = new ArrayList<Integer>();
                rowNum = new ArrayList<Integer>();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater layoutInflater = getLayoutInflater();
                View view = layoutInflater.inflate(R.layout.table_size,null);
                EditText col = view.findViewById(R.id.et_table_col);
                EditText row = view.findViewById(R.id.et_table_row);
                builder.setView(view)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (col.getText().toString().equals("") || row.getText().toString().equals("")){
                                    Toast.makeText(CreateFileActivity.this, "Enter all fields", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    for ( int k= 0 ;k<Integer.parseInt(col.getText().toString());k++){
                                        columnNum.add(k);
                                    }
                                    for ( int k= 0 ;k<Integer.parseInt(row.getText().toString());k++){
                                        rowNum.add(k);
                                    }
                                    createTable();
                                }
                            }
                        });
                builder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void createTable() {
        System.out.println(columnNum+" x"+rowNum);
        //columnNum = 3;
        //rowNum = 2;
        table = new PdfPTable(columnNum.size());

        columnNum.forEach(ind ->{
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            final EditText title = new EditText(this);
            alert.setTitle("Enter title of column "+(ind+1));
            title.setHint("Title of column");
            alert.setView(title);
            alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {


                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(title.getText().toString()));
                    table.addCell(header);
                }
            });
            alert.show();
        });

        columnNum.forEach(ind->{
            rowNum.forEach(jnd->{
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                final EditText title = new EditText(this);
                alert.setTitle("Enter value of cell row="+jnd+" column="+ind);
                title.setHint("Enter value");
                alert.setView(title);
                alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.WHITE);
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(title.getText().toString()));
                        table.addCell(header);
                    }
                });
                alert.show();
            });
        });


    }

    private void showImageImportDialog() {
        String[] items={"Camera","Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    //Toast.makeText(MainActivity.this, "Camera is clicked", Toast.LENGTH_SHORT).show();
                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else{
                        pickCamera();
                    }
                }
                else if (which == 1){
                    //Toast.makeText(MainActivity.this, "Gallery is clicked", Toast.LENGTH_SHORT).show();
                    if (!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else{
                        pickGallery();
                    }
                }
            }
        });
        builder.create().show();
    }



    private void pickGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);

    }

    private boolean checkStoragePermission() {
        boolean result= ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
        return result;
    }

    private void pickCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"NewPic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image to text");
        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);
    }


    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result= ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED;
        boolean result1= ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
        return result && result1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted){
                        pickCamera();
                    }
                    else {
                        Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0){
                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){
                        pickGallery();
                    }
                    else {
                        Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                CropImage.activity(image_uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri result_uri = result.getUri();

                mPreviewIV.setImageURI(result_uri);
                imageUri = result_uri;

                BitmapDrawable bitmapDrawable = (BitmapDrawable) mPreviewIV.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                if (!recognizer.isOperational()) {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                } else {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);

                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < items.size(); i++) {
                        TextBlock myItem = items.valueAt(i);
                        sb.append(myItem.getValue());
                        sb.append("\n");
                    }
                    if (dataFormat.equals("Image")){
                        mResultsET.setText(imageUri.getPath());
                        mPreviewIV.setVisibility(View.VISIBLE);
                    }else{
                        mResultsET.setText(sb.toString());
                    }

                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}