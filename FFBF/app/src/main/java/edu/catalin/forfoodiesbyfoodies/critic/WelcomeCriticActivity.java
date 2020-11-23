package edu.catalin.forfoodiesbyfoodies.critic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import edu.catalin.forfoodiesbyfoodies.MainActivity;
import edu.catalin.forfoodiesbyfoodies.R;

public class WelcomeCriticActivity extends AppCompatActivity {
    private Button btnGoToRestaurant, btnGoToStreetFood, btnUpdateCritic, btnLogoutCritic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_critic);

        btnGoToRestaurant = (Button) findViewById(R.id.btnGoToRestaurant);
        btnGoToStreetFood = (Button) findViewById(R.id.btnGoToStreetFood);
        btnUpdateCritic = (Button) findViewById(R.id.btnUpdateCritic);
        btnLogoutCritic = (Button) findViewById(R.id.btnLogoutCritic);

        btnGoToRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeCriticActivity.this, CriticRestaurantsActivity.class));
            }
        });

        btnGoToStreetFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeCriticActivity.this, CriticStreetFoodActivity.class));
            }
        });


        btnUpdateCritic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeCriticActivity.this, UpdateCriticActivity.class));
            }
        });

        btnLogoutCritic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(WelcomeCriticActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}