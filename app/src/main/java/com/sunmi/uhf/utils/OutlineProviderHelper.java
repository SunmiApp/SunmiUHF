package com.sunmi.uhf.utils;

import android.graphics.Outline;
import android.view.View;
import android.view.ViewOutlineProvider;

import org.jetbrains.annotations.NotNull;

/**
 * FileName: OutlineProviderHelper
 *
 * 用于约束View圆角边界，也方便将View背景处理为圆角
 */
public class OutlineProviderHelper {

    @NotNull
    public static ViewOutlineProvider getOutlineProvider(final float v) {
        return new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), v);
            }
        };
    }

    @NotNull
    public static ViewOutlineProvider getOutlineProviderOval() {
        return new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, view.getWidth(), view.getHeight());
            }
        };
    }

    @NotNull
    public static ViewOutlineProvider getOutlineProviderHalfHeight() {
        return new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), view.getHeight() / 2);

            }
        };
    }
}
