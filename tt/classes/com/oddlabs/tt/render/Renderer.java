package com.oddlabs.tt.render;

import com.oddlabs.event.Deterministic;
import com.oddlabs.matchmaking.Game;
import com.oddlabs.net.NetworkSelector;
import com.oddlabs.net.TaskThread;
import com.oddlabs.tt.Main;
import com.oddlabs.tt.animation.AnimationManager;
import com.oddlabs.tt.animation.TimerAnimation;
import com.oddlabs.tt.animation.Updatable;
import com.oddlabs.tt.audio.AbstractAudioPlayer;
import com.oddlabs.tt.audio.AudioManager;
import com.oddlabs.tt.audio.AudioParameters;
import com.oddlabs.tt.audio.AudioPlayer;
import com.oddlabs.tt.camera.CameraState;
import com.oddlabs.tt.camera.MenuCamera;
import com.oddlabs.tt.delegate.MainMenu;
import com.oddlabs.tt.event.LocalEventQueue;
import com.oddlabs.tt.form.MessageForm;
import com.oddlabs.tt.form.ProgressForm;
import com.oddlabs.tt.form.WarningForm;
import com.oddlabs.tt.global.Globals;
import com.oddlabs.tt.global.GlobalsInit;
import com.oddlabs.tt.global.Settings;
import com.oddlabs.tt.gui.GUI;
import com.oddlabs.tt.gui.GUIRoot;
import com.oddlabs.tt.gui.Languages;
import com.oddlabs.tt.gui.LocalInput;
import com.oddlabs.tt.gui.Skin;
import com.oddlabs.tt.landscape.LandscapeResources;
import com.oddlabs.tt.landscape.NotificationListener;
import com.oddlabs.tt.landscape.TreeSupply;
import com.oddlabs.tt.landscape.World;
import com.oddlabs.tt.landscape.WorldParameters;
import com.oddlabs.tt.model.Selectable;
import com.oddlabs.tt.player.Player;
import com.oddlabs.tt.player.PlayerInfo;
import com.oddlabs.tt.procedural.Landscape;
import com.oddlabs.tt.resource.IslandGenerator;
import com.oddlabs.tt.resource.NativeResource;
import com.oddlabs.tt.resource.WorldGenerator;
import com.oddlabs.tt.resource.WorldInfo;
import com.oddlabs.tt.util.BackBufferRenderer;
import com.oddlabs.tt.util.GLState;
import com.oddlabs.tt.util.GLStateStack;
import com.oddlabs.tt.util.GLUtils;
import com.oddlabs.tt.util.StatCounter;
import com.oddlabs.tt.util.StrictGLU;
import com.oddlabs.tt.util.StrictMatrix4f;
import com.oddlabs.tt.util.Target;
import com.oddlabs.tt.util.TeeOutputStream;
import com.oddlabs.tt.util.Utils;
import com.oddlabs.tt.vbo.VBO;
import com.oddlabs.tt.viewer.AmbientAudio;
import com.oddlabs.tt.viewer.Cheat;
import com.oddlabs.tt.viewer.Selection;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.input.Keyboard;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.opengl.ARBMultisample;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GLContext;

public final strictfp class Renderer {
	private final static FloatBuffer matrix_buf = BufferUtils.createFloatBuffer(16);

	private static GLStateStack display_state_stack = new GLStateStack();

	private final static Renderer renderer_instance = new Renderer();
	private final static StatCounter fps = new StatCounter(10);
	private static int num_triangles_rendered;

	private static boolean grab_frames = false;

	private final Locale default_locale = new Locale(Locale.getDefault().getLanguage(), Locale.getDefault().getCountry(), "default");
	private final StrictMatrix4f proj = new StrictMatrix4f();

	private static AbstractAudioPlayer music;
	private static String music_path;
	private static TimerAnimation music_timer;

	private static boolean finished = false;

	private final CameraState frustum_state = new CameraState();
	private boolean movie_recording_started = false;
	private AmbientAudio ambient;

	public static float getFPS() {
		return fps.getAveragePerUpdate();
	}

	public static boolean isRegistered() {
		return true;
	}

	public static void makeCurrent() {
		try {
			Display.makeCurrent();
			GLStateStack.setCurrent(display_state_stack);
		} catch (LWJGLException e) {
			throw new RuntimeException(e);
		}
	}

	public static void runGame(String[] args) throws IOException {
		renderer_instance.run(args);
	}

	public static Renderer getRenderer() {
		return renderer_instance;
	}

	private void runGameLoop(NetworkSelector network, GUI gui) {
		AnimationManager.runGameLoop(network, gui, grab_frames);
	}

	private void setupMatrices(GUIRoot gui_root) {
		proj.setIdentity();
		multProjection(proj);
		CameraState camera = gui_root.getDelegate().getCamera().getState();
		camera.setView(proj);

		if (!Globals.frustum_freeze) {
			frustum_state.set(camera);
		}
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		proj.store(matrix_buf);
		matrix_buf.rewind();
		GL11.glLoadMatrix(matrix_buf);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		camera.getModelView().store(matrix_buf);
		matrix_buf.rewind();
		GL11.glLoadMatrix(matrix_buf);
	}

	public static void multProjection(StrictMatrix4f matrix) {
		StrictGLU.gluPerspective(matrix,
				Globals.FOV,
				LocalInput.getViewAspect(),
				Globals.VIEW_MIN,
				Globals.VIEW_MAX);
	}

	public static void registerTrianglesRendered(int count) {
		num_triangles_rendered += count;
	}

	public static int getTrianglesRendered() {
		return num_triangles_rendered;
	}

	private void display(GUI gui) {
		num_triangles_rendered = 0;
		fps.updateDelta(System.currentTimeMillis());
		NativeResource.deleteFinalized();
		setupMatrices(gui.getGUIRoot());
		gui.render(ambient, frustum_state);
	}

	public static void shutdownWithQuitScreen(GUIRoot gui_root) {
		shutdown();
	}

	public static void shutdown() {
		finished = true;
	}

	public static boolean isFinished() {
		return finished;
	}

	private static void deleteLog(Path log) throws IOException {
            for (Path LOG_FILES : com.oddlabs.util.Utils.LOG_FILES) {
                Path log_file = log.resolve(LOG_FILES);
                Files.deleteIfExists(log_file);
            }
		Files.deleteIfExists(log);
	}

	private static void deleteOldLogs(File last_log_dir, File new_log_dir, File logs_dir) {
		File[] logs = logs_dir.listFiles();
		if (logs == null)
			return;
            for (File log : logs) try {
                if (!log.isDirectory() || log.equals(last_log_dir) || log.equals(new_log_dir))
                    continue;
                deleteLog(log.toPath());
            } catch (IOException ignored) {

            }
	}

	private void run(String[] args) throws IOException {
		long start_time = System.currentTimeMillis();
		boolean first_frame = true;
		System.out.println("********** Running tt **********");
		System.out.flush();
		String platform_dir_name;
		switch (LWJGLUtil.getPlatform()) {
			case LWJGLUtil.PLATFORM_MACOSX:
				platform_dir_name = "Library/Application Support" + File.separator;
				break;
			case LWJGLUtil.PLATFORM_LINUX:
				platform_dir_name = ".";
				break;
			case LWJGLUtil.PLATFORM_WINDOWS:
			default:
				platform_dir_name = "";
				break;
		}
        Path homeDir = Paths.get(System.getProperty("user.home"));
        if (!Files.isDirectory(homeDir)) {
            homeDir = Files.createTempDirectory(Globals.GAME_NAME);
        }
		Path game_dir = homeDir.resolve(platform_dir_name + Globals.GAME_NAME);
        Files.createDirectories(game_dir);
		boolean eventload = false;
		boolean zipped = false;
		boolean silent = false;
		Settings settings = new Settings();
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "--grabframes":
                        grab_frames = true;
                        break;
                    case "--eventload":
                        eventload = true;
                        i++;
                        switch (args[i]) {
                            case "zipped":
                                zipped = true;
                                break;
                            case "normal":
                                break;
                            default:
                                throw new RuntimeException("Unknown event load mode: " + args[i]);
                        }
                        break;
                    case "--silent":
                        silent = true;
                        break;
                    default:
                        throw new RuntimeException("Unknown command line flag: " + args[i]);
                }
			}
		}

		// fetch initial settings
		settings.load(game_dir);

		if (eventload || grab_frames) {
			String last_event_log_path = settings.last_event_log_dir + File.separator + "event.log";
			if (zipped)
				last_event_log_path += ".gz";
System.out.println("last_event_log_path = " + last_event_log_path);
			// Only use when anal debugging
//			ChecksumLogger.initLogging();
			LocalEventQueue.getQueue().loadEvents(new File(last_event_log_path), zipped);
		}


		Path event_logs_dir = game_dir.resolve("logs");
		Path event_log_dir = event_logs_dir.resolve(Long.toString(System.currentTimeMillis()));
		if (LocalEventQueue.getQueue().getDeterministic() == null && settings.save_event_log) {
			Files.createDirectories(event_log_dir);
			System.out.println("Writing log files in " + event_log_dir);
			LocalEventQueue.getQueue().setEventsLogged(new File(event_log_dir + File.separator + com.oddlabs.util.Utils.EVENT_LOG));

			try {
				OutputStream std_err_file = Files.newOutputStream(event_log_dir.resolve(com.oddlabs.util.Utils.STD_ERR));
				OutputStream std_out_file = Files.newOutputStream(event_log_dir.resolve(com.oddlabs.util.Utils.STD_OUT));
				OutputStream new_err;
				OutputStream new_out;
				if (!silent) {
					new_err = new TeeOutputStream(System.err, std_err_file);
					new_out = new TeeOutputStream(System.out, std_out_file);
				} else {
					new_err = std_err_file;
					new_out = std_out_file;
				}
				System.setErr(new PrintStream(new_err));
				System.setOut(new PrintStream(new_out));
			} catch (FileNotFoundException e) {
				System.err.println("Failed to setup logging to " + event_log_dir + " exception: " + e);
			}
		}
		Deterministic deterministic = LocalEventQueue.getQueue().getDeterministic();
		game_dir = deterministic.log(game_dir);
		event_log_dir = deterministic.log(event_log_dir);
		settings = (Settings)deterministic.log(settings);
		String default_language = (String)deterministic.log(Locale.getDefault().getLanguage());
		String language = settings.language;
		if (language.equals("default"))
			language = default_language;
		if (!Languages.hasLanguage(language))
			language = "en";
		Locale.setDefault(new Locale(language));
		Settings.setSettings(settings);
		Path last_event_log_dir = Paths.get(settings.last_event_log_dir);
		boolean crashed = settings.crashed;
		NetworkSelector network = new NetworkSelector(LocalEventQueue.getQueue().getDeterministic(), LocalEventQueue.getQueue()::getMillis);
                initNetwork(network);
		LocalInput.settings( game_dir, event_log_dir, settings);
		try {
			initNative(crashed);
		} catch (LWJGLException e) {
			// Let it propagate
			throw new RuntimeException(e);
		}
		TaskThread task_thread = network.getTaskThread();
		if (!settings.inDeveloperMode() && !deterministic.isPlayback())
			deleteOldLogs(last_event_log_dir.toFile(), event_log_dir.toFile(), event_logs_dir.toFile());
		Skin.load();
		GUI gui = new GUI();

		GlobalsInit.init();
		LocalInput.init();

long startup_timei = System.currentTimeMillis() - start_time;
System.out.println("Init done after " + startup_timei);
		ambient = new AmbientAudio(AudioManager.getManager());

		setupMainMenu(network, gui, true);

		boolean reset_keyboard = false;
// Registry hack for mikkel!
/*try {
String value = com.oddlabs.tt.util.WindowsRegistryInterface.queryRegistrationKey("HKEY_LOCAL_MACHINE", "HARDWARE\\DeviceMap\\Video", "\\Device\\Video0");
System.out.println("value = " + value);
} catch (Exception e) {
e.printStackTrace();
}*/
		try {
			while (!finished) {
				runGameLoop(network, gui);
				if (Display.isVisible()) {
					if (AL.isCreated())
						AL10.alListenerf(AL10.AL_GAIN, 1f);
					if (reset_keyboard) {
						reset_keyboard = false;
						LocalInput.resetKeyboard();
					}
					if (!first_frame && !BackBufferRenderer.isBackBufferDirty()) {
						Display.update();
					}
					display(gui);
					if (first_frame) {
						long startup_time = System.currentTimeMillis() - start_time;
						System.out.println("First frame rendered after " + startup_time + " milliseconds");
						first_frame = false;
					}
					if (grab_frames && movie_recording_started)
						GLUtils.takeScreenshot("");
				} else {
					reset_keyboard = true;
					if (AL.isCreated())
						AL10.alListenerf(AL10.AL_GAIN, 0f);
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			}
			LocalEventQueue.getQueue().getDeterministic().setEnabled(true);
			Settings.getSettings().save();
		} finally {
			cleanup();
		}
	}

	public Locale getDefaultLocale() {
		return default_locale;
	}

	private static void failedOpenGL(LWJGLException e) {
        System.err.println("OpenGL Failure");
		e.printStackTrace(System.err);

		Main.shutdown();
	}

	private static boolean readOrSetPreference(String key, boolean value) {
		String result_string = readOrSetPreference(key, ""+value);
		return Boolean.valueOf(result_string);
	}

	private static int readOrSetPreference(String key, int value) {
		String result_string = readOrSetPreference(key, ""+value);
		try {
			int result = Integer.parseInt(result_string);
			return result;
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private static String readOrSetPreference(String key, String value) {
		try {
			Preferences pref = Preferences.userNodeForPackage(com.oddlabs.tt.render.Renderer.class);
			String result = pref.get(key, null);
			if (result == null) {
				pref.put(key, value);
				return value;
			} else
				return result;
		} catch (Exception e) {
			System.out.println("Could not access preferences");
			return value;
		}
	}

	public static void startMenu(NetworkSelector network, GUI gui) {
		setupMainMenu(network, gui, false);
	}

	private static void setupMainMenu(final NetworkSelector network, GUI gui, final boolean first_progress) {
		final WorldGenerator generator = new IslandGenerator(256, Landscape.NATIVE, Globals.LANDSCAPE_HILLS, Globals.LANDSCAPE_VEGETATION, Globals.LANDSCAPE_RESOURCES, Globals.LANDSCAPE_SEED);
		ProgressForm.setProgressForm(network, gui, (GUIRoot gui_root) -> finishMainMenu(network, gui_root, first_progress, generator), first_progress);
	}

	private static UIRenderer finishMainMenu(NetworkSelector network, GUIRoot gui_root, boolean first_progress, WorldGenerator generator) {
		AnimationManager.freezeTime();
		PlayerInfo player_info = new PlayerInfo(0, 0, "");
		RenderQueues render_queues = new RenderQueues();
		LandscapeResources landscape_resources = World.loadCommon(render_queues);
		WorldParameters world_params = new WorldParameters(Game.GAMESPEED_NORMAL, "", 2, Player.DEFAULT_MAX_UNIT_COUNT);
		PlayerInfo[] players = new PlayerInfo[]{player_info};
		WorldInfo world_info = generator.generate(players.length, world_params.getInitialUnitCount(), 0f);
		World world = World.newWorld(AudioManager.getManager(), landscape_resources, null, LandscapeResources.loadTreeLowDetails(), new NotificationListener() {
                        @Override
			public final void gamespeedChanged(int speed) {
			}
                        @Override
			public final void playerGamespeedChanged() {
			}
                        @Override
			public final void newAttackNotification(Selectable target) {
			}
                        @Override
			public final void newSelectableNotification(Selectable target) {
			}
                        @Override
			public final void registerTarget(Target target) {
			}
                        @Override
			public final void unregisterTarget(Target target) {
			}
                        @Override
			public final void updateTreeLowDetail(StrictMatrix4f matrix, TreeSupply tree) {
			}
                        @Override
			public final void patchesEdited(int patch_x0, int patch_y0, int patch_x1, int patch_y1) {
			}
		}, world_params, world_info, generator.getTerrainType(), players, new float[][]{Player.COLORS[0]});
		AnimationManager manager = new AnimationManager();
		LandscapeRenderer landscape_renderer = new LandscapeRenderer(world, world_info, gui_root, manager);
		Player local_player = world.getPlayers()[0];
		Selection selection = new Selection(local_player);
		UIRenderer renderer = new DefaultRenderer(new Cheat(), local_player, render_queues, generator.getTerrainType(), world_info, landscape_renderer, new Picker(manager, local_player, render_queues, landscape_renderer, selection), selection, generator);
		setMusicPath("/music/menu.ogg", 0f);
		MainMenu main_menu = new MainMenu(network, gui_root, new MenuCamera(world, manager));
		gui_root.pushDelegate(main_menu);
		if (first_progress && Settings.getSettings().warning_no_sound && !LocalInput.alIsCreated()) {
			ResourceBundle bundle = ResourceBundle.getBundle(Renderer.class.getName());
			gui_root.addModalForm(new WarningForm(Utils.getBundleString(bundle, "sound_not_available_caption"), Utils.getBundleString(bundle, "sound_not_available_message")));
		}
		if (!initNetwork(network)) {
//		if (true) {
			ResourceBundle bundle = ResourceBundle.getBundle(Renderer.class.getName());
			gui_root.addModalForm(new MessageForm(Utils.getBundleString(bundle, "network_not_available_caption"),
						Utils.getBundleString(bundle, "network_not_available_message"),
						Utils.getBundleString(bundle, "quit"), (int button, int x, int y, int clicks) -> {
                                                    shutdown();
                        }));
		}
		// We'll leave out the reporting, since checksum errors can happen when a peer is disconnected halfway through it's EOT
		// broadcast
		/*		if (Globals.checksum_error_in_last_game) {
				Globals.checksum_error_in_last_game = false;
				ResourceBundle bundle = ResourceBundle.getBundle(Renderer.class.getName());
				GUIRoot.getGUIRoot().addModalForm(new QuestionForm(Utils.getBundleString(bundle, "checksum_error_message"), new BugReportListener()));
				}*/
		return renderer;
	}

	private static boolean initNetwork(NetworkSelector network) {
		boolean is_network_created;
		try {
			network.initSelector();
			com.oddlabs.util.Utils.tryGetLoopbackAddress();
			is_network_created = true;
		} catch (IOException e) {
			System.err.println("Failed to initialize network: " + e);
			is_network_created = false;
		}
		return LocalEventQueue.getQueue().getDeterministic().log(is_network_created);
	}

	public void startMovieRecording() {
		System.out.println("ACTION!");
		movie_recording_started = true;
	}

	public void cleanup() {
		System.out.println("Cleaning up...");
		LocalEventQueue.getQueue().dispose();
		destroyNative();
		System.out.println("Cleanup complete. Exiting");
	}

	public static void resetInput() {
		LocalInput.resetKeys();
	}

	private static void destroyNative() {
		destroyAL();
		Display.destroy();
	}

	public static void dumpWindowInfo() {
		int r = GLUtils.getGLInteger(GL11.GL_RED_BITS);
		int g = GLUtils.getGLInteger(GL11.GL_GREEN_BITS);
		int b = GLUtils.getGLInteger(GL11.GL_BLUE_BITS);
		int a = GLUtils.getGLInteger(GL11.GL_ALPHA_BITS);
		int depth = GLUtils.getGLInteger(GL11.GL_DEPTH_BITS);
		int stencil = GLUtils.getGLInteger(GL11.GL_STENCIL_BITS);
		int sample_buffers = 0;
		int samples = 0;
		if (GLContext.getCapabilities().GL_ARB_multisample) {
			sample_buffers = GLUtils.getGLInteger(ARBMultisample.GL_SAMPLE_BUFFERS_ARB);
			samples = GLUtils.getGLInteger(ARBMultisample.GL_SAMPLES_ARB);
		}
		System.out.println("r = " + r + " | g = " + g + " | b = " + b + " | a = " + a + " | depth = " + depth + " | stencil = " + stencil + " | sample_buffers = " + sample_buffers + " | samples = " + samples);
	}

	private void initNative(boolean crashed) throws LWJGLException {
		String os_name = System.getProperty("os.name");
		System.out.println("os_name = '" + os_name + "'");
		String os_arch = System.getProperty("os.arch");
		System.out.println("os_arch = '" + os_arch + "'");
		String os_version = System.getProperty("os.version");
		System.out.println("os_version = '" + os_version + "'");
		String java_version = System.getProperty("java.version");
		System.out.println("java_version = '" + java_version + "'");
		String java_vendor = System.getProperty("java.vendor");
		System.out.println("java_vendor = '" + java_vendor + "'");
		long total_mem = Runtime.getRuntime().maxMemory();
		System.out.println("total_mem = '" + total_mem + "'");

		try {
			AL.create(null, -1, -1, false);
			initAL();
		} catch (LWJGLException e) {
			System.err.println("Could not create sound system: " + e);
		}

		try {
			int bpp = Display.getDisplayMode().getBitsPerPixel();
			Keyboard.enableRepeatEvents(true);
			Display.setTitle("Tribal Trouble");
			Display.setFullscreen(Settings.getSettings().fullscreen && (!LocalEventQueue.getQueue().getDeterministic().isPlayback() || grab_frames));
			SerializableDisplayMode target_mode;
			if (!crashed) {
				target_mode = new SerializableDisplayMode(Settings.getSettings().new_view_width, Settings.getSettings().new_view_height, bpp, Settings.getSettings().new_view_freq);
			} else {
				target_mode = new SerializableDisplayMode(Settings.getSettings().view_width, Settings.getSettings().view_height, bpp, Settings.getSettings().view_freq);
			}
			LocalInput.getLocalInput().setModeToNearest(target_mode);
//if (System.currentTimeMillis() > 0)
//throw new LWJGLException("Det fejlede fordi du bad den om det");
		} catch (LWJGLException e) {
			destroyAL();
			failedOpenGL(e);
			throw e;
		}
		String version = GL11.glGetString(GL11.GL_VERSION);
		System.out.println("GL version: '" + version + "'");
		String vendor = GL11.glGetString(GL11.GL_VENDOR);
		System.out.println("GL vendor: '" + vendor + "'");
		String renderer = GL11.glGetString(GL11.GL_RENDERER);
		System.out.println("GL renderer: '" + renderer + "'");
		String extensions = GL11.glGetString(GL11.GL_EXTENSIONS);
		System.out.println("GL extensions: '" + extensions + "'");

		if (GLUtils.isIntelGMA950() || !Settings.getSettings().useTextureCompression()) {  // Intel mac mini hack
			Globals.disableTextureCompression();
		}

		dumpWindowInfo();
		try {
			if (!GLContext.getCapabilities().OpenGL13) {
				if (!GLContext.getCapabilities().GL_ARB_multitexture)
					throw new LWJGLException("Neither OpenGL 1.3 nor GL_ARB_multitexture is supported, one of which is required for the game to run. Please upgrade your video drivers and/or video card.");
				System.out.println("OpenGL 1.3 is not supported, using GL_ARB_multitexture and GL_ARB_texture_compression instead");
			} else
				System.out.println("OpenGL 1.3 is supported");
			int num_tex_units = GLUtils.getGLInteger(GL13.GL_MAX_TEXTURE_UNITS);
			if (num_tex_units < 2)
				throw new LWJGLException("Number of texture units " + num_tex_units + " < 2");
		} catch (LWJGLException e) {
			destroyAL();
			Display.destroy();
			failedOpenGL(e);
			throw e;
		}
		if (!GLContext.getCapabilities().OpenGL12)
			System.out.println("OpenGL 1.2 is not supported");
		display_state_stack = new GLStateStack();
		GLStateStack.setCurrent(display_state_stack);
		resetInput();
System.out.println("vsync = " + Settings.getSettings().vsync);
System.out.println("vbo = " + Settings.getSettings().useVBO());
System.out.println("pbuffer = " + Settings.getSettings().usePbuffer());
System.out.println("fbo = " + Settings.getSettings().useFBO());
System.out.println("use_texture_compression = " + Settings.getSettings().useTextureCompression());
		if (Settings.getSettings().vsync)
			Display.setVSyncEnabled(true);
		initGL();
		initVisibleGL();
	}

	private void initAL() {
		if (AL.isCreated()) {
			AL10.alDistanceModel(AL10.AL_INVERSE_DISTANCE);
//			resetMusicPath();
//			if (Settings.getSettings().play_music)
//				initMusicPlayer();
		}
	}

	public void toggleSound() {
		Settings.getSettings().play_sfx = !Settings.getSettings().play_sfx;
		if (Settings.getSettings().play_sfx)
			AudioManager.getManager().startSources();
		else
			AudioManager.getManager().stopSources();
	}

	public void toggleMusic() {
		Settings.getSettings().play_music = !Settings.getSettings().play_music;
		if (Settings.getSettings().play_music) {
			initMusicPlayer();
		} else if (music != null) {
			music.stop(2.5f, Settings.getSettings().music_gain);
		}
	}

	private static void initMusicPlayer() {
		music = AudioManager.getManager().newAudio(new AudioParameters<>(music_path, 0f, 0f, 0f, AudioPlayer.AUDIO_RANK_MUSIC, AudioPlayer.AUDIO_DISTANCE_MUSIC, Settings.getSettings().music_gain, 1f, 1f, true, true, true));
	}

	public static void setMusicPath(String music_path, float delay) {
		if (AL.isCreated()) {
			if (music != null && Settings.getSettings().play_music) {
				music.stop(2.5f, Settings.getSettings().music_gain);
			}
			Renderer.music_path = music_path;
			if (Settings.getSettings().play_music) {
				if (music_timer != null)
					music_timer.stop();
				music_timer = new TimerAnimation(new MusicTimer(), delay);
				music_timer.start();
			}
		}
	}

	private final static class MusicTimer implements Updatable {
                @Override
		public void update(Object anim) {
			if (music_timer != null)
				music_timer.stop();
			music_timer = null;
			if (Settings.getSettings().play_music) {
				initMusicPlayer();
			}
		}
	}

	public static AbstractAudioPlayer getMusicPlayer() {
		return music;
	}

	private static void destroyAL() {
		if (AL.isCreated()) {
			AudioManager.getManager().destroy();
			AL.destroy();
		}
	}

	private void initVisibleGL() {
		if (Settings.getSettings().fullscreen_depth_workaround) {
			IntBuffer dummy_buf = BufferUtils.createIntBuffer(1);
			GL11.glReadPixels(0, 0, 1, 1, GL11.GL_DEPTH_COMPONENT, GL11.GL_UNSIGNED_INT, dummy_buf);
		}

		FloatBuffer float_array = BufferUtils.createFloatBuffer(4);
		GL11.glEnable(GL11.GL_LIGHT0);
		float[] light_diff_color = {1.0f, 1.0f, 1.0f, 1.0f};
		float_array.put(light_diff_color);
		float_array.rewind();
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, float_array);
		GL11.glLightModeli(GL11.GL_LIGHT_MODEL_LOCAL_VIEWER, 1);

		float[] global_ambient = {0.65f, 0.65f, 0.65f, 1.0f};
		float_array.put(global_ambient);
		float_array.rewind();
		GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, float_array);
		float[] material_color = {1.0f, 1.0f, 1.0f, 1.0f};
		float_array.put(material_color);
		float_array.rewind();
		GL11.glMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE, float_array);
		Display.update();
	}

	public static void initGL() {
		VBO.releaseAll();
		GL11.glFrontFace(GL11.GL_CCW);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPixelStorei(GL11.GL_PACK_ROW_LENGTH, 0);
		GL11.glPixelStorei(GL11.GL_PACK_SKIP_PIXELS, 0);
		GL11.glPixelStorei(GL11.GL_PACK_SKIP_ROWS, 0);
		GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
		GL11.glPixelStorei(GL11.GL_PACK_SWAP_BYTES, 0);

		GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, 0);
		GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, 0);
		GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, 0);
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		GL11.glPixelStorei(GL11.GL_UNPACK_SWAP_BYTES, 0);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glShadeModel(GL11.GL_SMOOTH);
//		GL11.glAlphaFunc(GL11.GL_GREATER, Globals.ALPHA_CUTOFF);
		// Setup landscape texture coordinate gen
		GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
		GL11.glTexGeni(GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_REPLACE);
		GLState.activeTexture(GL13.GL_TEXTURE1);
		GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_DECAL);
		GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
		GL11.glTexGeni(GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
		GLState.activeTexture(GL13.GL_TEXTURE0);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glPointSize(7.0f);
		clearScreen();
		GL11.glClearDepth(1.0);
	}

	public static void clearScreen() {
		GL11.glClearColor(0f, 0f, 0f, 0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		//GL11.glClearColor(1f, 0f, 1f, 0f);
	}
}
