package ltu.course.mobile.project.greenerfootballcup;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ReportActivity extends AppCompatActivity {

    private File fileImgResult, fileImgFairplay, fileReport;
    private ImageView imageViewResult, imageViewFairplay, imageViewReport;
    private Button buttonSendReport;

    private static final int REQUEST_CAMERA_PERMISSION = 1324;
    private static final int REQUEST_CAPTURE_RESULT = 1526;
    private static final int REQUEST_CAPTURE_FAIRPLAY = 1527;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        imageViewResult = findViewById(R.id.imageViewResult);
        imageViewFairplay = findViewById(R.id.imageViewFairplay);
        imageViewReport = findViewById(R.id.imageViewReport);

        fileImgResult = new File(getFilesDir(), "pictureResults.png");
        fileImgFairplay = new File(getFilesDir(), "pictureFairplay.png");
        fileReport = new File(getFilesDir(), "report.pdf");

        fileImgResult.delete();
        fileImgFairplay.delete();
        imageViewResult.setOnClickListener(new ImageTakerListener(fileImgResult, REQUEST_CAPTURE_RESULT));
        imageViewFairplay.setOnClickListener(new ImageTakerListener(fileImgFairplay, REQUEST_CAPTURE_FAIRPLAY));
        //imageViewReport.setImageURI(Uri.fromFile(fileReport));

        buttonSendReport = findViewById(R.id.buttonSendReport);
    }

    private static final String FRAGMENT_DIALOG = "dialog";
    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                                                    Manifest.permission.CAMERA)) {
                ConfirmationDialogFragment
                        .newInstance(R.string.camera_permission_confirmation,
                                     new String[]{Manifest.permission.CAMERA},
                                     REQUEST_CAMERA_PERMISSION,
                                     R.string.camera_permission_not_granted)
                        .show(getSupportFragmentManager(), FRAGMENT_DIALOG);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                                                  REQUEST_CAMERA_PERMISSION);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (permissions.length != 1 || grantResults.length != 1) {
                    throw new RuntimeException("Error on requesting camera permission.");
                }
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.camera_permission_not_granted,
                                   Toast.LENGTH_SHORT).show();
                }
                // No need to start camera here; it is handled by onResume
                break;
        }
    }

    public static class ConfirmationDialogFragment extends DialogFragment {

        private static final String ARG_MESSAGE = "message";
        private static final String ARG_PERMISSIONS = "permissions";
        private static final String ARG_REQUEST_CODE = "request_code";
        private static final String ARG_NOT_GRANTED_MESSAGE = "not_granted_message";

        public static ConfirmationDialogFragment newInstance(@StringRes int message,
                                                             String[] permissions, int requestCode, @StringRes int notGrantedMessage) {
            ConfirmationDialogFragment fragment = new ConfirmationDialogFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_MESSAGE, message);
            args.putStringArray(ARG_PERMISSIONS, permissions);
            args.putInt(ARG_REQUEST_CODE, requestCode);
            args.putInt(ARG_NOT_GRANTED_MESSAGE, notGrantedMessage);
            fragment.setArguments(args);
            return fragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Bundle args = getArguments();
            return new AlertDialog.Builder(getActivity())
                    .setMessage(args.getInt(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok,
                                       new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialog, int which) {
                                               String[] permissions = args.getStringArray(ARG_PERMISSIONS);
                                               if (permissions == null) {
                                                   throw new IllegalArgumentException();
                                               }
                                               ActivityCompat.requestPermissions(getActivity(),
                                                                                 permissions, args.getInt(ARG_REQUEST_CODE));
                                           }
                                       })
                    /*.setNegativeButton(android.R.string.cancel,
                                       new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialog, int which) {
                                               Toast.makeText(getActivity(),
                                                              args.getInt(ARG_NOT_GRANTED_MESSAGE),
                                                              Toast.LENGTH_SHORT).show();
                                           }
                                       })*/
                    .create();
        }

    }

    private class ImageTakerListener implements View.OnClickListener {

        private File file;
        private int intentID;

        public ImageTakerListener(File pictureFile, int intentID) {
            file = pictureFile;
            this.intentID = intentID;
        }

        @Override
        public void onClick(View v) {
            if (file.exists())
            {
                openPopupImage(file, intentID);
            }
            else
            {
                takePicture(file, intentID);
            }
        }
    }

    private void openPopupImage(File imgFile, int intentID) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View popupView = layoutInflater.inflate(R.layout.popup_camera, null);
        popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT,
                                      WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);

        Button retake_picture = popupView.findViewById(R.id.buttonRetakePicture);
        ImageView imageView = popupView.findViewById(R.id.imageView);

        imageView.setImageURI(Uri.fromFile(imgFile));

        retake_picture.setOnClickListener(v -> takePicture(imgFile, intentID));

        popupWindow.showAtLocation(this.imageViewFairplay, Gravity.CENTER, 0, 0);
    }

    private void takePicture(File photoFile, int intentID) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            if (!photoFile.exists())
            {
                try
                {
                    photoFile.createNewFile();
                }
                catch (IOException e)
                {
                    Toast.makeText(this, "Could not create the picture file. Error: " + e,
                                   Toast.LENGTH_LONG).show();
                }
            }
            if(!photoFile.exists())
                return;

            /*Uri photoURI = Uri.fromFile(photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);*/
            startActivityForResult(takePictureIntent, intentID);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK)
        {
            File imageFile = null;
            if(requestCode == REQUEST_CAPTURE_RESULT)
                imageFile = fileImgResult;
            else if(requestCode == REQUEST_CAPTURE_FAIRPLAY)
                imageFile = fileImgFairplay;

            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(requestCode == REQUEST_CAPTURE_RESULT)
                imageViewResult.setImageBitmap(bitmap);
            else if(requestCode == REQUEST_CAPTURE_FAIRPLAY)
                imageViewFairplay.setImageBitmap(bitmap);

            checkReportCompleteAndGenerate();
        }
    }

    private void checkReportCompleteAndGenerate() {
        // TODO Complete method
        Toast.makeText(this, "Report generate method called.", Toast.LENGTH_SHORT).show();
    }
}
