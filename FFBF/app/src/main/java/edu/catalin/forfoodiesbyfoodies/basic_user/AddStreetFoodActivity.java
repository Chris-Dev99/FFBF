package edu.catalin.forfoodiesbyfoodies.basic_user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import edu.catalin.forfoodiesbyfoodies.R;
import edu.catalin.forfoodiesbyfoodies.utils.StreetFood;

public class AddStreetFoodActivity extends AppCompatActivity {

    private ImageView ivStreetFood;
    private EditText etStallName, etStreetFoodLocation, etStreetDescription;
    private Spinner spKind;
    private Button btnSaveStreetFood, btnImageChooseStreetFood;
    private StorageTask uploadTask;
    private StreetFood streetFood = new StreetFood();
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private FirebaseAuth fAuth;


    private Uri imageURI;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;

    String cameraPermission[];
    String storagePermission[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_street_food);

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        fAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("Images");
        reference = FirebaseDatabase.getInstance().getReference().child("streetfood");

        ivStreetFood = (ImageView)findViewById(R.id.ivStreetFood);
        etStallName = (EditText)findViewById(R.id.etStallName);
        etStreetFoodLocation = (EditText)findViewById(R.id.etStreetFoodLocation);
        etStreetDescription = (EditText)findViewById(R.id.etStreetDescription);
        btnImageChooseStreetFood = (Button)findViewById(R.id.btnImageChooseStreetFood);
        btnSaveStreetFood = (Button)findViewById(R.id.btnSaveStreetFoood);
        spKind = (Spinner)findViewById(R.id.spKind);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.kind_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spKind.setAdapter(adapter);

        btnImageChooseStreetFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageImportDialog();
            }
        });

        btnSaveStreetFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(AddStreetFoodActivity.this, "Upload in progress!", Toast.LENGTH_SHORT).show();
                } else {
                    fileUploader();
                }
            }
        });
    }

    private String getExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        if(mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) !=null){
            return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        }else{
            return null;
        }

    }

    private void fileUploader() {
        String imageId = System.currentTimeMillis() + ".streetFood." + getExtension(imageURI);
        String kind = spKind.getSelectedItem().toString();
        streetFood.setName(etStallName.getText().toString());
        streetFood.setLocation(etStreetFoodLocation.getText().toString());
        streetFood.setKind(kind);
        streetFood.setDescription(etStreetDescription.getText().toString());
        streetFood.setReview("not completed yet");
        streetFood.setImageId(imageId);

        reference = reference.child(etStallName.getText().toString());

        reference.setValue(streetFood);

        StorageReference ref = storageReference.child(imageId);
        uploadTask = ref.putFile(imageURI)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(AddStreetFoodActivity.this, "Image added successfully!", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddStreetFoodActivity.this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void showImageImportDialog() {
        String[] items = {"Camera", "Gallery"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Select Image");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    //camera option clicked
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        // permision allowed, take picture
                        pickCamera();
                    }
                }
                if (which == 1) {
                    // gallery
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickGallery();
                    }
                }
            }
        });
        dialog.create().show();
    }

    private void pickGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPick");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image To Text");
        imageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        pickCamera();
                    } else {
                        Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickGallery();
                    } else {
                        Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                CropImage.activity(imageURI)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);

            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                ivStreetFood.setImageURI(resultUri);
                ivStreetFood.setBackground(null);
                imageURI = resultUri;

                BitmapDrawable bitmapDrawable = (BitmapDrawable) ivStreetFood.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AddStreetFoodActivity.this, StreetFoodActivity.class));
        finish();
    }
}