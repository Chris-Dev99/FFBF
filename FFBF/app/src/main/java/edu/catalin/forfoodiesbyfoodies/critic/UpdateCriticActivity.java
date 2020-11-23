package edu.catalin.forfoodiesbyfoodies.critic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.catalin.forfoodiesbyfoodies.R;

public class UpdateCriticActivity extends AppCompatActivity {


    private TextView tvCurrentSurnameCritic, tvCurrentFirstCritic, tvCurrentEmailCritic;
    private EditText etNewSurnameCritic, etNewFirstNameCritic, etNewEmailCritic;
    private Button btnUpdateCriticData;
    private FirebaseAuth fAuth;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private String currentSurname, currentFirstName, currentEmail, currentPassword, currentRole;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_critic);

        fAuth = FirebaseAuth.getInstance();
        firebaseUser = fAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users").child(firebaseUser.getUid());
        tvCurrentSurnameCritic = (TextView)findViewById(R.id.tvCurrentSurnameCritic);
        tvCurrentFirstCritic = (TextView)findViewById(R.id.tvCurrentFirstCritic);
        tvCurrentEmailCritic = (TextView)findViewById(R.id.tvCurrentEmailCritic);
        etNewSurnameCritic = (EditText)findViewById(R.id.etNewSurnameCritic);
        etNewFirstNameCritic = (EditText)findViewById(R.id.etNewFirstNameCritic);
        etNewEmailCritic = (EditText)findViewById(R.id.etNewEmailCritic);
        btnUpdateCriticData = (Button)findViewById(R.id.btnUpdateCriticData);

        setupUpdateData();

        btnUpdateCriticData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String newEmail = etNewEmailCritic.getText().toString().trim();
                        String newSurname = etNewSurnameCritic.getText().toString().trim();
                        String newFirstName = etNewFirstNameCritic.getText().toString().trim();

                        if (newEmail.isEmpty()) {
                            newEmail = currentEmail;
                        }
                        if (newSurname.isEmpty()) {
                            newSurname = currentSurname;
                        }
                        if (newFirstName.isEmpty()) {
                            newFirstName = currentFirstName;
                        }


                        reference.child("surname").setValue(newSurname);
                        reference.child("firstName").setValue(newFirstName);
                        reference.child("email").setValue(newEmail);
                        Toast.makeText(UpdateCriticActivity.this, "Update Succeeded!", Toast.LENGTH_SHORT).show();
                        etNewEmailCritic.setText("");
                        etNewFirstNameCritic.setText("");
                        etNewSurnameCritic.setText("");
                        startActivity(new Intent(UpdateCriticActivity.this, WelcomeCriticActivity.class));
                        finish();
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });

    }

    private void setupUpdateData() {

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                currentSurname = snapshot.child("surname").getValue(String.class);
                currentFirstName = snapshot.child("firstName").getValue(String.class);
                currentEmail = snapshot.child("email").getValue(String.class);
                currentPassword = snapshot.child("password").getValue(String.class);
                currentRole = snapshot.child("role").getValue(String.class);

                tvCurrentSurnameCritic.setText(currentSurname);
                tvCurrentFirstCritic.setText(currentFirstName);
                tvCurrentEmailCritic.setText(currentEmail);


            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}