import java.util.concurrent.locks.ReentrantLock;

class HitCounter {

    private static final int WINDOW_SIZE = 300;
    private final Bucket[] buckets;

    public HitCounter() {
        buckets = new Bucket[WINDOW_SIZE];
        for (int i = 0; i < WINDOW_SIZE; i++) {
            buckets[i] = new Bucket();
        }
    }

    // Record a hit at given timestamp (seconds)
    public void hit(int timestamp) {
        int index = timestamp % WINDOW_SIZE;
        Bucket bucket = buckets[index];

        bucket.lock.lock();
        try {
            if (bucket.timestamp != timestamp) {
                bucket.timestamp = timestamp;
                bucket.count = 1;
            } else {
                bucket.count++;
            }
        } finally {
            bucket.lock.unlock();
        }
    }

    // Get hits in last 5 minutes
    public int getHits(int timestamp) {
        int total = 0;

        for (Bucket bucket : buckets) {
            bucket.lock.lock();
            try {
                if (timestamp - bucket.timestamp < WINDOW_SIZE) {
                    total += bucket.count;
                }
            } finally {
                bucket.lock.unlock();
            }
        }
        return total;
    }

    private static class Bucket {
        int timestamp;
        int count;
        ReentrantLock lock = new ReentrantLock();
    }
}
