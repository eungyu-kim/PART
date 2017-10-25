package io.github.hidroh.calendar.content;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.provider.CalendarContract;

public class EventCursor extends CursorWrapper {

    public static final String[] PROJECTION = new String[]{
            CalendarContract.Events._ID,
            CalendarContract.Events.CALENDAR_ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.ALL_DAY
    };
    private static final int PROJECTION_INDEX_ID = 0;
    private static final int PROJECTION_INDEX_CALENDAR_ID = 1;
    private static final int PROJECTION_INDEX_TITLE = 2;
    private static final int PROJECTION_INDEX_DTSTART = 3;
    private static final int PROJECTION_INDEX_DTEND = 4;
    private static final int PROJECTION_INDEX_ALL_DAY = 5;

    public EventCursor(Cursor cursor) {
        super(cursor);
    }

    public long getId() {
        return getLong(PROJECTION_INDEX_ID);
    }

    public long getCalendarId() {
        return getLong(PROJECTION_INDEX_CALENDAR_ID);
    }

    public String getTitle() {
        return getString(PROJECTION_INDEX_TITLE);
    }

    public long getDateTimeStart() {
        return getLong(PROJECTION_INDEX_DTSTART);
    }

    public long getDateTimeEnd() {
        return getLong(PROJECTION_INDEX_DTEND);
    }

    public boolean getAllDay() {
        return getInt(PROJECTION_INDEX_ALL_DAY) == 1;
    }
}
