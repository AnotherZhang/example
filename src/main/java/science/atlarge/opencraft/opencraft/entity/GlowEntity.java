package science.atlarge.opencraft.opencraft.entity;

import com.destroystokyo.paper.event.player.PlayerInitialSpawnEvent;
import com.flowpowered.network.Message;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Chunk;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityPortalExitEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.EntityUnleashEvent.UnleashReason;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataStore;
import org.bukkit.metadata.MetadataStoreBase;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import science.atlarge.opencraft.opencraft.EventFactory;
import science.atlarge.opencraft.opencraft.GlowServer;
import science.atlarge.opencraft.opencraft.GlowWorld;
import science.atlarge.opencraft.opencraft.block.GlowBlock;
import science.atlarge.opencraft.opencraft.chunk.GlowChunk;
import science.atlarge.opencraft.opencraft.entity.meta.MetadataIndex;
import science.atlarge.opencraft.opencraft.entity.meta.MetadataMap;
import science.atlarge.opencraft.opencraft.entity.objects.GlowItemFrame;
import science.atlarge.opencraft.opencraft.entity.objects.GlowLeashHitch;
import science.atlarge.opencraft.opencraft.entity.objects.GlowPainting;
import science.atlarge.opencraft.opencraft.entity.physics.BlockBoundingBoxes;
import science.atlarge.opencraft.opencraft.entity.physics.BoundingBox;
import science.atlarge.opencraft.opencraft.entity.physics.EntityBoundingBox;
import science.atlarge.opencraft.opencraft.net.message.play.entity.EntityMetadataMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.EntityRotationMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.EntityStatusMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.EntityTeleportMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.EntityVelocityMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.RelativeEntityPositionMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.RelativeEntityPositionRotationMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.SetPassengerMessage;
import science.atlarge.opencraft.opencraft.net.message.play.player.InteractEntityMessage;
import science.atlarge.opencraft.opencraft.util.Coordinates;
import science.atlarge.opencraft.opencraft.util.Position;
import science.atlarge.opencraft.opencraft.util.UuidUtils;
import science.atlarge.opencraft.opencraft.util.Vectors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents some entity in the world such as an item on the floor or a player.
 *
 * @author Graham Edgecombe
 */
public abstract class GlowEntity implements Entity {

    /**
     * An ID to use in network messages when the protocol calls for a 32-bit entity ID, but the
     * relevant Object doesn't exist, isn't an Entity, or is in a different World (and thus a
     * different space for 32-bit entity IDs).
     */
    public static final int ENTITY_ID_NOBODY = -1;

    /**
     * The offset to be used during collision.
     */
    public static final double COLLISION_OFFSET = 0.00001;

    /**
     * The metadata store for entities.
     */
    private static final MetadataStore<Entity> bukkitMetadata = new EntityMetadataStore();
    private static final Vector zeroG = new Vector();

    /**
     * A list containing all the block faces adjacent to the player.
     */
    private static final List<BlockFace> FACES = Arrays.asList(BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH,
            BlockFace.NORTH, BlockFace.DOWN, BlockFace.SELF, BlockFace.NORTH_EAST,
            BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST);

    /**
     * The server this entity belongs to.
     */
    @Getter
    protected final GlowServer server;

    /**
     * The entity's metadata.
     */
    protected final MetadataMap metadata = new MetadataMap(getClass());

    /**
     * The current position.
     */
    protected final Location location;

    /**
     * The position in the last cycle.
     */
    protected final Location previousLocation;

    /**
     * The entity's velocity, applied each tick.
     */
    protected final Vector velocity = new Vector();

    /**
     * A list of entities currently riding this entity.
     */
    private final List<Entity> passengers = new ArrayList<>();

    /**
     * The original location of this entity.
     */
    @Getter
    private final Location origin;

    /**
     * All entities that currently have this entity as leash holder.
     */
    @Getter
    private final List<GlowEntity> leashedEntities = Lists.newArrayList();

    /**
     * List of custom String data for the entity.
     */
    @Getter
    private final List<String> customTags = Lists.newArrayList();

    /**
     * The world this entity belongs to. Guarded by {@link #worldLock}.
     */
    @Getter
    protected GlowWorld world;

    /**
     * Lock to prevent concurrent modifications affected by switching worlds.
     */
    protected final ReadWriteLock worldLock = new ReentrantReadWriteLock();

    /**
     * A flag indicating if this entity is currently active.
     */
    protected boolean active = true;

    /**
     * This entity's current identifier for its world.
     */
    @Getter
    protected int entityId;

    /**
     * Whether the entity should have its position resent as if teleported.
     */
    @Getter
    protected boolean teleported;

    /**
     * Whether the entity should have its velocity resent.
     */
    protected boolean velocityChanged;

    /**
     * A counter of how long this entity has existed.
     */
    @Getter
    @Setter
    protected int ticksLived;

    /**
     * The entity this entity is currently riding.
     */
    @Getter
    protected GlowEntity vehicle;

    /**
     * The entity's bounding box, or null if it has no physical presence.
     */
    protected EntityBoundingBox boundingBox;
    protected boolean passengerChanged;

    /**
     * Whether this entity was forcibly removed from the world.
     */
    @Getter
    protected boolean removed;

    /**
     * Velocity reduction applied each tick in air.
     * For example, if the multiplier is 0.98,
     * the entity will lose 2% of its velocity each physics tick.
     * The default value 0.98 indicates little airdrag.
     */
    @Setter
    protected double airDragMultiplier = 0.98;

    /**
     * Velocity reduction applied each tick in water.
     * For example, if the multiplier is 0.98,
     * the entity will lose 2% of its velocity each physics tick.
     * The default value 0.8 indicates 20% water drag.
     */
    @Setter
    protected double liquidDrag = 0.8;

    /**
     * Gravity acceleration applied each tick.
     * The default value (0,-0.098,0) approximates Minecraft's gravity well.
     */
    @Setter
    protected Vector gravityAccel = new Vector(0, -0.098, 0);

    /**
     * The slipperiness multiplier applied according to the block this entity was on.
     */
    protected double slipMultiplier = 0.6;

    /**
     * This entity's unique id.
     */
    private UUID uuid;

    /**
     * An EntityDamageEvent representing the last damage cause on this entity.
     */
    @Getter
    @Setter
    private EntityDamageEvent lastDamageCause;

    /**
     * A flag indicting if the entity is on the ground.
     */
    @Getter
    private boolean onGround = false;

    /**
     * The distance the entity is currently falling without touching the ground.
     */
    @Getter
    private float fallDistance;

    /**
     * How long the entity has been on fire, or 0 if it is not.
     */
    @Getter
    @Setter
    private int fireTicks;

    private int portalTicks;

    /**
     * Whether gravity applies to the entity.
     */
    @Setter
    private boolean gravity = true;

    /**
     * Whether friction applies to the entity.
     */
    @Setter
    private boolean friction = true;

    /**
     * Whether this entity is invulnerable.
     */
    @Getter
    @Setter
    private boolean invulnerable;

    /**
     * The leash holders uuid of the entity. Will be null after the entities first tick.
     */
    private UUID leashHolderUniqueId;

    /**
     * Has the leash holder of the entity changed.
     */
    private boolean leashHolderChanged;

    ////////////////////////////////////////////////////////////////////////////
    // Command sender

    /**
     * The leash holder of the entity.
     */
    private GlowEntity leashHolder;

    /**
     * The Nether portal cooldown for the entity.
     */
    @Getter
    @Setter
    private int portalCooldown;

    /**
     * Whether this entity has operator permissions.
     */
    @Getter
    @Setter
    private boolean op;
    private Spigot spigot = new Spigot() {
        @Override
        public boolean isInvulnerable() {
            return GlowEntity.this.isInvulnerable();
        }
    };

    /**
     * Creates an entity and adds it to the specified world.
     *
     * @param location The location of the entity.
     */
    public GlowEntity(Location location) {
        // Set initial locations for events.
        this.origin = location.clone();
        this.previousLocation = location.clone();
        this.location = location.clone();

        portalTicks = 0;

        // this is so dirty I washed my hands after writing it.
        if (this instanceof GlowPlayer) {
            // initial spawn event, first
            location = EventFactory.getInstance()
                    .callEvent(new PlayerInitialSpawnEvent((Player) this, location))
                    .getSpawnLocation();

            // then, spawn location event (with location updated from initial spawn event)
            location = EventFactory.getInstance()
                    .callEvent(new PlayerSpawnLocationEvent((Player) this, location))
                    .getSpawnLocation();

            // Update from modifications done on events.
            Position.copyLocation(location, this.origin);
            Position.copyLocation(location, this.previousLocation);
            Position.copyLocation(location, this.location);
        }
        world = (GlowWorld) location.getWorld();
        server = world.getServer();

        server.getEntityIdManager().allocate(this);
        world.getEntityManager().register(this);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Core properties

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public void sendMessage(String s) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void sendMessage(String[] strings) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String getName() {
        return getType().getName();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Location stuff

    @Override
    public UUID getUniqueId() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
        return uuid;
    }

    /**
     * Sets this entity's unique identifier if possible.
     *
     * @param uuid The new UUID. Must not be null.
     * @throws IllegalArgumentException if the passed UUID is null.
     * @throws IllegalStateException    if a UUID has already been set.
     */
    public void setUniqueId(UUID uuid) {
        checkNotNull(uuid, "uuid must not be null");
        if (this.uuid == null) {
            this.uuid = uuid;
        } else if (!this.uuid.equals(uuid)) {
            // silently allow setting the same UUID, since
            // it can't be checked with getUniqueId()
            throw new IllegalStateException("UUID of " + this + " is already "
                    + UuidUtils.toString(this.uuid));
        }
    }

    @Override
    public boolean isDead() {
        return !active;
    }

    @Override
    public boolean isValid() {
        return world.getEntityManager().getEntity(entityId) == this;
    }

    @Override
    public Location getLocation() {
        return location.clone();
    }

    @Override
    public Location getLocation(Location loc) {
        return Position.copyLocation(location, loc);
    }

    public Location getPreviousLocation() {
        return previousLocation.clone();
    }

    @Override
    public Chunk getChunk() {
        return location.getChunk();
    }

    /**
     * Get the direction (SOUTH, WEST, NORTH, or EAST) this entity is facing.
     *
     * @return The cardinal BlockFace of this entity.
     */
    public BlockFace getCardinalFacing() {
        double rot = getLocation().getYaw() % 360;
        if (rot < 0) {
            rot += 360.0;
        }
        if (0 <= rot && rot < 45) {
            return BlockFace.SOUTH;
        } else if (45 <= rot && rot < 135) {
            return BlockFace.WEST;
        } else if (135 <= rot && rot < 225) {
            return BlockFace.NORTH;
        } else if (225 <= rot && rot < 315) {
            return BlockFace.EAST;
        } else if (315 <= rot && rot < 360.0) {
            return BlockFace.SOUTH;
        } else {
            return BlockFace.EAST;
        }
    }

    /**
     * Gets the full direction (including SOUTH_SOUTH_EAST etc) this entity is facing.
     *
     * @return The intercardinal BlockFace of this entity
     */
    public BlockFace getFacing() {
        long facing = Math.round(getLocation().getYaw() / 22.5) + 8;
        return Position.getDirection((byte) (facing % 16));
    }

    @Override
    public Vector getVelocity() {
        return velocity.clone();
    }

    @Override
    public void setVelocity(Vector velocity) {
        this.velocity.copy(velocity);
        velocityChanged = true;
    }

    /**
     * Returns the change in velocity per physics tick due to gravity.
     *
     * @return the change in velocity per physics tick due to gravity
     */
    public Vector getGravityAccel() {
        if (this.gravity) {
            return this.gravityAccel;
        } else {
            return GlowEntity.zeroG;
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Internals
    @Override
    public boolean teleport(Location location) {
        if (!(this instanceof GlowPlayer)) {
            // TODO: Properly test when Enderman teleportation is implemented.
            EntityTeleportEvent event = EventFactory.getInstance().callEvent(
                    new EntityTeleportEvent(this, getLocation(), location));
            if (event.isCancelled()) {
                return false;
            }
            location = event.getTo();
        }
        checkNotNull(location, "location cannot be null");
        checkNotNull(location.getWorld(), "location's world cannot be null");
        worldLock.writeLock().lock();
        try {
            if (location.getWorld() != world) {
                world.getEntityManager().unregister(this);
                world = (GlowWorld) location.getWorld();
                world.getEntityManager().register(this);
            }
        } finally {
            worldLock.writeLock().unlock();
        }
        setRawLocation(location, false);
        teleported = true;
        return true;
    }

    @Override
    public boolean teleport(Entity destination) {
        return teleport(destination.getLocation());
    }

    @Override
    public boolean teleport(Location location, TeleportCause cause) {
        return teleport(location);
    }

    @Override
    public boolean teleport(Entity destination, TeleportCause cause) {
        return teleport(destination.getLocation(), cause);
    }

    /**
     * Checks if this entity is within the visible radius of another.
     *
     * @param other The other entity.
     * @return {@code true} if the entities can see each other, {@code false} if not.
     */
    public boolean isWithinDistance(GlowEntity other) {
        if (other instanceof GlowLivingEntity) {
            return ((GlowLivingEntity) other).getDeathTicks() <= 20
                    && isWithinDistance(other.location);
        } else {
            return !other.isDead() && (isWithinDistance(other.location)
                    || other instanceof GlowLightningStrike);
        }
    }

    /**
     * Checks if this entity is within the visible radius of a location.
     *
     * @param loc The location.
     * @return {@code true} if the entities can see each other, {@code false} if not.
     */
    public boolean isWithinDistance(Location loc) {
        double dx = Math.abs(location.getX() - loc.getX());
        double dz = Math.abs(location.getZ() - loc.getZ());
        return loc.getWorld() == getWorld() && dx <= server.getViewDistance() * GlowChunk.WIDTH
                && dz <= server.getViewDistance() * GlowChunk.HEIGHT;
    }

    /**
     * Checks whether this entity should be saved as part of the world.
     *
     * @return True if the entity should be saved.
     */
    public boolean shouldSave() {
        return true;
    }

    /**
     * Called every game cycle. Subclasses should implement this to implement periodic functionality
     * e.g. mob AI.
     */
    public void pulse() {
        ticksLived++;
        if (!getLocation().getChunk().isLoaded()) {
            return;
        }

        if (fireTicks > 0) {
            --fireTicks;
        }
        metadata.setBit(MetadataIndex.STATUS, MetadataIndex.StatusFlags.ON_FIRE, fireTicks > 0);

        // resend position if it's been a while, causes ItemFrames to disappear and GlowPaintings
        // to dislocate.
        if (ticksLived % (30 * 20) == 0) {
            if (!(this instanceof GlowItemFrame || this instanceof GlowPainting)) {
                teleported = true;
            }
        }

        if (this instanceof GlowLivingEntity && !isDead() && ((GlowLivingEntity) this).hasAI()
                && this.getLocation().getChunk().isLoaded()) {
            GlowLivingEntity entity = (GlowLivingEntity) this;
            entity.getTaskManager().pulse();
        }

        followLead();

        pulsePhysics();

        Block currentBlock = location.getBlock();
        if (currentBlock.getType() == Material.ENDER_PORTAL || currentBlock.getType() == Material.PORTAL) {

            if (this instanceof GlowPlayer) {
                GlowPlayer player = (GlowPlayer) this;
                if (player.getGameMode() == GameMode.CREATIVE) {
                    portalTicks = 80;
                }
            }

            portalTicks++;
            if (portalTicks >= 80) {
                portalTicks = 0;
                interactWithPortal(currentBlock);
            }

        } else {
            portalTicks = 0;
        }

        if (leashHolderUniqueId != null && ticksLived < 2) {
            Optional<GlowEntity> any = world.getEntityManager().getAll().stream()
                    .filter(e -> leashHolderUniqueId.equals(e.getUniqueId())).findAny();
            if (!any.isPresent()) {
                world.dropItemNaturally(location, new ItemStack(Material.LEASH));
            }
            setLeashHolder(any.orElse(null));
            leashHolderUniqueId = null;
        }
    }

    /**
     * Fire when a entity interacts with a portal. Checks if the entity is allowed to teleport to another world and
     * handles transporting of entity.
     *
     * @param portal The block the entity interacts with.
     */
    private void interactWithPortal(Block portal) {

        Location previousLocation = location.clone();
        EntityPortalEnterEvent portalEnterEvent = new EntityPortalEnterEvent(this, portal.getLocation());

        boolean success = false;

        if (portal.getType() == Material.ENDER_PORTAL && server.getAllowEnd()) {
            EventFactory.getInstance().callEvent(portalEnterEvent);
            if (getWorld().getEnvironment() == Environment.THE_END) {
                success = teleportToSpawn();
            } else {
                success = teleportToEnd();
            }
        } else if (portal.getType() == Material.PORTAL && server.getAllowNether()) {
            EventFactory.getInstance().callEvent(portalEnterEvent);
            if (getWorld().getEnvironment() == Environment.NETHER) {
                success = teleportToSpawn();
            } else {
                success = teleportToNether();
            }
        } else {
            // not a portal or not allowed to teleport.
        }

        if (success) {
            EntityPortalExitEvent portalExitEvent = new EntityPortalExitEvent(
                    this,
                    previousLocation,
                    location.clone(),
                    velocity.clone(),
                    new Vector()
            );
            portalExitEvent = EventFactory.getInstance().callEvent(portalExitEvent);
            if (!portalExitEvent.getAfter().equals(velocity)) {
                setVelocity(portalExitEvent.getAfter());
            }

        }
    }

    private void followLead() {
        if (!isLeashed()) {
            return;
        }

        double distanceSquared = location.distanceSquared(leashHolder.getLocation());

        // No need to move when already close enough
        if (distanceSquared < 2 * 2) {
            return;
        }

        // TODO: Physics are not right
        // For example, gravity is not respected

        // Leashes break when the distance between leashholder and leashedentity is greater than
        // 10 blocks
        if (distanceSquared > 10 * 10) {
            // break leashitch, if the entity is the only one left attached
            // will also destroy all remaining leashes
            if (EntityType.LEASH_HITCH.equals(leashHolder.getType())
                    && leashHolder.leashedEntities.size() == 1) {
                leashHolder.remove();
            } else {
                // break leash
                unleash(this, UnleashReason.DISTANCE);
            }
            return;
        }

        if (!leashHolder.hasMoved()) {
            this.setVelocity(new Vector(0, 0, 0));
            return;
        }

        // Don't move when already close enough
        double speed = distanceSquared / (10.0 * 10.0 * 2);

        Vector direction = leashHolder.getLocation().toVector().subtract(location.toVector());
        direction.normalize();

        direction.multiply(speed);
        this.setVelocity(direction);
    }

    /**
     * Resets the previous location and other properties to their current value.
     */
    public void reset() {
        Position.copyLocation(location, previousLocation);
        metadata.resetChanges();
        teleported = false;
        velocityChanged = false;
        leashHolderChanged = false;
    }

    /**
     * Sets this entity's location.
     *
     * @param location The new location.
     * @param fall     Whether to calculate fall damage or not.
     */
    public void setRawLocation(Location location, boolean fall) {
        if (location.getWorld() != world) {
            throw new IllegalArgumentException(
                    "Cannot setRawLocation to a different world (got " + location.getWorld()
                            + ", expected " + world + ")");
        }

        if (Objects.equals(location, previousLocation)) {
            return;
        }

        if (teleported) {
            teleported = false;
        }

        world.getEntityManager().move(this, location);
        Position.copyLocation(this.location, this.previousLocation);
        Position.copyLocation(location, this.location);

        updateBoundingBox();

        Material type = location.getBlock().getType();

        if (hasMoved()) {
            if (!fall || type == Material.LADDER // todo: horses are not affected
                    || type == Material.VINE // todo: horses are not affected
                    || type == Material.WATER || type == Material.STATIONARY_WATER
                    || type == Material.WEB || type == Material.TRAP_DOOR
                    || type == Material.IRON_TRAPDOOR || onGround) {
                setFallDistance(0);
            } else if (location.getY() < previousLocation.getY() && !isInsideVehicle()) {
                setFallDistance((float) (fallDistance + previousLocation.getY() - location.getY()));
            }
        }

        // set entity to be on ground if its bounding box intercepts a solid block in -Y direction
        if (fall && hasDefaultLandingBehavior()) {
            double detectOffsetY = 0;
            if (boundingBox != null) {
                detectOffsetY = boundingBox.getSize().getY();
            }

            Location detectLocation = location.clone().add(0, -detectOffsetY, 0);
            setOnGround(detectLocation.getBlock().getType().isSolid());
        }
    }

    /**
     * Sets this entity's location and applies fall damage calculations.
     *
     * @param location The new location.
     */
    public void setRawLocation(Location location) {
        setRawLocation(location, true);
    }

    /**
     * If true, {@link #setRawLocation(Location, boolean)} with {@code fall} true will call
     * {@link #setOnGround(boolean)} according to whether or not our location is inside a solid
     * block.
     *
     * @return true to call {@link #setOnGround(boolean)} from {@link #setRawLocation(Location,
     * boolean)}; false otherwise
     */
    protected boolean hasDefaultLandingBehavior() {
        return true;
    }

    /**
     * Creates a list of {@link Message}s which can be sent to a client to spawn this entity.
     * Implementations in concrete subclasses may return a shallowly immutable list.
     *
     * @return A list of messages which can spawn this entity.
     */
    public abstract List<Message> createSpawnMessage();

    /**
     * Creates a {@link Message} which can be sent to a client to update this entity.
     *
     * @return A message which can update this entity.
     */
    public List<Message> createUpdateMessage() {

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        double dx = x * 32 - previousLocation.getX() * 32;
        double dy = y * 32 - previousLocation.getY() * 32;
        double dz = z * 32 - previousLocation.getZ() * 32;

        dx *= 128;
        dy *= 128;
        dz *= 128;

        boolean teleport = dx > Short.MAX_VALUE
                || dy > Short.MAX_VALUE
                || dz > Short.MAX_VALUE
                || dx < Short.MIN_VALUE
                || dy < Short.MIN_VALUE
                || dz < Short.MIN_VALUE;

        List<Message> result = new LinkedList<>();

        boolean moved = hasMoved();
        boolean rotated = hasRotated();

        if (teleported || moved && teleport) {
            result.add(new EntityTeleportMessage(entityId, location));
        } else if (rotated) {
            int yaw = Position.getIntYaw(location);
            int pitch = Position.getIntPitch(location);
            if (moved) {
                result.add(new RelativeEntityPositionRotationMessage(
                        entityId,
                        (short) dx,
                        (short) dy,
                        (short) dz,
                        yaw,
                        pitch
                ));
            } else {
                result.add(new EntityRotationMessage(entityId, yaw, pitch));
            }
        } else if (moved) {
            result.add(new RelativeEntityPositionMessage(entityId, (short) dx, (short) dy, (short) dz));
        }

        // send changed metadata
        List<MetadataMap.Entry> changes = metadata.getChanges();
        if (!changes.isEmpty()) {
            result.add(new EntityMetadataMessage(entityId, changes));
        }

        // send velocity if needed
        if (velocityChanged) {
            result.add(new EntityVelocityMessage(entityId, velocity));
        }

        if (passengerChanged) {

            // A player can be a passenger of any arbitrary entity, e.g. a boat
            // In case the current session belongs to this player passenger
            // We need to send the self_id

            int[] passengerIds = getPassengers().stream()
                    .mapToInt(Entity::getEntityId)
                    .toArray();
            result.add(new SetPassengerMessage(getEntityId(), passengerIds));
            passengerChanged = false;
        }

        return result;
    }

    /**
     * Checks if this entity has moved this cycle.
     *
     * @return {@code true} if so, {@code false} if not.
     */
    public boolean hasMoved() {
        return Position.hasMoved(location, previousLocation);
    }

    /**
     * Checks if this entity has rotated this cycle.
     *
     * @return {@code true} if so, {@code false} if not.
     */
    public boolean hasRotated() {
        return Position.hasRotated(location, previousLocation);
    }

    /**
     * Teleport this entity to the spawn point of the main world. This is used to teleport out of
     * the End.
     *
     * @return {@code true} if the teleport was successful.
     */
    protected boolean teleportToSpawn() {
        Location target = server.getWorlds().get(0).getSpawnLocation();

        EntityPortalEvent event = EventFactory.getInstance()
                .callEvent(new EntityPortalEvent(this, location.clone(), target, null));
        if (event.isCancelled()) {
            return false;
        }
        target = event.getTo();

        teleport(target);
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Physics stuff

    /**
     * Teleport this entity to the End. If no End world is loaded this does nothing.
     *
     * @return {@code true} if the teleport was successful.
     */
    protected boolean teleportToEnd() {
        if (!server.getAllowEnd()) {
            return false;
        }
        Location target = null;
        for (World world : server.getWorlds()) {
            if (world.getEnvironment() == Environment.THE_END) {
                target = world.getSpawnLocation();
                break;
            }
        }
        if (target == null) {
            return false;
        }

        EntityPortalEvent event = EventFactory.getInstance()
                .callEvent(new EntityPortalEvent(this, location.clone(), target, null));
        if (event.isCancelled()) {
            return false;
        }
        target = event.getTo();

        teleport(target);
        return true;
    }

    /**
     * Teleport this entity to the Nether. If no Nether world is loaded this does nothing.
     *
     * @return {@code true} if the teleport was successful.
     */
    protected boolean teleportToNether() {

        if (!server.getAllowNether()) {
            return false;
        }

        Location target = null;
        for (World world : server.getWorlds()) {
            if (world.getEnvironment() == Environment.NETHER) {
                target = world.getSpawnLocation();
                break;
            }
        }

        if (target == null) {
            return false;
        }

        EntityPortalEvent event = new EntityPortalEvent(this, location.clone(), target, null);
        event = EventFactory.getInstance().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        target = event.getTo();
        teleport(target);

        return true;
    }

    protected void setSize(float xz, float y) {
        setBoundingBox(xz, y);
    }

    /**
     * Determine if this entity is intersecting a block of the specified type.
     *
     * <p>If the entity has a defined bounding box, that is used to check for intersection.
     * Otherwise, a less accurate calculation using only the entity's location and its surrounding
     * blocks are used.
     *
     * @param material The material to check for.
     * @return True if the entity is intersecting
     */
    public boolean isTouchingMaterial(Material material) {
        if (boundingBox == null) {
            // less accurate calculation if no bounding box is present
            for (BlockFace face : FACES) {
                if (getLocation().getBlock().getRelative(face).getType() == material) {
                    return true;
                }
            }
        } else {
            // bounding box-based calculation
            Vector min = Vectors.floor(boundingBox.minCorner);
            Vector max = Vectors.ceil(boundingBox.maxCorner);
            for (int x = min.getBlockX(); x <= max.getBlockX(); ++x) {
                for (int y = min.getBlockY(); y <= max.getBlockY(); ++y) {
                    for (int z = min.getBlockZ(); z <= max.getBlockZ(); ++z) {
                        if (world.getBlockTypeIdAt(x, y, z) == material.getId()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    protected final void setBoundingBox(double xz, double y) {
        boundingBox = new EntityBoundingBox(xz, y);
        updateBoundingBox();
    }

    @Override
    public double getWidth() {
        return boundingBox.getSize().getX();
    }

    @Override
    public double getHeight() {
        return boundingBox.getSize().getY();
    }

    public boolean intersects(BoundingBox box) {
        return boundingBox != null && boundingBox.intersects(box);
    }

    /**
     * Get all block bounding boxes that are within range of the given BoundingBox.
     *
     * @param broadPhaseBox The BoundingBox to be used as block range
     * @return All boundingboxes from blocks that intersect with the give BoundingBox
     */
    List<BoundingBox> getIntersectingBlockBoundingBoxes(BoundingBox broadPhaseBox) {

        if (broadPhaseBox == null) {
            return Collections.emptyList();
        }

        Vector min = broadPhaseBox.minCorner;
        min.setY(min.getY() - 1);
        Vector max = broadPhaseBox.maxCorner;

        List<GlowBlock> glowBlocks = world.getOverlappingBlocks(min, max);

        List<BoundingBox> intersectingBoxes = glowBlocks.stream()
                .map(BlockBoundingBoxes::getBoundingBoxes)
                .flatMap(List::stream)
                .filter(box -> box.intersects(broadPhaseBox))
                .collect(Collectors.toList());

        return intersectingBoxes;
    }

    /**
     * Computes the velocity that is will be applied to the user this tick.
     */
    protected void computeVelocity() {
        if (hasFriction() && location != null) {

            Material material = location.getBlock().getType();

            // apply friction and gravity
            if (material == Material.WATER || material == Material.STATIONARY_WATER) {
                velocity.multiply(liquidDrag);
                velocity.setY(velocity.getY() + getGravityAccel().getY() / 4.0);
            } else if (material == Material.LAVA || material == Material.STATIONARY_LAVA) {
                velocity.multiply(liquidDrag - 0.3);
                velocity.setY(velocity.getY() + getGravityAccel().getY() / 4.0);
            } else {
                velocity.setY(airDragMultiplier * velocity.getY() + getGravityAccel().getY());

                double drag = airDragMultiplier;
                if (isOnGround()) {
                    drag = slipMultiplier;
                }

                velocity.setX(velocity.getX() * drag);
                velocity.setZ(velocity.getZ() * drag);
            }
        }
    }

    /**
     * Tests entity collision with the blocks around the entity and generates the response displacement.
     */
    protected Location resolveCollisions() {

        Location pendingLocation = location.clone();
        // The fraction of this tick's time that has been processed. 0.0 indicating none and 1.0 indicating completion.
        double elapsedTime = 0.0;

        // Break if we won't be moving.
        while (velocity.length() > 0.0 && elapsedTime < 1.0) {

            if (this.boundingBox == null) {
                break;
            }

            // Compute the remaining time and velocity during this cycle.
            double remainingTime = 1.0d - elapsedTime;
            Vector remainingDisplacement = velocity.clone().multiply(remainingTime);

            // Create a bounding box at the correct location and find the ones it interacts with.
            EntityBoundingBox pendingBox = this.boundingBox.createCopyAt(pendingLocation);
            BoundingBox broadPhaseBox = pendingBox.getBroadPhase(velocity);
            List<BoundingBox> intersectingBoxes = getIntersectingBlockBoundingBoxes(broadPhaseBox);

            // Break if there is nothing to collide with.
            if (intersectingBoxes.isEmpty()) {
                break;
            }

            // Find the closest one.
            Pair<Double, Vector> closest = intersectingBoxes.stream()
                    .map(box -> pendingBox.sweptAxisAlignedBoundingBox(remainingDisplacement, box))
                    .min(Comparator.comparingDouble(Pair::getLeft))
                    .get();

            // Move up to the collided with box.
            double collisionTime = closest.getLeft();
            Vector normal = closest.getRight();
            Vector displacement = remainingDisplacement.clone().multiply(collisionTime);
            Vector collisionOffsetVector = normal.clone().multiply(COLLISION_OFFSET);
            pendingLocation.add(displacement);
            pendingLocation.add(collisionOffsetVector);

            elapsedTime += collisionTime * remainingTime;

            // Break if we didn't actually collide.
            if (collisionTime >= 1.0) {
                break;
            }

            // Cancel out velocity in the direction of the collided with box.
            velocity.subtract(Vectors.project(velocity, normal));

            // Handle projectile interaction
            if (this instanceof Projectile) {

                Projectile projectile = (Projectile) this;
                Block block = pendingLocation.getBlock();

                ProjectileHitEvent event = new ProjectileHitEvent(projectile, block);
                EventFactory.getInstance().callEvent(event);

                collide(block);

                // TODO: Break here? it might possible that a projectile removes itself after colliding which
                //  could introduce bugs here
            }
        }

        // Perform the last step (only step when no collisions occur)
        Vector displacement = velocity.clone().multiply(1.0 - elapsedTime);
        pendingLocation.add(displacement);

        return pendingLocation;
    }

    /**
     * Calculate all the physics that impact the entity.
     */
    protected void pulsePhysics() {
        computeVelocity();
        Location finalLocation = resolveCollisions();
        setRawLocation(finalLocation);
        updateBoundingBox();
    }

    /**
     * Collide with the target block.
     *
     * @param block a block whose type {@link Material#isOccluding()}
     */
    public void collide(Block block) {
        // No-op by default.
    }

    protected void updateBoundingBox() {
        // make sure bounding box is up to date
        if (boundingBox != null) {
            boundingBox.setCenter(location.getX(), location.getY(), location.getZ());
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Various properties

    @Override
    public int getMaxFireTicks() {
        return 160;  // this appears to be Minecraft's default value
    }

    @Override
    public void setFallDistance(float distance) {
        fallDistance = Math.max(distance, 0);
    }

    /**
     * Sets the on-ground flag and clears fall distance.
     *
     * @param onGround true if this entity is now on the ground; false otherwise
     */
    public void setOnGround(boolean onGround) {
        if (this.onGround != onGround) {
            setFallDistance(0);
        }
        this.onGround = onGround;
    }

    /**
     * Destroys this entity by removing it from the world and marking it as not being active.
     */
    @Override
    public void remove() {
        removed = true;
        active = false;
        boundingBox = null;
        world.getEntityManager().unregister(this);
        server.getEntityIdManager().deallocate(this);
        this.setPassenger(null);
        leaveVehicle();
        ImmutableList.copyOf(this.leashedEntities)
                .forEach(e -> unleash(e, UnleashReason.HOLDER_GONE));

        if (isLeashed()) {
            unleash(this, UnleashReason.HOLDER_GONE);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Miscellaneous actions

    private void unleash(GlowEntity entity, UnleashReason reason) {
        EventFactory.getInstance().callEvent(new EntityUnleashEvent(entity, reason));
        world.dropItemNaturally(entity.location, new ItemStack(Material.LEASH));
        entity.setLeashHolder(null);
    }

    @Override
    public List<Entity> getNearbyEntities(double x, double y, double z) {
        // This behavior is similar to CraftBukkit, where a call with args
        // (0, 0, 0) finds any entities whose bounding boxes intersect that of
        // this entity.

        BoundingBox searchBox;
        if (boundingBox == null) {
            searchBox = BoundingBox.fromPositionAndSize(location.toVector(), new Vector(0, 0, 0));
        } else {
            searchBox = BoundingBox.copyOf(boundingBox);
        }
        Vector vec = new Vector(x, y, z);
        searchBox.minCorner.subtract(vec);
        searchBox.maxCorner.add(vec);

        return world.getEntityManager().getEntitiesInside(searchBox, this);
    }

    @Override
    public void playEffect(EntityEffect type) {
        if (type.getApplicable().isInstance(this)) {
            EntityStatusMessage message = new EntityStatusMessage(entityId, type);
            world.getRawPlayers().stream().filter(player -> player.canSeeEntity(this))
                    .forEach(player -> player.getSession().send(message));
        }
    }

    void playEffectKnownAndSelf(EntityEffect type) {
        if (type.getApplicable().isInstance(this)) {
            EntityStatusMessage message = new EntityStatusMessage(entityId, type);
            if (this instanceof GlowPlayer) {
                ((GlowPlayer) this).getSession().send(message);
            }
            world.getRawPlayers().stream().filter(player -> player.canSeeEntity(this))
                    .forEach(player -> player.getSession().send(message));
        }
    }

    @Override
    public EntityType getType() {
        return EntityType.UNKNOWN;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Entity stacking

    @Override
    public boolean isInsideVehicle() {
        return getVehicle() != null;
    }

    @Override
    public boolean leaveVehicle() {
        return isInsideVehicle() && vehicle.removePassenger(this);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Custom name

    @Override
    public String getCustomName() {
        String name = metadata.getString(MetadataIndex.NAME_TAG);
        if (name == null || name.isEmpty()) {
            name = "";
        }
        return name;
    }

    @Override
    public void setCustomName(String name) {
        if (name == null) {
            name = "";
        }

        if (name.length() > 64) {
            name = name.substring(0, 64);
        }

        metadata.set(MetadataIndex.NAME_TAG, name); // remove ?
    }

    @Override
    public boolean isCustomNameVisible() {
        return metadata.getBoolean(MetadataIndex.SHOW_NAME_TAG);
    }

    @Override
    public void setCustomNameVisible(boolean flag) {
        metadata.set(MetadataIndex.SHOW_NAME_TAG, flag);
    }

    @Override
    public boolean isGlowing() {
        return metadata.getBit(MetadataIndex.STATUS, MetadataIndex.StatusFlags.GLOWING);
    }

    @Override
    public void setGlowing(boolean glowing) {
        metadata.setBit(MetadataIndex.STATUS, MetadataIndex.StatusFlags.GLOWING, glowing);
    }

    @Override
    public Entity getPassenger() {
        if (passengers.size() > 0) {
            return passengers.get(0);
        }
        return null;
    }

    @Override
    public List<Entity> getPassengers() {
        return Collections.unmodifiableList(passengers);
    }

    @Override
    public boolean removePassenger(Entity passenger) {
        Preconditions.checkArgument(!this.equals(passenger), "Entity cannot ride itself.");

        if (passenger == null || !passengers.contains(passenger)) {
            return false; // nothing changed
        }

        if (EventFactory.getInstance()
                .callEvent(new EntityDismountEvent(passenger, this)).isCancelled()) {
            return false;
        }

        if (!(passenger instanceof GlowEntity)) {
            return false;
        }

        GlowEntity glowPassenger = (GlowEntity) passenger;

        if (this instanceof Vehicle && glowPassenger instanceof LivingEntity) {
            VehicleExitEvent event = EventFactory.getInstance()
                    .callEvent(new VehicleExitEvent((Vehicle) this, (LivingEntity) glowPassenger));
            if (event.isCancelled()) {
                return false;
            }
        }

        passengerChanged = true;
        glowPassenger.vehicle = null;

        glowPassenger.teleport(getDismountLocation());

        return passengers.remove(passenger);
    }

    @Override
    public boolean addPassenger(Entity passenger) {
        Preconditions.checkArgument(!this.equals(passenger), "Entity cannot ride itself.");

        if (passenger == null || passengers.contains(passenger)) {
            return false; // nothing changed
        }

        if (!(passenger instanceof GlowEntity)) {
            return false;
        }

        GlowEntity glowPassenger = (GlowEntity) passenger;

        if (glowPassenger.vehicle != null) {
            glowPassenger.vehicle.removePassenger(passenger);
        }

        if (this instanceof Vehicle) {
            VehicleEnterEvent event = EventFactory.getInstance().callEvent(
                    new VehicleEnterEvent((Vehicle) this, passenger));
            if (event.isCancelled()) {
                return false;
            }
        }
        EntityMountEvent event = EventFactory.getInstance().callEvent(
                new EntityMountEvent(passenger, this));
        if (event.isCancelled()) {
            return false;
        }

        passengerChanged = true;
        glowPassenger.vehicle = this;

        glowPassenger.teleport(getMountLocation());

        return this.passengers.add(passenger);
    }

    @Override
    public boolean setPassenger(Entity newPassenger) {
        Preconditions.checkArgument(!this.equals(newPassenger), "Entity cannot ride itself.");

        boolean result = false;
        for (Entity passenger : Lists.newArrayList(passengers)) {
            if (!Objects.equals(passenger, newPassenger)) {
                result = !removePassenger(passenger);
            }
        }

        if (newPassenger != null && passengers.size() == 0) {
            result = !addPassenger(newPassenger);
        }

        return !result;
    }

    public Location getMountLocation() {
        return this.location.clone().add(0, this.getHeight(), 0);
    }

    public Location getDismountLocation() {
        return this.location;
    }

    @Override
    public boolean isEmpty() {
        return getPassengers().isEmpty();
    }

    @Override
    public boolean eject() {
        return !isEmpty() && setPassenger(null);
    }

    public boolean hasFriction() {
        return friction;
    }

    @Override
    public boolean hasGravity() {
        return gravity;
    }

    @Override
    public Set<String> getScoreboardTags() {
        // todo: 1.11
        return null;
    }

    @Override
    public boolean addScoreboardTag(String tag) {
        // todo: 1.11
        return false;
    }

    @Override
    public boolean removeScoreboardTag(String tag) {
        // todo: 1.11
        return false;
    }

    @Override
    public boolean fromMobSpawner() {
        // TODO: Implementation (1.12.1)
        return false;
    }

    @Override
    public PistonMoveReaction getPistonMoveReaction() {
        // TODO: Implementation (1.12.1)
        return null;
    }

    @Override
    public boolean isSilent() {
        return metadata.getBoolean(MetadataIndex.SILENT);
    }

    @Override
    public void setSilent(boolean silent) {
        metadata.set(MetadataIndex.SILENT, silent);
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        bukkitMetadata.setMetadata(this, metadataKey, newMetadataValue);
    }

    public void damage(double amount) {
        damage(amount, null, DamageCause.CUSTOM);
    }

    public void damage(double amount, Entity source) {
        damage(amount, source, DamageCause.CUSTOM);
    }

    public void damage(double amount, DamageCause cause) {
        damage(amount, null, cause);
    }

    public void damage(double amount, Entity source, DamageCause cause) {
    }

    ////////////////////////////////////////////////////////////////////////////
    // Metadata

    public MetadataMap getMetadata() {
        return metadata;
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        return bukkitMetadata.getMetadata(this, metadataKey);
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        return bukkitMetadata.hasMetadata(this, metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        bukkitMetadata.removeMetadata(this, metadataKey, owningPlugin);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Permissions

    @Override
    public boolean isPermissionSet(String s) {
        return false;
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return false;
    }

    @Override
    public boolean hasPermission(String s) {
        return false;
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return false;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int i) {
        return null;
    }

    @Override
    public void removeAttachment(PermissionAttachment permissionAttachment) {

    }

    @Override
    public void recalculatePermissions() {

    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return null;
    }

    public boolean entityInteract(GlowPlayer player, InteractEntityMessage message) {
        // Override in subclasses to implement behavior
        return false;
    }

    @Override
    public Spigot spigot() {
        return spigot;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + entityId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GlowEntity other = (GlowEntity) obj;
        return entityId == other.entityId;
    }

    public boolean isLeashed() {
        return leashHolder != null;
    }

    /**
     * Gets the entity that is currently leading this entity.
     *
     * @return the entity holding the leash
     * @throws IllegalStateException if not currently leashed
     * @see org.bukkit.entity.LivingEntity#getLeashHolder()
     */
    public Entity getLeashHolder() throws IllegalStateException {
        if (!isLeashed()) {
            throw new IllegalStateException("Entity not leashed");
        }

        return leashHolder;
    }

    /**
     * Sets the leash on this entity to be held by the supplied entity.
     *
     * <p>This method has no effect on EnderDragons, Withers, Players, or Bats. Non-living entities
     * excluding leashes will not persist as leash holders.
     *
     * @param holder the entity to leash this entity to
     * @return whether the operation was successful
     * @see org.bukkit.entity.LivingEntity#setLeashHolder(Entity)
     */
    public boolean setLeashHolder(Entity holder) {
        // "This method has no effect on EnderDragons, Withers, Players, or Bats"
        if (!GlowLeashHitch.isAllowedLeashHolder(this.getType())) {
            return false;
        }

        if (this.leashHolder != null) {
            this.leashHolder.leashedEntities.remove(this);
        }

        if (holder == null) {
            // unleash
            this.leashHolder = null;
            leashHolderChanged = true;
            return true;
        }

        if (holder.isDead()) {
            return false;
        }

        this.leashHolder = (GlowEntity) holder;
        this.leashHolder.leashedEntities.add(this);
        leashHolderChanged = true;
        return true;
    }

    /**
     * Set the unique ID of this entities leash holder. Only useful during load of the entity.
     *
     * @param uniqueId The UUID
     */
    public void setLeashHolderUniqueId(UUID uniqueId) {
        if (ticksLived > 1 || isLeashed()) {
            return;
        }
        this.leashHolderUniqueId = uniqueId;
    }

    /**
     * Get the coordinates of the entity.
     *
     * @return The x and z coordinates of the entity.
     */
    public Coordinates getCoordinates() {
        return new Coordinates(location.getX(), location.getZ());
    }

    /**
     * The metadata store class for entities.
     */
    private static class EntityMetadataStore extends MetadataStoreBase<Entity>
            implements MetadataStore<Entity> {

        @Override
        protected String disambiguate(Entity subject, String metadataKey) {
            return UuidUtils.toString(subject.getUniqueId()) + ":" + metadataKey;
        }
    }
}
