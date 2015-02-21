/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.camera.captureintent;

import com.android.camera.app.CameraAppUI;
import com.android.camera.ui.CountDownView;
import com.android.camera.ui.PreviewOverlay;
import com.android.camera.ui.PreviewStatusListener;
import com.android.camera.ui.ProgressOverlay;
import com.android.camera.ui.focus.FocusRing;
import com.android.camera.util.AndroidServices;
import com.android.camera2.R;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Contains the UI for the ImageCaptureIntentModule.
 */
public class CaptureIntentModuleUI implements PreviewStatusListener.PreviewAreaChangedListener {
    public interface Listener {
        public void onZoomRatioChanged(float zoomRatio);
    }
    private final Listener mListener;

    private final CameraAppUI mAppUI;
    private final View mRootView;
    private final PreviewOverlay mPreviewOverlay;
    private final ProgressOverlay mProgressOverlay;
    private final FocusRing mFocusRing;
    private final CountDownView mCountdownView;

    /** The image view to display the captured picture. */
    private final ImageView mIntentReviewImageView;

    public CaptureIntentModuleUI(
            CameraAppUI appUI, View parent, Listener listener) {

        mAppUI = appUI;
        mListener = listener;
        mRootView = parent;

        mIntentReviewImageView = (ImageView) mRootView.findViewById(R.id.intent_review_imageview);

        final LayoutInflater layoutInflater = AndroidServices.instance().provideLayoutInflater();

        ViewGroup moduleRoot = (ViewGroup) mRootView.findViewById(R.id.module_layout);
        layoutInflater.inflate(R.layout.capture_module, moduleRoot, true);

        mPreviewOverlay = (PreviewOverlay) mRootView.findViewById(R.id.preview_overlay);
        mProgressOverlay = (ProgressOverlay) mRootView.findViewById(R.id.progress_overlay);
        mFocusRing = (FocusRing) mRootView.findViewById(R.id.focus_ring);
        mCountdownView = (CountDownView) mRootView.findViewById(R.id.count_down_view);
    }

    public void onPreviewStarted() {
        mAppUI.onPreviewStarted();
    }

    /**
     * Enables zoom UI, setting maximum zoom.
     * Called from Module when camera is available.
     *
     * @param maxZoom maximum zoom value.
     */
    public void initializeZoom(float maxZoom) {
        mPreviewOverlay.setupZoom(maxZoom, 0, mZoomChancedListener);
    }

    public FocusRing getFocusRing() {
        return mFocusRing;
    }

    public void setShutterButtonEnabled(boolean enabled) {
        mAppUI.setShutterButtonEnabled(enabled);
    }

    public void startFlashAnimation(boolean shortFlash) {
        mAppUI.startFlashAnimation(shortFlash);
    }

    /**
     * Starts the countdown timer.
     *
     * @param sec seconds to countdown
     */
    public void startCountdown(int sec) {
        mCountdownView.startCountDown(sec);
    }

    /**
     * Sets a listener that gets notified when the countdown is finished.
     */
    public void setCountdownFinishedListener(CountDownView.OnCountDownStatusListener listener) {
        mCountdownView.setCountDownStatusListener(listener);
    }

    /**
     * Transition to the UI where users can review the taken photo.
     *
     * @param reviewPictureBitmap The picture bitmap to be shown.
     */
    public void showPictureReviewUI(Bitmap reviewPictureBitmap) {
        mIntentReviewImageView.setImageBitmap(reviewPictureBitmap);
        mIntentReviewImageView.setVisibility(View.VISIBLE);

        mAppUI.transitionToIntentReviewLayout();
        mAppUI.hideModeOptions();
        mAppUI.disableModeOptions();
        mAppUI.setShutterButtonEnabled(false);
    }

    /**
     * Transition to the UI where users can take a photo.
     */
    public void showPictureCaptureUI() {
        mIntentReviewImageView.setVisibility(View.INVISIBLE);
        mIntentReviewImageView.setImageBitmap(null);

        mAppUI.transitionToIntentCaptureLayout();
        mAppUI.showModeOptions();
        mAppUI.enableModeOptions();
        mAppUI.setShutterButtonEnabled(true);
    }

    public void freezeScreenUntilPreviewReady() {
        mAppUI.freezeScreenUntilPreviewReady();
    }

    @Override
    public void onPreviewAreaChanged(RectF previewArea) {
        mCountdownView.onPreviewAreaChanged(previewArea);
        mProgressOverlay.setBounds(previewArea);
    }

    /** Set up listener to receive zoom changes from View and send to module. */
    private final PreviewOverlay.OnZoomChangedListener mZoomChancedListener =
            new PreviewOverlay.OnZoomChangedListener() {
        @Override
        public void onZoomValueChanged(float ratio) {
            mListener.onZoomRatioChanged(ratio);
        }

        @Override
        public void onZoomStart() {
        }

        @Override
        public void onZoomEnd() {
        }
    };
}