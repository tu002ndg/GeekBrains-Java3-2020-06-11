package hw7;

public class Main {

    public static void main(String[] args) {
        System.out.println("--Выполняем тесты--");
        try {
            Tester.start(Probe1.class);
            System.out.println();
            Tester.start(Probe2.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
