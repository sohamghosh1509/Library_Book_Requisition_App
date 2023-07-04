package com.example.librarybookrequisition;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class AdminLibraryBooksFragment extends Fragment {

    RecyclerView recyclerView;
    AdminLibraryBooksAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_library_books, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.LibraryBooksRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //String getKey = FirebaseDatabase.getInstance().getReference().child("AllBooks").getKey();

        FirebaseRecyclerOptions<Model> options =
                new FirebaseRecyclerOptions.Builder<Model>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("AllBooks"), Model.class)
                        .build();

        adapter = new AdminLibraryBooksAdapter(options);
        recyclerView.setAdapter(adapter);

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}