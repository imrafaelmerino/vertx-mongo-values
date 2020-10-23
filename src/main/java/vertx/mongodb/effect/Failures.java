package vertx.mongodb.effect;

import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.eventbus.ReplyFailure;
import jsonvalues.Prism;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings({"serial", "squid:S110"})
public class Failures {


    /**
     Error that happens when the domain can't be resolved: wrong name or there is no internet connection.
     */
    public static final int MONGO_TIMEOUT_CODE = 5000;

    public static final int UNKNOWN_MONGO_EXCEPTION_CODE = 5999;

    public static final Prism<Throwable, ReplyException> MONGO_TIMEOUT_PRISM =
            new Prism<>(e -> {
                if (e instanceof ReplyException) {
                    ReplyException replyException = (ReplyException) e;
                    if (replyException.failureCode() == MONGO_TIMEOUT_CODE)
                        return Optional.of(replyException);
                }
                return Optional.empty();
            },
                        e -> e
            );


    public static final Function<Throwable, ReplyException> toMongoValExc =
            exc -> {
                switch (exc.getClass()
                           .getSimpleName()) {
                    case "MongoTimeoutException":
                        return new ReplyException(ReplyFailure.RECIPIENT_FAILURE,
                                                  MONGO_TIMEOUT_CODE,
                                                  getMessage(exc)
                        );

                    default:
                        return new ReplyException(ReplyFailure.RECIPIENT_FAILURE,
                                                  UNKNOWN_MONGO_EXCEPTION_CODE,
                                                  getMessage(exc)
                        );
                }
            };

    private static String getMessage(final Throwable e) {
        return e.getStackTrace().length == 0 ?
               e.toString() :
               e.toString() + "@" + Arrays.toString(e.getStackTrace());
    }

}
