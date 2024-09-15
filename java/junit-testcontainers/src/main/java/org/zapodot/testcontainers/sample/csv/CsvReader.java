package org.zapodot.testcontainers.sample.csv;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CsvReader {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(CsvReader.class);

    /**
     * This class is not meant to be instantiated
     */
    private CsvReader() {
    }

    public static List<UserWithRoles> readFromFile(File inputFile) {
        LOGGER.info("Leser brukere fra fil {}", inputFile.getAbsolutePath());
        if(! inputFile.isFile()) {
            throw new IllegalArgumentException("Inputfilen " + inputFile.getAbsolutePath() + " eksisterer ikke");
        }
        final CsvMapper objectMapper = new CsvMapper();
        final CsvSchema csvSchema = CsvSchema.builder()
                .addColumn("name", CsvSchema.ColumnType.STRING)
                .addArrayColumn("roles", ";")
                .setColumnSeparator(',')
                .setSkipFirstDataRow(true)
                .setStrictHeaders(true)
                .build();
        try (MappingIterator<UserWithRoles> objectMappingIterator = objectMapper
                .readerFor(UserWithRoles.class)
                .with(csvSchema)
                .readValues(inputFile)) {
            return objectMappingIterator
                    .readAll();
        } catch (IOException e) {
            throw new RuntimeException("Kunne ikke lese brukere fra fil", e);
        }
    }
}
