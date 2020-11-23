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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.catalin.forfoodiesbyfoodies.MainActivity;
import edu.catalin.forfoodiesbyfoodies.R;
import edu.catalin.forfoodiesbyfoodies.utils.User;

public class RegisterCriticActivity extends AppCompatActivity {

    private Button btnRegisterCritic;
    private TextView tvPassCritic;
    private EditText etFirstNameCritic, etSurnameCritic, etEmailCritic, etPasswordCritic;
    private FirebaseAuth fAuth;
    private Toolbar toolbar;
    public static RegisterCriticActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_critic);

        instance = this;
        btnRegisterCritic = (Button) findViewById(R.id.btnSaveCritic);
        etFirstNameCritic = (EditText) findViewById(R.id.etfirstNameCritic);
        etSurnameCritic = (EditText) findViewById(R.id.etSurnameCritic);
        etEmailCritic = (EditText) findViewById(R.id.etEmailCritic);
        etPasswordCritic = (EditText) findViewById(R.id.etPasswordCritic);
        tvPassCritic = (TextView) findViewById(R.id.tvPassCritic);
        tvPassCritic.setError("Password must be at least 8 characters long!");
        fAuth = FirebaseAuth.getInstance();


        toolbar = (Toolbar) findViewById(R.id.addCriticToolBar);
        toolbar.inflateMenu(R.menu.admin_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.menu_admin_update) {
                    openActivityFromMenu(RegisterCriticActivity.this, UpdateAdminActivity.class, true);
                }
                if (item.getItemId() == R.id.menu_admin_restaurants) {
                    openActivityFromMenu(RegisterCriticActivity.this, AdminRestaurantsActivity.class, true);
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
                    startActivity(new Intent(RegisterCriticActivity.this, MainActivity.class));

                }
                return false;
            }
        });

        btnRegisterCritic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String surname = etSurnameCritic.getText().toString();
                String firstName = etFirstNameCritic.getText().toString();
                String email = etEmailCritic.getText().toString();
                String password = etPasswordCritic.getText().toString();
                String role = "critic";

                if (firstName.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterCriticActivity.this, "Please complete all required fields!", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 8) {
                    Toast.makeText(RegisterCriticActivity.this, "Please check password!", Toast.LENGTH_SHORT).show();
                } else {
                    //register in firebase the basic user
                    fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                createCriticUser(firstName, surname, email, password, role);
                                etFirstNameCritic.setText("");
                                etSurnameCritic.setText("");
                                etEmailCritic.setText("");
                                etPasswordCritic.setText("");
                            } else {
                                Toast.makeText(RegisterCriticActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
    }

    private void createCriticUser(String firstName, String surname, String email, String password, String role) {

        User user = new User(firstName, surname, email, password, role);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("users");
        reference.child(String.valueOf(fAuth.getUid()))
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterCriticActivity.this, "User created!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterCriticActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
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
}