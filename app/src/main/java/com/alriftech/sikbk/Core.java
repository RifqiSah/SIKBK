package com.alriftech.sikbk;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.LeadingMarginSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Core {

    private static Context ctx;
    private static String CHANNEL_ID = "SIKBK_NOTIFICATION_CHANNEL";

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public Core(Context ctx) {
        this.ctx = ctx;
        createNotificationChannel();
    }

    public static String getString(int code) {
        return ctx.getResources().getString(code);
    }

    public static String API(String URI) {
        return getString(R.string.API_URL) + URI + "/?api_key=" + getString(R.string.API_KEY);
    }

    public static String API(String URI, String page) {
        return getString(R.string.API_URL) + URI + "/?pageno=" + page + "&api_key=" + getString(R.string.API_KEY);
    }

    private static boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    private static boolean isConnectedWifi() {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    private static boolean isConnectedMobile() {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    public static boolean cekInternet(View v) {
        if (isOnline()) {
            if (isConnectedMobile() || isConnectedWifi()) {
                return true;
            }
        }
        else {
            Snackbar.make(v, "Tidak ada koneksi Intetnet!", Snackbar.LENGTH_LONG).show();
        }

        return false;
    }

    public static boolean cekInternet() {
        if (isOnline()) {
            if (isConnectedMobile() || isConnectedWifi()) {
                return true;
            }
        }

        return false;
    }

    public static String formatDate(String tanggal) {
        // 2018-12-24 13:00:00
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;

        try {
            date = sdf.parse(tanggal);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        sdf = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm");
        return sdf.format(date);
    }

    public static CharSequence formatList(String str) {
        String[] bulletpoint = str.split("\n");
        CharSequence allText = "";

        for (String aBulletpoint : bulletpoint) {
            String text = aBulletpoint.trim();// + ".";

            if (!text.endsWith("."))
                text += ".";

            SpannableString spannableString = new SpannableString(text + "\n");
            spannableString.setSpan(new LeadingMarginSpan() {
                @Override
                public int getLeadingMargin(boolean first) {
                    return getString(R.string.bulletpoint).length() * 20;
                }

                @Override
                public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
                    if (first) {
                        Paint.Style orgStyle = p.getStyle();
                        p.setStyle(Paint.Style.FILL);
                        c.drawText(getString(R.string.bulletpoint) + " ", 0, bottom - p.descent(), p);
                        p.setStyle(orgStyle);
                    }
                }
            }, 0, text.length(), 0);
            allText = TextUtils.concat(allText, spannableString);
        }

        return allText;
    }

    public static int getYear() {
        long yourmilliseconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Date resultdate = new Date(yourmilliseconds);

        return Integer.parseInt(sdf.format(resultdate));
    }

    public static String sisaWaktu(String tanggal) {
        // 2018-12-24 13:00:00
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;

        try {
            date = sdf.parse(tanggal);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long time1 = date.getTime();
        long time2 = System.currentTimeMillis();
        long timemilis = time1 - time2;

        if (timemilis < 1)
            return "0";

        long diffSeconds = timemilis / 1000;
        long diffMinutes = diffSeconds / 60;
        long diffHours = diffMinutes / 60;
        long diffDays = diffHours / 24;

        if (diffDays > 0)
            return diffDays + " hari tersisa";
        else if ((diffHours % 24) > 0)
            return (diffHours % 24) + " jam tersisa";
        else if ((diffMinutes % 60) > 0)
            return (diffMinutes % 60) + " menit tersisa. Segera!";
        else if ((diffSeconds % 60) > 0)
            return (diffSeconds % 60) + " detik tersisa. Segera!";
        else
            return "0";

//        return String.format("%02d Hari, %02d Jam, %02d Menit, %02d Detik",  diffDays, diffHours % 24, diffMinutes % 60, diffSeconds % 60);
    }

    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.trim().isEmpty())
            return false;

        return true;
    }

    public static double hitungJarak(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static void autoSizeListview(ListView lstView, int padding) {
        ListAdapter listadp = lstView.getAdapter();
        if (listadp != null) {
            int totalHeight = 0;

            for (int i = 0; i < listadp.getCount(); i++) {
                View listItem = listadp.getView(i, null, lstView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight() + padding;
            }

            ViewGroup.LayoutParams params = lstView.getLayoutParams();
            params.height = totalHeight + (lstView.getDividerHeight() * (listadp.getCount() - 1));
            lstView.setLayoutParams(params);
            lstView.requestLayout();
        }
    }

    public static void showNotification(String sTitle, String sContent) {
        Intent intent = new Intent(ctx, SplashActivity.class);
        intent.putExtra("notif", true);

        int requestID = (int) System.currentTimeMillis();
        int flags = PendingIntent.FLAG_CANCEL_CURRENT;

        PendingIntent pIntent = PendingIntent.getActivity(ctx, requestID, intent, flags);

        Notification noti = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(ContextCompat.getColor(ctx, R.color.white))
                .setContentTitle(sTitle)
                .setContentText(sContent)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(sContent))
                .build();

        NotificationManager mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, noti);
    }

    public static void showNotification(String sTitle, String sContent, boolean version) {
        final String appPackageName = ctx.getPackageName(); // getPackageName() from Context or Activity object
        Intent intent;

        try {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
        }
        catch (android.content.ActivityNotFoundException e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
        }

        int requestID = (int) System.currentTimeMillis();
        int flags = PendingIntent.FLAG_CANCEL_CURRENT;

        PendingIntent pIntent = PendingIntent.getActivity(ctx, requestID, intent, flags);
        Notification noti = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(ContextCompat.getColor(ctx, R.color.white))
                .setContentTitle(sTitle)
                .setContentText(sContent)
                .setContentIntent(pIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(sContent))
                .setAutoCancel(true)
                .build();

        NotificationManager mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, noti);
    }
}