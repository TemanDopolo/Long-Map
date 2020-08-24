import de.comparus.opensource.longmap.LongMap;
import de.comparus.opensource.longmap.LongMapImpl;
import de.comparus.opensource.longmap.exceptions.NoBucketWithSuchKeyException;
import de.comparus.opensource.longmap.utils.ContextValidation;
import de.comparus.opensource.longmap.utils.MapValidationStrategyImpl;

import java.util.Arrays;

public class Application {
    public static void main(String[] args) throws NoBucketWithSuchKeyException {
        LongMap map = new LongMapImpl();
        ContextValidation context = new ContextValidation(new MapValidationStrategyImpl());
        ((LongMapImpl) map).setContext(context);

        map.put(1L, "string1");
//        check NoBucketWithSuchKeyException generation
//        map.put(-2L, "string2");
        map.put(3L, "string3");
        map.put(4L, "string4");
        System.out.println(map.size());
        System.out.println(map.get(2L));

        System.out.println(map.containsKey(1L));
        System.out.println(map.containsValue("string4"));
        System.out.println(map.containsValue(null));

        Arrays.stream(map.keys()).forEach(System.out::println);
        Arrays.stream(map.values()).forEach(System.out::println);

        map.remove(1L);
        System.out.println(map.size());
        map.clear();
        System.out.println(map.size());

    }
}
