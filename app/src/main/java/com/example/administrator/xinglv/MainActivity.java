package com.example.administrator.xinglv;

/**
 * Created by Administrator on 2017/11/20.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.administrator.xinglv.tools.GitHubApi;
import com.example.administrator.xinglv.tools.RetrofitClient;
import com.example.administrator.xinglv.tools.Tools;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


/**
 * 程序的主入口
 */
public class MainActivity extends AppCompatActivity {
    //曲线
//    //Timer任务，与Timer配套使用
//    private static TimerTask task;
    // private static MyHandler handler;
    private String UserId;//当前用户id

    private int gx;
    private static int j;

    private String url = "http://mvvideo2.meitudata.com/5785a7e3e6a1b824.mp4";
    // private static MediaPlayer player;

    private static double flag = 1;

    private List<Long> landscapeTime = new ArrayList<Long>();
    private List<Long> landscapeGx = new ArrayList<Long>();


    private List<Long> gameTime = new ArrayList<Long>();
    private List<Long> gameGx = new ArrayList<Long>();

    private String title = "pulse";
    private XYSeries series;
    private XYMultipleSeriesDataset mDataset;
    private GraphicalView chart;
    private XYMultipleSeriesRenderer renderer;
    private Context context;
    private int addX = -1;
    double addY;
    int[] xv = new int[300];
    int[] yv = new int[300];
    int[] hua = new int[]{9, 10, 11, 12, 13, 14, 13, 12, 11, 10, 9, 8, 7, 6, 7, 8, 9, 10, 11, 10, 10};

    //	private static final String TAG = "HeartRateMonitor";
    private static final AtomicBoolean processing = new AtomicBoolean(false);
    //Android手机预览控件
    private static SurfaceView preview = null;
    //    //放视屏
//    private static SurfaceView previewVideo = null;
//    private static SurfaceHolder previewVideoHolder = null;
    //预览设置信息
    private static SurfaceHolder previewHolder = null;
    //Android手机相机句柄
    private static Camera camera = null;
    //private static View image = null;
    private static TextView text = null;
    private static TextView text1 = null;
    private static TextView text2 = null;
    private WakeLock wakeLock = null;
    private static int averageIndex = 0;
    private static final int averageArraySize = 4;
    private static final int[] averageArray = new int[averageArraySize];

    /**
     * 类型枚举
     *
     * @author liuyazhuang
     */
    public static enum TYPE {
        GREEN, RED
    }

    ;
    //设置默认类型
    private static TYPE currentType = TYPE.GREEN;

    //获取当前类型
    public static TYPE getCurrent() {
        return currentType;
    }

    //心跳下标值
    private static int beatsIndex = 0;
    //心跳数组的大小
    private static final int beatsArraySize = 3;
    //心跳数组
    private static final int[] beatsArray = new int[beatsArraySize];
    //心跳脉冲
    private static double beats = 0;
    //开始时间
    private static long startTime = 0;
    //开始测试的时间
    private static long createStartTime;


    //心率
    private static int beatsAvg;
    //心率暂存第一次心率
    private static int beatsAvgOld;

    private MyVideoView videoView;
    //标记video是哪个
    private int VideoFlag = 0;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new ContextWrapper(newBase) {
            @Override
            public Object getSystemService(String name) {
                if (Context.AUDIO_SERVICE.equals(name))
                    return getApplicationContext().getSystemService(name);
                return super.getSystemService(name);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UserId=getIntent().getStringExtra("UserId");

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.fengjing);
        videoView = (MyVideoView) this.findViewById(R.id.previewVideo);

        MediaController mc = new MediaController(this);
        mc.setVisibility(View.GONE);
        videoView.setMediaController(mc);

        videoView.setMediaController(new MediaController(this));
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.requestFocus();

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.getDuration();

                if (VideoFlag == 0) {
                    beatsAvgOld = beatsAvg;
                    Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.youxi);
                    videoView.setVideoURI(uri);
                    videoView.start();
                    videoView.requestFocus();
                    VideoFlag = 1;
                    //设置好图表的样式
                    setChartSettings(renderer, "X", "Y", 0, 3000, 239, 245, Color.WHITE, Color.WHITE);
                } else {

                    Log.i("datamy", gameGx.toString());
                    Log.i("datamy", gameTime.toString());
                      getData();
                }

//                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()+"/Test_Movie.m4v");
//                videoView.setVideoURI(uri);
//                videoView.start();
//                videoView.requestFocus();
            }
        });


        initConfig();

    }


   /* class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                //        		刷新图表
                updateChart();
                super.handleMessage(msg);
                this.sendEmptyMessageDelayed(1, 40);
            }
        }

    }

    //启动定时器
    private void startTimer() {
        handler = new MyHandler();
        handler.sendEmptyMessageDelayed(1, 40);
    }
*/

    /**
     * 初始化配置
     */
    private void initConfig() {

        //曲线
        context = getApplicationContext();

        //这里获得main界面上的布局，下面会把图表画在这个布局里面
        LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout1);

        //这个类用来放置曲线上的所有点，是一个点的集合，根据这些点画出曲线
        series = new XYSeries(title);

        //创建一个数据集的实例，这个数据集将被用来创建图表
        mDataset = new XYMultipleSeriesDataset();

        //将点集添加到这个数据集中
        mDataset.addSeries(series);

        //以下都是曲线的样式和属性等等的设置，renderer相当于一个用来给图表做渲染的句柄
        int color = Color.GREEN;
        PointStyle style = PointStyle.CIRCLE;
        renderer = buildRenderer(color, style, true);

        //设置好图表的样式
        setChartSettings(renderer, "X", "Y", 0, 3000, 230, 250, Color.WHITE, Color.WHITE);

        //生成图表
        chart = ChartFactory.getLineChartView(context, mDataset, renderer);

        //将图表添加到布局中去
        layout.addView(chart, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        //这里的Handler实例将配合下面的Timer实例，完成定时更新图表的功能

        // startTimer();

        //曲线
        //获取SurfaceView控件
        preview = (SurfaceView) findViewById(R.id.preview);
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        //		image = findViewById(R.id.image);
        text = (TextView) findViewById(R.id.text);
        text1 = (TextView) findViewById(R.id.text1);
        text2 = (TextView) findViewById(R.id.text2);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
    }

    //	曲线
    @Override
    public void onDestroy() {
        previewHolder.getSurface().release();
        videoView = null;
        preview = null;
        previewHolder = null;
        // handler.removeCallbacksAndMessages(null);
        // handler = null;
        System.gc();
        super.onDestroy();

    }

    ;

    /**
     * 创建图表
     *
     * @param color
     * @param style
     * @param fill
     * @return
     */
    protected XYMultipleSeriesRenderer buildRenderer(int color, PointStyle style, boolean fill) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

        //设置图表中曲线本身的样式，包括颜色、点的大小以及线的粗细等
        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(Color.RED);
        r.setFillPoints(true);
//		r.setPointStyle(null);
//		r.setFillPoints(fill);
        r.setLineWidth(1);
        renderer.addSeriesRenderer(r);
        return renderer;
    }

    /**
     * 设置图标的样式
     *
     * @param renderer
     * @param xTitle：x标题
     * @param yTitle：y标题
     * @param xMin：x最小长度
     * @param xMax：x最大长度
     * @param yMin:y最小长度
     * @param yMax：y最大长度
     * @param axesColor：颜色
     * @param labelsColor：标签
     */
    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String xTitle, String yTitle,
                                    double xMin, double xMax, double yMin, double yMax, int axesColor, int labelsColor) {
        //有关对图表的渲染可参看api文档
        renderer.setChartTitle(title);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        renderer.setXAxisMin(xMin);
        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
        //renderer.setShowGrid(true);
        //renderer.setGridColor(Color.WHITE);
        renderer.setXLabels(2);
        renderer.setYLabels(10);
        renderer.setXTitle("Time");
        renderer.setYTitle("mmHg");
        renderer.setYLabelsAlign(Align.RIGHT);
        renderer.setPointSize((float) 3);
        renderer.setShowLegend(false);
    }

    private long oldTime;
    private int isUpdata;
    private int oldXMin = 230;
    private int oldXMax = 250;

    public void setChartMaxMibn(long gx) {
        if (oldXMin >= gx || oldXMax <= gx) {
            oldXMin = (int) (gx - 10);
            oldXMax = (int) (gx + 10);
        }
    }

    /**
     * 绘制图像
     */
    private void myUpDate() {

        // 将数据封装成XYMultipleSeriesDataset
        // 设置每条折线的标题
        // 每条线每个点坐标值，也就是x,y值
        if (VideoFlag == 0) {
            for (int j = 0; j < landscapeTime.size(); j++) {
                series.add(landscapeTime.get(j), landscapeGx.get(j));
                setChartMaxMibn(landscapeGx.get(j));

                if (landscapeTime.get(j) > 3000) {
                    //设置好图表的样式
                    setChartSettings(renderer, "X", "Y", landscapeTime.get(j) - 3000, landscapeTime.get(j), oldXMin, oldXMax, Color.WHITE, Color.WHITE);
                }
            }
        } else {
            for (int j = 0; j < gameTime.size(); j++) {
                series.add(gameTime.get(j), gameGx.get(j));
                setChartMaxMibn(gameGx.get(j));
                if (gameTime.get(j) > 3000) {
                    //设置好图表的样式
                    setChartSettings(renderer, "X", "Y", gameTime.get(j) - 3000, gameTime.get(j), oldXMin, oldXMax, Color.WHITE, Color.WHITE);
                }
            }
        }

        Log.i("isUpdata", isUpdata + "");
        // 数据集里添上一条线
        //  mDataset.addSeries(series);
        // if (landscapeTime.size())return;
        chart.invalidate();
    }


    /**
     * 更新图标信息
     */
    private void updateChart() {
        //设置好下一个需要增加的节点
        if (flag == 1)
            addY = 10;
        else {
//			addY=250;
            flag = 1;
            if (gx < 200) {
                if (hua[20] > 1) {
                    Toast.makeText(MainActivity.this, "请用您的指尖完全盖住摄像头镜头，尝试调整位置直到本条消息不再出现。", Toast.LENGTH_SHORT).show();
                    hua[20] = 0;
                }
                hua[20]++;
                return;
            } else
                hua[20] = 10;
            j = 0;
        }

        if (j < 20) {
            addY = hua[j];
            j++;
        }

        //移除数据集中旧的点集
        mDataset.removeSeries(series);

        //判断当前点集中到底有多少点，因为屏幕总共只能容纳100个，所以当点数超过100时，长度永远是100
        int length = series.getItemCount();
        int bz = 0;
        //addX = length;
        if (length > 300) {
            length = 300;
            bz = 1;
        }
        addX = length;
        //将旧的点集中x和y的数值取出来放入backup中，并且将x的值加1，造成曲线向右平移的效果
        for (int i = 0; i < length; i++) {
            xv[i] = (int) series.getX(i) - bz;
            yv[i] = (int) series.getY(i);
        }

        //点集先清空，为了做成新的点集而准备
        series.clear();
        mDataset.addSeries(series);
        //将新产生的点首先加入到点集中，然后在循环体中将坐标变换后的一系列点都重新加入到点集中
        //这里可以试验一下把顺序颠倒过来是什么效果，即先运行循环体，再添加新产生的点
        series.add(addX, addY);

        for (int k = 0; k < length; k++) {
            series.add(xv[k], yv[k]);
        }
        //在数据集中添加新的点集
        //mDataset.addSeries(series);

        //视图更新，没有这一步，曲线不会呈现动态
        //如果在非UI主线程中，需要调用postInvalidate()，具体参考api
        chart.invalidate();
    } //曲线


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            wakeLock.acquire();
            camera = Camera.open();

        } catch (Exception e) {
            e.printStackTrace();
        }
        //     if (handler != null) {
        //      handler.sendEmptyMessage(1);
        //   }
        videoView.start();
    }

    @Override
    public void onPause() {

        if (camera == null) {
            return;
        }
        wakeLock.release();
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
        videoView.pause();
//        if (handler != null) {
//            handler.removeMessages(1);
//        }
        super.onPause();
    }


    /**
     * 相机预览方法
     * 这个方法中实现动态更新界面UI的功能，
     * 通过获取手机摄像头的参数来实时动态计算平均像素值、脉冲数，从而实时动态计算心率值。
     */
    private PreviewCallback previewCallback = new PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera cam) {

            if (createStartTime == 0) {
                createStartTime = System.currentTimeMillis();
                startTime = System.currentTimeMillis();
            }

            if (data == null)
                throw new NullPointerException();
            Camera.Size size = cam.getParameters().getPreviewSize();
            if (size == null)
                throw new NullPointerException();
            if (!processing.compareAndSet(false, true))
                return;
            int width = size.width;
            int height = size.height;
            //图像处理
            int imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(), height, width);
            gx = imgAvg;

            Log.i("time+gx", String.valueOf(System.currentTimeMillis() - createStartTime) + "----" + gx);


            if (VideoFlag == 0) {
                landscapeTime.add(System.currentTimeMillis() - createStartTime);
                landscapeGx.add(new Long((long) gx));
            } else {
                gameTime.add(System.currentTimeMillis() - createStartTime);
                gameGx.add(new Long((long) gx));
            }
//            if (gx < 200) {
//                Toast.makeText(MainActivity.this, "请用您的指尖完全盖住摄像头镜头，尝试调整位置直到本条消息不再出现。", Toast.LENGTH_SHORT).show();
//            }
            myUpDate();
            text1.setText("平均像素值是" + String.valueOf(imgAvg));

            //像素平均值imgAvg,日志
            //Log.i(TAG, "imgAvg=" + imgAvg);
            if (imgAvg == 0 || imgAvg == 255) {
                processing.set(false);
                return;
            }

            //计算平均值
            int averageArrayAvg = 0;
            int averageArrayCnt = 0;
            for (int i = 0; i < averageArray.length; i++) {
                if (averageArray[i] > 0) {
                    averageArrayAvg += averageArray[i];
                    averageArrayCnt++;
                }
            }

            //计算平均值
            int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt) : 0;
            TYPE newType = currentType;
            if (imgAvg < rollingAverage) {
                newType = TYPE.RED;
                if (newType != currentType) {
                    beats++;
                    flag = 0;
                    text2.setText("脉冲数是" + String.valueOf(beats));
                    //Log.e(TAG, "BEAT!! beats=" + beats);
                }
            } else if (imgAvg > rollingAverage) {
                newType = TYPE.GREEN;
            }

            if (averageIndex == averageArraySize)
                averageIndex = 0;
            averageArray[averageIndex] = imgAvg;
            averageIndex++;

            // Transitioned from one state to another to the same
            if (newType != currentType) {
                currentType = newType;
                //image.postInvalidate();
            }

            //获取系统结束时间（ms）
            long endTime = System.currentTimeMillis();

            double totalTimeInSecs = (endTime - startTime) / 1000d;

            if (totalTimeInSecs >= 2) {
                double bps = (beats / totalTimeInSecs);
                int dpm = (int) (bps * 60d);
                if (dpm < 30 || dpm > 180 || imgAvg < 200) {
                    //获取系统开始时间（ms）
                    startTime = System.currentTimeMillis();
                    //beats心跳总数
                    beats = 0;
                    processing.set(false);
                    return;
                }

                //Log.e(TAG, "totalTimeInSecs=" + totalTimeInSecs + " beats="+ beats);
                if (beatsIndex == beatsArraySize)
                    beatsIndex = 0;
                beatsArray[beatsIndex] = dpm;
                beatsIndex++;
                int beatsArrayAvg = 0;
                int beatsArrayCnt = 0;

                for (int i = 0; i < beatsArray.length; i++) {
                    if (beatsArray[i] > 0) {
                        beatsArrayAvg += beatsArray[i];
                        beatsArrayCnt++;
                    }
                }

                beatsAvg = (beatsArrayAvg / beatsArrayCnt);

                text.setText("您的的心率是" + String.valueOf(beatsAvg) + "  zhi:" + String.valueOf(beatsArray.length)
                        + "    " + String.valueOf(beatsIndex) + "    " + String.valueOf(beatsArrayAvg) + "    " + String.valueOf(beatsArrayCnt));

                //获取系统时间（ms）
                startTime = System.currentTimeMillis();
                beats = 0;
            }

            processing.set(false);
        }
    };


    /**
     * 预览回调接口
     */
    private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        //创建时调用
        @SuppressLint("LongLogTag")
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                if (previewHolder != null && previewCallback != null && camera != null) {
                    camera.setPreviewDisplay(previewHolder);
                    camera.setPreviewCallback(previewCallback);
                }
            } catch (Throwable t) {
                Log.e("PreviewDemo-surfaceCallback", "Exception in setPreviewDisplay()", t);
            }
        }

        //当预览改变的时候回调此方法
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                //				Log.d(TAG, "Using width=" + size.width + " height="	+ size.height);
            }
            camera.setParameters(parameters);
            camera.startPreview();
        }

        //销毁的时候调用
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // Ignore
        }
    };

    /**
     * 获取相机最小的预览尺寸
     *
     * @param width
     * @param height
     * @param parameters
     * @return
     */
    private static Camera.Size getSmallestPreviewSize(int width, int height,
                                                      Camera.Parameters parameters) {
        Camera.Size result = null;
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;
                    if (newArea < resultArea)
                        result = size;
                }
            }
        }
        return result;
    }

    //发送数据
    public void getData() {
        Retrofit retrofit = RetrofitClient.builderRetrofit();
        GitHubApi repo = retrofit.create(GitHubApi.class);

        Map<String, String> hashMap = new HashMap<String, String>();
        //hashMap.put("pulseUserId", getIntent().getStringExtra("UserId"));
        hashMap.put("pulseUserId", UserId);
        hashMap.put("pulseMean", String.valueOf(beatsAvgOld));
        hashMap.put("pulseVariation",String.valueOf(beatsAvg) );
        hashMap.put("pulseCompare", String.valueOf(beatsAvg - beatsAvgOld) );

        StringBuffer stringBufferTime=new StringBuffer();
        stringBufferTime.append(landscapeTime.toString());
        stringBufferTime.append(gameTime.toString());
       String dataTime= stringBufferTime.toString().replaceAll("[\\[\\]]","");


        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append(landscapeGx.toString());
        stringBuffer.append(gameGx.toString());
        String dataGx= stringBuffer.toString().replaceAll("[\\[\\]]","");

        hashMap.put("recordSecondes", dataTime);
        hashMap.put("recordPixels", dataGx);

        Call<ResponseBody> call = repo.postStringFlyRoute(hashMap);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                String str = null;
                try {
                    str = response.body().string().toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                switch (str) {
                    case "ok":
                        //handler.removeMessages(1);
                        Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                        //intent.putExtra("Url", "http://10.0.0.8:8080/company/blue.html");red.html?userId=2
                        intent.putExtra("Url", "http://10.0.0.8:8080/company/blue.html?userId="+UserId);
                        startActivity(intent);
                        MainActivity.this.finish();
                        break;
                    default:
                        Tools.showToast("未知错误", getApplicationContext());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Tools.showToast(t.getMessage(), getApplicationContext());
            }
        });
    }


}