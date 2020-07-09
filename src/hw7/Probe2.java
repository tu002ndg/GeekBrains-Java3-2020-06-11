package hw7;

public class Probe2 {

    @BeforeSuit
    public void init() {
        System.out.println("загрузка данных класса ");
    }

    @BeforeSuit
    public void init2() {
        System.out.println("загрузка данных класса ");
    }

    @Test(priority = 1)
    public void method1() {
        System.out.println("Выполнение метода 1");
    }

    @Test(priority = 1)
    public void method2() {
        System.out.println("Выполнение метода 2");
    }

    @Test(priority = 3)
    public void method3() {
        System.out.println("Выполнение метода 3");
    }

    @Test(priority = 2)
    public void method4() {
        System.out.println("Выполнение метода 4");
    }

    @AfterSuit
    public void close() {
        System.out.println("Завершение работы класса");
    }
}
