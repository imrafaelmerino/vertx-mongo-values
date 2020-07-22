package mongo.actors;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class RegisterMongoCodecs extends AbstractVerticle {

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
