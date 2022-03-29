package sn.awi.redis.catalog.utils;

import java.lang.instrument.Instrumentation;

public class ObjectSizeFetcher {

    private static Instrumentation instrumentation;

    private ObjectSizeFetcher() {
        // unreachable
    }

    public static void premain(Instrumentation inst) {
        instrumentation = inst;
    }

    public static long getObjectSize(Object o) {
        return instrumentation.getObjectSize(o);
    }
}
