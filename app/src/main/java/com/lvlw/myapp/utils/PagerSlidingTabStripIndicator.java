package com.lvlw.myapp.utils;

import android.view.View;

import com.lvlw.myapp.views.PagerSlidingTabStrip;

import solid.ren.skinlibrary.attr.base.SkinAttr;
import solid.ren.skinlibrary.utils.SkinResourcesUtils;

/**
 * Created by Wantrer on 2017/5/5 0005.
 */

public class PagerSlidingTabStripIndicator extends SkinAttr {
    @Override
    public void apply(View view) {
        if (view instanceof PagerSlidingTabStrip) {
            PagerSlidingTabStrip fab = (PagerSlidingTabStrip) view;
            if (RES_TYPE_NAME_COLOR.equals(attrValueTypeName)) {
                int color = SkinResourcesUtils.getColor(attrValueRefId);
                fab.setIndicatorColor(color);
            }
        }
    }
}
