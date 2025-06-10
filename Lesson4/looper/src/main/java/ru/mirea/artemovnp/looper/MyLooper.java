package ru.mirea.artemovnp.looper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class MyLooper extends Thread {
    public Handler mHandler;
    private Handler mainHandler;

    public MyLooper(Handler mainHandler) {
        this.mainHandler = mainHandler;
    }

    @Override
    public void run() {
        Looper.prepare();

        mHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                int age = msg.getData().getInt("AGE", 0);
                String job = msg.getData().getString("JOB");

                try {
                    Thread.sleep(age * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                String result = "Профессия: " + job + ", Возраст: " + age;
                Log.d("MyLooper", result);

                Message reply = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("RESULT", result);
                reply.setData(bundle);
                mainHandler.sendMessage(reply);
            }
        };

        Looper.loop();
    }
}