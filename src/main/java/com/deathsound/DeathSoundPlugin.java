package com.deathsound;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.sound.sampled.*;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.ActorDeath;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@PluginDescriptor(
		name = "Death Sound",
		description = "Plays a custom sound when the player dies.",
		tags = {"death", "sound", "music"}
)
public class DeathSoundPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private DeathSoundConfig config;

    /**
     * -- SETTER --
     *  This method is used for testing purposes to inject a mock Clip.
     *
     */
    @Setter
    private Clip clip;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Death Sound started!");
		// Load the sound file from the resources folder
		try
		{
			// The path must match the location of your .wav file in the resources directory
			InputStream audioSrc = DeathSoundPlugin.class.getResourceAsStream("clair_obscur_death.wav");
			if (audioSrc == null) {
				log.error("Sound file not found in resources!");
				return;
			}
			// Using BufferedInputStream for better performance
			InputStream bufferedIn = new BufferedInputStream(audioSrc);
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
			clip = AudioSystem.getClip();
			clip.open(audioStream);
		}
		catch (UnsupportedAudioFileException | IOException | LineUnavailableException e)
		{
			log.error("Error loading sound file", e);
		}
	}

	@Override
	protected void shutDown() throws Exception
	{
		// Release the audio clip resource
		if (clip != null)
		{
			clip.close();
		}
		log.info("Death Sound stopped!");
	}

	@Subscribe
	public void onActorDeath(ActorDeath actorDeath)
	{
		Actor actor = actorDeath.getActor();
		// Check if the actor that died is the local player
		if (actor instanceof Player && actor == client.getLocalPlayer())
		{
			// Play the sound if the clip has been loaded successfully
			if (clip != null)
			{
				// Rewind the clip to the beginning before playing
				clip.setFramePosition(0);
				clip.start();
			}
		}
	}

    @Provides
	DeathSoundConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DeathSoundConfig.class);
	}
}