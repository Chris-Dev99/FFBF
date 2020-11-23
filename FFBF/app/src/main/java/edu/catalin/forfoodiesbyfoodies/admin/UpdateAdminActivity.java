package edu.catalin.forfoodiesbyfoodies.admin;

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

public class UpdateAdminActivity extends AppCompatActivity {
    public static UpdateAdminActivity instance = null;
    private FirebaseAuth fAuth;
    private Toolbar toolbar;
    private TextView tvCurrentSurnameAdmin, tvCurrentFirstAdmin, tvCurrentEmailAdmin;
    private EditText  etNewSurnameAdmin, etNewFirstNameAdmin, etNewEmailAdmin;
    private Button btnUpdateAdminData;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private String currentSurname, currentFirstName, currentEmail, currentPassword, currentRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_admin);
        instance = this;

        tvCurrentSurnameAdmin = (TextView) findViewById(R.id.tvCurrentSurnameAdmin);
        tvCurrentFirstAdmin = (TextView) findViewById(R.id.tvCurrentFirstAdmin);
        tvCurrentEmailAdmin = (TextView) findViewById(R.id.tvCurrentEmailAdmin);
        etNewSurnameAdmin = (EditText) findViewById(R.id.etNewSurnameAdmin);
        etNewFirstNameAdmin = (EditText) findViewById(R.id.etNewFirstNameAdmin);
        etNewEmailAdmin = (EditText) findViewById(R.id.etNewEmailAdmin);
        btnUpdateAdminData = (Button) findViewById(R.id.btnUpdateAdminData);


        fAuth = FirebaseAuth.getInstance();
        firebaseUser = fAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users").child(firebaseUser.getUid());

        toolbar = (Toolbar) findViewById(R.id.updateAdminToolBar);
        toolbar.inflateMenu(R.menu.admin_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.menu_critic) {
                    openActivityFromMenu(UpdateAdminActivity.this, RegisterCriticActivity.class, true);
                }
                if (item.getItemId() == R.id.menu_admin_restaurants) {
                    openActivityFromMenu(UpdateAdminActivity.this, AdminRestaurantsActivity.class, true);
                }
                if (item.getItemId() == R.id.menu_admin_logout) {

                    if (AdminRestaurantsActivity.instance != null) {
                        AdminRestaurantsActivity.instance.finish();
                    }
                    if (UpdateAdminActivity.instance != null) {
                        UpdateAdminActivity.instance.finish();
                    }
                    if (RegisterCriticActivity.instance != null) {
                        RegisterCriticActivity.instance.finish();
                    }
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(UpdateAdminActivity.this, MainActivity.class));

                }
                return false;
            }
        });

        setupUpdateData();
        btnUpdateAdminData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String newEmail = etNewEmailAdmin.getText().toString().trim();
                        String newSurname = etNewSurnameAdmin.getText().toString().trim();
                        String newFirstName = etNewFirstNameAdmin.getText().toString().trim();


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
                        Toast.makeText(UpdateAdminActivity.this, "Update Succeeded!", Toast.LENGTH_SHORT).show();
                        etNewEmailAdmin.setText("");
                        etNewFirstNameAdmin.setText("");
                        etNewSurnameAdmin.setText("");

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

                tvCurrentSurnameAdmin.setText(currentSurname);
                tvCurrentFirstAdmin.setText(currentFirstName);
                tvCurrentEmailAdmin.setText(currentEmail);


            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}