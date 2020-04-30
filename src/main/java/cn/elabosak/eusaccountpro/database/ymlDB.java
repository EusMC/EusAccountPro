package cn.elabosak.eusaccountpro.database;

import org.bukkit.Location;
<<<<<<< HEAD
import org.bukkit.inventory.Inventory;
=======
import org.bukkit.inventory.ItemStack;
>>>>>>> parent of 1ed2b6b... ok

import java.io.IOException;
import java.util.UUID;

public class ymlDB extends Database{
    @Override
    public String getSecretKey(UUID uuid) throws IOException {
        return null;
    }

    @Override
    public boolean updatePlayer(UUID uuid, String secretKey) throws IOException {
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
    public boolean SafePoint(UUID uuid, Location safepoint) throws IOException {
        return false;
    }

    @Override
    public Location getSafePoint(UUID uuid) throws IOException {
        return null;
    }

    @Override
    public boolean updateInv(UUID uuid, ItemStack[] itemStacks) throws IOException {
        return false;
    }

    @Override
    public ItemStack[] getInv(UUID uuid) throws IOException {
        return null;
    }
<<<<<<< HEAD

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
>>>>>>> parent of 1ed2b6b... ok
}
