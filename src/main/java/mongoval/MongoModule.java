package mongoval;


import com.mongodb.client.MongoCollection;
import io.vertx.core.DeploymentOptions;
import jsonvalues.JsObj;
import vertxval.VertxModule;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public abstract class MongoModule extends VertxModule {
    private static final DeploymentOptions DEFAULT_DEPLOYMENT_OPTIONS = new DeploymentOptions().setWorker(true);
    public final Supplier<MongoCollection<JsObj>> collection;


    public MongoModule(final DeploymentOptions deploymentOptions,
                       final Supplier<MongoCollection<JsObj>> collection) {
        super(deploymentOptions);
        this.collection = requireNonNull(collection);
    }

    public MongoModule(final Supplier<MongoCollection<JsObj>> collection) {
        super(DEFAULT_DEPLOYMENT_OPTIONS);
        this.collection = requireNonNull(collection);
    }



}
