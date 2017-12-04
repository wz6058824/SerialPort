package com.shangtongyin.tools.serialport;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.zf.serial.SerialUtils;

public class MainActivity extends AppCompatActivity {
    private TextView tv;
    private SerialUtils serialUtils;
    final byte[] PRINT_Reset = new byte[]{0x1b, 0x40};//重置
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv= (TextView) findViewById(R.id.tv_1);
        serialUtils=new SerialUtils("/dev/ttyS0",9600);
        serialUtils.open();
        //需要回调可以开启这个功能
        serialUtils.setOnDataReceived(new SerialUtils.onDataReceived() {
            @Override
            public void Data(String data) {
                L.e("11",data);
                tv.setText(data);
            }
        });
        //持续发送自己定义一个循环(打印的话就可以不需要)
        GetWeightThreadForST m=new GetWeightThreadForST();
        m.start();
        //打印测试
//        try {
//            serialUtils.resetBalance(PRINT_Reset);
//            serialUtils.resetBalance("打印测试".getBytes("GBK"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁
        serialUtils.closeSerialPort();
    }
}
