package mx.wmx;

import java.io.IOException;

import java.nio.file.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import java.nio.channels.SeekableByteChannel;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class App {
	
	//Log file setting
    private static final String LOG_FILE_PATH = "/var/log/nginx/access.log";
    private static final String LOG_DATE_FORMAT = "dd/MMM/yyyy:HH:mm:ss Z";
    
    //Database settings
    private static final String DB_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    //connection settings
    private static final String DB_URL = "jdbc:mysql://localhost:3306/logiberto";
    private static final String DB_USER = "user1";
    private static final String DB_PASSWORD = "password1";
    
    //SQL INSERT
    private static final String DB_SQL_INSERT_LOG_LINE = "INSERT INTO access_log (ip_address, datetime, http_status_code,http_method, resource, user_agent) VALUES (?,?,?,?,?,?)";
    
    //Text file pointer
    private static long lastFilePosition = 0;

    public static void main(String[] args) {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path logDir = Paths.get(LOG_FILE_PATH).getParent();
            logDir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            while (true) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                	WatchEvent.Kind<?> kind = event.kind();
                    if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        Path changed = (Path) event.context();
                        if (true) {
                            List<String> newLines = readNewLogLines();
                            for (String line : newLines) {
                            	insertLogEntry(line);
                            }
                        }
                    }
                }
                key.reset();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static List<String> readNewLogLines() throws IOException {	
        Path logFilePath = Paths.get(LOG_FILE_PATH);
        List<String> newLines = new ArrayList<>();
        try (SeekableByteChannel sbc = Files.newByteChannel(logFilePath, StandardOpenOption.READ)) {
            sbc.position(lastFilePosition);
            StringBuilder builder = new StringBuilder();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int bytesRead;
            while ((bytesRead = sbc.read(buffer)) > 0) {
                buffer.flip();
                builder.append(StandardCharsets.UTF_8.decode(buffer));
                buffer.clear();
            }
            lastFilePosition = sbc.position();
            String content = builder.toString();
            if (!content.isEmpty()) {
                String[] lines = content.split("\r?\n");
                for (String line : lines) {
                    newLines.add(line);
                }
            }
        }
        return newLines;
    }
    
    private static void insertLogEntry(String logEntry) {
        String regex = "(\\d+\\.\\d+\\.\\d+\\.\\d+) - - \\[(.*?)\\]  (\\d+) \"(\\w+) (.*?) HTTP/\\d\\.\\d\" \\d+ \"-\" \"(.*?)\" \"-\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(logEntry);

        if (matcher.matches()) {
            String ipAddress = matcher.group(1);
            String timestampStr = matcher.group(2);
            int statusCode = Integer.parseInt(matcher.group(3));
            String httpMethod = matcher.group(4);
            String requestUri = matcher.group(5);
            String userAgent = matcher.group(6);
            
            // Parse log Date
            SimpleDateFormat sdf = new SimpleDateFormat(LOG_DATE_FORMAT);
         
            Date datetime = null;
            try {
            	datetime = sdf.parse(timestampStr);
            } catch(ParseException e) {
            	System.err.println("Ocurrio un error al parsear la fecha" + e);
            }
             
            // Parse log Date
            SimpleDateFormat db_sdf = new SimpleDateFormat(DB_DATE_FORMAT);
            String dbFormattedString = db_sdf.format(datetime);
            String sql = DB_SQL_INSERT_LOG_LINE;
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, ipAddress);
                pstmt.setString(2, dbFormattedString);
                pstmt.setInt(3, statusCode);
                pstmt.setString(4, httpMethod);
                pstmt.setString(5, requestUri);
                pstmt.setString(6, userAgent);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Log entry format is invalid: " + logEntry);
      }
    }
}