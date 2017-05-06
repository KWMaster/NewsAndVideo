package com.lvlw.myapp.eventmessage;

import android.os.Bundle;

/**
 * Created by w9859 on 2017/3/10.
 */

public class EventMessage {
    Bundle mBundle;
    int tag;

    public Bundle getBundle() {
        return mBundle;
    }

    public void setBundle(Bundle bundle) {
        mBundle = bundle;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public static  class EventMessageAction{
        public final static int TAG_GO_MAIN=0; //跳转主页
        public final static int TAG_GO_SHOPCART=1; //购物车
        public final static int TAG_GO_MESSAGE=2; // 消息
    }
}
