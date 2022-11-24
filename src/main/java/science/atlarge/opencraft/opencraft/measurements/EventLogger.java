package science.atlarge.opencraft.opencraft.measurements;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class EventLogger implements AutoCloseable {

    private final Map<String, Long> timings = new ConcurrentHashMap<>();

    abstract public void init() throws IOException;

    abstract public void log(String key, String value);

    public void log(String key, int value) {
        log(key, String.valueOf(value));
    }

    public void log(String key, double value) {
        log(key, String.valueOf(value));
    }

    public void start(String key) {
        timings.put(key, System.nanoTime());
    }

    public void stop(String key) {
        if (timings.containsKey(key)) {
            log(key, (System.nanoTime() - timings.remove(key)) / 1000000.0);
        }
    }

    public void cancel(String key) {
        timings.remove(key);
    }

    abstract public void flush();
}
