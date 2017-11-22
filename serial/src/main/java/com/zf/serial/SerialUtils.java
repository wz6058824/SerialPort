package com.zf.serial;


import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;

/**
 * Created by zf on 2017/3/11.
 */
public  class SerialUtils {

    private String path="/dev/ttyS0";
    private int baudrate=9600;
    private final int TIMERDELAY = 150;
    private int READDELAY = 50;
    private SerialPort mSerialPortBalance = null;
    ReadThread mThread;
    private boolean isRun=true;
    public SerialUtils(String path, int baudrate){
        this.path=path;
        this.baudrate=baudrate;
    }
    public boolean open(){
        try {
            mSerialPortBalance = getSerialPortPrinter();
            if(mSerialPortBalance.getOutputStream()==null||mSerialPortBalance.getInputStream()==null) {
                return false;
            }
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }
    public SerialPort getSerialPortPrinter() throws SecurityException,
            IOException, InvalidParameterException {
        if (mSerialPortBalance == null) {
            mSerialPortBalance = new SerialPort(new File(path), baudrate, 0);
        }
        return mSerialPortBalance;
    }
    //关闭
    public void closeSerialPort() {
        if (mSerialPortBalance != null) {
            mSerialPortBalance.close();
            mSerialPortBalance = null;
        }
        mSerialPortBalance = null;
        onDataReceived=null;
        if (mThread != null) {
            mThread.interrupt();
        }
    }

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (isRun) {
                int size;
                try {
                    byte[] buffer = new byte[64];
                    if (mSerialPortBalance == null) {
                        return;
                    }
                    /**
                     * 这里的read要尤其注意，它会一直等待数据，等到天荒地老，海枯石烂。如果要判断是否接受完成，只有设置结束标识，
                     * 或作其他特殊的处理。
                     */
                    size = mSerialPortBalance.getInputStream().read(buffer);
                    if (size > 0) {
                        final String str = new String(buffer, 0, size).trim();
                        Log.e("str",str);
                        if(onDataReceived!=null){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onDataReceived.Data(str);
                                }
                            });

                        }
                    }
                    Thread.sleep(READDELAY);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    private Handler handler;
    public void setOnDataReceived(SerialUtils.onDataReceived onDataReceived) {
        if(mThread==null) {
            this.onDataReceived = onDataReceived;
            isRun = true;
            mThread = new ReadThread();
            mThread.start();
            handler=new Handler();
        }
    }

    private onDataReceived onDataReceived;
    public interface onDataReceived{
        void Data(String data);
    }




    /**
     * 传输数据
     */
    public void resetBalance(byte[] order) {
        try {
            getSerialPortPrinter().getOutputStream().write(order);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getBaudrate() {
        return baudrate;
    }

    public String getPath() {
        return path;
    }
}
