package io.github.hidroh.calendar.content;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.provider.CalendarContract;

public class CalendarCursor extends CursorWrapper {

    public static final String[] PROJECTION = new String[]{
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
    };
    private static final int PROJECTION_INDEX_ID = 0;
    private static final int PROJECTION_INDEX_DISPLAY_NAME = 1;

    public CalendarCursor(Cursor cursor) {
        super(cursor);
    }

    public long getId() {
        return getLong(PROJECTION_INDEX_ID);
    }

    public String getDisplayName() {
        return getString(PROJECTION_INDEX_DISPLAY_NAME);
    }
}
