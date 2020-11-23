package edu.catalin.forfoodiesbyfoodies.critic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import edu.catalin.forfoodiesbyfoodies.R;
import edu.catalin.forfoodiesbyfoodies.utils.Review;

public class ReviewStreetFoodActivity extends AppCompatActivity {
    private TextView tvStreetName, tvStreetLocation, tvKind, tvStreetDescr;
    private ImageView ivStreetImage;
    private String imageId;
    private Button btnReview;
    private EditText etReviewStreet;
    private Review reviewObject = new Review();
    private DatabaseReference reference;
    private FirebaseAuth fAuth;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_street_food);
        intent = getIntent();

        ivStreetImage = (ImageView) findViewById(R.id.ivStreetImageCritic);
        tvStreetName = (TextView) findViewById(R.id.tvStreetNameCritic);
        tvStreetLocation = (TextView) findViewById(R.id.tvStreetLocationCritic);
        tvKind = (TextView) findViewById(R.id.tvKindCritic);
        tvStreetDescr = (TextView) findViewById(R.id.tvStreetDescrCritic);
        btnReview = (Button)findViewById(R.id.btnReviewStreetFood);
        etReviewStreet = (EditText)findViewById(R.id.etCommentReviewStreet);
        fAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("comments");


        tvStreetName.setText("Name: " + intent.getStringExtra("nameStreetCritic"));
        tvStreetLocation.setText("Location: " + intent.getStringExtra("locationStreetCritic"));
        tvKind.setText("Kind: " + intent.getStringExtra("kindStreetCritic"));
        tvStreetDescr.setText("Description: " + intent.getStringExtra("descriptionStreetCritic"));

        imageId = intent.getStringExtra("imageStreetCritic");
        setImage();
        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveStreetFoodReview();
            }
        });
    }

    private void saveStreetFoodReview() {
        String review = etReviewStreet.getText().toString().trim();
        if(review.isEmpty()){
            Toast.makeText(ReviewStreetFoodActivity.this, "Please add a review before saving!", Toast.LENGTH_SHORT).show();
        }else{
            reviewObject.setRestaurantName(intent.getStringExtra("nameStreetCritic"));
            reviewObject.setReview(review);
            reference.push().setValue(reviewObject)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ReviewStreetFoodActivity.this, "Review added successfully!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ReviewStreetFoodActivity.this, CriticStreetFoodActivity.class));
                                finish();
                            }else {
                                Toast.makeText(ReviewStreetFoodActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
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
                Toast.makeText(ReviewStreetFoodActivity.this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        ivStreetImage.setAdjustViewBounds(true);
        ivStreetImage.setMaxWidth(350);
        ivStreetImage.setMaxHeight(350);

    }
}