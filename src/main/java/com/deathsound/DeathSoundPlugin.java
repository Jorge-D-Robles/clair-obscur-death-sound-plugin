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
		name = "Clair Obscur Death",
		description = "Plays the death scene from expedition 33 when the player dies. For those who come after.",
		tags = {"death", "sound", "music", "expedition 33", "clair obscur"}
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
     * @param clip The Clip object to use.
     */
    @Setter
    private Clip clip;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Death Sound started!");
		try
		{
			InputStream audioSrc = DeathSoundPlugin.class.getResourceAsStream("clair_obscur_death.wav");
			if (audioSrc == null) {
				log.error("Sound file not found in resources!");
				return;
			}
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
		if (actor instanceof Player && actor == client.getLocalPlayer())
		{
			if (clip != null)
			{
				// Set the volume BEFORE playing the clip
				setVolume();
				clip.setFramePosition(0);
				clip.start();
			}
		}
	}

	/**
	 * Sets the volume of the clip based on the configuration.
	 */
	private void setVolume()
	{
		// Get the volume from the config, defaulting to 80 if something goes wrong.
		float volume = config.volume() / 100.0f;

		// Ensure the volume is within the valid range [0.0, 1.0]
		volume = Math.max(0.0f, Math.min(1.0f, volume));

		try
		{
			// A "FloatControl" is what the Java Sound API uses to manage settings like volume, pan, and reverb.
			FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

			// The volume is set in decibels. We need to convert our linear scale [0.0, 1.0]
			// to the logarithmic decibel scale.
			float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
			gainControl.setValue(dB);
		}
		catch (IllegalArgumentException e)
		{
			log.warn("Could not set volume. Master gain control not found on clip.", e);
		}
	}


	@Provides
	DeathSoundConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DeathSoundConfig.class);
	}

}