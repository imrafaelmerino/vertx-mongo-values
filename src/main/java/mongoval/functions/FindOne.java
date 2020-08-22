package mongoval.functions;

import com.mongodb.client.MongoCollection;
import jsonvalues.JsObj;

import java.util.function.Supplier;

import static mongoval.Converters.getFirst;

public class FindOne extends Find<JsObj> {

    public FindOne(final Supplier<MongoCollection<JsObj>> collection) {
        super(collection,
              getFirst
             );
    }


}
