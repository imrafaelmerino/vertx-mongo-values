package vertx.mongodb.effect.functions;

import com.mongodb.client.MongoCollection;
import vertx.mongodb.effect.Converters;
import jsonvalues.JsObj;

import java.util.function.Supplier;

public class FindOne extends Find<JsObj> {

    public FindOne(final Supplier<MongoCollection<JsObj>> collectionSupplier) {
        super(collectionSupplier,
              Converters.findIterableHead
             );
    }


}
