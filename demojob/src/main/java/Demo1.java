/**
 * @author zyj
 * @date 2019/5/19
 */
public class Demo1 {

    public static void main(String[] args) throws InterruptedException {


        Thread thread = new Thread(new Runnable() {
            public void run() {
                while (true) {

                    try {
                        Thread.sleep(3000L);



                        System.out.println("hello");

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        thread.run();
        Thread.sleep(10000L);




    }
}
