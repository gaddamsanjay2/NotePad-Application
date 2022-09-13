package com.example.notepad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UpdateActivity extends AppCompatActivity {


    private EditText titleEdt,contentEdt;
    ImageView img;
    private String titlestr,contentstr,imgstr,id1;
    private FirebaseFirestore db;
    ModelClass modelClass;
    List<ModelClass> modelClassList=new ArrayList<>();
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);



        Intent i=getIntent();

        titlestr=i.getStringExtra("title");
        contentstr=i.getStringExtra("content");
        id1=i.getStringExtra("id");
        imgstr=i.getStringExtra("imgg");
        db = FirebaseFirestore.getInstance();

        titleEdt=findViewById(R.id.titleud1);
        contentEdt=findViewById(R.id.descud1);
        img=findViewById(R.id.imgud1);

        // creating variable for button
        Button updateBtn = findViewById(R.id.updatesbuttonidd);
        Button deletebtn=findViewById(R.id.deletedbutton);
        Button cancelbtn=findViewById(R.id.cancelbutton);

        titleEdt.setText(titlestr);
        contentEdt.setText(contentstr);

        Glide.with(this).load(imgstr).into(img);


        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateActivity.this,MainActivity2.class);
                startActivity(intent);
            }
        });



        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //deleteData(modelClass);
                ProgressDialog progressDialog=new ProgressDialog(view.getContext());
                progressDialog.setTitle("Deleting...");
                FirebaseFirestore.getInstance().collection("NotePad").document(id1).delete();
                finish();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titlestr = titleEdt.getText().toString();
                contentstr= contentEdt.getText().toString();
                imgstr=img.getResources().toString();

                if (TextUtils.isEmpty( titlestr)) {
                    titleEdt.setError("Please enter Title");
                } else if (TextUtils.isEmpty(contentstr)) {
                    contentEdt.setError("Please enter Description");
                }  else {
                    updateCourses();

                }


            }
        });
    }

    private void updateCourses() {

        try {
            FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
            ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("Updating the Data almost Done");
            progressDialog.show();

            ModelClass modelClass=new ModelClass(id1,titlestr,contentstr,firebaseAuth.getUid());
            FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();

            firebaseFirestore.collection("NotePad").document(id1).update("title",""+titleEdt.getText().toString(),"content",""+contentEdt.getText().toString())


//            firebaseFirestore.collection("Notes").document(id1).set(modelClass)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Toast.makeText(UpdateActivity.this, "successfully updated", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UpdateActivity.this, "fail to update", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }





}
