package uk.gov.hmcts.bar.api.liquibase;

import liquibase.configuration.GlobalConfiguration;
import liquibase.configuration.LiquibaseConfiguration;
import liquibase.exception.LockException;
import liquibase.lockservice.DatabaseChangeLogLock;
import liquibase.lockservice.StandardLockService;
import liquibase.logging.LogService;
import liquibase.logging.LogType;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

public class BarLockService extends StandardLockService {

    public static final long lockExpirationTimeMinute = 11L;

    @Override
    public void waitForLock() throws LockException {
        Date latestLockTime = getLatestLockTime().orElse(null);
        boolean locked = false;
        long timeToGiveUp = new Date().getTime() + (getChangeLogLockWaitTime() * 1000 * 60);
        while (!locked && (new Date().getTime() < timeToGiveUp)) {
            if (!isLockAlive(latestLockTime)){
                releaseLock();
            }
            locked = acquireLock();
            if (!locked) {
                LogService.getLog(getClass()).info(LogType.LOG, "Waiting for changelog lock....");
                try {
                    Thread.sleep(getChangeLogLockRecheckTime() * 1000);
                } catch (InterruptedException e) {
                    // Restore thread interrupt status
                    Thread.currentThread().interrupt();
                }
            }
        }

        if (!locked) {
            DatabaseChangeLogLock[] locks = listLocks();
            String lockedBy;
            if (locks.length > 0) {
                DatabaseChangeLogLock lock = locks[0];
                lockedBy = lock.getLockedBy() + " since " +
                    DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                        .format(lock.getLockGranted());
            } else {
                lockedBy = "UNKNOWN";
            }
            throw new LockException("Could not acquire change log lock.  Currently locked by " + lockedBy);
        }
    }

    @Override
    public int getPriority() {
        return 2;
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
        return new Date().getTime() < latestLocktime.getTime() + lockExpirationTimeMinute * 1000 * 60;
    }
}
