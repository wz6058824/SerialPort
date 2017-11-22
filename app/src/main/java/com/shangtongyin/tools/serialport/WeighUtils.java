package com.shangtongyin.tools.serialport;


import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;

/**
 * Created by zf on 2017/3/11.
 */
public abstract class WeighUtils {


    public int getBaudrate() {
        return baudrate;
    }

    public String getPath() {
        return path;
    }

    private String path="/dev/ttyS0";
    private int baudrate=9600;
    private final int TIMERDELAY = 150;
    private int READDELAY = 50;
    private SerialPort mSerialPortBalance = null;
    private byte[] readWeightOrder=new byte[]{0x55, (byte) 0xAA};

    ReadThread mThread;
    GetWeightThreadForST gThread;
    private boolean isRun;
    public boolean open(){
        try {
            mSerialPortBalance = getSerialPortPrinter();
            if(mSerialPortBalance.getOutputStream()==null||mSerialPortBalance.getInputStream()==null) {
                return false;
            }
            isRun = true;
            mThread = new ReadThread();
            mThread.start();
            gThread = new GetWeightThreadForST();
            gThread.start();
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
        if (gThread != null) {
            gThread.stopRun();
        }
        if (mThread != null) {
            mThread.interrupt();
        }
    }
    public class GetWeightThreadForST extends Thread {

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
                    getSerialPortPrinter().getOutputStream().write(readWeightOrder);
                    sleep(TIMERDELAY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
    private double record=0;
    private int num=0;
    public class ReadThread extends Thread {

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
                        String str = new String(buffer, 0, size).trim();
                        L.e("weight",str);
                        if (str.contains("kg")) {
                            int index = str.indexOf("kg");
                            str=str.substring(0, index).replace(" ","");
                            try {
                                double b= Double.parseDouble(str);
                                onDataReceived(b);
                            }catch (Exception e){
                            }

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
    protected abstract void onDataReceived(double d);

    public byte[] getReadWeightOrder() {
        return readWeightOrder;
    }

    public void setReadWeightOrder(byte[] readWeightOrder) {
        this.readWeightOrder = readWeightOrder;
    }

    /**
     * 置0
     */
    public void resetBalance() {
        try {
            byte[] order = new byte[]{0x55, 0x00};
            getSerialPortPrinter().getOutputStream().write(order);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 置0
     */
    public void resetBalance(byte[] order) {
        try {
            getSerialPortPrinter().getOutputStream().write(order);
            L.e("getOutputStream","getOutputStream");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setBaudrate(int baudrate) {
        this.baudrate = baudrate;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public void suspend(){
        if (gThread!=null){
            gThread.stopRun();
            gThread.interrupt();
            gThread=null;
        }
    }
    public void restart(){
        gThread=new GetWeightThreadForST();
        gThread.start();
    }
}
