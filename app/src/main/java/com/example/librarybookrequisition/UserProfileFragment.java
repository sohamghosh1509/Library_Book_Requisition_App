package com.example.librarybookrequisition;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileFragment extends Fragment {

    CircleImageView imageView;
    TextView userName;
    Button signOutBtn,updateDetailsBtn,requestNewTitleBtn;
    TextView phoneNumberTxt, departmentTxt;
    DatabaseReference databaseReference;
    String userId, studentName, dept;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        imageView = (CircleImageView) view.findViewById(R.id.ProfilePic);
        userName = (TextView) view.findViewById(R.id.UserNameTxt);
        signOutBtn = (Button) view.findViewById(R.id.SignOutBtn);
        phoneNumberTxt = (TextView) view.findViewById(R.id.PhoneNumberText);
        departmentTxt = (TextView) view.findViewById(R.id.DepartmentText);
        updateDetailsBtn = (Button) view.findViewById(R.id.UpdateProfileBtn);
        requestNewTitleBtn = (Button) view.findViewById(R.id.requestNewTitleBtn);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("AllUsers");
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        if(acct != null) {
            userId = acct.getId().toString();
            Picasso.get().load(acct.getPhotoUrl()).into(imageView);
            databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                    Model model = snapshot.getValue(Model.class);
                    dept = model.getDept();
                    studentName = model.getName();
                    userName.setText(model.getName());
                    phoneNumberTxt.setText("Phone Number: " + model.getPhone());
                    departmentTxt.setText("Department: " + model.getDept());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleSignInOptions gso = new GoogleSignInOptions.
                        Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                        build();

                //GoogleSignInClient to access the current user
                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //User Signout
                            FirebaseAuth.getInstance().signOut();

                            //Redirecting to starting Activity
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        }

                    }
                });

            }
        });
        updateDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                String studentName = userName.getText().toString();
//                String phoneNumber = phoneNumberTxt.getText().toString();
//                String department = departmentTxt.getText().toString();
//
//                //Checking for empty fields
//                if (phoneNumber.isEmpty() || department.isEmpty()) {
//                    Toast.makeText(getContext(), "Please,Fill Details", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getContext(), UserDetailsActivity.class);
//                    intent.putExtra("studentName",studentName);
//                    intent.putExtra("phoneNumber",phoneNumber);
//                    intent.putExtra("department",department);
                    getActivity().startActivity(intent);
            }
        });

        requestNewTitleBtn.setOnClickListener(view1 -> {
            Intent in = new Intent(getContext(),RequestNewTitleActivity.class);
            in.putExtra("studentName", studentName);
            in.putExtra("department", dept);
            startActivity(in);
        });

        return view;
    }
}