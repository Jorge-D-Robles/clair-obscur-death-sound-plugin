package com.deathsound;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("deathsound")
public interface DeathSoundConfig extends Config
{
    @Range(
            min = 0,
            max = 100
    )
    @ConfigItem(
            keyName = "volume",
            name = "Volume",
            description = "Sets the volume of the death sound.",
            position = 1
    )
    default int volume()
    {
        return 80;
    }
}