package com.example.librarybookrequisition;

import android.content.Intent;
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
import com.google.firebase.database.FirebaseDatabase;

public class AdminLibraryBooksAdapter extends FirebaseRecyclerAdapter<Model, AdminLibraryBooksAdapter.Viewholder> {

    public AdminLibraryBooksAdapter(@NonNull FirebaseRecyclerOptions<Model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdminLibraryBooksAdapter.Viewholder holder, int position, @NonNull Model model) {
        holder.bookName.setText("Book Name: " + model.getBookName());
        holder.booksCount.setText("Available Books: " + model.getBooksCount());
        holder.bookLocation.setText("Book Location: " + model.getBookLocation());
        holder.bookCategory.setText("Book Category: " + model.getCategory());

        holder.edit.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(),EditBookDetailsActivity.class);
            intent.putExtra("pushKey",model.getPushKey());
            view.getContext().startActivity(intent);
        });

        holder.delete.setOnClickListener(view -> {
            FirebaseDatabase.getInstance().getReference().child("AllBooks").child(model.getPushKey()).removeValue();
            Toast.makeText(view.getContext(), "Book Deleted Successfully", Toast.LENGTH_SHORT).show();
        });
    }

    @NonNull
    @Override
    public AdminLibraryBooksAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_book_layout_admin, parent, false);
        return new AdminLibraryBooksAdapter.Viewholder(view);
    }

    class Viewholder extends RecyclerView.ViewHolder{

        TextView bookName, booksCount, bookLocation, bookCategory;
        Button edit,delete;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            bookName = (TextView) itemView.findViewById(R.id.BookNameTxt);
            booksCount = (TextView) itemView.findViewById(R.id.BooksCountTxt);
            bookLocation = (TextView) itemView.findViewById(R.id.BooksLocationTxt);
            bookCategory = (TextView) itemView.findViewById(R.id.BooksCategoryTxt);
            edit = (Button) itemView.findViewById(R.id.EditBtn);
            delete = (Button) itemView.findViewById(R.id.DeleteBtn);
        }
    }
}