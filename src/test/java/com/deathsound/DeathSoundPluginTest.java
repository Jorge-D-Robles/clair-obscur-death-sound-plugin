package com.deathsound;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.ActorDeath;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations; // Import this

import javax.sound.sampled.Clip;

import static org.mockito.Mockito.*;

/**
 * Tests for the Death Sound plugin.
 * NOTE: This test uses manual mock initialization with MockitoAnnotations.openMocks()
 * instead of a JUnit runner.
 */
public class DeathSoundPluginTest
{
	@Mock
	private Client client;

	@Mock
	private Player localPlayer;

	@Mock
	private Clip clip;

	@InjectMocks
	private DeathSoundPlugin deathSoundPlugin;

	@Before
	public void setUp() throws Exception
	{
		// This line initializes all the @Mock and @InjectMocks annotations above.
		MockitoAnnotations.openMocks(this);

		when(client.getLocalPlayer()).thenReturn(localPlayer);
		deathSoundPlugin.setClip(clip);
	}

	@Test
	public void testPlayerDeathPlaysSound()
	{
		// --- ARRANGE ---
		ActorDeath actorDeath = new ActorDeath(localPlayer);

		// --- ACT ---
		deathSoundPlugin.onActorDeath(actorDeath);

		// --- ASSERT ---
		verify(clip).setFramePosition(0);
		verify(clip).start();
	}

	@Test
	public void testOtherActorDeathDoesNotPlaySound()
	{
		// --- ARRANGE ---
		Player otherPlayer = mock(Player.class);
		ActorDeath actorDeath = new ActorDeath(otherPlayer);

		// --- ACT ---
		deathSoundPlugin.onActorDeath(actorDeath);

		// --- ASSERT ---
		verify(clip, never()).start();
	}

	@Test
	public void testShutdownClosesClip() throws Exception
	{
		// --- ARRANGE ---
		// No specific arrangement needed.

		// --- ACT ---
		deathSoundPlugin.shutDown();

		// --- ASSERT ---
		verify(clip).close();
	}
}