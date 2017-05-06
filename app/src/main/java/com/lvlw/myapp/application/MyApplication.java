package com.lvlw.myapp.application;


import com.facebook.drawee.backends.pipeline.Fresco;
import com.lvlw.myapp.utils.FloatingActionButtonColorNormalAtrr;
import com.lvlw.myapp.utils.FloatingActionButtonColorPressedAtrr;
import com.lvlw.myapp.utils.PagerSlidingTabStripAttr;
import com.lvlw.myapp.utils.PagerSlidingTabStripIndicator;

import solid.ren.skinlibrary.SkinConfig;
import solid.ren.skinlibrary.base.SkinBaseApplication;


/**
 * Created by w9859 on 2017/3/16.
 */

public class MyApplication extends SkinBaseApplication{
    @Override
    public void onCreate() {
        super.onCreate();
        SkinConfig.setCanChangeStatusColor(true);
        SkinConfig.setDebug(true);
        SkinConfig.setTransitionAnim(false);
        SkinConfig.addSupportAttr("fabColorNormal",new FloatingActionButtonColorNormalAtrr());
        SkinConfig.addSupportAttr("fabColorPressed",new FloatingActionButtonColorPressedAtrr());
        SkinConfig.addSupportAttr("PagerSlidingTabStripbgd",new PagerSlidingTabStripAttr());
        SkinConfig.addSupportAttr("PagerSlidingTabStripidc",new PagerSlidingTabStripIndicator());
        Fresco.initialize(this);
    }
}
