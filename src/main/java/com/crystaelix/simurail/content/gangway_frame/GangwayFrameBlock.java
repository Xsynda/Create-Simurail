package com.crystaelix.simurail.content.gangway_frame;

import com.crystaelix.simurail.content.SimurailBlockEntities;
import com.crystaelix.simurail.content.SimurailBlocks;
import com.crystaelix.simurail.content.automatic_coupler.AutomaticCouplerBlock;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;

import dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener;
import dev.ryanhcode.sable.api.block.BlockSubLevelCollisionShape;
import dev.ryanhcode.sable.api.physics.collider.SableCollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GangwayFrameBlock extends HorizontalDirectionalBlock implements IBE<GangwayFrameBlockEntity>, BlockSubLevelCollisionShape, BlockSubLevelAssemblyListener, ProperWaterloggedBlock, IWrenchable {

	public static final MapCodec<GangwayFrameBlock> CODEC = simpleCodec(GangwayFrameBlock::new);
	public static final EnumProperty<GangwayFrameShape> SHAPE = EnumProperty.create("shape", GangwayFrameShape.class, GangwayFrameShape.NORMAL);
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public static final double PI_DIV_6 = Math.PI / 6;

	public GangwayFrameBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(SHAPE, GangwayFrameShape.U).setValue(POWERED, false).setValue(WATERLOGGED, false));
	}

	@Override
	protected MapCodec<GangwayFrameBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, SHAPE, POWERED, WATERLOGGED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Level level = context.getLevel();
		BlockState oldState = level.getBlockState(context.getClickedPos());
		if(oldState.is(SimurailBlocks.AUTOMATIC_COUPLER) && oldState.getValue(AutomaticCouplerBlock.GANGWAY_SHAPE) == GangwayFrameShape.NONE) {
			double hitY = context.getClickLocation().y - context.getClickedPos().getY();
			GangwayFrameShape shape = hitY > 0.5 ? GangwayFrameShape.U : GangwayFrameShape.D;
			return oldState.setValue(AutomaticCouplerBlock.GANGWAY_SHAPE, shape);
		}

		Direction direction = context.getClickedFace();
		GangwayFrameShape shape;
		if(direction.getAxis() == Direction.Axis.Y) {
			shape = direction == Direction.UP ? GangwayFrameShape.D : GangwayFrameShape.U;
			direction = context.getHorizontalDirection().getOpposite();
		}
		else {
			double x = switch(direction) {
			case SOUTH -> context.getClickLocation().x - context.getClickedPos().getX() - 0.5;
			case NORTH -> context.getClickedPos().getX() - context.getClickLocation().x + 0.5;
			case WEST -> context.getClickLocation().z - context.getClickedPos().getZ() - 0.5;
			case EAST -> context.getClickedPos().getZ() - context.getClickLocation().z + 0.5;
			default -> throw new IllegalArgumentException("Unexpected value: " + direction);
			};
			double y = context.getClickLocation().y - context.getClickedPos().getY() - 0.5;
			double angle = Math.atan2(y, x);
			if     (angle > PI_DIV_6 * 5)  shape = GangwayFrameShape.L;
			else if(angle > PI_DIV_6 * 4)  shape = GangwayFrameShape.LU;
			else if(angle > PI_DIV_6 * 2)  shape = GangwayFrameShape.U;
			else if(angle > PI_DIV_6 * 1)  shape = GangwayFrameShape.UR;
			else if(angle > -PI_DIV_6 * 1) shape = GangwayFrameShape.R;
			else if(angle > -PI_DIV_6 * 2) shape = GangwayFrameShape.RD;
			else if(angle > -PI_DIV_6 * 4) shape = GangwayFrameShape.D;
			else if(angle > -PI_DIV_6 * 5) shape = GangwayFrameShape.DL;
			else                           shape = GangwayFrameShape.L;
		}
		BlockState state = defaultBlockState().
				setValue(FACING, direction).
				setValue(SHAPE, shape).
				setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
		return withWater(state, context);
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		updateWater(level, state, pos);
		return state;
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return fluidState(state);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getValue(SHAPE).getShape(state.getValue(FACING));
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		if(!(context instanceof SableCollisionContext) && level.getBlockEntity(pos) instanceof GangwayFrameBlockEntity be && be.collisionShape != null) {
			return be.collisionShape;
		}
		return state.getShape(level, pos, context);
	}

	@Override
	protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getShape(level, pos, context);
	}

	@Override
	public VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
		return state.getShape(level, pos);
	}

	@Override
	public VoxelShape getSubLevelCollisionShape(BlockGetter level, BlockState state) {
		return state.getValue(SHAPE).getSubLevelShape(state.getValue(FACING));
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
		if(level.isClientSide()) {
			return;
		}
		boolean previouslyPowered = state.getValue(POWERED);
		boolean isPowered = level.hasNeighborSignal(pos);
		if(previouslyPowered != isPowered) {
			level.setBlock(pos, state.cycle(POWERED), Block.UPDATE_CLIENTS);
			if(isPowered) {
				withBlockEntityDo(level, pos, GangwayFrameBlockEntity::tryDisconnectGangway);
			}
		}
	}

	@Override
	protected boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
		return useContext.getItemInHand().is(SimurailBlocks.AUTOMATIC_COUPLER.asItem());
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if(stack.isEmpty()) {
			if(!level.isClientSide()) {
				withBlockEntityDo(level, pos, be -> {
					if(be.getGangwayPartner() == null) {
						be.tryConnectGangway();
					}
					else {
						be.tryDisconnectGangway();
					}
				});
			}
			return ItemInteractionResult.SUCCESS;
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		level.setBlock(pos, state.setValue(SHAPE, state.getValue(SHAPE).nextWrenchShape()), Block.UPDATE_ALL);
		IWrenchable.playRotateSound(level, pos);
		return InteractionResult.SUCCESS;
	}

	@Override
	public Class<GangwayFrameBlockEntity> getBlockEntityClass() {
		return GangwayFrameBlockEntity.class;
	}

	@Override
	public BlockEntityType<GangwayFrameBlockEntity> getBlockEntityType() {
		return SimurailBlockEntities.GANGWAY_FRAME.get();
	}

	@Override
	public void afterMove(ServerLevel originLevel, ServerLevel resultingLevel, BlockState newState, BlockPos oldPos, BlockPos newPos) {
		withBlockEntityDo(resultingLevel, newPos, GangwayFrameBlockEntity::afterMove);
	}
}
