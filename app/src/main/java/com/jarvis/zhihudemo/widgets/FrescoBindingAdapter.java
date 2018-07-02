//package com.jarvis.zhihudemo.widgets;
//
//import android.net.Uri;
//import android.support.annotation.ColorRes;
//import android.text.TextUtils;
//import com.facebook.drawee.backends.pipeline.Fresco;
//import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
//import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
//import com.facebook.drawee.view.SimpleDraweeView;
//import com.facebook.imagepipeline.request.ImageRequest;
//import com.facebook.imagepipeline.request.ImageRequestBuilder;
//import java8.util.Objects;
//import jp.wasabeef.fresco.processors.BlurPostprocessor;
//import jp.wasabeef.fresco.processors.ColorFilterPostprocessor;
//import jp.wasabeef.fresco.processors.CombinePostProcessors.Builder;
///**
// * @author Jarvis @ Zhihu Inc.
// * @version 1.0
// * @title ZhihuDemo
// * @description 该类主要功能描述
// * @create 2017/11/24 上午10:00
// * @changeRecord [修改记录] <br/>
// */
//
//public class FrescoBindingAdapter {
//
//    public FrescoBindingAdapter() {
//    }
//
//    public static void loadImage(SimpleDraweeView view, String imageUrl, boolean imageBlur, @ColorRes Integer imageColorFilter, int blurRadius) {
//        if(TextUtils.isEmpty(imageUrl)) {
//            view.setImageURI(Uri.EMPTY);
//        } else {
//            if(!imageBlur && !Objects.nonNull(imageColorFilter)) {
//                view.setImageURI(imageUrl);
//            } else {
//                Builder processBuilder = new Builder();
//                if(imageBlur) {
//                    int radius = blurRadius > 0?blurRadius:1;
//                    processBuilder.add(new BlurPostprocessor(view.getContext(), radius));
//                }
//
//                if(Objects.nonNull(imageColorFilter)) {
//                    processBuilder.add(new ColorFilterPostprocessor(imageColorFilter.intValue()));
//                }
//
//                ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(imageUrl)).setPostprocessor(processBuilder.build()).build();
//                PipelineDraweeController controller = (PipelineDraweeController)((PipelineDraweeControllerBuilder)((PipelineDraweeControllerBuilder) Fresco.newDraweeControllerBuilder().setImageRequest(imageRequest)).setOldController(view.getController())).build();
//                view.setController(controller);
//            }
//
//        }
//    }
//}
