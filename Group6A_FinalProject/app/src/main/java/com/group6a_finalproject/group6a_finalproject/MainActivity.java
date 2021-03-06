package com.group6a_finalproject.group6a_finalproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
//import com.mopub.common.HttpClient;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;
import com.parse.twitter.Twitter;
import com.parse.SendCallback;
import com.parse.SignUpCallback;
//import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    final String fGOTO_REGISTER_NEW_USER = "android.intent.action.REGISTER_NEW_USER";
    final String fGOTO_MAIN_PROFILE = "android.intent.action.MAIN_PROFILE";
    final String fCONSUMER_KEY = "m0tzHoXVBegpvbzY23yE89SFY";
    final String fSECRET = "uwAXIjcn6oTpCRXJKvG5mMkBvPop2ABvUtj98bMMCOFuM7hOeY";
    EditText fEmail, fPassword;
    LoginButton fFacebookLogin;
//    TwitterLoginButton fTwitterButton;
    CallbackManager fCallBack;
    Bitmap fBitmap = null;

    ArrayList<Integer> privacy_settings;
    public static final CharSequence[] TWITTER_PRIVACY_SETTINGS = {"MALE", "Listed User", "Receive Push Notifications"};
    public static final String TWITTER_API_CALL_USER = "https://api.twitter.com/1.1/users/show.json?user_id=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getItems();

        ParseUser lCheckUser = ParseUser.getCurrentUser();
        if(lCheckUser!=null){
            toActivity(fGOTO_MAIN_PROFILE);
            finish();
        }
        //region FacebookLogin
        //sets permissions to let FB know the info I want
        fFacebookLogin.setReadPermissions("email");
        fFacebookLogin.registerCallback(fCallBack, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                //sending request to get information for current user
                GraphRequest lRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        try {
                            //login and register new facebook user
                            ParseQuery<ParseUser> lQuery = new ParseQuery<ParseUser>("_User");
                            lQuery.whereEqualTo("username", jsonObject.getString("email"));
                            //checking if facebook user is already in Parse db
                            List<ParseUser> lList = lQuery.find();

                            if (lList.size() == 0){
                                getFBUserIcon(new URL("https://graph.facebook.com/" + jsonObject.getString("id") + "/picture?type=large"));
                                createFBUser(jsonObject);
                            }
                            else
                                loginFBUser(lList.get(0).getUsername(), "pass");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "email, gender, name");
                lRequest.setParameters(parameters);
                lRequest.executeAsync();
                makeToast("Login success.");
            }

            @Override
            public void onCancel() {
                makeToast("Login attempt cancelled.");
            }

            @Override
            public void onError(FacebookException e) {
                e.printStackTrace();
                makeToast("Login attempt failed.");
            }
        });
        //endregion

       /* fTwitterButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.d("Twitter", result.data.toString());
                ParseTwitterUtils.logIn(MainActivity.this, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user == null) {
                            return;
                        } else if (user.isNew()) {

                        } else {

                        }
                    }
                });
            }

            @Override
            public void failure(TwitterException e) {
                Log.d("Twitter", e.toString());
                makeToast("Twitter login failed.");
            }
        });*/

    }

    public void loginFBUser(String aUser, String aPW) {
        ParseUser.logInInBackground(aUser, aPW, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    toActivity(fGOTO_MAIN_PROFILE);
                    finish();
                }
            }
        });
    }

    public void getFBUserIcon (final URL aURL) throws IOException {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection lURLCon = (HttpURLConnection) aURL.openConnection();
                    InputStream inputStream = new BufferedInputStream(lURLCon.getInputStream());
                    fBitmap = BitmapFactory.decodeStream(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void createFBUser(final JSONObject aFBUser) throws JSONException, IOException {
        final ParseUser lUser = new ParseUser();
        lUser.setEmail(aFBUser.getString("email"));
        lUser.setPassword("pass");
        lUser.setUsername(aFBUser.getString("email"));
        lUser.put("name", aFBUser.getString("name"));
        lUser.put("gender", aFBUser.getString("gender"));
        lUser.put("getNotification", true);
        lUser.put("isVisible", true);

        if(fBitmap != null){
            ByteArrayOutputStream lStream = new ByteArrayOutputStream();
            fBitmap.compress(Bitmap.CompressFormat.PNG, 100, lStream);
            byte[] lImageToUpload = lStream.toByteArray();

            final ParseFile lImageFile = new ParseFile("default.png", lImageToUpload);
            lImageFile.saveInBackground();
            lUser.put("thumbnail", lImageFile);
        }

        lUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    sendNotiication(lUser);

                    setupInstallation(lUser);
                    finish();
                } else {
                    fEmail.setError("Email already exists");
                }
            }
        });
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
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void getItems (){
        fEmail = (EditText) findViewById(R.id.editTextLoginEmail);
        fPassword = (EditText) findViewById(R.id.editTextLoginPassword);

        fFacebookLogin = (LoginButton) findViewById(R.id.facebookLogin_button);

        FacebookSdk.sdkInitialize(getApplicationContext());
        fCallBack = CallbackManager.Factory.create();

        ParseTwitterUtils.initialize(fCONSUMER_KEY, fSECRET);
        TwitterAuthConfig twitAuth = new TwitterAuthConfig(fCONSUMER_KEY, fSECRET);

        Fabric.with(this, new TwitterCore(twitAuth));

//        fTwitterButton = (TwitterLoginButton) findViewById(R.id.twitterLogin_button);
    }

    public void facebookLoginOnClick (View aView){

    }

    public void twitterLoginOnClick(View aView){
        ParseTwitterUtils.logIn(this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Twitter login.");
                } else if (user.isNew()) {
                    new TwitterAuthentication().execute(TWITTER_API_CALL_USER + ParseTwitterUtils.getTwitter());
                } else {
                    setupInstallation(user);

                    toActivity(fGOTO_MAIN_PROFILE);
                    finish();
                }
            }
        });
    }

    public void toActivity(String aIntent){
        Intent lIntent = new Intent(aIntent);
        startActivity(lIntent);

    }

    public void makeToast(String aString){
        Toast.makeText(getApplicationContext(), aString, Toast.LENGTH_SHORT).show();
    }

    public void loginOnClick (View aView){
        String lEmail, lPassWord;
        lEmail = fEmail.getText().toString();
        lPassWord = fPassword.getText().toString();

        if(lEmail.isEmpty() || lPassWord.isEmpty()){
            if(lEmail.isEmpty()){
                fEmail.setError("Email cannot be empty!");
            }

            if(lPassWord.isEmpty()){
                fPassword.setError("Password cannot be empty!");
            }
            return;
        }else{
            ParseUser.logInInBackground(lEmail, lPassWord, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        toActivity(fGOTO_MAIN_PROFILE);

                        setupInstallation(user);

                        finish();
                    } else
                        makeToast("Invalid credentials");
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fCallBack.onActivityResult(requestCode, resultCode, data);
//        fTwitterButton.onActivityResult(requestCode,  resultCode,  data);

    }

    public void registerOnClick (View aView){
        toActivity(fGOTO_REGISTER_NEW_USER);
    }

    public void setupInstallation(ParseUser user){
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("user", user.getObjectId());

        installation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null)
                    e.printStackTrace();
            }
        });
    }

    private void sendNotiication(final ParseUser aUser){

        ParseQuery<ParseUser> user = ParseQuery.getQuery("_User");
        user.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        user.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    for (ParseUser object : objects) {
                        if (object.getBoolean("getNotification")) {
                            Map<String, String> lNotifyUsers = new HashMap<>();
                            lNotifyUsers.put("toUser", object.getObjectId());
                            lNotifyUsers.put("fromUser", ParseUser.getCurrentUser().getString("name"));
                            lNotifyUsers.put("type", "New User");
                            lNotifyUsers.put("message", " has newly signed up to the system");

                            ParseCloud.callFunctionInBackground("notifyPush", lNotifyUsers, new FunctionCallback<Object>() {
                                @Override
                                public void done(Object object, ParseException e) {

                                    if (e == null) {

                                    } else {
                                        Log.d("see", "error");
                                        e.printStackTrace();

                                    }
                                }
                            });
                        }
                    }

                    createInstallation(aUser);
                } else e.printStackTrace();
            }
        });


    }

    private void createInstallation(final ParseUser aUser){
        ParseInstallation lNewUser = ParseInstallation.getCurrentInstallation();
        lNewUser.put("user", ParseUser.getCurrentUser().getObjectId());
        lNewUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    makeToast("Registration Successful");
                    loginFBUser(aUser.getUsername(), "pass");
                } else e.printStackTrace();
            }
        });
    }

    private class TwitterAuthentication extends AsyncTask<String, Void, ParseUser>{
        @Override
        protected ParseUser doInBackground(String... params) {
            HttpUriRequest request = new HttpGet(params[0]);
            Twitter twitter1 = ParseTwitterUtils.getTwitter();
            twitter1.signRequest(request);
            HttpClient client = new DefaultHttpClient();

            //                HttpResponse response = client.execute(request);
//                BufferedReader input = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
//
//                String result = input.readLine();
//                JSONObject JsonResponse = new JSONObject(result);
//
//                Log.e("Response",JsonResponse.toString());

            ParseUser user = ParseUser.getCurrentUser();

//                user.setUsername(JsonResponse.getString("name"));
//                String[] array = JsonResponse.getString("name").split(" ");
//                user.put("name", array[0]);
//                user.put("image", JsonResponse.getString("profile_image_url"));
            user.put("name",twitter1.getScreenName());
            return user;

//            return null;
        }

        @Override
        protected void onPostExecute(ParseUser parseUser) {
            if (parseUser == null){
                makeToast("EmptyUser");
            }
            displayUserSettingsForTwitterLogin(parseUser);
        }
    }

    public void displayUserSettingsForTwitterLogin(final ParseUser user) {
//        privacy_settings = new ArrayList<>();
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setTitle("User Profile and Settings")
//                .setCancelable(false)
//                .setMultiChoiceItems(TWITTER_PRIVACY_SETTINGS, null, new DialogInterface.OnMultiChoiceClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//                        if (isChecked) {
//                            privacy_settings.add(which);
//                        } else {
//                            privacy_settings.remove(which);
//                        }
//                    }
//                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                populatePrivacySettingsForTwitter(user);
//            }
//        });
//        builder.create().show();

        populatePrivacySettingsForTwitter(user);
    }

    public void populatePrivacySettingsForTwitter(final ParseUser user) {

        user.put("gender", "Male");
        user.put("isVisible", true);
        user.put("getNotification", true);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                    user.setEmail(user.getObjectId()+"@xyz.com");
                    user.saveInBackground();

                    sendNotiication(user);
                    setupInstallation(user);
                    toActivity(fGOTO_MAIN_PROFILE);
                    finish();
                } else {
                    e.printStackTrace();
                }
            }
        });

    }
}
