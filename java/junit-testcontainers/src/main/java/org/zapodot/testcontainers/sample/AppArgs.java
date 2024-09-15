package org.zapodot.testcontainers.sample;

import com.beust.jcommander.Parameter;

import java.io.File;
import java.net.URL;

public final class AppArgs {

    @Parameter(names = "--jdbc-url", description = "The JDBC URL to use for the database connection", required = true)
    private String jdbcUrl;

    @Parameter(names = "--jdbc-user", description = "The JDBC user to use for the database connection", required = true)
    private String jdbcUser;

    @Parameter(names = "--jdbc-password", description = "The JDBC password to use for the database connection", required = true)
    private String jdbcPassword;

    @Parameter(names = "--input-file", description = "The input file to process")
    private File inputFile;

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getJdbcUser() {
        return jdbcUser;
    }

    public String getJdbcPassword() {
        return jdbcPassword;
    }

    public File getInputFile() {
        return inputFile;
    }
}
