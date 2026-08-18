package com.pedestriamc.strings.user;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.local.Locality;
import com.pedestriamc.strings.api.collections.BoundedLinkedBuffer;
import com.pedestriamc.strings.api.text.format.ComponentConverter;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.common.user.AbstractUser;
import com.pedestriamc.strings.common.user.UserBuilder;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Stores information about players for Strings.
 * Defaults to Vault values where available.
 */
@SuppressWarnings("FieldCanBeLocal")
public final class User extends AbstractUser implements Permissible {

    private final Strings strings;

    private final Player player;
    private final Audience audience;
    private final String name;

    private @Nullable String prefix;
    private @Nullable String suffix;
    private @Nullable String displayName;

    private volatile World worldSnapshot;
    private volatile double xSnapshot;
    private volatile double ySnapshot;
    private volatile double zSnapshot;

    BoundedLinkedBuffer<EntityDamageEvent> previousDamage = new BoundedLinkedBuffer<>(2);

    public static User of(@NotNull StringsUser user) {
        if(user instanceof User u) {
            return u;
        }

        throw new RuntimeException("Provided User does not implement StringsBukkit User.");
    }

    public static Player playerOf(@NotNull StringsUser user) {
        if (user instanceof User u) {
            return u.player();
        } else {
            UUID uniqueId = user.getUniqueId();
            return Bukkit.getPlayer(uniqueId);
        }
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull UserBuilder<User> builder(Strings strings, UUID uuid, boolean isNew) {
        return new UserBuilder<>(User::new, strings, uuid, isNew);
    }

    User(@NotNull UserBuilder<User> builder) {
        super(builder);
        this.strings = (Strings) builder.getStrings();
        this.player = Objects.requireNonNull(strings.getServer().getPlayer(getUniqueId()));
        this.audience = loadAudience(getUniqueId());

        this.name = player.getName();
        this.prefix = Objects.requireNonNullElse(builder.getPrefix(), "");
        this.suffix = Objects.requireNonNullElse(builder.getSuffix(), "");
        this.displayName = Objects.requireNonNullElse(builder.getDisplayName(), player.getDisplayName());
        if (strings.isUsingVault()) {
            this.prefix = Objects.requireNonNullElse(strings.getVaultChat().getPlayerPrefix(player), this.prefix);
            this.suffix = Objects.requireNonNullElse(strings.getVaultChat().getPlayerSuffix(player), this.suffix);
        }
        updatePositionSnapshot();
    }

    @NotNull
    private Audience loadAudience(@NotNull UUID uuid) {
        return strings.adventure().player(player);
    }

    @Nullable
    public EntityDamageEvent getSecondToLastDamage() {
        if (previousDamage.size() < 2) {
            return null;
        }

        BoundedLinkedBuffer.Node<EntityDamageEvent> tail = previousDamage.getTail();
        if (tail != null) {
            return tail.get();
        }

        return null;
    }

    public void pushDamageEvent(@NotNull EntityDamageEvent event) {
        previousDamage.add(event);
    }


    /**
     * Sends a message to the User.
     * @param message The message.
     */
    @Override
    public void sendMessage(@NotNull String message) {
        strings.forEntity(strings, player, () -> player.sendMessage(message));
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        strings.forEntity(strings, player, () -> audience.sendMessage(message));
    }

    @NotNull
    public Player player() {
        return player;
    }

    @NotNull
    public World getWorld() {
        return worldSnapshot;
    }

    @Override
    public @NotNull Audience audience() {
        return audience;
    }

    @Override
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Provides the User's chat color.
     * @return A chat color.
     */
    @NotNull
    @Override
    public String getChatColor() {
        return bukkitColor(getChatColorComponent().toString());
    }

    /**
     * Provides the User's chat color.
     * If the User's chat color is null, the chat color of the passed in channel is returned.
     * @param channel The channel to get the fallback chat color from.
     * @return A chat color.
     */
    @SuppressWarnings("java:S1874")
    public String getChatColor(@NotNull Channel channel) {
        String chatColor = getChatColor();
        if (chatColor.isEmpty()) {
            return channel.getDefaultColor();
        }
        return chatColor;
    }

    @Override
    public void setDisplayName(@NotNull String displayName) {
        this.displayName = displayName;
        strings.forEntity(strings, player, () -> player.setDisplayName(displayName));
    }

    @Override
    public @NotNull String getDisplayName() {
        return displayName;
    }

    @Override
    public void setPrefix(@NotNull String prefix) {
        Objects.requireNonNull(prefix);
        this.prefix = prefix;
        if (strings.isUsingVault()) {
            strings.forEntity(strings, player, () -> strings.getVaultChat().setPlayerPrefix(player, prefix));
        }
    }

    @Override
    public @NotNull String getPrefix() {
        if (strings.isUsingVault()) {
            return bukkitColor(Objects.requireNonNullElse(prefix, ""));
        } else {
            if (prefix == null || prefix.isEmpty()) {
                return "";
            }
            return bukkitColor(prefix);
        }
    }

    @Override
    public void setSuffix(@NotNull String suffix) {
        Objects.requireNonNull(suffix);
        this.suffix = suffix;
        if (strings.isUsingVault()) {
            strings.forEntity(strings, player, () -> strings.getVaultChat().setPlayerSuffix(player, suffix));
        }
    }

    @Override
    public @NotNull String getSuffix() {
        if (strings.isUsingVault()) {
            return bukkitColor(Objects.requireNonNullElse(suffix, ""));
        } else {
            if (suffix == null || suffix.isEmpty()) {
                return "";
            }
            return bukkitColor(suffix);
        }
    }

    @Override
    public boolean isPermissionSet(@NotNull String name) {
        return player().isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission perm) {
        return player().isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(@NotNull String name) {
        return player().hasPermission(name);
    }

    @Override
    public boolean isOperator() {
        return player.isOp();
    }

    @Override
    public double distanceSquared(@NotNull StringsUser user) {
        if (getLocality() != user.getLocality()) {
            return Double.MAX_VALUE;
        }

        double dx = getX() - user.getX();
        double dy = getY() - user.getY();
        double dz = getZ() - user.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    @Override
    public Locality<?> getLocality() {
        return strings.localityManager().get(worldSnapshot);
    }

    @Override
    public double getX() {
        return xSnapshot;
    }

    @Override
    public double getY() {
        return ySnapshot;
    }

    @Override
    public double getZ() {
        return zSnapshot;
    }

    /** Updates the position view used by cross-region calculations. */
    public void updatePositionSnapshot() {
        updatePositionSnapshot(player.getLocation());
    }

    /** Updates the position view from an already thread-safe event location. */
    public void updatePositionSnapshot(@NotNull Location location) {
        worldSnapshot = location.getWorld();
        xSnapshot = location.getX();
        ySnapshot = location.getY();
        zSnapshot = location.getZ();
    }

    @Override
    public boolean hasPermission(@NotNull Permission perm) {
        return player().hasPermission(perm);
    }

    @Override
    @NotNull
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value) {
        return player().addAttachment(plugin, name, value);
    }

    @Override
    @NotNull
    public PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return player().addAttachment(plugin);
    }

    @Override
    @Nullable
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value, int ticks) {
        return player().addAttachment(plugin, name, value, ticks);
    }

    @Override
    @Nullable
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, int ticks) {
        return player().addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(@NotNull PermissionAttachment attachment) {
        player().removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        player().recalculatePermissions();
    }

    @Override
    @NotNull
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return player().getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return player().isOp();
    }

    @Override
    public void setOp(boolean value) {
        player.setOp(value);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof User u) {
            return this.getUniqueId().equals(u.getUniqueId());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getUniqueId().hashCode();
    }

    @Contract("_ -> new")
    private @NotNull String bukkitColor(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}
