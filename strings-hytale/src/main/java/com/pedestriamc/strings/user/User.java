package com.pedestriamc.strings.user;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.local.Locality;
import com.pedestriamc.strings.common.user.AbstractUser;
import com.pedestriamc.strings.common.user.UserBuilder;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class User extends AbstractUser {

    private final Strings strings;

    private final UUID uuid;
    private final PlayerRef player;

    public User(@NotNull UserBuilder<User> builder) {
        super(builder);
        this.strings = (Strings) builder.getStrings();

        uuid = builder.getUuid();
        player = Universe.get().getPlayer(uuid);
    }

    @Override
    public @NotNull String getName() {
        return player.getUsername();
    }

    @Override
    public @NotNull String getChatColor() {
        return "";
    }

    @Override
    public @NotNull String getPrefix() {
        return "";
    }

    @Override
    public void setPrefix(@NotNull String prefix) {

    }

    @Override
    public @NotNull String getSuffix() {
        return "";
    }

    @Override
    public void setSuffix(@NotNull String suffix) {

    }

    @Override
    public @NotNull String getDisplayName() {
        return player.getUsername();
    }

    @Override
    public void setDisplayName(@NotNull String displayName) {

    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return false;
    }

    @Override
    public boolean isOperator() {
        return false;
    }

    @Override
    public double distanceSquared(com.pedestriamc.strings.api.user.@NotNull StringsUser user) {
        return 0;
    }

    @Override
    public Locality<?> getLocality() {
        World world = strings.universe().getWorld(player.getUuid());
        return strings.localityManager().get(world);
    }

    @Override
    public double getX() {
        return 0;
    }

    @Override
    public double getY() {
        return 0;
    }

    @Override
    public double getZ() {
        return 0;
    }

    @Override
    public void sendMessage(@NotNull String message) {

    }

    @Override
    public void sendMessage(@NotNull Component message) {
        throw new UnsupportedOperationException("Adventure not used on Hytale");
    }

    @Override
    public @NotNull Audience audience() {
        throw new UnsupportedOperationException("Adventure not used on Hytale");
    }
}
