package com.example.blooddonationapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.blooddonationapp.Model.User;
import com.example.blooddonationapp.Model.chat;
import com.example.blooddonationapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {


 public static final int MSG_TYPE_LEFT=0;
 public static final int MSG_TYPE_RIGHT=1;

private Context context;
private List<chat> chatList;


FirebaseUser fuser;
private DatabaseReference userRef;




public MessageAdapter(Context context, List<chat> chatList, String prflimgurl) {
        this.context = context;
        this.chatList = chatList;
        }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


    if(viewType==MSG_TYPE_RIGHT) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
        return new MessageAdapter.ViewHolder(view);
    }else {

        View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
        return new MessageAdapter.ViewHolder(view);

    }
    }


    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        final chat chat= chatList.get(position);

        holder.show_message.setText(chat.getMessage());








    }









    @Override
public int getItemCount() {
        return chatList.size();
        }

public class ViewHolder extends RecyclerView.ViewHolder{


    public CircleImageView userprofileimage;
    public TextView show_message;



    public ViewHolder(@NonNull View itemView) {
        super(itemView);


        show_message=itemView.findViewById(R.id.show_message);







    }
}


    @Override
    public int getItemViewType(int position) {
         fuser= FirebaseAuth.getInstance().getCurrentUser();
         if(chatList.get(position).getSender().equals(fuser.getUid())){
             return MSG_TYPE_RIGHT;
         }else{
             return MSG_TYPE_LEFT;
         }
    }
}


