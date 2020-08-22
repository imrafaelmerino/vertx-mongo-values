package mongoval;

import jsonvalues.JsObj;


public class UpdateMessage {
  public final JsObj filter;
  public final JsObj update;


  public UpdateMessage(final JsObj filter,
                       final JsObj update) {
    this.filter = filter;
    this.update = update;
  }
}
