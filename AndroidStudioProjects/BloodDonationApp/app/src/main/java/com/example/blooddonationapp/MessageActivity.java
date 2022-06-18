package com.example.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.blooddonationapp.Adapter.MessageAdapter;
import com.example.blooddonationapp.Model.User;
import com.example.blooddonationapp.Model.chat;
import com.example.blooddonationapp.Notification.APIservice;
import com.example.blooddonationapp.Notification.Data1;
import com.example.blooddonationapp.Notification.MyResponse;
import com.example.blooddonationapp.Notification.Token;
import com.example.blooddonationapp.Notification.client;
import com.example.blooddonationapp.Notification.sender;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

   private CircleImageView profileimg;
   private TextView username;
    private Toolbar toolBar;

    private ImageButton imageButton;
    private EditText editText;

    private FirebaseUser fuser;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;

    MessageAdapter messageAdapter;
    List<chat> chatList;
    RecyclerView recyclerView;

    String userid;

    APIservice apiService;

    boolean notify=false;





    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);



        toolBar=findViewById(R.id.mesotoolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        apiService= client.getclient("https://fcm.googleapis.com/").create(APIservice.class);

        profileimg=findViewById(R.id.mesoprofile);
        username=findViewById(R.id.sender);

        imageButton=findViewById(R.id.btn_send);

        recyclerView=findViewById(R.id.mesorecycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        editText=findViewById(R.id.text_send);

        intent= getIntent();

        String id= intent.getStringExtra("id");

        fuser=FirebaseAuth.getInstance().getCurrentUser();


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify=true;
                String msg=editText.getText().toString();

                if(!msg.equals("")){
                    sendMessage(fuser.getUid(),id,msg);

                }else {
                    Toast.makeText(MessageActivity.this,"Type message",Toast.LENGTH_SHORT).show();
                }
                editText.setText("");

            }
        });

        fuser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("users").child(id);


            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    username.setText(user.getName());

                    if (snapshot.hasChild("profileimage")) {
                        String imageurl = snapshot.child("profileimage").getValue().toString();
                        Glide.with(getApplicationContext()).load(imageurl).into(profileimg);
                    } else {
                        profileimg.setImageResource(R.drawable.profile);
                    }

                    readMessages(fuser.getUid(), id, user.getProfileimage());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("chatList")
                .child(fuser.getUid())
                .child(id);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(!snapshot.exists()){
                    ref.child("id").setValue(id);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void  sendMessage(String sender, String receiver, String message){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap= new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);

        reference.child("chats").push().setValue(hashMap);

        final String msg=message;
        reference= FirebaseDatabase.getInstance().getReference("users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if(notify) {
                    User user = snapshot.getValue(User.class);
                    sendNotification(receiver, user.getName(), msg);
                }
                notify=false;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void sendNotification(String receiver, final String username, final String message){

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query= tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Token token = dataSnapshot.getValue(Token.class);
                    Data1 data= new Data1(fuser.getUid(), R.mipmap.ic_launcher, username+": "+message,"New Message", userid);

                    sender sender1= new sender(data, token.getToken());

                    apiService.sendNotification(sender1)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code()==200){
                                        if(response.body().success !=1){
                                            Toast.makeText(MessageActivity.this, "failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMessages(String myid, String userid, String imageurl){
        chatList=new ArrayList<>();

        reference=FirebaseDatabase.getInstance().getReference("chats");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    chat shat=  dataSnapshot.getValue(chat.class);
                    if(shat.getReceiver().equals(myid) && shat.getSender().equals(userid)||shat.getReceiver().equals(userid)&&shat.getSender().equals(myid)){

                        chatList.add(shat);

                    }


                        messageAdapter= new MessageAdapter(MessageActivity.this, chatList,imageurl);
                        recyclerView.setAdapter(messageAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}