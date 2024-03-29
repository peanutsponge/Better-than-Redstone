package peanutsponge.better_than_redstone.signal_components;

import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import peanutsponge.better_than_redstone.BlockDirectional;
import peanutsponge.better_than_redstone.Directions;

import static peanutsponge.better_than_redstone.BetterThanRedstoneMod.MOD_ID;
import static peanutsponge.better_than_redstone.Signal.*;
import static turniplabs.halplibe.helper.TextureHelper.getOrCreateBlockTextureIndex;

public class BlockSignalDisplay extends BlockDirectional {
	public int[] atlasIndicesOutput = new int[16];
	public BlockSignalDisplay(String key, int id) {
		super(key, id, Material.metal);
		for(int i = 0; i < 16; ++i) {
			this.atlasIndicesOutput[i] = getOrCreateBlockTextureIndex(MOD_ID, key + " ("+ i +").png");
		}
	}
	@Override
	public int getOutputTexture(int data) {
		return this.atlasIndicesOutput[getSignalCode(data)];
	}

	public void onBlockPlaced(World world, int x, int y, int z, Side side, EntityLiving entity, double sideHeight) {
		super.onBlockPlaced(world, x, y, z, side, entity, sideHeight);
		this.propagateCurrent(world, x, y, z);
	}
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
		this.propagateCurrent(world, x, y, z);
	}


	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		this.propagateCurrent(world, x, y, z);
		super.onNeighborBlockChange(world, x, y, z, blockId);
	}

	/**
	 * Calculates the current it should have, updates its metadata accordingly and notifies neighbors
	 */
	private void propagateCurrent(World world, int x, int y, int z) {
		int data = world.getBlockMetadata(x, y, z);
		int oldCurrent = getSignalCode(data);
		int newCurrent = getInputCurrent(world,x,y,z);
		if (newCurrent != oldCurrent){
			world.setBlockMetadata(x, y, z, makeMetaData(Directions.getDirectionCode(data), newCurrent));
			world.notifyBlocksOfNeighborChange(x,y,z,this.id);
		}
	}
}
