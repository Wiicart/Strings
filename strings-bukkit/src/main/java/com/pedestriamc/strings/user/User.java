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
        this.displayName = Objects.requireNonNullElse(builder.getDisplayName(), "");
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
        player().sendMessage(message);
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        if (player instanceof Audience a) {
            a.sendMessage(message);
        } else {
            // Relying on audience unreliable for releases.
            player.sendMessage(ComponentConverter.toString(message));
        }

    }

    @NotNull
    public Player player() {
        return player;
    }

    @NotNull
    public World getWorld() {
        return player.getWorld();
    }

    @Override
    public @NotNull Audience audience() {
        return strings.adventure().player(player());
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
        return color(getChatColorComponent().toString());
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
        this.player.setDisplayName(displayName);
    }

    @Override
    public @NotNull String getDisplayName() {
        return player.getDisplayName();
    }

    @Override
    public void setPrefix(@NotNull String prefix) {
        Objects.requireNonNull(prefix);
        this.prefix = prefix;
        if (strings.isUsingVault()) {
            strings.getVaultChat().setPlayerPrefix(player, prefix);
        }
    }

    @Override
    public @NotNull String getPrefix() {
        if (strings.isUsingVault()) {
            return color(strings.getVaultChat().getPlayerPrefix(player));
        } else {
            if (prefix == null || prefix.isEmpty()) {
                return "";
            }
            return color(prefix);
        }
    }

    @Override
    public void setSuffix(@NotNull String suffix) {
        Objects.requireNonNull(suffix);
        this.suffix = suffix;
        if (strings.isUsingVault()) {
            strings.getVaultChat().setPlayerSuffix(player, suffix);
        }
    }

    @Override
    public @NotNull String getSuffix() {
        if (strings.isUsingVault()) {
            return color(strings.getVaultChat().getPlayerSuffix(player));
        } else {
            if (suffix == null || suffix.isEmpty()) {
                return "";
            }
            return color(suffix);
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

        Location thisLocation = player().getLocation();
        Location otherLocation = playerOf(user).getLocation();

        return thisLocation.distanceSquared(otherLocation);
    }

    @Override
    public Locality<?> getLocality() {
        return strings.localityManager().get(player.getWorld());
    }

    @Override
    public double getX() {
        return player.getLocation().getX();
    }

    @Override
    public double getY() {
        return player.getLocation().getY();
    }

    @Override
    public double getZ() {
        return player.getLocation().getZ();
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

    @Contract("_ -> new")
    private @NotNull String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}
