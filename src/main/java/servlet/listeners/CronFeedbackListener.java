package servlet.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.FeedbackService;
import servlet.BuscadorViajes;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class CronFeedbackListener implements ServletContextListener {
    private ScheduledExecutorService scheduler;
    private final FeedbackService feedbackService = new FeedbackService();
    private static final Logger logger = LoggerFactory.getLogger(CronFeedbackListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            try {
                feedbackService.procesarFeedbackPendiente();
            } catch (Exception e) {
                logger.error("Error al procesar el feedback pendiente: {}", e.getMessage());
            }
        }, 0, 24, TimeUnit.HOURS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null) scheduler.shutdownNow();
    }
}
