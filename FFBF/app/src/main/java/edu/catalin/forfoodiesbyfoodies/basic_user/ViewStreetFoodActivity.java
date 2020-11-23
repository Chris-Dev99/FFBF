package edu.catalin.forfoodiesbyfoodies.basic_user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import edu.catalin.forfoodiesbyfoodies.R;

public class ViewStreetFoodActivity extends AppCompatActivity {

    private TextView tvStreetName, tvStreetLocation, tvKind, tvStreetDescr, tvStreetReviews;
    private ImageView ivStreetImage;
    private String imageId;
    private Button btnViewReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_street_food);
        Intent intent = getIntent();
        ivStreetImage = (ImageView) findViewById(R.id.ivStreetImage);
        tvStreetName = (TextView) findViewById(R.id.tvStreetName);
        tvStreetLocation = (TextView) findViewById(R.id.tvStreetLocation);
        tvKind = (TextView) findViewById(R.id.tvKind);
        tvStreetDescr = (TextView) findViewById(R.id.tvStreetDescr);
        tvStreetReviews = (TextView) findViewById(R.id.tvStreetReviews);
        btnViewReview = (Button) findViewById(R.id.btnViewStreetReview);

        tvStreetName.setText("Name: " + intent.getStringExtra("nameStreet"));
        String stallName = intent.getStringExtra("nameStreet");
        tvStreetLocation.setText("Location: " + intent.getStringExtra("locationStreet"));
        tvKind.setText("Kind: " + intent.getStringExtra("kindStreet"));
        tvStreetDescr.setText("Description: " + intent.getStringExtra("descriptionStreet"));
        tvStreetReviews.setText("Reviews: " + intent.getStringExtra("reviewStreet"));
        tvStreetReviews.setVisibility(View.INVISIBLE);

        imageId = intent.getStringExtra("imageStreet");
        setImage();
        btnViewReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // view reviews for street food -> open new activity and display data from db
                Intent intentStreet = new Intent(ViewStreetFoodActivity.this, CheckStreetFoodReviewActivity.class);
                intentStreet.putExtra("streetFoodStallName", stallName);
                startActivity(intentStreet);
                finish();
            }
        });
    }

    private void setImage() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Images").child(imageId);
        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        DisplayMetrics dm = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(dm);

                        ivStreetImage.setImageBitmap(bm);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ViewStreetFoodActivity.this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        ivStreetImage.setAdjustViewBounds(true);
        ivStreetImage.setMaxWidth(350);
        ivStreetImage.setMaxHeight(350);

    }
}