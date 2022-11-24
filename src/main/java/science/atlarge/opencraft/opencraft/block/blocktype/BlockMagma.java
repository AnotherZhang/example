package science.atlarge.opencraft.opencraft.block.blocktype;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import science.atlarge.opencraft.opencraft.block.GlowBlock;
import science.atlarge.opencraft.opencraft.inventory.ToolType;

public class BlockMagma extends BlockDirectDrops {

    public BlockMagma() {
        super(Material.MAGMA, ToolType.PICKAXE);
    }

    @Override
    public void onEntityStep(GlowBlock block, LivingEntity entity) {
        entity.damage(1.0, EntityDamageEvent.DamageCause.FIRE);
    }
}
