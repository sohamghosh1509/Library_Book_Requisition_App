package com.example.librarybookrequisition;

import android.provider.ContactsContract;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class AdminDashBoardAdapter extends FirebaseRecyclerAdapter<Model, AdminDashBoardAdapter.Viewholder> {

    public AdminDashBoardAdapter(@NonNull FirebaseRecyclerOptions<Model> options) {

        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull Viewholder holder, int position, @NonNull Model model) {

        //Setting data to android materials
        holder.bookName.setText("Book Name: " + model.getBookName());
        holder.booksCount.setText("Available Books: " + model.getBooksCount());
        holder.bookLocation.setText("Book Location: " + model.getBookLocation());
        holder.bookCategory.setText("Book Category: "+ model.getCategory());


        holder.studentNameTxt.setText("Name: "+ model.getName());
        holder.userPhoneNumberTxt.setText("Contact No: "+ model.getPhone());
        holder.dept.setText("Department: "+ model.getDept());

//        Picasso.get().load(model.getImageUrl()).into(holder.imageView);

        //Implementing the OnClick Listener to delete the data from the database
        holder.denyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Getting user id from the gmail sing in
                String userId=model.getUserId();
                DatabaseReference booksRequestedReference = FirebaseDatabase.getInstance().getReference().child("BooksRequested");
                booksRequestedReference.orderByChild("bookName").equalTo(model.getBookName()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {

                            //getting the parent node of the data
                            String key = ds.getKey();

                            //removing the data from the database
                            booksRequestedReference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if (task.isSuccessful()) {
//                                        FirebaseDatabase.getInstance().getReference().child("StudentsRequestedBooks").child(userId)
//                                                .child(key).removeValue()
//                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
//                                                        if (task.isSuccessful()) {

                                                            //Generating Unique Key
                                                            String pushKey=FirebaseDatabase.getInstance().getReference().child("UserNotifications").push().getKey();

                                                            //Adding Notification To Database
                                                            FirebaseDatabase.getInstance().getReference().child("UserNotifications").child(userId).child(pushKey)
                                                                    .child("notification").setValue("Your Request for Book " + model.getBookName() + " has been denied By Librarian")
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {

                                                                            //Showing the Toast message to the user
                                                                            Toast.makeText(view.getContext(), "Book Request denied successfully", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });



//                                                        }
//                                                    }
//                                                });
                                    }
                                }
                            });


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
            }
        });

        //implementing onClickListener
        holder.grantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(model.getBooksCount())> 0) {
                    DatabaseReference booksRequestedReference = FirebaseDatabase.getInstance().getReference().child("BooksRequested");
                    booksRequestedReference.orderByChild("bookName").equalTo(model.getBookName()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {

                                //getting the parent node of the data
                                String key = ds.getKey();

                                //removing the data from the database
                                booksRequestedReference.child(key).removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });

                    //Generating Unique key
                    String pushKey = FirebaseDatabase.getInstance().getReference().child("UserNotifications").push().getKey();
                    //Adding Notification to database
                    FirebaseDatabase.getInstance().getReference().child("UserNotifications").child(model.getUserId())
                            .child(pushKey)
                            .child("notification").setValue("Your Request for Book " + model.getBookName() + " has been granted By Librarian")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    HashMap<String,String> bookDetails = new HashMap<>();
                                    bookDetails.put("bookName", model.getBookName());
                                    bookDetails.put("booksCount", String.valueOf(Integer.parseInt(model.getBooksCount()) - 1));
                                    bookDetails.put("bookLocation", model.getBookLocation());
                                    bookDetails.put("category", model.getCategory());
                                    bookDetails.put("pushKey", model.getPushKey());

                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("AllBooks");
                                    databaseReference.child(model.getPushKey()).setValue(bookDetails);

                                    //Showing toast Message

                                    Toast.makeText(view.getContext(), "Book Request granted!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

    }


    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //the data objects are inflated into the xml file single_data_item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_admin_dashboard, parent, false);
        return new Viewholder(view);
    }

    //we need view holder to hold each objet form recyclerview and to show it in recyclerview
    class Viewholder extends RecyclerView.ViewHolder {


//        ImageView imageView;
        TextView bookName, booksCount, bookLocation, bookCategory;
        Button grantBtn, denyBtn;

        TextView studentNameTxt, userAddressTxt, userPhoneNumberTxt, dept;


        public Viewholder(@NonNull View itemView) {
            super(itemView);


            //Assigning Address of the android materials
//            imageView = (ImageView) itemView.findViewById(R.id.BookImage);
            bookName = (TextView) itemView.findViewById(R.id.BookNameTxt);
            booksCount = (TextView) itemView.findViewById(R.id.BooksCountTxt);
            bookLocation = (TextView) itemView.findViewById(R.id.BooksLocationTxt);
            bookCategory = (TextView) itemView.findViewById(R.id.BooksCategoryTxt);


            studentNameTxt = (TextView) itemView.findViewById(R.id.StudentNameTxt);
            dept = (TextView) itemView.findViewById(R.id.DepartmentTxt);
            userPhoneNumberTxt = (TextView) itemView.findViewById(R.id.PhoneNumberTxt);
//
            grantBtn=(Button)itemView.findViewById(R.id.GrantBtn);
            denyBtn=(Button)itemView.findViewById(R.id.DenyBtn);

        }
    }

}


