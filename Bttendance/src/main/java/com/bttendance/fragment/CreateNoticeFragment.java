package com.bttendance.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.bttendance.R;
import com.bttendance.helper.KeyboardHelper;
import com.bttendance.model.json.PostJson;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2014. 1. 27..
 */
public class CreateNoticeFragment extends BTFragment {

    private int mCourseID;
    private EditText mMessage;

    public CreateNoticeFragment(int courseID) {
        mCourseID = courseID;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        KeyboardHelper.hide(getActivity(), mMessage);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_notice, container, false);
        mMessage = (EditText) view.findViewById(R.id.message);
        KeyboardHelper.show(getActivity(), mMessage);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (getSherlockActivity() == null)
            return;

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setTitle(getString(R.string.create_notice));
        actionBar.setDisplayHomeAsUpEnabled(true);
        inflater.inflate(R.menu.create_notice_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.abs__home:
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_post:
                if (mMessage != null && mMessage.getText().toString().length() > 0)
                    getBTService().postCreateNotice(mCourseID, mMessage.getText().toString(), new Callback<PostJson>() {
                        @Override
                        public void success(PostJson postJson, Response response) {
                            if (CreateNoticeFragment.this.getActivity() != null)
                                CreateNoticeFragment.this.getActivity().onBackPressed();
                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            if (CreateNoticeFragment.this.getActivity() != null)
                                CreateNoticeFragment.this.getActivity().onBackPressed();
                        }
                    });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
