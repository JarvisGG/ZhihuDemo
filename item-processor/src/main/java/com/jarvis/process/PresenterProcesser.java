package com.jarvis.process;

import com.jarvis.inter.IProcessor;
import com.jarvis.item.annotation.ItemAnnotation;
import com.jarvis.item.annotation.Presenter.Presenter;
import com.jarvis.item.view.IPresenter;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;


/**
 * @author yyf @ Zhihu Inc.
 * @since 07-09-2018
 */
public class PresenterProcesser implements IProcessor {

    private AnnotationProcessor mAbstractProcessor;

    @Override
    public void process(RoundEnvironment roundEnv, AnnotationProcessor abstractProcessor) {

        mAbstractProcessor = abstractProcessor;

        String className = "PresenterSelectors";
        TypeSpec.Builder tb = classBuilder(className)
                .superclass(IPresenter.class)
                .addModifiers(PUBLIC, FINAL)
                .addJavadoc("@ PresenterSelectors 此类由apt自动生成")
                .addField(presenterTypeField())
                .addMethod(constructor());

        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(Presenter.class)) {
            if (annotatedElement.getKind() != ElementKind.CLASS) {
                error(annotatedElement, "Only classes can be annotated with @%s",
                        Presenter.class.getSimpleName());
            }
            analysisAnnotated(tb, annotatedElement);
        }

        JavaFile javaFile = JavaFile.builder("com.jarvis.annotation", tb.build()).build();
        try {
            javaFile.writeTo(mAbstractProcessor.mFiler);
        } catch (IOException e) {
        }

//        final String packageName = mAbstractProcessor.mElements.getPackageOf(classElement).getQualifiedName().toString();
//        final ClassName className = ClassName.get(packageName, classElement.getSimpleName().toString() + "Poet");
    }

    public FieldSpec presenterTypeField() {
        return FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(List.class),
                WildcardTypeName.subtypeOf(IPresenter.class)),
                "mPresenters",
                Modifier.PRIVATE)
                .build();
    }

    public FieldSpec instanceField() {
        return FieldSpec.builder(ParameterizedTypeName.get(ClassName.get("com.jarvis.annotation", "PresenterSelector")),
                "sInstance",
                Modifier.PUBLIC, STATIC)
                .build();
    }

    public MethodSpec constructor() {
        return MethodSpec.methodBuilder("getInstance")
                .addModifiers(Modifier.PRIVATE)
                .build();
    }

    public MethodSpec getIntnstanceMethod() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC, STATIC)
                .addCode("if(sInstance == null) {\n" +
                        "            sInstance = new PresenterSelector();\n" +
                        "        }\n" +
                        "        return sInstance;")
                .returns(ParameterizedTypeName.get(ClassName.get("com.jarvis.annotation", "PresenterSelector")))
                .build();
    }

    private void analysisAnnotated(TypeSpec.Builder tb, Element annotatedElement) {
        Presenter presenters = annotatedElement.getAnnotation(Presenter.class);

        Class<? extends IPresenter>[] clazzs = presenters.value();

        MethodSpec.Builder initPresenterMethod = MethodSpec.methodBuilder("initPresenter")
                .addModifiers(PRIVATE);
        CodeBlock.Builder blockBuilder = CodeBlock.builder();

        for (Class<? extends IPresenter> presenter : clazzs) {
            tb.addField(presenterField(presenter));
        }
    }

    private FieldSpec presenterField(Class<? extends IPresenter> presenter) {
        return FieldSpec.builder(presenter.getClass(),
                "m"+presenter.getSimpleName()+"Presenters",
                Modifier.PRIVATE)
                .build();
    }

    private void error(Element e, String msg, Object... args) {
        mAbstractProcessor.mMessager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, args),
                e);
    }


    public static AnnotationValue getAnnotationValue(AnnotationMirror mirror, String key) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry :
                mirror.getElementValues().entrySet()) {

            if (entry.getKey().getSimpleName().contentEquals(key)) {
                return entry.getValue();
            }
        }
        return null;
    }

}


