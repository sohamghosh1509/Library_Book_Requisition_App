package com.example.librarybookrequisition;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminNewTitleRequestsAdapter extends FirebaseRecyclerAdapter<Model, AdminNewTitleRequestsAdapter.Viewholder> {

    public AdminNewTitleRequestsAdapter(@NonNull FirebaseRecyclerOptions<Model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull Viewholder holder, int position, @NonNull Model model) {
        holder.notificationTxt.setText(model.getNotification());

        holder.grantBtn.setOnClickListener(view -> {

            String pushKey = FirebaseDatabase.getInstance().getReference().child("UserNotifications").push().getKey();
            FirebaseDatabase.getInstance().getReference().child("UserNotifications").child(model.getUserId()).child(pushKey)
                    .child("notification").setValue("Your Request for new title " + model.getBookName() + " has been granted By Librarian and the new title shall be available in the Library for issue.")
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(view.getContext(), "Request granted!", Toast.LENGTH_SHORT).show();
                        }
                    });

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("AdminNotifications");
            databaseReference.orderByChild("bookName").equalTo(model.getBookName()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String key = ds.getKey();
                        databaseReference.child(key).removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });
        holder.denyBtn.setOnClickListener(view -> {
            String pushKey = FirebaseDatabase.getInstance().getReference().child("UserNotifications").push().getKey();
            FirebaseDatabase.getInstance().getReference().child("UserNotifications").child(model.getUserId()).child(pushKey)
                    .child("notification").setValue("Your Request for new title " + model.getBookName() + " has been denied By Librarian")
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(view.getContext(), "Request denied!", Toast.LENGTH_SHORT).show();
                        }
                    });

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("AdminNotifications");
            databaseReference.orderByChild("bookName").equalTo(model.getBookName()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String key = ds.getKey();
                        databaseReference.child(key).removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_admin_new_title_requests_layout, parent, false);
        return new Viewholder(view);

    }
    class Viewholder extends RecyclerView.ViewHolder
    {
        TextView notificationTxt;
        Button grantBtn,denyBtn;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            notificationTxt = (TextView) itemView.findViewById(R.id.NotificationTxt);
            grantBtn = (Button) itemView.findViewById(R.id.GrantBtn);
            denyBtn = (Button) itemView.findViewById(R.id.DenyBtn);
        }
    }
}
