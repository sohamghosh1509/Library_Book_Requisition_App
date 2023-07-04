package com.example.librarybookrequisition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddBooksFragment extends Fragment implements AdapterView.OnItemSelectedListener {


    Button submitBtn;
    EditText bookNameEditText,booksCountEditText,booksLocationEditText;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    Spinner spinner;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_books, container, false);

        bookNameEditText= (EditText) view.findViewById(R.id.BookNameEditTxt);
        booksCountEditText= (EditText) view.findViewById(R.id.TotalBooksEditTxt);
        booksLocationEditText = (EditText) view.findViewById(R.id.BookLocationEditTxt);
        spinner = (Spinner)view.findViewById(R.id.Spinner);

        submitBtn = (Button) view.findViewById(R.id.AddBookBtn);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("AllBooks");
        storageReference = FirebaseStorage.getInstance().getReference();

        spinner.setOnItemSelectedListener(this);
        List<String> categories = new ArrayList<String>();

        categories.add("Computer Science");
        categories.add("Electronics");
        categories.add("Mathematics");
        categories.add("Physics");
        categories.add("Electrical");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        submitBtn.setOnClickListener(view1 -> {
            String bookName= bookNameEditText.getText().toString();
            String booksCount=booksCountEditText.getText().toString();
            String bookLocation=booksLocationEditText.getText().toString();
            String  category = spinner.getSelectedItem().toString();
            if (bookName.isEmpty() || booksCount.isEmpty() || bookLocation.isEmpty()) {
                Toast.makeText(getContext(), "Please Enter Details", Toast.LENGTH_SHORT).show();
            } else {
                //calling the method to add data to firebase
                Toast.makeText(getContext(),"Please,Wait uploading data...",Toast.LENGTH_SHORT).show();
                addBookDetails(bookName,booksCount,bookLocation,category);
            }
        });

        return view;
    }

    private void addBookDetails(String bookName, String booksCount, String bookLocation,String  category)
    {
        String push = databaseReference.push().getKey().toString();

        HashMap<String,String> bookDetails = new HashMap<>();
        bookDetails.put("bookName", bookName);
        bookDetails.put("booksCount", booksCount);
        bookDetails.put("bookLocation", bookLocation);
        bookDetails.put("category",category);
        bookDetails.put("pushKey",push);

        databaseReference.child(push).setValue(bookDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        //Showing the toast to user for confirmation
                        Toast.makeText(getContext(), "Data Uploaded Successfully", Toast.LENGTH_SHORT).show();

                        //Calling the same intent to reset all the current data
                        Intent intent = new Intent(getContext(), AdminActivity.class);
                        getActivity().startActivity(intent);
                        getActivity().finish();

//                        FirebaseDatabase.getInstance().getReference().child("TotalBooksCategories")
//                                .child(push).child("category").setValue(category)
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void unused) {
//
//                                        //Showing the toast to user for confirmation
//                                        Toast.makeText(getContext(), "Data Uploaded Successfully", Toast.LENGTH_SHORT).show();
//
//                                        //Calling the same intent to reset all the current data
//                                        Intent intent = new Intent(getContext(), AdminActivity.class);
//                                        getActivity().startActivity(intent);
//                                        getActivity().finish();
//
//                                    }
//                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {

                //Showing the toast message to the user to reUpload the data
                Toast.makeText(getContext(), "Failed To Upload Please,Try Again", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        String item = adapterView.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(adapterView.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}