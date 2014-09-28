package com.bttendance.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bttendance.BTDebug;
import com.bttendance.R;
import com.bttendance.activity.course.AddCourseActivity;
import com.bttendance.activity.guide.GuideActivity;
import com.bttendance.adapter.SideListAdapter;
import com.bttendance.event.attendance.AttdStartedEvent;
import com.bttendance.fragment.BTFragment;
import com.bttendance.fragment.course.CourseDetailFragment;
import com.bttendance.fragment.course.NoCourseFragment;
import com.bttendance.fragment.profile.ProfileFragment;
import com.bttendance.fragment.setting.SettingFragment;
import com.bttendance.helper.ScreenHelper;
import com.bttendance.model.BTKey;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.UserJson;
import com.bttendance.view.BeautiToast;
import com.squareup.otto.BTEventBus;
import com.uservoice.uservoicesdk.Config;
import com.uservoice.uservoicesdk.UserVoice;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 11. 20..
 */
public class MainActivity extends BTActivity implements AdapterView.OnItemClickListener {

    private static Handler mUIHandler = new Handler();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private SideListAdapter mSideAdapter;
    private ListView mListMenu;

    private boolean mFinishApplication = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStack.clear(this);
        setContentView(R.layout.activity_main);

        mSideAdapter = new SideListAdapter(getApplicationContext());
        mSideAdapter.setNotifyOnChange(true);
        mListMenu = (ListView) findViewById(R.id.left_drawer);
        mListMenu.setAdapter(mSideAdapter);
        mListMenu.setOnItemClickListener(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_navigation_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
                replacePendingFragment();
                if (getBTService() != null)
                    getBTService().socketConnect();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                refreshSideMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        ViewGroup.LayoutParams params = mListMenu.getLayoutParams();
        params.width = ScreenHelper.getNaviDrawerWidth(this);
        mListMenu.setLayoutParams(params);

        BTEventBus.getInstance().register(mEventDispatcher);

        setFirstFragment();
    }

    private void setFirstFragment() {
        BTFragment fragment;
        int lastCourse = BTPreference.getLastSeenCourse(this);
        if (lastCourse == 0)
            fragment = new NoCourseFragment();
        else {
            fragment = new CourseDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(BTKey.EXTRA_COURSE_ID, lastCourse);
            fragment.setArguments(bundle);
        }

        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() == 0) {
            fm.beginTransaction().replace(R.id.content, fragment).commit();
            fragment.onPendingFragmentResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BTEventBus.getInstance().unregister(mEventDispatcher);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (BTTable.getAttdCheckingIds().size() > 0)
            BTEventBus.getInstance().post(new AttdStartedEvent(true));

        if (!BTPreference.seenGuide(this)) {
            Intent intent = new Intent(this, GuideActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @Override
    protected void onServieConnected() {
        super.onServieConnected();

        //Check whether on going Attendance exists
        getBTService().autoSignin(new Callback<UserJson>() {
            @Override
            public void success(UserJson userJson, Response response) {
            }

            @Override
            public void failure(RetrofitError retrofitError) {
            }
        });

        BTDebug.LogError("ACTION_SHOW_COURSE #1");
        Intent intent = getIntent();
        if (intent == null)
            return;

        BTDebug.LogError("ACTION_SHOW_COURSE #2 : " + intent.toString());
        String action = intent.getAction();
        if (action == null)
            return;

        BTDebug.LogError("ACTION_SHOW_COURSE #3");
        if (action.equals(BTKey.IntentKey.ACTION_SHOW_COURSE)) {
            BTDebug.LogError("ACTION_SHOW_COURSE #4");
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        FragmentManager fm = getSupportFragmentManager();
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(mListMenu))
                    mDrawerLayout.closeDrawer(mListMenu);
                else if (fm.getBackStackEntryCount() > 0)
                    super.onBackPressed();
                else
                    mDrawerLayout.openDrawer(mListMenu);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * ************************
     * Option Menu
     * *************************
     */

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        if (isDrawerOpen()) {
            actionBar.setTitle(getString(R.string.menu));
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.no_menu, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout.isDrawerOpen(mListMenu);
    }

    /**
     * ************************
     * Back Press
     * *************************
     */
    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (mDrawerLayout.isDrawerOpen(mListMenu))
            mDrawerLayout.closeDrawer(mListMenu);
        else if (fm.getBackStackEntryCount() > 0) {
            super.onBackPressed();
        } else
            tryToFinish();
    }

    private void tryToFinish() {
        if (mFinishApplication) {
            finish();
        } else {
            BeautiToast.show(this, getString(R.string.please_press_back_button_again_to_exit_));
            mFinishApplication = true;
            mUIHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mFinishApplication = false;
                }
            }, 3000);
        }
    }

    /**
     * ************************
     * onItemClick
     * *************************
     */

    private BTFragment pendingFragment;

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        SideListAdapter.SideItem item = mSideAdapter.getItem(position);

        BTFragment fragment = null;

        switch (item.getType()) {
            case Header:
                fragment = new ProfileFragment();
                break;
            case AddCourse: {
                Intent intent = new Intent(this, AddCourseActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            }
            case Guide:
                Intent intent = new Intent(this, GuideActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case Setting:
                fragment = new SettingFragment();
                break;
            case Feedback:
                UserJson user = BTPreference.getUser(this);
                Config config = new Config("bttendance.uservoice.com");
                config.setForumId(259759);
                config.identifyUser(user.email, user.full_name, user.email);
                UserVoice.init(config, this);
                UserVoice.launchPostIdea(this);
                overridePendingTransition(R.anim.slide_in_up, R.anim.fade_out_slow);
                break;
            case Course:
                fragment = new CourseDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(BTKey.EXTRA_COURSE_ID, (Integer) item.getObject());
                fragment.setArguments(bundle);
                break;
            case Section: //nothing happens
            case Margin:
            default:
                break;
        }

        if (fragment != null) {
            pendingFragment = fragment;
            mDrawerLayout.closeDrawer(mListMenu);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetMainFragment();
    }

    private int mResetCourseID = 0;

    public void setResetCourseID(int courseID) {
        mResetCourseID = courseID;

        if (isVisible())
            resetMainFragment();
    }

    private void resetMainFragment() {
        if (mResetCourseID == 0)
            return;

        CourseDetailFragment fragment = new CourseDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BTKey.EXTRA_COURSE_ID, mResetCourseID);
        fragment.setArguments(bundle);

        FragmentManager fm = getSupportFragmentManager();
        while (fm.getBackStackEntryCount() > 0) {
            onBackPressed();
        }

        fm.beginTransaction().replace(R.id.content, fragment).commit();
        fragment.onPendingFragmentResume();

        mDrawerLayout.closeDrawer(mListMenu);
        mResetCourseID = 0;
    }

    public void refreshSideList() {
        mSideAdapter.refreshAdapter();
    }

    // onDrawerClosed
    private void replacePendingFragment() {
        FragmentManager fm = getSupportFragmentManager();
        if (pendingFragment != null && fm.getBackStackEntryCount() == 0) {
            fm.beginTransaction().replace(R.id.content, pendingFragment).commit();
            pendingFragment.onPendingFragmentResume();
        }
        pendingFragment = null;
    }

    // onDrawerOpened
    private void refreshSideMenu() {
        if (getBTService() == null)
            return;

        getBTService().courses(new Callback<CourseJson[]>() {
            @Override
            public void success(CourseJson[] courseJsons, Response response) {
                if (mSideAdapter != null)
                    mSideAdapter.refreshAdapter();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
            }
        });
    }
}
