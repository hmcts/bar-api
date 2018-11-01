package uk.gov.hmcts.bar.api.liquibase;

import liquibase.exception.LockException;
import liquibase.lockservice.DatabaseChangeLogLock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class BarLockServiceTest {

    private BarLockService spyBarLockService;
    Date lockDate1;
    Date lockDate2;

    @Before
    public void setUp() throws LockException {
        lockDate1 = Date.from(LocalDateTime.now().minusMinutes(12).atZone(ZoneId.systemDefault()).toInstant());
        lockDate2 = Date.from(LocalDateTime.now().minusMinutes(13).atZone(ZoneId.systemDefault()).toInstant());
        DatabaseChangeLogLock[] toReturn =
            new DatabaseChangeLogLock[]
                {
                    new DatabaseChangeLogLock(1, lockDate1, "akiss"),
                    new DatabaseChangeLogLock(1, lockDate2, "akiss")
                };
        spyBarLockService = spy(new BarLockService());
        doReturn(toReturn).when(spyBarLockService).listLocks();
        doReturn(2L).when(spyBarLockService).getChangeLogLockWaitTime();
        doReturn(1L).when(spyBarLockService).getChangeLogLockRecheckTime();
        doNothing().when(spyBarLockService).releaseLock();
        doReturn(true).when(spyBarLockService).acquireLock();

    }

    @Test
    public void waitForLock() throws LockException {
        spyBarLockService.waitForLock();
        verify(spyBarLockService, times(1)).releaseLock();
        verify(spyBarLockService, times(1)).acquireLock();
    }

    @Test
    public void testGetLatestLockTime() throws LockException {
        Assert.assertEquals(Optional.of(lockDate1), spyBarLockService.getLatestLockTime());
    }

    @Test
    public void testIsLockAlive() {
        LocalDateTime ldt = LocalDateTime.now();
        Date latestLockTime = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        Assert.assertTrue(spyBarLockService.isLockAlive(latestLockTime));

        ldt = LocalDateTime.now().minusMinutes(12);
        latestLockTime = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        Assert.assertFalse(spyBarLockService.isLockAlive(latestLockTime));
    }
}
