package com.example.librarybookrequisition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RequestNewTitleActivity extends AppCompatActivity {
    EditText bookNameEditTxt;
    Spinner spinner;
    DatabaseReference databaseReference;
    Button requestTitleBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_new_title);

        bookNameEditTxt = (EditText) findViewById(R.id.NewBookNameEditTxt);
        requestTitleBtn = (Button) findViewById(R.id.RequestTitleBtn);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("AdminNotifications");
        spinner = (Spinner) findViewById(R.id.TitleSpinner);

        //spinner.setOnItemSelectedListener(this);
        List<String> categories = new ArrayList<String>();

        categories.add("Computer Science");
        categories.add("Electronics");
        categories.add("Mathematics");
        categories.add("Physics");
        categories.add("Electrical");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        requestTitleBtn.setOnClickListener(view -> {
            String bookName= bookNameEditTxt.getText().toString();
            String category = spinner.getSelectedItem().toString();
            String studentName = getIntent().getStringExtra("studentName");
            String department = getIntent().getStringExtra("department");
            String userId = GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getId();
            String notification = studentName + " of " + department + " department has requested for new title " + bookName + " of category " + category+".";
            String push = databaseReference.push().getKey().toString();

            if (bookName.isEmpty()) {
                Toast.makeText(this, "Please Enter Details", Toast.LENGTH_SHORT).show();
            }
            else
            {
                HashMap<String,String> requestDetails = new HashMap<>();
                requestDetails.put("bookName",bookName);
                requestDetails.put("category",category);
                requestDetails.put("name",studentName);
                requestDetails.put("dept",department);
                requestDetails.put("userId",userId);
                requestDetails.put("notification",notification);

                databaseReference.child(push).setValue(requestDetails)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getApplicationContext(),"Request Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                }

        });
    }
}