package mongo.actors;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import jsonvalues.JsObj;

public class UpdateMessageCodec implements MessageCodec<UpdateMessage, UpdateMessage> {

  public static final UpdateMessageCodec INSTANCE = new UpdateMessageCodec();

  private void UpdateMessage(){};

  @Override
  public void encodeToWire(final Buffer buffer,
                           final UpdateMessage updateMessage) {
    byte[] filter = updateMessage.filter.serialize();
    byte[] update = updateMessage.update.serialize();
    buffer.appendInt(filter.length);
    buffer.appendInt(update.length);
    buffer.appendBytes(filter);
    buffer.appendBytes(update);
  }

  @Override
  public UpdateMessage decodeFromWire(int pos,
                                      final Buffer buffer) {
    int filterLength = buffer.getInt(pos);
    pos += 4;
    int updateLength = buffer.getInt(pos);
    pos += 4;
    String filter = buffer.getString(pos,
                                     pos + filterLength);
    pos += filterLength;
    String update = buffer.getString(pos,
                                     pos + updateLength);
    return new UpdateMessage(JsObj.parse(filter),JsObj.parse(update));
  }

  @Override
  public UpdateMessage transform(final UpdateMessage message) {
    return message;
  }

  @Override
  public String name() {
    return "mongo-update-docs";
  }

  @Override
  public byte systemCodecID() {
    return -1;
  }
}
