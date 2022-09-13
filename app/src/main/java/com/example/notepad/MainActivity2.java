package com.example.notepad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity2 extends AppCompatActivity {

    Button Add;

    FirebaseFirestore firebaseFirestore;
    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    ModelClass modelClass;
    AdapterClass adapterClass;
    List<ModelClass> modelClassList = new ArrayList<>();
    AlertDialog alertDialog;

    EditText titleedt, contentedt;


    String titleStr, contentStr;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference("ODS/");
    private Button btnSelect, btnUpload;
    private ImageView imageView;

    ImageView imgages;
    // Uri indicates, where the image will be picked from
    private Uri filePath;
    FirebaseAuth firebaseAuth;
    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    ImageView setImage;
    Uri uri;
    String filenameStr, filenameStr1, id1;
    //Storage
    StorageReference storageReference;
    UploadTask uploadTask;
    FirebaseStorage firebaseStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Add = findViewById(R.id.ADDbtn);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        progressDialog.dismiss();

        imgages = findViewById(R.id.recyclerimg);
        recyclerView = findViewById(R.id.recycler);

        firebaseFirestore = FirebaseFirestore.getInstance();
        modelClassList = new ArrayList<>();


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterClass = new AdapterClass(this);
        recyclerView.setAdapter(adapterClass);



     }
        public void showAddNote (View view){


            // Create an alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // set the custom layout
            final View customLayout = getLayoutInflater().inflate(R.layout.alert_dialog, null);
            builder.setView(customLayout);

            firebaseStorage = FirebaseStorage.getInstance();
            //Storage location Created in Firebase Storage
            storageReference = firebaseStorage.getReference("ODS");

            firebaseFirestore = FirebaseFirestore.getInstance();

            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);

            Button save = customLayout.findViewById(R.id.save);
            Button cancel = customLayout.findViewById(R.id.canceladddata);
            titleedt = customLayout.findViewById(R.id.title_edt);
            contentedt = customLayout.findViewById(R.id.content_edt);


//            ActionBar actionBar;
//            actionBar = getSupportActionBar();
//            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#0F9D58"));
//            actionBar.setBackgroundDrawable(colorDrawable);

            btnSelect = customLayout.findViewById(R.id.chooseFile);
            setImage = customLayout.findViewById(R.id.setimageid);


            btnSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // SelectImage();

                    pickImageFromGallery();
                }
            });

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // getting data from edittext fields.
                    titleStr = titleedt.getText().toString();
                    contentStr = contentedt.getText().toString();


                    // validating the text fields if empty or not.
                    if (TextUtils.isEmpty(titleStr)) {
                        titleedt.setError("Please enter Course Name");
                    } else if (TextUtils.isEmpty(contentStr)) {
                        contentedt.setError("Please enter Course Description");
                    } else {
                        // calling method to add data to Firebase Firestore.
                        // addDataToFirestore(titlestr, descstr);

                        uploadtoStorage();

                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();


        }

        private void uploadtoStorage () {
            try {
                progressDialog.show();
                progressDialog.setMessage("Image Uploading...");

                filenameStr = titleedt.getText().toString();
                filenameStr1 = contentedt.getText().toString();

                StorageReference riversRef = storageReference.child(uri.getLastPathSegment());
                uploadTask = riversRef.putFile(uri);

                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity2.this, "" + exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        // ...
                        progressDialog.dismiss();

                        downloadURL(uploadTask, riversRef);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    private void downloadURL(UploadTask uploadTask, StorageReference riversRef) {
            try {
                progressDialog.show();
                progressDialog.setMessage("downloading url..");

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        progressDialog.dismiss();
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return riversRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            String myurl = downloadUri.toString();
                            cloudFireStore(myurl);

                        } else {
                            // Handle failures
                            // ...
                            Toast.makeText(MainActivity2.this, "No url found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    private void cloudFireStore(String myurl) {
        try {
            FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
            ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("Saving Data almost Done");
            progressDialog.show();

            String noteId=UUID.randomUUID().toString();
            ModelClass modelClass=new ModelClass(noteId,titleStr,contentStr,myurl,firebaseAuth.getUid());
            FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();

            firebaseFirestore.collection("NotePad").document(noteId).set(modelClass)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Toast.makeText(MainActivity2.this, "successfully stored the data", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
//                            finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity2.this, "fail to store", Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void pickImageFromGallery() {
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,202);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case 202:
                if(resultCode==RESULT_OK&&data!=null&&data.getData()!=null)
                {
                    uri=data.getData();
                    setImage.setImageURI(uri);
                    //Log.d("Name0",""+uri);

                    //path.setText("Select Path is "+uri);
                    // getfileName(uri);
                }
                else
                {
                    Toast.makeText(this, "File not Choose", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        FirebaseFirestore.getInstance().collection("NotePad")
                .whereEqualTo("uid",FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dlist=queryDocumentSnapshots.getDocuments();
                        adapterClass.clear();
                        for (int i=0;i<dlist.size();i++)
                        {
                            DocumentSnapshot documentSnapshot=dlist.get(i);
                            ModelClass modelClass=documentSnapshot.toObject(ModelClass.class);
                            adapterClass.add(modelClass);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity2.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}










