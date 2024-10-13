package org.example.providers;

public class SystemTimeProvider implements ITimeProvider {
    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
