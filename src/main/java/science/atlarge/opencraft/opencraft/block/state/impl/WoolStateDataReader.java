package science.atlarge.opencraft.opencraft.block.state.impl;

import com.google.common.collect.ImmutableSet;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.material.Wool;
import science.atlarge.opencraft.opencraft.block.state.BlockStateData;
import science.atlarge.opencraft.opencraft.block.state.BlockStateReader;
import science.atlarge.opencraft.opencraft.block.state.InvalidBlockStateException;
import science.atlarge.opencraft.opencraft.block.state.StateSerialization;

import java.util.Set;

public class WoolStateDataReader extends BlockStateReader<Wool> {

    private static final Set<String> VALID_STATES = ImmutableSet.of("color");

    @Override
    public Set<String> getValidStates() {
        return VALID_STATES;
    }

    @Override
    public Wool read(Material material, BlockStateData data) throws InvalidBlockStateException {
        Wool wool = new Wool();
        if (data.contains("color")) {
            DyeColor color = StateSerialization.getColor(data.get("color"));
            if (color == null) {
                throw new InvalidBlockStateException(material, data);
            }
            wool.setColor(color);
        } else {
            wool.setColor(DyeColor.WHITE);
        }
        return wool;
    }

    @Override
    public boolean matches(BlockStateData state, Wool data) throws InvalidBlockStateException {
        if (state.contains("color")) {
            DyeColor color = StateSerialization.getColor(state.get("color"));
            if (color == null) {
                throw new InvalidBlockStateException(data.getItemType(), state);
            }
            return data.getColor() == color;
        }
        return true;
    }
}
