package me.ht9.rose.feature.command.commands;

import me.ht9.rose.feature.command.impl.Executable;
import me.ht9.rose.util.Globals;

public final class VclipCommand extends Executable implements Globals {
    @Override
    public void accept(String[] args) {
        if (mc.thePlayer != null && args.length == 1) {
            try {
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + Integer.parseInt(args[0]), mc.thePlayer.posZ);
            } catch (NumberFormatException ignored) {}
        }
    }
}
