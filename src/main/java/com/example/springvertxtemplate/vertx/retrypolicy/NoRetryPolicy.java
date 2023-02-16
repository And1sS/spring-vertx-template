package com.example.springvertxtemplate.vertx.retrypolicy;

public final class NoRetryPolicy implements RetryPolicy {

    private static final NoRetryPolicy value = new NoRetryPolicy();

    private NoRetryPolicy() {
    }

    public static NoRetryPolicy instance() {
        return value;
    }
}
