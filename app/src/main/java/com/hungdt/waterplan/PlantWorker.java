package com.hungdt.waterplan;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.hungdt.waterplan.database.DBHelper;
import com.hungdt.waterplan.dataset.Constant;
import com.hungdt.waterplan.model.Remind;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PlantWorker extends Worker {
    public static final String KEY_TASK_OUTPUT = "key_output";
    public static final String channelID = "channelID";
    public static final String channelName = "Channel Plant";

    Calendar calendar = Calendar.getInstance();

    public PlantWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data data1 = null;
        Log.e("123", "out On()");
        if (DBHelper.getInstance(getApplicationContext()).getPermission().equals("On")) {
            Log.e("123", "in On()");
            Data data = getInputData();
            String desc = data.getString(KEY.KEY_TASK_DESC);

            List<Remind> reminds = DBHelper.getInstance(getApplicationContext()).getAllRemind();

            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfDT = new SimpleDateFormat(Constant.getDateTimeFormat());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfTime = new SimpleDateFormat(Constant.getTimeFormat());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfHour = new SimpleDateFormat(Constant.getHourFormat());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfMinute = new SimpleDateFormat(Constant.getMinuteFormat());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfDay = new SimpleDateFormat(Constant.getDayFormat());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfMonth = new SimpleDateFormat(Constant.getMonthFormat());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfYear = new SimpleDateFormat(Constant.getYearFormat());
            String instanceTime = sdfDT.format(calendar.getTime());
            Date dateInstance;
            Date date;
            int hourInstance = 0;
            int minuteInstance = 0;
            int countDayInstance = 0;
            try {
                dateInstance = sdfDT.parse(instanceTime);

                if (dateInstance != null) {
                    hourInstance = Integer.parseInt(sdfHour.format(dateInstance));
                    minuteInstance = Integer.parseInt(sdfMinute.format(dateInstance));
                    countDayInstance = countDay(Integer.parseInt(sdfYear.format(dateInstance)),
                            Integer.parseInt(sdfMonth.format(dateInstance)),
                            Integer.parseInt(sdfDay.format(dateInstance)));
                }
                for (int i = 0; i < reminds.size(); i++) {
                    date = sdfDT.parse(reminds.get(i).getRemindCreateDT());
                    if (date != null) {
                        int dayCheck = countDay(Integer.parseInt(sdfYear.format(date)),
                                Integer.parseInt(sdfMonth.format(date)),
                                Integer.parseInt(sdfDay.format(date)))
                                + Integer.parseInt(reminds.get(i).getCareCycle());
                        if (countDayInstance >= dayCheck) {
                            if (minuteInstance < 45 && Integer.parseInt(sdfHour.format(date)) == hourInstance) {
                                int nbCheck = minuteInstance - Integer.parseInt(sdfMinute.format(date)) + 15;
                                if (nbCheck >= 0 && nbCheck <= 15) {
                                    displayNotification("Don't forget to take care of your plant ", "at " + sdfTime.format(date), i);
                                }
                            }
                            if (minuteInstance >= 45) {
                                if (Integer.parseInt(sdfHour.format(date)) == hourInstance) {
                                    int nbCheck = minuteInstance - Integer.parseInt(sdfMinute.format(date)) + 15;
                                    if (nbCheck > 0 && nbCheck <= 15) {
                                        displayNotification("Don't forget to take care of your plant ", "at " + sdfTime.format(date), i);
                                    }
                                }
                                if (Integer.parseInt(sdfHour.format(date)) == hourInstance + 1) {
                                    int nbCheck = 60 - minuteInstance + Integer.parseInt(sdfMinute.format(date));
                                    if (nbCheck >= 0 && nbCheck <= 15) {
                                        displayNotification("Don't forget to take care of your plant ", "at " + sdfTime.format(date), i);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //setting phần hiển thị
            data1 = new Data.Builder()
                    .putString(KEY_TASK_OUTPUT, "Task Finished!")
                    .build();

        }
        return Result.success(data1);
    }

    private void displayNotification(String task, String desc, int i) {

        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }


        //Phần hiển thị notifi
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle(task)
                .setContentText(desc)
                .setSmallIcon(R.mipmap.ic_launcher);


        manager.notify(i, builder.build());
    }

    private int countDay(int year, int month, int day) {
        if (month < 3) {
            year--;
            month += 12;
        }
        return 365 * year + year / 4 - year / 100 + year / 400 + (153 * month - 457) / 5 + day - 306;
    }
}
