package com.example.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {
    private TextView already2;
    private Button button;
    private CircleImageView profile_pic;
    private TextInputEditText username3, email3, phone3, password3, password4;
    private Spinner bloodgroups1;

    private ProgressDialog loader2;

    private Uri resulturi;

    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseRef;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        already2=findViewById(R.id.already2);
        button=findViewById(R.id.registerbtn);
        profile_pic=findViewById(R.id.profile_image);
        username3=findViewById(R.id.username);
        email3=findViewById(R.id.email);
        phone3=findViewById(R.id.number);
        password3=findViewById(R.id.password);
        password4=findViewById(R.id.password2);
        bloodgroups1=findViewById(R.id.bloodgroup);

        loader2=new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();

        profile_pic.setOnClickListener(new View.OnClickListener() {
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
               final String password31=password3.getText().toString().trim();
               final String password41=password4.getText().toString().trim();
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
               if (TextUtils.isEmpty(password31)){
                   password3.setError("password is required");
                   return;
               }
               if(TextUtils.isEmpty(password41)){
                   password4.setError("confirm password");
                   return;
               }
               if(bloodgrouup.equals("SELECT YOUR BLOOD GROUP")){
                   Toast.makeText(RegistrationActivity.this,"SELECT BLOOD GROUP",Toast.LENGTH_SHORT);
                   return;
               }
               else{

                   loader2.setMessage("Registering you");
                   loader2.setCanceledOnTouchOutside(false);
                   loader2.show();

                   mAuth.createUserWithEmailAndPassword(email31,password31).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {

                           if(!task.isSuccessful()){
                               String error=task.getException().toString();
                               Toast.makeText(RegistrationActivity.this,"Error"+error,Toast.LENGTH_SHORT);
                           }else{

                               String CurrentUserId=mAuth.getCurrentUser().getUid();
                               userDatabaseRef= FirebaseDatabase.getInstance().getReference()
                                       .child("users").child(CurrentUserId);

                               HashMap userInfo=new HashMap();
                               userInfo.put("id",CurrentUserId);
                               userInfo.put("name",username31);
                               userInfo.put("email",email31);
                               userInfo.put("number",phone31);
                               userInfo.put("bloodgroup",bloodgrouup);
                               //userInfo.put("search",bloodgrouup);
                              // userInfo.put("password",password31);

                               userDatabaseRef.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                                   @Override
                                   public void onComplete(@NonNull Task task) {
                                       if(task.isSuccessful()){
                                           Toast.makeText(RegistrationActivity.this,"Data set successfully",Toast.LENGTH_SHORT);
                                       }
                                       else {
                                           Toast.makeText(RegistrationActivity.this,task.getException().toString(),Toast.LENGTH_SHORT);
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
                                           Toast.makeText(RegistrationActivity.this,"Upload failed",Toast.LENGTH_SHORT);


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
                                                                 Toast.makeText(RegistrationActivity.this,"Image uploaded successfuly",Toast.LENGTH_SHORT);
                                                             }else{
                                                                 Toast.makeText(RegistrationActivity.this,task.getException().toString(),Toast.LENGTH_SHORT);
                                                             }

                                                         }
                                                     });
                                                     finish();
                                                }
                                            });

                                        }

                                       }
                                   });




                                   Intent intent=new Intent(RegistrationActivity.this, HomeActivity.class);
                                   startActivity(intent);
                                       finish();
                                       loader2.dismiss();

                               }

                           }


                       }
                   });


               }


            }
        });

        already2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RegistrationActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK && data !=null){
            resulturi =data.getData();
            profile_pic.setImageURI(resulturi);
        }
    }
}