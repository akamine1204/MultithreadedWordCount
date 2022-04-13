import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class WordCountRunnable implements Callable<Map<String,Integer>> {
    private String pathString;
    private HashMap<String,Integer> map = new HashMap<>();


    //constructor
    public WordCountRunnable(String pathString){
        this.pathString = pathString;
    }

    //overriding call method
    public HashMap<String,Integer> call() throws Exception {
        String[] word = this.pathString.toLowerCase().split("[^a-zA-Z]+");
        for(String str : word){
            if(!map.containsKey(str)) map.put(str,1);
            else map.replace(str,map.get(str),map.get(str)+1);
        }
        return map;
    }

}
