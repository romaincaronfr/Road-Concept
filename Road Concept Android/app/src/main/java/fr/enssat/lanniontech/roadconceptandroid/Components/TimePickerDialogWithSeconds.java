package fr.enssat.lanniontech.roadconceptandroid.Components;

import java.util.Calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import fr.enssat.lanniontech.roadconceptandroid.Components.TimePicker.OnTimeChangedListener;
import fr.enssat.lanniontech.roadconceptandroid.R;

/**
 * A dialog that prompts the user for the time of day using a {@link TimePicker}.
 */
public class TimePickerDialogWithSeconds extends AlertDialog implements OnClickListener,
        OnTimeChangedListener {

    /**
     * The callback interface used to indicate the user is done filling in
     * the time (they clicked on the 'Set' button).
     */
    public interface OnTimeSetListener {

        /**
         * @param view The view associated with this listener.
         * @param hourOfDay The hour that was set.
         * @param minute The minute that was set.
         */
        void onTimeSet(TimePicker view, int hourOfDay, int minute, int seconds);
    }

    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";
    private static final String SECONDS = "seconds";
    private static final String IS_24_HOUR = "is24hour";

    private final TimePicker mTimePicker;
    private OnTimeSetListener mCallback;
    private final Calendar mCalendar;
    private final java.text.DateFormat mDateFormat;

    int mInitialHourOfDay;
    int mInitialMinute;
    int mInitialSeconds;
    boolean mIs24HourView;

    /**
     * @param context Parent.
     * @param hourOfDay The initial hour.
     * @param minute The initial minute.
     * @param is24HourView Whether this is a 24 hour view, or AM/PM.
     */
    public TimePickerDialogWithSeconds(Context context,
                                       int hourOfDay, int minute, int seconds, boolean is24HourView) {

        this(context, 0, hourOfDay, minute, seconds, is24HourView);
    }

    /**
     * @param context Parent.
     * @param theme the theme to apply to this dialog
     * @param hourOfDay The initial hour.
     * @param minute The initial minute.
     * @param is24HourView Whether this is a 24 hour view, or AM/PM.
     */
    public TimePickerDialogWithSeconds(Context context,
                                       int theme,
                                       int hourOfDay, int minute, int seconds, boolean is24HourView) {
        super(context, theme);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mInitialHourOfDay = hourOfDay;
        mInitialMinute = minute;
        mInitialSeconds = seconds;
        mIs24HourView = is24HourView;

        mDateFormat = DateFormat.getTimeFormat(context);
        mCalendar = Calendar.getInstance();
        updateTitle(mInitialHourOfDay, mInitialMinute, mInitialSeconds);

        setButton("Valider", this);
        setButton2("Annuler", (OnClickListener) null);
        //setIcon(android.R.drawable.ic_dialog_time);

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.time_picker_dialog, null);
        setView(view);
        mTimePicker = (TimePicker) view.findViewById(R.id.timePicker);

        // initialize state
//        mTimePicker.setCurrentHour(mInitialHourOfDay);
//        mTimePicker.setCurrentMinute(mInitialMinute);
//        mTimePicker.setCurrentSecond(mInitialSeconds);
        mTimePicker.setHoursMinutesSeconds(mInitialHourOfDay,mInitialMinute,mInitialSeconds);
        mTimePicker.setIs24HourView(mIs24HourView);
    }

    public void setOnTimeSetListener(OnTimeSetListener callBack){
        mCallback = callBack;
    }

    public void setOnTimeChangedListener(OnTimeChangedListener callBackTime){
        mTimePicker.setOnTimeChangedListener(callBackTime);
    }

    public void onClick(DialogInterface dialog, int which) {
        if (mCallback != null) {
            mTimePicker.clearFocus();
            mCallback.onTimeSet(mTimePicker, mTimePicker.getCurrentHour(),
                    mTimePicker.getCurrentMinute(), mTimePicker.getCurrentSeconds());
        }
    }

    public void onTimeChanged(TimePicker view, int hourOfDay, int minute, int seconds) {
        updateTitle(hourOfDay, minute, seconds);
    }

    public void updateTime(int hourOfDay, int minutOfHour, int seconds) {
//        mTimePicker.setCurrentHour(hourOfDay);
//        mTimePicker.setCurrentMinute(minutOfHour);
//        mTimePicker.setCurrentSecond(seconds);
        mTimePicker.setHoursMinutesSeconds(hourOfDay,minutOfHour,seconds);
    }

    public void updateTitle(int hour, int minute, int seconds) {
        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
        mCalendar.set(Calendar.MINUTE, minute);
        mCalendar.set(Calendar.SECOND, seconds);
        setTitle(mDateFormat.format(mCalendar.getTime()) + ":" + String.format("%02d" , seconds));
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(HOUR, mTimePicker.getCurrentHour());
        state.putInt(MINUTE, mTimePicker.getCurrentMinute());
        state.putInt(SECONDS, mTimePicker.getCurrentSeconds());
        state.putBoolean(IS_24_HOUR, mTimePicker.is24HourView());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int hour = savedInstanceState.getInt(HOUR);
        int minute = savedInstanceState.getInt(MINUTE);
        int seconds = savedInstanceState.getInt(SECONDS);
        mTimePicker.setCurrentHour(hour);
        mTimePicker.setCurrentMinute(minute);
        mTimePicker.setCurrentSecond(seconds);
        mTimePicker.setIs24HourView(savedInstanceState.getBoolean(IS_24_HOUR));
        mTimePicker.setOnTimeChangedListener(this);
        updateTitle(hour, minute, seconds);
    }


}
