package com.automated.imagepicker;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.automated.imagepicker.Utils.ProgressDialogUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.automated.imagepicker.Listener.PermissionInterface;
import com.automated.imagepicker.Utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import id.zelory.compressor.Compressor;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ImagePickerActivity extends AppCompatActivity
{

    private int PICK_IMAGE = 1000;

    private int CAMERA_REQUEST = 1888;

    File photoFile;


    public static String IMAGE="IMAGE";

    ProgressDialog progressDialog;




    public void checkpermission(final PermissionInterface permissionInterface){
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                /* ... */

                if(report.areAllPermissionsGranted())
                {

                  showDialog();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {

            }


        }).check();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        checkpermission(new PermissionInterface() {
            @Override
            public void interfaceClick(String tag) {

            }
        });
    }

    public void showDialog(){

        progressDialog= ProgressDialogUtils.getProgressDialog(ImagePickerActivity.this);
        final Dialog dialog = new Dialog(ImagePickerActivity.this, R.style.MyDialog);
        dialog.setTitle("Select Option");
        dialog.setContentView(R.layout.dialog_upload_documents);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        LinearLayout ll_gallery=dialog.findViewById(R.id.ll_gallery);
        LinearLayout ll_camera=dialog.findViewById(R.id.ll_camera);

        ll_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(pickPhoto, PICK_IMAGE);
            }
        });
        ll_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {

                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(ImagePickerActivity.this,
                            getResources().getString(R.string.authority),
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                }

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK){
            if(requestCode==CAMERA_REQUEST)
            {
                progressDialog.show();
                Compressor.getDefault(this).compressToFileAsObservable(photoFile)

                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<File>() {
                            @Override
                            public void call(File bitmap) {

                                progressDialog.dismiss();
                                Intent data=new Intent();
                                data.putExtra(IMAGE,bitmap);
                                setResult(Activity.RESULT_OK,data);
                                finish();

                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Toast.makeText(getApplicationContext(),"error while compressing",Toast.LENGTH_LONG).show();
                            }
                        });
            }
            else if (requestCode == PICK_IMAGE && data != null) {
                try {

                    progressDialog.show();

                    File file=new File(FileUtils.getPath(ImagePickerActivity.this,data.getData()));
                    Compressor.getDefault(this).compressToFileAsObservable(file)

                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<File>() {
                                @Override
                                public void call(File bitmap) {
                                    progressDialog.dismiss();
                                    Intent data=new Intent();
                                    data.putExtra(IMAGE,bitmap);
                                    setResult(Activity.RESULT_OK,data);
                                    finish();

                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {

                                    Toast.makeText(getApplicationContext(),"error while compressing",Toast.LENGTH_LONG).show();
                                }
                            });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String mFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File mFile = File.createTempFile(mFileName, ".jpg", storageDir);
        return mFile;
    }

}
