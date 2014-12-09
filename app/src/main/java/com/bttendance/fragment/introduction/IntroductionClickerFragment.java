package com.bttendance.fragment.introduction;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bttendance.R;
import com.bttendance.activity.guide.GuideActivity;
import com.bttendance.fragment.BTFragment;
import com.bttendance.fragment.SimpleWebViewFragment;
import com.bttendance.model.BTUrl;

/**
 * Created by TheFinestArtist on 2014. 8. 13..
 */
public class IntroductionClickerFragment extends BTFragment {

    Button mSeeMore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_introduction_clicker, container, false);
        if (Build.VERSION.SDK_INT < 11)
            ((ImageView) view.findViewById(R.id.introduction_clicker)).setImageResource(R.drawable.clicker_bg);

        mSeeMore = (Button) view.findViewById(R.id.see_more);
        mSeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GuideActivity.class);
                intent.putExtra(SimpleWebViewFragment.EXTRA_URL, BTUrl.getTutorialClicker(getActivity()));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.modal_activity_open_enter, R.anim.modal_activity_open_exit);
            }
        });
        return view;
    }
}
