package com.shangtongyin.tools.serialport;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.zf.serial.SerialUtils;

public class MainActivity extends AppCompatActivity {
    private TextView tv;
    private SerialUtils serialUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv= (TextView) findViewById(R.id.tv_1);
        serialUtils=new SerialUtils("/dev/ttyS0",9600);
        serialUtils.open();
        serialUtils.setOnDataReceived(new SerialUtils.onDataReceived() {
            @Override
            public void Data(String data) {
                L.e("11",data);
                tv.setText(data);
            }
        });
        GetWeightThreadForST m=new GetWeightThreadForST();
        m.start();
    }
    private class GetWeightThreadForST extends Thread {
        public boolean isRun;
        @Override
        public synchronized void start() {
            // TODO Auto-generated method stub
            isRun = true;
            super.start();
        }

        public void stopRun() {
            isRun = false;
            interrupt();
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            while (isRun) {
                try {
                    serialUtils.resetBalance(new byte[]{0x55, (byte) 0xAA});
                    sleep(150);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
