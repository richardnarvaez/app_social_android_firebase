package com.richardnarvaez.up.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerInitListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView;
import com.richardnarvaez.up.Activity.PhothoUpload.NewPostActivity;
import com.richardnarvaez.up.R;
import com.richardnarvaez.up.Utility.FirebaseUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YouTubeUploadActivity extends AppCompatActivity {

    private LinearLayout layoutYoutube;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_upload);
        layoutYoutube = findViewById(R.id.layoutYoutube);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent);
            }
        }

    }

    String titleYT, authorYT, author_url, image_url;
    String id;

    private void handleSendText(Intent intent) {
        final String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null && isYouTube(sharedText)) {
            layoutYoutube.setVisibility(View.VISIBLE);
            id = extractId(sharedText);
            new JsonTask().execute("https://noembed.com/embed?url=https://www.youtube.com/watch?v=" + id);

            YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube);
            youTubePlayerView.getPlayerUIController().showFullscreenButton(false);
            youTubePlayerView.getPlayerUIController().showYouTubeButton(false);
            getLifecycle().addObserver(youTubePlayerView);
            youTubePlayerView.initialize(new YouTubePlayerInitListener() {
                @Override
                public void onInitSuccess(final YouTubePlayer initializedYouTubePlayer) {
                    initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                        @Override
                        public void onReady() {
                            initializedYouTubePlayer.loadVideo(id, 0);
                        }
                    });
                }
            }, true);

        } else {
            Toast.makeText(this, "El url es erroneo.", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isYouTube(String sharedText) {
        if (Patterns.WEB_URL.matcher(sharedText).matches() && sharedText.matches(".*(youtube|youtu.be).*")) {
            return true;
        } else {
            return false;
        }
    }

    private ProgressDialog pd;

    @SuppressLint("StaticFieldLeak")
    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(YouTubeUploadActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();
            }

            try {
                JSONObject jObject = new JSONObject(result);

                authorYT = jObject.getString("author_name");
                titleYT = jObject.getString("title");
                author_url = jObject.getString("author_url");
                image_url = jObject.getString("thumbnail_url");

                TextView credits = findViewById(R.id.credits);
                credits.setText(authorYT);
                TextInputEditText editTitle = findViewById(R.id.etTitle);
                editTitle.setText(titleYT);

                FloatingActionButton uploadVideo = findViewById(R.id.uploadVideo);
                uploadVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(YouTubeUploadActivity.this)
                                .setTitle("Upload")
                                .setMessage("Todo esta listo, deseas subir el post?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DatabaseReference mDatabase = FirebaseUtil.getPostsRef();
                                        String uid = FirebaseUtil.getCurrentUserId();
                                        String key = mDatabase.push().getKey();

                                        Map<String, Object> result = new HashMap<>();
                                        result.put("type", "_type_youtube");
                                        result.put("user_uid", uid);
                                        result.put("credits", authorYT);
                                        result.put("credits_url", author_url);
                                        result.put("name", titleYT);
                                        result.put("video", id);
                                        result.put("full_url", image_url.replace("hqdefault", "maxresdefault"));
                                        result.put("date", System.currentTimeMillis());

                                        mDatabase.child(key).setValue(result);
                                        FirebaseUtil.getCurrentUserRef().child("post").child(key).setValue(System.currentTimeMillis());
                                        Toast.makeText(YouTubeUploadActivity.this, "Publicado...", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                })
                                .setNegativeButton("No", null)
                                .show();


                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private String extractId(String url) {
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url); //url is youtube url for which you want to extract the id.
        if (matcher.find()) {
            return matcher.group();
        }

        return null;
    }

}
