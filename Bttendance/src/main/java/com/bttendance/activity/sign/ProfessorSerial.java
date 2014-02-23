package com.bttendance.activity.sign;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.bttendance.R;
import com.bttendance.activity.BTActivity;
import com.bttendance.model.BTKey;
import com.bttendance.model.json.SchoolJson;
import com.bttendance.view.BeautiToast;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 11. 27..
 */
public class ProfessorSerial extends BTActivity {


    private EditText mSerial = null;
    private View mSerialDiv = null;
    private Button mEnter = null;
    private TextView mReqSerial = null;
    private int mSerialCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_serial);

        mSerial = (EditText) findViewById(R.id.serial);
        mSerialDiv = findViewById(R.id.serial_divider);

        mSerial.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mSerialDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_cyan));
                } else {
                    mSerialDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
                }
            }
        });

        mSerial.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSerialCount = mSerial.getText().toString().length();
                isEnableSignIn();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mEnter = (Button) findViewById(R.id.enter);
        mEnter.setEnabled(false);
        mEnter.setTextColor(getResources().getColor(R.color.grey_hex_cc));
        mEnter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ((Button) v).setTextColor(getResources().getColor(R.color.bttendance_navy));
                    v.setPressed(true);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ((Button) v).setTextColor(getResources().getColor(R.color.bttendance_cyan));
                    v.setPressed(false);
                    serialValidate();
                }
                if (event.getX() < 0
                        || event.getX() > v.getWidth()
                        || event.getY() < 0
                        || event.getY() > v.getHeight()) {
                    ((Button) v).setTextColor(getResources().getColor(R.color.bttendance_cyan));
                    v.setPressed(false);
                }
                return true;
            }
        });

//        SpannableStringBuilder builder = new SpannableStringBuilder();
//        String forgot_password = getString(R.string.request_serial);
//        String forgot_password_html = "<a href=\"" + BTUrl.REQUEST_SERIAL + "\">"
//                + forgot_password + "</a>";
//        SpannableString SpannableHTML = new SpannableString(Html.fromHtml(forgot_password_html));
//        SpannableHTML.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.bttendance_navy)), 0, forgot_password.length(), 0);
//        builder.append(SpannableHTML);

//        mReqSerial = (TextView) findViewById(R.id.req_serial);
//        mReqSerial.setText(builder, TextView.BufferType.SPANNABLE);
//        mReqSerial.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void isEnableSignIn() {

        if (mSerialCount > 1) {
            mEnter.setEnabled(true);
            mEnter.setTextColor(getResources().getColor(R.color.bttendance_cyan));
        } else {
            mEnter.setEnabled(false);
            mEnter.setTextColor(getResources().getColor(R.color.grey_hex_cc));
        }
    }

    protected void serialValidate() {
        if (getBTService() == null)
            return;

        String serial = mSerial.getText().toString();
        getBTService().serialValidate(serial, new Callback<SchoolJson>() {
            @Override
            public void success(SchoolJson school, Response response) {
                if (school != null) {
                    BeautiToast.show(getApplicationContext(), getString(R.string.validated));
//                    Intent intent = new Intent(ProfessorSerial.this, SignUpActivity.class);
//                    intent.putExtra(BTKey.EXTRA_TYPE, BTKey.Type.PROFESSOR);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
                } else
                    BeautiToast.show(getApplicationContext(), getString(R.string.wrong_serial));
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mSerialDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
        isEnableSignIn();
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
}
