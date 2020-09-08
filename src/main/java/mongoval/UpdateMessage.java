package mongoval;

import jsonvalues.JsObj;


import static java.util.Objects.requireNonNull;


public class UpdateMessage {
    public final JsObj filter;
    public final JsObj update;


    public UpdateMessage(final JsObj filter,
                         final JsObj update
                        ) {
        this.filter = requireNonNull(filter);
        this.update = requireNonNull(update);
    }
}
