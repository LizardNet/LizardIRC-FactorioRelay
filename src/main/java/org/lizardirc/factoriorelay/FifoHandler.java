package org.lizardirc.factoriorelay;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class FifoHandler {
    private final Path fifoPath;

    public FifoHandler(Path fifoPath) {
        this.fifoPath = Objects.requireNonNull(fifoPath);
    }

    public synchronized void write(String string) throws IOException {
        try (PrintStream fifo = new PrintStream(Files.newOutputStream(fifoPath, StandardOpenOption.WRITE, StandardOpenOption.SYNC))) {
            fifo.println(string);
        }
    }
}
