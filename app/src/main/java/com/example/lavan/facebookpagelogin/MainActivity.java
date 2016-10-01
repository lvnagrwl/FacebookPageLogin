package com.example.lavan.facebookpagelogin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.io.InputStream;
import java.util.jar.JarInputStream;

public class MainActivity extends AppCompatActivity {

    private CallbackManager callbackManager; //manages the callback into the facebookSDK from activity's on ActivityResult
    private TextView textView , textView1;
    private AccessTokenTracker accessTokenTracker; //helps in providing temporary access to facebook APIs
    private ProfileTracker profileTracker; // receives notification on profile changes
    public ImageView image;

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();// getter foe new access token
            Profile profile =  Profile.getCurrentProfile();
            Toast.makeText(getApplicationContext(),"Loggin .. !!",Toast.LENGTH_SHORT).show();
            displayMessage(profile);

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    };

    public MainActivity(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplication().getApplicationContext());


        callbackManager = CallbackManager.Factory.create();// handles login responses

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

                textView.setVisibility(View.GONE);
                textView1.setVisibility(View.GONE);
                image.setVisibility(View.GONE);

                //write here what you wish to do with Logout button of Facebook

            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                displayMessage(currentProfile);
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
        textView = (TextView)findViewById(R.id.textview);
        textView1 = (TextView)findViewById(R.id.textview1);
        image = (ImageView) findViewById(R.id.imageview);


        loginButton.setReadPermissions("user_friends");
        loginButton.registerCallback(callbackManager , callback);





    }

    @Override
    protected void onActivityResult(int requestCode , int responseCode , Intent intent){
        super.onActivityResult(requestCode , responseCode , intent);
        callbackManager.onActivityResult(requestCode, responseCode,intent);
    }


    private void displayMessage(Profile profile){
        if(profile!=null) {
            textView.setText(profile.getFirstName());
            textView.setVisibility(View.VISIBLE);
            textView1.setText(profile.getLastName());
            textView1.setVisibility(View.VISIBLE);
            ImageDownload im = new ImageDownload(image);
            String profileUrl = profile.getProfilePictureUri(300,300).toString();
            if(profileUrl!=null) {
                im.execute(profileUrl);
                image.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public  void onStop(){
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    public  void onResume(){
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        displayMessage(profile);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class ImageDownload extends AsyncTask<String , Void , Bitmap>{

        String display;
        Bitmap mIcon;
        ImageView bmImage;
        public ImageDownload(ImageView bmImage){
            this.bmImage = bmImage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
           display = urls[0];
             mIcon = null;

            try {
                InputStream in = new java.net.URL(display).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            }catch(Exception e){

                Log.e("Error" , e.getMessage());
                e.printStackTrace();

            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result){
            bmImage.setImageBitmap(result);

        }

    }

}
