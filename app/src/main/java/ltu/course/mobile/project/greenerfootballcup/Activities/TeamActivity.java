package ltu.course.mobile.project.greenerfootballcup.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ltu.course.mobile.project.greenerfootballcup.R;
import ltu.course.mobile.project.greenerfootballcup.utilities.Adapter.PlayerAdapter;
import ltu.course.mobile.project.greenerfootballcup.utilities.CustomView.ConfigurationView;
import ltu.course.mobile.project.greenerfootballcup.utilities.CustomView.DrawingView;
import ltu.course.mobile.project.greenerfootballcup.utilities.CustomView.LoadingView;
import ltu.course.mobile.project.greenerfootballcup.utilities.LoginDatas;
import ltu.course.mobile.project.greenerfootballcup.utilities.MatchData;
import ltu.course.mobile.project.greenerfootballcup.utilities.Model.Player;
import ltu.course.mobile.project.greenerfootballcup.utilities.Model.Team;
import ltu.course.mobile.project.greenerfootballcup.utilities.ParserHTML;
import ltu.course.mobile.project.greenerfootballcup.utilities.Utilities;

public class TeamActivity extends AppCompatActivity{

    private MediaPlayer mMediaPlayer;

    private String url ;
    private ListView playerList;
    private Player[] players;
    private ImageView preview_signature;
    private LoadingView loadingView;

    private Handler handlerActivity;
    private PopupWindow popupWindow;

    private Team team;
    private boolean noMaxPlayer;
    private boolean noMaxOveragedPlayer;
    private boolean signed;

    private boolean isTeamA;
    private String signatureFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        Intent intent = getIntent();
        url = intent.getStringExtra(MatchActivity.TEAM_URL);

        isTeamA = intent.getIntExtra(MatchActivity.CODE_TEAM, MatchActivity.CODE_TEAM_A) == MatchActivity.CODE_TEAM_A;

        players = null;
        team = new Team();
        noMaxOveragedPlayer = false;
        noMaxPlayer = false;
        signed = false;

        playerList = findViewById(R.id.players);
        playerList.setVisibility(View.INVISIBLE);
        preview_signature = findViewById(R.id.preview_signature);
        Button btnConfirm = findViewById(R.id.btnConfirm);
        Button btnAdminAccess = findViewById(R.id.btnAdminAccess);
        loadingView = findViewById(R.id.loadingView);
        loadingView.setMaxProgress(3);
        handlerActivity = new Handler();

        View header = getLayoutInflater().inflate(R.layout.player_list_header, null);
        playerList.addHeaderView(header);



        btnConfirm.setOnClickListener(v -> {
            if( team.getNumberPlayer() > 0 && signed && ((noMaxOveragedPlayer && noMaxPlayer) || (noMaxPlayer && !noMaxOveragedPlayer && !team.maxOlderPlayerOvershoot()) || (!noMaxPlayer && !team.maxPlayerOvershoot() && noMaxOveragedPlayer) || (!team.maxOlderPlayerOvershoot() && !team.maxPlayerOvershoot()))){
                releaseMediaPlayer();
                if(isTeamA)
                {
                    MatchData.getInstance().setTeamA(team);
                    MatchData.getInstance().setSignatureTeamA(new File(signatureFileName));
                }
                else
                {
                    MatchData.getInstance().setTeamB(team);
                    MatchData.getInstance().setSignatureTeamB(new File(signatureFileName));
                }
                setResult(RESULT_OK);
                finish();
            }else {
                if (team.maxPlayerOvershoot() && !noMaxPlayer)
                    Toast.makeText(getApplicationContext(), R.string.maxPlayerOvershoot, Toast.LENGTH_SHORT).show();
                else if (!noMaxOveragedPlayer && team.maxOlderPlayerOvershoot())
                    Toast.makeText(getApplicationContext(),getString(R.string.maxOlderPlayerOvershoot)+LoginDatas.getInstance().getMaxOveragedPlayer(), Toast.LENGTH_SHORT).show();
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

        btnAdminAccess.setOnClickListener(v -> openVerifyPassword());

        preview_signature.setOnClickListener(v -> openPopupSignature());

        GetPlayers();
    }

    public void GetPlayers(){
        LoadViewAsyncTask parsePlayers = new LoadViewAsyncTask();
        parsePlayers.execute();
    }

    private void openPopupSignature() {
        LayoutInflater layoutInflater = getLayoutInflater();
        View popupView = layoutInflater.inflate(R.layout.popup_signature, null);
        popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);

        Button reset_signature = popupView.findViewById(R.id.reset_signature);
        Button confirm_signature = popupView.findViewById(R.id.confirm_signature);
        final DrawingView drawingView = popupView.findViewById(R.id.signature);

        confirm_signature.setOnClickListener(v -> {
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

        });

        reset_signature.setOnClickListener(v -> {
            drawingView.clearDrawing();
            signed = false;
        });

        popupWindow.showAtLocation(this.preview_signature, Gravity.CENTER,0,0);
    }

    public void saveSignature(Bitmap bitmap){
        try {
            int id = isTeamA ? R.string.signatureTeamAFileName : R.string.signatureTeamBFileName;
            signatureFileName = getFilesDir().getPath() + getResources().getString(id);
            final FileOutputStream out = new FileOutputStream(new File(signatureFileName ));
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

    /**
     * Open a AlertDialog asking the user the admin code
     * If it's the right admin code, then it opens the configuration popup
     *
     */
    public void openVerifyPassword(){
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.verify_password_view, null);

        EditText password = view.findViewById(R.id.et_verify_password);

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(password.getError() != null)
                    password.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        final android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this).setView(view)
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {

            Button button = dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view1 -> {
                if(password.getText().toString().equals(LoginDatas.getInstance().getAdminCode())){
                    dialog.dismiss();
                    openConfigurationDialog();
                }
                else{
                    password.setError(getResources().getString(R.string.wrongAdminCode));
                }
            });
        });
        dialog.show();
    }

    /**
     * Open a AlertDialog using by the user to modify the maximum age, the maximum number of players and the maximum number of overaged players
     * If it's the right admin code, then it opens the configuration dialog
     *
     */
    public void openConfigurationDialog(){
        ConfigurationView view = new ConfigurationView(this);
        ToggleButton toggle_max_overaged_player = view.findViewById(R.id.toggle_max_overaged_player);
        ToggleButton toggle_max_number_player = view.findViewById(R.id.toggle_max_number_player);

        EditText edt_max_age = view.findViewById(R.id.edt_max_age);
        EditText edt_max_overaged_player = view.findViewById(R.id.edt_max_overaged_player);
        EditText edt_max_number_player = view.findViewById(R.id.edt_max_number_player);

        toggle_max_number_player.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                edt_max_number_player.setEnabled(true);
                noMaxPlayer = false;
            }else{
                edt_max_number_player.setEnabled(false);
                noMaxPlayer = true;
            }
        });

        toggle_max_overaged_player.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                edt_max_overaged_player.setEnabled(true);
                edt_max_age.setEnabled(true);
                noMaxOveragedPlayer = false;
            }else{
                edt_max_overaged_player.setEnabled(false);
                edt_max_age.setEnabled(false);
                noMaxOveragedPlayer = true;
            }
        });

        noMaxOveragedPlayer = false;
        noMaxPlayer = false;


        final android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this).setView(view)
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view1 -> {
                LoginDatas.getInstance().setAgeThreshold(Integer.parseInt( edt_max_age.getText().toString()));
                LoginDatas.getInstance().setMaxPlayer(Integer.parseInt( edt_max_number_player.getText().toString()));
                LoginDatas.getInstance().setMaxOveragedPlayer(Integer.parseInt( edt_max_overaged_player.getText().toString()));
                dialog.dismiss();
            });
        });
        dialog.show();
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
                handlerActivity.post(() -> Utilities.checkInternetConnection(TeamActivity.this));
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
                PlayerAdapter playerAdapter = new PlayerAdapter(getApplicationContext(), players,
                                                                team);
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
