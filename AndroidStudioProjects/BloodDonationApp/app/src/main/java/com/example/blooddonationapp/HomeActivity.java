package com.example.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.blooddonationapp.Adapter.UserAdapter;
import com.example.blooddonationapp.Model.User;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.constraintlayout.motion.widget.Debug.getLocation;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolBar;
    private NavigationView nav_view;


    private CircleImageView nav_profile_image;
    private TextView nav_username,nav_bloodgroup,nav_email;

    private RecyclerView recyclerview;

    private ProgressBar progressBar;


    private List<User> userList;
    private UserAdapter userAdapter;
    private Button message;

    private LocationManager locationManager;

    private DatabaseReference userRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // getSupportActionBar().hide();
        setContentView(R.layout.activity_home);

        toolBar=(Toolbar) findViewById(R.id.toolBar2);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("Blood Donation App");

        drawerLayout=findViewById(R.id.drawerLayout1);
        nav_view=findViewById(R.id.navigationBar1);
        progressBar=findViewById(R.id.load1);
        message=findViewById(R.id.message1);

        recyclerview=findViewById(R.id.recylerView);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerview.setLayoutManager(layoutManager);

        userList= new ArrayList<>();


        userAdapter=new UserAdapter(HomeActivity.this,userList);

        recyclerview.setAdapter(userAdapter);


        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                userList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){

                    User user=dataSnapshot.getValue(User.class);
                    userList.add(user);
                }
                //userList.clear();
                userAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                if(userList.isEmpty()){
                    Toast.makeText(HomeActivity.this,"No user found",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(HomeActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

        nav_view.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(HomeActivity.this,drawerLayout,toolBar,R.string.navigation_drawer_open,R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        nav_profile_image=nav_view.getHeaderView(0).findViewById(R.id.userprofile);
        nav_username=nav_view.getHeaderView(0).findViewById(R.id.nav_username);
        nav_email=nav_view.getHeaderView(0).findViewById(R.id.nav_email);
        nav_bloodgroup=nav_view.getHeaderView(0).findViewById(R.id.nav_blood_group);


        userRef= FirebaseDatabase.getInstance().getReference().child("users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid()
        );

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    String name= snapshot.child("name").getValue().toString();
                    nav_username.setText(name);

                    String email= snapshot.child("email").getValue().toString();
                    nav_email.setText(email);

                    String bloodgroup= snapshot.child("bloodgroup").getValue().toString();
                    nav_bloodgroup.setText(bloodgroup);




                    if(snapshot.hasChild("profileimage")) {
                        String imageurl = snapshot.child("profileimage").getValue().toString();
                        Glide.with(getApplicationContext()).load(imageurl).into(nav_profile_image);
                    }else{
                        nav_profile_image.setImageResource(R.drawable.profile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();


        getLocations();

    }
    private DatabaseReference ref;

    private void getLocations() {




        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        ref=FirebaseDatabase.getInstance().getReference("location").child(firebaseUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {




                    LocationCallback locationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {

                            if (locationResult == null) {
                                return;
                            }
                            Location driverLocation = locationResult.getLastLocation();

                                final String value_lat = String.valueOf(driverLocation.getLatitude());
                                final String value_lng = String.valueOf(driverLocation.getLongitude());

                                ref.child(firebaseUser.getUid()).child("Latitude").setValue(value_lat);
                                ref.child(firebaseUser.getUid()).child("Longitude").setValue(value_lng);

                                Toast.makeText(HomeActivity.this,"Location Updated",Toast.LENGTH_SHORT).show();

                        }
                    };
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
       switch (item.getItemId()){
           case R.id.message1:
               Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
               startActivity(intent);
               break;



           case R.id.profile:
               Intent intent30 = new Intent(HomeActivity.this, ProfileActivity.class);
               startActivity(intent30);
               break;



           case R.id.emergency:
               Intent intent11 = new Intent(HomeActivity.this, MapsActivity.class);
               startActivity(intent11);
               break;

           case R.id.aplus:
               Intent intent3 = new Intent(HomeActivity.this, categories_selected.class);
               intent3.putExtra("group","A+");
               startActivity(intent3);
               break;

           case R.id.aminus:
               Intent intent4 = new Intent(HomeActivity.this, categories_selected.class);
               intent4.putExtra("group","A-");
               startActivity(intent4);
               break;
           case R.id.abplus:
               Intent intent5 = new Intent(HomeActivity.this, categories_selected.class);
               intent5.putExtra("group","AB+");
               startActivity(intent5);
               break;
           case R.id.abminus:
               Intent intent6 = new Intent(HomeActivity.this, categories_selected.class);
               intent6.putExtra("group","AB-");
               startActivity(intent6);
               break;
           case R.id.bminus:
               Intent intent7 = new Intent(HomeActivity.this, categories_selected.class);
               intent7.putExtra("group","B-");
               startActivity(intent7);
               break;
           case R.id.bplus:
               Intent intent8 = new Intent(HomeActivity.this, categories_selected.class);
               intent8.putExtra("group","B+");
               startActivity(intent8);
               break;

           case R.id.ominus:
               Intent intent9 = new Intent(HomeActivity.this, categories_selected.class);
               intent9.putExtra("group","O-");
               startActivity(intent9);
               break;
           case R.id.oplus:
               Intent intent10 = new Intent(HomeActivity.this, categories_selected.class);
               intent10.putExtra("group","O+");
               startActivity(intent10);
               break;





           case R.id.logout:
               FirebaseAuth.getInstance().signOut();
               Intent intent2 = new Intent(HomeActivity.this, LoginActivity.class);
               startActivity(intent2);

               Toast.makeText(HomeActivity.this,"SIGNED OUT",Toast.LENGTH_SHORT).show();
               break;
       }
       drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }


}