package com.tenforce.lodms.transformers.validator;

import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ValidationLogWriter {
    public final static String INFO = "INFO";
    public final static String WARN = "WARNING";
    public final static String ERR = "ERROR";
    private BufferedWriter writer;
    private Logger logger = Logger.getLogger(ValidationLogWriter.class);

    public ValidationLogWriter(String path) throws IOException {
        this.writer = new BufferedWriter(new FileWriter(path, false));
        write(INFO, "validation started created " + nowAsISO());
    }

    public void write(String level, String message) {
        try {
            writer.write(String.format("[%s] %s\n", level, message));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void info(String message) {
        write(INFO, message);
    }

    public void finish() throws IOException {
        write(INFO, "validation finished " + nowAsISO());
        writer.close();
    }

    private String nowAsISO() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
        df.setTimeZone(tz);
        return df.format(new Date());
    }

}
