package com.example.blooddonationapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.blooddonationapp.MessageActivity;
import com.example.blooddonationapp.Model.User;
import com.example.blooddonationapp.R;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<User> userList;
    private DatabaseReference userRef;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.user_displayed_layout, parent, false);
        return new ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final User user= userList.get(position);

        holder.username.setText(user.getName());
        holder.bloodgroup.setText(user.getBloodgroup());
       // holder.email.setText(user.getEmail());
        holder.number.setText(user.getNumber());


        //holder.setIsRecyclable(false);

            Glide.with(context).load(user.getProfileimage()).into(holder.userprofileimage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, MessageActivity.class);
                intent.putExtra("id",user.getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        public CircleImageView userprofileimage;
        public TextView username, email, number, bloodgroup;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userprofileimage=itemView.findViewById(R.id.userprofileimage);
            username=itemView.findViewById(R.id.select_name);

            number=itemView.findViewById(R.id.select_number);
            bloodgroup=itemView.findViewById(R.id.select_bloodgroup);





        }
    }
}
