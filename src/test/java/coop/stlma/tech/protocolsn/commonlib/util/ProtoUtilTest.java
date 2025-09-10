package coop.stlma.tech.protocolsn.commonlib.util;

import com.google.protobuf.Timestamp;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

@MicronautTest
class ProtoUtilTest {

    @Test
    void testFromInstant_happyPath() {
        Instant instant = Instant.ofEpochMilli(482198400000L);
        Timestamp timestamp = ProtoUtil.fromInstant(instant);
        Assertions.assertNotNull(timestamp);
        Assertions.assertEquals(instant.getEpochSecond(), timestamp.getSeconds());
        Assertions.assertEquals(instant.getNano(), timestamp.getNanos());
    }

    @Test
    void testFromInstant_null() {
        Timestamp timestamp = ProtoUtil.fromInstant(null);
        Assertions.assertNull(timestamp);
    }

    @Test
    void testFromTimestamp_happyPath() {
        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(482198400L)
                .build();

        Instant instant = ProtoUtil.fromTimestamp(timestamp);

        Assertions.assertNotNull(instant);
        Assertions.assertEquals(timestamp.getSeconds(), instant.getEpochSecond());
    }

    @Test
    void testFromTimestamp_null() {
        Instant instant = ProtoUtil.fromTimestamp(null);
        Assertions.assertNull(instant);
    }
}
