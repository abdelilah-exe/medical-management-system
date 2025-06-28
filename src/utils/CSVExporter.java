package utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CSVExporter {

    public static void exportToCSV(String filePath, List<String> headers, List<List<String>> rows) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            // Write headers
            writer.write(String.join(",", headers));
            writer.newLine();

            // Write rows
            for (List<String> row : rows) {
                writer.write(String.join(",", row));
                writer.newLine();
            }

            System.out.println("CSV export completed: " + filePath);
        } catch (IOException e) {
            System.out.println("Error exporting CSV: " + e.getMessage());
        }
    }
}
