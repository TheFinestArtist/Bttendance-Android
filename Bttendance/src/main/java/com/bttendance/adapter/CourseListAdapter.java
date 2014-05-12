package com.bttendance.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bttendance.R;
import com.bttendance.event.AddFragmentEvent;
import com.bttendance.event.attendance.AttdInProgressEvent;
import com.bttendance.event.attendance.AttdStartEvent;
import com.bttendance.fragment.CourseDetailFragment;
import com.bttendance.helper.DateHelper;
import com.bttendance.helper.IntArrayHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.UserJson;
import com.bttendance.view.Bttendance;
import com.squareup.otto.BTEventBus;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class CourseListAdapter extends CursorAdapter implements View.OnClickListener {

    public CourseListAdapter(Context context, Cursor c) {
        super(context, c, false);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.course_item, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        CourseJson course = BTTable.MyCourseTable.get(cursor.getInt(0));
        UserJson user = BTPreference.getUser(mContext);

        Bttendance bttendance = (Bttendance) view.findViewById(R.id.bttendance);
        View btButton = view.findViewById(R.id.bttendance_bt);
        View selector = view.findViewById(R.id.item_selector);

        btButton.setOnClickListener(this);
        selector.setOnClickListener(this);

        selector.setVisibility(View.VISIBLE);

        btButton.setTag(R.id.course_id, course.id);
        selector.setTag(R.id.course_id, course.id);

        long currentTime = DateHelper.getCurrentGMTTimeMillis();

        boolean mTime = course.attdCheckedAt != null && currentTime - DateHelper.getTime(course.attdCheckedAt) < Bttendance.PROGRESS_DURATION;
        boolean supved = IntArrayHelper.contains(user.supervising_courses, course.id);

        if (mTime) {
            long time = currentTime - DateHelper.getTime(course.attdCheckedAt);
            int progress = (int) ((float) 100 * ((float) Bttendance.PROGRESS_DURATION - (float) time) / (float) Bttendance.PROGRESS_DURATION);
            bttendance.setBttendance(Bttendance.STATE.CHECKING, progress);
            btButton.setTag(R.id.checking, true);
            btButton.setClickable(false);
        } else {
            int grade = Integer.parseInt(course.grade);
            bttendance.setBttendance(Bttendance.STATE.GRADE, grade);
            btButton.setTag(R.id.checking, false);
            btButton.setClickable(supved);
        }

        TextView title = (TextView) view.findViewById(R.id.title);
        TextView message = (TextView) view.findViewById(R.id.message);
        title.setText(course.number + " " + course.name);
        message.setText(context.getString(R.string.prof_) + course.professor_name + "\n" + course.school.name);
    }

    @Override
    public long getItemId(int position) {
        Cursor cursor = (Cursor) getItem(position);
        if (cursor == null)
            return -1;

        int id = cursor.getInt(0);
        return id;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bttendance_bt:
                boolean checking = (Boolean) v.getTag(R.id.checking);
                if (checking)
                    BTEventBus.getInstance().post(new AttdInProgressEvent());
                else
                    BTEventBus.getInstance().post(new AttdStartEvent((Integer) v.getTag(R.id.course_id)));
                break;
            case R.id.item_selector:
                int course_id = (Integer) v.getTag(R.id.course_id);
                CourseJson course = BTTable.MyCourseTable.get(course_id);
                CourseDetailFragment frag = new CourseDetailFragment(course.id);
                BTEventBus.getInstance().post(new AddFragmentEvent(frag));
                break;
            default:
                break;
        }
    }
}
