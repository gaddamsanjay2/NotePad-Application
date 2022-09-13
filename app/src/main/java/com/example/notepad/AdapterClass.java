package com.example.notepad;

import static android.os.Build.ID;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterClass extends RecyclerView.Adapter<AdapterClass.MyHolder> {

    Context context;
    List<ModelClass> modelClassList;

    String titleStr,contentStr;
    private FirebaseFirestore firebaseFirestore;

    AlertDialog alertDialog;

    public AdapterClass(Context context) {
        this.context = context;
      modelClassList=new ArrayList<>();
    }

    public void add(ModelClass modelClass)
    {
        modelClassList.add(modelClass);
        notifyDataSetChanged();
    }
    public void clear()
    {
        modelClassList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_layout,parent,false);
        return new MyHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        ModelClass modelClass = modelClassList.get(position);
        holder.Name_txt.setText(modelClassList.get(position).getTitle());
        holder.Content_txt.setText(modelClassList.get(position).getContent());


        Glide.with(context).load(modelClassList.get(position).getImg()).into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, UpdateActivity.class);

                i.putExtra("id", modelClass.getId());
                i.putExtra("title", modelClass.getTitle());
                i.putExtra("content", modelClass.getContent());
                i.putExtra("imgg", modelClass.getImg());
                context.startActivity(i);
            }
        });


    }



    @Override
    public int getItemCount() {
        return  modelClassList.size();
    }



    public class MyHolder extends RecyclerView.ViewHolder {
        TextView Name_txt, Content_txt;
        ImageView imageView;

        // RecyclerView recyclerView;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            Name_txt = itemView.findViewById(R.id.name);
            Content_txt = itemView.findViewById(R.id.content);

            imageView = itemView.findViewById(R.id.recyclerimg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ModelClass modelClass = modelClassList.get(getAdapterPosition());


                }
            });
        }
    }


}
