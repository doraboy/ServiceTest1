package tw.dora.servicetest1;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.SeekBar;

import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {
    private MediaPlayer mediaPlayer ;
    private Timer timer;

    public MyService() {
        Log.v("brad","MyService()");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(mediaPlayer!=null) return;

        mediaPlayer = MediaPlayer.create(this,R.raw.brad);
        int len = mediaPlayer.getDuration();
        Intent intent = new Intent("musicPlaying");
        intent.putExtra("len",len);
        sendBroadcast(intent);

        Log.v("brad","music:"+len);

        timer = new Timer();
        timer.schedule(new MyTask(),0,60);
    }

    private class MyTask extends TimerTask{
        @Override
        public void run() {
            if(mediaPlayer!= null && mediaPlayer.isPlaying()) {
                    Intent intent = new Intent("musicPlaying");
                    intent.putExtra("now", mediaPlayer.getCurrentPosition());
                    sendBroadcast(intent);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean isStart = intent.getBooleanExtra("start",false);
        int seekto = intent.getIntExtra("seekto",-1);
        if(seekto != -1 && mediaPlayer!=null){
            mediaPlayer.seekTo(seekto);
        }else if(isStart){
            if(!mediaPlayer.isPlaying()) mediaPlayer.start();
        }else{
            if(mediaPlayer!=null && mediaPlayer.isPlaying()) mediaPlayer.pause();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if(timer!=null){
            timer.cancel();
            timer.purge();
            timer = null;
        }

        Log.v("brad","MyService:onDestroy()");
    }
}
