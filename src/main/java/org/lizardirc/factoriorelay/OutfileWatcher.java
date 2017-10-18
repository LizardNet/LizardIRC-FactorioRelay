/*
 * LIZARDIRC/FACTORIORELAY
 * By the LizardIRC Development Team (see AUTHORS.txt file)
 *
 * Copyright (C) 2017 by the LizardIRC Development Team. Some rights reserved.
 *
 * License GPLv3+: GNU General Public License version 3 or later (at your choice):
 * <http://gnu.org/licenses/gpl.html>. This is free software: you are free to
 * change and redistribute it at your will provided that your redistribution, with
 * or without modifications, is also licensed under the GNU GPL. (Although not
 * required by the license, we also ask that you attribute us!) There is NO
 * WARRANTY FOR THIS SOFTWARE to the extent permitted by law.
 *
 * Note that this is an official project of the LizardIRC IRC network.  For more
 * information about LizardIRC, please visit our website at
 * <https://www.lizardirc.org>.
 *
 * This project contains code from and components derived from the
 * LizardIRC/Beancounter IRC bot <https://www.lizardirc.org/?page=beancounter>,
 * which is also licensed GNU GPLv3+.
 *
 * This is an open source project. The source Git repositories, which you are
 * welcome to contribute to, can be found here:
 * <https://gerrit.fastlizard4.org/r/gitweb?p=LizardIRC%2FFactorioRelay.git;a=summary>
 * <https://git.fastlizard4.org/gitblit/summary/?r=LizardIRC/FactorioRelay.git>
 *
 * Gerrit Code Review for the project:
 * <https://gerrit.fastlizard4.org/r/#/q/project:LizardIRC/FactorioRelay,n,z>
 *
 * Alternatively, the project source code can be found on the PUBLISH-ONLY mirror
 * on GitHub: <https://github.com/LizardNet/LizardIRC-FactorioRelay>
 *
 * Note: Pull requests and patches submitted to GitHub will be transferred by a
 * developer to Gerrit before they are acted upon.
 */

package org.lizardirc.factoriorelay;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.pircbotx.PircBotX;

public class OutfileWatcher<T extends PircBotX> implements Runnable {
    private final FactorioListener<T> parent;
    private final Path path;
    private volatile boolean interrupted = false;

    private long readStartPosition = 0;

    public OutfileWatcher(FactorioListener<T> parent, Path outfilePath) {
        this.parent = Objects.requireNonNull(parent);
        path = Objects.requireNonNull(outfilePath);
    }

    @Override
    public void run() {
        try (final WatchService watchService = path.getFileSystem().newWatchService()) {
            readStartPosition = Files.size(path);
            path.getParent().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

            while (!interrupted) {
                WatchKey wk = watchService.take();
                for (WatchEvent<?> event : wk.pollEvents()) {
                    final Path changed = (Path) event.context();
                    if (changed.getFileName().equals(path.getFileName())) {
                        parent.processLogLines(getNewLines());
                    }
                }

                if (!wk.reset()) {
                    throw new Exception("WatchKey unregistered");
                }
            }
        } catch (Exception e) {
            System.out.println("Exception occurred in OutfileWatcher: " + e.toString());
            e.printStackTrace();
            parent.signalError();
        }
    }

    synchronized void interrupt() {
        interrupted = true;
    }

    /**
     * Gets all lines from the Factorio console log file since the last call to this method, or since the OutfileWatcher
     * thread was started if this method has not yet been called.  Returns an empty list of there are no new lines.
     *
     * @return The new lines in the log file, each line having its own entry in the list, guaranteed to be in the order
     *         that the lines are given in the log file; or an empty set if there are no new lines.
     */
    private List<String> getNewLines() {
        final List<String> retval = new ArrayList<>();

        // None of the nio solutions are as clean as this, unfortunately
        try (final RandomAccessFile outfile = new RandomAccessFile(path.toFile(), "r")) {
            outfile.seek(readStartPosition);
            while (true) {
                String buffer = outfile.readLine();
                if (buffer == null) {
                    readStartPosition = outfile.getFilePointer();
                    break;
                } else {
                    retval.add(buffer);
                }
            }
        } catch (IOException e) {
            System.out.println("Exception occurred in OutfileWatcher while attempting to get lines from the outfile: " + e.toString());
            e.printStackTrace();
            parent.signalError();
        }

        return retval;
    }
}
