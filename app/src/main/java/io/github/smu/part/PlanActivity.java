package io.github.smu.part;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TextView;

import io.github.smu.part.content.EventCursor;
import io.github.smu.part.content.EventsQueryHandler;
import io.github.smu.part.widget.AgendaAdapter;
import io.github.smu.part.widget.AgendaView;
import io.github.smu.part.widget.EventCalendarView;
import io.github.smu.part.widget.EventEditView;

/**
 * Created by soslt on 2017-10-25.
 */

public class PlanActivity extends AppCompatActivity {

    private static final String STATE_TOOLBAR_TOGGLE = "state:toolbarToggle";
    private static final String STATE_EMPTY_VISIBLE = "state:emptyVisible";

    private final Coordinator mCoordinator = new Coordinator();
    private CheckedTextView mToolbarToggle;
    private EventCalendarView mCalendarView;
    private AgendaView mAgendaView;
    private FloatingActionButton mFabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plan);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayOptions(
                ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
        setupContentView();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCoordinator.restoreState(savedInstanceState);
        if (savedInstanceState.getBoolean(STATE_TOOLBAR_TOGGLE, false)) {
            View toggleButton = findViewById(R.id.toolbar_toggle_frame);
            if (toggleButton != null) { // can be null as disabled in landscape
                toggleButton.performClick();
            }
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mCoordinator.coordinate(mToolbarToggle, mCalendarView, mAgendaView);
        boolean emptyVisible = savedInstanceState != null &&
                savedInstanceState.getBoolean(STATE_EMPTY_VISIBLE, false);
        if (emptyVisible) {
            toggleEmptyView(true);
        } else {
            if (checkPermissions()) {
                loadEvents();
            } else {
                requestPermissions();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_today, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.action_today) {
            mCoordinator.reset();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mCoordinator.saveState(outState);
        outState.putBoolean(STATE_TOOLBAR_TOGGLE, mToolbarToggle.isChecked());
        //noinspection ConstantConditions
        outState.putBoolean(STATE_EMPTY_VISIBLE,
                findViewById(R.id.empty).getVisibility() == View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCalendarView.deactivate();
        mAgendaView.setAdapter(null); // force detaching adapter
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (checkPermissions()) {
            toggleEmptyView(false);
            loadEvents();
        } else {
            toggleEmptyView(true);
        }
    }

    private void setupContentView() {
        mToolbarToggle = (CheckedTextView) findViewById(R.id.toolbar_toggle);
        View toggleButton = findViewById(R.id.toolbar_toggle_frame);
        if (toggleButton != null) { // can be null as disabled in landscape
            toggleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mToolbarToggle.toggle();
                    toggleCalendarView();
                }
            });
        }
        mCalendarView = (EventCalendarView) findViewById(R.id.calendar_view);
        mAgendaView = (AgendaView) findViewById(R.id.agenda_view);
        mFabAdd = (FloatingActionButton) findViewById(R.id.fab);
        mFabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEvent();
            }
        });
        //noinspection ConstantConditions
        mFabAdd.hide();
    }

    private void toggleCalendarView() {
        if (mToolbarToggle.isChecked()) {
            mCalendarView.setVisibility(View.VISIBLE);
        } else {
            mCalendarView.setVisibility(View.GONE);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void toggleEmptyView(boolean visible) {
        if (visible) {
            findViewById(R.id.empty).setVisibility(View.VISIBLE);
            findViewById(R.id.empty).bringToFront();
            findViewById(R.id.button_permission)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestPermissions();
                        }
                    });
        } else {
            findViewById(R.id.empty).setVisibility(View.GONE);
        }
    }

    private void createEvent() {
        startActivity(new Intent(this, EditActivity.class)
                .putExtra(EditActivity.EXTRA_EVENT,
                        EventEditView.Event.createInstance()));
    }

    private boolean checkPermissions() {
        return (checkPermission(Manifest.permission.READ_CALENDAR) |
                checkPermission(Manifest.permission.WRITE_CALENDAR)) ==
                PackageManager.PERMISSION_GRANTED;
    }

    @VisibleForTesting
    protected void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_CALENDAR,
                        Manifest.permission.WRITE_CALENDAR},
                0);
    }

    private void loadEvents() {
        mFabAdd.show();
        mCalendarView.setCalendarAdapter(new CalendarCursorAdapter(this));
        mAgendaView.setAdapter(new AgendaCursorAdapter(this));
    }

    @VisibleForTesting
    protected int checkPermission(@NonNull String permission) {
        return ActivityCompat.checkSelfPermission(this, permission);
    }

    /**
     * Coordinator utility that synchronizes widgets as selected date changes
     */
    static class Coordinator {
        private static final String STATE_SELECTED_DATE = "state:selectedDate";

        private final EventCalendarView.OnChangeListener mCalendarListener
                = new EventCalendarView.OnChangeListener() {
            @Override
            public void onSelectedDayChange(long calendarDate) {
                sync(calendarDate, mCalendarView);
            }
        };
        private final AgendaView.OnDateChangeListener mAgendaListener
                = new AgendaView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(long dayMillis) {
                sync(dayMillis, mAgendaView);
            }
        };
        private TextView mTextView;
        private EventCalendarView mCalendarView;
        private AgendaView mAgendaView;
        private long mSelectedDayMillis = CalendarUtils.NO_TIME_MILLIS;

        /**
         * Set up widgets to be synchronized
         * @param textView      title
         * @param calendarView  calendar view
         * @param agendaView    agenda view
         */
        public void coordinate(@NonNull TextView textView,
                               @NonNull EventCalendarView calendarView,
                               @NonNull AgendaView agendaView) {
            if (mCalendarView != null) {
                mCalendarView.setOnChangeListener(null);
            }
            if (mAgendaView != null) {
                mAgendaView.setOnDateChangeListener(null);
            }
            mTextView = textView;
            mCalendarView = calendarView;
            mAgendaView = agendaView;
            if (mSelectedDayMillis < 0) {
                mSelectedDayMillis = CalendarUtils.today();
            }
            mCalendarView.setSelectedDay(mSelectedDayMillis);
            agendaView.setSelectedDay(mSelectedDayMillis);
            updateTitle(mSelectedDayMillis);
            calendarView.setOnChangeListener(mCalendarListener);
            agendaView.setOnDateChangeListener(mAgendaListener);
        }

        void saveState(Bundle outState) {
            outState.putLong(STATE_SELECTED_DATE, mSelectedDayMillis);
        }

        void restoreState(Bundle savedState) {
            mSelectedDayMillis = savedState.getLong(STATE_SELECTED_DATE,
                    CalendarUtils.NO_TIME_MILLIS);
        }

        void reset() {
            mSelectedDayMillis = CalendarUtils.today();
            if (mCalendarView != null) {
                mCalendarView.reset();
            }
            if (mAgendaView != null) {
                mAgendaView.reset();
            }
            updateTitle(mSelectedDayMillis);
        }

        private void sync(long dayMillis, View originator) {
            mSelectedDayMillis = dayMillis;
            if (originator != mCalendarView) {
                mCalendarView.setSelectedDay(dayMillis);
            }
            if (originator != mAgendaView) {
                mAgendaView.setSelectedDay(dayMillis);
            }
            updateTitle(dayMillis);
        }

        private void updateTitle(long dayMillis) {
            mTextView.setText(CalendarUtils.toMonthString(mTextView.getContext(), dayMillis));
        }
    }

    static class AgendaCursorAdapter extends AgendaAdapter {

        private final DayEventsQueryHandler mHandler;

        public AgendaCursorAdapter(Context context) {
            super(context);
            mHandler = new DayEventsQueryHandler(context.getContentResolver(), this);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            deactivate();
        }

        @Override
        protected void loadEvents(long timeMillis) {
            mHandler.startQuery(timeMillis, timeMillis, timeMillis + DateUtils.DAY_IN_MILLIS);
        }
    }

    static class CalendarCursorAdapter extends EventCalendarView.CalendarAdapter {
        private final MonthEventsQueryHandler mHandler;

        public CalendarCursorAdapter(Context context) {
            mHandler = new MonthEventsQueryHandler(context.getContentResolver(), this);
        }

        @Override
        protected void loadEvents(long monthMillis) {
            long startTimeMillis = CalendarUtils.monthFirstDay(monthMillis),
                    endTimeMillis = startTimeMillis + DateUtils.DAY_IN_MILLIS *
                            CalendarUtils.monthSize(monthMillis);
            mHandler.startQuery(monthMillis, startTimeMillis, endTimeMillis);
        }
    }

    static class DayEventsQueryHandler extends EventsQueryHandler {

        private final AgendaCursorAdapter mAgendaCursorAdapter;

        public DayEventsQueryHandler(ContentResolver cr, AgendaCursorAdapter agendaCursorAdapter) {
            super(cr);
            mAgendaCursorAdapter = agendaCursorAdapter;
        }

        @Override
        protected void handleQueryComplete(int token, Object cookie, EventCursor cursor) {
            mAgendaCursorAdapter.bindEvents((Long) cookie, cursor);
        }
    }

    static class MonthEventsQueryHandler extends EventsQueryHandler {

        private final CalendarCursorAdapter mAdapter;

        public MonthEventsQueryHandler(ContentResolver cr, CalendarCursorAdapter adapter) {
            super(cr);
            mAdapter = adapter;
        }

        @Override
        protected void handleQueryComplete(int token, Object cookie, EventCursor cursor) {
            mAdapter.bindEvents((Long) cookie, cursor);
        }
    }
}