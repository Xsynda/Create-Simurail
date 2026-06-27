package com.crystaelix.simurail.content.automatic_coupler;

import com.crystaelix.simurail.content.gangway_frame.GangwayFrame;
import com.crystaelix.simurail.content.gangway_frame.GangwayFrameBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AutomaticCouplerBlockItem extends BlockItem {

	public AutomaticCouplerBlockItem(AutomaticCouplerBlock block, Properties properties) {
		super(block, properties);
	}

	@Override
	protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
		BlockPos gangwayPartnerPos = null;
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		if(level.getBlockEntity(pos) instanceof GangwayFrameBlockEntity frame) {
			GangwayFrame partner = frame.getGangwayPartner();
			if(partner != null) {
				gangwayPartnerPos = partner.getBlockPos();
			}
		}
		boolean result = super.placeBlock(context, state);
		if(gangwayPartnerPos != null && level.getBlockEntity(pos) instanceof AutomaticCouplerBlockEntity coupler) {
			coupler.setGangwayPartnerReverse(gangwayPartnerPos);
		}
		return result;
	}
}
