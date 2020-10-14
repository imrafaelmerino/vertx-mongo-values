package mongoval;

import io.vertx.core.eventbus.ReplyException;
import jsonvalues.Prism;
import vertxval.VertxValException;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import static io.vertx.core.eventbus.ReplyFailure.RECIPIENT_FAILURE;

@SuppressWarnings({"serial", "squid:S110"})
public class MongoValException extends VertxValException {


    /**
     Error that happens when the domain can't be resolved: wrong name or there is no internet connection.
     */
    public static final int MONGO_TIMEOUT_CODE = 5000;

    public static final int UNKNOWN_MONGO_EXCEPTION_CODE = 5999;


    public static final Prism<Throwable, VertxValException> prism = new Prism<>(
            t -> {
                if (t instanceof VertxValException) return Optional.of(((VertxValException) t));
                else return Optional.empty();
            },
            v -> v
    );


    public static final Function<Throwable, MongoValException> toMongoValExc =
            exc -> {
                switch (exc.getClass()
                           .getSimpleName()) {
                    case "MongoTimeoutException":
                        return new MongoValException(MONGO_TIMEOUT_CODE,
                                                     exc
                        );

                    default:
                        return new MongoValException(UNKNOWN_MONGO_EXCEPTION_CODE,
                                                     exc
                        );
                }
            };

    public MongoValException(final int code,
                             final String message) {
        super(code,
              message
             );
    }

    public MongoValException(final int code,
                             final Throwable e) {
        super(code,
              e.getStackTrace().length == 0 ?
              e.toString() :
              e.toString() + "@" + Arrays.toString(e.getStackTrace())
             );
    }


}
