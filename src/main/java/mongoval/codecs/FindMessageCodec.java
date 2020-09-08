package mongoval.codecs;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import jsonvalues.*;
import mongoval.FindMessage;
import mongoval.FindMessageBuilder;

import java.util.concurrent.TimeUnit;

public class FindMessageCodec implements MessageCodec<FindMessage, FindMessage> {

    static final FindMessageCodec INSTANCE = new FindMessageCodec();

    private FindMessageCodec() {
    }

    private static final String NO_CURSOR_TIMEOUT = "noCursorTimeout";
    private static final String HINT_STRING = "hintString";
    private static final String HINT = "hint";
    private static final String MAX = "max";
    private static final String MIN = "min";
    private static final String MAX_TIME = "maxTime";
    private static final String SKIP = "skip";
    private static final String LIMIT = "limit";
    private static final String COMMENT = "comment";
    private static final String FILTER = "filter";
    private static final String BATCH_SIZE = "batchSize";
    private static final String SORT = "sort";
    private static final String PROJECTION = "projection";
    private static final String MAX_AWAIT_TIME = "maxAwaitTime";
    private static final String OP_LOG_REPLAY = "oplogReplay";
    private static final String SHOW_RECORD_ID = "showRecordId";
    private static final String PARTIAL = "partial";
    private static final String RETURN_KEY = "returnKey";

    @Override
    public void encodeToWire(final Buffer buffer,
                             final FindMessage findMessage) {


        JsObj  obj   = toJsObj(findMessage);
        byte[] bytes = obj.serialize();

        buffer.appendInt(bytes.length);
        buffer.appendBytes(bytes);

    }

    private JsObj toJsObj(final FindMessage findMessage) {
        JsObj options = JsObj.empty();

        options = options.set(FILTER,
                              findMessage.filter
                             );

        options.set(LIMIT,
                    JsInt.of(findMessage.limit)
                   );

        options = options.set(BATCH_SIZE,
                              JsInt.of(findMessage.batchSize)
                             );

        options = options.set(MAX_AWAIT_TIME,
                              JsLong.of(findMessage.maxAwaitTime)
                             );

        options = options.set(MAX_TIME,
                              JsLong.of(findMessage.maxTime)
                             );

        options = options.set(SKIP,
                              JsInt.of(findMessage.skip)
                             );

        options = options.set(NO_CURSOR_TIMEOUT,
                              JsBool.of(findMessage.noCursorTimeout)
                             );
        options = options.set(OP_LOG_REPLAY,
                              JsBool.of(findMessage.oplogReplay)
                             );
        options = options.set(SHOW_RECORD_ID,
                              JsBool.of(findMessage.showRecordId)
                             );
        options = options.set(PARTIAL,
                              JsBool.of(findMessage.partial)
                             );
        options = options.set(RETURN_KEY,
                              JsBool.of(findMessage.returnKey)
                             );

        String hintString = findMessage.hintString;
        if (hintString != null) options = options.set(HINT_STRING,
                                                      JsStr.of(hintString)
                                                     );
        JsObj hint = findMessage.hint;
        if (hint != null) options = options.set(HINT,
                                                hint
                                               );
        JsObj projection = findMessage.projection;
        if (projection != null) options = options.set(PROJECTION,
                                                      projection
                                                     );
        JsObj sort = findMessage.sort;
        if (sort != null) options = options.set(SORT,
                                                sort
                                               );

        String comment = findMessage.comment;
        if (comment != null) options = options.set(COMMENT,
                                                   JsStr.of(comment)
                                                  );
        JsObj max = findMessage.max;
        if (max != null) options = options.set(MAX,
                                               max
                                              );
        JsObj min = findMessage.min;
        if (min != null) options = options.set(MIN,
                                               min
                                              );



        return options;
    }

    @Override
    public FindMessage decodeFromWire(int pos,
                                      final Buffer buffer) {
        int length = buffer.getInt(pos);
        byte[] bytes = buffer.getBytes(pos + 4,
                                       pos + length
                                      );
        JsObj options = JsObj.parse(new String(bytes));

        Boolean noCursorTimeout = options.getBool(NO_CURSOR_TIMEOUT);
        Boolean oplogReplay     = options.getBool(OP_LOG_REPLAY);
        Boolean partial         = options.getBool(PARTIAL);
        Boolean returnKey       = options.getBool(RETURN_KEY);
        Boolean showRecordId    = options.getBool(SHOW_RECORD_ID);
        Integer maxTime         = options.getInt(MAX_TIME);
        Integer maxAwaitTime    = options.getInt(MAX_AWAIT_TIME);
        FindMessageBuilder builder = new FindMessageBuilder().batchSize(options.getInt(BATCH_SIZE))
                                                             .comment(options.getStr(COMMENT))
                                                             .filter(options.getObj(FILTER))
                                                             .hint(options.getObj(HINT))
                                                             .limit(options.getInt(LIMIT))
                                                             .hintString(options.getStr(HINT_STRING))
                                                             .max(options.getObj(MAX))
                                                             .min(options.getObj(MIN))
                                                             .noCursorTimeout(Boolean.TRUE.equals(noCursorTimeout))
                                                             .oplogReplay(Boolean.TRUE.equals(oplogReplay))
                                                             .partial(Boolean.TRUE.equals(partial))
                                                             .projection(options.getObj(PROJECTION))
                                                             .returnKey(Boolean.TRUE.equals(returnKey))
                                                             .showRecordId(Boolean.TRUE.equals(showRecordId))
                                                             .skip(options.getInt(SKIP))
                                                             .sort(options.getObj(SORT));
        if (maxTime != null) builder.maxTime(maxTime,
                                             TimeUnit.MILLISECONDS
                                            );
        if (maxAwaitTime != null) builder.maxAwaitTime(maxAwaitTime,
                                                       TimeUnit.MILLISECONDS
                                                      );
        return builder.create();

    }

    @Override
    public FindMessage transform(final FindMessage findMessage) {
        return findMessage;
    }

    @Override
    public String name() {
        return "mongo-find-message";
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }


}
