package com.pedestriamc.strings.channel;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.local.LocalChannel;
import com.pedestriamc.strings.api.channel.local.Locality;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

// Checks if a Channel can be resolved in all possible scenarios. Expected to be used in ChannelLoader instance.
public class ResolutionValidator<L extends Locality<?>> {

    private static final String STATUS_PLACEHOLDER = "{STATUS}";
    private static final String OK = "No issues found.";
    private static final String ERR = "Potential issues found.";
    private static final String LOCALITY_ERR = "[!] Can't guarantee channel resolution in world: %s\n";

    private static final String HEADER = """
           (Channel Resolution Report): {STATUS}
           *NOTE: A "No issues found" status does not guarantee issues can't arise through player-specific issues, such as a Channel mute.
    """;

    private final ChannelLoader loader;
    private final Set<L> localities;

    public ResolutionValidator(@NotNull ChannelLoader loader, @NotNull Collection<L> localities) {
        this.loader = loader;
        this.localities = new HashSet<>(localities);

    }

    public String generateReport() {
        SortedSet<Channel> channels = loader.getSortedChannelSet();
        StringBuilder builder = new StringBuilder(HEADER);
        boolean issueFound = false;

        for (L locality : localities) {
            String result = testLocality(locality, channels);
            if (result != null) {
                issueFound = true;
                builder.append(result); // new line should already be handled
            }
        }

        String message = builder.toString();
        return message.replace(STATUS_PLACEHOLDER, issueFound ? ERR : OK);
    }

    @Nullable
    private String testLocality(@NotNull L locality, @NotNull Set<Channel> channels) {
        Set<Channel> result = channels.stream()
                .filter(c -> c.getMembership() == Membership.DEFAULT)
                .filter(c -> !(c instanceof LocalChannel<?> local) || local.containsLocality(locality))
                .collect(Collectors.toSet());

        return result.isEmpty() ? String.format(LOCALITY_ERR, locality.getName()) : null;
    }

}
