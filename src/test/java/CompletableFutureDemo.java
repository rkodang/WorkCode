import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class CompletableFutureDemo {

    static List<MyRoom> roomList = Arrays.asList(new MyRoom("A"), new MyRoom("B"), new MyRoom("C"), new MyRoom("D"), new MyRoom("E"), new MyRoom("F"),new MyRoom("G"),new MyRoom("h"),new MyRoom("I"));

    public static void main(String[] args) throws Exception {
       test7();

    }

    private static void test7() {
        CompletableFuture<Integer> A = CompletableFuture.supplyAsync(() -> {
            System.err.println("haha");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 10;
        });

        CompletableFuture<Integer> B = CompletableFuture.supplyAsync(() -> {
            System.err.println("hihi");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 20;
        });

        CompletableFuture<Integer> C = A.thenCombine(B, (x, y) -> {
            return x * 10 + y * 2;
        });
        System.err.println(C.join());
    }

    private static void test6() {
        CompletableFuture<String> A = CompletableFuture.supplyAsync(() -> {
            System.err.println("haha");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "A";
        });

        CompletableFuture<String> B = CompletableFuture.supplyAsync(() -> {
            System.err.println("hihi");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "B";
        });
        CompletableFuture<String> result = A.applyToEither(B, f -> {
            int i = 10;
            String abc = "hello";
            return f + "quick";
        });
        System.err.println(result.join());

    }

    /**
     * thenRun A执行完到B,B不需要A的结果
     * thenAccept A执行完到B,B需要A的结果,但是B没有返回值
     * thenApply A执行完到B,B需要A的结果,但是B有返回值
     *
     */
    private static void test5() {
        CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
            return "hello";
        }).handle((f,e) -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return f + "then";
        }).handle((f,e)->{
            return f+"2";
        }).whenComplete((f,e)->{
            if (e!=null) {
                System.err.println(f+"error");
                e.printStackTrace();
            }
        });
        System.err.println(stringCompletableFuture.join());
        System.err.println("------------------------------");
        System.err.println(CompletableFuture.supplyAsync(() -> "AAA").thenRun(() -> { }).join());
        System.err.println("------------------------------");
        System.err.println(CompletableFuture.supplyAsync(() -> "AAA").thenAccept(r-> System.err.println(r+"  "+"thenAccept")).join());
        System.err.println("------------------------------");
        System.err.println(CompletableFuture.supplyAsync(() -> "AAA").thenApply(r-> r+"thenApply").join());
    }

    private static void test4() {
        long start = System.currentTimeMillis();
        List<String> one = testGetNormal(roomList, "count");
        for (String s : one) {
            System.err.println(s);
        }
        long End = System.currentTimeMillis();
        System.err.println("用时:" + (End - start) + "毫秒");

        long start1 = System.currentTimeMillis();
        List<String> two = testGetAsync(roomList, "big");
        for (String s : two) {
            System.err.println(s);
        }
        long End1 = System.currentTimeMillis();
        System.err.println("用时:" + (End1 - start1) + "毫秒");
    }

    private static List<String> testGetAsync(List<MyRoom> list, String peopleName) {
        return list.stream().map(f -> CompletableFuture.supplyAsync(() -> String.format(peopleName + " so good %s + and num is %.2f", f.getRoomName(), f.yoHo(peopleName)))).collect(Collectors.toList()).stream().map(f -> f.join()).collect(Collectors.toList());
    }

    private static List<String> testGetNormal(List<MyRoom> list, String peopleName) {
        return list.stream().map(f -> String.format(peopleName + " so good %s + and num is %.2f", f.getRoomName(), f.yoHo(peopleName))).collect(Collectors.toList());
    }

    private static void test3() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        CompletableFuture<Void> shit = CompletableFuture.runAsync(() -> {
            System.err.println("shit," + Thread.currentThread().getName());
        }, executorService);
        System.err.println(shit.get());

        CompletableFuture<String> cp = CompletableFuture.supplyAsync(() -> {
            return "shutDown," + Thread.currentThread().getName();
        }, executorService);
        executorService.shutdown();
    }

    private static void test2() throws Exception {
        FutureTask<String> futureTask = new FutureTask<String>(() -> {
            System.err.println("Yo....");
            TimeUnit.SECONDS.sleep(5);
            return "overTime";
        });
        new Thread(futureTask, "GDX").start();
        System.err.println("sick");
        //阻塞了GG~
        System.err.println(futureTask.get(20, TimeUnit.SECONDS));
    }

    private static void test1() throws Exception {
        FutureTask<String> futureTask = new FutureTask<>(new MyThread());
        new Thread(futureTask, "U").start();
        System.err.println(futureTask.get());
    }

}

class MyThread implements Callable<String> {
    @Override
    public String call() throws Exception {
        return "你是什么Callable的" + Thread.currentThread().getName();
    }
}

class MyRoom {
    private String roomName;

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public MyRoom(String roomName) {
        this.roomName = roomName;
    }

    public double yoHo(String peopleName) {
        try {
            TimeUnit.MILLISECONDS.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ThreadLocalRandom.current().nextDouble() * 2 + peopleName.charAt(0);
    }
}