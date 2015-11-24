package pl.xdcodes.stramek.awesomenotes.parse;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import pl.xdcodes.stramek.awesomenotes.Config;
import pl.xdcodes.stramek.awesomenotes.R;

public class ParseDialog extends DialogFragment {

    private String login;
    private String password;

    private ProgressBar progressBar;

    private EditText loginET;
    private EditText passwordET;
    private TextInputLayout loginTIL;
    private TextInputLayout passwordTIL;

    private AlertDialog dialog;

    public enum status { LOG_IN_SUCCESS, CREATE_ACCOUNT_SUCCESS, NO_INTERNET_CONNECTION }

    public interface StatusDialogListener {
        void onFinishDialog(status message);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View parseView = View.inflate(getContext(), R.layout.parse_new_account, null);

        loginET = (EditText) parseView.findViewById(R.id.login);
        passwordET = (EditText) parseView.findViewById(R.id.password);
        progressBar = (ProgressBar) parseView.findViewById(R.id.progressBar);
        loginTIL = (TextInputLayout) parseView.findViewById(R.id.login_input_layout);
        passwordTIL = (TextInputLayout) parseView.findViewById(R.id.password_input_layout);

        SharedPreferences prefs = getContext().getSharedPreferences("parse", Context.MODE_PRIVATE);
        login = prefs.getString("lastLogin", "");
        password = prefs.getString("lastPassword", "");

        loginET.setText(login);
        passwordET.setText(password);

        builder.setTitle(getString(R.string.parse_dialog_title));
        builder.setView(parseView);
        builder.setPositiveButton(getString(R.string.parse_log_in), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        }).setNegativeButton(getString(R.string.parse_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        }).setNeutralButton(getString(R.string.parse_create), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog = builder.create();
        if(login.equals("")) {
            loginET.requestFocus();
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editTextsNotEmpty(loginET.getText().toString(), passwordET.getText().toString())) {
                    if(isOnline()) {
                        showProgressBar();

                        ParseUser.logInInBackground(loginET.getText().toString(), passwordET.getText().toString(), new LogInCallback() {
                            public void done(ParseUser user, ParseException e) {
                                if (user != null) {
                                    saveLoginAndPassword();
                                    ParseUser.logOut();
                                    returnDialogValue(status.LOG_IN_SUCCESS);
                                } else {
                                    hideProgressBar();
                                    loginTIL.setError(getString(R.string.parse_wrong_login_or_password));
                                    passwordTIL.setError(getString(R.string.parse_wrong_login_or_password));
                                    loginET.requestFocus();
                                }
                            }
                        });
                    } else {
                        returnDialogValue(status.NO_INTERNET_CONNECTION);
                    }
                }
            }
        });

        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if(editTextsNotEmpty(loginET.getText().toString(), passwordET.getText().toString())) {
                    if(isOnline()) {

                        showProgressBar();

                        ParseUser user = new ParseUser();

                        user.setUsername(loginET.getText().toString());
                        user.setPassword(passwordET.getText().toString());

                        user.signUpInBackground(new SignUpCallback() {
                            public void done(ParseException e) {
                                if (e == null) {
                                    saveLoginAndPassword();
                                    returnDialogValue(status.CREATE_ACCOUNT_SUCCESS);
                                } else {
                                    hideProgressBar();
                                    loginTIL.setError(getString(R.string.parse_login_taken));
                                    loginET.requestFocus();
                                }
                            }
                        });
                    } else {
                        returnDialogValue(status.NO_INTERNET_CONNECTION);
                    }
                }
            }
        });

        return dialog;
    }

    private void saveLoginAndPassword() {
        SharedPreferences.Editor editor = getContext().getSharedPreferences("parse", Context.MODE_PRIVATE).edit();
        editor.putString("lastLogin", loginET.getText().toString());
        editor.putString("lastPassword", passwordET.getText().toString());
        editor.commit();
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) Config.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void returnDialogValue(status s) {
        StatusDialogListener activity = (StatusDialogListener) getActivity();
        activity.onFinishDialog(s);
        dialog.dismiss();
    }

    private boolean editTextsNotEmpty(String login, String password) {

        if(login.length() < 5) {
            loginTIL.setError("Login musi mieć conajmniej 5 znaków");
            loginET.requestFocus();
            return false;
        } else {
            loginTIL.setErrorEnabled(false);
        }
        if(password.length() < 5) {
            passwordTIL.setError("Hasło musi mieć conajmniej 5 znaków");
            passwordET.requestFocus();
            return false;
        } else {
            passwordTIL.setErrorEnabled(false);
        }
        return true;
    }

    private void showProgressBar() {
        loginET.setVisibility(View.INVISIBLE);
        passwordET.setVisibility(View.INVISIBLE);
        loginTIL.setVisibility(View.INVISIBLE);
        passwordTIL.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        loginET.setVisibility(View.VISIBLE);
        passwordET.setVisibility(View.VISIBLE);
        loginTIL.setVisibility(View.VISIBLE);
        passwordTIL.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }
}
