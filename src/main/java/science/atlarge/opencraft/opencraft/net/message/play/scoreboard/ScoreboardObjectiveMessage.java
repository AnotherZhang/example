package science.atlarge.opencraft.opencraft.net.message.play.scoreboard;

import com.flowpowered.network.Message;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import science.atlarge.opencraft.opencraft.scoreboard.RenderType;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScoreboardObjectiveMessage implements Message {

    private final String name;
    private final String displayName;
    private final int action;
    private final RenderType renderType;

    public static ScoreboardObjectiveMessage create(String name, String displayName) {
        return new ScoreboardObjectiveMessage(name, displayName, Action.CREATE.ordinal(),
                RenderType.INTEGER);
    }

    public static ScoreboardObjectiveMessage create(String name, String displayName,
            RenderType renderType) {
        return new ScoreboardObjectiveMessage(name, displayName, Action.CREATE.ordinal(),
                renderType);
    }

    public static ScoreboardObjectiveMessage remove(String name) {
        return new ScoreboardObjectiveMessage(name, null, Action.REMOVE.ordinal(), null);
    }

    public static ScoreboardObjectiveMessage update(String name, String displayName) {
        return new ScoreboardObjectiveMessage(name, displayName, Action.UPDATE.ordinal(),
                RenderType.INTEGER);
    }

    public static ScoreboardObjectiveMessage update(String name, String displayName,
            RenderType renderType) {
        return new ScoreboardObjectiveMessage(name, displayName, Action.UPDATE.ordinal(),
                renderType);
    }

    private enum Action {
        CREATE,
        REMOVE,
        UPDATE
    }
}
