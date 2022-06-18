package com.example.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.example.blooddonationapp.Adapter.UserAdapter;
import com.example.blooddonationapp.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class categories_selected extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;

    private List<User> userList;
    private UserAdapter userAdapter;

    private String title="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_selected);

        toolbar=findViewById(R.id.selectedtoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        recyclerView=findViewById(R.id.selectorecycler);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        userList= new ArrayList<>();
        userAdapter= new UserAdapter(categories_selected.this, userList);
        recyclerView.setAdapter(userAdapter);
        

            if(getIntent().getExtras()!=null){
                title=getIntent().getStringExtra("group");
                getSupportActionBar().setTitle("Blood group "+ title );


                readUsers();



            }


    }

    private void readUsers() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("users");

                Query query= reference.orderByChild("bloodgroup").equalTo(title);

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userList.clear();
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                            User user=dataSnapshot.getValue(User.class);
                            userList.add(user);
                        }
                        userAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






    }
}