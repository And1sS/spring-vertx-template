package com.example.springvertxtemplate.vertx.retrypolicy;

public sealed interface RetryPolicy permits MakeRetryPolicy, NoRetryPolicy {
}
