package cr0s.warpdrive.config;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBlockTransformer;
import cr0s.warpdrive.compat.CompatAdvancedRepulsionSystems;
import cr0s.warpdrive.compat.CompatAppliedEnergistics2;
import cr0s.warpdrive.compat.CompatArsMagica2;
import cr0s.warpdrive.compat.CompatBiblioCraft;
import cr0s.warpdrive.compat.CompatBotania;
import cr0s.warpdrive.compat.CompatBuildCraft;
import cr0s.warpdrive.compat.CompatCarpentersBlocks;
import cr0s.warpdrive.compat.CompatComputerCraft;
import cr0s.warpdrive.compat.CompatCustomNpcs;
import cr0s.warpdrive.compat.CompatDraconicEvolution;
import cr0s.warpdrive.compat.CompatEnderIO;
import cr0s.warpdrive.compat.CompatEvilCraft;
import cr0s.warpdrive.compat.CompatForgeMultipart;
import cr0s.warpdrive.compat.CompatImmersiveEngineering;
import cr0s.warpdrive.compat.CompatIndustrialCraft2;
import cr0s.warpdrive.compat.CompatJABBA;
import cr0s.warpdrive.compat.CompatMekanism;
import cr0s.warpdrive.compat.CompatMetallurgy;
import cr0s.warpdrive.compat.CompatNatura;
import cr0s.warpdrive.compat.CompatOpenComputers;
import cr0s.warpdrive.compat.CompatParziStarWars;
import cr0s.warpdrive.compat.CompatPneumaticCraft;
import cr0s.warpdrive.compat.CompatRedstonePaste;
import cr0s.warpdrive.compat.CompatSGCraft;
import cr0s.warpdrive.compat.CompatStargateTech2;
import cr0s.warpdrive.compat.CompatTConstruct;
import cr0s.warpdrive.compat.CompatTechguns;
import cr0s.warpdrive.compat.CompatThaumcraft;
import cr0s.warpdrive.compat.CompatThermalDynamics;
import cr0s.warpdrive.compat.CompatThermalExpansion;
import cr0s.warpdrive.compat.CompatWarpDrive;
import cr0s.warpdrive.config.structures.StructureManager;
import cr0s.warpdrive.data.CelestialObject;
import cr0s.warpdrive.data.CelestialObjectManager;
import cr0s.warpdrive.data.EnumShipMovementType;
import cr0s.warpdrive.data.EnumDisplayAlignment;
import cr0s.warpdrive.data.EnumTooltipCondition;
import cr0s.warpdrive.network.PacketHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.oredict.OreDictionary;

public class WarpDriveConfig {
	
	private static final boolean unused = false; // TODO
	
	private static String stringConfigDirectory;
	private static File configDirectory;
	private static DocumentBuilder xmlDocumentBuilder;
	private static final String[] defaultXML_fillers = {
			"filler-default.xml",
			"filler-netherores.xml",
			"filler-undergroundbiomes.xml",
	};
	private static final String[] defaultXML_loots = {
			"loot-default.xml",
	};
	private static final String[] defaultXML_structures = {
			"structures-default.xml",
			"structures-netherores.xml",
	};
	private static final String[] defaultXML_celestialObjects = {
			"celestialObjects-default.xml"
	};
	
	public static GenericSetManager<Filler> FillerManager = new GenericSetManager<>("filler", "filler", "fillerSet", Filler.DEFAULT);
	public static GenericSetManager<Loot> LootManager = new GenericSetManager<>("loot", "loot", "lootSet", Loot.DEFAULT);
	
	/*
	 * The variables which store whether or not individual mods are loaded
	 */
	public static boolean isAdvancedRepulsionSystemLoaded = false;
	public static boolean isAdvancedSolarPanelLoaded = false;
	public static boolean isCoFHCoreLoaded = false;
	public static boolean isComputerCraftLoaded = false;
	public static boolean isDefenseTechLoaded = false;
	public static boolean isEnderIOLoaded = false;
	public static boolean isForgeMultipartLoaded = false;
	public static boolean isGregTech5Loaded = false;
	public static boolean isGregTech6Loaded = false;
	public static boolean isICBMClassicLoaded = false;
	public static boolean isICBMLoaded = false;
	public static boolean isImmersiveEngineeringLoaded = false;
	public static boolean isIndustrialCraft2Loaded = false;
	public static boolean isNotEnoughItemsLoaded = false;
	public static boolean isOpenComputersLoaded = false;
	public static boolean isThermalExpansionLoaded = false;
	
	public static ItemStack IC2_compressedAir;
	public static ItemStack IC2_emptyCell;
	public static Block IC2_rubberWood;
	public static ItemStack IC2_Resin;
	public static Block CC_Computer, CC_peripheral, CCT_Turtle, CCT_Expanded, CCT_Advanced;
	
	// Mod configuration (see loadConfig() for comments/definitions)
	// General
	public static int G_SPACE_BIOME_ID = 95;
	public static int G_SPACE_PROVIDER_ID = 14;
	public static int G_HYPERSPACE_PROVIDER_ID = 15;
	public static int G_ENTITY_SPHERE_GENERATOR_ID = 241;
	public static int G_ENTITY_STAR_CORE_ID = 242;
	public static int G_ENTITY_CAMERA_ID = 243;
	public static int G_ENTITY_PARTICLE_BUNCH_ID = 244;
	
	public static final int LUA_SCRIPTS_NONE = 0;
	public static final int LUA_SCRIPTS_TEMPLATES = 1;
	public static final int LUA_SCRIPTS_ALL = 2;
	public static int G_LUA_SCRIPTS = LUA_SCRIPTS_ALL;
	public static String G_SCHEMALOCATION = "warpDrive_schematics";
	public static int G_BLOCKS_PER_TICK = 3500;
	public static boolean G_ENABLE_PROTECTION_CHECKS = true;
	
	// Client
	public static float CLIENT_LOCATION_SCALE = 1.0F;
	public static String CLIENT_LOCATION_FORMAT_TITLE = "§l%1$s";
	public static int CLIENT_LOCATION_BACKGROUND_COLOR = Commons.colorARGBtoInt(64, 48, 48, 48);
	public static int CLIENT_LOCATION_TEXT_COLOR = Commons.colorARGBtoInt(230, 180, 180, 240);
	public static boolean CLIENT_LOCATION_HAS_SHADOW = true;
	public static EnumDisplayAlignment CLIENT_LOCATION_SCREEN_ALIGNMENT = EnumDisplayAlignment.MIDDLE_RIGHT;
	public static int CLIENT_LOCATION_SCREEN_OFFSET_X = 0;
	public static int CLIENT_LOCATION_SCREEN_OFFSET_Y = -20;
	public static EnumDisplayAlignment CLIENT_LOCATION_TEXT_ALIGNMENT = EnumDisplayAlignment.TOP_RIGHT;
	public static float CLIENT_LOCATION_WIDTH_RATIO = 0.0F;
	public static int CLIENT_LOCATION_WIDTH_MIN = 90;
	
	public static EnumTooltipCondition CLIENT_TOOLTIP_REGISTRY_NAME = EnumTooltipCondition.ADVANCED_TOOLTIPS;
	public static EnumTooltipCondition CLIENT_TOOLTIP_ORE_DICTIONARY_NAME = EnumTooltipCondition.ALWAYS;
	public static EnumTooltipCondition CLIENT_TOOLTIP_ARMOR = EnumTooltipCondition.ALWAYS;
	public static EnumTooltipCondition CLIENT_TOOLTIP_BURN_TIME = EnumTooltipCondition.ADVANCED_TOOLTIPS;
	public static EnumTooltipCondition CLIENT_TOOLTIP_DURABILITY = EnumTooltipCondition.ALWAYS;
	public static EnumTooltipCondition CLIENT_TOOLTIP_FLAMMABILITY = EnumTooltipCondition.ADVANCED_TOOLTIPS;
	public static EnumTooltipCondition CLIENT_TOOLTIP_FLUID = EnumTooltipCondition.ALWAYS;
	public static EnumTooltipCondition CLIENT_TOOLTIP_HARDNESS = EnumTooltipCondition.ADVANCED_TOOLTIPS;
	public static EnumTooltipCondition CLIENT_TOOLTIP_HARVESTING = EnumTooltipCondition.ALWAYS;
	public static EnumTooltipCondition CLIENT_TOOLTIP_OPACITY = EnumTooltipCondition.ADVANCED_TOOLTIPS;
	public static EnumTooltipCondition CLIENT_TOOLTIP_REPAIR_WITH = EnumTooltipCondition.ON_SNEAK;
	
	// Logging
	public static boolean LOGGING_JUMP = false;
	public static boolean LOGGING_JUMPBLOCKS = false;
	public static boolean LOGGING_ENERGY = false;
	public static boolean LOGGING_EFFECTS = false;
	public static boolean LOGGING_CLOAKING = false;
	public static boolean LOGGING_VIDEO_CHANNEL = false;
	public static boolean LOGGING_TARGETING = false;
	public static boolean LOGGING_WEAPON = false;
	public static boolean LOGGING_CAMERA = false;
	public static boolean LOGGING_BUILDING = false;
	public static boolean LOGGING_COLLECTION = false;
	public static boolean LOGGING_TRANSPORTER = false;
	public static boolean LOGGING_LUA = false;
	public static boolean LOGGING_RADAR = false;
	public static boolean LOGGING_BREATHING = false;
	public static boolean LOGGING_WORLD_GENERATION = false;
	public static boolean LOGGING_PROFILING_CPU_USAGE = true;
	public static boolean LOGGING_PROFILING_MEMORY_ALLOCATION = false;
	public static boolean LOGGING_PROFILING_THREAD_SAFETY = false;
	public static boolean LOGGING_DICTIONARY = false;
	public static boolean LOGGING_STARMAP = false;
	public static boolean LOGGING_BREAK_PLACE = false;
	public static boolean LOGGING_FORCEFIELD = false;
	public static boolean LOGGING_FORCEFIELD_REGISTRY = false;
	public static boolean LOGGING_ACCELERATOR = false;
	public static boolean LOGGING_XML_PREPROCESSOR = false;
	public static boolean LOGGING_RENDERING = false;
	public static boolean LOGGING_CHUNK_HANDLER = false;
	public static boolean LOGGING_CHUNK_LOADING = true;
	public static boolean LOGGING_ENTITY_FX = false;
	public static boolean LOGGING_CLIENT_SYNCHRONIZATION = false;
	
	// Starmap
	public static int STARMAP_REGISTRY_UPDATE_INTERVAL_SECONDS = 10;
	public static boolean STARMAP_ALLOW_OVERLAPPING_CELESTIAL_OBJECTS = false;
	
	// Space generator
	public static int SPACE_GENERATOR_Y_MIN_CENTER = 55;
	public static int SPACE_GENERATOR_Y_MAX_CENTER = 128;
	public static int SPACE_GENERATOR_Y_MIN_BORDER = 5;
	public static int SPACE_GENERATOR_Y_MAX_BORDER = 200;
	
	// Ship movement costs
	public static ShipMovementCosts.Factors[] SHIP_MOVEMENT_COSTS_FACTORS = null;
	
	// Ship
	public static int SHIP_MAX_ENERGY_STORED = 100000000;
	public static int SHIP_TELEPORT_ENERGY_PER_ENTITY = 1000000;
	public static int SHIP_VOLUME_MAX_ON_PLANET_SURFACE = 1200;
	public static int SHIP_VOLUME_MIN_FOR_HYPERSPACE = 3000;
	public static int SHIP_MAX_SIDE_SIZE = 127;
	public static int SHIP_COLLISION_TOLERANCE_BLOCKS = 3;
	public static int SHIP_WARMUP_RANDOM_TICKS = 60;
	public static int SHIP_CONTROLLER_UPDATE_INTERVAL_SECONDS = 2;
	public static int SHIP_CORE_ISOLATION_UPDATE_INTERVAL_SECONDS = 10;
	public static int SHIP_VOLUME_SCAN_AGE_TOLERANCE_SECONDS = 120;
	public static String[] SHIP_VOLUME_UNLIMITED_PLAYERNAMES = { "notch", "someone" };
	public static int SHIP_SUMMON_MAX_RANGE = 500;
	public static boolean SHIP_SUMMON_ACROSS_DIMENSIONS = false;
	
	// Radar
	public static int RADAR_MAX_ENERGY_STORED = 100000000; // 100kk eU
	public static int RADAR_SCAN_MIN_ENERGY_COST = 10000;
	public static double[] RADAR_SCAN_ENERGY_COST_FACTORS = { 0.0, 0.0, 0.0, 0.0001 };
	public static int RADAR_SCAN_MIN_DELAY_SECONDS = 1;
	public static double[] RADAR_SCAN_DELAY_FACTORS_SECONDS = { 1.0, 0.001, 0.0, 0.0 };
	public static int RADAR_MAX_ISOLATION_RANGE = 2;
	public static int RADAR_MIN_ISOLATION_BLOCKS = 2;
	public static int RADAR_MAX_ISOLATION_BLOCKS = 16;
	public static double RADAR_MIN_ISOLATION_EFFECT = 0.12;
	public static double RADAR_MAX_ISOLATION_EFFECT = 1.00;
	
	// Ship Scanner
	public static int SS_MAX_DEPLOY_RADIUS_BLOCKS = 50;
	public static int SS_SEARCH_INTERVAL_TICKS = 20;
	public static int SS_SCAN_BLOCKS_PER_SECOND = 10;
	public static int SS_DEPLOY_BLOCKS_PER_INTERVAL = 10;
	public static int SS_DEPLOY_INTERVAL_TICKS = 4;
	
	// Laser medium
	public static int LASER_MEDIUM_MAX_ENERGY_STORED = 100000;
	
	// Laser Emitter
	// 1 main laser + 4 boosting lasers = 10 * 100k + 0.6 * 40 * 100k = 3.4M
	public static int    LASER_CANNON_MAX_MEDIUMS_COUNT = 10;
	public static int    LASER_CANNON_MAX_LASER_ENERGY = 3400000;
	public static int    LASER_CANNON_EMIT_FIRE_DELAY_TICKS = 5;
	public static int    LASER_CANNON_EMIT_SCAN_DELAY_TICKS = 1;
	
	public static double LASER_CANNON_BOOSTER_BEAM_ENERGY_EFFICIENCY = 0.60D;
	public static double LASER_CANNON_ENERGY_ATTENUATION_PER_AIR_BLOCK  = 0.000200D;
	public static double LASER_CANNON_ENERGY_ATTENUATION_PER_VOID_BLOCK = 0.000005D;
	public static double LASER_CANNON_ENERGY_ATTENUATION_PER_BROKEN_BLOCK = 0.23D;
	public static int    LASER_CANNON_RANGE_MAX = 500;
	
	public static int    LASER_CANNON_ENTITY_HIT_SET_ON_FIRE_SECONDS = 20;
	public static int    LASER_CANNON_ENTITY_HIT_ENERGY = 15000;
	public static int    LASER_CANNON_ENTITY_HIT_BASE_DAMAGE = 3;
	public static int    LASER_CANNON_ENTITY_HIT_ENERGY_PER_DAMAGE = 30000;
	public static int    LASER_CANNON_ENTITY_HIT_MAX_DAMAGE = 100;
	
	public static int    LASER_CANNON_ENTITY_HIT_EXPLOSION_ENERGY_THRESHOLD = 900000;
	public static float  LASER_CANNON_ENTITY_HIT_EXPLOSION_BASE_STRENGTH = 4.0F;
	public static int    LASER_CANNON_ENTITY_HIT_EXPLOSION_ENERGY_PER_STRENGTH = 125000;
	public static float  LASER_CANNON_ENTITY_HIT_EXPLOSION_MAX_STRENGTH = 4.0F;
	
	public static int    LASER_CANNON_BLOCK_HIT_ENERGY_MIN = 75000;
	public static int    LASER_CANNON_BLOCK_HIT_ENERGY_PER_BLOCK_HARDNESS = 150000;
	public static int    LASER_CANNON_BLOCK_HIT_ENERGY_MAX = 750000;
	public static double LASER_CANNON_BLOCK_HIT_ABSORPTION_PER_BLOCK_HARDNESS = 0.01;
	public static double LASER_CANNON_BLOCK_HIT_ABSORPTION_MAX = 0.80;
	
	public static float  LASER_CANNON_BLOCK_HIT_EXPLOSION_HARDNESS_THRESHOLD = 5.0F;
	public static float  LASER_CANNON_BLOCK_HIT_EXPLOSION_BASE_STRENGTH = 8.0F;
	public static int    LASER_CANNON_BLOCK_HIT_EXPLOSION_ENERGY_PER_STRENGTH = 125000;
	public static float  LASER_CANNON_BLOCK_HIT_EXPLOSION_MAX_STRENGTH = 50F;
	
	// Mining laser
	// BuildCraft quarry values for reference
	// - harvesting one block is 60 MJ/block = 600 RF/block = ~145 EU/block
	// - maximum speed is 3.846 ticks per blocks
	// - overall consumption varies from 81.801 to 184.608 MJ/block (depending on speed) = up to 1846.08 RF/block = up to ~448 EU/block
	// - at radius 5, one layer takes ~465 ticks ((ML_MAX_RADIUS * 2 + 1) ^ 2 * 3.846)
	// - overall consumption is ((ML_MAX_RADIUS * 2 + 1) ^ 2) * 448 => ~ 54208 EU/layer
	// WarpDrive mining laser in comparison
	// - each mined layer is scanned twice
	// - default ore generation: 1 ore out of 25 blocks
	// - overall consumption in 'all, space' is ML_EU_PER_LAYER_SPACE / ((ML_MAX_RADIUS * 2 + 1) ^ 2) + ML_EU_PER_BLOCK_SPACE => ~ 356 EU/block
	// - overall consumption in 'all, space' is ML_EU_PER_LAYER_SPACE + ((ML_MAX_RADIUS * 2 + 1) ^ 2) * ML_EU_PER_BLOCK_SPACE => ~ 43150 EU/layer
	// - overall consumption in 'ores, space' is ML_EU_PER_LAYER_SPACE + ((ML_MAX_RADIUS * 2 + 1) ^ 2) * ML_EU_PER_BLOCK_SPACE * ML_EU_MUL_ORESONLY / 25 => ~ 28630 EU/layer
	// - at radius 5, one layer takes 403 ticks (2 * ML_SCAN_DELAY_TICKS + ML_MINE_DELAY_TICKS * (ML_MAX_RADIUS * 2 + 1) ^ 2)
	public static int MINING_LASER_MAX_MEDIUMS_COUNT = 3;
	public static int MINING_LASER_RADIUS_NO_LASER_MEDIUM = 4;
	public static int MINING_LASER_RADIUS_PER_LASER_MEDIUM = 1;
	public static int MINING_LASER_WARMUP_DELAY_TICKS = 20;
	public static int MINING_LASER_SCAN_DELAY_TICKS = 20;
	public static int MINING_LASER_MINE_DELAY_TICKS = 3;
	public static int MINING_LASER_SPACE_ENERGY_PER_LAYER = 20000;
	public static int MINING_LASER_PLANET_ENERGY_PER_LAYER = 33000;
	public static int MINING_LASER_SPACE_ENERGY_PER_BLOCK = 1500;
	public static int MINING_LASER_PLANET_ENERGY_PER_BLOCK = 2500;
	public static double MINING_LASER_ORESONLY_ENERGY_FACTOR = 15.0; // lower value encourages to keep the land 'clean'
	public static double MINING_LASER_SILKTOUCH_ENERGY_FACTOR = 1.5;
	public static double MINING_LASER_SILKTOUCH_DEUTERIUM_L = 0.0;
	public static double MINING_LASER_FORTUNE_ENERGY_FACTOR = 1.5;
	
	// Tree farm
	public static int TREE_FARM_MAX_MEDIUMS_COUNT = 5;
	public static int TREE_FARM_MAX_SCAN_RADIUS_NO_LASER_MEDIUM = 3;
	public static int TREE_FARM_MAX_SCAN_RADIUS_PER_LASER_MEDIUM = 2;
	public static int TREE_FARM_totalMaxRadius = 0;
	public static int TREE_FARM_MAX_LOG_DISTANCE = 8;
	public static int TREE_FARM_MAX_LOG_DISTANCE_PER_MEDIUM = 4;
	
	// Cloaking
	public static int CLOAKING_MAX_ENERGY_STORED = 500000000;
	public static int CLOAKING_COIL_CAPTURE_BLOCKS = 5;
	public static int CLOAKING_MAX_FIELD_RADIUS = 63;
	public static int CLOAKING_TIER1_ENERGY_PER_BLOCK = 32;
	public static int CLOAKING_TIER2_ENERGY_PER_BLOCK = 128;
	public static int CLOAKING_FIELD_REFRESH_INTERVAL_SECONDS = 3;
	
	// Air generator
	public static int BREATHING_ENERGY_PER_CANISTER = 200;
	public static int[] BREATHING_ENERGY_PER_NEW_AIR_BLOCK = { 12, 180, 2610 };
	public static int[] BREATHING_ENERGY_PER_EXISTING_AIR_BLOCK = { 4, 60, 870 };
	public static int[] BREATHING_MAX_ENERGY_STORED = { 1400, 21000, 304500 };  // almost 6 mn of autonomy
	public static int BREATHING_AIR_GENERATION_TICKS = 40;
	public static int[] BREATHING_AIR_GENERATION_RANGE_BLOCKS = { 16, 48, 144 };
	public static int BREATHING_VOLUME_UPDATE_DEPTH_BLOCKS = 256;
	public static int BREATHING_AIR_SIMULATION_DELAY_TICKS = 30;
	public static final boolean BREATHING_AIR_BLOCK_DEBUG = false;
	public static boolean BREATHING_AIR_AT_ENTITY_DEBUG = false;
	
	// IC2 Reactor cooler
	public static int IC2_REACTOR_MAX_HEAT_STORED = 30000;
	public static int IC2_REACTOR_FOCUS_HEAT_TRANSFER_PER_TICK = 648;
	public static int IC2_REACTOR_COMPONENT_HEAT_TRANSFER_PER_TICK = 54;
	public static int IC2_REACTOR_REACTOR_HEAT_TRANSFER_PER_TICK = 54;
	public static int IC2_REACTOR_COOLING_PER_INTERVAL = 1080;
	public static double IC2_REACTOR_ENERGY_PER_HEAT = 2.0D;
	public static int IC2_REACTOR_COOLING_INTERVAL_TICKS = 10;
	
	// Transporter
	public static int      TRANSPORTER_MAX_ENERGY_STORED = 1000000;
	public static int      TRANSPORTER_ENERGY_STORED_UPGRADE_BONUS = TRANSPORTER_MAX_ENERGY_STORED / 2;
	public static int      TRANSPORTER_ENERGY_STORED_UPGRADE_MAX_QUANTITY = 8;
	public static int      TRANSPORTER_SETUP_UPDATE_PARAMETERS_TICKS = 1 * 20;
	public static int      TRANSPORTER_SETUP_SCANNER_RANGE_XZ_BLOCKS = 8;
	public static int      TRANSPORTER_SETUP_SCANNER_RANGE_Y_BELOW_BLOCKS = 3;
	public static int      TRANSPORTER_SETUP_SCANNER_RANGE_Y_ABOVE_BLOCKS = 1;
	public static int      TRANSPORTER_RANGE_BASE_BLOCKS = 256;
	public static int      TRANSPORTER_RANGE_UPGRADE_BLOCKS = 64;
	public static int      TRANSPORTER_RANGE_UPGRADE_MAX_QUANTITY = 8;
	public static double[] TRANSPORTER_LOCKING_ENERGY_FACTORS = { 20.0, 3.0, 0.0, 10.0, 1.0 / Math.sqrt(2.0) };
	public static double   TRANSPORTER_LOCKING_STRENGTH_FACTOR_PER_TICK = Math.pow(0.01D, 1.0D / 300.0D); // natural decay down to 1% over 300 ticks
	public static double   TRANSPORTER_LOCKING_STRENGTH_IN_WILDERNESS = 0.25D;
	public static double   TRANSPORTER_LOCKING_STRENGTH_AT_BEACON = 0.50D;
	public static double   TRANSPORTER_LOCKING_STRENGTH_AT_TRANSPORTER = 1.00D;
	public static double   TRANSPORTER_LOCKING_STRENGTH_BONUS_AT_MAX_ENERGY_FACTOR = 0.5D;
	public static double   TRANSPORTER_LOCKING_STRENGTH_UPGRADE = 0.15D;
	public static double   TRANSPORTER_LOCKING_SPEED_IN_WILDERNESS = 0.25D;
	public static double   TRANSPORTER_LOCKING_SPEED_AT_BEACON = 0.75D;
	public static double   TRANSPORTER_LOCKING_SPEED_AT_TRANSPORTER = 1.0D;
	public static double   TRANSPORTER_LOCKING_SPEED_UPGRADE = 0.25D;
	public static int      TRANSPORTER_LOCKING_SPEED_OPTIMAL_TICKS = 5 * 20;
	public static int      TRANSPORTER_LOCKING_UPGRADE_MAX_QUANTITY = 2;
	public static int      TRANSPORTER_JAMMED_COOLDOWN_TICKS = 2 * 20;
	public static double[] TRANSPORTER_ENERGIZING_ENERGY_FACTORS = { 10000.0, 1500.0, 0.0, 10.0, 1.0 / Math.sqrt(2.0) };
	public static double   TRANSPORTER_ENERGIZING_MAX_ENERGY_FACTOR = 10.0D;
	public static int      TRANSPORTER_ENERGIZING_FAILURE_MAX_DAMAGE = 5;
	public static double   TRANSPORTER_ENERGIZING_SUCCESS_LOCK_BONUS = 0.20D;
	public static int      TRANSPORTER_ENERGIZING_SUCCESS_MAX_DAMAGE = 100;
	public static double   TRANSPORTER_ENERGIZING_LOCKING_LOST = 0.5D;
	public static int      TRANSPORTER_ENERGIZING_CHARGING_TICKS = 3 * 20;
	public static int      TRANSPORTER_ENERGIZING_COOLDOWN_TICKS = 10 * 20;
	public static double   TRANSPORTER_ENERGIZING_ENTITY_MOVEMENT_TOLERANCE_BLOCKS = 1.0D;
	public static int      TRANSPORTER_ENTITY_GRAB_RADIUS_BLOCKS = 2;
	public static int      TRANSPORTER_FOCUS_SEARCH_RADIUS_BLOCKS = 2;
	public static int      TRANSPORTER_BEACON_MAX_ENERGY_STORED = 60000;
	public static int      TRANSPORTER_BEACON_ENERGY_PER_TICK = 60000 / (300 * 20);  // 10 EU/t over 5 minutes
	public static int      TRANSPORTER_BEACON_DEPLOYING_DELAY_TICKS = 1 * 20;
	
	// Enantiomorphic power reactor
	public static int ENAN_REACTOR_MAX_ENERGY_STORED = 100000000;
	public static int ENAN_REACTOR_UPDATE_INTERVAL_TICKS = 5;
	public static int ENAN_REACTOR_MAX_LASERS_PER_SECOND = 6;
	
	// Power store
	public static int[] ENERGY_BANK_MAX_ENERGY_STORED = { 800000, 4000000, 20000000 };
	public static int[] ENERGY_BANK_IC2_TIER = { 2, 3, 4 };
	public static int[] ENERGY_BANK_TRANSFER_PER_TICK = { 200, 1000, 5000 };
	public static double[] ENERGY_BANK_EFFICIENCY_PER_UPGRADE = { 0.95D, 0.98D, 1.0D };
	
	// Laser lift
	public static int LIFT_MAX_ENERGY_STORED = 900;
	public static int LIFT_ENERGY_PER_ENTITY = 150;
	public static int LIFT_UPDATE_INTERVAL_TICKS = 10;
	public static int LIFT_ENTITY_COOLDOWN_TICKS = 40;
	
	// Chunk loader
	public static int CHUNK_LOADER_MAX_ENERGY_STORED = 1000000;
	public static int CHUNK_LOADER_MAX_RADIUS = 2;
	public static int CHUNK_LOADER_ENERGY_PER_CHUNK = 8;
	
	// Hull
	public static float[] HULL_HARDNESS = { 25.0F, 50.0F, 80.0F };
	public static float[] HULL_BLAST_RESISTANCE = { 60.0F, 90.0F, 120.0F };
	
	// Block transformers library
	public static HashMap<String, IBlockTransformer> blockTransformers = new HashMap<>(30);
	
	// Particles accelerator
	public static boolean ACCELERATOR_ENABLE = false;


	public static boolean ALLOW_PLAYER_SPACE_TRAVEL = false;
	public static int PLAYER_SPACE_TRAVEL_Y_LEVEL = 500;
	
	public static final double[] ACCELERATOR_TEMPERATURES_K = { 270.0, 200.0, 7.0 };
	public static final double ACCELERATOR_THRESHOLD_DEFAULT = 0.95D;
	public static int ACCELERATOR_MAX_PARTICLE_BUNCHES = 20;
	
	public static Block getModBlock(final String mod, final String id) {
		try {
			return GameRegistry.findBlock(mod, id);
		} catch (final Exception exception) {
			WarpDrive.logger.info(String.format("Failed to get mod block for %s:%s", mod, id));
			exception.printStackTrace();
		}
		return Blocks.fire;
	}
	
	public static ItemStack getModItemStack(final String mod, final String id, final int meta) {
		final ItemStack itemStack = getModItemStackOrNull(mod, id, meta);
		if (itemStack != null) {
			return itemStack;
		}
		return new ItemStack(Blocks.fire);
	}
	
	public static ItemStack getModItemStack(final String mod1, final String id1, final int meta1,
	                                        final String mod2, final String id2, final int meta2) {
		ItemStack itemStack = getModItemStackOrNull(mod1, id1, meta1);
		if (itemStack != null) {
			return itemStack;
		}
		itemStack = getModItemStackOrNull(mod2, id2, meta2);
		if (itemStack != null) {
			return itemStack;
		}
		return new ItemStack(Blocks.fire);
	}
	
	public static ItemStack getModItemStack(final String mod1, final String id1, final int meta1,
	                                        final String mod2, final String id2, final int meta2,
	                                        final String mod3, final String id3, final int meta3) {
		ItemStack itemStack = getModItemStackOrNull(mod1, id1, meta1);
		if (itemStack != null) {
			return itemStack;
		}
		itemStack = getModItemStackOrNull(mod2, id2, meta2);
		if (itemStack != null) {
			return itemStack;
		}
		itemStack = getModItemStackOrNull(mod3, id3, meta3);
		if (itemStack != null) {
			return itemStack;
		}
		return new ItemStack(Blocks.fire);
	}
	
	private static ItemStack getModItemStackOrNull(final String mod, final String id, final int meta) {
		try {
			final ItemStack itemStack = new ItemStack((Item) Item.itemRegistry.getObject(mod + ":" + id));
			if (meta != -1) {
				itemStack.setItemDamage(meta);
			}
			return itemStack;
		} catch (final Exception exception) {
			WarpDrive.logger.info(String.format("Failed to get mod item for %s:%s@%d", mod, id, meta));
			return null;
		}
	}
	
	public static ItemStack getOreDictionaryEntry(final String ore) {
		if (!OreDictionary.doesOreNameExist(ore)) {
			WarpDrive.logger.info("Failed to get ore named " + ore);
			return new ItemStack(Blocks.fire);
		}
		final List<ItemStack> itemStacks = OreDictionary.getOres(ore);
		if (itemStacks.isEmpty()) {
			WarpDrive.logger.info(String.format("Failed to get item from empty ore dictionary '%s'", ore));
			return new ItemStack(Blocks.fire);
		}
		return itemStacks.get(0);
	}
	
	protected static double[] getDoubleList(final Configuration config, final String category, final String key, final String comment, final double[] valuesDefault) {
		double[] valuesRead = config.get(category, key, valuesDefault, comment).getDoubleList();
		if (valuesRead.length != valuesDefault.length) {
			valuesRead = valuesDefault.clone();
		}
		
		return valuesRead;
	}
	
	public static void reload() {
		CelestialObjectManager.clearForReload(false);
		onFMLpreInitialization(stringConfigDirectory);
		onFMLPostInitialization();
		
		@SuppressWarnings("unchecked")
		final List<EntityPlayer> entityPlayers = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		for (final EntityPlayer entityPlayer : entityPlayers) {
			if ( entityPlayer instanceof EntityPlayerMP
			  && !(entityPlayer instanceof FakePlayer) ) {
				final CelestialObject celestialObject = CelestialObjectManager.get(entityPlayer.worldObj,
				                                                                   MathHelper.floor_double(entityPlayer.posX),
				                                                                   MathHelper.floor_double(entityPlayer.posZ));
				PacketHandler.sendClientSync((EntityPlayerMP) entityPlayer, celestialObject);
			}
		}
	}
	
	public static void onFMLpreInitialization(final String stringConfigDirectory) {
		WarpDriveConfig.stringConfigDirectory = stringConfigDirectory;
		
		// create mod folder
		configDirectory = new File(stringConfigDirectory, WarpDrive.MODID);
		//noinspection ResultOfMethodCallIgnored
		configDirectory.mkdir();
		if (!configDirectory.isDirectory()) {
			throw new RuntimeException("Unable to create config directory " + configDirectory);
		}
		
		// unpack default XML files if none are defined
		unpackResourcesToFolder("filler", ".xml", defaultXML_fillers, "config", configDirectory);
		unpackResourcesToFolder("loot", ".xml", defaultXML_loots, "config", configDirectory);
		unpackResourcesToFolder("structures", ".xml", defaultXML_structures, "config", configDirectory);
		unpackResourcesToFolder("celestialObjects", ".xml", defaultXML_celestialObjects, "config", configDirectory);
		
		// always unpack the XML Schema
		unpackResourceToFolder("WarpDrive.xsd", "config", configDirectory);
		
		// read configuration files
		loadConfig(new File(configDirectory, "config.yml"));
		loadDictionary(new File(configDirectory, "dictionary.yml"));
		CelestialObjectManager.load(configDirectory);
		
		// read mod dependencies
		isCoFHCoreLoaded = Loader.isModLoaded("CoFHCore");
		isComputerCraftLoaded = Loader.isModLoaded("ComputerCraft");
		isEnderIOLoaded = Loader.isModLoaded("EnderIO");
		
		isDefenseTechLoaded = Loader.isModLoaded("DefenseTech");
		isICBMLoaded = Loader.isModLoaded("icbm");
		
		isGregTech5Loaded = false;
		isGregTech6Loaded = false;
		if (Loader.isModLoaded("gregtech")) {
			final String gregTechVersion = FMLCommonHandler.instance().findContainerFor("gregtech").getVersion();
			isGregTech5Loaded = gregTechVersion.equalsIgnoreCase("MC1710") || gregTechVersion.startsWith("5.");
			isGregTech6Loaded = gregTechVersion.startsWith("GT6-MC1710");
			if ( (isGregTech5Loaded && isGregTech6Loaded)
			     || (!isGregTech5Loaded && !isGregTech6Loaded) ) {
				throw new RuntimeException(String.format("Unsupported gregtech version '%s', please report to mod author", gregTechVersion));
			}
		}
		
		isIndustrialCraft2Loaded = Loader.isModLoaded("IC2");
		isOpenComputersLoaded = Loader.isModLoaded("OpenComputers");
	}
	
	public static void loadConfig(final File file) {
		final Configuration config = new Configuration(file);
		config.load();
		
		// General
		G_SPACE_BIOME_ID = Commons.clamp(Integer.MIN_VALUE, Integer.MAX_VALUE,
				config.get("general", "space_biome_id", G_SPACE_BIOME_ID, "Space biome ID").getInt());
		G_SPACE_PROVIDER_ID = Commons.clamp(Integer.MIN_VALUE, Integer.MAX_VALUE,
				config.get("general", "space_provider_id", G_SPACE_PROVIDER_ID, "Space dimension provider ID").getInt());
		G_HYPERSPACE_PROVIDER_ID = Commons.clamp(Integer.MIN_VALUE, Integer.MAX_VALUE,
				config.get("general", "hyperspace_provider_id", G_HYPERSPACE_PROVIDER_ID, "Hyperspace dimension provider ID").getInt());
		
		G_ENTITY_SPHERE_GENERATOR_ID = Commons.clamp(Integer.MIN_VALUE, Integer.MAX_VALUE,
				config.get("general", "entity_sphere_generator_id", G_ENTITY_SPHERE_GENERATOR_ID, "Entity sphere generator ID").getInt());
		G_ENTITY_STAR_CORE_ID = Commons.clamp(Integer.MIN_VALUE, Integer.MAX_VALUE,
				config.get("general", "entity_star_core_id", G_ENTITY_STAR_CORE_ID, "Entity star core ID").getInt());
		G_ENTITY_CAMERA_ID = Commons.clamp(Integer.MIN_VALUE, Integer.MAX_VALUE,
				config.get("general", "entity_camera_id", G_ENTITY_CAMERA_ID, "Entity camera ID").getInt());
		G_ENTITY_PARTICLE_BUNCH_ID = Commons.clamp(Integer.MIN_VALUE, Integer.MAX_VALUE,
			config.get("general", "entity_particle_bunch_id", G_ENTITY_PARTICLE_BUNCH_ID, "Entity particle bunch ID").getInt());
		
		G_LUA_SCRIPTS = Commons.clamp(0, 2,
				config.get("general", "lua_scripts", G_LUA_SCRIPTS,
						"LUA scripts to load when connecting machines: 0 = none, 1 = templates in a subfolder, 2 = ready to roll (templates are still provided)").getInt());
		G_SCHEMALOCATION = config.get("general", "schematic_location", G_SCHEMALOCATION, "Folder where to save ship schematics").getString();
		G_BLOCKS_PER_TICK = Commons.clamp(100, 100000,
				config.get("general", "blocks_per_tick", G_BLOCKS_PER_TICK,
						"Number of blocks to move per ticks, too high will cause lag spikes on ship jumping or deployment, too low may break the ship wirings").getInt());
		G_ENABLE_PROTECTION_CHECKS = config.get("general", "enable_protection_checks", G_ENABLE_PROTECTION_CHECKS, "Enable area protection checks from other mods or plugins, disable if you use the event system exclusively").getBoolean(G_ENABLE_PROTECTION_CHECKS);
		
		// Client
		CLIENT_LOCATION_SCALE = Commons.clamp(0.25F, 4.0F, (float) config.get("client", "location_scale", CLIENT_LOCATION_SCALE,
		                                   "Scale for location text font").getDouble() );
		
		CLIENT_LOCATION_FORMAT_TITLE = config.get("client", "location_prefix", CLIENT_LOCATION_FORMAT_TITLE, 
		                                          "Format for location title").getString();
		{
			String stringValue = config.get("client", "location_background_color", String.format("0x%6X", CLIENT_LOCATION_BACKGROUND_COLOR),
			                                      "Hexadecimal color code for location tile and description background (0xAARRGGBB where AA is alpha, RR is Red, GG is Green and BB is Blue component)").getString();
			CLIENT_LOCATION_BACKGROUND_COLOR = (int) (Long.decode(stringValue) & 0xFFFFFFFFL);
			
			stringValue = config.get("client", "location_text_color", String.format("0x%6X", CLIENT_LOCATION_TEXT_COLOR),
			                         "Hexadecimal color code for location tile and description foreground (0xAARRGGBB where AA is alpha, RR is Red, GG is Green and BB is Blue component)").getString();
			CLIENT_LOCATION_TEXT_COLOR = (int) (Long.decode(stringValue) & 0xFFFFFFFFL);
		}
		CLIENT_LOCATION_HAS_SHADOW = config.get("client", "location_has_shadow", CLIENT_LOCATION_HAS_SHADOW,
		                                        "Shadow casting option for current celestial object name").getBoolean(CLIENT_LOCATION_HAS_SHADOW);
		CLIENT_LOCATION_SCREEN_ALIGNMENT = EnumDisplayAlignment.valueOf(config.get("client", "location_screen_alignment", CLIENT_LOCATION_SCREEN_ALIGNMENT.name(),
		                                              "Alignment on screen: TOP_LEFT, TOP_CENTER, TOP_RIGHT, MIDDLE_LEFT, MIDDLE_CENTER, MIDDLE_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER or BOTTOM_RIGHT").getString());
		CLIENT_LOCATION_SCREEN_OFFSET_X = config.get("client", "location_offset_x", CLIENT_LOCATION_SCREEN_OFFSET_X,
		                                             "Horizontal offset on screen, increase to move to the right").getInt();
		CLIENT_LOCATION_SCREEN_OFFSET_Y = config.get("client", "location_offset_y", CLIENT_LOCATION_SCREEN_OFFSET_Y,
		                                             "Vertical offset on screen, increase to move down").getInt();
		CLIENT_LOCATION_TEXT_ALIGNMENT = EnumDisplayAlignment.valueOf(config.get("client", "location_text_alignment", CLIENT_LOCATION_TEXT_ALIGNMENT.name(),
		                                            "Text alignment: TOP_LEFT, TOP_CENTER, TOP_RIGHT, MIDDLE_LEFT, MIDDLE_CENTER, MIDDLE_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER or BOTTOM_RIGHT").getString());
		CLIENT_LOCATION_WIDTH_RATIO = (float) config.get("client", "location_width_ratio", CLIENT_LOCATION_WIDTH_RATIO,
		                                         "Text width as a ratio of full screen width").getDouble();
		CLIENT_LOCATION_WIDTH_MIN = config.get("client", "location_width_min", CLIENT_LOCATION_WIDTH_MIN,
		                                       "Text width as a minimum 'pixel' count").getInt();
		
		final String commentTooltip = "When to show %s in tooltips. Valid values are " + EnumTooltipCondition.formatAllValues() + ".";
		CLIENT_TOOLTIP_REGISTRY_NAME = EnumTooltipCondition.valueOf(config.get("client", "tooltip_registry_name", CLIENT_TOOLTIP_REGISTRY_NAME.name(),
		                                                                       String.format(commentTooltip, "registry name")).getString());
		CLIENT_TOOLTIP_ORE_DICTIONARY_NAME = EnumTooltipCondition.valueOf(config.get("client", "tooltip_ore_dictionary_name", CLIENT_TOOLTIP_ORE_DICTIONARY_NAME.name(),
		                                                                              String.format(commentTooltip, "ore dictionary names")).getString());
		CLIENT_TOOLTIP_ARMOR = EnumTooltipCondition.valueOf(config.get("client", "tooltip_armor_stats", CLIENT_TOOLTIP_ARMOR.name(),
		                                                               String.format(commentTooltip, "armor stats")).getString());
		CLIENT_TOOLTIP_BURN_TIME = EnumTooltipCondition.valueOf(config.get("client", "tooltip_burn_time", CLIENT_TOOLTIP_BURN_TIME.name(),
		                                                                   String.format(commentTooltip, "burn time")).getString());
		CLIENT_TOOLTIP_DURABILITY = EnumTooltipCondition.valueOf(config.get("client", "tooltip_durability", CLIENT_TOOLTIP_DURABILITY.name(),
		                                                                    String.format(commentTooltip, "durability")).getString());
		CLIENT_TOOLTIP_FLAMMABILITY = EnumTooltipCondition.valueOf(config.get("client", "tooltip_flammability", CLIENT_TOOLTIP_FLAMMABILITY.name(),
		                                                                      String.format(commentTooltip, "flammability")).getString());
		CLIENT_TOOLTIP_FLUID = EnumTooltipCondition.valueOf(config.get("client", "tooltip_fluid_stats", CLIENT_TOOLTIP_FLUID.name(),
		                                                               String.format(commentTooltip, "fluid stats")).getString());
		CLIENT_TOOLTIP_HARDNESS = EnumTooltipCondition.valueOf(config.get("client", "tooltip_hardness", CLIENT_TOOLTIP_HARDNESS.name(),
		                                                                  String.format(commentTooltip, "hardness & explosion resistance")).getString());
		CLIENT_TOOLTIP_HARVESTING = EnumTooltipCondition.valueOf(config.get("client", "tooltip_harvesting_stats", CLIENT_TOOLTIP_HARVESTING.name(),
		                                                                    String.format(commentTooltip, "harvesting stats")).getString());
		CLIENT_TOOLTIP_OPACITY = EnumTooltipCondition.valueOf(config.get("client", "tooltip_opacity", CLIENT_TOOLTIP_OPACITY.name(),
		                                                                 String.format(commentTooltip, "opacity")).getString());
		CLIENT_TOOLTIP_REPAIR_WITH = EnumTooltipCondition.valueOf(config.get("client", "tooltip_repair_material", CLIENT_TOOLTIP_REPAIR_WITH.name(),
		                                                                     String.format(commentTooltip, "repair material")).getString());
		
		// Logging
		LOGGING_JUMP = config.get("logging", "enable_jump_logs", LOGGING_JUMP, "Basic jump logs, should always be enabled").getBoolean(true);
		LOGGING_JUMPBLOCKS = config.get("logging", "enable_jumpblocks_logs", LOGGING_JUMPBLOCKS, "Detailed jump logs to help debug the mod, will spam your logs...").getBoolean(false);
		LOGGING_ENERGY = config.get("logging", "enable_energy_logs", LOGGING_ENERGY, "Detailed energy logs to help debug the mod, enable it before reporting a bug").getBoolean(false);
		if (WarpDrive.isDev) {// disabled in production, for obvious reasons :)
			LOGGING_EFFECTS = config.get("logging", "enable_effects_logs", LOGGING_EFFECTS, "Detailed effects logs to help debug the mod, will spam your console!").getBoolean(false);
			LOGGING_CLOAKING = config.get("logging", "enable_cloaking_logs", LOGGING_CLOAKING, "Detailed cloaking logs to help debug the mod, will spam your console!").getBoolean(false);
			LOGGING_VIDEO_CHANNEL = config.get("logging", "enable_videoChannel_logs", LOGGING_VIDEO_CHANNEL, "Detailed video channel logs to help debug the mod, will spam your console!").getBoolean(false);
			LOGGING_TARGETING = config.get("logging", "enable_targeting_logs", LOGGING_TARGETING, "Detailed targeting logs to help debug the mod, will spam your console!").getBoolean(false);
			LOGGING_CLIENT_SYNCHRONIZATION = config.get("logging", "enable_client_synchronization_logs", LOGGING_CLIENT_SYNCHRONIZATION, "Detailed client synchronization logs to help debug the mod.").getBoolean(false);
		} else {
			LOGGING_EFFECTS = false;
			LOGGING_CLOAKING = false;
			LOGGING_VIDEO_CHANNEL = false;
			LOGGING_TARGETING = false;
			LOGGING_CLIENT_SYNCHRONIZATION = false;
		}
		LOGGING_WEAPON = config.get("logging", "enable_weapon_logs", LOGGING_WEAPON, "Detailed weapon logs to help debug the mod, enable it before reporting a bug").getBoolean(false);
		LOGGING_CAMERA = config.get("logging", "enable_camera_logs", LOGGING_CAMERA, "Detailed camera logs to help debug the mod, enable it before reporting a bug").getBoolean(false);
		LOGGING_BUILDING = config.get("logging", "enable_building_logs", LOGGING_BUILDING, "Detailed building logs to help debug the mod, enable it before reporting a bug").getBoolean(false);
		LOGGING_COLLECTION = config.get("logging", "enable_collection_logs", LOGGING_COLLECTION, "Detailed collection logs to help debug the mod, enable it before reporting a bug").getBoolean(false);
		LOGGING_TRANSPORTER = config.get("logging", "enable_transporter_logs", LOGGING_TRANSPORTER, "Detailed transporter logs to help debug the mod, enable it before reporting a bug").getBoolean(false);
		LOGGING_LUA = config.get("logging", "enable_LUA_logs", LOGGING_LUA, "Detailed LUA logs to help debug the mod, enable it before reporting a bug").getBoolean(false);
		LOGGING_RADAR = config.get("logging", "enable_radar_logs", LOGGING_RADAR, "Detailed radar logs to help debug the mod, enable it before reporting a bug").getBoolean(false);
		LOGGING_BREATHING = config.get("logging", "enable_breathing_logs", LOGGING_BREATHING, "Detailed breathing logs to help debug the mod, enable it before reporting a bug").getBoolean(false);
		LOGGING_WORLD_GENERATION = config.get("logging", "enable_world_generation_logs", LOGGING_WORLD_GENERATION, "Detailed world generation logs to help debug the mod, enable it before reporting a bug").getBoolean(false);
		LOGGING_PROFILING_CPU_USAGE = config.get("logging", "enable_profiling_CPU_time", LOGGING_PROFILING_CPU_USAGE, "Profiling logs for CPU time, enable it to check for lag").getBoolean(true);
		LOGGING_PROFILING_MEMORY_ALLOCATION = config.get("logging", "enable_profiling_memory_allocation", LOGGING_PROFILING_MEMORY_ALLOCATION, "Profiling logs for memory allocation, enable it to check for lag").getBoolean(true);
		LOGGING_PROFILING_THREAD_SAFETY = config.get("logging", "enable_profiling_thread_safety", LOGGING_PROFILING_THREAD_SAFETY, "Profiling logs for multi-threading, enable it to check for ConcurrentModificationException").getBoolean(false);
		LOGGING_DICTIONARY = config.get("logging", "enable_dictionary_logs", LOGGING_DICTIONARY, "Dictionary logs, enable it to dump blocks hardness and blast resistance at boot").getBoolean(true);
		LOGGING_STARMAP = config.get("logging", "enable_starmap_logs", LOGGING_STARMAP, "Starmap logs, enable it to dump starmap registry updates").getBoolean(false);
		LOGGING_BREAK_PLACE = config.get("logging", "enable_break_place_logs", LOGGING_BREAK_PLACE, "Detailed break/place event logs to help debug the mod, enable it before reporting a bug").getBoolean(false);
		LOGGING_FORCEFIELD = config.get("logging", "enable_forcefield_logs", LOGGING_FORCEFIELD, "Detailed forcefield logs to help debug the mod, enable it before reporting a bug").getBoolean(false);
		LOGGING_FORCEFIELD_REGISTRY = config.get("logging", "enable_forcefield_registry_logs", LOGGING_FORCEFIELD_REGISTRY, "ForceField registry logs, enable it to dump forcefield registry updates").getBoolean(false);
		LOGGING_ACCELERATOR = config.get("logging", "enable_accelerator_logs", LOGGING_ACCELERATOR, "Detailed accelerator logs to help debug the mod, enable it before reporting a bug").getBoolean(false);
		LOGGING_XML_PREPROCESSOR = config.get("logging", "enable_XML_preprocessor_logs", LOGGING_XML_PREPROCESSOR, "Save XML preprocessor results as output*.xml file, enable it to debug your XML configuration files").getBoolean(false);
		LOGGING_RENDERING = config.get("logging", "enable_rendering_logs", LOGGING_RENDERING, "Detailed rendering logs to help debug the mod.").getBoolean(false);
		LOGGING_CHUNK_HANDLER = config.get("logging", "enable_chunk_handler_logs", LOGGING_CHUNK_HANDLER, "Detailed chunk data logs to help debug the mod.").getBoolean(false);
		LOGGING_CHUNK_LOADING = config.get("logging", "enable_chunk_loading_logs", LOGGING_CHUNK_LOADING, "Chunk loading logs, enable it to report chunk loaders updates").getBoolean(false);
		LOGGING_ENTITY_FX = config.get("logging", "enable_entity_fx_logs", LOGGING_ENTITY_FX, "EntityFX logs, enable it to dump entityFX registry updates").getBoolean(false);
		
		// Starmap registry
		STARMAP_REGISTRY_UPDATE_INTERVAL_SECONDS = Commons.clamp(0, 300,
			config.get("starmap", "registry_update_interval", STARMAP_REGISTRY_UPDATE_INTERVAL_SECONDS, "(measured in seconds)").getInt());
		STARMAP_ALLOW_OVERLAPPING_CELESTIAL_OBJECTS = 
			config.get("starmap", "allow_overlapping_celestial_objects", STARMAP_ALLOW_OVERLAPPING_CELESTIAL_OBJECTS, "Enable to bypass the check at boot. Use at your own risk!").getBoolean();
		
		// Ship movement costs
		SHIP_MOVEMENT_COSTS_FACTORS = new ShipMovementCosts.Factors[EnumShipMovementType.length];
		for (final EnumShipMovementType shipMovementType : EnumShipMovementType.values()) {
			SHIP_MOVEMENT_COSTS_FACTORS[shipMovementType.ordinal()] = new ShipMovementCosts.Factors(
			        shipMovementType.maximumDistanceDefault,
			        shipMovementType.energyRequiredDefault,
			        shipMovementType.warmupDefault,
			        shipMovementType.sicknessDefault,
			        shipMovementType.cooldownDefault);
			if (shipMovementType.hasConfiguration) {
				SHIP_MOVEMENT_COSTS_FACTORS[shipMovementType.ordinal()].load(config, "ship_movement_costs", shipMovementType.getName(), shipMovementType.getDescription());
			}
		}
		
		// Ship
		SHIP_MAX_ENERGY_STORED = Commons.clamp(0, Integer.MAX_VALUE,
				config.get("ship", "max_energy_stored", SHIP_MAX_ENERGY_STORED, "Maximum energy stored").getInt());
		
		SHIP_TELEPORT_ENERGY_PER_ENTITY = Commons.clamp(0, Integer.MAX_VALUE,
				config.get("ship", "teleport_energy_per_entity", SHIP_TELEPORT_ENERGY_PER_ENTITY, "Energy cost per entity").getInt());
		
		SHIP_VOLUME_MAX_ON_PLANET_SURFACE = Commons.clamp(0, 10000000,
				config.get("ship", "volume_max_on_planet_surface", SHIP_VOLUME_MAX_ON_PLANET_SURFACE, "Maximum ship mass (in blocks) to jump on a planet").getInt());
		SHIP_VOLUME_MIN_FOR_HYPERSPACE = Commons.clamp(0, 10000000,
				config.get("ship", "volume_min_for_hyperspace", SHIP_VOLUME_MIN_FOR_HYPERSPACE, "Minimum ship mass (in blocks) to enter or exit hyperspace without a jumpgate").getInt());
		SHIP_VOLUME_UNLIMITED_PLAYERNAMES = config.get("ship", "volume_unlimited_playernames", SHIP_VOLUME_UNLIMITED_PLAYERNAMES,
				"List of player names which have unlimited block counts to their ship").getStringList();
		
		SHIP_MAX_SIDE_SIZE = Commons.clamp(0, 30000000,
				config.get("ship", "max_side_size", SHIP_MAX_SIDE_SIZE, "Maximum ship size on each axis in blocks").getInt());
		SHIP_COLLISION_TOLERANCE_BLOCKS = Commons.clamp(0, 30000000,
				config.get("ship", "collision_tolerance_blocks", SHIP_COLLISION_TOLERANCE_BLOCKS, "Tolerance in block in case of collision before causing damages...").getInt());
		
		SHIP_WARMUP_RANDOM_TICKS = Commons.clamp(10, 200,
				config.get("ship", "warmup_random_ticks", SHIP_WARMUP_RANDOM_TICKS, "Random variation added to warmup (measured in ticks)").getInt());
		
		SHIP_SUMMON_MAX_RANGE = config.get("ship", "summon_max_range", SHIP_SUMMON_MAX_RANGE, "Maximum range from which players can be summoned (measured in blocks), set to -1 for unlimited range").getInt();
		SHIP_SUMMON_ACROSS_DIMENSIONS = config.get("ship", "summon_across_dimensions", false, "Enable summoning players from another dimension").getBoolean(false);
		
		SHIP_CORE_ISOLATION_UPDATE_INTERVAL_SECONDS = Commons.clamp(0, 300,
				config.get("ship", "core_isolation_update_interval", SHIP_CORE_ISOLATION_UPDATE_INTERVAL_SECONDS, "(measured in seconds)").getInt());
		SHIP_VOLUME_SCAN_AGE_TOLERANCE_SECONDS = Commons.clamp(0, 300,
                config.get("ship", "volume_scan_age_tolerance", SHIP_VOLUME_SCAN_AGE_TOLERANCE_SECONDS, "Ship volume won't be refreshed unless it's older than that many seconds").getInt());
		SHIP_CONTROLLER_UPDATE_INTERVAL_SECONDS = Commons.clamp(0, 300,
				config.get("ship", "controller_update_interval", SHIP_CONTROLLER_UPDATE_INTERVAL_SECONDS, "(measured in seconds)").getInt());
		
		// Radar
		RADAR_MAX_ENERGY_STORED = Commons.clamp(0, Integer.MAX_VALUE,
				config.get("radar", "max_energy_stored", RADAR_MAX_ENERGY_STORED, "maximum energy stored").getInt());
		
		RADAR_SCAN_MIN_ENERGY_COST = Commons.clamp(0, Integer.MAX_VALUE,
				config.get("radar", "min_energy_cost", RADAR_SCAN_MIN_ENERGY_COST, "minimum energy cost per scan (0+), independently of radius").getInt());
		RADAR_SCAN_ENERGY_COST_FACTORS = 
				config.get("radar", "factors_energy_cost", RADAR_SCAN_ENERGY_COST_FACTORS, "energy cost factors {a, b, c, d}. You need to provide exactly 4 values.\n"
						+ "The equation used is a + b * radius + c * radius^2 + d * radius^3").getDoubleList();
		if (RADAR_SCAN_ENERGY_COST_FACTORS.length != 4) {
			RADAR_SCAN_ENERGY_COST_FACTORS = new double[4];
			Arrays.fill(RADAR_SCAN_ENERGY_COST_FACTORS, 1.0);
		}
		RADAR_SCAN_MIN_DELAY_SECONDS = Commons.clamp(1, Integer.MAX_VALUE,
				config.get("radar", "scan_min_delay_seconds", RADAR_SCAN_MIN_DELAY_SECONDS, "minimum scan delay per scan (1+), (measured in seconds)").getInt());
		RADAR_SCAN_DELAY_FACTORS_SECONDS = 
				config.get("radar", "scan_delay_factors_seconds", RADAR_SCAN_DELAY_FACTORS_SECONDS, "scan delay factors {a, b, c, d}. You need to provide exactly 4 values.\n"
						+ "The equation used is a + b * radius + c * radius^2 + d * radius^3, (measured in seconds)").getDoubleList();
		if (RADAR_SCAN_DELAY_FACTORS_SECONDS.length != 4) {
			RADAR_SCAN_DELAY_FACTORS_SECONDS = new double[4];
			Arrays.fill(RADAR_SCAN_DELAY_FACTORS_SECONDS, 1.0);
		}
		
		RADAR_MAX_ISOLATION_RANGE = Commons.clamp(2, 8,
				config.get("radar", "max_isolation_range", RADAR_MAX_ISOLATION_RANGE, "radius around core where isolation blocks count (2 to 8), higher is lagger").getInt());
		
		RADAR_MIN_ISOLATION_BLOCKS = Commons.clamp(0, 20,
				config.get("radar", "min_isolation_blocks", RADAR_MIN_ISOLATION_BLOCKS, "number of isolation blocks required to get some isolation (0 to 20)").getInt());
		RADAR_MAX_ISOLATION_BLOCKS = Commons.clamp(5, 94,
				config.get("radar", "max_isolation_blocks", RADAR_MAX_ISOLATION_BLOCKS, "number of isolation blocks required to reach maximum effect (5 to 94)").getInt());
		
		RADAR_MIN_ISOLATION_EFFECT = Commons.clamp(0.01D, 0.95D,
				config.get("radar", "min_isolation_effect", RADAR_MIN_ISOLATION_EFFECT, "isolation effect achieved with min number of isolation blocks (0.01 to 0.95)").getDouble(0.12D));
		RADAR_MAX_ISOLATION_EFFECT = Commons.clamp(0.01D, 1.0D,
				config.get("radar", "max_isolation_effect", RADAR_MAX_ISOLATION_EFFECT, "isolation effect achieved with max number of isolation blocks (0.01 to 1.00)").getDouble(1.00D));
		
		// Ship Scanner
		SS_MAX_DEPLOY_RADIUS_BLOCKS = Commons.clamp(5, 150,
				config.get("ship_scanner", "max_deploy_radius_blocks", SS_MAX_DEPLOY_RADIUS_BLOCKS, "Max distance from ship scanner to ship core, measured in blocks (5-150)").getInt());
		SS_SEARCH_INTERVAL_TICKS = Commons.clamp(5, 150,
			config.get("ship_scanner", "search_interval_ticks", SS_SEARCH_INTERVAL_TICKS, "Max distance from ship scanner to ship core, measured in blocks (5-150)").getInt());
		SS_SCAN_BLOCKS_PER_SECOND = Commons.clamp(1, 50000,
			config.get("ship_scanner", "scan_blocks_per_second", SS_SCAN_BLOCKS_PER_SECOND, "Scanning speed, measured in blocks (1-5000)").getInt());
		SS_DEPLOY_BLOCKS_PER_INTERVAL = Commons.clamp(1, 3000,
			config.get("ship_scanner", "deploy_blocks_per_interval", SS_DEPLOY_BLOCKS_PER_INTERVAL, "Deployment speed, measured in blocks (1-3000)").getInt());
		SS_DEPLOY_INTERVAL_TICKS = Commons.clamp(1, 60,
			config.get("ship_scanner", "deploy_interval_ticks", SS_DEPLOY_INTERVAL_TICKS, "Delay between deployment of 2 sets of blocks, measured in ticks (1-60)").getInt());
		
		// Laser medium
		LASER_MEDIUM_MAX_ENERGY_STORED = Commons.clamp(1, Integer.MAX_VALUE,
				config.get("laser_medium", "max_energy_stored", LASER_MEDIUM_MAX_ENERGY_STORED, "Maximum energy stored").getInt());
		
		// Laser cannon
		LASER_CANNON_MAX_MEDIUMS_COUNT = Commons.clamp(1, 64,
				config.get("laser_cannon", "max_mediums_count", LASER_CANNON_MAX_MEDIUMS_COUNT, "Maximum number of laser mediums per laser").getInt());
		LASER_CANNON_MAX_LASER_ENERGY = Commons.clamp(1, Integer.MAX_VALUE,
				config.get("laser_cannon", "max_laser_energy", LASER_CANNON_MAX_LASER_ENERGY, "Maximum energy in beam after accounting for boosters beams").getInt());
		LASER_CANNON_EMIT_FIRE_DELAY_TICKS = Commons.clamp(1, 100,
				config.get("laser_cannon", "emit_fire_delay_ticks", LASER_CANNON_EMIT_FIRE_DELAY_TICKS, "Delay while booster beams are accepted, before actually shooting").getInt());
		LASER_CANNON_EMIT_SCAN_DELAY_TICKS = Commons.clamp(1, 100,
				config.get("laser_cannon", "emit_scan_delay_ticks", LASER_CANNON_EMIT_SCAN_DELAY_TICKS, "Delay while booster beams are accepted, before actually scanning").getInt());
		
		LASER_CANNON_BOOSTER_BEAM_ENERGY_EFFICIENCY = Commons.clamp(0.01D, 10.0D,
				config.get("laser_cannon", "booster_beam_energy_efficiency", LASER_CANNON_BOOSTER_BEAM_ENERGY_EFFICIENCY, "Energy factor applied from boosting to main laser").getDouble(0.6D));
		LASER_CANNON_ENERGY_ATTENUATION_PER_AIR_BLOCK = Commons.clamp(0.0D, 0.1D,
				config.get("laser_cannon", "energy_attenuation_per_air_block", LASER_CANNON_ENERGY_ATTENUATION_PER_AIR_BLOCK, "Energy attenuation when going through air blocks (on a planet or any gaz in space)").getDouble());
		LASER_CANNON_ENERGY_ATTENUATION_PER_VOID_BLOCK = Commons.clamp(0.0D, 0.1D,
				config.get("laser_cannon", "energy_attenuation_per_air_block", LASER_CANNON_ENERGY_ATTENUATION_PER_VOID_BLOCK, "Energy attenuation when going through void blocks (in space or hyperspace)").getDouble());
		LASER_CANNON_ENERGY_ATTENUATION_PER_BROKEN_BLOCK = Commons.clamp(0.0D, 1.0D,
				config.get("laser_cannon", "energy_attenuation_per_air_block", LASER_CANNON_ENERGY_ATTENUATION_PER_BROKEN_BLOCK, "Energy attenuation when going through a broken block").getDouble());
		LASER_CANNON_RANGE_MAX = Commons.clamp(64, 512,
				config.get("laser_cannon", "range_max", LASER_CANNON_RANGE_MAX, "Maximum distance travelled").getInt());
		
		LASER_CANNON_ENTITY_HIT_SET_ON_FIRE_SECONDS = Commons.clamp(0, 300,
				config.get("laser_cannon", "entity_hit_set_on_fire_seconds", LASER_CANNON_ENTITY_HIT_SET_ON_FIRE_SECONDS, "Duration of fire effect on entity hit (in seconds)").getInt());
		
		LASER_CANNON_ENTITY_HIT_ENERGY = Commons.clamp(0, LASER_CANNON_MAX_LASER_ENERGY,
				config.get("laser_cannon", "entity_hit_energy", LASER_CANNON_ENTITY_HIT_ENERGY, "Base energy consumed from hitting an entity").getInt());
		LASER_CANNON_ENTITY_HIT_BASE_DAMAGE = Commons.clamp(0, LASER_CANNON_MAX_LASER_ENERGY,
				config.get("laser_cannon", "entity_hit_base_damage", LASER_CANNON_ENTITY_HIT_BASE_DAMAGE, "Minimum damage to entity hit (measured in half hearts)").getInt());
		LASER_CANNON_ENTITY_HIT_ENERGY_PER_DAMAGE = Commons.clamp(0, LASER_CANNON_MAX_LASER_ENERGY,
				config.get("laser_cannon", "entity_hit_energy_per_damage", LASER_CANNON_ENTITY_HIT_ENERGY_PER_DAMAGE, "Energy required by additional hit point (won't be consumed)").getInt());
		LASER_CANNON_ENTITY_HIT_MAX_DAMAGE = Commons.clamp(0, Integer.MAX_VALUE,
				config.get("laser_cannon", "entity_hit_max_damage", LASER_CANNON_ENTITY_HIT_MAX_DAMAGE, "Maximum damage to entity hit, set to 0 to disable damage completely").getInt());
		
		LASER_CANNON_ENTITY_HIT_EXPLOSION_ENERGY_THRESHOLD = Commons.clamp(0, Integer.MAX_VALUE,
				config.get("laser_cannon", "entity_hit_energy_threshold_for_explosion", LASER_CANNON_ENTITY_HIT_EXPLOSION_ENERGY_THRESHOLD, "Minimum energy to cause explosion effect").getInt());
		LASER_CANNON_ENTITY_HIT_EXPLOSION_BASE_STRENGTH = (float) Commons.clamp(0.0D, 100.0D,
				config.get("laser_cannon", "entity_hit_explosion_base_strength", LASER_CANNON_ENTITY_HIT_EXPLOSION_BASE_STRENGTH, "Explosion base strength, 4 is Vanilla TNT").getDouble());
		LASER_CANNON_ENTITY_HIT_EXPLOSION_ENERGY_PER_STRENGTH = Commons.clamp(1, Integer.MAX_VALUE,
				config.get("laser_cannon", "entity_hit_explosion_energy_per_strength", LASER_CANNON_ENTITY_HIT_EXPLOSION_ENERGY_PER_STRENGTH, "Energy per added explosion strength").getInt());
		LASER_CANNON_ENTITY_HIT_EXPLOSION_MAX_STRENGTH = (float) Commons.clamp(0.0D, 1000.0D,
				config.get("laser_cannon", "entity_hit_explosion_max_strength", LASER_CANNON_ENTITY_HIT_EXPLOSION_MAX_STRENGTH, "Maximum explosion strength, set to 0 to disable explosion completely").getDouble());
		
		LASER_CANNON_BLOCK_HIT_ENERGY_MIN = Commons.clamp(0, Integer.MAX_VALUE,
				config.get("laser_cannon", "block_hit_energy_min", LASER_CANNON_BLOCK_HIT_ENERGY_MIN, "Minimum energy required for breaking a block").getInt());
		LASER_CANNON_BLOCK_HIT_ENERGY_PER_BLOCK_HARDNESS = Commons.clamp(0, Integer.MAX_VALUE,
				config.get("laser_cannon", "block_hit_energy_per_block_hardness", LASER_CANNON_BLOCK_HIT_ENERGY_PER_BLOCK_HARDNESS, "Energy cost per block hardness for breaking a block").getInt());
		LASER_CANNON_BLOCK_HIT_ENERGY_MAX = Commons.clamp(0, Integer.MAX_VALUE,
				config.get("laser_cannon", "block_hit_energy_max", LASER_CANNON_BLOCK_HIT_ENERGY_MAX, "Maximum energy required for breaking a block").getInt());
		LASER_CANNON_BLOCK_HIT_ABSORPTION_PER_BLOCK_HARDNESS = Commons.clamp(0.0D, 1.0D,
				config.get("laser_cannon", "block_hit_absorption_per_block_hardness", LASER_CANNON_BLOCK_HIT_ABSORPTION_PER_BLOCK_HARDNESS, "Probability of energy absorption (i.e. block not breaking) per block hardness. Set to 1.0 to always break the block.").getDouble());
		LASER_CANNON_BLOCK_HIT_ABSORPTION_MAX = Commons.clamp(0.0D, 1.0D,
				config.get("laser_cannon", "block_hit_absorption_max", LASER_CANNON_BLOCK_HIT_ABSORPTION_MAX, "Maximum probability of energy absorption (i.e. block not breaking)").getDouble());
		
		LASER_CANNON_BLOCK_HIT_EXPLOSION_HARDNESS_THRESHOLD = (float) Commons.clamp(0.0D, 10000.0D,
				config.get("laser_cannon", "block_hit_explosion_hardness_threshold", LASER_CANNON_BLOCK_HIT_EXPLOSION_HARDNESS_THRESHOLD,
						"Minimum block hardness required to cause an explosion").getDouble());
		LASER_CANNON_BLOCK_HIT_EXPLOSION_BASE_STRENGTH = (float) Commons.clamp(0.0D, 1000.0D,
				config.get("laser_cannon", "block_hit_explosion_base_strength", LASER_CANNON_BLOCK_HIT_EXPLOSION_BASE_STRENGTH, "Explosion base strength, 4 is Vanilla TNT").getDouble());
		LASER_CANNON_BLOCK_HIT_EXPLOSION_ENERGY_PER_STRENGTH = Commons.clamp(1, Integer.MAX_VALUE,
				config.get("laser_cannon", "block_hit_explosion_energy_per_strength", LASER_CANNON_BLOCK_HIT_EXPLOSION_ENERGY_PER_STRENGTH, "Energy per added explosion strength").getInt());
		LASER_CANNON_BLOCK_HIT_EXPLOSION_MAX_STRENGTH = (float) Commons.clamp(0.0D, 1000.0D,
				config.get("laser_cannon", "block_hit_explosion_max_strength", LASER_CANNON_BLOCK_HIT_EXPLOSION_MAX_STRENGTH, "Maximum explosion strength, set to 0 to disable explosion completely").getDouble());
		
		// Mining Laser
		MINING_LASER_MAX_MEDIUMS_COUNT = Commons.clamp(1, 10,
				config.get("mining_laser", "max_mediums_count", MINING_LASER_MAX_MEDIUMS_COUNT, "Maximum number of laser mediums").getInt());
		MINING_LASER_RADIUS_NO_LASER_MEDIUM = Commons.clamp(0, 15,
		                                                    config.get("mining_laser", "radius_no_laser_medium", MINING_LASER_RADIUS_NO_LASER_MEDIUM, "Mining radius without any laser medium, measured in blocks").getInt());
		MINING_LASER_RADIUS_PER_LASER_MEDIUM = Commons.clamp(1, 8,
		                                                     config.get("mining_laser", "radius_per_laser_medium", MINING_LASER_RADIUS_PER_LASER_MEDIUM, "Bonus to mining radius per laser medium, measured in blocks").getInt());
		
		MINING_LASER_WARMUP_DELAY_TICKS = Commons.clamp(1, 300,
				config.get("mining_laser", "warmup_delay_ticks", MINING_LASER_WARMUP_DELAY_TICKS, "Warmup duration (buffer on startup when energy source is weak)").getInt());
		MINING_LASER_SCAN_DELAY_TICKS = Commons.clamp(1, 300,
				config.get("mining_laser", "scan_delay_ticks", MINING_LASER_SCAN_DELAY_TICKS, "Scan duration per layer").getInt());
		MINING_LASER_MINE_DELAY_TICKS = Commons.clamp(1, 300,
				config.get("mining_laser", "mine_delay_ticks", MINING_LASER_MINE_DELAY_TICKS, "Mining duration per scanned block").getInt());
		
		MINING_LASER_PLANET_ENERGY_PER_LAYER = Commons.clamp(1, Integer.MAX_VALUE,
				config.get("mining_laser", "planet_energy_per_layer", MINING_LASER_PLANET_ENERGY_PER_LAYER, "Energy cost per layer on a planet").getInt());
		MINING_LASER_PLANET_ENERGY_PER_BLOCK = Commons.clamp(1, Integer.MAX_VALUE,
				config.get("mining_laser", "planet_energy_per_block", MINING_LASER_PLANET_ENERGY_PER_BLOCK, "Energy cost per block in space").getInt());
		MINING_LASER_SPACE_ENERGY_PER_LAYER = Commons.clamp(1, Integer.MAX_VALUE,
				config.get("mining_laser", "space_energy_per_layer", MINING_LASER_SPACE_ENERGY_PER_LAYER, "Energy cost per layer on a planet").getInt());
		MINING_LASER_SPACE_ENERGY_PER_BLOCK = Commons.clamp(1, Integer.MAX_VALUE,
				config.get("mining_laser", "space_energy_per_block", MINING_LASER_SPACE_ENERGY_PER_BLOCK, "Energy cost per block in space").getInt());
		
		MINING_LASER_ORESONLY_ENERGY_FACTOR = Commons.clamp(1.5D, 1000.0D,
				config.get("mining_laser", "oresonly_energy_factor", MINING_LASER_ORESONLY_ENERGY_FACTOR, "Energy cost multiplier per block when mining only ores").getDouble(MINING_LASER_ORESONLY_ENERGY_FACTOR));
		MINING_LASER_SILKTOUCH_ENERGY_FACTOR = Commons.clamp(1.5D, 1000.0D,
				config.get("mining_laser", "silktouch_energy_factor", MINING_LASER_SILKTOUCH_ENERGY_FACTOR, "Energy cost multiplier per block when mining with silktouch").getDouble(MINING_LASER_SILKTOUCH_ENERGY_FACTOR));
		
		if (unused) {
			MINING_LASER_SILKTOUCH_DEUTERIUM_L = Commons.clamp(0.0D, 10.0D,
					config.get("mining_laser", "silktouch_deuterium_l", MINING_LASER_SILKTOUCH_DEUTERIUM_L, "Deuterium cost per block when mining with silktouch (0 to disable)").getDouble(1.0D));
			if (MINING_LASER_SILKTOUCH_DEUTERIUM_L < 0.001D) {
				MINING_LASER_SILKTOUCH_DEUTERIUM_L = 0.0D;
			}
			MINING_LASER_FORTUNE_ENERGY_FACTOR = Commons.clamp(0.01D, 1000.0D,
					config.get("mining_laser", "fortune_energy_factor", MINING_LASER_FORTUNE_ENERGY_FACTOR, "Energy cost multiplier per fortune level").getDouble(2.5D));
		}
		
		// Tree Farm
		TREE_FARM_MAX_MEDIUMS_COUNT = Commons.clamp(1, 10,
				config.get("tree_farm", "max_mediums_count", TREE_FARM_MAX_MEDIUMS_COUNT, "Maximum number of laser mediums").getInt());
		TREE_FARM_MAX_SCAN_RADIUS_NO_LASER_MEDIUM = Commons.clamp(0, 15,
				config.get("tree_farm", "max_scan_radius_no_laser_medium", TREE_FARM_MAX_SCAN_RADIUS_NO_LASER_MEDIUM, "Maximum scan radius without any laser medium, on X and Z axis, measured in blocks").getInt());
		TREE_FARM_MAX_SCAN_RADIUS_PER_LASER_MEDIUM = Commons.clamp(1, 8,
				config.get("tree_farm", "max_scan_radius_per_laser_medium", TREE_FARM_MAX_SCAN_RADIUS_PER_LASER_MEDIUM, "Bonus to maximum scan radius per laser medium, on X and Z axis, measured in blocks").getInt());
		TREE_FARM_totalMaxRadius = TREE_FARM_MAX_SCAN_RADIUS_NO_LASER_MEDIUM + TREE_FARM_MAX_MEDIUMS_COUNT * TREE_FARM_MAX_SCAN_RADIUS_PER_LASER_MEDIUM;
		
		TREE_FARM_MAX_LOG_DISTANCE = Commons.clamp(1, 64,
				config.get("tree_farm", "max_reach_distance", TREE_FARM_MAX_LOG_DISTANCE, "Maximum reach distance of the laser without any laser medium, measured in blocks").getInt());
		TREE_FARM_MAX_LOG_DISTANCE_PER_MEDIUM = Commons.clamp(0, 16,
				config.get("tree_farm", "max_reach_distance_per_laser_medium", TREE_FARM_MAX_LOG_DISTANCE_PER_MEDIUM, "Bonus to maximum reach distance per laser medium, measured in blocks").getInt());
		
		// Cloaking
		CLOAKING_MAX_ENERGY_STORED = Commons.clamp(1, Integer.MAX_VALUE,
				config.get("cloaking", "max_energy_stored", CLOAKING_MAX_ENERGY_STORED, "Maximum energy stored").getInt());
		CLOAKING_COIL_CAPTURE_BLOCKS = Commons.clamp(0, 30,
				config.get("cloaking", "coil_capture_blocks", CLOAKING_COIL_CAPTURE_BLOCKS, "Extra blocks covered after the outer coils").getInt());
		CLOAKING_MAX_FIELD_RADIUS = Commons.clamp(CLOAKING_COIL_CAPTURE_BLOCKS + 3, 128,
				config.get("cloaking", "max_field_radius", CLOAKING_MAX_FIELD_RADIUS, "Maximum distance between cloaking core and any cloaked side").getInt());
		CLOAKING_TIER1_ENERGY_PER_BLOCK = Commons.clamp(0, Integer.MAX_VALUE,
				config.get("cloaking", "tier1_energy_per_block", CLOAKING_TIER1_ENERGY_PER_BLOCK, "Energy cost per non-air block in a Tier1 cloak").getInt());
		CLOAKING_TIER2_ENERGY_PER_BLOCK = Commons.clamp(CLOAKING_TIER1_ENERGY_PER_BLOCK, Integer.MAX_VALUE,
				config.get("cloaking", "tier2_energy_per_block", CLOAKING_TIER2_ENERGY_PER_BLOCK, "Energy cost per non-air block in a Tier2 cloak").getInt());
		CLOAKING_FIELD_REFRESH_INTERVAL_SECONDS = Commons.clamp(1, 30,
				config.get("cloaking", "field_refresh_interval_seconds", CLOAKING_FIELD_REFRESH_INTERVAL_SECONDS, "Update speed of cloak simulation").getInt());
		
		// Air generator
		BREATHING_MAX_ENERGY_STORED = config.get("breathing", "max_energy_stored", BREATHING_MAX_ENERGY_STORED, "Maximum energy stored").getIntList();
		assert(BREATHING_MAX_ENERGY_STORED.length == 3);
		BREATHING_MAX_ENERGY_STORED[0] = Commons.clamp(1                        , BREATHING_MAX_ENERGY_STORED[1], BREATHING_MAX_ENERGY_STORED[0]);
		BREATHING_MAX_ENERGY_STORED[1] = Commons.clamp(BREATHING_MAX_ENERGY_STORED[0], BREATHING_MAX_ENERGY_STORED[2], BREATHING_MAX_ENERGY_STORED[1]);
		BREATHING_MAX_ENERGY_STORED[2] = Commons.clamp(BREATHING_MAX_ENERGY_STORED[1], Integer.MAX_VALUE             , BREATHING_MAX_ENERGY_STORED[2]);
		
		BREATHING_ENERGY_PER_CANISTER = Commons.clamp(1, BREATHING_MAX_ENERGY_STORED[0],
		                                              config.get("breathing", "energy_per_canister", BREATHING_ENERGY_PER_CANISTER, "Energy cost per air canister refilled").getInt());
		
		BREATHING_ENERGY_PER_NEW_AIR_BLOCK = config.get("breathing", "energy_per_new_air_block", BREATHING_ENERGY_PER_NEW_AIR_BLOCK, "Energy cost to start air distribution per open side per interval").getIntList();
		assert(BREATHING_ENERGY_PER_NEW_AIR_BLOCK.length == 3);
		BREATHING_ENERGY_PER_NEW_AIR_BLOCK[0] = Commons.clamp(1                               , BREATHING_MAX_ENERGY_STORED[0], BREATHING_ENERGY_PER_NEW_AIR_BLOCK[0]);
		BREATHING_ENERGY_PER_NEW_AIR_BLOCK[1] = Commons.clamp(BREATHING_ENERGY_PER_NEW_AIR_BLOCK[0], BREATHING_MAX_ENERGY_STORED[1], BREATHING_ENERGY_PER_NEW_AIR_BLOCK[1]);
		BREATHING_ENERGY_PER_NEW_AIR_BLOCK[2] = Commons.clamp(BREATHING_ENERGY_PER_NEW_AIR_BLOCK[1], BREATHING_MAX_ENERGY_STORED[2], BREATHING_ENERGY_PER_NEW_AIR_BLOCK[2]);
		
		BREATHING_ENERGY_PER_EXISTING_AIR_BLOCK = config.get("breathing", "energy_per_existing_air_block", BREATHING_ENERGY_PER_EXISTING_AIR_BLOCK, "Energy cost to sustain air distribution per open side per interval").getIntList();
		assert(BREATHING_ENERGY_PER_EXISTING_AIR_BLOCK.length == 3);
		BREATHING_ENERGY_PER_EXISTING_AIR_BLOCK[0] = Commons.clamp(1                                    , BREATHING_MAX_ENERGY_STORED[0], BREATHING_ENERGY_PER_EXISTING_AIR_BLOCK[0]);
		BREATHING_ENERGY_PER_EXISTING_AIR_BLOCK[1] = Commons.clamp(BREATHING_ENERGY_PER_EXISTING_AIR_BLOCK[0], BREATHING_MAX_ENERGY_STORED[1], BREATHING_ENERGY_PER_EXISTING_AIR_BLOCK[1]);
		BREATHING_ENERGY_PER_EXISTING_AIR_BLOCK[2] = Commons.clamp(BREATHING_ENERGY_PER_EXISTING_AIR_BLOCK[1], BREATHING_MAX_ENERGY_STORED[2], BREATHING_ENERGY_PER_EXISTING_AIR_BLOCK[2]);
		
		BREATHING_AIR_GENERATION_TICKS = Commons.clamp(1, 300,
		                                               config.get("breathing", "air_generation_interval_ticks", BREATHING_AIR_GENERATION_TICKS, "Update speed of air generation").getInt());
		
		BREATHING_AIR_GENERATION_RANGE_BLOCKS = config.get("breathing", "air_generation_range_blocks", BREATHING_AIR_GENERATION_RANGE_BLOCKS, "Maximum range of an air generator for each tier, measured in block").getIntList();
		assert(BREATHING_AIR_GENERATION_RANGE_BLOCKS.length == 3);
		BREATHING_AIR_GENERATION_RANGE_BLOCKS[0] = Commons.clamp(8                                  , BREATHING_AIR_GENERATION_RANGE_BLOCKS[1], BREATHING_AIR_GENERATION_RANGE_BLOCKS[0]);
		BREATHING_AIR_GENERATION_RANGE_BLOCKS[1] = Commons.clamp(BREATHING_AIR_GENERATION_RANGE_BLOCKS[0], BREATHING_AIR_GENERATION_RANGE_BLOCKS[2], BREATHING_AIR_GENERATION_RANGE_BLOCKS[1]);
		BREATHING_AIR_GENERATION_RANGE_BLOCKS[2] = Commons.clamp(BREATHING_AIR_GENERATION_RANGE_BLOCKS[1], 256                                , BREATHING_AIR_GENERATION_RANGE_BLOCKS[2]);
		
		BREATHING_VOLUME_UPDATE_DEPTH_BLOCKS = Commons.clamp(10, 256,
		        config.get("breathing", "volume_update_depth_blocks", BREATHING_VOLUME_UPDATE_DEPTH_BLOCKS, "Maximum depth of blocks to update when a volume has changed.\nHigher values may cause TPS lag spikes, Lower values will exponentially increase the repressurization time").getInt());
		BREATHING_AIR_SIMULATION_DELAY_TICKS = Commons.clamp(1, 90,
				config.get("breathing", "simulation_delay_ticks", BREATHING_AIR_SIMULATION_DELAY_TICKS, "Minimum delay between consecutive air propagation updates of the same block.").getInt());
		BREATHING_AIR_AT_ENTITY_DEBUG = config.get("breathing", "enable_air_at_entity_debug", BREATHING_AIR_AT_ENTITY_DEBUG, "Spam creative players with air status around them, use at your own risk.").getBoolean(false);
		
		// IC2 Reactor cooler
		IC2_REACTOR_MAX_HEAT_STORED = Commons.clamp(1, 32767,
		        config.get("ic2_reactor_laser", "max_heat_stored", IC2_REACTOR_MAX_HEAT_STORED, "Maximum heat stored in the focus").getInt());
		IC2_REACTOR_COMPONENT_HEAT_TRANSFER_PER_TICK = Commons.clamp(0, 32767,
		        config.get("ic2_reactor_laser", "component_heat_transfer_per_tick", IC2_REACTOR_COMPONENT_HEAT_TRANSFER_PER_TICK, "Maximum component heat added to the focus every reactor tick").getInt());
		IC2_REACTOR_FOCUS_HEAT_TRANSFER_PER_TICK = Commons.clamp(0, 32767,
		        config.get("ic2_reactor_laser", "focus_heat_transfer_per_tick", IC2_REACTOR_FOCUS_HEAT_TRANSFER_PER_TICK, "Maximum heat transferred between 2 connected focus every reactor tick").getInt());
		IC2_REACTOR_REACTOR_HEAT_TRANSFER_PER_TICK = Commons.clamp(0, 32767,
		        config.get("ic2_reactor_laser", "reactor_heat_transfer_per_tick", IC2_REACTOR_REACTOR_HEAT_TRANSFER_PER_TICK, "Maximum reactor heat added to the focus every reactor tick").getInt());
		IC2_REACTOR_COOLING_PER_INTERVAL = Commons.clamp(1, 32767,
		        config.get("ic2_reactor_laser", "cooling_per_interval", IC2_REACTOR_COOLING_PER_INTERVAL, "Heat extracted from the focus by interval").getInt());
		IC2_REACTOR_ENERGY_PER_HEAT = Commons.clamp(2.0D, 100000.0D,
				config.get("ic2_reactor_laser", "energy_per_heat", IC2_REACTOR_ENERGY_PER_HEAT, "Energy cost per heat absorbed").getDouble());
		IC2_REACTOR_COOLING_INTERVAL_TICKS = Commons.clamp(0, 1200,
				config.get("ic2_reactor_laser", "cooling_interval_ticks", IC2_REACTOR_COOLING_INTERVAL_TICKS, "Update speed of the check for reactors to cooldown. Use 10 to tick as fast as the reactor simulation").getInt());
		
		// Transporter
		TRANSPORTER_MAX_ENERGY_STORED = Commons.clamp(1, Integer.MAX_VALUE,
				config.get("transporter", "max_energy_stored", TRANSPORTER_MAX_ENERGY_STORED, "Maximum energy stored").getInt());
//		TRANSPORTER_ENERGY_PER_BLOCK = Commons.clamp(1.0D, TRANSPORTER_MAX_ENERGY_STORED / 10.0D,
//				config.get("transporter", "energy_per_block", TRANSPORTER_ENERGY_PER_BLOCK, "Energy cost per block distance").getDouble(100.0D));
//		TRANSPORTER_MAX_BOOST_MUL = Commons.clamp(1.0D, 1000.0D,
//				config.get("transporter", "max_boost", TRANSPORTER_MAX_BOOST_MUL, "Maximum energy boost allowed").getDouble(4.0));
		
		// Enantiomorphic reactor
		ENAN_REACTOR_MAX_ENERGY_STORED = Commons.clamp(1, Integer.MAX_VALUE,
				config.get("enantiomorphic_reactor", "max_energy_stored", ENAN_REACTOR_MAX_ENERGY_STORED, "Maximum energy stored").getInt());
		ENAN_REACTOR_UPDATE_INTERVAL_TICKS = Commons.clamp(1, 300,
				config.get("enantiomorphic_reactor", "update_interval_ticks", ENAN_REACTOR_UPDATE_INTERVAL_TICKS, "Update speed of the reactor simulation").getInt());
		ENAN_REACTOR_MAX_LASERS_PER_SECOND = Commons.clamp(4, 80,
				config.get("enantiomorphic_reactor", "max_lasers", ENAN_REACTOR_MAX_LASERS_PER_SECOND, "Maximum number of stabilisation laser shots per seconds before loosing efficiency").getInt());
		
		// Energy bank
		ENERGY_BANK_MAX_ENERGY_STORED = config.get("energy_bank", "max_energy_stored", ENERGY_BANK_MAX_ENERGY_STORED, "Maximum energy stored for each energy bank").getIntList();
		assert(ENERGY_BANK_MAX_ENERGY_STORED.length == 3);
		ENERGY_BANK_MAX_ENERGY_STORED[0] = Commons.clamp(                               0, ENERGY_BANK_MAX_ENERGY_STORED[1], ENERGY_BANK_MAX_ENERGY_STORED[0]);
		ENERGY_BANK_MAX_ENERGY_STORED[1] = Commons.clamp(ENERGY_BANK_MAX_ENERGY_STORED[0], ENERGY_BANK_MAX_ENERGY_STORED[2], ENERGY_BANK_MAX_ENERGY_STORED[1]);
		ENERGY_BANK_MAX_ENERGY_STORED[2] = Commons.clamp(ENERGY_BANK_MAX_ENERGY_STORED[1], Integer.MAX_VALUE               , ENERGY_BANK_MAX_ENERGY_STORED[2]);
		
		ENERGY_BANK_IC2_TIER = config.get("energy_bank", "ic2_tier", ENERGY_BANK_IC2_TIER, "IC2 energy tier for each energy bank (0 is BatBox, etc.)").getIntList();
		assert(ENERGY_BANK_IC2_TIER.length == 3);
		ENERGY_BANK_IC2_TIER[0] = Commons.clamp(                      0, ENERGY_BANK_IC2_TIER[1], ENERGY_BANK_IC2_TIER[0]);
		ENERGY_BANK_IC2_TIER[1] = Commons.clamp(ENERGY_BANK_IC2_TIER[0], ENERGY_BANK_IC2_TIER[2], ENERGY_BANK_IC2_TIER[1]);
		ENERGY_BANK_IC2_TIER[2] = Commons.clamp(ENERGY_BANK_IC2_TIER[1], Integer.MAX_VALUE      , ENERGY_BANK_IC2_TIER[2]);
		
		ENERGY_BANK_TRANSFER_PER_TICK = config.get("energy_bank", "transfer_per_tick", ENERGY_BANK_TRANSFER_PER_TICK, "Internal energy transferred per tick for each energy bank").getIntList();
		assert(ENERGY_BANK_TRANSFER_PER_TICK.length == 3);
		ENERGY_BANK_TRANSFER_PER_TICK[0] = Commons.clamp(                               0, ENERGY_BANK_TRANSFER_PER_TICK[1], ENERGY_BANK_TRANSFER_PER_TICK[0]);
		ENERGY_BANK_TRANSFER_PER_TICK[1] = Commons.clamp(ENERGY_BANK_TRANSFER_PER_TICK[0], ENERGY_BANK_TRANSFER_PER_TICK[2], ENERGY_BANK_TRANSFER_PER_TICK[1]);
		ENERGY_BANK_TRANSFER_PER_TICK[2] = Commons.clamp(ENERGY_BANK_TRANSFER_PER_TICK[1], Integer.MAX_VALUE               , ENERGY_BANK_TRANSFER_PER_TICK[2]);
		
		ENERGY_BANK_EFFICIENCY_PER_UPGRADE = config.get("energy_bank", "efficiency_per_upgrade", ENERGY_BANK_EFFICIENCY_PER_UPGRADE, "Energy transfer efficiency for each upgrade apply, first value is without upgrades (0.8 means 20% loss)").getDoubleList();
		assert(ENERGY_BANK_EFFICIENCY_PER_UPGRADE.length >= 1);
		ENERGY_BANK_EFFICIENCY_PER_UPGRADE[0] = Math.min(1.0D, Commons.clamp(                                 0.5D, ENERGY_BANK_EFFICIENCY_PER_UPGRADE[1], ENERGY_BANK_EFFICIENCY_PER_UPGRADE[0]));
		ENERGY_BANK_EFFICIENCY_PER_UPGRADE[1] = Math.min(1.0D, Commons.clamp(ENERGY_BANK_EFFICIENCY_PER_UPGRADE[0], ENERGY_BANK_EFFICIENCY_PER_UPGRADE[2], ENERGY_BANK_EFFICIENCY_PER_UPGRADE[1]));
		ENERGY_BANK_EFFICIENCY_PER_UPGRADE[2] = Math.min(1.0D, Commons.clamp(ENERGY_BANK_EFFICIENCY_PER_UPGRADE[1], Integer.MAX_VALUE                    , ENERGY_BANK_EFFICIENCY_PER_UPGRADE[2]));
		
		// Lift
		LIFT_MAX_ENERGY_STORED = Commons.clamp(1, Integer.MAX_VALUE,
				config.get("lift", "max_energy_stored", LIFT_MAX_ENERGY_STORED, "Maximum energy stored").getInt());
		LIFT_ENERGY_PER_ENTITY = Commons.clamp(1, Integer.MAX_VALUE,
				config.get("lift", "energy_per_entity", LIFT_ENERGY_PER_ENTITY, "Energy consumed per entity moved").getInt());
		LIFT_UPDATE_INTERVAL_TICKS = Commons.clamp(1, 60,
				config.get("lift", "update_interval_ticks", LIFT_UPDATE_INTERVAL_TICKS, "Update speed of the check for entities").getInt());
		LIFT_ENTITY_COOLDOWN_TICKS = Commons.clamp(1, 6000,
				config.get("lift", "entity_cooldown_ticks", LIFT_ENTITY_COOLDOWN_TICKS, "Cooldown after moving an entity").getInt());
		
		
		// Chunk loader
		CHUNK_LOADER_MAX_ENERGY_STORED = Commons.clamp(1, Integer.MAX_VALUE,
				config.get("chunk_loader", "max_energy_stored", CHUNK_LOADER_MAX_ENERGY_STORED, "Maximum energy stored").getInt());
		CHUNK_LOADER_MAX_RADIUS = Commons.clamp(1, Integer.MAX_VALUE,
				config.get("chunk_loader", "max_radius", CHUNK_LOADER_MAX_RADIUS, "Maximum radius when loading a square shape, measured in chunks. A linear shape can be up to 1 chunk wide by (radius + 1 + radius) ^ 2 chunks long.").getInt());
		CHUNK_LOADER_ENERGY_PER_CHUNK = Commons.clamp(1, 100,
				config.get("chunk_loader", "energy_per_chunk", CHUNK_LOADER_ENERGY_PER_CHUNK, "Energy consumed per chunk loaded").getInt());
		
		
		// Particles accelerator
		ACCELERATOR_ENABLE = config.get("accelerator", "enable", ACCELERATOR_ENABLE, "Enable accelerator blocks. Requires a compatible server, as it won't work in single player").getBoolean(false);

		// Allow fly to space
		
		ALLOW_PLAYER_SPACE_TRAVEL = config.get("general", "enable_player_space_travel", ALLOW_PLAYER_SPACE_TRAVEL, "Allow players to travel to space without a ship by flying above a given Y level (default 500)").getBoolean(false);
		PLAYER_SPACE_TRAVEL_Y_LEVEL = config.get("general", "player_space_travel_y_level", PLAYER_SPACE_TRAVEL_Y_LEVEL, "If enabled, the height a player must reach to go to space").getInt(500);

		
		ACCELERATOR_MAX_PARTICLE_BUNCHES = Commons.clamp(2, 100,
				config.get("accelerator", "max_particle_bunches", ACCELERATOR_MAX_PARTICLE_BUNCHES, "Maximum number of particle bunches per accelerator controller").getInt());
		
		config.save();
	}
	
	public static void loadDictionary(final File file) {
		final Configuration config = new Configuration(file);
		config.load();
		
		// Dictionary
		Dictionary.loadConfig(config);
	
		config.save();
	}
	
	public static void registerBlockTransformer(final String modId, final IBlockTransformer blockTransformer) {
		blockTransformers.put(modId, blockTransformer);
		WarpDrive.logger.info(modId + " blockTransformer registered");
	}
	
	public static void onFMLInitialization() {
		CompatWarpDrive.register();
		
		// read non-explicit mod dependencies
		isAdvancedRepulsionSystemLoaded = Loader.isModLoaded("AdvancedRepulsionSystems");
		isAdvancedSolarPanelLoaded = Loader.isModLoaded("AdvancedSolarPanel");
		isForgeMultipartLoaded = Loader.isModLoaded("ForgeMultipart");
		isICBMClassicLoaded = Loader.isModLoaded("icbmclassic");
		isNotEnoughItemsLoaded = Loader.isModLoaded("NotEnoughItems");
		isImmersiveEngineeringLoaded = Loader.isModLoaded("ImmersiveEngineering");
		isThermalExpansionLoaded = Loader.isModLoaded("ThermalExpansion");
		
		// apply compatibility modules
		if (isAdvancedRepulsionSystemLoaded) {
			CompatAdvancedRepulsionSystems.register();
		}
		
		final boolean isAppliedEnergistics2Loaded = Loader.isModLoaded("appliedenergistics2");
		if (isAppliedEnergistics2Loaded) {
			CompatAppliedEnergistics2.register();
		}
		
		final boolean isArsMagica2Loaded = Loader.isModLoaded("arsmagica2");
		if (isArsMagica2Loaded) {
			CompatArsMagica2.register();
		}
		
		if (isComputerCraftLoaded) {
			loadCC();
			CompatComputerCraft.register();
		}
		
		if (isEnderIOLoaded) {
			CompatEnderIO.register();
		}
		
		if (isForgeMultipartLoaded) {
			isForgeMultipartLoaded = CompatForgeMultipart.register();
		}
		
		if (isImmersiveEngineeringLoaded) {
			CompatImmersiveEngineering.register();
		}
		
		if (isIndustrialCraft2Loaded) {
			loadIC2();
			CompatIndustrialCraft2.register();
		}
		
		if (isOpenComputersLoaded) {
			CompatOpenComputers.register();
		}
		
		if (isThermalExpansionLoaded) {
			CompatThermalExpansion.register();
		}
		
		final boolean isBotaniaLoaded = Loader.isModLoaded("Botania");
		if (isBotaniaLoaded) {
			CompatBotania.register();
		}
		
		final boolean isBiblioCraftLoaded = Loader.isModLoaded("BiblioCraft");
		if (isBiblioCraftLoaded) {
			CompatBiblioCraft.register();
		}
		
		final boolean isBuildCraftLoaded = Loader.isModLoaded("BuildCraft|Core");
		if (isBuildCraftLoaded) {
			CompatBuildCraft.register();
		}
		
		final boolean isCarpentersBlocksLoaded = Loader.isModLoaded("CarpentersBlocks");
		if (isCarpentersBlocksLoaded) {
			CompatCarpentersBlocks.register();
		}
		
		final boolean isCustomNpcsLoaded = Loader.isModLoaded("customnpcs");
		if (isCustomNpcsLoaded) {
			CompatCustomNpcs.register();
		}
		
		final boolean isDraconicEvolutionLoaded = Loader.isModLoaded("DraconicEvolution");
		if (isDraconicEvolutionLoaded) {
			CompatDraconicEvolution.register();
		}
		
		final boolean isEvilCraftLoaded = Loader.isModLoaded("evilcraft");
		if (isEvilCraftLoaded) {
			CompatEvilCraft.register();
		}
		
		final boolean isJABBAloaded = Loader.isModLoaded("JABBA");
		if (isJABBAloaded) {
			CompatJABBA.register();
		}
		
		final boolean isMekanismLoaded = Loader.isModLoaded("Mekanism");
		if (isMekanismLoaded) {
			CompatMekanism.register();
		}
		
		final boolean isMetallurgyLoaded = Loader.isModLoaded("Metallurgy");
		if (isMetallurgyLoaded) {
			CompatMetallurgy.register();
		}
		
		final boolean isNaturaLoaded = Loader.isModLoaded("Natura");
		if (isNaturaLoaded) {
			CompatNatura.register();
		}
		
		final boolean isPneumaticCraftLoaded = Loader.isModLoaded("PneumaticCraft");
		if (isPneumaticCraftLoaded) {
			CompatPneumaticCraft.register();
		}
		
		final boolean isParziStarWarsLoaded = Loader.isModLoaded("starwarsmod");
		if (isParziStarWarsLoaded) {
			CompatParziStarWars.register();
		}
		
		final boolean isRedstonePasteLoaded = Loader.isModLoaded("RedstonePasteMod");
		if (isRedstonePasteLoaded) {
			CompatRedstonePaste.register();
		}
		
		final boolean isSGCraftLoaded = Loader.isModLoaded("SGCraft");
		if (isSGCraftLoaded) {
			CompatSGCraft.register();
		}
		
		final boolean isStargateTech2Loaded = Loader.isModLoaded("StargateTech2");
		if (isStargateTech2Loaded) {
			CompatStargateTech2.register();
		}
		
		final boolean isTConstructLoaded = Loader.isModLoaded("TConstruct");
		if (isTConstructLoaded) {
			CompatTConstruct.register();
		}
		
		final boolean isTechgunsLoaded = Loader.isModLoaded("Techguns");
		if (isTechgunsLoaded) {
			CompatTechguns.register();
		}
		
		final boolean isThaumcraftLoaded = Loader.isModLoaded("Thaumcraft");
		if (isThaumcraftLoaded) {
			CompatThaumcraft.register();
		}
		
		final boolean isThermalDynamicsLoaded = Loader.isModLoaded("ThermalDynamics");
		if (isThermalDynamicsLoaded) {
			CompatThermalDynamics.register();
		}
	}
	
	public static void onFMLPostInitialization() {
		// load XML files
		FillerManager.load(configDirectory);
		LootManager.load(configDirectory);
		StructureManager.load(configDirectory);
		
		Dictionary.apply();
	}
	
	private static void loadIC2() {
		try {
			IC2_emptyCell = getModItemStack("IC2", "itemCellEmpty", -1);
			IC2_compressedAir = getModItemStack("IC2", "itemCellEmpty", 5);
			
			IC2_rubberWood = getModBlock("IC2", "blockRubWood");
			IC2_Resin = getModItemStack("IC2", "itemHarz", -1);
		} catch (final Exception exception) {
			WarpDrive.logger.error("Error loading IndustrialCraft2 classes");
			exception.printStackTrace();
		}
	}
	
	private static void loadCC() {
		try {
			CC_Computer = getModBlock("ComputerCraft", "CC-Computer");
			CC_peripheral = getModBlock("ComputerCraft", "CC-Peripheral");
			CCT_Turtle = getModBlock("ComputerCraft", "CC-Turtle");
			CCT_Expanded = getModBlock("ComputerCraft", "CC-TurtleExpanded");
			CCT_Advanced = getModBlock("ComputerCraft", "CC-TurtleAdvanced");
		} catch (final Exception exception) {
			WarpDrive.logger.error("Error loading ComputerCraft classes");
			exception.printStackTrace();
		}
	}
	
	public static DocumentBuilder getXmlDocumentBuilder() {
		if (xmlDocumentBuilder == null) {
			
			final ErrorHandler xmlErrorHandler = new ErrorHandler() {
				@Override
				public void warning(final SAXParseException exception) {
					WarpDrive.logger.warn(String.format("XML warning at line %d: %s",
					                                    exception.getLineNumber(),
					                                    exception.getLocalizedMessage() ));
					// exception.printStackTrace();
				}
				
				@Override
				public void fatalError(final SAXParseException exception) {
					WarpDrive.logger.warn(String.format("XML fatal error at line %d: %s",
					                      exception.getLineNumber(),
					                      exception.getLocalizedMessage() ));
					// exception.printStackTrace();
				}
				
				@Override
				public void error(final SAXParseException exception) {
					WarpDrive.logger.warn(String.format("XML error at line %d: %s",
					                                    exception.getLineNumber(),
					                                    exception.getLocalizedMessage() ));
					
					// exception.printStackTrace();
				}
			};
			
			final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setIgnoringComments(false);
			documentBuilderFactory.setNamespaceAware(true);
			documentBuilderFactory.setValidating(true);
			documentBuilderFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
			
			try {
				xmlDocumentBuilder = documentBuilderFactory.newDocumentBuilder();
			} catch (final ParserConfigurationException exception) {
				exception.printStackTrace();
			}
			xmlDocumentBuilder.setErrorHandler(xmlErrorHandler);
		}
		
		return xmlDocumentBuilder;
	}
	
	/**
	 * Check if a category of configuration files are missing, unpack default ones from the mod's resources to the specified target folder
	 * Target folder should be already created
	 **/
	private static void unpackResourcesToFolder(final String prefix, final String suffix, final String[] filenames, final String resourcePathSource, final File folderTarget) {
		final File[] files = configDirectory.listFiles((file_notUsed, name) -> name.startsWith(prefix) && name.endsWith(suffix));
		if (files == null) {
			throw new RuntimeException(String.format("Critical error accessing configuration directory, searching for %s*%s files: %s", prefix, suffix, configDirectory));
		}
		if (files.length == 0) {
			for (final String filename : filenames) {
				unpackResourceToFolder(filename, resourcePathSource, folderTarget);
			}
		}
	}
	
	/**
	 * Copy a default configuration file from the mod's resources to the specified configuration folder
	 * Target folder should be already created
	 **/
	private static void unpackResourceToFolder(final String filename, final String resourcePathSource, final File folderTarget) {
		final String resourceName = resourcePathSource + "/" + filename;
		
		final File destination = new File(folderTarget, filename);
		
		try {
			final InputStream inputStream = WarpDrive.class.getClassLoader().getResourceAsStream(resourceName);
			final BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destination));
			
			final byte[] byteBuffer = new byte[Math.max(8192, inputStream.available())];
			int bytesRead;
			while ((bytesRead = inputStream.read(byteBuffer)) >= 0) {
				outputStream.write(byteBuffer, 0, bytesRead);
			}
			
			inputStream.close();
			outputStream.close();
		} catch (final Exception exception) {
			WarpDrive.logger.error("Failed to unpack resource \'" + resourceName + "\' into " + destination);
			exception.printStackTrace();
		}
	}
}
