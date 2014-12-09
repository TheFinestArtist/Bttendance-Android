package com.bttendance.activity.guide;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.bttendance.R;
import com.bttendance.activity.BTActivity;
import com.bttendance.fragment.SimpleWebViewFragment;

/**
 * Created by TheFinestArtist on 2014. 08. 14..
 */
public class GuideActivity extends BTActivity {

    String mUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        mUrl = getIntent().getStringExtra(SimpleWebViewFragment.EXTRA_URL);

        SimpleWebViewFragment frag = new SimpleWebViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SimpleWebViewFragment.EXTRA_URL, mUrl);
        frag.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(
                R.anim.no_anim,
                R.anim.no_anim,
                R.anim.no_anim,
                R.anim.no_anim);
        ft.add(R.id.content, frag, ((Object) frag).getClass().getSimpleName());
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.modal_activity_close_enter, R.anim.modal_activity_close_exit);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
