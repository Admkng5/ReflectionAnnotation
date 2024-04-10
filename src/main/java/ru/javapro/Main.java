package ru.javapro;

public class Main {

    @BeforeSuite
    public static void beforeSuiteAnnotationRunner() {
        System.out.println("This is beforeSuiteAnnotationRunner");
    }

    @BeforeTest
    public static void beforeTestAnnotationRunner() {
        System.out.println("This is beforeTestAnnotationRunner");
    }

    @Test
    public void testAnnotationRunner() {
        System.out.println("This is testAnnotationRunner");
    }

    @Test(priority = 8)
    public void testAnnotationRunnerHighPriority() {
        System.out.println("This is testAnnotationRunnerHighPriority");
    }

    @CsvSource("10, Java, 20, true")
    public void testMethod(int a, String b, int c, boolean d) {
        System.out.println("This is CsvSource method with parameters: " + a + ", " + b + ", " + c + ", " + d + ", ");
    }

    @AfterTest
    public static void afterTestAnnotationRunner() {
        System.out.println("This is afterTestAnnotationRunner");
    }

    @AfterSuite
    public static void afterSuiteAnnotationRunner() {
        System.out.println("This is afterSuiteAnnotationRunner");
    }

}
