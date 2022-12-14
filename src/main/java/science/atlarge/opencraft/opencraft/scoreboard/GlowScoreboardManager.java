package science.atlarge.opencraft.opencraft.scoreboard;

import org.bukkit.scoreboard.ScoreboardManager;
import science.atlarge.opencraft.opencraft.GlowServer;

import java.io.IOException;

/**
 * ScoreboardManager implementation.
 */
public class GlowScoreboardManager implements ScoreboardManager {

    private final GlowServer server;
    private GlowScoreboard mainScoreboard;

    public GlowScoreboardManager(GlowServer server) {
        this.server = server;
    }

    @Override
    public GlowScoreboard getMainScoreboard() {
        if (mainScoreboard == null) {
            GlowScoreboard newScoreboard;
            try {
                newScoreboard = server.getScoreboardIoService().readMainScoreboard();
            } catch (IOException e) {
                newScoreboard = new GlowScoreboard();
            }
            mainScoreboard = newScoreboard;
        }
        return mainScoreboard;

    }

    @Override
    public GlowScoreboard getNewScoreboard() {
        return new GlowScoreboard();
    }
}
