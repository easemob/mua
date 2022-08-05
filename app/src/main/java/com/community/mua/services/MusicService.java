package com.community.mua.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleService;

import com.community.mua.common.Constants;
import com.community.mua.livedatas.LiveDataBus;
import com.community.mua.livedatas.LiveEvent;
import com.community.mua.ui.MainActivity;

import java.io.IOException;

public class MusicService extends LifecycleService {
    private AssetManager mAssetManager;
    private MediaPlayer mMediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String CHANNEL_ONE_ID = "com.community.mua";
            String CHANNEL_ONE_NAME = "Channel One";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);

//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            PendingIntent pendingIntent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            } else {
                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            }

            Notification notification = new Notification.Builder(this).setChannelId(CHANNEL_ONE_ID)
                    .setTicker("Mua")
                    .setContentTitle("Mua")
                    .setContentIntent(pendingIntent)
                    .getNotification();
            notification.flags |= Notification.FLAG_NO_CLEAR;
            startForeground(1, notification);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAssetManager = getAssets();
        //创建MediaPlayer
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mMediaPlayer.start();
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.e("tagqi", "onCompletion");
                mMediaPlayer.start();
            }
        });

        LiveDataBus.get().with(Constants.BGM_CTRL, LiveEvent.class).observe(this, this::onMusicEvent);
    }

    private String music;

    private void onMusicEvent(LiveEvent event) {
        switch (event.event) {
            case Constants.BGM_START: // 播放
                if (TextUtils.isEmpty(event.message)) {
                    break;
                }
                music = event.message;
                prepareAndPlay();
                break;
            //停止声音
            case Constants.BGM_PAUSE:
                mMediaPlayer.pause();
                break;
            case Constants.BGM_STOP:
                //如果原来正在播放或暂停
                mMediaPlayer.stop();
                break;
        }

    }

    private void prepareAndPlay() {
        try {
            //打开指定的音乐文件
            AssetFileDescriptor assetFileDescriptor = mAssetManager.openFd(music);
            mMediaPlayer.reset();
            //使用MediaPlayer加载指定的声音文件
            mMediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            mMediaPlayer.prepare(); // 准备声音
            mMediaPlayer.setLooping(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }
}
