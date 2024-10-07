import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
public class ArrayProcessing {

    static final int SIZE = 40;
    static final int HALF = SIZE / 2;

    public static void main(String[] args) {
        method1();
        method2();
        method3(4);
    }

    public static void method1() {
        float[] arr = new float[SIZE];
        // ���������� ������� ���������
        for (int i = 0; i < SIZE; i++) {
            arr[i] = 1.0f;
        }

        long startTime = System.currentTimeMillis();

        // ����������
        for (int i = 0; i < SIZE; i++) {
            arr[i] = (float) (arr[i] * Math.sin(0.2f + i / 5.0) * Math.cos(0.2f + i / 5.0) * Math.cos(0.4f + i / 2.0));
        }

        long endTime = System.currentTimeMillis();
        System.out.println("����� ���������� ������������� ������ " + (endTime - startTime) + " ��");
        System.out.println("������ �������: " + arr[0] + ", ��������� �������: " + arr[SIZE - 1]);
    }

    public static void method2() {
        float[] arr = new float[SIZE];

        for (int i = 0; i < SIZE; i++) {
            arr[i] = 1.0f;
        }

        float[] a1 = new float[HALF];
        float[] a2 = new float[HALF];

        long startTime = System.currentTimeMillis();

        // ���������� ������� �� ���
        System.arraycopy(arr, 0, a1, 0, HALF);
        System.arraycopy(arr, HALF, a2, 0, HALF);

        // �������� �������
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < HALF; i++) {
                a1[i] = (float) (a1[i] * Math.sin(0.2f + i / 5.0) * Math.cos(0.2f + i / 5.0) * Math.cos(0.4f + i / 2.0));
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < HALF; i++) {
                a2[i] = (float) (a2[i] * Math.sin(0.2f + (i + HALF) / 5.0) * Math.cos(0.2f + (i + HALF) / 5.0) * Math.cos(0.4f + (i + HALF) / 2.0));
            }
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // ������� �������� ������� � ����
        System.arraycopy(a1, 0, arr, 0, HALF);
        System.arraycopy(a2, 0, arr, HALF, HALF);

        long endTime = System.currentTimeMillis();
        System.out.println("����� ���������� �������������� ������: " + (endTime - startTime) + " ��");
        System.out.println("������ �������: " + arr[0] + ", ��������� �������: " + arr[SIZE - 1]);
    }

    public static void method3(int numThreads) {
        float[] arr = new float[SIZE];

        for (int i = 0; i < SIZE; i++) {
            arr[i] = 1.0f;
        }

        long startTime = System.currentTimeMillis();
        // ��������� ������ �� ����� ��� ������� ������
        float[][] parts = new float[numThreads][];
        int partSize = SIZE / numThreads;
        int remainder = SIZE % numThreads;

        // �������������� ����� �������
        for (int i = 0; i < numThreads; i++) {
            int currentPartSize = partSize + (i == 0 ? remainder : 0);
            parts[i] = new float[currentPartSize];
            System.arraycopy(arr, i * partSize + (i == 0 ? 0 : remainder), parts[i], 0, currentPartSize);
        }

        CountDownLatch latch = new CountDownLatch(numThreads);

        // ��������� ������
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            final int index = i;
            threads[index] = new Thread(() -> {
                for (int j = 0; j < parts[index].length; j++) {
                    parts[index][j] = (float) (parts[index][j] * Math.sin(0.2f + (j + index * partSize + (index == 0 ? 0 : remainder)) / 5.0) * Math.cos(0.2f + (j + index * partSize + (index == 0 ? 0 : remainder)) / 5.0) * Math.cos(0.4f + (j + index * partSize + (index == 0 ? 0 : remainder)) / 2.0));
                }
                System.out.println("����� " + index + " �������� ��������: " + Arrays.toString(parts[index]));
                latch.countDown();
            });
            threads[index].start();
        }

        // ���� ���������� ���� �������
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < numThreads; i++) {
            System.arraycopy(parts[i], 0, arr, i * partSize + (i == 0 ? 0 : remainder), parts[i].length);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("����� ���������� ������ � " + numThreads + " ��������: " + (endTime - startTime) + " ��");
        System.out.println("������ �������: " + arr[0] + ", ��������� �������: " + arr[SIZE - 1]);
    }
}
