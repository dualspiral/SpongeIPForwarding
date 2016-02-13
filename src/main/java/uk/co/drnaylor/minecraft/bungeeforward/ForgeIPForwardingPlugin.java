package uk.co.drnaylor.minecraft.bungeeforward;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.event.EventHandler;

import java.util.Arrays;

public class ForgeIPForwardingPlugin extends Plugin implements Listener {

    @Override
    public void onEnable() {
        this.getLogger().info("Hi! We're adding in IP Forwarding support for SpongeForge servers for you now.");
        this.getProxy().getPluginManager().registerListener(this, this);
    }

    // First point that the UserConnection object has been initialised, but before a server connection takes place.
    @EventHandler
    public void onPostLoginEvent(PostLoginEvent event) {
        ProxiedPlayer pp = event.getPlayer();
        if (pp.isForgeUser() && pp instanceof UserConnection) {
            try {
                init((UserConnection) pp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void init(UserConnection conn) throws Exception {
        // If we IP forward, add the a FML marker to the game profile.
        if (BungeeCord.getInstance().config.isIpForward()) {
            // Get the user profile.
            LoginResult profile = conn.getPendingConnection().getLoginProfile();

            // Get the current properties and copy them into a slightly bigger array.
            LoginResult.Property[] oldp = profile.getProperties();
            LoginResult.Property[] newp = Arrays.copyOf(oldp, oldp.length + 2);

            // Add a new profile property that specifies that this user is a Forge user.
            newp[newp.length - 2] = new LoginResult.Property("forgeClient", "true", null);

            // If we do not perform the replacement, then the IP Forwarding code in Spigot et. al. will try to split on this prematurely.
            newp[newp.length - 1] = new LoginResult.Property("extraData", conn.getExtraDataInHandshake().replaceAll("\0", "\1"), "");

            // Set the properties in the profile. All done.
            profile.setProperties(newp);
        }
    }
}
