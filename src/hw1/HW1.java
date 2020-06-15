package hw1;


import hw1.fruits.Apple;
import hw1.fruits.Fruit;
import hw1.fruits.Orange;

import java.util.ArrayList;

public class HW1 {
    final static Integer[] arr_int = {0, 1, 2, 3, 4, 5};
    final static String[] arr_str = {"one", "two", "three", "four", "five"};
    final static Object[] arr_obj = {0, "Zero", 2.0, null};

    public static void main(String[] args) {
        new Task1().run();
        new Task2().run();
        new Task3().run();
    }

    static <T> void genericPrintArray(T[] arr) {
        System.out.printf("{");
        for (T item : arr
                ) {
            System.out.printf("%s,", item);
        }
        System.out.printf("}%n");
    }
}

  class Task1 {

        void run() {

            System.out.println("------- task 1 ----");
            swapTwoArrayItems(HW1.arr_int, 0, 3);
            swapTwoArrayItems(HW1.arr_str, 0, 3);
            swapTwoArrayItems(HW1.arr_obj, 0, 3);
            System.out.println("   - generic style -");
            genericSwapTwoArrayItems(HW1.arr_int, 0, 3);
            genericSwapTwoArrayItems(HW1.arr_str, 0, 3);
            genericSwapTwoArrayItems(HW1.arr_obj, 0, 3);
            System.out.println("------- end task 1 ----");
        }

        private void swapTwoArrayItems(Object[] arr, int idx1, int idx2) {
            Object swap = null;
            System.out.println("Array of " + arr.getClass().getSimpleName());
            printArray(arr);
            try {
                swap = arr[idx1];
                arr[idx1] = arr[idx2];
                arr[idx2] = swap;
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Индекс выходит за границы массива");
                return;
            } finally {
                System.out.printf("a[%d]<->a[%d]%n", idx1, idx2);
            }
            printArray(arr);
        }

        private void printArray(Object[] arr) {
            System.out.printf("{");
            for (Object item : arr
                    ) {
                System.out.printf("%s,", item);
            }
            System.out.printf("}%n");
        }


        private <T> void genericSwapTwoArrayItems
                (T[] arr, int idx1, int idx2) {
            T swap;
            System.out.println("Array of " + arr.getClass().getSimpleName());
            HW1.genericPrintArray(arr);
            try {
                swap = arr[idx1];
                arr[idx1] = arr[idx2];
                arr[idx2] = swap;
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Индекс выходит за границы массива");
                return;
            } finally {
                System.out.printf("a[%d]<->a[%d]%n", idx1, idx2);
            }
            HW1.genericPrintArray(arr);
        }
    }

    class Task2 {
        void run() {
            System.out.println("------- task 2 ----");
            System.out.println("Array -> ArrayList");
            ArrayList<Integer> list_int =
                    genericArrayToList(HW1.arr_int);
            System.out.println(list_int);
            ArrayList list_str =
                    genericArrayToList(HW1.arr_str);
            System.out.println(list_str);
            ArrayList<Object> list_obj =
                    genericArrayToList(HW1.arr_obj);
            System.out.println(list_obj);
            System.out.println("---   end task 2 ---");
        }

        private <T> ArrayList<T> genericArrayToList(T[] arr) {
            ArrayList arrayList = new ArrayList<T>();
            for (T item: arr
                 ) {
                arrayList.add(item);
            }
            return arrayList;
        }
    }

    class Task3 {
        void run() {
            System.out.println("--- task 3 ---");
            System.out.println("-------- Boxes with fruits -----");
            // box with apples
            BoxWithFruits<Apple> b1 =
                    createBoxWithFruits(new Apple(),10, "b1");

            // another box with apples
            BoxWithFruits<Apple> b2 =
                    createBoxWithFruits(new Apple(),14, "b2");

            b1.moveTo(b2);
            System.out.println("b1->b2");
            System.out.printf("[b1]: %s%n",b1);
            System.out.printf("[b2]: %s%n",b2);

            // box with orange
            BoxWithFruits<Orange> b3 =
                    createBoxWithFruits(new Orange(),16, "b3");

            System.out.println("Compare boxes weights:");
            System.out.println("b1 = b2? "+b1.compareByWeight(b2));
            System.out.println("b2 = b3? "+b2.compareByWeight(b3));
            System.out.println("---   end task 3 ---");

        }

         private BoxWithFruits createBoxWithFruits(
                 Fruit f,int num, String name) {
            BoxWithFruits b = new BoxWithFruits();
            b.addFruitInBox(f,num);
            System.out.printf("box with <%s> [%s]: %s%n",
                    f.getClass().getSimpleName(),name, b);
            return b;
        }

    }



