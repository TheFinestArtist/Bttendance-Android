package com.bttendance.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.bttendance.R;
import com.bttendance.adapter.FeedAdapter;
import com.bttendance.event.AddFragmentEvent;
import com.bttendance.event.LoadingEvent;
import com.bttendance.event.ShowAlertDialogEvent;
import com.bttendance.event.attendance.AttdCheckedEvent;
import com.bttendance.event.attendance.AttdStartedEvent;
import com.bttendance.event.refresh.RefreshFeedEvent;
import com.bttendance.event.update.UpdateCourseListEvent;
import com.bttendance.event.update.UpdateFeedEvent;
import com.bttendance.helper.DipPixelHelper;
import com.bttendance.helper.IntArrayHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.cursor.PostCursor;
import com.bttendance.model.json.CourseJsonHelper;
import com.bttendance.model.json.EmailJson;
import com.bttendance.model.json.PostJson;
import com.bttendance.model.json.UserJson;
import com.bttendance.view.Bttendance;
import com.squareup.otto.BTEventBus;
import com.squareup.otto.Subscribe;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class CourseDetailFragment extends BTFragment implements View.OnClickListener {

    ListView mListView;
    FeedAdapter mAdapter;
    CourseJsonHelper mCourseHelper;
    View header;
    boolean mAuth;

    public CourseDetailFragment(int courseID) {
        mCourseHelper = new CourseJsonHelper(getActivity(), courseID);
        UserJson user = BTPreference.getUser(getActivity());
        mAuth = IntArrayHelper.contains(user.supervising_courses, mCourseHelper.getID());
    }

    /**
     * Action Bar Menu
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setTitle(mCourseHelper.getName());
        actionBar.setDisplayHomeAsUpEnabled(true);
        inflater.inflate(R.menu.profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.abs__home:
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_setting:
                registerForContextMenu(mListView);
                getActivity().openContextMenu(mListView);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Context Menu
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (mAuth)
            getActivity().getMenuInflater().inflate(R.menu.course_detail_supv_context_menu, menu);
        else
            getActivity().getMenuInflater().inflate(R.menu.course_detail_std_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {

        switch (item.getItemId()) {
            case R.id.show_grades:
                showGrade();
                return true;
            case R.id.export_grades:
                exportGrade();
                return true;
            case R.id.add_manager:
                showAddManager();
                return true;
            case R.id.unjoin_course:
                unjoinCourse();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Drawing View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);
        header = inflater.inflate(R.layout.course_header, null, false);
        refreshHeader();
        mListView.addHeaderView(header);
        header.findViewById(R.id.clicker_bt).setOnClickListener(this);
        header.findViewById(R.id.attendance_bt).setOnClickListener(this);
        header.findViewById(R.id.notice_bt).setOnClickListener(this);
        View padding = new View(getActivity());
        padding.setMinimumHeight((int) DipPixelHelper.getPixel(getActivity(), 7));
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

        BTEventBus.getInstance().post(new LoadingEvent(true));
        getBTService().courseFeed(mCourseHelper.getID(), 0, new Callback<PostJson[]>() {
            @Override
            public void success(PostJson[] posts, Response response) {
                mAdapter.swapCursor(new PostCursor(BTTable.getPostsOfCourse(mCourseHelper.getID())));
                BTEventBus.getInstance().post(new LoadingEvent(false));
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                BTEventBus.getInstance().post(new LoadingEvent(false));
            }
        });
    }

    private void refreshHeader() {
        if (!this.isAdded() || header == null)
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!mAuth)
                    header.findViewById(R.id.manager_layout).setVisibility(View.GONE);

                Bttendance bttendance = (Bttendance) header.findViewById(R.id.bttendance);
                int grade = Integer.parseInt(mCourseHelper.getGrade());
                bttendance.setBttendance(Bttendance.STATE.GRADE, grade);
                TextView courseInfo = (TextView) header.findViewById(R.id.course_info);
                courseInfo.setText(getString(R.string.prof_) + " " + mCourseHelper.getProfessorName() + "\n"
                        + mCourseHelper.getSchoolName() + "\n\n"
                        + String.format(getString(R.string.n_students), mCourseHelper.getStudentCount()) + "\n"
                        + String.format(getString(R.string.n_attendance_rate), grade) + "\n"
                        + String.format(getString(R.string.n_clickers), mCourseHelper.getClickerUsage()) + "\n"
                        + String.format(getString(R.string.n_notices), mCourseHelper.getNoticeUsage())
                );
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
    public void onUpdate(UpdateFeedEvent event) {
        swapCursor();
        refreshHeader();
    }

    @Subscribe
    public void onRefresh(RefreshFeedEvent event) {
        getFeed();
        refreshHeader();
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
                    mAdapter.swapCursor(new PostCursor(BTTable.getPostsOfCourse(mCourseHelper.getID())));
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clicker_bt:
                showClicker();
                break;
            case R.id.attendance_bt:
                startAttendance();
                break;
            case R.id.notice_bt:
                showNotice();
                break;
        }
    }

    /**
     * Private Methods
     */
    private void showClicker() {
        StartClickerFragment frag = new StartClickerFragment(mCourseHelper.getID());
        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
    }

    private void startAttendance() {

    }

    private void showNotice() {
        CreateNoticeFragment frag = new CreateNoticeFragment(mCourseHelper.getID());
        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
    }

    private void showGrade() {
        GradeFragment frag = new GradeFragment(mCourseHelper.getID());
        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
    }

    private void exportGrade() {
        final ProgressDialog progress = ProgressDialog.show(getActivity(), "", getString(R.string.exporting_grades));
        getBTService().courseExportGrades(mCourseHelper.getID(), new Callback<EmailJson>() {
            @Override
            public void success(EmailJson email, Response response) {
                if (progress.isShowing())
                    progress.dismiss();

                BTDialogFragment.DialogType type = BTDialogFragment.DialogType.OK;
                String title = getString(R.string.export_grades);
                String message = String.format(getString(R.string.exporting_grade_has_been_finished), email.email);
                BTEventBus.getInstance().post(new ShowAlertDialogEvent(type, title, message));
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                if (progress.isShowing())
                    progress.dismiss();
            }
        });
    }

    private void showAddManager() {
        AddManagerFragment frag = new AddManagerFragment(mCourseHelper.getID());
        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
    }

    private void unjoinCourse() {
        final ProgressDialog progress = ProgressDialog.show(getActivity(), "", getString(R.string.unjoining_course));
        getBTService().dettendCourse(mCourseHelper.getID(), new Callback<UserJson>() {
            @Override
            public void success(UserJson user, Response response) {
                if (progress.isShowing())
                    progress.dismiss();

                BTEventBus.getInstance().post(new UpdateCourseListEvent());
                BTEventBus.getInstance().post(new UpdateFeedEvent());

                int count = getActivity().getSupportFragmentManager().getBackStackEntryCount();
                getActivity().getSupportFragmentManager().popBackStack();
                while (count-- >= 0)
                    getActivity().getSupportFragmentManager().popBackStack();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                if (progress.isShowing())
                    progress.dismiss();
            }
        });
    }
}
