/**
 * 
 */
package com.someguyssoftware.treasure2.generator.well;

import java.util.Random;

import com.someguyssoftware.gottschcore.cube.Cube;
import com.someguyssoftware.gottschcore.positional.ICoords;
import com.someguyssoftware.gottschcore.random.RandomHelper;
import com.someguyssoftware.gottschcore.world.WorldInfo;
import com.someguyssoftware.treasure2.Treasure;
import com.someguyssoftware.treasure2.config.IWellConfig;
import com.someguyssoftware.treasure2.generator.TemplateGeneratorData;
import com.someguyssoftware.treasure2.generator.TreasureGeneratorData;
import com.someguyssoftware.treasure2.generator.TreasureGeneratorResult;
import com.someguyssoftware.treasure2.meta.StructureArchetype;
import com.someguyssoftware.treasure2.meta.StructureType;
import com.someguyssoftware.treasure2.world.gen.structure.IStructureInfo;
import com.someguyssoftware.treasure2.world.gen.structure.ITemplateGenerator;
import com.someguyssoftware.treasure2.world.gen.structure.TemplateGenerator;
import com.someguyssoftware.treasure2.world.gen.structure.TemplateHolder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.template.PlacementSettings;

/**
 * @author Mark Gottschling on Aug 20, 2019
 *
 */
public class WellGenerator implements IWellGenerator<TreasureGeneratorResult<TreasureGeneratorData>> {

	
	@Override
	public boolean generate(World world, Random random, ICoords spawnCoords, IWellConfig config) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TreasureGeneratorResult<TreasureGeneratorData> generate2(World world, Random random,
			ICoords spawnCoords, IWellConfig config) {
		TreasureGeneratorResult<TreasureGeneratorData> result = new TreasureGeneratorResult<>(TreasureGeneratorData.class);
		
		// 1. determine y-coord of land surface
		spawnCoords = WorldInfo.getDryLandSurfaceCoords(world, spawnCoords);
		if (spawnCoords == null || spawnCoords == WorldInfo.EMPTY_COORDS) {
			Treasure.logger.debug("Returning due to marker coords == null or EMPTY_COORDS");
			return result.fail(); 
		}
		
		// 2. check if it has 50% land
		if (!WorldInfo.isSolidBase(world, spawnCoords, 3, 3, 50)) {
			Treasure.logger.debug("Coords [{}] does not meet solid base requires for {} x {}", spawnCoords.toShortString(), 3, 3);
			return result.fail();
		}		
		
		// get the biome ID
		Biome biome = world.getBiome(spawnCoords.toPos());
		
		// generate the structure
		TemplateGenerator generator = new TemplateGenerator();
		generator.setNullBlock(Blocks.BEDROCK);
		
		// get the template
		TemplateHolder holder = Treasure.TEMPLATE_MANAGER.getTemplate(world, random, StructureArchetype.SURFACE, StructureType.WELL, biome);
		if (holder == null) return result.fail();
				
		// select a random rotation
		Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
		Treasure.logger.debug("rotation used -> {}", rotation);
		
		// setup placement
		PlacementSettings placement = new PlacementSettings();
		placement.setRotation(rotation).setRandom(random);
		
		// build well
		 TreasureGeneratorResult<TemplateGeneratorData> genResult = generator.generate2(world, random, holder,  placement, spawnCoords);
		Treasure.logger.debug("Well gen  structure result -> {}", genResult.isSuccess());
		 if (!genResult.isSuccess()) {
			 Treasure.logger.debug("failing well gen.");
			return result.fail();
		}
		
		// get the rotated/transformed size
		//BlockPos transformedSize = holder.getTemplate().transformedSize(rotation);
		BlockPos transformedSize = genResult.getData().getSize().toPos();
		
		// add flowers around well
		//addDecorations(world, random, spawnCoords, transformedSize.getX(), transformedSize.getZ());
		
		// TODO add chest if any
				
		// add the structure data to the result
		result.setData(genResult.getData());

		Treasure.logger.info("CHEATER! Wishing Well at coords: {}", spawnCoords.toShortString());
		
		return result.success();
	}

	/**
	 * 
	 * @param world
	 * @param random
	 * @param coords
	 * @param width
	 * @param depth
	 */
	public void addDecorations(World world, Random random, ICoords coords, int width, int depth) {
		ICoords startCoords = coords.add(-1, 0, -1);
	
		// TODO change to scan the entire size (x,z) of well footprint and detect the edges ... place flowers adjacent to edge blocks. 
		
		// north of well
		for (int widthIndex = 0; widthIndex < width; widthIndex++) {
			if (RandomHelper.randomInt(0, 1) == 0) {
//				ICoords decoCoords = startCoords.add(widthIndex, 0, 0);
				addDecoration(world, random, startCoords.add(widthIndex, 0, 0));
			}
		}
		
		// south of well
		startCoords = coords.add(-1, 0, depth);
		for (int widthIndex = 0; widthIndex < width; widthIndex++) {
			if (RandomHelper.randomInt(0,  1) == 0) {
				addDecoration(world, random, startCoords.add(widthIndex, 0, 0));
			}
		}
		
		// west of well
		startCoords = coords.add(-1, 0, 0);
		for (int depthIndex = 0; depthIndex < depth-1; depthIndex++) {
			if (RandomHelper.randomInt(0,  1) == 0) {
				addDecoration(world, random, startCoords.add(0, 0, depthIndex));
			}
		}
		
		// east of well
		startCoords = coords.add(width, 0, 0);
		for (int depthIndex = 0; depthIndex < depth-1; depthIndex++) {
			if (RandomHelper.randomInt(0,  1) == 0) {
				addDecoration(world, random, startCoords.add(0, 0, depthIndex));
			}
		}
	}

	@Override
	public void addDecoration(World world, Random random, ICoords coords) {
		IBlockState blockState = null;
		ICoords markerCoords = WorldInfo.getDryLandSurfaceCoords(world, coords);
		
		if (markerCoords == null || markerCoords == WorldInfo.EMPTY_COORDS) {
			Treasure.logger.debug("Returning due to marker coords == null or EMPTY_COORDS");
			return;
		}
		
		Cube markerCube = new Cube(world, markerCoords.add(0, -1, 0));
//		Treasure.logger.debug("Marker on block: {}", markerCube.getState());
		if (markerCube.equalsBlock(Blocks.GRASS)) {
			blockState = getDecorationBlockState(world, Blocks.RED_FLOWER);
		}
		else if (markerCube.equalsBlock(Blocks.DIRT)) {
			DirtType dirtType = markerCube.getState().getValue(BlockDirt.VARIANT);
			if (dirtType == DirtType.DIRT) {
				blockState = getDecorationBlockState(world, Blocks.RED_FLOWER);
			}
			else if (dirtType == DirtType.PODZOL) {
//				Treasure.logger.debug("On podzol block");
				Block mushBlock = random.nextInt(2) == 0 ? Blocks.BROWN_MUSHROOM : Blocks.RED_MUSHROOM;
				blockState = getDecorationBlockState(world, mushBlock);
			}
			else {
//				Treasure.logger.debug("On coarse dirt block");
//				Block grassBlock = Blocks.TALLGRASS;
//				blockState = grassBlock.getDefaultState().withProperty(BlockTallGrass.TYPE, BlockTallGrass.EnumType.values()[meta]);			
				blockState = getDecorationBlockState(world, Blocks.TALLGRASS);
			}
		}
		else if (markerCube.equalsBlock(Blocks.MYCELIUM)) {
//			Treasure.logger.debug("On mycelium block");
			Block mushBlock = random.nextInt(2) == 0 ? Blocks.BROWN_MUSHROOM_BLOCK : Blocks.RED_MUSHROOM;
			blockState = getDecorationBlockState(world, mushBlock);					
		}
		else {
//			Treasure.logger.debug("On other block");
			blockState = getDecorationBlockState(world, Blocks.TALLGRASS);
	}				
		// set the block state
		world.setBlockState(coords.toPos(), blockState, 3);
//		Treasure.logger.debug("Generating blockstate: {}", blockState);
	}
}