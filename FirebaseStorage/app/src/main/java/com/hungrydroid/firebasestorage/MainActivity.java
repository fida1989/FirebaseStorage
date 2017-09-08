package com.hungrydroid.firebasestorage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ImageView iv;
    StorageReference storageRef,mountainsRef,mountainImagesRef;
    ArrayList<Image> images;
    int REQUEST_CODE_PICKER = 10;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = (ImageView)findViewById(R.id.iv);
        iv.setDrawingCacheEnabled(true);
        iv.buildDrawingCache();
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create a reference to "mountains.jpg"
                mountainsRef = storageRef.child("images/"+name);

                Bitmap bitmap = iv.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = mountainsRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        System.out.println(exception.toString());
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        System.out.println(downloadUrl);
                        Toast.makeText(MainActivity.this, name+" uploaded!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(MainActivity.this,ViewActivity.class);
                        i.putExtra("image",downloadUrl.toString());
                        startActivity(i);
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(MainActivity.this, taskSnapshot.getBytesTransferred()+"/"+taskSnapshot.getTotalByteCount(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        ((Button)findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ImagePickerActivity.class);

                intent.putExtra(ImagePickerActivity.INTENT_EXTRA_FOLDER_MODE, true);
                intent.putExtra(ImagePickerActivity.INTENT_EXTRA_MODE, ImagePickerActivity.MODE_MULTIPLE);
                intent.putExtra(ImagePickerActivity.INTENT_EXTRA_LIMIT, 1);
                intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SHOW_CAMERA, true);
                intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES, images);
                //intent.putExtra(ImagePickerActivity.INTENT_EXTRA_FOLDER_TITLE, "Album");
                intent.putExtra(ImagePickerActivity.INTENT_EXTRA_IMAGE_TITLE, "Tap to select images");
                //intent.putExtra(ImagePickerActivity.INTENT_EXTRA_IMAGE_DIRECTORY, "Camera");
                startActivityForResult(intent, REQUEST_CODE_PICKER);
            }
        });

        // Create a storage reference from our app
        storageRef = FirebaseStorage.getInstance().getReference();



        // Create a reference to 'images/mountains.jpg'
        //mountainImagesRef = storageRef.child("images/ad.jpg");

        // While the file names are the same, the references point to different files
        //mountainsRef.getName().equals(mountainImagesRef.getName());    // true
        //mountainsRef.getPath().equals(mountainImagesRef.getPath());    // false
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICKER && resultCode == RESULT_OK && data != null) {
            images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
            // do your logic ....
            //name = new File(images.get(0).getPath()).getName().replace(".png","");
            name = images.get(0).getName().replace(".png","");
            Toast.makeText(MainActivity.this, name+"", Toast.LENGTH_SHORT).show();
            Picasso.with(MainActivity.this).load(new File(images.get(0).getPath())).into(iv);

        }
    }


}
