package com.bongole.ti.gpuimage;

import java.util.ArrayList;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageMonochromeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;

import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiContext;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiDrawableReference;
import org.appcelerator.titanium.view.TiUIView;

import ti.modules.titanium.ui.TableViewProxy;
import ti.modules.titanium.ui.widget.TiImageView;
import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;

import com.bongole.ti.gpuimage.AndroidModule;

@Kroll.proxy(creatableInModule=AndroidModule.class, propertyAccessors = {
	"decodeRetries",
	TiC.PROPERTY_AUTOROTATE,
	TiC.PROPERTY_DEFAULT_IMAGE,
	TiC.PROPERTY_DURATION,
	TiC.PROPERTY_ENABLE_ZOOM_CONTROLS,
	TiC.PROPERTY_IMAGE,
	TiC.PROPERTY_IMAGES,
	TiC.PROPERTY_REPEAT_COUNT,
	TiC.PROPERTY_URL
})
public class GPUImageViewProxy extends TiViewProxy
{
	private Bitmap bitmap;
	private Bitmap originalBitmap;
	private ArrayList<TiDrawableReference> imageSources;

	public GPUImageViewProxy()
	{
		super();
	}

	public GPUImageViewProxy(TiContext tiContext)
	{
		this();
	}

	@Override
	public TiUIView createView(Activity activity) {
		return new GPUImageView(this);
	}

	public Bitmap getBitmap()
	{
		if (bitmap != null && bitmap.isRecycled())
		{
			// Cleanup after recycled bitmaps
			bitmap = null;
		}
		return bitmap;
	}

	public ArrayList<TiDrawableReference> getImageSources()
	{
		return imageSources;
	}

	public void onImageSourcesChanged(GPUImageView imageView, ArrayList<TiDrawableReference> imageSources)
	{
		this.imageSources = imageSources;
		// The current cached bitmap, if any, can't be trusted now
		onBitmapChanged(imageView, null);
	}

	public void onBitmapChanged(GPUImageView imageView, Bitmap bitmap)
	{
		this.bitmap = bitmap;
	}

	public boolean inTableView()
	{
		TiViewProxy parent = getParent();
		while (parent != null) {
			if (parent instanceof TableViewProxy) {
				return true;
			}
			parent = parent.getParent();
		}
		return false;
	}

	private GPUImageView getImageView() {
		return (GPUImageView) getOrCreateView();
	}
	
	@Kroll.method
	public void start() {
		getImageView().start();
	}
	
	@Kroll.method
	public void stop() {
		getImageView().stop();
	}
	
	@Kroll.method
	public void pause() {
		getImageView().pause();
	}
	
	@Kroll.method
	public void resume() {
		getImageView().resume();
	}
	
	@Kroll.getProperty @Kroll.method
	public boolean getAnimating() {
		return getImageView().isAnimating();
	}
	
	@Kroll.getProperty @Kroll.method
	public boolean getPaused() 
	{
		return getImageView().isPaused();
	}
	
	@Kroll.getProperty @Kroll.method
	public boolean getReverse() {
		return getImageView().isReverse();
	}
	
	@Kroll.setProperty(runOnUiThread=true) @Kroll.method(runOnUiThread=true)
	public void setReverse(boolean reverse) {
		getImageView().setReverse(reverse);
	}
	
	@Kroll.method
	public TiBlob toBlob() {
		return getImageView().toBlob();
	}

	@Override
	public void releaseViews()
	{
		bitmap = null;
		imageSources = null;
		super.releaseViews();
	}
	
	@Kroll.setProperty @Kroll.method
	public void setFilter(String filterType){
		if( this.bitmap != null ){
			if( this.originalBitmap == null ){
				this.originalBitmap = this.bitmap;
			}
			
			GPUImageView v = (GPUImageView) this.view;
			GPUImage gpuImage = new GPUImage(v.getNativeView().getContext());
			
			if( AndroidModule.SEPIA_FILTER.equals(filterType) ){
				gpuImage.setFilter(new GPUImageSepiaFilter());
			}
			else if( AndroidModule.MONOCHROME_FILTER.equals(filterType) ){
				gpuImage.setFilter(new GPUImageMonochromeFilter());
			}
			else if( AndroidModule.GRAYSCALE_FILTER.equals(filterType) ){
				gpuImage.setFilter(new GPUImageGrayscaleFilter());
			}
			else{
				v.setImage(this.originalBitmap);
				return;
			}
			
			gpuImage.setImage(this.originalBitmap);
			Bitmap b = gpuImage.getBitmapWithFilterApplied();
			v.setImage(b);
		}
	}
}

