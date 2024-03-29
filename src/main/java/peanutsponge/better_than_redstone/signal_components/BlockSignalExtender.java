package peanutsponge.better_than_redstone.signal_components;

import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import peanutsponge.better_than_redstone.BlockDirectional;
import peanutsponge.better_than_redstone.Directions;

import java.util.Random;

import static peanutsponge.better_than_redstone.BetterThanRedstoneMod.*;
import static peanutsponge.better_than_redstone.Signal.*;
import static turniplabs.halplibe.helper.TextureHelper.getOrCreateBlockTextureIndex;

public class BlockSignalExtender extends BlockDirectional {
	public int[] atlasIndicesOutput = new int[2];

	public BlockSignalExtender(String key, int id) {
		super(key, id, Material.metal);
		this.atlasIndicesOutput[0] = getOrCreateBlockTextureIndex(MOD_ID, "signal_extender_off.png");
		this.atlasIndicesOutput[1] = getOrCreateBlockTextureIndex(MOD_ID, "signal_extender_on.png");
		this.atlasIndices[1] = getOrCreateBlockTextureIndex(MOD_ID, "signal_extender_side.png");
		}
	@Override
	public int getOutputTexture(int data) {
		return this.isOn(data) ? this.atlasIndicesOutput[1] : this.atlasIndicesOutput[0];
	}

 	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		int data = world.getBlockMetadata(x, y, z);
		if (this.isOn(data) && !hasInputCurrent(world, x, y, z)) { // turn off
			setOn(world, x, y, z, 0);
		} else if (!this.isOn(data) && hasInputCurrent(world, x, y, z)) { // turn on
			setOn(world, x, y, z, 1);
		}
	}

	@Override
	public void onBlockPlaced(World world, int x, int y, int z, Side side, EntityLiving entity, double sideHeight) {
		super.onBlockPlaced(world, x,  y,  z, side, entity, sideHeight);
		boolean hasInputCurrent = hasInputCurrent(world, x, y, z);
		if (hasInputCurrent) {
			world.scheduleBlockUpdate(x, y, z, this.id, 1);
		}
	}

 	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		int data = world.getBlockMetadata(x, y, z);
		boolean hasInput = hasInputCurrent(world, x, y, z);
		if (this.isOn(data) && !hasInput) { //turn off
			world.scheduleBlockUpdate(x, y, z, this.id, getSumSideCurrent(world, x, y, z));
		} else if (!this.isOn(data) && hasInput) { //turn on
			world.scheduleBlockUpdate(x, y, z, this.id, 1);
		}
	}
	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public boolean isPoweringTo(WorldSource blockAccess, int x, int y, int z, int side) {
		int data = blockAccess.getBlockMetadata(x, y, z);
		Direction direction = Directions.getPlacementDirection(Directions.getDirectionCode(data));
		return this.isOn(data) & side == direction.getOpposite().getId();
	}

public void setOn(World world, int x, int y, int z, int on) {
	int data = world.getBlockMetadata(x, y, z);
	int direction = Directions.getDirectionCode(data);
	world.setBlockMetadataWithNotify(x, y, z, direction + (on * 16));
	}

	public boolean isOn(int data) {
		int on = data >> 4;
		return (on == 1);
	}
}
