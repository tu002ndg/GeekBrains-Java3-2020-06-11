package hw7;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class Tester {

    private static Object ts = null;


    public static void start(Class testClass) throws Exception {

        try {
            ts = testClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        doTest();
    }

    private static void doTest() throws Exception {
        Class cl = ts.getClass();
        System.out.printf("Тестирование класса %s%n",
                cl.getSimpleName());


        System.out.print("Before suit: ");
        Method beforeSuit;
        Method afterSuit;
        Method methods [] = cl.getDeclaredMethods();

        if (Arrays.stream(methods)
                .filter(m->m.getAnnotation(BeforeSuit.class)!=null).count()>1)
             throw new Exception("@BeforeSuite должен\n присутствовать в единственном экземпляре");

        if (Arrays.stream(methods)
                .filter(m->m.getAnnotation(AfterSuit.class)!=null).count()>1)
            throw new Exception("@AfterSuite должен\n присутствовать в единственном экземпляре");

        try {
            beforeSuit =
            Arrays.stream(methods)
                    .filter(m->m.getAnnotation(BeforeSuit.class)!=null)
                    .findFirst().get();
            beforeSuit.invoke(ts,null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        System.out.println("Tests: ");
        Arrays.stream(methods)
                .filter(m->m.getAnnotation(Test.class)!=null)
                .sorted((m1,m2)-> m1.getAnnotation(Test.class).priority()>m2.getAnnotation(Test.class).priority()?1:
                m1.getAnnotation(Test.class).priority()<m2.getAnnotation(Test.class).priority()?-1:0)
                .forEach(method -> {
                    try {
                        System.out.printf(" (приоритет: %d) ",
                                method.getAnnotation(Test.class).priority());
                        method.invoke(ts,null);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                });
        System.out.print("After suit: ");
        try {
            afterSuit =
            Arrays.stream(methods)
                    .filter(m->m.getAnnotation(AfterSuit.class)!=null)
                    .findFirst().get();
            afterSuit.invoke(ts,null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }
}
