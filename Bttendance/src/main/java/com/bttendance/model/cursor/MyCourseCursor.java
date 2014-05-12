package com.bttendance.model.cursor;

import android.content.Context;
import android.database.MatrixCursor;

import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.UserJson;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class MyCourseCursor extends MatrixCursor {

    private final static String[] COLUMNS = {
            "_id"
    };

    public MyCourseCursor(Context context) {
        super(COLUMNS);
        UserJson user = BTPreference.getUser(context);

        for (int i = 0; i < user.supervising_courses.length; i++)
            if (BTTable.MyCourseTable.get(user.supervising_courses[i].id) != null)
                addRow(new Object[]{user.supervising_courses[i]});

        for (int i = 0; i < user.attending_courses.length; i++)
            if (BTTable.MyCourseTable.get(user.attending_courses[i].id) != null)
                addRow(new Object[]{user.attending_courses[i]});
    }
}
