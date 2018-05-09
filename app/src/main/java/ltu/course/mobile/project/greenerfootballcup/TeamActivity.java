package ltu.course.mobile.project.greenerfootballcup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ltu.course.mobile.project.greenerfootballcup.utilities.DrawingView;
import ltu.course.mobile.project.greenerfootballcup.utilities.LoadingView;
import ltu.course.mobile.project.greenerfootballcup.utilities.LoginDatas;
import ltu.course.mobile.project.greenerfootballcup.utilities.ParserHTML;
import ltu.course.mobile.project.greenerfootballcup.utilities.Player;
import ltu.course.mobile.project.greenerfootballcup.utilities.PlayerAdapter;
import ltu.course.mobile.project.greenerfootballcup.utilities.Team;
import ltu.course.mobile.project.greenerfootballcup.utilities.Utilities;

public class TeamActivity extends Activity{

    private MediaPlayer mMediaPlayer;

    private String url = "http://teamplaycup.se/cup/?team&home=kurirenspelen/17&scope=A-2&name=Notvikens%20IK" ;
    private ListView playerList;
    private Player[] players;
    private PlayerAdapter playerAdapter;
    private Button btnConfirm;
    private ImageView preview_signature;
    private Button btnAdminAccess;
    private LoadingView loadingView;

    private Handler handlerActivity;
    private PopupWindow popupWindow;

    private Team team;
    private boolean adminAccess;
    private boolean signed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        players = null;
        team = new Team();
        adminAccess = false;
        signed = false;

        playerList = (ListView)findViewById(R.id.players);
        playerList.setVisibility(View.INVISIBLE);
        preview_signature = (ImageView) findViewById(R.id.preview_signature);
        btnConfirm = (Button)findViewById(R.id.btnConfirm);
        btnAdminAccess = (Button)findViewById(R.id.btnAdminAccess);
        loadingView = findViewById(R.id.loadingView);
        loadingView.setMaxProgress(3);
        handlerActivity = new Handler();

        View header = (View)getLayoutInflater().inflate(R.layout.player_list_header,null);
        playerList.addHeaderView(header);



        btnConfirm.setOnClickListener(v -> {
            if( team.getNumberPlayer() > 0 && ((adminAccess && !team.maxPlayerOvershoot()) || (!team.maxOlderPlayerOvershoot() && !team.maxPlayerOvershoot())) && signed){
                releaseMediaPlayer();
                Intent myIntent = new Intent(getApplicationContext(), ReportActivity.class);
                startActivity(myIntent);
            }else {
                if (team.maxPlayerOvershoot())
                    Toast.makeText(getApplicationContext(), R.string.maxPlayerOvershoot, Toast.LENGTH_SHORT).show();
                else if (!adminAccess && team.maxOlderPlayerOvershoot())
                    Toast.makeText(getApplicationContext(), R.string.maxOlderPlayerOvershoot, Toast.LENGTH_SHORT).show();
                else if (team.getNumberPlayer() == 0)
                    Toast.makeText(getApplicationContext(), R.string.notEnoughPlayers, Toast.LENGTH_SHORT).show();
                else if(!signed)
                    Toast.makeText(getApplicationContext(), R.string.notSigned, Toast.LENGTH_SHORT).show();

                if(mMediaPlayer == null)
                    mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.error_sound);
                else if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.error_sound);
                }
                mMediaPlayer.start();
            }
        });

        btnAdminAccess.setOnClickListener(v -> OpenPopupAdminAccess());

        preview_signature.setOnClickListener(v -> OpenPopupSignature());

        GetPlayers();
    }

    public void GetPlayers(){
        LoadViewAsyncTask parsePlayers = new LoadViewAsyncTask();
        parsePlayers.execute();
    }

    private void OpenPopupAdminAccess() {
        LayoutInflater layoutInflater = getLayoutInflater();
        View popupView = layoutInflater.inflate(R.layout.popup_admin_access, null);
        popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);

        Button btnValidateAdminAccess = (Button)popupView.findViewById(R.id.validateAdminAccess);
        Button btnCancelAdminAccess = (Button)popupView.findViewById(R.id.cancelAdminAccess);
        final EditText admin_code =(EditText)popupView.findViewById(R.id.admin_access);

        btnValidateAdminAccess.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(admin_code.getText() != null)
                {
                    if(admin_code.getText().toString().equals(LoginDatas.getInstance().getAdminCode())){
                        adminAccess = true;
                        btnAdminAccess.setBackgroundColor(Color.RED);
                        popupWindow.dismiss();
                        popupWindow=null;
                    }
                    else{
                        adminAccess = false;
                        Toast.makeText(getApplicationContext(),R.string.toastWrongAdminCode,Toast.LENGTH_SHORT).show();
                    }

                }else{
                    adminAccess = false;
                    Toast.makeText(getApplicationContext(),R.string.toastWrongAdminCode,Toast.LENGTH_SHORT).show();
                }
            }});

        btnCancelAdminAccess.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                popupWindow=null;
                adminAccess = false;
            }});

        popupWindow.showAtLocation(this.btnAdminAccess, Gravity.CENTER,0,0);
    }

    private void OpenPopupSignature() {
        LayoutInflater layoutInflater = getLayoutInflater();
        View popupView = layoutInflater.inflate(R.layout.popup_signature, null);
        popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);

        Button reset_signature = (Button)popupView.findViewById(R.id.reset_signature);
        Button confirm_signature = (Button)popupView.findViewById(R.id.confirm_signature);
        final DrawingView drawingView =(DrawingView)popupView.findViewById(R.id.signature);

        confirm_signature.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                try{
                    Bitmap bitmap = drawingView.getBitmap();
                    saveSignature(bitmap);
                    preview_signature.setImageBitmap(bitmap);
                    popupWindow.dismiss();
                    popupWindow=null;
                }catch (DrawingView.NullSignatureException e){
                    Toast.makeText(getApplicationContext(), R.string.notSigned, Toast.LENGTH_SHORT).show();
                    signed = false;
                }

            }});

        reset_signature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.clearDrawing();
                signed = false;
            }
        });

        popupWindow.showAtLocation(this.preview_signature, Gravity.CENTER,0,0);
    }

    public void saveSignature(Bitmap bitmap){
        try {
            final FileOutputStream out = new FileOutputStream(new File(getFilesDir().getPath() +R.string.signatureFileName ));
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            signed = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void releaseMediaPlayer(){
        if(mMediaPlayer != null){
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

    }

    private class LoadViewAsyncTask extends AsyncTask<String, Integer, Utilities.Result> {

        @Override
        protected void onPreExecute() {
            loadingView.setOnClickListener(null);
            loadingView.setLoadingText(R.string.loading);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            loadingView.updateBar(progress[0]);
        }

        @Override
        protected Utilities.Result doInBackground(String... strings) {
            Utilities.Result result = new Utilities.Result();
            result.success = false;
            try {
                publishProgress(0);
                handlerActivity.post(() -> {
                    Utilities.checkInternetConnection(TeamActivity.this);
                });
                publishProgress(1);
                Document html = ParserHTML.getHTMLDocument(url);
                publishProgress(2);
                players = ParserHTML.extractPlayers(html);
                publishProgress(3);
                result.success = true;
            }
            catch (HttpStatusException httpException)
                {
                    result.errorMessage = getString(R.string.website_down_error);
                }
            catch (Exception e)
                {
                    result.errorMessage = getString(R.string.loading_failed);
                }
                return result;
        }

        @Override
        protected void onPostExecute(Utilities.Result result) {
            if (result.success) {
                playerAdapter = new PlayerAdapter(getApplicationContext(), players, team);
                playerList.setAdapter(playerAdapter);
                playerList.setVisibility(View.VISIBLE);
                loadingView.setVisibility(View.INVISIBLE);
            }else{
                loadingView.displayError(result.errorMessage);
                loadingView.setOnClickListener((c) -> (new LoadViewAsyncTask()).execute());
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            loadingView.displayError(getString(R.string.loading_failed));
        }
    }
}
