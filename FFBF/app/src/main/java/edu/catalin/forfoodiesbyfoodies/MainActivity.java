package edu.catalin.forfoodiesbyfoodies;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.catalin.forfoodiesbyfoodies.admin.AdminRestaurantsActivity;
import edu.catalin.forfoodiesbyfoodies.critic.WelcomeCriticActivity;
import edu.catalin.forfoodiesbyfoodies.register.RegisterActivity;
import edu.catalin.forfoodiesbyfoodies.basic_user.RestaurantsActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText etEmail, etPassword;
    private TextView tvRegister;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = (Button) findViewById(R.id.btnLogin);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        tvRegister = (TextView) findViewById(R.id.tvRegister);
        fAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please complete all required fields!", Toast.LENGTH_SHORT).show();
                } else {
                    //login
                    fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                checkRole();
                            }else{
                                Toast.makeText(MainActivity.this, "Error! " + task.getException().getMessage() ,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();

            }
        });


    }

    private void checkRole(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("users").child(fAuth.getCurrentUser().getUid()).child("role");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String role = snapshot.getValue().toString();
                if(role.equals("basic")){
                    Intent intent = new Intent(MainActivity.this, RestaurantsActivity.class);
                    startActivity(intent);
                    finish();

                }else if(role.equals("admin")){
                    Intent intent = new Intent(MainActivity.this, AdminRestaurantsActivity.class);
                    startActivity(intent);
                    finish();
                }else if(role.equals("critic")){
                    Intent intent = new Intent(MainActivity.this, WelcomeCriticActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}