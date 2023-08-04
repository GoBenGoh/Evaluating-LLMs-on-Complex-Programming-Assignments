import java.io.File;

public class XmlReportPoller {
    private final int maxAttempts;
    private final int pollingInterval;

    public XmlReportPoller(int maxAttempts, int pollingInterval) {
        this.maxAttempts = maxAttempts;
        this.pollingInterval = pollingInterval;
    }

    public File waitForReport(String xmlFilePath) {
        File reportFile = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            reportFile = new File(xmlFilePath);
            if (reportFile.exists()) {
                System.out.println("Found report");
                return reportFile;
            } else {
                try {
                    System.out.println("Didn't find report");
                    Thread.sleep(pollingInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return null; // Report not found after maximum attempts
    }
}
