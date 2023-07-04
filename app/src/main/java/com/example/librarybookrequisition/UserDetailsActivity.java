package com.example.librarybookrequisition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class UserDetailsActivity extends AppCompatActivity {
    EditText studentNameTxt,studentPhNoTxt,studentDeptTxt;
    Button addDataBtn;
    String studentName,phoneNumber,department,id;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        studentNameTxt = (EditText) findViewById(R.id.StudentNameTxt);
        studentPhNoTxt = (EditText) findViewById(R.id.PhoneNumberTxt);
        studentDeptTxt = (EditText) findViewById(R.id.DepartmentTxt);
        addDataBtn = (Button) findViewById(R.id.StudentDetailsBtn);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("AllUsers");
        id = GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getId();

        databaseReference.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Model model = snapshot.getValue(Model.class);
                studentNameTxt.setText(model.getName());
                studentPhNoTxt.setText(model.getPhone());
                studentDeptTxt.setText(model.getDept());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        addDataBtn.setOnClickListener(view -> {
            studentName = studentNameTxt.getText().toString();
            phoneNumber = studentPhNoTxt.getText().toString();
            department = studentDeptTxt.getText().toString();

            if (phoneNumber.isEmpty() || studentName.isEmpty() || department.isEmpty())
                Toast.makeText(getApplicationContext(), "Please,Fill all the Details", Toast.LENGTH_SHORT).show();
            else
                addStudentDetails(studentName,phoneNumber,department);
        });
    }

    private void addStudentDetails(String studentName, String phoneNumber, String department) {
        //String id = GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getId();
        HashMap<String,Object> studentDetails = new HashMap<>();
        studentDetails.put("name",studentName);
        studentDetails.put("phone",phoneNumber);
        studentDetails.put("dept",department);
        databaseReference.child(id)
                .updateChildren(studentDetails).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull @NotNull Task task) {
                if (task.isSuccessful()) {


                    //showing the toast message to user
                    Toast.makeText(getApplicationContext(), "Details added Successfully", Toast.LENGTH_SHORT).show();

                    //Changing current intent after adding the details to firebase
                    Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }
}