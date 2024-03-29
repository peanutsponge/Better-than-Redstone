package peanutsponge.better_than_redstone;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

import static peanutsponge.better_than_redstone.BetterThanRedstoneMod.MOD_ID;
import static peanutsponge.better_than_redstone.Directions.getPlacementDirection;
import static peanutsponge.better_than_redstone.Directions.makeDirectionCode;
import static turniplabs.halplibe.helper.TextureHelper.getOrCreateBlockTextureIndex;

public class BlockDirectional extends Block {
	public int[] atlasIndices = new int[4];
	public BlockDirectional(String key, int id, Material material) {
		super(key, id, material);
		this.atlasIndices[0] = getOrCreateBlockTextureIndex(MOD_ID, "default_input.png");
		this.atlasIndices[1] = getOrCreateBlockTextureIndex(MOD_ID, "default_side.png");
		this.atlasIndices[3] = getOrCreateBlockTextureIndex(MOD_ID, "default_output.png");
	}

	public int getOutputTexture(int data) {
		return this.atlasIndices[3];
	}
	public int getInputTexture(int data) {
		return this.atlasIndices[0];
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(Side side, int data) {
		Direction placementDirection = getPlacementDirection(Directions.getDirectionCode(data));
		if (side.getId() == placementDirection.getId()) { // face texture
			return getOutputTexture(data);
		} else if (side.getId() == placementDirection.getOpposite().getId()) {
			return getInputTexture(data);
		} else return this.atlasIndices[1]; //TODO differing side support
	}
	public void onBlockPlaced(World world, int x, int y, int z, Side side, EntityLiving entity, double sideHeight) {
		Direction placementDirection = entity.getPlacementDirection(side).getOpposite();
		Direction horizontalDirection = Direction.getHorizontalDirection(entity.yRot);
//		LOGGER.info("On block placed: " + horizontalDirection +" , "+ horizontalDirection.getHorizontalIndex());
		world.setBlockMetadataWithNotify(x, y, z, makeDirectionCode(placementDirection, horizontalDirection));
	}
}
