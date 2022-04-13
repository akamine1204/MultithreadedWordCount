import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


public class Main {

    //chuyen file thanh string
    public static String[] fileToString(File[] files){
        String[] PathString = new String[files.length];
        int i = 0;

        for(File f : files){
            try {
                byte[] bytes = Files.readAllBytes(f.toPath());
                PathString[i] = new String(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            i++;
        }
        return PathString;
    }


    public static void main(String[]args){

        //chuyen cac File thanh cac String
        File file = new File("Data");
        File[] pathFile = file.listFiles();
        String PathString[] = fileToString(pathFile);


        //khoi tao 1 threadpool voi 5 thread toi da hoat dong song song
        List<Future<Map<String,Integer>>> futureList = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(5);

        Future<Map<String,Integer>> future;
        Callable<Map<String,Integer>> callable;

        for(String str : PathString){
            callable = new WordCountRunnable(str);
            future = executor.submit(callable);

            futureList.add(future);
        }

        // shut down the executor service now
        executor.shutdown();

        // Cho cho den khi tat ca cac luong hoat dong xong
        while (!executor.isTerminated()) {}

        //WordCountMap la` 1 hashMap tong hop tat ca cac word duoc dem tu 10 file dau` vao`
        Map<String,Integer> WordCountMap = new HashMap<>();
        //Thuc hien merge cac Future sau khi submit tra ve vao WordCountMap
        for(Future<Map<String,Integer>> fut : futureList){
            Map<String,Integer> map =null;
            try {
                map = new HashMap<String,Integer>(fut.get());
                map.forEach((k,v) -> WordCountMap.merge(k,v,(v1,v2)-> v1+v2));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Dem so tu xuat hien trong tat ca cac file input: ");
        System.out.println(WordCountMap);
        System.out.println();

        //Thuc hien sap xep cac Entry trong WordCountMap theo thu tu giam dan value va truyen 10 phan tu dau tien vao 1 linkedHashMap
        Map <String,Integer> newMap = WordCountMap.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(10)
                .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1, LinkedHashMap::new));
        System.out.println("10 phan tu co tan so xuat hien nhieu nhat la: ");
        System.out.println(newMap);


        //tao 1 file output.txt va ghi vao file cac phan tu trong linkedHashMap vua khoi tao ben tren
        FileWriter fw = null;
        try {
            fw = new FileWriter("Data/output.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fw.write("Top 10 tu co tan so xuat hien nhieu nhat trong 10 file text la:\n");
            for(Map.Entry<String,Integer> entry : newMap.entrySet()){
                fw.write(entry.getKey().toString()+" - "+entry.getValue().toString()+"\n");

            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
