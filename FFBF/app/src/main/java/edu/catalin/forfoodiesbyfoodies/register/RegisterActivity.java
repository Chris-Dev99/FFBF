package edu.catalin.forfoodiesbyfoodies.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class RegisterActivity extends AppCompatActivity {

    private Button btnRegister;
    private TextView tvLogin, tvPass;
    private EditText etFirstName, etSurname, etEmail, etPassword;
    private FirebaseAuth fAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegister = (Button) findViewById(R.id.btnSave);
        tvLogin = (TextView) findViewById(R.id.tvLogin);
        etFirstName = (EditText) findViewById(R.id.etfirstName);
        etSurname = (EditText) findViewById(R.id.etSurname);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        tvPass = (TextView) findViewById(R.id.tvPass);
        tvPass.setError("Password must be at least 8 characters long!");
        fAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String surname = etSurname.getText().toString();
                String firstName = etFirstName.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String role = "basic";

                if (firstName.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please complete all required fields!", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 8) {
                    Toast.makeText(RegisterActivity.this, "Please check password!", Toast.LENGTH_SHORT).show();
                } else {
                    //register in firebase the basic user
                    fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                createUser(firstName, surname, email, password, role);
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                finish();

                            } else {
                                Toast.makeText(RegisterActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            }
        });

    }

    private void createUser(String firstName, String surname, String email, String password, String role) {
        User user = new User(firstName, surname, email, password, role);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("users");
        reference.child(String.valueOf(fAuth.getUid()))
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "User created!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}