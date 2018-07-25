/**
 * 
 */
package com.someguyssoftware.treasure2.generator.chest;

import java.util.List;
import java.util.Random;

import com.someguyssoftware.gottschcore.random.RandomHelper;
import com.someguyssoftware.treasure2.Treasure;
import com.someguyssoftware.treasure2.enums.Rarity;
import com.someguyssoftware.treasure2.loot.TreasureLootTables;

import net.minecraft.world.storage.loot.LootTable;

/**
 * 
 * @author Mark Gottschling on Jan 24, 2018
 *
 */
public class CommonChestGenerator extends AbstractChestGenerator {
	
	/**
	 * 
	 */
	public CommonChestGenerator() {}

	/**
	 * 
	 * @param rarity
	 * @return
	 */
//	@Override
//	public LootContainer selectContainer(Random random, final Rarity rarity) {
//		LootContainer container = LootContainer.EMPTY_CONTAINER;
//		
//		// select the loot container by rarities
//		Rarity[] rarities = new Rarity[] {Rarity.COMMON, Rarity.UNCOMMON};
//		List<LootContainer> containers = DbManager.getInstance().getContainersByRarity(Arrays.asList(rarities));
//		if (containers != null && !containers.isEmpty()) {
//			if (containers.size() == 1) {
//				container = containers.get(0);
//			}
//			else {
//				container = containers.get(RandomHelper.randomInt(random, 0, containers.size()-1));
//			}
//			Treasure.logger.info("Chosen chest container:" + container);
//		}
//		return container;
//	}

	/**
	 * 
	 * @param random
	 * @param chestRarity
	 * @return
	 */
	@Override
	public LootTable selectLootTable(Random random, final Rarity chestRarity) {
		LootTable table = null;
		// select the loot table by rarity
		List<LootTable> tables = TreasureLootTables.CHEST_LOOT_TABLE_MAP.get(Rarity.COMMON);
		tables.addAll(TreasureLootTables.CHEST_LOOT_TABLE_MAP.get(Rarity.UNCOMMON));
		
		if (tables != null && !tables.isEmpty()) {
			int index = 0;
			/*
			 * get a random container
			 */
			if (tables.size() == 1) {
				table = tables.get(index);
			}
			else {
				index = RandomHelper.randomInt(random, 0, tables.size()-1);
				table = tables.get(index);
			}
			Treasure.logger.debug("Selected loot table index --> {}", index);
		}
		return table;
	}
	
}
