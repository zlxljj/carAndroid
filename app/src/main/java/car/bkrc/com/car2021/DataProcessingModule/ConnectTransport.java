package car.bkrc.com.car2021.DataProcessingModule;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import org.greenrobot.eventbus.EventBus;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import car.bkrc.com.car2021.FragmentView.RightAutoFragment;
import car.bkrc.com.car2021.R;
import car.bkrc.com.car2021.Utils.BitmapUtils;
import car.bkrc.com.car2021.Utils.CameraUtile.XcApplication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import car.bkrc.com.car2021.MessageBean.DataRefreshBean;
import car.bkrc.com.car2021.Utils.CarPlate.carPlate;
import car.bkrc.com.car2021.Utils.CarRgbLight.CarRgbLight;
import car.bkrc.com.car2021.Utils.CarShape.CarShape;
import car.bkrc.com.car2021.Utils.OtherUtil.SerialPort;

import car.bkrc.com.car2021.ActivityView.FirstActivity;
import car.bkrc.com.car2021.FragmentView.LeftFragment;
import car.bkrc.com.car2021.FragmentView.RightAutoFragment;
import car.bkrc.com.car2021.Utils.TrafficRecUtil.ColorRecognition;
import car.bkrc.com.car2021.ViewAdapter.OtherAdapter;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.RETR_EXTERNAL;
import static car.bkrc.com.car2021.ActivityView.FirstActivity.Connect_Transport;
import static car.bkrc.com.car2021.ActivityView.FirstActivity.IPCamera;
import static car.bkrc.com.car2021.ActivityView.FirstActivity.toastUtil;
import static car.bkrc.com.car2021.FragmentView.LeftFragment.cameraCommandUtil;
import static car.bkrc.com.car2021.FragmentView.RightAutoFragment.QrFlag;
import static car.bkrc.com.car2021.FragmentView.RightAutoFragment.result_qr;
import static car.bkrc.com.car2021.FragmentView.RightAutoFragment.rgb_result;
import static car.bkrc.com.car2021.FragmentView.RightAutoFragment.shapeNum;
import static car.bkrc.com.car2021.Utils.CarShape.CarShape.dis_x;
import static car.bkrc.com.car2021.Utils.CarShape.CarShape.dis_y;
import static car.bkrc.com.car2021.Utils.CarShape.CarShape.x_max;
import static car.bkrc.com.car2021.Utils.CarShape.CarShape.x_min;
import static car.bkrc.com.car2021.Utils.CarShape.CarShape.y_max;
import static car.bkrc.com.car2021.Utils.CarShape.CarShape.y_min;

/**
 * Socket???????????????
 */
public class ConnectTransport {
    public static DataInputStream bInputStream = null;
    public static DataOutputStream bOutputStream = null;
    public static Socket socket = null;
    public byte[] rbyte = new byte[50];
    private Handler reHandler;
    public short TYPE = 0xAA;
    public short TYPE2 = 0xBB;
    public short MAJOR = 0x00;
    public short FIRST = 0x00;
    public short SECOND = 0x00;
    public short THRID = 0x00;
    public short CHECKSUM = 0x00;

    public static RightAutoFragment RightAutoFragment;
    public static ColorRecognition ColorRecognition;
    public static LeftFragment left_Fragment;//?????????;
    public static QR_Recognition qr_recognition;
    public  OtherAdapter OtherAdapter;
    public static CarShape carShape;
    public static carPlate carPlate;
    public static CarRgbLight CarRgbLight;

    private static OutputStream SerialOutputStream;
    private InputStream SerialInputStream;
    private boolean Firstdestroy = false;  ////Firstactivity ??????????????????


    public void destory() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                bInputStream.close();
                bOutputStream.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void connect(Handler reHandler, String IP) {
        try {
            this.reHandler = reHandler;
            Firstdestroy = false;
            int port = 60000;
            socket = new Socket(IP, port);
            bInputStream = new DataInputStream(socket.getInputStream());
            bOutputStream = new DataOutputStream(socket.getOutputStream());
            if (!inputDataState) {
                reThread();
            }
            EventBus.getDefault().post(new DataRefreshBean(3));
        } catch (SocketException ignored) {
            EventBus.getDefault().post(new DataRefreshBean(4));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



    public void serial_connect(Handler reHandler) {
        this.reHandler = reHandler;
        try {
            int baudrate = 115200;
            String path = "/dev/ttyS4";
            SerialPort mSerialPort = new SerialPort(new File(path), baudrate, 0);
            SerialOutputStream = mSerialPort.getOutputStream();
            SerialInputStream = mSerialPort.getInputStream();
            //new Thread(new SerialRunnable()).start();
            //reThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        XcApplication.executorServicetor.execute(new SerialRunnable());
        //new Thread(new serialRunnable()).start();
    }

    byte[] serialreadbyte = new byte[50];

    class SerialRunnable implements Runnable {
        @Override
        public void run() {
            while (SerialInputStream != null) {
                try {
                    int num = SerialInputStream.read(serialreadbyte);
                    // String  readserialstr =new String(serialreadbyte);
                    String readserialstr = new String(serialreadbyte, 0, num, "utf-8");
                    Log.e("----serialreadbyte----", "******" + readserialstr);
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = serialreadbyte;
                    reHandler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean inputDataState = false;

    private void reThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto1-generated method stub
                while (socket != null && !socket.isClosed()) {
                    if (Firstdestroy == true)  //Firstactivity ????????????
                    {
                        break;
                    }
                    try {
                        inputDataState = true;
                        bInputStream.read(rbyte);
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = rbyte;//?????????????????????
                        reHandler.sendMessage(msg);
                    } catch (SocketException ignored) {
                        EventBus.getDefault().post(new DataRefreshBean(4));
                        destory();
                        inputDataState = false;
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        EventBus.getDefault().post(new DataRefreshBean(4));
                        destory();
                        inputDataState = false;
                    } catch (UnsupportedOperationException ignored) {
                        inputDataState = false;
                    }
                }
            }
        }).start();//????????????
    }



    private void send() {
        CHECKSUM = (short) ((MAJOR + FIRST + SECOND + THRID) % 256);
        // ????????????????????????

        final byte[] sbyte = {0x55, (byte) TYPE, (byte) MAJOR, (byte) FIRST, (byte) SECOND, (byte) THRID, (byte) CHECKSUM, (byte) 0xBB};

        if (XcApplication.isserial == XcApplication.Mode.SOCKET) {
            XcApplication.executorServicetor.execute(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        if (socket != null && !socket.isClosed()) {
                            bOutputStream.write(sbyte, 0, sbyte.length);
                            bOutputStream.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (XcApplication.isserial == XcApplication.Mode.SERIAL) {

            XcApplication.executorServicetor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        SerialOutputStream.write(sbyte, 0, sbyte.length);
                        SerialOutputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (XcApplication.isserial == XcApplication.Mode.USB_SERIAL)
            try {
                FirstActivity.sPort.write(sbyte, 5000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException ignored) {

            }
    }


    public Handler cameraSet = new Handler(){//???????????????
        @Override
        public void handleMessage(@NonNull Message msg) {
            XcApplication.executorServicetor.execute(new Runnable() {
                public void run() {
                    switch (msg.what) {
                        //??????????????????
                        case 1:
                            LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 0, 1);  //??????
                            break;
                        case 2:
                            LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 2, 1);  //??????
                            break;
                        case 3:
                            LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 4, 1);  //??????
                            break;
                        case 4:
                            LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 6, 1);  //??????
                            break;
                        case 5:
                            LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 32, 0); // ???????????????1
                            break;
                        case 6:
                            LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 33, 0); // ???????????????1
                            break;
                        case 7:
                            LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 31, 0); // ???????????????
                            break;
                        case 8:
                            LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 36, 0); // ???????????????2
                            break;
                        case 9:
                            LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 37, 0); // ???????????????2
                            break;
                        case 10:
                            LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 38, 0); // ???????????????3
                            break;
                        case 11:
                            LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 39, 0); // ???????????????3
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    };



    private void sendSecend() {
        CHECKSUM = (short) ((MAJOR + FIRST + SECOND + THRID) % 256);
        // ????????????????????????

        final byte[] sbyte = {0x55, (byte) TYPE2, (byte) MAJOR, (byte) FIRST, (byte) SECOND, (byte) THRID, (byte) CHECKSUM, (byte) 0xBB};

        if (XcApplication.isserial == XcApplication.Mode.SOCKET) {
            XcApplication.executorServicetor.execute(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        if (socket != null && !socket.isClosed()) {
                            bOutputStream.write(sbyte, 0, sbyte.length);
                            bOutputStream.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (XcApplication.isserial == XcApplication.Mode.SERIAL) {

            XcApplication.executorServicetor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        SerialOutputStream.write(sbyte, 0, sbyte.length);
                        SerialOutputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (XcApplication.isserial == XcApplication.Mode.USB_SERIAL)
            try {
                FirstActivity.sPort.write(sbyte, 5000);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void send_voice(final byte[] textbyte) {
        if (XcApplication.isserial == XcApplication.Mode.SOCKET) {
            XcApplication.executorServicetor.execute(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        if (socket != null && !socket.isClosed()) {
                            bOutputStream.write(textbyte, 0, textbyte.length);
                            bOutputStream.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (XcApplication.isserial == XcApplication.Mode.SERIAL) {

            XcApplication.executorServicetor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        SerialOutputStream.write(textbyte, 0, textbyte.length);
                        SerialOutputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (XcApplication.isserial == XcApplication.Mode.USB_SERIAL)
            try {
                FirstActivity.sPort.write(textbyte, 5000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException ignored) {
                Log.e("UART:", "unline");
            }
    }


    // ??????
    public void go(int sp_n, int en_n) {
        MAJOR = 0x02;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = (byte) (en_n & 0xff);
        THRID = (byte) (en_n >> 8);
        send();
    }

    // ??????
    public void back(int sp_n, int en_n) {
        MAJOR = 0x03;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = (byte) (en_n & 0xff);
        THRID = (byte) (en_n >> 8);
        send();
    }


    //??????
    public void left(int sp_n) {
        MAJOR = 0x04;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = (byte) 0x00;
        THRID = (byte) 0x00;
        send();
    }


    // ??????
    public void right(int sp_n) {
        MAJOR = 0x05;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = (byte) 0x00;
        THRID = (byte) 0x00;
        send();
    }

    // ??????
    public void stop() {
        MAJOR = 0x01;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }
    public static byte[] qr_resultByt;
    public static String[] qr_resultArr;
    public static String rec_result = "red";
    public static String qr_result = "???????????????";
    public static int[] Pic=null;
    private Message message;
    // ??????????????????
    public static int mark = 0;
    public void autoDrive() {
          switch(mark)
        {

            case 1://???????????????
                RightAutoFragment = new RightAutoFragment();
                RightAutoFragment.cleanflag();
                CarShape.ret_x=0;
                CarShape.ret_y=0;
                CarShape.dis_x=0;
                CarShape.dis_y=0;
                rgbReadNum = 0;
                task_handler.sendEmptyMessageDelayed(1, 100);//????????????????????????opencv
                mark=0;
                break;
            case 2://???????????????
                RightAutoFragment = new RightAutoFragment();
                RightAutoFragment.cleanflag();
                //RightAutoFragment.QrFlag=true;
                message = new Message();
                message.what = 2;
                task_handler.sendMessage(message);//???????????????
                mark=0;
                break;
            case 3://????????????
                RightAutoFragment = new RightAutoFragment();
                RightAutoFragment.cleanflag();
                CarShape.ret_x=0;
                CarShape.ret_y=0;
                CarShape.dis_x=0;
                CarShape.dis_y=0;
                shapeReadNum=0;
                message = new Message();
                message.what = 3;
                task_handler.sendMessage(message);//??????????????????????????????opencv
                mark=0;
                break;

            case 4://????????????
                RightAutoFragment = new RightAutoFragment();
                RightAutoFragment.cleanflag();
                CarShape.ret_x=0;
                CarShape.ret_y=0;
                CarShape.dis_x=0;
                CarShape.dis_y=0;
                shapeReadNum=0;
                message = new Message();
                message.what = 4;
                task_handler.sendMessage(message);//??????????????????????????????opencv
                mark=0;
                break;


            case 20:
                RightAutoFragment = new RightAutoFragment();
                RightAutoFragment.cleanflag();
                task_handler.sendEmptyMessageDelayed(20, 100);//????????????????????????opencv
                mark=0;
                break;
            default:
                break;
        }

    }

    public short[]  qr_resultSho;
    public byte[] order_data = new byte[6];
    public void Sw_algorithm(int num,String Qrvalue)//????????????
    {
        switch(num)
        {
            case 1:				//RSA
                new algorithm().RSA_Code(Qrvalue,order_data);
                break;
            case 2:				//CRC
                new algorithm().CRC_Code(Qrvalue,order_data);
                break;
            case 3:				//?????????
                new algorithm().Affine(Qrvalue,order_data);
                break;
        }
    }

    private int imgReadNum = 0;
    private int shapeReadNum = 0;
    private int rgbReadNum = 0;
    private int QRReadNum = 0;

    private Bitmap image1 = null;
    private Bitmap image2 = null;
    private Bitmap image3 = null;
    private Bitmap bitmapRgb = null;


    private int picShapeNum=0;

    private int rednum,greennum,bluenum;
    private String shapedata;
    private String[] shapedataarr;
    private short[] shapedata_sho;
    Message msg = new Message();
    @SuppressLint("HandlerLeak")
    public Handler task_handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what)
            {
                case 1://???????????????:???????????????????????????????????????????????????????????????????????????
                    int[] Point=null;
                    image1 = left_Fragment.bitmap.copy(ARGB_8888, true);
                    bitmapRgb = left_Fragment.bitmap.copy(ARGB_8888, true);
                    CarRgbLight = new CarRgbLight();
                    Point=CarRgbLight.HoughCircles(image1);
                    int getCircle=5;
                    while(getCircle!=0){
                        if (CarRgbLight.HoughCircles_num<3){
                            Point=CarRgbLight.HoughCircles(image1);
                            getCircle--; //????????????
                        }
                        else{
                            getCircle=0;
                        }
                    }
                    yanchi(500);
                    traffic_control(0x0E, 0x01, 0x00);//????????????????????????????????????
                    yanchi(500);
                    traffic_control(0x0E, 0x01, 0x00);//????????????????????????????????????
                    yanchi(500);
                    traffic_control(0x0E, 0x01, 0x00);//????????????????????????????????????
                    yanchi(1000);
                    Log.d("rgb","?????????????????????");
                    Bitmap  bitmapRgbsrc= left_Fragment.bitmap.copy(ARGB_8888, true);
                    RightAutoFragment.image_show.setImageBitmap(bitmapRgbsrc);//??????
                    Log.d("rgb","???????????????"+ "x:"+Point[0]+"y:"+ Point[1]+ "disx:"+Point[2]+"disy:"+ Point[3]);
                    CarRgbLight=new CarRgbLight();
                    Bitmap  bitmapRgbdst=CarRgbLight.cutRgbPic(bitmapRgbsrc,Point[0],Point[1],Point[2],Point[3]);
                    RightAutoFragment.rec_image_show.setImageBitmap( bitmapRgbdst);//??????
                    BitmapUtils.saveBitmap(bitmapRgbdst,"/DCIM/Car/",".png");//????????????
                    CarRgbLight.trfficLight( bitmapRgbdst);//????????????????????????????????????
                    rec_result=CarRgbLight.trffictResult;//????????????????????????res

                    Log.d("rgb","??????????????????"+rec_result);
                    switch (rec_result)
                    {
                        case "red":
                            yanchi(500);
                            traffic_control(0x0E ,0x02,0x01);
                            yanchi(500);
                            traffic_control(0x0E ,0x02,0x01);
                            Log.d("rgb", "????????????red");
                            break;
                        case "green":
                            yanchi(500);
                            traffic_control(0x0E ,0x02,0x02);
                            yanchi(500);
                            traffic_control(0x0E ,0x02,0x02);
                            Log.d("rgb", "????????????green");
                            break;

                        case "yellow":
                            yanchi(500);
                            traffic_control(0x0E ,0x02,0x03);
                            yanchi(500);
                            traffic_control(0x0E ,0x02,0x03);
                            Log.d("rgb", "????????????yellow");
                            break;

                        default:
                            yanchi(500);
                            traffic_control(0x0E ,0x02,0x01);//????????????????????????????????????
                            yanchi(500);
                            traffic_control(0x0E ,0x02,0x01);//????????????????????????????????????
                            break;
                    }

                    break;

                case 2://???????????????
                    if (QRReadNum<5){
                        RightAutoFragment.QrFlag=true;
                        RightAutoFragment.QRRecon();
                        qr_result= RightAutoFragment.result_qr;
                        QRReadNum++;
                        if (qr_result!=null){
                            QRReadNum=6;
                        }
                        task_handler.sendEmptyMessageDelayed(2, 500);//????????????case2
                    }
                    else{
                        RightAutoFragment.QrFlag=false;
                        if (qr_result!=null) {
                            Log.d("qr", "?????????????????????1"+qr_result);
                            qr_result = qr_result.substring(1, qr_result.length() - 1);//???????????????????????????
                            qr_result=qr_result.replace("0x","");//?????????????????????0x
                            //qr_result=qr_result.replace(",","");//????????????????????????
                           // qr_result=qr_result.replace(",","");
                            qr_resultByt=qr_result.getBytes();//??????????????????
                            qr_resultArr = qr_result.split(",");//?????????????????????,??????????????????

                            int[] num=new int[qr_resultArr.length];
                            try {
                                for (int i=0;i<qr_resultArr.length;i++)
                                {
                                    num[i] = algorithm.OxStringtoInt(qr_resultArr[i]);//???16?????????????????????10?????????int
                                    // qr_resultSho[i]=Short.valueOf(algorithm.OxStringtoInt(qr_resultArr[i])+"");//int??????short
                                }
                                qr_resultSho=new short[num.length];
                                qr_resultSho=algorithm.shortint2hex(num);
                                Log.d("auto", "shortnum1 = " + Short.valueOf(algorithm.OxStringtoInt(qr_resultArr[0])+""));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Log.d("qr", "?????????????????????"+qr_result);
                            TYPE=0xAA;
                            MAJOR = 0x10;
                            FIRST = qr_resultSho[0];//????????????????????????????????????????????????BYTE?????????????????????????????????
                            SECOND = qr_resultSho[1];
                            THRID = qr_resultSho[2];
                            send();
                            Log.d("qr", "??????????????????");
                            yanchi(500);
                            MAJOR = 0x11;
                            FIRST = qr_resultSho[3];//????????????????????????????????????????????????BYTE?????????????????????????????????
                            SECOND =qr_resultSho[4];
                            THRID = qr_resultSho[5];
                            send();
                            Log.d("qr", "??????????????????");
                        }
                    }

                    //Sw_algorithm(2,result_qr);					// ?????????????????????
                    //qr_result="{0x03,0x05,0x14,0x45,0xDE,0x92}";

                    break;
                case 3:
                    Log.d("shape","??????????????????"+imgReadNum);
                    if (shapeReadNum<4) {
                        switch (shapeReadNum)//??????tft??????????????????????????????????????????retx???rety???disx???disy?????????
                        {
                            case 0:
                                image1 = left_Fragment.bitmap.copy(ARGB_8888, true);//?????????????????????
                                break;
                            case 1:
                                image2 = left_Fragment.bitmap.copy(ARGB_8888, true);//???????????????????????????????????????
                               // CarShape.autoCut(image1, image2);//??????????????????????????????????????????????????????
                                break;
                            case 2:
                                image1 = left_Fragment.bitmap.copy(ARGB_8888, true);//????????????????????????
                                CarShape.autoCut(image1, image2);//??????????????????????????????
                                break;
                            case 3:
                                image2 = left_Fragment.bitmap.copy(ARGB_8888, true);//????????????????????????
                                CarShape.autoCut(image1, image2);//??????????????????????????????
                                break;
                        }
                        shapeReadNum++;
                        tftDown();//??????????????????????????????
                        task_handler.sendEmptyMessageDelayed(3, 2500);//????????????case3
                    }
                else {//????????????????????????????????????????????????
                        if(imgReadNum<7){
                            imgReadNum++;
                            carShape=new CarShape();
                           // long current = System.currentTimeMillis();
                            Bitmap bitmapShape = left_Fragment.bitmap.copy(ARGB_8888, true);//????????????????????????
                            Bitmap bitmap_opencv_cut = CarShape.opencvCutmap( bitmapShape,CarShape.ret_x,CarShape.ret_y,CarShape.dis_x,CarShape.dis_y);;//??????????????????????????????
                            RightAutoFragment.image_show.setImageBitmap(bitmap_opencv_cut);//????????????????????????
                            carShape.colorAndShape(bitmap_opencv_cut);//??????????????????????????????????????????
                            //Log.d("auto time",(System.currentTimeMillis()-current)+"" );
                            RightAutoFragment.rec_image_show.setImageBitmap(CarShape.rebitmap_opencv);//??????????????????
                            if ( carShape.shapeResultNum>9)
                            {
                                picShapeNum=imgReadNum;
                                rednum=carShape.red_num;
                                greennum=carShape.green_num;
                                bluenum=carShape.blue_num;
                            }
                            tftDown();//??????????????????????????????
                            task_handler.sendEmptyMessageDelayed(3, 2000);//????????????case3
                        }else{
                            imgReadNum = 0;
                            shapedata="F"+rednum+","+"F"+greennum+","+"F"+bluenum;
                            shapedataarr=shapedata.split(",");
                            int[] num=new int[shapedataarr.length];
                            try {
                                for (int i=0;i<shapedataarr.length;i++)
                                {
                                    num[i] = algorithm.OxStringtoInt(shapedataarr[i]);//???16?????????????????????10?????????int
                                }
                                shapedata_sho=new short[num.length];
                                shapedata_sho=algorithm.shortint2hex(num);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            TYPE=0xAA;
                            MAJOR = 0x40;
                            FIRST =  shapedata_sho[0];//????????????????????????????????????????????????BYTE?????????????????????????????????
                            SECOND =  shapedata_sho[1];
                            THRID =  shapedata_sho[2];
                            send();
                            yanchi(100);
                            send();
                        }
                    }

                    break;

                case 4:
                    Log.d("auto","??????????????????");
                    if (shapeReadNum<3) {
                        switch (shapeReadNum)//??????tft??????????????????????????????????????????retx???rety???disx???disy?????????
                        {
                            case 0:
                                image1 = left_Fragment.bitmap.copy(ARGB_8888, true);//?????????????????????
                                break;
                            case 1:
                                image2 = left_Fragment.bitmap.copy(ARGB_8888, true);//???????????????????????????????????????
                                CarShape.autoCut(image1, image2);//??????????????????????????????????????????????????????
                                break;
                            case 2:
                                image1 = left_Fragment.bitmap.copy(ARGB_8888, true);//????????????????????????
                                CarShape.autoCut(image1, image2);//??????????????????????????????
                                break;
                        }
                        shapeReadNum++;
                        tftDown();//??????????????????????????????
                        task_handler.sendEmptyMessageDelayed(4, 2000);//????????????case3
                    }
                    else {//????????????????????????????????????????????????
                        if(imgReadNum<1){
                            imgReadNum++;
                            carShape=new CarShape();
                            carPlate=new carPlate();
                            // long current = System.currentTimeMillis();
                            Bitmap bitmapShape = left_Fragment.bitmap.copy(ARGB_8888, true);//????????????????????????
                            Bitmap bitmap_opencv_cut = CarShape.opencvCutmap( bitmapShape,CarShape.ret_x,CarShape.ret_y,CarShape.dis_x,CarShape.dis_y);;//??????????????????????????????
                            RightAutoFragment.image_show.setImageBitmap(bitmap_opencv_cut);//????????????????????????
                            carPlate.plateDetector(bitmap_opencv_cut);
                            //RightAutoFragment.rec_image_show.setImageBitmap(CarShape.rebitmap_opencv);//??????????????????
//                            if ( carShape.shapeResultNum>9)
//                            {
//                                picShapeNum=imgReadNum;
//                                rednum=carShape.red_num;
//                                greennum=carShape.green_num;
//                                bluenum=carShape.blue_num;
//                            }
                            tftDown();//??????????????????????????????
                            task_handler.sendEmptyMessageDelayed(4, 2000);//????????????case3
                        }else{
//                            imgReadNum = 0;
//                            shapedata="F"+rednum+","+"F"+greennum+","+"F"+bluenum;
//                            shapedataarr=shapedata.split(",");
//                            int[] num=new int[shapedataarr.length];
//                            try {
//                                for (int i=0;i<shapedataarr.length;i++)
//                                {
//                                    num[i] = algorithm.OxStringtoInt(shapedataarr[i]);//???16?????????????????????10?????????int
//                                }
//                                shapedata_sho=new short[num.length];
//                                shapedata_sho=algorithm.shortint2hex(num);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            TYPE=0xAA;
//                            MAJOR = 0x40;
//                            FIRST =  shapedata_sho[0];//????????????????????????????????????????????????BYTE?????????????????????????????????
//                            SECOND =  shapedata_sho[1];
//                            THRID =  shapedata_sho[2];
//                            send();
                        }
                    }

                    break;

                case 30://???????????????:?????????????????????????????????
                    if (rgbReadNum<3) {//?????????????????????
                        switch (rgbReadNum)//??????tft??????????????????????????????????????????retx???rety???disx???disy?????????
                        {
                            case 0:
                                image1 = left_Fragment.bitmap.copy(ARGB_8888, true);//?????????????????????
                                Log.d("rgb", "?????????????????????" );
                                break;
                            case 1:
                                image2 = left_Fragment.bitmap.copy(ARGB_8888, true);//???????????????????????????????????????
                                Log.d("rgb", "?????????????????????" );
                                break;
                            case 2:
                                image3 = left_Fragment.bitmap.copy(ARGB_8888, true);//????????????????????????
                                Log.d("rgb", "?????????????????????" );
                                break;
                        }
                        rgbReadNum++;
                        task_handler.sendEmptyMessageDelayed(1, 600);//????????????case3
                    }
                    else {
                        if (rgbReadNum < 6)//???????????????3????????????????????????????????????????????????????????????
                        {
                            Mat src1 = new Mat();
                            Mat src2 = new Mat();
                            Mat src3 = new Mat();
                            Utils.bitmapToMat(image1, src1);
                            Utils.bitmapToMat(image2, src2);
                            Utils.bitmapToMat(image3, src3);
                            CarRgbLight = new CarRgbLight();
                            CarRgbLight.imageSubtract3(src1, src2, src3);//?????????????????????????????????????????????????????????
                            Log.d("rgb", "?????????" + rgbReadNum + "?????????");
                            switch (rgbReadNum) {
                                case 3:
                                    image1 = left_Fragment.bitmap.copy(ARGB_8888, true);
                                    src1 = new Mat();
                                    Utils.bitmapToMat(image1, src1);
                                    break;
                                case 4:
                                    image2 = left_Fragment.bitmap.copy(ARGB_8888, true);
                                    src2 = new Mat();
                                    Utils.bitmapToMat(image2, src2);
                                    break;
                                case 5:
                                    image3 = left_Fragment.bitmap.copy(ARGB_8888, true);
                                    src3 = new Mat();
                                    Utils.bitmapToMat(image3, src3);
                                    break;
                                case 6:
                                    image1 = left_Fragment.bitmap.copy(ARGB_8888, true);
                                    src3 = new Mat();
                                    Utils.bitmapToMat(image1, src1);
                                    break;
                                case 7:
                                    image2 = left_Fragment.bitmap.copy(ARGB_8888, true);
                                    src3 = new Mat();
                                    Utils.bitmapToMat(image2, src2);
                                    break;
//                                case 8:
//                                    image3 = left_Fragment.bitmap.copy(ARGB_8888, true);
//                                    src3 = new Mat();
//                                    Utils.bitmapToMat(image3, src3);
//                                    break;
                                default:
                                    break;
                            }
                            rgbReadNum++;
                            task_handler.sendEmptyMessageDelayed(1, 600);//????????????case3
                        }
                        else {
                            //3??????????????????????????????????????????????????????
                            rgbReadNum = 0;
                            traffic_control(0x0E, 0x01, 0x00);//?????????????????????????????????????????????????????????
                            Log.d("rgb","?????????????????????");
                            yanchi(500);
                            traffic_control(0x0E, 0x01, 0x00);//????????????????????????????????????
                            yanchi(500);
                            bitmapRgb = left_Fragment.bitmap.copy(ARGB_8888, true);
                            Log.d("rgb","???????????????"+ "x:"+CarShape.ret_x+"y:"+ CarShape.ret_y+ "disx:"+dis_x+"disy:"+ dis_y);
                            Bitmap res = CarRgbLight.cutRgbPic(bitmapRgb, CarShape.ret_x, CarShape.ret_y, CarShape.dis_x, CarShape.dis_y);//????????????
                            RightAutoFragment.rec_image_show.setImageBitmap(res);//??????
                            CarRgbLight.trfficLight(res);//????????????????????????????????????
                            rec_result=CarRgbLight.trffictResult;//????????????????????????res
                            Log.d("rgb","??????????????????"+rec_result);
                            switch (rec_result)
                            {
                                case "red":
                                    traffic_control(0x0E ,0x02,0x01);

                                    Log.d("rgb", "red");
                                    break;
                                case "green":
                                    traffic_control(0x0E ,0x02,0x02);
                                    Log.d("rgb", "green");
                                    break;

                                case "yellow":
                                    traffic_control(0x0E ,0x02,0x03);
                                    Log.d("rgb", "yellow");
                                    break;

                                default:
                                    traffic_control(0x0E ,0x02,0x01);//????????????????????????????????????
                                    break;
                            }
                        }
                    }

                    break;

                default:
                    break;
            }

            super.handleMessage(msg);
        }
    };





    private Resources getResources() {
        // TODO Auto-generated method stub
        Resources mResources = null;
        mResources = getResources();
        return mResources;
    }

//????????????



    // ??????
    public void line(int sp_n) {  //??????
        MAJOR = 0x06;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    //???????????????
    public void clear() {
        MAJOR = 0x07;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    public void stateChange(final int i) {//?????????????????????
        final short temp = TYPE;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (i == 1) {//????????????
                    TYPE = 0x02;
                    MAJOR = 0x80;
                    FIRST = 0x01;
                    SECOND = 0x00;
                    THRID = 0x00;
                    send();
                    yanchi(500);

                    TYPE = (byte) 0xAA;
                    MAJOR = 0x80;
                    FIRST = 0x01;
                    SECOND = 0x00;
                    THRID = 0x00;
                    send();
                    TYPE = 0x02;
                } else if (i == 2) {// ????????????
                    TYPE = 0x02;
                    MAJOR = 0x80;
                    FIRST = 0x00;
                    SECOND = 0x00;
                    THRID = 0x00;
                    send();
                    yanchi(500);

                    TYPE = (byte) 0xAA;
                    MAJOR = 0x80;
                    FIRST = 0x00;
                    SECOND = 0x00;
                    THRID = 0x00;
                    send();
                    TYPE = 0xAA;
                }
                TYPE = temp;
            }
        }).start();
    }

    // ??????
    public void infrared(final byte one, final byte two, final byte thrid, final byte four, final byte five,
                         final byte six) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MAJOR = 0x10;
                FIRST = one;
                SECOND = two;
                THRID = thrid;
                send();
                yanchi(500);
                MAJOR = 0x11;
                FIRST = four;
                SECOND = five;
                THRID = six;
                send();
                yanchi(500);
                MAJOR = 0x12;
                FIRST = 0x00;
                SECOND = 0x00;
                THRID = 0x00;
                send();
                yanchi(1000);
            }
        }).start();
    }

    //TFT?????????
    public void tftUp() {
        TYPE = (byte) 0xAA;
        MAJOR = 0x50;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }


    //TFT?????????
    public void tftDown() {
        TYPE = (byte) 0xAA;
        MAJOR = 0x51;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    // ??????led???
    public void lamp(byte command) {
        MAJOR = 0x40;
        FIRST = command;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    // ?????????
    public void light(int left, int right) {
        if (left == 1 && right == 1) {
            MAJOR = 0x20;
            FIRST = 0x01;
            SECOND = 0x01;
            THRID = 0x00;
            send();
        } else if (left == 1 && right == 0) {
            MAJOR = 0x20;
            FIRST = 0x01;
            SECOND = 0x00;
            THRID = 0x00;
            send();
        } else if (left == 0 && right == 1) {
            MAJOR = 0x20;
            FIRST = 0x00;
            SECOND = 0x01;
            THRID = 0x00;
            send();
        } else if (left == 0 && right == 0) {
            MAJOR = 0x20;
            FIRST = 0x00;
            SECOND = 0x00;
            THRID = 0x00;
            send();
        }
    }


    // ?????????
    public void buzzer(int i) {
        if (i == 1)
            FIRST = 0x01;
        else if (i == 0)
            FIRST = 0x00;
        MAJOR = 0x30;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    /**
     * ?????????????????????
     */
    public void qr_rec(int state) {
        FIRST = 0x92;
        MAJOR = (byte) state;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    public void gear(int i) {// ???????????????
        if (i == 1)
            MAJOR = 0x61;
        else if (i == 2)
            MAJOR = 0x62;
        else if (i == 3)
            MAJOR = 0x63;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    //????????????
    public void infrared_stereo(final short[] data) {
        MAJOR = 0x10;
        FIRST = 0xff;
        SECOND = data[0];
        THRID = data[1];
        send();
        yanchi(500);
        MAJOR = 0x11;
        FIRST = data[2];
        SECOND = data[3];
        THRID = data[4];
        send();
        yanchi(500);
        MAJOR = 0x12;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        yanchi(700);
    }

    //????????????
    public void infrared_dis(final short[] data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MAJOR = 0x10;
                FIRST = 0xff;
                SECOND = data[0];
                THRID = data[1];
                send();
                yanchi(500);
                MAJOR = 0x11;
                FIRST = data[2];
                SECOND = data[3];
                THRID = data[4];
                send();
                yanchi(500);
                MAJOR = 0x12;
                FIRST = 0x00;
                SECOND = 0x00;
                THRID = 0x00;
                send();
                yanchi(500);
            }
        }).start();
    }


    //???????????????
    public void traffic_control(int type, int major, int first) {
        byte temp = (byte) TYPE;
        TYPE = (short) type;
        MAJOR = (byte) major;
        FIRST = (byte) first;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        TYPE = temp;
    }

    /**
     * ??????????????????
     *
     * @param major ????????????
     * @param first ????????????
     */
    public void rudder_control(int major, int first) {
        byte temp = (byte) TYPE;
        TYPE = (short) 0x0C;
        MAJOR = (byte) 0x08;
        FIRST = (byte) major;
        SECOND = (byte) first;
        THRID = 0x00;
        send();
        TYPE = temp;
    }

    //??????????????????
    public void garage_control(int type, int major, int first) {
        byte temp = (byte) TYPE;
        TYPE = (short) type;
        MAJOR = (byte) major;
        FIRST = (byte) first;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        TYPE = temp;
    }

    public void gate(int major, int first, int second, int third) {// ??????
        byte temp = (byte) TYPE;
        TYPE = 0x03;
        MAJOR = (byte) major;
        FIRST = (byte) first;
        SECOND = (byte) second;
        THRID = (byte) third;
        send();
        TYPE = temp;
    }

    //LCD ?????????????????????????????????
    public void digital_close() {//???????????????
        byte temp = (byte) TYPE;
        TYPE = 0x04;
        MAJOR = 0x03;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        TYPE = temp;
    }

    public void digital_open() {//???????????????
        byte temp = (byte) TYPE;
        TYPE = 0x04;
        MAJOR = 0x03;
        FIRST = 0x01;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        TYPE = temp;
    }

    public void digital_clear() {//???????????????
        byte temp = (byte) TYPE;
        TYPE = 0x04;
        MAJOR = 0x03;
        FIRST = 0x02;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        TYPE = temp;
    }

    public void digital_dic(int dis) {//LCD????????????????????????????????????

        byte temp = (byte) TYPE;
        int a = 0, b = 0, c = 0;
        a = (dis / 100) & (0xF);
        b = (dis % 100 / 10) & (0xF);
        c = (dis % 10) & (0xF);
        b = b << 4;
        b = b | c;
        TYPE = 0x04;
        MAJOR = 0x04;
        FIRST = 0x00;
        SECOND = (short) (a);
        THRID = (short) (b);
        send();
        TYPE = temp;
    }

    public void digital(int i, int one, int two, int three) {// ?????????
        byte temp = (byte) TYPE;
        TYPE = 0x04;
        if (i == 1) {//??????????????????????????????
            MAJOR = 0x01;
            FIRST = (byte) one;
            SECOND = (byte) two;
            THRID = (byte) three;
        } else if (i == 2) {//??????????????????????????????
            MAJOR = 0x02;
            FIRST = (byte) one;
            SECOND = (byte) two;
            THRID = (byte) three;
        }
        send();
        TYPE = temp;
    }

    public void VoiceBroadcast()  //????????????????????????
    {
        byte temp = (byte) TYPE;
        TYPE = (short) 0x06;
        MAJOR = (short) 0x20;
        FIRST = (byte) 0x01;
        SECOND = (byte) 0x00;
        THRID = (byte) 0x00;
        send();
        TYPE = temp;
    }

    public void TFT_LCD(int type, int MAIN, int KIND, int COMMAD, int DEPUTY)  //tft lcd
    {
        byte temp = (byte) TYPE;
        TYPE = (short) type;
        MAJOR = (short) MAIN;
        FIRST = (byte) KIND;
        SECOND = (byte) COMMAD;
        THRID = (byte) DEPUTY;
        send();
        TYPE = temp;
    }

    public void magnetic_suspension(int MAIN, int KIND, int COMMAD, int DEPUTY) //?????????
    {
        byte temp = (byte) TYPE;
        TYPE = (short) 0x0A;
        MAJOR = (short) MAIN;
        FIRST = (byte) KIND;
        SECOND = (byte) COMMAD;
        THRID = (byte) DEPUTY;
        send();
        TYPE = temp;
    }

    // ??????
    public void yanchi(int time) {

                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
    }

}
