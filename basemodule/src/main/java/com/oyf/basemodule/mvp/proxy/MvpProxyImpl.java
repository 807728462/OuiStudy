package com.oyf.basemodule.mvp.proxy;

import com.oyf.basemodule.log.LogUtils;
import com.oyf.basemodule.mvp.BasePresenter;
import com.oyf.basemodule.mvp.IPresenter;
import com.oyf.basemodule.mvp.IView;
import com.oyf.basemodule.mvp.inject.InjectPresenter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @创建者 oyf
 * @创建时间 2020/6/1 16:05
 * @描述
 **/
public class MvpProxyImpl<V extends IView> implements IMvpProxy {
    private V mView;
    private List<IPresenter> mPresenters;

    public MvpProxyImpl(V view) {
        this.mView = view;
        mPresenters = new ArrayList<>();
    }

    @Override
    public void bindMvpProxy() {
        LogUtils.d("MvpProxyImpl.bindMvpProxy");
        // 这个地方要去注入 Presenter 通过反射 (Dagger) soEasy
        Field[] fields = mView.getClass().getDeclaredFields();
        for (Field field : fields) {
            InjectPresenter injectPresenter = field.getAnnotation(InjectPresenter.class);
            if (injectPresenter != null) {
                // 创建注入
                Class<? extends BasePresenter> presenterClazz = null;
                // 自己去判断一下类型？ 获取继承的父类，如果不是 继承 BasePresenter 抛异常
                try {
                    presenterClazz = (Class<? extends BasePresenter>) field.getType();
                } catch (Exception e) {
                    // 乱七八糟一些注入
                    throw new RuntimeException("No support inject presenter type " + field.getType().getName());
                }

                try {
                    // 创建 Presenter 对象
                    IPresenter basePresenter = presenterClazz.newInstance();
                    // 并没有解绑，还是会有问题，这个怎么办？1 2
                    basePresenter.takeView(mView);
                    // 设置
                    field.setAccessible(true);
                    field.set(mView, basePresenter);
                    mPresenters.add(basePresenter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void unbindMvpProxy() {
        LogUtils.d("MvpProxyImpl.unbindMvpProxy");
        // 一定要解绑
        for (IPresenter presenter : mPresenters) {
            presenter.dropView();
        }
        mView = null;
    }
}
