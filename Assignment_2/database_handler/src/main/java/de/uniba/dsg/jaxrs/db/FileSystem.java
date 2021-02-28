package de.uniba.dsg.jaxrs.db;

import com.google.gson.Gson;
import de.uniba.dsg.jaxrs.model.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

class FileSystem {

    private static final Path bottleFile = Paths.get("files" + File.separator + "bottles.json");
    private static final Path crateFile = Paths.get("files" + File.separator + "crates.json");

    private static class Bottles {
        List<Bottle> bottles;

        public Bottles(List<Bottle> bottles) {
            this.bottles = bottles;
        }
    }

    private static class Crates {
        List<Crate> crates;

        public Crates(List<Crate> crates) {
            this.crates = crates;
        }
    }

    static synchronized State readState() {
        try {
            Gson gson = new Gson();

            String bottleFileContent  = String.join(System.lineSeparator(), Files.readAllLines(bottleFile));
            String crateFileContent  = String.join(System.lineSeparator(), Files.readAllLines(crateFile));

            Bottles bottles = gson.fromJson(bottleFileContent, Bottles.class);
            Crates crates = gson.fromJson(crateFileContent, Crates.class);

            return new State(bottles.bottles, crates.crates);
        } catch (IOException e) {
            try {
                createDbFilesIfNotExist();

                return readState();
            } catch (IOException ignored) {
                throw new RuntimeException("Could not create database file. Maybe wrong file permissions set in the CWD");
            }
        }
    }

    static synchronized void writeState(State state) {
        try {
            Gson gson = new Gson();

            String bottleFileContent = gson.toJson(new Bottles(state.getBottles()));
            String crateFileContent = gson.toJson(new Crates(state.getCrates()));

            Files.write(bottleFile, bottleFileContent.getBytes());
            Files.write(crateFile, crateFileContent.getBytes());
        } catch (IOException e) {
            try {
                createDbFilesIfNotExist();
                writeState(state);
            } catch (IOException ignored) {
                throw new RuntimeException("Could not create database file. Maybe wrong file permissions set in the CWD");
            }
        }
    }

    private static void createDbFilesIfNotExist() throws IOException {
        File newBottleFile = bottleFile.toFile();
        File newCrateFile = crateFile.toFile();

        if(!newBottleFile.exists()) {
            newBottleFile.getParentFile().mkdirs();
            newBottleFile.createNewFile();
        }

        if(!newCrateFile.exists()) {
            newCrateFile.getParentFile().mkdirs();
            newCrateFile.createNewFile();
        }

        State initState = new State(null, null).getInitState();
        writeState(initState);
    }
}
