package com.example.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {


    private Button button;
    private CircleImageView profile_pic2;
    private TextInputEditText username3, email3, phone3;
    private Spinner bloodgroups1;

    private ProgressDialog loader2;

    private Uri resulturi;

    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        button=findViewById(R.id.registerbtn9);
        profile_pic2=findViewById(R.id.profile_image9);
        username3=findViewById(R.id.username9);
        email3=findViewById(R.id.email9);
        phone3=findViewById(R.id.number9);

        bloodgroups1=findViewById(R.id.bloodgroup9);

        loader2=new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();

        profile_pic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username31 = username3.getText().toString().trim();
                final String email31=email3.getText().toString().trim();
                final String phone31=phone3.getText().toString().trim();

                final String bloodgrouup=bloodgroups1.getSelectedItem().toString();

                if(TextUtils.isEmpty(username31)){
                    username3.setError("Username is required");
                    return;
                }
                if(TextUtils.isEmpty(email31)){
                    email3.setError("email is required");
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email31).matches()){
                    email3.setError("provide valid email");
                    return;

                }
                if (TextUtils.isEmpty(phone31)){
                    phone3.setError("phone number is required");
                    return;
                }

                if(bloodgrouup.equals("SELECT YOUR BLOOD GROUP")){
                    Toast.makeText(ProfileActivity.this,"SELECT BLOOD GROUP",Toast.LENGTH_SHORT);
                    return;
                }
              else{

                    loader2.setMessage("Updating");
                    loader2.setCanceledOnTouchOutside(false);
                    loader2.show();


                    DatabaseReference userReference= FirebaseDatabase.getInstance().getReference().child("users").child(
                            FirebaseAuth.getInstance().getCurrentUser().getUid()
                    );
                    userReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String CurrentUserId=mAuth.getCurrentUser().getUid();
                            userDatabaseRef= FirebaseDatabase.getInstance().getReference()
                                    .child("users").child(CurrentUserId);

                            HashMap userInfo=new HashMap();
                            userInfo.put("id",CurrentUserId);
                            userInfo.put("name",username31);
                            userInfo.put("email",email31);
                            userInfo.put("number",phone31);
                            userInfo.put("bloodgroup",bloodgrouup);


                            userDatabaseRef.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if(task.isSuccessful()){

                                        Intent intent =new Intent(ProfileActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(ProfileActivity.this,task.getException().toString(),Toast.LENGTH_SHORT);
                                    }
                                    finish();
                                    //loader2.dismiss();
                                }
                            });

                            if(resulturi!=null){
                                final StorageReference filepath= FirebaseStorage.getInstance().getReference()
                                        .child("profile_images").child(CurrentUserId);
                                Bitmap bitmap=null;
                                try{
                                    bitmap= MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),resulturi);
                                }catch (IOException e){
                                    e.printStackTrace();
                                }
                                ByteArrayOutputStream byteArrayOutputStream= new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG,20,byteArrayOutputStream);
                                byte[]data= byteArrayOutputStream.toByteArray();

                                UploadTask uploadTask= filepath.putBytes(data);

                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ProfileActivity.this,"Upload failed",Toast.LENGTH_SHORT).show();


                                    }
                                });
                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        if(taskSnapshot.getMetadata()!=null&&taskSnapshot.getMetadata().getReference()!=null){
                                            Task<Uri> result=taskSnapshot.getStorage().getDownloadUrl();
                                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String imageuri=uri.toString();
                                                    Map getimagemap=new HashMap();
                                                    getimagemap.put("profileimage",imageuri);

                                                    userDatabaseRef.updateChildren(getimagemap).addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {
                                                            if(task.isSuccessful()){
                                                                Toast.makeText(ProfileActivity.this,"Image uploaded successfuly",Toast.LENGTH_SHORT);
                                                            }else{
                                                                Toast.makeText(ProfileActivity.this,task.getException().toString(),Toast.LENGTH_SHORT);
                                                            }

                                                        }
                                                    });
                                                    finish();
                                                }
                                            });

                                        }

                                    }
                                });


                                Intent intent=new Intent(ProfileActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                                loader2.dismiss();




                            }



                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

            }
        });




    }
}