package com.android.camera.stats;

import com.google.common.annotations.VisibleForTesting;

import com.android.camera.exif.ExifInterface;
import com.android.camera.ui.TouchCoordinate;

/**
 * Accumulates statistics during the lifecycle of a Capture Session. Since a
 * CaptureSession instance is available for the lifetime of the request and the
 * image processing of the said request, CaptureSessionStatsCollector is
 * attached to the CaptureSession so that we can collect information from both
 * the CaptureModule and the ImageBackend.
 */

public class CaptureSessionStatsCollector {

    protected final UsageStatistics mUsageStatistics;
    // Define all fields as Objects so that we know whether they were set or not.
    // A required field
    protected Integer mMode;

    // Fields with defaults, which are passed as primitives
    protected Boolean mIsFrontFacing = Boolean.FALSE;
    protected Boolean mIsHdr = Boolean.FALSE;
    protected Float mZoom = new Float(0.0f);

    // Optional fields (passed as Java Objects)
    protected String mFilename;
    protected ExifInterface mExifInterface;
    protected String mFlashSetting;
    protected Boolean mGridLinesOn;
    protected Float mTimerSeconds;
    protected TouchCoordinate mTouchCoordinate;
    protected Boolean mVolumeButtonShutter;

    /**
     * Constructor
     */
    public CaptureSessionStatsCollector() {
        mUsageStatistics = UsageStatistics.instance();
    }

    /**
     * Constructor for testing/dependency injection
     */
    @VisibleForTesting
    public CaptureSessionStatsCollector(UsageStatistics usageStatistics) {
        mUsageStatistics = usageStatistics;
    }

    /**
     * Accumulate the information that should be available at the time of the Capture Request.
     * If you are unable to deliver one of these parameters, you may want to think again.
     *
     * @param mode a mode specified by eventprotos.NavigationChange.Mode
     * @param filename filename of image to be created
     * @param frontFacing whether the camera request is going to the front camera or not
     * @param isHDR whether the camera is HDR mode
     * @param zoom value of the zoom on the camera request
     * @param flashSetting string representing the state of the flash (KEY_FLASH_MODE)
     * @param gridLinesOn whether the gridlines are on the preview display
     * @param timerSeconds value of the countdown timer
     * @param touchCoordinate the last shutter touch coordinate
     * @param volumeButtonShutter whether the volume button was used to initialize the request.
     */
    public void decorateAtTimeCaptureRequest(
            final int mode,
            final String filename,
            final boolean frontFacing,
            final boolean isHDR,
            final float zoom,
            final String flashSetting,
            final boolean gridLinesOn,
            final float timerSeconds,
            final TouchCoordinate touchCoordinate,
            final Boolean volumeButtonShutter
    ) {
        mMode = mode;
        mFilename = filename;
        mIsFrontFacing = frontFacing;
        mIsHdr = isHDR;
        mZoom = zoom;
        mFlashSetting = flashSetting;
        mGridLinesOn = gridLinesOn;
        mTimerSeconds = timerSeconds;
        mTouchCoordinate = touchCoordinate;
        mVolumeButtonShutter = volumeButtonShutter;
    }

    /**
     * Accumalate the information that should be available at the time of
     * Write-To-Disk. If you are unable to deliver one of these parameters, you
     * may want to think again.
     *
     * @param exifInterface exif values to be associated with the JPEG image
     *            file that is being created.
     */
    public void decorateAtTimeWriteToDisk(
            final ExifInterface exifInterface
    ) {
        mExifInterface = exifInterface;
    }

    /**
     * Send capture event to the UsageStatistic singleton.
     */
    public void photoCaptureDoneEvent(){
        if(isValidForPhotoCaptureEvent()) {
            mUsageStatistics.photoCaptureDoneEvent(
                    mMode, mFilename, mExifInterface, mIsFrontFacing,
                    mIsHdr, mZoom, mFlashSetting, mGridLinesOn, mTimerSeconds,
                    mTouchCoordinate, mVolumeButtonShutter);
        }
    }

    /**
     * Returns whether all the fields in the CaptureSessionStatsCollector are set or not.
     */
    public boolean isCompleteForPhotoCaptureEvent() {
        return (mMode != null) &&
                (mFilename != null) &&
                (mExifInterface != null) &&
                (mIsFrontFacing != null) &&
                (mIsHdr != null) &&
                (mZoom != null) &&
                (mFlashSetting != null) &&
                (mGridLinesOn != null) &&
                (mTimerSeconds != null) &&
                (mTouchCoordinate != null) &&
                (mVolumeButtonShutter != null);
    }

    /**
     * Return whether state of collector is sufficient for PhotoCaptureEvent.
     *
     * @return whether state of collector is sufficient for PhotoCaptureEvent.
     */
    public boolean isValidForPhotoCaptureEvent() {
        return (mMode != null);
    }



}