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
import android.os.Build;
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
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ltu.course.mobile.project.greenerfootballcup.R;
import ltu.course.mobile.project.greenerfootballcup.utilities.ConfirmationDialogFragment;
import ltu.course.mobile.project.greenerfootballcup.utilities.LoginDatas;
import ltu.course.mobile.project.greenerfootballcup.utilities.MatchData;
import ltu.course.mobile.project.greenerfootballcup.utilities.Model.Field;
import ltu.course.mobile.project.greenerfootballcup.utilities.Model.Match;
import ltu.course.mobile.project.greenerfootballcup.utilities.Model.Team;
import ltu.course.mobile.project.greenerfootballcup.utilities.ReportGenerator;
import ltu.course.mobile.project.greenerfootballcup.utilities.Utilities;

public class ReportActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS = 1324;
    private static final int REQUEST_CAPTURE_RESULT = 1526;
    private static final int REQUEST_CAPTURE_FAIRPLAY = 1527;

    private static final String FRAGMENT_DIALOG = "dialog";

    private File fileImgResult, fileImgFairplay, fileReport, backupFile;
    private ImageView imageViewResult, imageViewFairplay;
    private PDFView imageViewReport;

    private Button buttonSendReport;
    private Button buttonPreviewReport;

    private PopupWindow popupWindow;

    private GenerateReportTask generateReportTask;
    private ProgressBar progressBarResult, progressBarFairplay;
    private boolean hasAuthorisations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        hasAuthorisations = false;

        imageViewResult = (ImageView) findViewById(R.id.imageViewResult);
        imageViewFairplay = (ImageView) findViewById(R.id.imageViewFairplay);
        imageViewReport = (PDFView) findViewById(R.id.imageViewReport);

        progressBarResult = (ProgressBar) findViewById(R.id.progressBarResult);
        progressBarFairplay = (ProgressBar) findViewById(R.id.progressBarFairplay);

        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        fileImgResult = new File(dir, "pictureResults.jpg");
        fileImgFairplay = new File(dir, "pictureFairplay.jpg");
        backupFile = new File(dir, "backup.jpg");

        imageViewResult.setOnClickListener((c) -> {
            if(fileImgResult.exists())
                openPopupImage(fileImgResult, REQUEST_CAPTURE_RESULT);
            else
                takePicture(REQUEST_CAPTURE_RESULT);});
        imageViewFairplay.setOnClickListener((c) -> {
            if(fileImgFairplay.exists())
                openPopupImage(fileImgFairplay, REQUEST_CAPTURE_FAIRPLAY);
            else
                takePicture(REQUEST_CAPTURE_FAIRPLAY);});

        new LoadImage().execute(new Param(fileImgResult, imageViewResult, progressBarResult));
        new LoadImage().execute(new Param(fileImgFairplay, imageViewFairplay, progressBarFairplay));

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
        checkAuthorisations();
    }
    private void checkAuthorisations(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                   == PackageManager.PERMISSION_GRANTED
                && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED))
        {
            hasAuthorisations = true;
        }
        else
        {
            hasAuthorisations = false;
            String[] permissions = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) ?
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE} :
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                                                    Manifest.permission.CAMERA)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this,
                                                                           Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
                        ActivityCompat.shouldShowRequestPermissionRationale(this,
                                                                Manifest.permission.READ_EXTERNAL_STORAGE)))
            {
                ConfirmationDialogFragment
                        .newInstance(R.string.permission_confirmation,
                                     permissions,
                                     REQUEST_PERMISSIONS,
                                     R.string.permission_confirmation)
                        .show(getSupportFragmentManager(), FRAGMENT_DIALOG);
            }
            else
            {
                ActivityCompat.requestPermissions(this, permissions,REQUEST_PERMISSIONS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                hasAuthorisations = true;
                if (grantResults.length <= 0) {
                    hasAuthorisations = false;
                }
                for(int result : grantResults)
                    if(result != PackageManager.PERMISSION_GRANTED)
                        hasAuthorisations = false;
                if(!hasAuthorisations) {
                    Toast.makeText(this, R.string.camera_permission_not_granted,
                                   Toast.LENGTH_LONG).show();
                }
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

    private void openPopupReport() {
        LayoutInflater layoutInflater = getLayoutInflater();
        View popupView = layoutInflater.inflate(R.layout.popup_report, null);
        popupWindow = new PopupWindow(popupView,
                                      WindowManager.LayoutParams.WRAP_CONTENT,
                                      WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);

        PDFView pdfView = (PDFView) popupView.findViewById(R.id.pdfView);
        Button buttonRegen = (Button) popupView.findViewById(R.id.buttonRegenerate);
        TextView textViewPath = (TextView) popupView.findViewById(R.id.textViewPath);

        buttonRegen.setOnClickListener((c) -> {
            new GenerateReportTask().execute();
            popupWindow.dismiss();
        });

        textViewPath.setText(fileReport.getAbsolutePath());

        popupView.setOnClickListener((c) ->  popupWindow.dismiss());

        popupWindow.showAtLocation(imageViewFairplay, Gravity.CENTER, 0, 0);
        pdfView.fromFile(fileReport).load();
    }

    private void takePicture(int intentID) {
        checkAuthorisations();
        if(!hasAuthorisations){
            Toast.makeText(this, R.string.camera_permission_not_granted, Toast.LENGTH_LONG).show();
            return;
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            try
            {
                File file = getPictureFile(intentID);
                Uri photoURI;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    photoURI = FileProvider.getUriForFile(this,"ltu.course.mobile.project.greenerfootballcup.fileprovider",file);
                else
                    photoURI = Uri.fromFile(file);
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
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(file.getAbsolutePath(), options);

                int height = options.outHeight;
                int width = options.outWidth;

                int nh = (int) ( height * (1024.0 / width) );
                bmp = Utilities.decodeSampledBitmapFromResource(file.getAbsolutePath(),1024,nh);
                return bmp != null;
            }
            catch (Exception e)
            {
                Log.d("","Error while loading picture",e);
                return false;
            }
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

    private class RotateAndSaveFile extends AsyncTask<File, Void, Void> {

        private Callback callback;

        public RotateAndSaveFile setCallback(Callback callback)
        {
            this.callback = callback;
            return this;
        }

        @Override
        protected Void doInBackground(File... files) {
            try
            {
                File imageFile = files[0];


                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);

                int height = options.outHeight;
                int width = options.outWidth;
                int nh = (int) ( height * (1024.0 / width) );

                Bitmap bmp = Utilities.decodeSampledBitmapFromResource(tempFile.getAbsolutePath(),1024,nh);
                ExifInterface ei = new ExifInterface(tempFile.getPath());
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                                     ExifInterface.ORIENTATION_UNDEFINED);

                if(orientation == ExifInterface.ORIENTATION_ROTATE_180)
                    bmp = rotateImage(bmp, 180);

                FileOutputStream fos = new FileOutputStream(imageFile);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                tempFile.delete();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(callback != null)
                callback.execute();
        }

        private Bitmap rotateImage(Bitmap source, float angle) {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                                       matrix, true);
        }
    }

    private interface Callback {
        void execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK)
        {
            File imgFile = requestCode == REQUEST_CAPTURE_RESULT ? fileImgResult : fileImgFairplay;
            ImageView imgView = requestCode == REQUEST_CAPTURE_RESULT ? imageViewResult : imageViewFairplay;
            ProgressBar progressBar = requestCode == REQUEST_CAPTURE_RESULT ? progressBarResult : progressBarFairplay;

            new RotateAndSaveFile()
                    .setCallback(() -> {
                        new LoadImage().execute(new Param(imgFile, imgView, progressBar));
                        checkReportCompleteAndGenerate();
                    })
                    .execute(imgFile);

            if(popupWindow != null && popupWindow.isShowing())
                popupWindow.dismiss();
        } else if (backupFile.exists()){
            File imgFile = requestCode == REQUEST_CAPTURE_RESULT ? fileImgResult : fileImgFairplay;
            backupFile.getAbsoluteFile().renameTo(imgFile);
        }
    }

    private void checkReportCompleteAndGenerate() {
        checkAuthorisations();
        if(!hasAuthorisations){
            Toast.makeText(this, R.string.storage_permission_confirmation, Toast.LENGTH_LONG).show();
            return;
        }
        if(fileImgResult.exists() && fileImgFairplay.exists())
        {
            if(generateReportTask != null)
                generateReportTask.cancel(true);
            generateReportTask = new GenerateReportTask();
            generateReportTask.execute();
        }
    }

    private void sendReport() {
        Match match = MatchData.getInstance().getMatch();
        Date today = Calendar.getInstance().getTime();
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String dateStr = format.format(today);
        String subject = "Results match "+match.getNumber()+" at "+
                         match.getTime()+", group "+match.getGroup();
        String text = "Result for the match number "+match.getNumber()+
                      " opposing "+ match.getFirstTeam()+" and "+match.getSecondTeam()+" of the group "+match.getGroup()+
                      " the "+dateStr+ " at "+match.getTime()+ " on the field "+MatchData.getInstance().getField().getFullName()+"."+
                      "\n\nMail generated automatically by the \"Greener Football Cup\" application.";

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
            Toast.makeText(ReportActivity.this, "Report generation started.", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean ret = false;
            try
            {
                Match match = MatchData.getInstance().getMatch();
                Team teamA = MatchData.getInstance().getTeamA();
                Team teamB = MatchData.getInstance().getTeamB();
                Field field = MatchData.getInstance().getField();
                File signatureTeamA = MatchData.getInstance().getSignatureTeamA();
                File signatureTeamB = MatchData.getInstance().getSignatureTeamB();

                File dir = getReportSaveDir();
                Date today = Calendar.getInstance().getTime();
                DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String dateStr = format.format(today);
                String fileName = "MATCH_"+match.getNumber()+"_"+dateStr+"_"+
                                  match.getTime()+"_"+match.getGroup()+".pdf";
                fileReport = new File(dir, fileName);
                ret = ReportGenerator.generate(fileReport, fileImgResult, fileImgFairplay,
                                               match, field, teamA, teamB,
                                               signatureTeamA, signatureTeamB);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return ret;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if(success)
            {
                Toast.makeText(ReportActivity.this, "Report generation complete! File saved at "+fileReport.getAbsolutePath()
                        , Toast.LENGTH_LONG).show();
                imageViewReport.fromFile(fileReport)
                               .enableDoubletap(false)
                               .load();
                buttonSendReport.setEnabled(true);
                buttonPreviewReport.setOnClickListener((c)->openPopupReport());
                buttonPreviewReport.setText(R.string.button_preview_report);
                buttonPreviewReport.setEnabled(true);
            } else {
                Toast.makeText(ReportActivity.this, "Report generation failed.", Toast.LENGTH_LONG).show();
                buttonPreviewReport.setOnClickListener((c)->new GenerateReportTask().execute());
                buttonPreviewReport.setText(R.string.button_regen_report);
                buttonPreviewReport.setEnabled(true);
            }
        }
    }

    private File getReportSaveDir() {
        checkAuthorisations();
        if(!hasAuthorisations){
            Toast.makeText(this, R.string.storage_permission_confirmation, Toast.LENGTH_LONG).show();
            return null;
        }
        File ret = new File(Environment.getExternalStorageDirectory(), "GreenerFootballCup");
        if(!ret.exists() || !ret.isDirectory())
        {
            boolean deleted = ret.delete();
            boolean maked = ret.mkdir();
            boolean izok = deleted || maked;
        }
        return ret;
    }
}
