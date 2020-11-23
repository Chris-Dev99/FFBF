package edu.catalin.forfoodiesbyfoodies.basic_user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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

import edu.catalin.forfoodiesbyfoodies.MainActivity;
import edu.catalin.forfoodiesbyfoodies.R;

public class UpdateDataActivity extends AppCompatActivity {

    public static UpdateDataActivity instance = null;
    private TextView tvCurrentSurname, tvCurrentFirst, tvCurrentEmail;
    private EditText etNewSurname, etNewFirstName, etNewEmail;
    private Button btnUpdateData;
    private FirebaseAuth fAuth;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private String currentSurname, currentFirstName, currentEmail, currentPassword, currentRole;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_data);

        instance = this;
        tvCurrentSurname = (TextView) findViewById(R.id.tvCurrentSurname);
        tvCurrentFirst = (TextView) findViewById(R.id.tvCurrentFirst);
        tvCurrentEmail = (TextView) findViewById(R.id.tvCurrentEmail);
        etNewSurname = (EditText) findViewById(R.id.etNewSurname);
        etNewFirstName = (EditText) findViewById(R.id.etNewFirstName);
        etNewEmail = (EditText) findViewById(R.id.etNewEmail);
        btnUpdateData = (Button) findViewById(R.id.btnUpdateData);

        fAuth = FirebaseAuth.getInstance();
        firebaseUser = fAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users").child(firebaseUser.getUid());

        toolbar = (Toolbar) findViewById(R.id.updateToolBar);
        toolbar.inflateMenu(R.menu.menu_app);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_restaurants) {
                    openActivityFromMenu(UpdateDataActivity.this, RestaurantsActivity.class, true);

                }
                if (item.getItemId() == R.id.menu_street) {
                    openActivityFromMenu(UpdateDataActivity.this, StreetFoodActivity.class, true);


                }
                if (item.getItemId() == R.id.menu_logout) {
                    if (RestaurantsActivity.instance != null) {
                        RestaurantsActivity.instance.finish();
                    }
                    if (UpdateDataActivity.instance != null) {
                        UpdateDataActivity.instance.finish();
                    }
                    if (StreetFoodActivity.instance != null) {
                        StreetFoodActivity.instance.finish();
                    }
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(UpdateDataActivity.this, MainActivity.class));

                }
                return false;
            }
        });

        setupUpdateData();

        btnUpdateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                        String newEmail = etNewEmail.getText().toString().trim();
                        String newSurname = etNewSurname.getText().toString().trim();
                        String newFirstName = etNewFirstName.getText().toString().trim();
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
                        Toast.makeText(UpdateDataActivity.this, "Update Succeeded!", Toast.LENGTH_SHORT).show();
                        etNewEmail.setText("");
                        etNewFirstName.setText("");
                        etNewSurname.setText("");

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void openActivityFromMenu(Context packageContext, Class<?> cls, boolean flagState) {
        Intent intent = new Intent(packageContext, cls);
        if (flagState) {
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        }

        startActivity(intent);

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

                tvCurrentSurname.setText(currentSurname);
                tvCurrentFirst.setText(currentFirstName);
                tvCurrentEmail.setText(currentEmail);


            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}