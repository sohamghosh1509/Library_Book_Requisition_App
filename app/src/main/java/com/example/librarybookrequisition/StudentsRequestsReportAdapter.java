package com.example.librarybookrequisition;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class StudentsRequestsReportAdapter extends FirebaseRecyclerAdapter<Model, StudentsRequestsReportAdapter.Viewholder> {

    public StudentsRequestsReportAdapter(@NonNull FirebaseRecyclerOptions<Model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull StudentsRequestsReportAdapter.Viewholder holder, int position, @NonNull Model model) {
        FirebaseDatabase.getInstance().getReference().child("AllUsers").child(model.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Model model1 = snapshot.getValue(Model.class);
                holder.studentName.setText("Student Name: "+ model1.getName());
                holder.phoneNo.setText("Contact No: "+model1.getPhone());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.bookName.setText("Book Name: " + model.getBookName());
//        holder.booksCount.setText("Available Books: " + model.getBooksCount());
//        holder.bookLocation.setText("Book Location: " + model.getBookLocation());
        holder.bookCategory.setText("Book Category: " + model.getCategory());
    }

    @NonNull
    @Override
    public StudentsRequestsReportAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_report_layout, parent, false);
        return new StudentsRequestsReportAdapter.Viewholder(view);
    }

    class Viewholder extends RecyclerView.ViewHolder{

        TextView bookName, studentName, bookCategory, phoneNo;
//        Button edit;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            studentName = (TextView) itemView.findViewById(R.id.StudentNameTxt);
            phoneNo = (TextView) itemView.findViewById(R.id.PhoneNumberTxt);
            bookName = (TextView) itemView.findViewById(R.id.BookNameTxt);
//            booksCount = (TextView) itemView.findViewById(R.id.BooksCountTxt);
//            bookLocation = (TextView) itemView.findViewById(R.id.BooksLocationTxt);
            bookCategory = (TextView) itemView.findViewById(R.id.BooksCategoryTxt);
//            edit = (Button) itemView.findViewById(R.id.EditBtn);
        }
    }
}
