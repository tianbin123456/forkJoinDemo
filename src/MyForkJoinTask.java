import com.sun.org.apache.xerces.internal.dom.PSVIAttrNSImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * @Auther: T
 * @Date: 2019/10/18 09:31
 * @Description:
 */
public class MyForkJoinTask extends RecursiveTask<List<String>> {
    private static MyForkJoinTask myForkJoinTask;
    private List<String> list;
    protected int threshold=2;

    private MyForkJoinTask(List<String> list){
        this.list=list;
    }

    @Override
    protected List<String> compute() {
        if(list.size()<2){
            List<String> list1=new ArrayList<>();
            list1.add(list.get(0)+"AA");
            return  list1;
        }
        else {
            int middle = list.size() / 2;
            List<String> leftList = list.subList(0, middle);
            List<String> rightList = list.subList(middle, list.size());
            MyForkJoinTask left = new MyForkJoinTask(leftList);
            MyForkJoinTask right = new MyForkJoinTask(rightList);
            left.fork();
            right.fork();
            List<String> join =left.join();
            join.addAll(right.join());
            return join;
        }
    }


    public static MyForkJoinTask getInstance(List<String> list) {
        if (myForkJoinTask == null) {
            synchronized (MyForkJoinTask.class) {
                if (myForkJoinTask == null) {
                    myForkJoinTask = new MyForkJoinTask(list);
                }
            }
        }
        return myForkJoinTask;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        List<String> list2=new ArrayList<>();
        for (int i=0;i<100;i++){
            list2.add(i+"");
        }
        MyForkJoinTask myForkJoinTask=MyForkJoinTask.getInstance(list2);
        ForkJoinPool pool=new ForkJoinPool();
        ForkJoinTask<List<String>> my= pool.submit(myForkJoinTask);
        System.out.println(my.get());
        pool.shutdown();
    }
}
