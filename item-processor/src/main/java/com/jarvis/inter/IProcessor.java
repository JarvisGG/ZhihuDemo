package com.jarvis.inter;

import com.jarvis.process.AnnotationProcessor;

import javax.annotation.processing.RoundEnvironment;

/**
 * @author yyf @ Zhihu Inc.
 * @since 07-09-2018
 */
public interface IProcessor {
    void process(RoundEnvironment roundEnv, AnnotationProcessor mAbstractProcessor);
}
