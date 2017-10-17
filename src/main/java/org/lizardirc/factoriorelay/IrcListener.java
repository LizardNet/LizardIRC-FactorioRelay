package org.lizardirc.factoriorelay;

import java.nio.file.Path;
import java.util.Objects;

import org.lizardirc.beancounter.utils.IrcColors;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.QuitEvent;

public class IrcListener<T extends PircBotX> extends ListenerAdapter<T> {
    private final FifoHandler fifoHandler;

    public IrcListener(Path fifoPath) {
        Objects.requireNonNull(fifoPath);
        fifoHandler = new FifoHandler(fifoPath);
    }

    @Override
    public void onMessage(MessageEvent<T> event) throws Exception {
        String user = event.getUser().getNick();
        String channel = event.getChannel().getName();
        String message = prepareMessage(event.getMessage());

        fifoHandler.write("[IRC] (" + channel + ") <" + user + "> " + message);
    }

    @Override
    public void onAction(ActionEvent<T> event) throws Exception {
        String user = event.getUser().getNick();
        String channel = event.getChannel().getName();
        String message = prepareMessage(event.getMessage());

        fifoHandler.write("[IRC] (" + channel + ") * " + user + ' ' + message);
    }

    @Override
    public void onJoin(JoinEvent<T> event) throws Exception {
        String user = event.getUser().getNick();
        String channel = event.getChannel().getName();

        fifoHandler.write("[IRC] " + user + " joined " + channel);
    }

    @Override
    public void onPart(PartEvent<T> event) throws Exception {
        String user = event.getUser().getNick();
        String channel = event.getChannel().getName();
        String message = prepareMessage(event.getReason());

        fifoHandler.write("[IRC] " + user + " parted " + channel + " [" + message + ']');
    }

    @Override
    public void onQuit(QuitEvent<T> event) throws Exception {
        String user = event.getUser().getNick();
        String message = prepareMessage(event.getReason());

        fifoHandler.write("[IRC] " + user + " quit [" + message + ']');
    }

    @Override
    public void onNickChange(NickChangeEvent<T> event) throws Exception {
        String oldNick = event.getOldNick();
        String newNick = event.getNewNick();

        fifoHandler.write("[IRC] " + oldNick + " is now known as " + newNick);
    }

    @Override
    public void onKick(KickEvent<T> event) throws Exception {
        String kicker = event.getUser().getNick();
        String victim = event.getRecipient().getNick();
        String channel = event.getChannel().getName();
        String message = prepareMessage(event.getReason());

        fifoHandler.write("[IRC] " + victim + " was kicked from " + channel + " by " + kicker + " [" + message + ']');
    }

    private String prepareMessage(String message) {
        return IrcColors.stripFormatting(message);
    }
}
