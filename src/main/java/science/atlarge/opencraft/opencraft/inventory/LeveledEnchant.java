package science.atlarge.opencraft.opencraft.inventory;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import science.atlarge.opencraft.opencraft.constants.GlowEnchantment;
import science.atlarge.opencraft.opencraft.util.WeightedRandom.Choice;

public class LeveledEnchant extends EnchantmentOffer implements Choice {

    public LeveledEnchant(Enchantment enchantment, int enchantmentLevel, int cost) {
        super(enchantment, enchantmentLevel, cost);
    }

    @Override
    public int getWeight() {
        return ((GlowEnchantment) getEnchantment()).getWeight();
    }
}
