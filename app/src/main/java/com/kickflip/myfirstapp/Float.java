package com.kickflip.myfirstapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Float extends Service{
    private WindowManager windowManager;

    private GridView gridView;
    private Categories categories;
    private InvisibleBox invisibleBox;

    private int chosenApp = -1;
    private int chosenCategorie = -1;

    private static final String LOG_TAG = "ForegroundService";

    private int FOREGROUND_SERVICE = 101;

    private boolean listOn = false;

    private NotificationCompat.Builder notificationBuilder;
    private boolean haptic = true;

    private int delay;

    private MyBroadcastReceiver receiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter = new IntentFilter(PropertiesFragment.PROPERTIES_ACTION);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(PropertiesFragment.PROPERTIES_ACTION + ".icon_size");
        filter.addAction(LookFeelFragment.LOOK_FEEL_ACTION);

        receiver = new MyBroadcastReceiver();
        registerReceiver(receiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, final int flags, int startId) {
        if (intent != null && intent.getAction().equals(MyActivity.STARTFOREGROUND_ACTION)) {
            haptic = intent.getBooleanExtra("switch_haptic", true);
            delay = intent.getIntExtra("list_delay", 0);

            Intent notificationIntent = new Intent(this, MyActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.notification_title))
                    .setContentText(getString(R.string.notification_content))
                    .setSmallIcon(android.R.color.transparent)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true);

            if (intent.getBooleanExtra("switch_notification", true)) {
                notificationBuilder.setPriority(Notification.PRIORITY_DEFAULT);
            }else {
                notificationBuilder.setPriority(Notification.PRIORITY_MIN);
            }

            startForeground(FOREGROUND_SERVICE, notificationBuilder.build());


            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    75,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.TOP | Gravity.RIGHT;

            invisibleBox = new InvisibleBox(this);
            windowManager.addView(invisibleBox, params);

            invisibleBox.setAlpha(intent.getBooleanExtra("switch_show", false) ? 1 : 0);

            //gridView = MyActivity.getGridView();

            final CategorieInfo[] categorieInfos = {new CategorieInfo("1", R.mipmap.ic_launcher), new CategorieInfo("2", R.mipmap.ic_launcher), new CategorieInfo("3", R.mipmap.ic_launcher), new CategorieInfo("4", R.mipmap.ic_launcher)};
            for (CategorieInfo categorieInfo:categorieInfos){
                List<ApplicationInfo> applicationInfos = categorieInfo.getApplicationInfos();

                List<ApplicationInfo> applicationInfosAll = MyActivity.getApplist();

                Random random = new Random();

                for (int i = 0; i < 10; i++){
                    applicationInfos.add(applicationInfosAll.get(random.nextInt(applicationInfosAll.size())));
                }
            }

            categories = new Categories(getApplicationContext(), categorieInfos);

            final Handler handler = new Handler();
            final Runnable mLongPressed = new Runnable() {
                public void run() {
                    listOn = true;

                    WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                            WindowManager.LayoutParams.WRAP_CONTENT,
                            WindowManager.LayoutParams.WRAP_CONTENT,
                            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                            WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                            PixelFormat.TRANSLUCENT);
                    params.gravity = Gravity.TOP | Gravity.RIGHT;
                    params.dimAmount = 0.75f;

                    windowManager.addView(categories, params);
                }
            };

            invisibleBox.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        handler.postDelayed(mLongPressed, delay);
                        //return true;
                    }
                    if((event.getAction() == MotionEvent.ACTION_MOVE)||(event.getAction() == MotionEvent.ACTION_UP))
                        handler.removeCallbacks(mLongPressed);

                    switch (event.getAction()) {

                        case MotionEvent.ACTION_UP:
                            if (listOn) {
                                windowManager.removeView(categories);

                                listOn = false;
                            }

                            if (chosenCategorie != -1) {
                                categories.getChildAt(chosenCategorie).setBackgroundColor(0);

                                if (gridView != null) {
                                    windowManager.removeView(gridView);
                                }
                            }


                            if (chosenApp != -1) {

                                ApplicationInfo app = categorieInfos[chosenCategorie].getApplicationInfos().get(chosenApp);
                                try {
                                    Intent intent = getPackageManager().getLaunchIntentForPackage(app.packageName);

                                    if (null != intent) {
                                        startActivity(intent);
                                    }
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }

                                gridView.getChildAt(chosenApp).setBackgroundColor(0);
                            }

                            chosenApp = -1;
                            gridView = null;
                            chosenCategorie = -1;

                            return true;
                        case MotionEvent.ACTION_MOVE:
                            if (listOn) {
                                if (chosenCategorie != -1) {
                                    categories.getChildAt(chosenCategorie).setBackgroundColor(0);
                                }

                                DisplayMetrics metrics = new DisplayMetrics();
                                windowManager.getDefaultDisplay().getMetrics(metrics);
                                int width = metrics.widthPixels;

                                for (int index = 0; index < categories.getChildCount(); index++) {
                                    ImageView nextChild = (ImageView) categories.getChildAt(index);

                                    if (width - (nextChild.getX() + nextChild.getWidth()) <= event.getRawX() && width - nextChild.getX() >= event.getRawX() && nextChild.getY() <= event.getRawY() && nextChild.getY() + nextChild.getHeight() >= event.getRawY()) {
                                        nextChild.setBackgroundColor(Color.RED);

                                        if (chosenCategorie != index) {
                                            if (haptic) {
                                                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                                vibrator.vibrate(20);
                                            }

                                            if (gridView != null) {
                                                windowManager.removeView(gridView);
                                            }

                                            chosenCategorie = index;

                                            gridView = new AppGrid(getApplicationContext(), categorieInfos[chosenCategorie].getApplicationInfos());
                                            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                                                    WindowManager.LayoutParams.WRAP_CONTENT,
                                                    WindowManager.LayoutParams.WRAP_CONTENT,
                                                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                                                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                                                    PixelFormat.TRANSLUCENT);
                                            params.gravity = Gravity.TOP | Gravity.LEFT;


                                            windowManager.addView(gridView, params);
                                        }


                                        return true;
                                    }
                                }

                                if (gridView != null) {
                                    if (chosenApp != -1) {
                                        gridView.getChildAt(chosenApp).setBackgroundColor(0);
                                    }

                                    for (int index = 0; index < gridView.getChildCount(); index++) {
                                        ImageView nextChild = (ImageView) gridView.getChildAt(index);

                                        if (nextChild.getX() <= event.getRawX() && nextChild.getX() + nextChild.getWidth() >= event.getRawX() && nextChild.getY() <= event.getRawY() && nextChild.getY() + nextChild.getHeight() >= event.getRawY()) {
                                            nextChild.setBackgroundColor(Color.RED);

                                            if (chosenApp != index && haptic) {
                                                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                                vibrator.vibrate(20);
                                            }

                                            chosenApp = index;

                                            return true;
                                        }
                                    }

                                    chosenApp = -1;

                                }

                                return true;
                            }
                    }
                    return false;
                }

            });

        } else if(intent != null && intent.getAction().equals(MyActivity.STOPFOREGROUND_ACTION)) {
            stopForeground(true);
            stopSelf();

            if (invisibleBox != null) {
                windowManager.removeView(invisibleBox);
            }
        }

        return START_STICKY;
    }




    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_REBOOT)) {

                // && PreferenceManager.getDefaultSharedPreferences(context).getBoolean("switch_start_boot", false)

                Intent serviceIntent = new Intent(context, Float.class);
                serviceIntent.setAction(MyActivity.STARTFOREGROUND_ACTION);
                context.startService(serviceIntent);

            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                if (listOn) {
                    windowManager.removeView(gridView);

                    if (chosenApp != -1) {
                        gridView.getChildAt(chosenApp).setBackgroundColor(0);
                    }

                    chosenApp = -1;
                    listOn = false;
                }
            } else if (intent.getAction().equals(PropertiesFragment.PROPERTIES_ACTION)) {
                if (intent.getStringExtra("key").equals("switch_notification")) {
                    if (intent.getBooleanExtra("value", false)) {
                        notificationBuilder.setPriority(Notification.PRIORITY_DEFAULT);
                    } else {
                        notificationBuilder.setPriority(Notification.PRIORITY_MIN);
                    }
                    startForeground(FOREGROUND_SERVICE, notificationBuilder.build());
                } else if (intent.getStringExtra("key").equals("switch_haptic")) {
                    haptic = intent.getBooleanExtra("value", false);
                } else if (intent.getStringExtra("key").equals("switch_snap")) {

                } else if (intent.getStringExtra("key").equals("list_orientation")) {

                } else if (intent.getStringExtra("key").equals("list_delay")) {
                    delay = Integer.valueOf(intent.getStringExtra("value"));
                }

            } else if (intent.getAction().equals(PropertiesFragment.PROPERTIES_ACTION + ".icon_size")) {
                //gridView = MyActivity.getGridView();
            } else if (intent.getAction().equals(LookFeelFragment.LOOK_FEEL_ACTION)) {
                if (intent.getStringExtra("key").equals("color_picker")) {
                    invisibleBox.setBackgroundColor(intent.getIntExtra("value", 0));
                }else if (intent.getStringExtra("key").equals("switch_show")) {
                    invisibleBox.setAlpha(intent.getBooleanExtra("value", false) ? 1 : 0);
                }else if (intent.getStringExtra("key").equals("slider_width")) {

                    WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                            intent.getIntExtra("value", 75),
                            WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.TYPE_PHONE,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                            PixelFormat.TRANSLUCENT);
                    params.gravity = Gravity.TOP | Gravity.RIGHT;

                    windowManager.updateViewLayout(invisibleBox, params);
                }

                Log.i("ForegroundService", "Recieved");
            }
        }
    }
    private class InvisibleBox extends View{

        public InvisibleBox(Context context) {
            super(context);
            this.setBackgroundColor(Color.BLACK);
            this.setAlpha(0);
        }
    }
}
