package com.example.librarybookrequisition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    GoogleSignInClient mSignInClient;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressBar;
    Button signInButton;
    Spinner spinner;

    String[] roles = {"Admin", "User"};
    String userRole = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();

        //Progress bar
        progressBar = new ProgressDialog(this);
        progressBar.setTitle("Please Wait...");
        progressBar.setMessage("We are setting Everything for you...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);


        spinner = (Spinner) findViewById(R.id.spinner);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                userRole = roles[i];
                Toast.makeText(getApplicationContext(), roles[i], Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        signInButton = (Button) findViewById(R.id.GoogleSignInBtn);


        //Google Signin Options to get gmail and performa gmail login
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1026421751332-d5m09vkogpfcvtjev0etrnno2lukjbpv.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);


        //Implementing OnClickListener to perform Login action
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Showing all Gmails
                Intent intent = mSignInClient.getSignInIntent();
                startActivityForResult(intent, 100);

            }
        });


    }

    //checking if user/admin already Logged In
    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    //Updating the UI accordingly if already Logged In
    public void updateUI(GoogleSignInAccount account) {
        if(account != null) {
            String id = account.getId().toString();
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("AllUsers")
                    .child(id)
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        DataSnapshot dataSnapshot = task.getResult();
                        String role = String.valueOf(dataSnapshot.child("role").getValue());
                        adminOruser(role);
                    } else
                        Toast.makeText(this, "User does not exist!", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(this, "Failed to read", Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignIn
                    .getSignedInAccountFromIntent(data);

            if (googleSignInAccountTask.isSuccessful()) {
                progressBar.show();
                try {
                    GoogleSignInAccount googleSignInAccount = googleSignInAccountTask.getResult(ApiException.class);

                    if (googleSignInAccount != null) {
                        AuthCredential authCredential = GoogleAuthProvider
                                .getCredential(googleSignInAccount.getIdToken(), null);

                        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    //Hashmap to store the userdetails and setting it to fireabse
                                    HashMap<String, Object> user_details = new HashMap<>();

                                    //Accessing the user details from gmail
                                    String id = googleSignInAccount.getId().toString();
                                    String name = googleSignInAccount.getDisplayName().toString();
                                    String mail = googleSignInAccount.getEmail().toString();
                                    String pic = googleSignInAccount.getPhotoUrl().toString();

                                    //storing data in hashmap
                                    user_details.put("id", id);
                                    user_details.put("name", name);
                                    user_details.put("mail", mail);
                                    user_details.put("profilepic", pic);
                                    user_details.put("role", userRole);

                                    //Adding data to firebase
                                    FirebaseDatabase.getInstance().getReference().child("AllUsers").child(id)
                                            .updateChildren(user_details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                progressBar.cancel();
                                                adminOruser(userRole);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }

                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //Role-based display of UI
    public void adminOruser(String userRole)
    {
        if (userRole.equals("Admin")) {
            //navigating to the main activity after user successfully registers
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            //Clears older activities and tasks
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            //navigating to the main activity after user successfully registers
//           Intent intent = new Intent(getApplicationContext(), UserDetailsActivity.class);
            //Clears older activities and tasks
//           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//           startActivity(intent);
        }
    }

}
