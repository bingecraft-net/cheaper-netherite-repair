package net.bingecraft.bloodmoon;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class BloodmoonRepeatingTask implements Runnable {
    private World world;

    BloodmoonRepeatingTask(World world) {
        this.world = world;
    }

    @Override
    public void run() {
        long fullTime = world.getFullTime();
        long time = world.getTime();
        boolean isBloodMoon = ((fullTime / 24000) % 3) > 1;
        boolean isNight = time > 12000 && time < 23000;
        if (isBloodMoon && isNight && world.getDifficulty() != Difficulty.HARD) {
            world.setDifficulty(Difficulty.HARD);
            Bukkit.getConsoleSender().sendMessage("difficulty set to hard");
            for (Player player : world.getPlayers()) {
                player.sendMessage("the blood moon is rising");
            }
        } else if (world.getDifficulty() == Difficulty.HARD && (!isBloodMoon || !isNight)) {
            world.setDifficulty(Difficulty.PEACEFUL);
            Bukkit.getConsoleSender().sendMessage("difficulty set to peaceful");
        }
    }
}
