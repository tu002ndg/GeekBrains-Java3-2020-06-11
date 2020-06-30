package hw5;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;


public class MainClass {
    public static final int CARS_COUNT = 4;
    public static CountDownLatch cdl0;
    public static CountDownLatch cdl;
    public static CyclicBarrier cb;
    public static Semaphore smp;

    public static void main(String[] args) {
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
        Race race = new Race(new Road(60), new Tunnel(), new Road(40));
        Car[] cars = new Car[CARS_COUNT];
        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Car(race, 20 + (int) (Math.random() * 10));
        }
        cdl0 = new CountDownLatch(cars.length);
        cdl = new CountDownLatch(cars.length);
        cb = new CyclicBarrier(cars.length);
        smp = new Semaphore(cars.length/2);

        for (int i = 0; i < cars.length; i++) {
            new Thread(cars[i]).start();
        }
        try {
            cdl0.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");
    }
}
