package net.bingecraft.cheaper_netherite_repair;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.function.Consumer;

public final class Listener implements org.bukkit.event.Listener {

  private final double durabilityPerNetherBrick;
  private final Consumer<Runnable> scheduler;

  public Listener(double durabilityPerNetherBrick, Consumer<Runnable> scheduler) {
    this.durabilityPerNetherBrick = durabilityPerNetherBrick;
    this.scheduler = scheduler;
  }

  private static final Material[] repairables = {
    Material.NETHERITE_AXE,
    Material.NETHERITE_BOOTS,
    Material.NETHERITE_CHESTPLATE,
    Material.NETHERITE_HELMET,
    Material.NETHERITE_HOE,
    Material.NETHERITE_LEGGINGS,
    Material.NETHERITE_PICKAXE,
    Material.NETHERITE_SHOVEL,
    Material.NETHERITE_SWORD,
  };

  @EventHandler
  public void onPrepareAnvilEvent(PrepareAnvilEvent event) {
    ItemStack first = event.getInventory().getFirstItem();
    ItemStack second = event.getInventory().getSecondItem();
    tryRepair(event, first, second);
    tryRepair(event, second, first);
  }

  private void tryRepair(PrepareAnvilEvent event, ItemStack first, ItemStack second) {
    if (first == null || !isCheaplyRepairable(first.getType()) || second == null) return;

    if (second.getType().equals(Material.NETHER_BRICK)) {
      int amount = (int) (durabilityPerNetherBrick * second.getAmount());
      ItemStack repaired = repair(first, amount);
      event.setResult(repaired);
      scheduler.accept(() -> event.getInventory().setRepairCost(0));
    }
    else if (second.getType().equals(Material.NETHER_BRICKS)) {
      int amount = (int) (durabilityPerNetherBrick * second.getAmount() * 4);
      ItemStack firstItemRepaired = repair(first, amount);
      event.setResult(firstItemRepaired);
      scheduler.accept(() -> event.getInventory().setRepairCost(0));
    }
  }

  private static boolean isCheaplyRepairable(Material material) {
    for (Material repairable : repairables) {
      if (repairable.equals(material)) return true;
    }
    return false;
  }

  private static ItemStack repair(ItemStack itemStack, int amount) {
    Damageable damageable = (Damageable) itemStack.getItemMeta().clone();
    damageable.setDamage(damageable.getDamage() - amount);

    ItemStack repaired = itemStack.clone();
    repaired.setItemMeta(damageable);

    return repaired;
  }
}
