package lesson4.part2;

import java.io.*;

//2. Написать небольшой метод, в котором 3 потока
//        построчно пишут данные
//        в файл (по 10 записей с периодом в 20 мс).
public class Lesson4part2 {

    private volatile static char currentChar = 'A';
    public static DataOutputStream out = null;

    public static void main(String[] args) throws InterruptedException {
        //1
        Lesson4part2 monitor = new Lesson4part2();
        Thread t1 = new Thread(() -> {
            printToChar(monitor, 'A', 'B');
        });
        Thread t2 = new Thread(() -> {
            printToChar(monitor, 'B', 'C');
        });
        Thread t3 = new Thread(() -> {
            printToChar(monitor, 'C', 'A');
        });
        t1.start();
        t2.start();
        t3.start();
        t1.join();
        t2.join();
        t3.join();
        System.out.println("\n*********************");
        //2
        try {
            out = new DataOutputStream(new FileOutputStream("1.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Thread thWrite1 = new Thread(() -> {
            monitor.fileWriterText(out, "print thread №1 \n");
        });
        Thread thWrite2 = new Thread(() -> {
            monitor.fileWriterText(out, "print thread №2 \n");
        });
        Thread thWrite3 = new Thread(() -> {
            monitor.fileWriterText(out, "print thread №3 \n");
        });
        thWrite1.start();
        thWrite2.start();
        thWrite3.start();
        thWrite1.join();
        thWrite2.join();
        thWrite3.join();
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("*********************");

        //3
        Mfy mfy = new Mfy();
        mfy.scan("jgrtgrtggger5tg", 20);
        mfy.scan("jrtgtrgtg", 20);
        mfy.print("lh,nktykhplh", 15);
        mfy.print("gtgrtgtg", 5);
    }

    private void fileWriterText(DataOutputStream out, String text) {
        try {
            for (int i = 0; i < 10; i++) {
                out.writeUTF(text);
                System.out.print(text);
                Thread.sleep(20);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void printToChar(Object monitor, char letter1, char letter2) {
        synchronized (monitor) {
            try {
                for (int i = 0; i < 5; i++) {
                    while (currentChar != letter1) {
                        monitor.wait();
                    }
                    System.out.print(currentChar);
                    currentChar = letter2;
                    monitor.notifyAll();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

class Mfy {
    private Object monitorPrint = new Object();
    private Object monitorScan = new Object();

    public void print(String text, int page) {
        new Thread(() -> {
            synchronized (monitorPrint) {
                for (int i = 1; i <= page; i++) {
                    System.out.println("Печать " + text + " страница-" + i);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("\n" + text + "-" + page + " стр. отпечатанно.");
            }
        }).start();
    }

    public void scan(String text, int page) {
        new Thread(() -> {
            synchronized (monitorScan) {
                for (int i = 1; i <= page; i++) {
                    System.out.println("Сканер " + text + " страница " + i);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("***\n" + text + "-" + page + " стр. отсканированно.");
            }
        }).start();
    }
}


