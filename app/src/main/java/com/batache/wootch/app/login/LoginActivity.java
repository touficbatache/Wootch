package com.batache.wootch.app.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.batache.wootch.R;
import com.batache.wootch.app.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

  private FirebaseAuth auth;
  private GoogleSignInClient googleSignInClient;
  private static final int RC_SIGN_IN = 1;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    auth = FirebaseAuth.getInstance();

    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(getString(R.string.default_web_client_id))
        .requestEmail()
        .build();
    googleSignInClient = GoogleSignIn.getClient(this, gso);

    findViewById(R.id.sign_in_button).setOnClickListener(view -> signIn());
  }

  @Override
  protected void onStart() {
    super.onStart();

    updateUI(auth.getCurrentUser());
  }

  private void signIn() {
    Intent signInIntent = googleSignInClient.getSignInIntent();
    startActivityForResult(signInIntent, RC_SIGN_IN);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == RC_SIGN_IN && data != null) {
      if (resultCode == RESULT_OK) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
          GoogleSignInAccount account = task.getResult(ApiException.class);
          firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
          // Google Sign In failed, update UI appropriately
          Log.w("LoginAct: ", "Google sign in failed", e);
          // ...
        }
      }
    }
  }

  private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
    AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
    auth.signInWithCredential(credential)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
              FirebaseUser user = auth.getCurrentUser();
              updateUI(user);
            } else {
//              Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
              updateUI(null);
            }
          }
        });
  }

  private void updateUI(FirebaseUser user) {
//    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
    if (user != null) {
      user.getDisplayName();

      startActivity(new Intent(this, MainActivity.class));
      finish();
    }
  }
}
