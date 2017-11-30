package com.xy.android.androidserialportapi;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android_serialport_api.SerialPort;

/**
 * Created by xy on 2017/11/21.
 */

public class SerialPortUtil {
    private static final String TAG = SerialPortUtil.class.getSimpleName();

    public static SerialPort serialPort = null;

    public static InputStream mInputStream = null;

    public static OutputStream mOutputStream = null;

    public static boolean flag = false;

    private static BufferedReader br;

    /**
     * 打开串口
     */

    public static void openSrialPort(String port, int baudrate) {

        Log.i(TAG, "打开串口");

        try {

            serialPort = new SerialPort(new File("/dev/" + port), baudrate, 0);

            //获取打开的串口中的输入输出流，以便于串口数据的收发

            mInputStream = serialPort.getInputStream();

            mOutputStream = serialPort.getOutputStream();

            flag = true;

            //接收串口数据

            receiveSerialPort();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    /**
     * 接收串口数据
     */

    public static void receiveSerialPort() {

        new Thread(new Runnable() {

            @Override

            public void run () {

            //循环接收串口数据

                while (flag) {

                    try {

                        if (mInputStream == null) return;

                        br = new BufferedReader(new InputStreamReader(mInputStream));

                        String str;

                        while ((str = br.readLine()) != null)

                        {

                            if (TextUtils.isEmpty(str)) continue;

                            Log.i(TAG, "接收串口数据:" + str);

                            if (String.valueOf(str.charAt(0)).equals("{") && str.substring(str.length() - 1).equals("}")) {

                                acceptAndNotify(str);

                            }

                        }

                    } catch (Exception e) {

                        e.printStackTrace();

                    }

                }

            }

        }).start();

    }

    /**
     * 区分收到的指令数据并分类分发
     *
     * @paramjsonBack收到的JSON指令
     */

    private static void acceptAndNotify(String jsonBack) {

        if (jsonBack == null || "".equals(jsonBack.trim()))

            throw new IllegalArgumentException("JsonBack is illegal, please check args ... ");

        JsonParser jsonParser = new JsonParser();

        JsonObject json = (JsonObject) jsonParser.parse(jsonBack);

        if (json == null)

            throw new JsonParseException("Json Parse error, please check args ... ");

        String protocolResult = json.getAsJsonPrimitive("protocol").getAsString();

        if (protocolResult == null || "".equals(protocolResult))

            throw new NumberFormatException("转化错误... ");

        switch (protocolResult) {

            case "coin_in":

//                EventBus.getDefault().post(new SerialPortEvent(GsonUtil.parse(jsonBack, CoinAndRemoteCoinBean.class)));

                break;

            case "remote_coin_in":

//                EventBus.getDefault().post(new SerialPortEvent(GsonUtil.parse(jsonBack, CoinAndRemoteCoinBean.class)));

                break;

            case "key_event":

//                EventBus.getDefault().post(new SerialPortEvent(GsonUtil.parse(jsonBack, OrientationAndKeyBean.class)));

                break;

        }

    }

    /**
     * 发送串口数据
     *
     * @paramdata要发送的数据
     */

    public static void sendSerialPort(String data) {

        Log.i(TAG, "发送串口数据：" + data);

        try {

            byte[] sendData = data.getBytes();

            mOutputStream.write(sendData);

            mOutputStream.flush();

            Log.i(TAG, "发送串口数据成功！");

        } catch (IOException e) {

            e.printStackTrace();

            Log.i(TAG, "发送串口数据失败！");

        }

    }

    /**
     * 关闭串口
     * <p>
     * 关闭串口中的输入输出流
     * <p>
     * 然后将flag的值设为flag，终止接收数据线程
     */

    public static void closeSerialPort() {

        Log.i(TAG, "关闭串口");

        try {

            if (serialPort != null) {

                serialPort.close();

            }

            if (mInputStream != null) {

                mInputStream.close();

            }

            if (mOutputStream != null) {

                mOutputStream.close();

            }

            if (br != null) {

                br.close();

            }

            flag = false;

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

}
