package joserodpt.realskywars.api.chests;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RSWGroupedChest extends RSWChest {
    private Group group;

    public RSWGroupedChest(Type ct, Location l, BlockFace bf, Group group) {
        super(ct, l, bf);
        this.group = group;
    }

    public RSWGroupedChest(Type ct, String worldName, int x, int y, int z, BlockFace bf, Group group) {
        super(ct, worldName, x, y, z, bf);
        this.group = group;
    }

    @Override
    public void populate() {
        if(!isOpened()) {
            Inventory inv = ((Chest) getChestBlock().getState()).getInventory();
            if (group.hasItems()) {
                group.getItems(inv);
            } else {
                super.populate();
                group.setItems(inv);
            }
            this.opened = true;
        }
    }

    @Override
    public void clear() {
        super.clear();
        if(this.group != null)
            this.group.clear();
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public static class Group {
        public final int id;
        private ItemStack[] items;

        public Group(int id) {
            this.id = id;
            items = null;
        }

        public boolean hasItems() {
            return items != null;
        }

        public void setItems(Inventory inventory) {
            items = new ItemStack[inventory.getSize()];
            for(int i = 0; i < items.length; ++i) {
                ItemStack stack = inventory.getItem(i);
                items[i] = stack == null ? null : stack.clone();
            }
        }

        public void getItems(Inventory inventory) {
            for(int i = 0; i < Math.min(inventory.getSize(), items.length); ++i) {
                if(items[i] == null) {
                    inventory.setItem(i, null);
                }
                inventory.setItem(i, items[i].clone());
            }
        }

        public void clear() {
            items = null;
        }
    }
}
