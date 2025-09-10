package coop.stlma.tech.protocolsn.commonlib.util;

import com.google.protobuf.Timestamp;

import java.time.Instant;

public class ProtoUtil {
    private ProtoUtil() {}

    public static Timestamp fromInstant(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    public static Instant fromTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}
