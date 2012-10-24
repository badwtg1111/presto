package com.facebook.presto;

import io.airlift.log.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class RetryDriver
{
    private static final Logger log = Logger.get(RetryDriver.class);

    private RetryDriver()
    {
    }

    public static <V> V runWithRetry(Callable<V> callable)
            throws Exception
    {
        return runWithRetry(callable, "<default>");
    }

    public static <V> V runWithRetry(Callable<V> callable, int maxRetryAttempts)
            throws Exception
    {
        return runWithRetry(callable, "<default>", maxRetryAttempts);
    }

    public static <V> V runWithRetry(Callable<V> callable, String callableName)
            throws Exception
    {
        return runWithRetry(callable, callableName, 10);
    }

    public static <V> V runWithRetry(Callable<V> callable, String callableName, int maxRetryAttempts)
            throws Exception
    {
        return runWithRetry(callable, callableName, maxRetryAttempts, 1);
    }

    public static <V> V runWithRetry(Callable<V> callable, String callableName, int maxRetryAttempts, int sleepSecs)
            throws Exception
    {
        checkNotNull(callable, "callable is null");
        checkNotNull(callableName, "callableName is null");
        checkArgument(maxRetryAttempts > 0, "maxRetryAttempts must be greater than zero");
        checkArgument(sleepSecs >= 0, "sleepSecs must be at least than zero");

        int attempt = 0;
        while (true) {
            attempt++;
            try {
                return callable.call();
            }
            catch (Exception e) {
                if (attempt == maxRetryAttempts) {
                    throw e;
                }
                else {
                    log.warn("Failed on executing %s with attempt %d, will retry. Exception: %s", callableName, attempt, e.getMessage());
                }
                TimeUnit.SECONDS.sleep(sleepSecs);
            }
        }
    }
}