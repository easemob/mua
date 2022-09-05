package com.easemob.mua.utils;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Properties;

/**
 * @author easemob_developer
 * @date 2022/7/11
 */
public class SnowflakeShardingKeyGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(SnowflakeShardingKeyGenerator.class);

    public static final long EPOCH;

    private static final long SEQUENCE_BITS = 12L;

    private static final long WORKER_ID_BITS = 10L;

    private static final long SEQUENCE_MASK = (1 << SEQUENCE_BITS) - 1;

    private static final long WORKER_ID_LEFT_SHIFT_BITS = SEQUENCE_BITS;

    private static final long TIMESTAMP_LEFT_SHIFT_BITS = WORKER_ID_LEFT_SHIFT_BITS + WORKER_ID_BITS;

    private static final long WORKER_ID_MAX_VALUE = 1L << WORKER_ID_BITS;

    private static final long WORKER_ID = 0;

    private static final int MAX_TOLERATE_TIME_DIFFERENCE_MILLISECONDS = 10;

    private Properties properties = new Properties();

    /**
     * @param properties
     *            the properties to set
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    private byte sequenceOffset;

    private long sequence;

    private long lastMilliseconds;

    static {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, Calendar.NOVEMBER, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        EPOCH = calendar.getTimeInMillis();
    }

    public synchronized Comparable<?> generateKey() {
        long currentMilliseconds = System.currentTimeMillis();
        if (waitTolerateTimeDifferenceIfNeed(currentMilliseconds)) {
            currentMilliseconds = System.currentTimeMillis();
        }
        if (lastMilliseconds == currentMilliseconds) {
            if (0L == (sequence = (sequence + 1) & SEQUENCE_MASK)) {
                currentMilliseconds = waitUntilNextTime(currentMilliseconds);
            }
        } else {
            vibrateSequenceOffset();
            sequence = sequenceOffset;
        }
        lastMilliseconds = currentMilliseconds;
        return ((currentMilliseconds - EPOCH) << TIMESTAMP_LEFT_SHIFT_BITS)
                | (getWorkerId() << WORKER_ID_LEFT_SHIFT_BITS) | sequence;
    }

    private boolean waitTolerateTimeDifferenceIfNeed(final long currentMilliseconds) {
        if (lastMilliseconds <= currentMilliseconds) {
            return false;
        }
        long timeDifferenceMilliseconds = lastMilliseconds - currentMilliseconds;
        Preconditions.checkState(timeDifferenceMilliseconds < getMaxTolerateTimeDifferenceMilliseconds(),
                "Clock is moving backwards, last time is %d milliseconds, current time is %d milliseconds",
                lastMilliseconds, currentMilliseconds);
        try {
            Thread.sleep(timeDifferenceMilliseconds);
        } catch (InterruptedException e) {
            LOGGER.error("分布式主键线程休眠异常", e);
        }
        return true;
    }

    private long getWorkerId() {
        long result = Long.valueOf(properties.getProperty("worker.id", String.valueOf(WORKER_ID)));
        Preconditions.checkArgument(result >= 0L && result < WORKER_ID_MAX_VALUE);
        return result;
    }

    private int getMaxTolerateTimeDifferenceMilliseconds() {
        return Integer.valueOf(properties.getProperty("max.tolerate.time.difference.milliseconds",
                String.valueOf(MAX_TOLERATE_TIME_DIFFERENCE_MILLISECONDS)));
    }

    private long waitUntilNextTime(final long lastTime) {
        long result = System.currentTimeMillis();
        while (result <= lastTime) {
            result = System.currentTimeMillis();
        }
        return result;
    }

    private void vibrateSequenceOffset() {
        sequenceOffset = (byte)(~sequenceOffset & 1);
    }
}
