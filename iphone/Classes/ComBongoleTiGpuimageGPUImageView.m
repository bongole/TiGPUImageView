/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */

#import "ComBongoleTiGpuimageGPUImageView.h"
#import "GPUImageFilter.h"
#import "GPUImageSepiaFilter.h"
#import "GPUImageMonochromeFilter.h"
#import "GPUImageGrayscaleFilter.h"
#import "GPUImageFastBlurFilter.h"
#import <objc/runtime.h>

@implementation ComBongoleTiGpuimageGPUImageView

-(void)setFilter_:(id)args
{
    // ENSURE_TYPE_OR_NIL(args, NSString);
    ENSURE_SINGLE_ARG_OR_NIL(args, NSDictionary);
    //ENSURE_UI_THREAD_1_ARG(args);
    
	NSString *filterType = [args objectForKey:@"filtertype"];
    
    UIImageView *imageview;
    object_getInstanceVariable(self, "imageView", (void *)&imageview);
    
    UIImage *image = imageview.image;
    if( image != nil ){
        UIImage* original_image = [self.proxy valueForUndefinedKey:@"original_image"];
        if( original_image == nil ){
            original_image = image;
            [self.proxy setValue:original_image forUndefinedKey:@"original_image"];
        }
        
        GPUImageFilter *stillImageFilter;
        if( [kSepiaFilter isEqualToString:filterType] ){
            stillImageFilter = [[GPUImageSepiaFilter alloc] init];
        }
        else if( [kMonochromeFilter isEqualToString:filterType] ){
            stillImageFilter = [[GPUImageMonochromeFilter alloc] init];
        }
        else if( [kGrayscaleFilter isEqualToString:filterType] ){
            stillImageFilter = [[GPUImageGrayscaleFilter alloc] init];
        }
        else if( [kFastBlurFilter isEqualToString:filterType] ){
            stillImageFilter = [[GPUImageFastBlurFilter alloc] init];
			[stillImageFilter setBlurSize:[TiUtils floatValue:[args objectForKey:@"blursize"]]];
	    }
        else{
            [imageview setImage:original_image];
            return;
        }
        
        GPUImagePicture *stillImageSource = [[GPUImagePicture alloc] initWithImage:original_image];
        
        [stillImageSource addTarget:stillImageFilter];
        [stillImageSource processImage];
        
        
        UIImage *currentFilteredImage = [stillImageFilter imageFromCurrentlyProcessedOutput];
        [imageview setImage:currentFilteredImage];
        
        [stillImageFilter release];
        [stillImageSource release];
    }
}

@end
