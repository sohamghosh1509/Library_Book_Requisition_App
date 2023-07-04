package com.example.librarybookrequisition;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class UserHomeAdapter extends FirebaseRecyclerAdapter<Model, UserHomeAdapter.Viewholder> {

    public UserHomeAdapter(@NonNull FirebaseRecyclerOptions<Model> options) {

        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull UserHomeAdapter.Viewholder holder, int position, @NonNull Model model) {
        holder.bookName.setText("Book Name: " + model.getBookName());
        holder.booksCount.setText("Available Books: " + model.getBooksCount());
        holder.bookLocation.setText("Book Location: " + model.getBookLocation());
        holder.bookCategory.setText("Book Category: " + model.getCategory());

        String pushKey = model.getPushKey();

        holder.requestBtn.setOnClickListener(view -> {
//            Intent intent=new Intent(view.getContext(), RequestBookActivity.class);
//            intent.putExtra("pushKey", pushKey);
//            view.getContext().startActivity(intent);
            String bookLocation = model.getBookLocation();
            String bookName = model.getBookName();
            String booksCount = model.getBooksCount();
            String bookCategory = model.getCategory();
//                String imageUrl = model.getImageUrl();

            String userId = GoogleSignIn.getLastSignedInAccount(view.getContext()).getId();
            FirebaseDatabase.getInstance().getReference().child("AllUsers").child(userId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                            //Getting user data using Model Class
                            Model model1 = snapshot.getValue(Model.class);
                            String name = model1.getName();
                            String dept = model1.getDept();
//                                String city = model1.getCity();
                                String phoneNumber = model1.getPhone();
//                                String pincode = model1.getPincode();


                            HashMap userDetailsforAdmin=new HashMap();
                            userDetailsforAdmin.put("name",name);
//                                userDetails.put("city",city);
                            userDetailsforAdmin.put("phone",phoneNumber);
                            userDetailsforAdmin.put("dept", dept);
//                                userDetails.put("pincode",pincode);
                            userDetailsforAdmin.put("bookLocation",bookLocation);
                            userDetailsforAdmin.put("bookName",bookName);
                            userDetailsforAdmin.put("booksCount",booksCount);
                            userDetailsforAdmin.put("category", bookCategory);
                            userDetailsforAdmin.put("userId",userId);
                            userDetailsforAdmin.put("pushKey", model.getPushKey());

                            String push=FirebaseDatabase.getInstance().getReference().child("BooksRequested").push().getKey();
                            FirebaseDatabase.getInstance().getReference().child("BooksRequested").child(push)
                                    .updateChildren(userDetailsforAdmin)
                                    .addOnSuccessListener(new OnSuccessListener() {
                                        @Override
                                        public void onSuccess(Object o) {
                                            HashMap StudentRequestDetails=new HashMap();
                                            //StudentRequestDetails.put("bookLocation",bookLocation);
                                            StudentRequestDetails.put("bookName",bookName);
                                            //StudentRequestDetails.put("booksCount",booksCount);
                                            StudentRequestDetails.put("category", bookCategory);
                                            StudentRequestDetails.put("userId",userId);
                                            StudentRequestDetails.put("pushKey", model.getPushKey());

                                            FirebaseDatabase.getInstance().getReference().child("StudentsRequestedBooks").child(dept)
                                                    .child(push)
                                                    .updateChildren(StudentRequestDetails)
                                                    .addOnSuccessListener(new OnSuccessListener() {
                                                        @Override
                                                        public void onSuccess(Object o) {

                                                            Toast.makeText(view.getContext(), "Book requested successfully!",Toast.LENGTH_SHORT).show();

                                                        }
                                                    });

                                        }
                                    });


                            //Toast message
                            Toast.makeText(view.getContext(), "Book requested!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
        });
    }

    @NonNull
    @Override
    public UserHomeAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_book_layout, parent, false);
        return new UserHomeAdapter.Viewholder(view);
    }

    class Viewholder extends RecyclerView.ViewHolder{

        TextView bookName, booksCount, bookLocation, bookCategory;
        Button requestBtn;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            bookName = (TextView) itemView.findViewById(R.id.BookNameTxt);
            booksCount = (TextView) itemView.findViewById(R.id.BooksCountTxt);
            bookLocation = (TextView) itemView.findViewById(R.id.BooksLocationTxt);
            bookCategory = (TextView) itemView.findViewById(R.id.BooksCategoryTxt);
            requestBtn = (Button) itemView.findViewById(R.id.RequestBookBtn);
        }
    }
}