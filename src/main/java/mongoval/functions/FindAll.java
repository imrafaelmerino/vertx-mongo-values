package mongoval.functions;

import com.mongodb.client.MongoCollection;
import jsonvalues.JsArray;
import jsonvalues.JsObj;

import java.util.function.Supplier;

import static mongoval.Converters.toJsArray;


public class FindAll extends Find<JsArray> {

    public FindAll(final Supplier<MongoCollection<JsObj>> collection) {
        super(collection,
              toJsArray
             );
    }


}
