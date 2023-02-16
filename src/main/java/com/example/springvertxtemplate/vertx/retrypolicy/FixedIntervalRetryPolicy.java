package com.example.springvertxtemplate.vertx.retrypolicy;

import lombok.Value;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Value(staticConstructor = "of")
public class FixedIntervalRetryPolicy implements MakeRetryPolicy {

    long delay;

    @Override
    public RetryPolicy next() {
        return this;
    }
}

