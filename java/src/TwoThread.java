public class TwoThread{

    private static int counter = 1;
    private static final int MAX = 10;
    
    private static final Object lock = new Object();

    public static void main(String[] args) {
        
        Thread thread2 = new Thread(() -> {
            while (counter < MAX) {
                synchronized (lock) {
             
                    while (counter % 2 == 0) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                   
                    if (counter <= MAX) {
                        System.out.println("Thread 2 (Odd)  : " + counter);
                        counter++;
                        lock.notify(); 
             

                    }
                }
            }
        });

        Thread thread1 = new Thread(() -> {
        	
//        	try { Thread.sleep(5000); } catch (InterruptedException e) {}
        	
            while (counter <= MAX) {

                synchronized (lock) {
                    while (counter % 2 != 0) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }

                    if (counter <= MAX) {
                        System.out.println("Thread 1 (Even) : " + counter);
                        counter++;
                        lock.notify();   
                    
                    }
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                
            }
        });
        
        thread2.start();
        thread1.start();
    }
}