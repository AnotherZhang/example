package science.atlarge.opencraft.opencraft.net.codec.play.scoreboard;

import com.flowpowered.network.Codec;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;
import science.atlarge.opencraft.opencraft.net.codec.CodecTest;
import science.atlarge.opencraft.opencraft.net.message.play.scoreboard.ScoreboardTeamMessage;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardTeamCodecTest extends CodecTest<ScoreboardTeamMessage> {

    @Override
    protected Codec<ScoreboardTeamMessage> createCodec() {
        return new ScoreboardTeamCodec();
    }

    @Override
    protected ScoreboardTeamMessage createMessage() {
        List<String> players = new ArrayList<>();
        players.add("player");
        return ScoreboardTeamMessage.create(
                "teamName",
                "displayName",
                "prefix",
                "suffix",
                false,
                true,
                Team.OptionStatus.ALWAYS,
                Team.OptionStatus.FOR_OTHER_TEAMS,
                ChatColor.RED,
                players
        );
    }
}
