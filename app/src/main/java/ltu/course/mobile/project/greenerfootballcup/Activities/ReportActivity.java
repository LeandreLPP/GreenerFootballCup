package ltu.course.mobile.project.greenerfootballcup.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.IOException;

import ltu.course.mobile.project.greenerfootballcup.R;
import ltu.course.mobile.project.greenerfootballcup.utilities.ConfirmationDialogFragment;
import ltu.course.mobile.project.greenerfootballcup.utilities.LoginDatas;
import ltu.course.mobile.project.greenerfootballcup.utilities.ReportGenerator;

public class ReportActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 1324;
    private static final int REQUEST_CAPTURE_RESULT = 1526;
    private static final int REQUEST_CAPTURE_FAIRPLAY = 1527;

    private static final String FRAGMENT_DIALOG = "dialog";

    private File fileImgResult, fileImgFairplay, fileReport, backupFile;
    private ImageView imageViewResult, imageViewFairplay;
    private PDFView imageViewReport;

    private Button buttonSendReport;
    private Button buttonPreviewReport;

    private PopupWindow popupWindow;

    private boolean hasAuthorisationCamera;
    private GenerateReportTask generateReportTask;
    private ProgressBar progressBarResult, progressBarFairplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        hasAuthorisationCamera = false;

        imageViewResult = (ImageView) findViewById(R.id.imageViewResult);
        imageViewFairplay = (ImageView) findViewById(R.id.imageViewFairplay);
        imageViewReport = (PDFView) findViewById(R.id.imageViewReport);

        progressBarResult = (ProgressBar) findViewById(R.id.progressBarResult);
        progressBarFairplay = (ProgressBar) findViewById(R.id.progressBarFairplay);

        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        fileImgResult = new File(dir, "pictureResults.jpg");
        fileImgFairplay = new File(dir, "pictureFairplay.jpg");
        backupFile = new File(dir, "backup.jpg");
        fileReport = new File(dir, "report.pdf");

        imageViewResult.setOnClickListener((c) -> {
            if(fileImgResult.exists())
                openPopupImage(fileImgResult, REQUEST_CAPTURE_RESULT);
            else
                takePicture(REQUEST_CAPTURE_RESULT);});
        imageViewFairplay.setOnClickListener((c) -> {
            if(fileImgResult.exists())
                openPopupImage(fileImgFairplay, REQUEST_CAPTURE_FAIRPLAY);
            else
                takePicture(REQUEST_CAPTURE_FAIRPLAY);});

        new LoadImage().execute(new Param(fileImgResult, imageViewResult, progressBarResult));
        new LoadImage().execute(new Param(fileImgFairplay, imageViewFairplay, progressBarFairplay));
        // TODO report image

        buttonSendReport = (Button) findViewById(R.id.buttonSendReport);
        buttonSendReport.setOnClickListener((c)->sendReport());
        buttonSendReport.setEnabled(false);
        buttonPreviewReport = (Button) findViewById(R.id.buttonPreviewReport);
        buttonPreviewReport.setOnClickListener((c)->openPopupReport());
        buttonPreviewReport.setEnabled(false);
        checkReportCompleteAndGenerate();
    }
    @Override
    protected void onResume() {
        super.onResume();
        checkAuthorisationCamera();
    }

    private void checkAuthorisationCamera(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED)
        {
            hasAuthorisationCamera = true;
        }
        else
        {
            hasAuthorisationCamera = false;
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                                                    Manifest.permission.CAMERA))
            {
                ConfirmationDialogFragment
                        .newInstance(R.string.camera_permission_confirmation,
                                     new String[]{Manifest.permission.CAMERA},
                                     REQUEST_CAMERA_PERMISSION,
                                     R.string.camera_permission_not_granted)
                        .show(getSupportFragmentManager(), FRAGMENT_DIALOG);
            }
            else
            {
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
                hasAuthorisationCamera = false;
                if (permissions.length != 1 || grantResults.length != 1) {
                    throw new RuntimeException("Error on requesting camera permission.");
                }
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    hasAuthorisationCamera = true;
                }
                else {
                    Toast.makeText(this, R.string.camera_permission_not_granted,
                                   Toast.LENGTH_SHORT).show();
                }
                // No need to start camera here; it is handled by onResume
                break;
        }
    }

    private void openPopupImage(File imgFile, int intentID) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View popupView = layoutInflater.inflate(R.layout.popup_camera, null);
        popupView.setOnClickListener((c) -> popupWindow.dismiss());
        popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT,
                                      WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);

        Button retake_picture = (Button) popupView.findViewById(R.id.buttonRetakePicture);
        ImageView imageView = (ImageView) popupView.findViewById(R.id.imageView);
        ProgressBar progressBar = (ProgressBar) popupView.findViewById(R.id.progressBar);

        new LoadImage().execute(new Param(imgFile, imageView, progressBar));

        retake_picture.setOnClickListener(v -> takePicture(intentID));

        popupWindow.showAtLocation(imageViewFairplay, Gravity.CENTER, 0, 0);
    }


    private PDFView pdfView;
    private void openPopupReport() {
        LayoutInflater layoutInflater = getLayoutInflater();
        View popupView = layoutInflater.inflate(R.layout.popup_report, null);
        popupWindow = new PopupWindow(popupView,
                                                  WindowManager.LayoutParams.WRAP_CONTENT,
                                                  WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);

        pdfView = (PDFView) popupView.findViewById(R.id.pdfView);
        popupView.setOnClickListener((c) ->  popupWindow.dismiss());

        popupWindow.showAtLocation(imageViewFairplay, Gravity.CENTER, 0, 0);
        pdfView.fromFile(fileReport).load();
    }

    private void takePicture(int intentID) {
        checkAuthorisationCamera();
        if(!hasAuthorisationCamera){
            Toast.makeText(this, R.string.camera_permission_not_granted, Toast.LENGTH_LONG).show();
            return;
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            try
            {
                File file = getPictureFile(intentID);
                Uri photoURI = FileProvider.getUriForFile(this,
                                                          "ltu.course.mobile.project.greenerfootballcup.fileprovider",
                                                          file);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, intentID);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                Toast.makeText(this, "Error while creating the temp file: "+e, Toast.LENGTH_LONG).show();
            }
        }
    }

    private File tempFile;
    private File getPictureFile(int intentID) throws IOException {
        File originalFile = intentID == REQUEST_CAPTURE_RESULT ? fileImgResult : fileImgFairplay;
        String name = intentID == REQUEST_CAPTURE_RESULT ? "pictureResults" : "pictureFairplay";
        if (originalFile.exists()){
            originalFile.renameTo(backupFile);
        }
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        tempFile = File.createTempFile(name,".jpg", dir);

        return tempFile;
    }

    private class LoadImage extends AsyncTask<Param, Void, Boolean> {

        private Bitmap bmp;
        private ImageView imgView;
        private ProgressBar progressBar;

        @Override
        protected Boolean doInBackground(Param... params) {
            progressBar = params[0].progressBar;
            publishProgress();
            File file = params[0].imageFile;
            imgView = params[0].imgToLoad;
            try
            {
                bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
                int nh = (int) ( bmp.getHeight() * (2048.0 / bmp.getWidth()) );
                bmp = Bitmap.createScaledBitmap(bmp, 2048,  nh, true);

                ExifInterface ei = new ExifInterface(file.getPath());
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                                     ExifInterface.ORIENTATION_UNDEFINED);

                Bitmap rotatedBitmap = null;
                switch(orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(bmp, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(bmp, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(bmp, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotatedBitmap = bmp;
                }
                bmp = rotatedBitmap;
                return bmp != null;
            }
            catch (Exception e)
            {
                Log.d("","Error while saving picture",e);
                return false;
            }
        }

        private Bitmap rotateImage(Bitmap source, float angle) {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                                       matrix, true);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Boolean b) {
            progressBar.setVisibility(View.GONE);
            if(b)
                imgView.setImageBitmap(bmp);
        }
    }

    private class Param {
        public File imageFile;
        public ImageView imgToLoad;
        public ProgressBar progressBar;
        public Param(File f, ImageView v, ProgressBar b)
        {
            imageFile = f;
            imgToLoad = v;
            progressBar = b;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK)
        {
            File imgFile = requestCode == REQUEST_CAPTURE_RESULT ? fileImgResult : fileImgFairplay;
            ImageView imgView = requestCode == REQUEST_CAPTURE_RESULT ? imageViewResult : imageViewFairplay;
            ProgressBar progressBar = requestCode == REQUEST_CAPTURE_RESULT ? progressBarResult : progressBarFairplay;

            tempFile.renameTo(imgFile);

            new LoadImage().execute(new Param(imgFile, imgView, progressBar));

            if(popupWindow != null && popupWindow.isShowing())
                popupWindow.dismiss();

            checkReportCompleteAndGenerate();
        } else if (backupFile.exists()){
            File imgFile = requestCode == REQUEST_CAPTURE_RESULT ? fileImgResult : fileImgFairplay;
            backupFile.getAbsoluteFile().renameTo(imgFile);
        }
    }

    private void checkReportCompleteAndGenerate() {
        if(fileImgResult.exists() && fileImgFairplay.exists())
        {
            if(generateReportTask != null)
                generateReportTask.cancel(true);
            generateReportTask = new GenerateReportTask();
            generateReportTask.execute();
        }
    }

    private void sendReport() {
        String subject = "Results for the match"; // TODO generate coherent text
        String text = "Result"; //TODO generate coherent text

        sendEmail(this, LoginDatas.getInstance().getEmailAddress(), subject, text, fileReport);
    }

    public static void sendEmail(Context context, String emailTo,
                                 String subject, String emailText, File attachment) {
        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        String[] to = new String[] {emailTo};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailText);
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(attachment));

        // needed to select only the email apps
        Intent emailSelectorIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
        emailIntent.setSelector(emailSelectorIntent);

        context.startActivity(Intent.createChooser(emailIntent , "Send email..."));
    }

    private class GenerateReportTask extends AsyncTask<Void, Void, Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            buttonPreviewReport.setEnabled(false);
            buttonSendReport.setEnabled(false);
            Toast.makeText(ReportActivity.this, "Started generating", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return ReportGenerator.generate(fileReport, fileImgResult, fileImgFairplay, null, null, null);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            Toast.makeText(ReportActivity.this, "Generation completed: "+success, Toast.LENGTH_LONG).show();
            if(success)
            {
                imageViewReport.fromFile(fileReport)
                               .enableDoubletap(false)
                               .load();
                buttonSendReport.setEnabled(true);
                buttonPreviewReport.setEnabled(true);
            }
        }
    }
}
