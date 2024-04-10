package ru.javapro;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestRunner {

    static int numberBeforeSuiteAnnotations;
    static int numberCSVSourceAnnotations;
    static int numberAfterSuiteAnnotations;
    static Main objClassMain = new Main();

    public static void main(String[] args) throws Exception {
        runTests(Main.class);
    }

    public static void callInvokeMethod(List<Method> methods) {
        for (Method method : methods) {
            try {
                method.invoke(objClassMain);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void callInvokeMethod(Method method) {
            try {
                method.invoke(objClassMain);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    static void runTests(Class Main) throws InvocationTargetException, IllegalAccessException {

        Class<Main> classMain = Main.class;
        Method declaredMethods[] = classMain.getDeclaredMethods();

        List<Method> beforeTestMethods = new ArrayList<>();
        List<Method> testMethods = new ArrayList<>();
        List<Method> csvSourceMethods = new ArrayList<>();
        List<Method> afterTestMethods = new ArrayList<>();

        Method beforeSuiteMethod = null;
        Method afterSuiteMethod = null;

        for(Method declaredMethod: declaredMethods) {
            if(declaredMethod.isAnnotationPresent(BeforeSuite.class)) {
                numberBeforeSuiteAnnotations++;
                beforeSuiteMethod = declaredMethod;
            }

            if(declaredMethod.isAnnotationPresent(BeforeTest.class)) beforeTestMethods.add(declaredMethod);

            if(declaredMethod.isAnnotationPresent(Test.class)) {
                if (declaredMethod.getAnnotation(Test.class).priority() < 1 || declaredMethod.getAnnotation(Test.class).priority() > 10) {
                    throw new IllegalArgumentException("Значение приоритета должно быть от 1 до 10 включительно.");
                }
                testMethods.add(declaredMethod);
            }

            if(declaredMethod.isAnnotationPresent(CsvSource.class)) {
                csvSourceMethods.add(declaredMethod);
                numberCSVSourceAnnotations++;
            }

            if(declaredMethod.isAnnotationPresent(AfterTest.class)) afterTestMethods.add(declaredMethod);

            if(declaredMethod.isAnnotationPresent(AfterSuite.class)) {
                numberAfterSuiteAnnotations++;
                afterSuiteMethod = declaredMethod;
            }

        if(numberBeforeSuiteAnnotations > 1) throw new RuntimeException("Multiple @BeforeSuite annotations for methods found.");
        else if(numberAfterSuiteAnnotations > 1) throw new RuntimeException("Multiple @AfterSuite annotations for methods found.");

    }

        testMethods.sort((m1, m2) -> m2.getAnnotation(Test.class).priority() - m1.getAnnotation(Test.class).priority());

        if(numberBeforeSuiteAnnotations > 0) callInvokeMethod(beforeSuiteMethod);
        for(Method method : testMethods) {
            callInvokeMethod(beforeTestMethods);
            callInvokeMethod(method);
            callInvokeMethod(afterTestMethods);
        }
        if(numberCSVSourceAnnotations > 0) {
            for(Method csvSourceMethod : csvSourceMethods) {
                CsvSource csvSourceAnnotation = csvSourceMethod.getAnnotation(CsvSource.class);
                String[] values = csvSourceAnnotation.value().split(", ");
                Class<?>[] parameterTypes = csvSourceMethod.getParameterTypes();
                Object[] argsArray = new Object[parameterTypes.length];

                if (values.length == parameterTypes.length) {
                    for (int i = 0; i < values.length; i++) {
                        if (parameterTypes[i] == int.class) {
                            argsArray[i] = Integer.parseInt(values[i]);
                        } else if (parameterTypes[i] == boolean.class) {
                            argsArray[i] = Boolean.parseBoolean(values[i]);
                        } else {
                            argsArray[i] = values[i];
                        }
                    }
                }
                csvSourceMethod.invoke(objClassMain, argsArray);
            }
        }
        if(numberAfterSuiteAnnotations > 0) callInvokeMethod(afterSuiteMethod);

    }

}
