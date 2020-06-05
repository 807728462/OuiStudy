package com.oyf.codecollection;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oyf.basemodule.bus.OBus;
import com.oyf.basemodule.bus.OSubscribe;
import com.oyf.basemodule.bus.OThreadMode;
import com.oyf.basemodule.mvp.BaseActivity;
import com.oyf.basemodule.mvp.BasePresenter;
import com.oyf.codecollection.bean.UserBean;
import com.oyf.codecollection.chain.Intercept;
import com.oyf.codecollection.chain.OneInterceptor;
import com.oyf.codecollection.chain.RealInterceptorChain;
import com.oyf.codecollection.chain.ThreeInterceptor;
import com.oyf.codecollection.chain.TwoInterceptor;
import com.oyf.codecollection.company.EstoreExplosiveActivity;
import com.oyf.codecollection.ui.activity.BehaviorActivity;
import com.oyf.codecollection.ui.activity.ChinaMapActivity;
import com.oyf.codecollection.ui.activity.CompanyActivity;
import com.oyf.codecollection.ui.activity.CoordinatorLayoutActivity;
import com.oyf.codecollection.ui.activity.DataBindingActivity;
import com.oyf.codecollection.ui.activity.GalleryActivity;
import com.oyf.codecollection.ui.activity.HighlightActivity;
import com.oyf.codecollection.ui.activity.MusicActivity;
import com.oyf.codecollection.ui.activity.MyRecycleViewActivity;
import com.oyf.codecollection.ui.activity.NinePasswordActivity;
import com.oyf.codecollection.ui.activity.OGlideActivity;
import com.oyf.codecollection.ui.activity.PlayMusicActivity;
import com.oyf.codecollection.ui.activity.RippleActivity;
import com.oyf.codecollection.ui.activity.ScanQRCodeActivity;
import com.oyf.codecollection.ui.activity.ViewActivity;
import com.oyf.codecollection.ui.activity.test.TestMvpActivity;
import com.oyf.codecollection.ui.activity.test.TestSkinActivity;
import com.oyf.codecollection.ui.adapter.BaseAdapter;
import com.oyf.codecollection.ui.adapter.BaseViewHolder;
import com.oyf.codecollection.ui.bean.ItemBean;
import com.oyf.codecollection.ui.bean.ListBean;
import com.oyf.ookhttp.OCall;
import com.oyf.ookhttp.OCallBack;
import com.oyf.ookhttp.OOkHttpClient;
import com.oyf.ookhttp.ORequest;
import com.oyf.ookhttp.OResponse;
import com.oyf.ookhttp.interceptor.LogInterceptor;
import com.oyf.plugin.NoRegisterActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = MainActivity.class.getName();

    private RecyclerView rcv;
    private List<ListBean> mLists = new ArrayList<>();
    private BaseAdapter mAdapter;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    protected void initView(Bundle savedInstanceState) {
        //LayoutInflater.from(this).inflate(R.layout.rcv_item, (ViewGroup) getWindow().getDecorView().getRootView(), true);
        OBus.getDefault().register(this);
        rcv = findViewById(R.id.rcv);
        findViewById(R.id.bt_send_event).setOnClickListener(this);

        Log.d("------------", Integer.class.isAssignableFrom(Integer.class) + "");
        Log.d("------------", Number.class.isAssignableFrom(Integer.class) + "");

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_send_event:
                Intent intent = new Intent();
                //intent.setAction("HighlightActivity");
                Uri uri = Uri.parse("test://12456:8080");

                intent.setData(uri);

                startActivity(intent);
                EventBus.getDefault().postSticky(new UserBean());
                break;
            default:
                break;
        }
    }

    private static final String PATH = "http://restapi.amap.com/v3/weather/weatherInfo?city=110101&key=13cb58f5884f9749287abbead9c658f2";

    public void testOkHttp(View view) {

        OOkHttpClient okHttpClient = new OOkHttpClient.Builder().addInterceptor(new LogInterceptor()).build();

        ORequest oRequest = new ORequest.Builder().url(PATH).build();

        OCall oCall = okHttpClient.newCall(oRequest);
        oCall.enqueue(new OCallBack() {
            @Override
            public void onFailure(OCall call, IOException e) {
                ORequest request = call.request();
                Log.d(OOkHttpClient.TAG, "失败的url=" + request.url());
            }

            @Override
            public void onResponse(OCall call, OResponse response) throws IOException {
                Log.d(OOkHttpClient.TAG, "成功的结果的=" + response.string());
            }
        });
    }

    @Subscribe
    public void onEvent(Object o) {
        toast("以Object接收的Event");
        Log.d(TAG, "MainActivity.onEvent.Object");
    }

    @Subscribe
    public void onEvent(String o) {
        toast("以String接收的Event");
        Log.d(TAG, "MainActivity.onEvent.String");
    }

    protected void initData(Bundle savedInstanceState) {
        rcv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BaseAdapter<ListBean>(mLists, R.layout.rcv_main_list) {
            @Override
            protected void convert(BaseViewHolder helper, ListBean data) {
                helper.setText(R.id.tv_title, helper.getAdapterPosition() + "-" + data.title)
                        .setText(R.id.tv_details, data.details);
            }
        };
        mAdapter.setOnItemClickListener(new BaseAdapter.OnRecyclerViewItemClickListener<ListBean>() {
            @Override
            public void onItemClick(View view, int viewType, ListBean data, int position) {
                //EventBus.getDefault().post(new TestMain());
                startActivity(new Intent(MainActivity.this, data.clazz));
            }
        });
        rcv.setAdapter(mAdapter);
        initLists();
        List<Intercept> mlist = new ArrayList<>();
        mlist.add(new OneInterceptor());
        mlist.add(new TwoInterceptor());
        mlist.add(new ThreeInterceptor());
        RealInterceptorChain realInterceptorChain = new RealInterceptorChain(mlist, 0);
        realInterceptorChain.proceed();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "MainActivity.onNewIntent");
    }

    public void initLists() {
        mLists.add(new ListBean("测试静态换肤", "静态换肤，白天晚上模式切换", TestSkinActivity.class));
        mLists.add(new ListBean("gilde指南", "自定义glide，自己手写简单", OGlideActivity.class));
        mLists.add(new ListBean("新手提示", "新手提示指引", HighlightActivity.class));
        mLists.add(new ListBean("爆款", "两级RecycleView联动", EstoreExplosiveActivity.class));
        mLists.add(new ListBean("company", "公司的自定义view", CompanyActivity.class));
        mLists.add(new ListBean("扫描二维码", "自定义SurfaceView,添加扫描框等", ScanQRCodeActivity.class));
        mLists.add(new ListBean("DataBinding", "DataBinding的基本使用", DataBindingActivity.class));
        //mLists.add(new ListBean("vLayout的使用", "vlayout的基本使用方法，添加删除动画", VLayoutActivity.class));
        mLists.add(new ListBean("behavior的使用", "使用behavior，在recyccleview的滑动控制按钮的显示与隐藏", BehaviorActivity.class));
        mLists.add(new ListBean("使用svg画中国地图", "使用svg加path，svg的dom解析", ChinaMapActivity.class));
        mLists.add(new ListBean("语音识别扩散", "识别扩散波纹效果", RippleActivity.class));
        mLists.add(new ListBean("自定义RecycleView", "自己使用ViewGroup，实现RecycleView效果", MyRecycleViewActivity.class));
        mLists.add(new ListBean("网易音乐列表", "网易的音乐列表界面，滑动效果配合", MusicActivity.class));
        mLists.add(new ListBean("播放音乐界面", "音乐播放效果，滑动切歌，service播放音乐，ViewFlipper的基本使用", PlayMusicActivity.class));
        mLists.add(new ListBean("图片滚动配合", "ViewFlipper跟Gallery的配合使用", GalleryActivity.class));
        mLists.add(new ListBean("自定义View", "一些自定义view", ViewActivity.class));
        mLists.add(new ListBean("九宫格密码支付", "一些自定义view", NinePasswordActivity.class));
        mLists.add(new ListBean("跳转未注册的界面", "使用hook技术跳转未注册的界面，在IActivityManager进行拦截，在ActivityThread进行Handler拦截", NoRegisterActivity.class));
        mLists.add(new ListBean("CoordinatorLayoutActivity中滑动回弹等问题", "处理CoordinatorLayout中Appbar跟Recycleview的回弹冲突问题", CoordinatorLayoutActivity.class));
        mLists.add(new ListBean("mvp测试", "测试mvp的架构", TestMvpActivity.class));
    }

    @OSubscribe(threadMode = OThreadMode.MAIN)
    public void logMain(ItemBean itemBean) {
        Log.d(TAG, "logMain.thread=" + Thread.currentThread() + ",name=" + itemBean.name);
    }

    @OSubscribe(threadMode = OThreadMode.BACKGROUND)
    public void logY(ItemBean itemBean) {
        Log.d(TAG, "logY.thread=" + Thread.currentThread().getName() + ",name=" + itemBean.name);
    }
}
