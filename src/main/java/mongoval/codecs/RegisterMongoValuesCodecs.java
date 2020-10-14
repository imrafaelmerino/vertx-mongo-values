package mongoval.codecs;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import mongoval.FindMessage;
import mongoval.UpdateMessage;
public class RegisterMongoValuesCodecs extends AbstractVerticle {

    @Override
    public void start(final Promise<Void> startPromise)  {
        try {
            vertx.eventBus()
                 .registerDefaultCodec(UpdateMessage.class,
                                       UpdateMessageCodec.INSTANCE
                                      );
            vertx.eventBus()
                 .registerDefaultCodec(FindMessage.class,
                                       FindMessageCodec.INSTANCE
                                      );
            startPromise.complete();
        } catch (Exception e) {
           startPromise.fail(e);
        }


    }
}
