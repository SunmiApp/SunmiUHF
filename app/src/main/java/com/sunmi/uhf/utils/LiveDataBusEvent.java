package com.sunmi.uhf.utils;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * User: highsixty
 * Date: 2020-04-09 11:16
 * email: gaolulin@sunmi.com
 */
public class LiveDataBusEvent {

    private final Map<String, BusMutableLiveData<Object>> bus;

    private LiveDataBusEvent() {
        bus = new HashMap<>();
    }

    private static class SingletonHolder {
        private static final LiveDataBusEvent DEFAULT_BUS = new LiveDataBusEvent();
    }

    public static LiveDataBusEvent get() {
        return SingletonHolder.DEFAULT_BUS;
    }


    public MutableLiveData<Object> with(String key) {
        return with(key, Object.class, false);
    }

    public MutableLiveData<Object> with(String key, boolean sticky) {
        return with(key, Object.class, sticky);
    }

    public <T> MutableLiveData<T> with(String key, Class<T> type) {
        return with(key, type, false);
    }

    public <T> MutableLiveData<T> with(String key, Class<T> type, boolean sticky) {
        if (!bus.containsKey(key)) {
            bus.put(key, new BusMutableLiveData<>(sticky));
        }
        return (MutableLiveData<T>) bus.get(key);
    }

    private static class ObserverWrapper<T> implements Observer<T> {

        static final int START_VERSION = -1;
        /**
         * 与LiveData版本号同步，如果onChanged回调时，版本号相同，则不做处理
         */
        private int mVersion;
        private Observer<T> observer;
        private WeakReference<BusMutableLiveData> weakLiveData;

        public ObserverWrapper(Observer<T> observer, BusMutableLiveData liveData) {
            this.observer = observer;
            mVersion = liveData.mVersion;
            weakLiveData = new WeakReference<>(liveData);
        }

        @Override
        public void onChanged(@Nullable T t) {
            if (observer != null) {
                if (isCallOnObserve()) {
                    return;
                }
                observer.onChanged(t);
            }
        }

        private boolean isCallOnObserve() {
            if (mVersion != START_VERSION) {
                /**
                 * 如果版本号不等于起始值，说明LiveData中已经有数值了，此时判断当前版本号和LiveData中的版本号是否相同
                 * 如果相同说明是在observe时，直接触发的回调，返回true，不回调onChanged
                 * mVersion设置为起始值，便于下次LiveData数据变化后，直接返回false，回调onChanged
                 */
                boolean isSame = weakLiveData.get() != null && mVersion == weakLiveData.get().mVersion;
                mVersion = START_VERSION;
                weakLiveData.clear();
                return isSame;
            }
            return false;
        }
    }

    private static class BusMutableLiveData<T> extends MutableLiveData<T> {

        private int mVersion = ObserverWrapper.START_VERSION;
        private Map<Observer, WeakReference<ObserverWrapper>> observerMap = new WeakHashMap<>();

        private boolean sticky = false;

        public BusMutableLiveData(boolean sticky) {
            this.sticky = sticky;
        }

        @Override
        public void setValue(T value) {
            mVersion++;
            super.setValue(value);
        }

        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
            if (sticky) {
                super.observe(owner, observer);
            } else {
                WeakReference<ObserverWrapper> wr = observerMap.get(observer);
                ObserverWrapper wrapper = wr == null ? null : wr.get();
                if (wrapper == null) {
                    wrapper = new ObserverWrapper<>(observer, this);
                    observerMap.put(observer, new WeakReference<>(wrapper));
                }
                super.observe(owner, wrapper);
            }

        }

        @Override
        public void observeForever(@NonNull Observer<? super T> observer) {
            if (sticky) {
                super.observeForever(observer);
            } else {
                WeakReference<ObserverWrapper> wr = observerMap.get(observer);
                ObserverWrapper wrapper = wr == null ? null : wr.get();
                if (wrapper == null) {
                    wrapper = new ObserverWrapper<>(observer, this);
                    observerMap.put(observer, new WeakReference<>(wrapper));
                }
                super.observeForever(wrapper);
            }

        }


        @Override
        public void removeObserver(@NonNull Observer<? super T> observer) {
            if (observer instanceof ObserverWrapper) {
                super.removeObserver(observer);
                return;
            }
            if (sticky) {
                super.removeObserver(observer);
            } else {
                WeakReference<ObserverWrapper> wr = observerMap.get(observer);
                ObserverWrapper wrapper = wr == null ? null : wr.get();
                if (wrapper == null)
                    return;
                super.removeObserver(wrapper);
            }

        }

    }

}
