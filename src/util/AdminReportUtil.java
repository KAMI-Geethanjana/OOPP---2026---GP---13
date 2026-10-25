package util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility for Administrative Report Generation and File I/O.
 * - Character Streams Writing (FileWriter -> BufferedWriter -> PrintWriter)
 * - Character Streams Reading (FileReader -> BufferedReader -> readLine())
 * - Exception Handling (IOException, FileNotFoundException)
 * - Proper stream flushing and closing (out.flush(), in.close() in finally)
 */
public final class AdminReportUtil {

    private AdminReportUtil() {
        // Prevent instantiation
    }

    /**
     * Exports user records to a formatted text/CSV report using Character Streams.
     * - FileWriter
     * - BufferedWriter
     * - PrintWriter
     * - flush() and close()
     */
    public static void exportUserReport(List<Map<String, Object>> users, File targetFile) throws IOException {
        if (targetFile == null) {
            throw new IllegalArgumentException("Target file cannot be null.");
        }

        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter out = null;

        try {
            fw = new FileWriter(targetFile);
            bw = new BufferedWriter(fw);
            out = new PrintWriter(bw);

            // Report Header
            out.println("==========================================================================================");
            out.println("          FACULTY OF TECHNOLOGY - UNIVERSITY OF RUHUNA (FTMS)");
            out.println("                   ADMINISTRATIVE USER AUDIT REPORT");
            out.println("==========================================================================================");
            out.println("Generated Date : " + new java.util.Date());
            out.println("Total Records  : " + (users != null ? users.size() : 0));
            out.println("------------------------------------------------------------------------------------------");
            out.printf("%-8s | %-15s | %-25s | %-15s | %-10s | %-15s%n",
                    "USER ID", "USERNAME", "FULL NAME", "ROLE", "STATUS", "REG/EMP NO");
            out.println("------------------------------------------------------------------------------------------");

            if (users != null) {
                for (Map<String, Object> u : users) {
                    int id = (int) u.getOrDefault("user_id", 0);
                    String username = String.valueOf(u.getOrDefault("username", ""));
                    String fullName = String.valueOf(u.getOrDefault("full_name", ""));
                    String role = String.valueOf(u.getOrDefault("role", ""));
                    boolean active = (boolean) u.getOrDefault("is_active", true);
                    String status = active ? "ACTIVE" : "LOCKED";
                    String extraId = String.valueOf(u.getOrDefault("extra_id", "-"));

                    out.printf("%-8d | %-15s | %-25s | %-15s | %-10s | %-15s%n",
                            id, username, fullName, role, status, extraId);
                }
            }

            out.println("==========================================================================================");
            out.println("                         END OF OFFICIAL REPORT");
            out.println("==========================================================================================");

            // Flush stream explicitly
            out.flush();

        } finally {
            // Close stream safely in finally block
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * Reads lines from a text file using BufferedReader.
     * - FileReader
     * - BufferedReader
     * - readLine() loop until null
     * - in.close()
     */
    public static List<String> readTextFile(File file) throws IOException {
        List<String> lines = new ArrayList<>();
        FileReader fr = null;
        BufferedReader in = null;

        try {
            fr = new FileReader(file);
            in = new BufferedReader(fr);

            String line = in.readLine();
            while (line != null) {
                lines.add(line);
                line = in.readLine(); // Read next line until EOF
            }
        } finally {
            if (in != null) {
                in.close(); // Clean stream close
            }
        }

        return lines;
    }
}
