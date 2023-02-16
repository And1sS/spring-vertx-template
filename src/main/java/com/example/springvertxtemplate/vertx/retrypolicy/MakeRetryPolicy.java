package com.example.springvertxtemplate.vertx.retrypolicy;

public sealed interface MakeRetryPolicy
        extends RetryPolicy
        permits ExponentialBackoffRetryPolicy, FixedIntervalRetryPolicy {

    long delay();

    RetryPolicy next();
}
