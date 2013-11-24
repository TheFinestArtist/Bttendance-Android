package com.utopia.bttendance.activity.sign;

import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.utopia.bttendance.BTDebug;
import com.utopia.bttendance.R;
import com.utopia.bttendance.activity.BTActivity;
import com.utopia.bttendance.helper.UUIDHelper;
import com.utopia.bttendance.model.BTPreference;
import com.utopia.bttendance.model.json.ErrorJson;
import com.utopia.bttendance.model.json.UserJson;
import com.utopia.bttendance.view.BeautiToast;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SignUpActivity extends BTActivity {

    private EditText mFullName = null;
    private EditText mUsername = null;
    private EditText mEmail = null;
    private EditText mPassword = null;
    private EditText mPasswordHint = null;
    private View mFullNameDiv = null;
    private View mUsernameDiv = null;
    private View mEmailDiv = null;
    private View mPasswordDiv = null;
    private int mFullNameCount = 0;
    private int mUsernameCount = 0;
    private int mEmailCount = 0;
    private int mPasswordCount = 0;
    private Button mSignUp = null;
    private TextView mTermOfUse = null;
    private String mFullNameString = null;
    private String mUsernameString = null;
    private String mEmailString = null;
    private String mPasswordString = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFullName = (EditText) findViewById(R.id.full_name);
        mUsername = (EditText) findViewById(R.id.username);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mPasswordHint = (EditText) findViewById(R.id.password_hint);
        mFullNameDiv = findViewById(R.id.full_name_divider);
        mUsernameDiv = findViewById(R.id.username_divider);
        mEmailDiv = findViewById(R.id.email_divider);
        mPasswordDiv = findViewById(R.id.password_divider);

        mFullName.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mFullNameDiv.setBackgroundColor(getResources().getColor(
                            R.color.bttendance_blue_point));
                } else {
                    mFullNameDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
                }
            }
        });

        mFullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mFullNameCount = mFullName.getText().toString().length();
                isEnableSignUp();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mUsername.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mUsernameDiv.setBackgroundColor(getResources().getColor(
                            R.color.bttendance_blue_point));
                } else {
                    mUsernameDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
                }
            }
        });

        mUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUsernameCount = mUsername.getText().toString().length();
                isEnableSignUp();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mEmail.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mEmailDiv
                            .setBackgroundColor(getResources().getColor(R.color.bttendance_blue_point));
                } else {
                    mEmailDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
                }
            }
        });

        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEmailCount = mEmail.getText().toString().length();
                isEnableSignUp();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mPassword.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mPasswordDiv.setBackgroundColor(getResources().getColor(
                            R.color.bttendance_blue_point));
                } else {
                    mPasswordDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
                }
            }
        });

        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPasswordCount = mPassword.getText().toString().length();
                isEnableSignUp();
                if (mPasswordCount == 0) {
                    mPasswordHint.setVisibility(View.VISIBLE);
                } else {
                    mPasswordHint.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mSignUp = (Button) findViewById(R.id.signup);
        mSignUp.setEnabled(false);
        mSignUp.setTextColor(getResources().getColor(R.color.grey_hex_eb));
        mSignUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ((Button) v).setTextColor(getResources().getColor(R.color.bttendance_blue_main));
                    v.setPressed(true);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ((Button) v).setTextColor(getResources().getColor(R.color.bttendance_blue_point));
                    v.setPressed(false);
                    trySignUp();
                }
                if (event.getX() < 0
                        || event.getX() > v.getWidth()
                        || event.getY() < 0
                        || event.getY() > v.getHeight()) {
                    ((Button) v).setTextColor(getResources().getColor(R.color.bttendance_blue_point));
                    v.setPressed(false);
                }
                return true;
            }
        });


        SpannableStringBuilder builder = new SpannableStringBuilder();

        String string_format = getString(R.string.by_tapping_i_agree_to_the);
        SpannableString SpannableFormat = new SpannableString(string_format);
        builder.append(SpannableFormat);

        String term_and_condition = getString(R.string.terms_and_conditions);
        String term_and_condition_html = "<a href=\"http://m.vingle.net/about/terms\">"
                + term_and_condition + "</a>";
        SpannableString SpannableHTML = new SpannableString(Html.fromHtml(term_and_condition_html));
        SpannableHTML.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.bttendance_blue_main)), 0, term_and_condition.length(), 0);
        builder.append(SpannableHTML);

        SpannableString SpannableComma = new SpannableString(".");
        builder.append(SpannableComma);

        mTermOfUse = (TextView) findViewById(R.id.term_of_use);
        mTermOfUse.setText(builder, TextView.BufferType.SPANNABLE);
        mTermOfUse.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void isEnableSignUp() {
        if (mFullNameCount > 0 && mUsernameCount > 0 && mEmailCount > 0 && mPasswordCount > 5) {
            mSignUp.setEnabled(true);
            mSignUp.setTextColor(getResources().getColor(R.color.bttendance_blue_point));
        } else {
            mSignUp.setEnabled(false);
            mSignUp.setTextColor(getResources().getColor(R.color.grey_hex_eb));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mFullNameString != null)
            mFullName.setText(mFullNameString);
        if (mUsernameString != null)
            mUsername.setText(mUsernameString);
        if (mEmailString != null)
            mEmail.setText(mEmailString);
        if (mPasswordString != null)
            mPassword.setText(mPasswordString);

        mFullNameDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
        mUsernameDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
        mEmailDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
        mPasswordDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
    }

    @Override
    public void onPause() {
        super.onPause();

        mFullNameString = mFullName.getText().toString();
        mUsernameString = mUsername.getText().toString();
        mEmailString = mEmail.getText().toString();
        mPasswordString = mPassword.getText().toString();
    }

    private void trySignUp() {
        String fullName = mFullName.getText().toString();
        String username = mUsername.getText().toString();
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        UserJson user = new UserJson();
        user.username = username;
        user.full_name = fullName;
        user.email = email;
        user.password = password;
        user.device_type = "Android";
        user.device_uuid = UUIDHelper.getUUID(this);

        getBTService().signup(user, new Callback<UserJson>() {
            @Override
            public void success(UserJson user, Response response) {
                BTDebug.LogInfo(user.toJson());
                BTPreference.setUser(SignUpActivity.this, user);
                startActivity(getNextIntent());
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                try {
                    String error = retrofitError.getBodyAs(ErrorJson.class).toString();
                    BeautiToast.show(getApplicationContext(), error);
                    BTDebug.LogError(error);
                } catch (Exception e) {
                    BTDebug.LogError(e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return (super.onOptionsItemSelected(item));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_right);
    }

}// end of class
