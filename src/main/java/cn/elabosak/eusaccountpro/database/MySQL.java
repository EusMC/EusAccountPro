package cn.elabosak.eusaccountpro.database;

import org.bukkit.Location;
<<<<<<< HEAD
<<<<<<< HEAD
import org.bukkit.inventory.Inventory;
=======
>>>>>>> parent of e751e49... eap exit and inv json done
=======
>>>>>>> parent of e751e49... eap exit and inv json done

import java.io.IOException;
import java.util.UUID;

public class MySQL extends Database {

    @Override
    public String getSecretKey(UUID uuid) throws IOException {
        return null;
    }

    @Override
    public boolean updatePlayer(UUID uuid, String secretKey) {
        return false;
    }

    @Override
    public boolean isPlayerRegistered(UUID uuid) {
        return false;
    }

    @Override
    public boolean deletePlayer(UUID uuid) {
        return false;
    }

    @Override
    public boolean SafePoint(UUID uuid, Location safepoint) {
        return false;
    }

    @Override
    public Location getSafePoint(UUID uuid) {
        return null;
    }

<<<<<<< HEAD
<<<<<<< HEAD
    @Override
    public boolean updateInv(UUID uuid, Inventory inventory) throws IOException {
        return false;
    }

    @Override
    public Inventory getInv(UUID uuid) throws IOException {
        return null;
    }

    @Override
    public boolean deleteInv(UUID uuid) throws IOException {
        return false;
    }

    @Override
    public boolean updateIP(UUID uuid, String string) {
        return false;
    }

    @Override
    public String getIPdata(UUID uuid) {
        return null;
    }

=======
>>>>>>> parent of e751e49... eap exit and inv json done
=======
>>>>>>> parent of e751e49... eap exit and inv json done

}
