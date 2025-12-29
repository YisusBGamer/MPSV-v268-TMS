/*
 * Decompiled with CFR 0.152.
 */
package Net.server;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.Randomizer;

public class Timer {
    private static final AtomicInteger threadNumber = new AtomicInteger(1);
    protected String file;
    protected String name;
    static ScheduledThreadPoolExecutor ses;

    public void start() {
        if (ses != null && !ses.isShutdown() && !ses.isTerminated()) {
            return;
        }
        ses = new ScheduledThreadPoolExecutor(20, new RejectedThreadFactory(this));
        ses.setKeepAliveTime(10L, TimeUnit.MINUTES);
        ses.allowCoreThreadTimeOut(true);
        ses.setMaximumPoolSize(50);
    }

    public ScheduledThreadPoolExecutor getSES() {
        return ses;
    }

    public void stop() {
        if (ses != null) {
            ses.shutdown();
        }
    }

    public ScheduledFuture<?> schedule(Runnable r, long delay) {
        if (ses == null) {
            return null;
        }
        return ses.schedule(new LoggingSaveRunnable(r, this.file), delay, TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> scheduleAtTimestamp(Runnable command, long timestamp) {
        return this.schedule(command, timestamp - System.currentTimeMillis());
    }

    public static void startAll() {
        WorldTimer.getInstance().start();
        MapTimer.getInstance().start();
        BuffTimer.getInstance().start();
        CoolDownTimer.getInstance().start();
        EventTimer.getInstance().start();
        CloneTimer.getInstance().start();
        EtcTimer.getInstance().start();
        CheatTimer.getInstance().start();
        PingTimer.getInstance().start();
        PlayerTimer.getInstance().start();
        ExpiredTimer.getInstance().start();
        MobTimer.getInstance().start();
    }

    public ScheduledFuture<?> register(Runnable r, long repeatTime, long delay) {
        if (ses == null) {
            return null;
        }
        return ses.scheduleAtFixedRate(new LoggingSaveRunnable(r, this.file), delay, repeatTime, TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> register(Runnable r, long repeatTime) {
        if (ses == null) {
            return null;
        }
        return ses.scheduleAtFixedRate(new LoggingSaveRunnable(r, this.file), 0L, repeatTime, TimeUnit.MILLISECONDS);
    }

    private final class RejectedThreadFactory
    implements ThreadFactory {
        private final AtomicInteger threadNumber2 = new AtomicInteger(1);
        private final String tname;

        public RejectedThreadFactory(Timer timer) {
            this.tname = timer.name + Randomizer.nextInt();
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName(this.tname + "-W-" + threadNumber.getAndIncrement() + "-" + this.threadNumber2.getAndIncrement());
            return t;
        }
    }

    private static final class LoggingSaveRunnable
    implements Runnable {
        static final Logger log = LoggerFactory.getLogger(LoggingSaveRunnable.class);
        final Runnable r;
        String file;

        public LoggingSaveRunnable(Runnable r, String file) {
            this.r = r;
            this.file = file;
        }

        @Override
        public void run() {
            try {
                this.r.run();
            }
            catch (Throwable t) {
                log.error("Error in scheduled task: {}", (Object)this.file, (Object)t);
            }
        }
    }

    public static final class WorldTimer
    extends Timer {
        private static final WorldTimer instance = new WorldTimer();

        private WorldTimer() {
            this.name = "Worldtimer";
        }

        public static WorldTimer getInstance() {
            return instance;
        }
    }

    public static final class MapTimer
    extends Timer {
        private static final MapTimer instance = new MapTimer();

        private MapTimer() {
            this.name = "Maptimer";
        }

        public static MapTimer getInstance() {
            return instance;
        }
    }

    public static final class BuffTimer
    extends Timer {
        private static final BuffTimer instance = new BuffTimer();

        private BuffTimer() {
            this.name = "Bufftimer";
        }

        public static BuffTimer getInstance() {
            return instance;
        }
    }

    public static final class CoolDownTimer
    extends Timer {
        private static final CoolDownTimer instance = new CoolDownTimer();

        private CoolDownTimer() {
            this.name = "CoolDownTimer";
        }

        public static CoolDownTimer getInstance() {
            return instance;
        }
    }

    public static final class EventTimer
    extends Timer {
        private static final EventTimer instance = new EventTimer();

        private EventTimer() {
            this.name = "Eventtimer";
        }

        public static EventTimer getInstance() {
            return instance;
        }
    }

    public static final class CloneTimer
    extends Timer {
        private static final CloneTimer instance = new CloneTimer();

        private CloneTimer() {
            this.name = "Clonetimer";
        }

        public static CloneTimer getInstance() {
            return instance;
        }
    }

    public static final class EtcTimer
    extends Timer {
        private static final EtcTimer instance = new EtcTimer();

        private EtcTimer() {
            this.name = "Etctimer";
        }

        public static EtcTimer getInstance() {
            return instance;
        }
    }

    public static final class CheatTimer
    extends Timer {
        private static final CheatTimer instance = new CheatTimer();

        private CheatTimer() {
            this.name = "Cheattimer";
        }

        public static CheatTimer getInstance() {
            return instance;
        }
    }

    public static final class PingTimer
    extends Timer {
        private static final PingTimer instance = new PingTimer();

        private PingTimer() {
            this.name = "Pingtimer";
        }

        public static PingTimer getInstance() {
            return instance;
        }
    }

    public static final class GuiTimer extends Timer {

        private static final GuiTimer instance = new GuiTimer();

        private GuiTimer() {
            name = "GuiTimer";
        }

        public static GuiTimer getInstance() {
            return instance;
        }
    }


    public static final class PlayerTimer
    extends Timer {
        private static final PlayerTimer instance = new PlayerTimer();

        private PlayerTimer() {
            this.name = "PlayerTimer";
        }

        public static PlayerTimer getInstance() {
            return instance;
        }
    }

    public static final class ExpiredTimer
    extends Timer {
        private static final PlayerTimer instance = new PlayerTimer();

        private ExpiredTimer() {
            this.name = "ExpiredTimer";
        }

        public static PlayerTimer getInstance() {
            return instance;
        }
    }

    public static class MobTimer
    extends Timer {
        private static final MobTimer instance = new MobTimer();

        private MobTimer() {
            this.name = "MobTimer";
        }

        public static MobTimer getInstance() {
            return instance;
        }

        @Override
        public ScheduledFuture<?> register(Runnable r, long repeatTime, long delay) {
            if (ses == null) {
                return null;
            }
            return ses.scheduleAtFixedRate(new LoggingSaveRunnable(r, this.file), delay, repeatTime, TimeUnit.MILLISECONDS);
        }

        @Override
        public ScheduledFuture<?> register(Runnable r, long repeatTime) {
            if (ses == null) {
                return null;
            }
            return ses.scheduleAtFixedRate(new LoggingSaveRunnable(r, this.file), 0L, repeatTime, TimeUnit.MILLISECONDS);
        }

        @Override
        public ScheduledFuture<?> schedule(Runnable r, long delay) {
            if (ses == null) {
                return null;
            }
            return ses.schedule(new LoggingSaveRunnable(r, this.file), delay, TimeUnit.MILLISECONDS);
        }

        @Override
        public ScheduledFuture<?> scheduleAtTimestamp(Runnable command, long timestamp) {
            return this.schedule(command, timestamp - System.currentTimeMillis());
        }
    }
}

