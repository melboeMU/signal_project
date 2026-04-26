package com.data_management;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Reads generated output files from a directory and stores parsed data in DataStorage.
 *
 * Assumptions:
 * 1. The path points to a directory created with --output file:<output_dir>.
 * 2. The directory contains one or more text-based output files.
 * 3. Each non-empty line represents one measurement.
 * 4. Expected line format:
 *
 *    timestamp,patientId,recordType,measurementValue
 *
 *    Example with epoch millis:
 *    1713772800000,1,HeartRate,78.0
 *
 *    Example with ISO date-time:
 *    2026-04-22T10:15:30,1,HeartRate,78.0
 *
 * 5. Header lines are ignored.
 * 6. Malformed lines cause an IOException, because silently skipping potentially
 *    important medical data would be risky.
 */
public class FileDataReader implements DataReader {

    private final Path outputDirectory;

    public FileDataReader(Path outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        if (dataStorage == null) {
            throw new IllegalArgumentException("dataStorage must not be null");
        }

        if (outputDirectory == null) {
            throw new IllegalStateException("Output directory must not be null");
        }

        if (!Files.exists(outputDirectory)) {
            throw new IOException("Output directory does not exist: " + outputDirectory);
        }

        if (!Files.isDirectory(outputDirectory)) {
            throw new IOException("Provided path is not a directory: " + outputDirectory);
        }

        try (Stream<Path> files = Files.list(outputDirectory)) {
            List<Path> sortedFiles = files
                    .filter(Files::isRegularFile)
                    .filter(path -> !path.getFileName().toString().startsWith("."))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .toList();

            for (Path file : sortedFiles) {
                readSingleFile(file, dataStorage);
            }
        }
    }

    private void readSingleFile(Path file, DataStorage dataStorage) throws IOException {
        List<String> lines = Files.readAllLines(file);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();

            if (line.isEmpty()) {
                continue;
            }

            if (isHeader(line)) {
                continue;
            }

            try {
                String[] parts = line.split(",");

                if (parts.length != 4) {
                    throw new IllegalArgumentException(
                            "Expected 4 comma-separated values but got " + parts.length);
                }

                long timestamp = parseTimestamp(parts[0].trim());
                int patientId = Integer.parseInt(parts[1].trim());
                String recordType = parts[2].trim();
                double measurementValue = Double.parseDouble(parts[3].trim());

                if (recordType.isEmpty()) {
                    throw new IllegalArgumentException("recordType must not be empty");
                }

                dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);

            } catch (Exception e) {
                throw new IOException(
                        "Failed to parse line " + (i + 1) + " in file " + file.getFileName() + ": " + line,
                        e);
            }
        }
    }

    private boolean isHeader(String line) {
        String normalized = line.toLowerCase().replace(" ", "");
        return normalized.equals("timestamp,patientid,recordtype,measurementvalue")
                || normalized.equals("timestamp,patientid,signaltype,value");
    }

    /**
     * Supports two timestamp formats:
     * 1. Unix epoch milliseconds, e.g. 1713772800000
     * 2. ISO LocalDateTime, e.g. 2026-04-22T10:15:30
     *
     * ISO values are converted using the system default timezone.
     */
    private long parseTimestamp(String rawTimestamp) {
        try {
            return Long.parseLong(rawTimestamp);
        } catch (NumberFormatException e) {
            LocalDateTime dateTime = LocalDateTime.parse(rawTimestamp);
            return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
    }
}