package com.bttendance.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bttendance.R;
import com.bttendance.adapter.FeedAdapter;
import com.bttendance.event.attendance.AttdCheckedEvent;
import com.bttendance.event.attendance.AttdStartedEvent;
import com.bttendance.event.refresh.RefreshFeedEvent;
import com.bttendance.event.update.UpdateFeedEvent;
import com.bttendance.helper.DipPixelHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.cursor.PostCursor;
import com.bttendance.model.json.PostJson;
import com.squareup.otto.BTEventBus;
import com.squareup.otto.Subscribe;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class FeedFragment extends BTFragment {

    ListView mListView;
    FeedAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);
        View padding = new View(getActivity());
        padding.setMinimumHeight((int) DipPixelHelper.getPixel(getActivity(), 7));
        mListView.addHeaderView(padding);
        mListView.addFooterView(padding);
        mAdapter = new FeedAdapter(getActivity(), null);
        mListView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        getFeed();
    }

    public void getFeed() {
        if (getBTService() == null)
            return;

        getBTService().feed(0, new Callback<PostJson[]>() {
            @Override
            public void success(PostJson[] posts, Response response) {
                swapCursor();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
            }
        });
    }

    @Subscribe
    public void onAttdStarted(AttdStartedEvent event) {
        swapCursor();
    }

    @Subscribe
    public void onAttdChecked(AttdCheckedEvent event) {
        swapCursor();
    }

    @Subscribe
    public void onRefresh(RefreshFeedEvent event) {
        getFeed();
    }

    @Subscribe
    public void onUpdate(UpdateFeedEvent event) {
        swapCursor();
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        swapCursor();
    }

    private void swapCursor() {
        if (this.isAdded() && mAdapter != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.swapCursor(new PostCursor(BTTable.getPostsOfMyCourse(BTPreference.getUser(getActivity()))));
                }
            });
        }
    }
}
