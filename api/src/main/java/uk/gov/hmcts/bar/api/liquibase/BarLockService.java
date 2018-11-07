package uk.gov.hmcts.bar.api.liquibase;

import liquibase.exception.LockException;
import liquibase.lockservice.DatabaseChangeLogLock;
import liquibase.lockservice.StandardLockService;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

public class BarLockService extends StandardLockService {

    public static final long LOCK_EXPIRATION_TIME_MINUTE = 11L;

    @Override
    public void waitForLock() throws LockException {
        Date latestLockTime = getLatestLockTime().orElse(null);
        if (!isLockAlive(latestLockTime)){
            releaseLock();
        }
        super.waitForLock();
    }

    @Override
    public int getPriority() {
        return super.getPriority() + 1;
    }

    public Optional<Date> getLatestLockTime() throws LockException {
        DatabaseChangeLogLock[] locks = listLocks();
        return Arrays.stream(locks)
            .map(DatabaseChangeLogLock::getLockGranted)
            .max(Date::compareTo);
    }

    public boolean isLockAlive(Date latestLocktime) {
        if (latestLocktime == null){
            return true;
        }
        return new Date().getTime() < latestLocktime.getTime() + LOCK_EXPIRATION_TIME_MINUTE * 1000 * 60;
    }
}
