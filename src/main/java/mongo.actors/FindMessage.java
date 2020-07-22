package mongo.actors;

import jsonvalues.JsObj;

import java.util.Objects;

public class FindMessage {
    FindMessage(final JsObj filter,
                final JsObj sort,
                final JsObj projection,
                final JsObj hint,
                final JsObj max,
                final JsObj min,
                final String hintString,
                final Integer skip,
                final Integer limit,
                final boolean showRecordId,
                final boolean returnKey,
                final String comment,
                final boolean noCursorTimeout,
                final boolean partial,
                final boolean oplogReplay,
                final Integer batchSize,
                final Long maxAwaitTime,
                final Long maxTime) {
        this.filter = Objects.requireNonNull(filter);
        this.sort = sort;
        this.projection = projection;
        this.hint = hint;
        this.max = max;
        this.min = min;
        this.hintString = hintString;
        this.skip = skip;
        this.limit = limit;
        this.showRecordId = showRecordId;
        this.returnKey = returnKey;
        this.comment = comment;
        this.noCursorTimeout = noCursorTimeout;
        this.partial = partial;
        this.oplogReplay = oplogReplay;
        this.batchSize = batchSize;
        this.maxAwaitTime = maxAwaitTime;
        this.maxTime = maxTime;
    }


    public final JsObj filter;

    public final JsObj sort;

    public final JsObj projection;

    public final JsObj hint;

    public final JsObj max;

    public final JsObj min;

    public final String hintString;

    public final Integer skip;

    public final Integer limit;

    public final boolean showRecordId;

    public final boolean returnKey;

    public final String comment;

    public final boolean noCursorTimeout;

    public final boolean partial;

    public final boolean oplogReplay;

    public final Integer batchSize;

    public final Long maxAwaitTime;

    public final Long maxTime;




}
