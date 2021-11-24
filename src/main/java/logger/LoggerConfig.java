package logger;

import org.apache.log4j.*;
import org.apache.log4j.Logger;

public class LoggerConfig {
    public static void configure(){
        String pattern = "%p:%c:Line %L %n";
        PatternLayout layout = new PatternLayout(pattern);
        FileAppender fileAppender = new FileAppender();
        fileAppender.setFile("logs/log.out");
        fileAppender.setLayout(layout);
        fileAppender.setThreshold(Level.WARN);
        fileAppender.activateOptions();
        Logger.getRootLogger().addAppender(fileAppender);
    }
}
