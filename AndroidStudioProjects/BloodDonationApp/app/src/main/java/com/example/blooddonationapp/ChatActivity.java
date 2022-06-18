
package com.example.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.blooddonationapp.Adapter.UserAdapter;
import com.example.blooddonationapp.Model.User;
import com.example.blooddonationapp.Model.chat;
import com.example.blooddonationapp.Model.chatList;
import com.example.blooddonationapp.Notification.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> chatUsers;

    private Toolbar toolBar;

    FirebaseUser fuser;
    DatabaseReference reference;

    private List<chatList> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        toolBar=findViewById(R.id.chattoolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("CHATS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.chat_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();


        reference=FirebaseDatabase.getInstance().getReference("chatList").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    chatList chatlist= dataSnapshot.getValue(chatList.class);
                    usersList.add(chatlist);

                }
                ChatList();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        updateToken(FirebaseInstanceId.getInstance().getToken());


    }
    private void updateToken(String token){

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);


    }


    private void ChatList() {
        chatUsers=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatUsers.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                   User users= dataSnapshot.getValue(User.class);
                   for (chatList chatlist: usersList){
                       if(users.getId().equals(chatlist.getId())){
                           chatUsers.add(users);
                       }
                   }
                }

                userAdapter=new UserAdapter(ChatActivity.this,chatUsers);
                recyclerView.setAdapter(userAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}



