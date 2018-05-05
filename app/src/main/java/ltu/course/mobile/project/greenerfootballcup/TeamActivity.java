package ltu.course.mobile.project.greenerfootballcup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
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
import android.widget.Toast;

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
import ltu.course.mobile.project.greenerfootballcup.utilities.LoginDatas;
import ltu.course.mobile.project.greenerfootballcup.utilities.ParserHTML;
import ltu.course.mobile.project.greenerfootballcup.utilities.Player;
import ltu.course.mobile.project.greenerfootballcup.utilities.PlayerAdapter;
import ltu.course.mobile.project.greenerfootballcup.utilities.Team;

public class TeamActivity extends AppCompatActivity {

    private MediaPlayer mMediaPlayer;

    private String url = "http://teamplaycup.se/cup/?team&home=kurirenspelen/17&scope=A-2&name=Notvikens%20IK" ;
    private ListView playerList;
    private List<Player> players;
    private Document document;
    private PlayerAdapter playerAdapter;
    private Button btnConfirm;
    private ImageView preview_signature;
    private Button btnAdminAccess;

    private PopupWindow popupWindow;

    private Team team;
    private boolean adminAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        players = new ArrayList<>();
        team = new Team();
        adminAccess = false;

        playerList = (ListView)findViewById(R.id.players);
        preview_signature = (ImageView) findViewById(R.id.preview_signature);
        btnConfirm = (Button)findViewById(R.id.btnConfirm);
        btnAdminAccess = (Button)findViewById(R.id.btnAdminAccess);

        View header = (View)getLayoutInflater().inflate(R.layout.player_list_header,null);
        playerList.addHeaderView(header);



        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( team.getNumberPlayer() > 0 && ((adminAccess && !team.maxPlayerOvershoot()) || (!team.maxOlderPlayerOvershoot() && !team.maxPlayerOvershoot()))){
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
                    if(mMediaPlayer == null)
                        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.error_sound);
                    else if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.stop();
                        mMediaPlayer.release();
                        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.error_sound);
                    }
                    mMediaPlayer.start();
                }
            }
        });

        btnAdminAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenPopupAdminAccess();
            }
        });

        preview_signature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenPopupSignature();
            }
        });

        GetPlayers();
    }

    public void GetPlayers(){
        final ParsePlayers parsePlayers = new ParsePlayers();
        parsePlayers.execute();
    }

    private class ParsePlayers extends AsyncTask<String, Long, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                document = ParserHTML.getHTMLDocument(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Get all hearders
            Elements allH3headers = document.select("h3");
            Element playerHeader = null;
            //Find the right header
            for (Element header : allH3headers) {
                if (header.text().equals("Spelare")) {
                    playerHeader = header;
                    break;
                }
            }

            //Get all the players
            Elements tablePlayers = playerHeader.nextElementSibling().select("table").select("tbody").select("tr");

            //Parse name and age
            for (Element player : tablePlayers) {
                Elements data = player.select("td");
                players.add(new Player(data.get(0).text(),data.get(1).text()));
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            playerAdapter = new PlayerAdapter(getApplicationContext(), players,team);
            playerList.setAdapter(playerAdapter);
        }
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
                        btnAdminAccess.setBackgroundColor(Color.argb(1,224,224,224));
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
                        Bitmap bitmap = drawingView.getBitmap();
                        saveSignature(bitmap);
                        preview_signature.setImageBitmap(bitmap);
                        popupWindow.dismiss();
                        popupWindow=null;
            }});

        reset_signature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.clearDrawing();
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
}
