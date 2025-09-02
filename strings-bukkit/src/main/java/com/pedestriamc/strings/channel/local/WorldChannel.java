package com.pedestriamc.strings.channel.local;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.data.LocalChannelBuilder;
import com.pedestriamc.strings.api.channel.local.Locality;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.channel.DefaultChannel;
import com.pedestriamc.strings.user.util.UserUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.pedestriamc.strings.api.channel.data.IChannelBuilder.Identifier;

import java.util.HashSet;
import java.util.Set;

/**
 * Channel implementation that focuses on one or more {@link World}(s) on the server.
 * Expected to be used with the {@link DefaultChannel}
 */
public class WorldChannel extends AbstractLocalChannel {

    public static final Identifier IDENTIFIER = Identifier.WORLD;

    public WorldChannel(StringsPlatform plugin, LocalChannelBuilder<?> builder) {
        this((Strings) plugin, builder);
    }

    public WorldChannel(@NotNull Strings strings, @NotNull LocalChannelBuilder<?> builder) {
        super(strings, builder);
    }

    @Override
    public @NotNull Set<StringsUser> getRecipients(@NotNull StringsUser sender) {
        HashSet<StringsUser> recipients = new HashSet<>();
        UserUtil userUtil = getUserUtil();

        for(World w : Locality.convertToWorlds(getWorlds())) {
            recipients.addAll(convertToUsers(w.getPlayers()));
        }

        recipients.addAll(getMembers());
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p.hasPermission("strings.channels." + this.getName() + ".receive")) {
                recipients.add(userUtil.getUser(p));
            }
        }
        
        return filterMutesAndIgnores(sender, recipients);
    }

    @Override
    public @NotNull Type getType() {
        return Type.WORLD;
    }

    // N/A to this implementation, UnsupportedOperationException always thrown.
    @Override
    public double getProximity() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("getProximity() called on WorldChannel instance, which is unsupported.");
    }

    // N/A to this implementation, UnsupportedOperationException always thrown.
    @Override
    public void setProximity(double proximity) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("setProximity() called on WorldChannel instance, which is unsupported.");
    }

}

