package co.za.assignment.Assignment.writer;

import org.springframework.batch.item.support.AbstractItemStreamItemWriter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * @author : Mpholo Leboea
 * @Created : 2022/04/05
 **/
public class ConsoleItemWriter extends AbstractItemStreamItemWriter {

    public static Map<String,Integer> words = new HashMap<>();

    @Override
    public void write(List list) throws Exception {
        Map<String, Integer> wordsCounter = new HashMap<>();
        BiFunction<String, Integer, Integer> mapper = (k, v) -> v + 1;

        list.stream().forEach(line ->{

            System.out.println(line);
            String[] words = line.toString().split(" ");
            System.out.println("Words in line "+words.length);
            for(String w:words) {
                if(!w.equals(" ")) {
                    final Integer value = wordsCounter.get(w);
                    if(value!=null) {
                        wordsCounter.computeIfPresent(w,mapper);
                    } else {
                        wordsCounter.put(w, 1);
                    }
                }


            }
        });

        wordsCounter.forEach( (k,v) -> {
            BiFunction<String, Integer, Integer> mapper2 = (k1, v1) -> v1 + v;
            if(words.get(k)!=null) {
                words.computeIfPresent(k,mapper2);
            } else {
                words.put(k,v);
            }
            }
        );
        System.out.println("words in sentence "+ wordsCounter);

    }
}
