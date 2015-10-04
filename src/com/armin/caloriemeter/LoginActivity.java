package com.armin.caloriemeter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	private static String[] DUMMY_CREDENTIALS = new String[] {
			"foo@example.com:hello", "bar@example.com:world" };

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;
	private UserRegisterTask mRegTask = null;

	private boolean login = true;
	
	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;
	private String mPasswordReapet;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private EditText mPasswordRepeatView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private TextView registerSignInLink;
	private Button registerButton;
	private Button signInButton;
	private TextView forgotPasswordLink;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		mPasswordRepeatView = (EditText) findViewById(R.id.repeat_password);
		registerButton = (Button) findViewById(R.id.register_button);
		signInButton = (Button) findViewById(R.id.sign_in_button);
		forgotPasswordLink = (TextView) findViewById(R.id.forgot_password_link);
		registerSignInLink = (TextView) findViewById(R.id.register_sign_in_link);
		
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mPasswordRepeatView.setVisibility(View.INVISIBLE);
		
		registerButton.setVisibility(View.GONE);
		
		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		signInButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		
		registerSignInLink.setOnClickListener(
				new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(login)
						{
							mPasswordRepeatView.setVisibility(View.VISIBLE);
							signInButton.setVisibility(View.GONE);
							registerSignInLink.setText(getString(R.string.action_sign_in_link));
							registerButton.setVisibility(View.VISIBLE);
							mPasswordView.setImeActionLabel(getString(R.string.action_register), 0);
						}
						else
						{
							mPasswordRepeatView.setVisibility(View.INVISIBLE);
							signInButton.setVisibility(View.VISIBLE);
							registerSignInLink.setText(getString(R.string.action_sign_up_link));
							registerButton.setVisibility(View.GONE);
							mPasswordView.setImeActionLabel(getString(R.string.action_sign_in), 0);
						}
						registerSignInLink.setPaintFlags(registerSignInLink.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
						login = !login;
					}
				});
		
		registerButton.setOnClickListener(
				new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						attemptRegister();
					}
				});
		
		forgotPasswordLink.setOnClickListener(
				new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//TODO forgot password
					}
				});

	}

	protected void attemptRegister() {
		
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the register attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		mPasswordReapet = mPasswordRepeatView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		} else if (!mPassword.equals(mPasswordReapet)) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordRepeatView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@") || !mEmail.contains(".")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user register attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_register);
			showProgress(true);
			mRegTask = new UserRegisterTask();
			mRegTask.execute((Void) null);
		}

		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@") || !mEmail.contains(".")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				return false;
			}

			for (String credential : DUMMY_CREDENTIALS) {
				String[] pieces = credential.split(":");
				if (pieces[0].equals(mEmail)) {
					// Account exists, return true if the password matches.
					return pieces[1].equals(mPassword);
				}
			}

			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				getSharedPreferences(Constants.USER_SHARED_PREFERENCES, 0).edit().clear().commit();
				User.setEmail(mEmail);
				startActivity(new Intent(LoginActivity.this, HistoryActivity.class));
			} else {
				mEmailView
						.setError(getString(R.string.error_invalid_username_or_password));
				mEmailView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
	
	/**
	 * Represents an asynchronous registration task used to authenticate
	 * the user.
	 */
	public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {
	
		View focusView = null;
		@Override
		protected Boolean doInBackground(Void... params) {
			
			// TODO: register in server

			try {
				// Simulate network access.
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				return false;
			}

			for (String credential : DUMMY_CREDENTIALS) {
				String[] pieces = credential.split(":");
				if (pieces[0].equals(mEmail)) {
					return false;
				}
			}
			//TODO: Register
			
			String[] tmp = new String[DUMMY_CREDENTIALS.length+1];
			for(int i = 0; i < tmp.length-1; i++)
				tmp[i] = DUMMY_CREDENTIALS[i];
			tmp[tmp.length - 1] = mEmail+":"+mPassword;
			
			DUMMY_CREDENTIALS = new String[tmp.length];
			for(int i = 0; i < tmp.length; i++)
				DUMMY_CREDENTIALS[i] = tmp[i];
			
			
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mRegTask = null;
			showProgress(false);

			if (success) {
				getSharedPreferences(Constants.USER_SHARED_PREFERENCES, 0).edit().clear().commit();
				User.setEmail(mEmail);
				startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
			} else {
				mEmailView.setError(getString(R.string.error_email_exists));
				mEmailView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mRegTask = null;
			showProgress(false);
		}
	}
}
//TODO code for RTL layout 
//LinearLayout ll = // inflate
//ArrayList<View> views = new ArrayList<View>();
//for(int x = 0; x < ll.getChildCount(); x++) {
//    views.add(ll.getChildAt(x));
//}
//ll.removeAllViews();
//for(int x = views.size() - 1; x >= 0; x--) {
//    ll.addView(views.get(x));
//}