package com.deathsound;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class DeathSoundLauncher
{
    public static void main(String[] args) throws Exception
    {
        // This line is the magic. It tells RuneLite to load your plugin class.
        ExternalPluginManager.loadBuiltin(DeathSoundPlugin.class);

        // This line starts the RuneLite client as normal.
        RuneLite.main(args);
    }
}