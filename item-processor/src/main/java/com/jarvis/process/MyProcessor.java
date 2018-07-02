package com.jarvis.process;

import com.google.auto.service.AutoService;
import com.jarvis.item.annotation.ItemAnnotation;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/3/26 下午3:44
 * @changeRecord [修改记录] <br/>
 */

@AutoService(Processor.class)
public class MyProcessor extends AbstractProcessor {

    private Types mTypeUtils;
    private Elements mElementUtils;
    private Filer mFiler;
    private Messager mMessager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        mTypeUtils = processingEnv.getTypeUtils();
        mElementUtils = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();

    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(ItemAnnotation.class)) {
            if (annotatedElement.getKind() != ElementKind.CLASS) {
                error(annotatedElement, "Only classes can be annotated with @%s",
                        ItemAnnotation.class.getSimpleName());
            }
            analysisAnnotated(annotatedElement);

        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        final Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        annotations.add(ItemAnnotation.class);
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }


    private void error(Element e, String msg, Object... args) {
        mMessager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, args),
                e);
    }

    private void analysisAnnotated(Element classElement) {
        ItemAnnotation annotation = classElement.getAnnotation(ItemAnnotation.class);
        String name = annotation.name();
        String text = annotation.text();

        final String packageName = mElementUtils.getPackageOf(classElement).getQualifiedName().toString();
        final ClassName className = ClassName.get(packageName, classElement.getSimpleName().toString() + "Poet");

        final TypeSpec typeSpec = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(getAnnotationValue("getAnnotationValue", name, text))
                .build();

        JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();
        try {
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            mMessager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }


    }

    private static MethodSpec getAnnotationValue(String methodName, String name, String text) {
        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(String.class)
                .addStatement("return $S", text + name)
                .build();
    }
}
