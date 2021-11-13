package com.slmanju.builderpattern;

import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;

import java.util.Arrays;
import java.util.List;

/**
 * @author Manjula Jayawardana
 */
public class StandardBuilderGenerator {

  private PsiClass targetClass;
  private PsiElementFactory elementFactory;
  private String className;
  private List<PsiField> fields;

  public StandardBuilderGenerator(PsiClass targetClass) {
    this.targetClass = targetClass;
    this.elementFactory = JavaPsiFacade.getElementFactory(targetClass.getProject());
    this.className = targetClass.getName();
    this.fields = Arrays.asList(targetClass.getFields());
  }

  public static StandardBuilderGenerator getInstance(PsiClass targetClass) {
    return new StandardBuilderGenerator(targetClass);
  }

  public void generate() {
    targetClass.add(createPrivateConstructor());
    targetClass.add(createFactoryMethod());
    targetClass.add(createBuilderClass());
  }

  private PsiMethod createPrivateConstructor() {
    StringBuilder stringBuilder = new StringBuilder()
        .append("private ").append(className).append("(").append("Builder builder) {");

    fields.forEach(field -> {
      String statement = "this." + field.getName() + " = builder." + field.getName() + ";\n";
      stringBuilder.append(statement);
    });

    stringBuilder.append("}");
    return elementFactory.createMethodFromText(stringBuilder.toString(), null);
  }

  private PsiMethod createFactoryMethod() {
    String stringBuilder = "public static Builder builder() {" +
        "return new Builder();" +
        "}";
    return elementFactory.createMethodFromText(stringBuilder, null);
  }

  private PsiClass createBuilderClass() {
    PsiClass innerBuilderClass = elementFactory.createClass("Builder");
    innerBuilderClass.getModifierList().setModifierProperty(PsiModifier.STATIC, true);
    innerBuilderClass.getModifierList().setModifierProperty(PsiModifier.FINAL, true);

    fields.forEach(field -> {
      PsiField psiField = elementFactory.createField(field.getName(), field.getType());
      innerBuilderClass.add(psiField);
    });

    String constructorString = "private Builder() {}";
    PsiMethod innerBuilderConstructor = elementFactory.createMethodFromText(constructorString, null);
    innerBuilderClass.add(innerBuilderConstructor);

    fields.forEach(field -> {
      String fieldName = field.getName();
      String methodString = "public Builder " + fieldName + "(" + field.getType().getPresentableText() + " " + fieldName + ") {" +
          "this." + fieldName + " = " + fieldName + ";\n" +
          "return this;" +
          "}";
      PsiMethod innerBuilderMethod = elementFactory.createMethodFromText(methodString, null);
      innerBuilderClass.add(innerBuilderMethod);
    });

    String buildString = "public " + className + " build() {" +
        "return new " + className + "(this);" +
        "}";
    PsiMethod build = elementFactory.createMethodFromText(buildString, null);
    innerBuilderClass.add(build);

    return innerBuilderClass;
  }

}
