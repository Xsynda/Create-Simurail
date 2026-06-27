package com.crystaelix.simurail.content.gangway_frame;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.joml.Vector3d;
import org.joml.Vector3dc;

import com.crystaelix.simurail.api.math.Quad3d;
import com.crystaelix.simurail.api.math.SimurailMath;
import com.crystaelix.simurail.api.util.SchematicContextUtil;
import com.crystaelix.simurail.content.SimurailSoundEvents;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GangwayFrameBlockEntity extends SmartBlockEntity implements GangwayFrame {

	protected BlockPos gangwayPartnerPos;
	protected UUID gangwayPartnerSubLevelID;

	protected Quad3d lastPartnerQuadOffset = new Quad3d();
	protected Vector3d lastPartnerCenterOffset = new Vector3d();
	protected Vector3d lastPartnerDir = new Vector3d();
	protected boolean hasPartner = false;
	public int timer = 0;

	protected VoxelShape collisionShape = Shapes.empty();

	public GangwayFrameBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
		super(type, pos, blockState);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	@Override
	public Direction getFacing() {
		return getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
	}

	@Override
	public GangwayFrameShape getGangwayShape() {
		return getBlockState().getValue(GangwayFrameBlock.SHAPE);
	}

	@Override
	public Vector3d getGangwayCenter(Vector3d dest) {
		BlockPos pos = getBlockPos();
		return getGangwayShape().center(getFacing(), dest).add(pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public Vector3dc getDirection() {
		return switch(getFacing()) {
		case EAST -> SimurailMath.DIR_XP;
		case WEST -> SimurailMath.DIR_XN;
		case SOUTH -> SimurailMath.DIR_ZP;
		case NORTH -> SimurailMath.DIR_ZN;
		case null, default -> throw new IllegalArgumentException("Unexpected value: " + getFacing());
		};
	}

	@Override
	public boolean isPowered() {
		return getBlockState().getValue(BlockStateProperties.POWERED);
	}

	@Override
	public boolean isGangwayPowered() {
		if(isPowered()) {
			return true;
		}
		return GangwayFrame.getNeighbors(this, level, 15).stream().anyMatch(GangwayFrame::isPowered);
	}

	@Override
	public void setGangwayPartner(BlockPos gangwayPartnerPos) {
		if(gangwayPartnerPos.equals(this.gangwayPartnerPos)) {
			return;
		}
		if(level.getBlockEntity(gangwayPartnerPos) instanceof GangwayFrame partner &&
				getGangwayShape().connectsTo() == partner.getGangwayShape()) {
			SubLevel partnerSubLevel = Sable.HELPER.getContaining(level, gangwayPartnerPos);
			if(this.gangwayPartnerPos != null) {
				removeGangwayPartner();
			}
			this.gangwayPartnerPos = gangwayPartnerPos;
			gangwayPartnerSubLevelID = partnerSubLevel == null ? null : partnerSubLevel.getUniqueId();
			partner.setGangwayPartnerReverse(getBlockPos());
			if(!level.isClientSide()) {
				setChanged();
				sendData();
				level.playSound(null, getBlockPos(), SimurailSoundEvents.GANGWAY_CONNECT.get(), SoundSource.BLOCKS, 0.25F, 1F);
				level.playSound(null, gangwayPartnerPos, SimurailSoundEvents.GANGWAY_CONNECT.get(), SoundSource.BLOCKS, 0.25F, 1F);
			}
		}
	}

	@Override
	public void setGangwayPartnerReverse(BlockPos gangwayPartnerPos) {
		this.gangwayPartnerPos = gangwayPartnerPos;
		if(gangwayPartnerPos != null) {
			SubLevel partnerSubLevel = Sable.HELPER.getContaining(level, gangwayPartnerPos);
			gangwayPartnerSubLevelID = partnerSubLevel == null ? null : partnerSubLevel.getUniqueId();
		}
		else {
			gangwayPartnerSubLevelID = null;
		}
		if(!level.isClientSide()) {
			setChanged();
			sendData();
		}
	}

	@Override
	public void removeGangwayPartner() {
		if(gangwayPartnerPos == null) {
			return;
		}
		if(gangwayPartnerPos != null && level.getBlockEntity(gangwayPartnerPos) instanceof GangwayFrame partner) {
			partner.setGangwayPartnerReverse(null);
			if(!level.isClientSide()) {
				level.playSound(null, gangwayPartnerPos, SimurailSoundEvents.GANGWAY_DISCONNECT.get(), SoundSource.BLOCKS, 0.25F, 1F);
			}
		}
		gangwayPartnerPos = null;
		gangwayPartnerSubLevelID = null;
		if(!level.isClientSide()) {
			setChanged();
			sendData();
			level.playSound(null, getBlockPos(), SimurailSoundEvents.GANGWAY_DISCONNECT.get(), SoundSource.BLOCKS, 0.25F, 1F);
		}
	}

	@Override
	public GangwayFrame getGangwayPartner() {
		if(gangwayPartnerPos != null && level.getBlockEntity(gangwayPartnerPos) instanceof GangwayFrame partner) {
			return partner;
		}
		return null;
	}

	public void afterMove() {
		if(gangwayPartnerPos != null) {
			setGangwayPartner(gangwayPartnerPos);
		}
	}

	@Override
	public void initialize() {
		super.initialize();
		hasPartner = getGangwayPartner() != null;
		collisionShape = getBlockState().getShape(level, gangwayPartnerPos);
	}

	@Override
	public void tick() {
		super.tick();
		GangwayFrame partner = getGangwayPartner();
		if(partner != null) {
			BlockPos selfPos = getBlockPos();
			BlockPos partnerPos = partner.getBlockPos();

			SubLevel selfSubLevel = Sable.HELPER.getContaining(this);
			SubLevel partnerSubLevel = Sable.HELPER.getContaining(level, partnerPos);
			Pose3dc selfPose = selfSubLevel == null ? SimurailMath.POSE_I : selfSubLevel.logicalPose();
			Pose3dc partnerPose = partnerSubLevel == null ? SimurailMath.POSE_I : partnerSubLevel.logicalPose();

			partner.getGangwayShape().quad(partner.getFacing(), lastPartnerQuadOffset);
			lastPartnerQuadOffset.add(partnerPos.getX(), partnerPos.getY(), partnerPos.getZ());
			partner.getGangwayCenter(lastPartnerCenterOffset);
			lastPartnerDir.set(partner.getDirection());

			lastPartnerQuadOffset.transformPosition(partnerPose);
			partnerPose.transformPosition(lastPartnerCenterOffset);
			partnerPose.transformNormal(lastPartnerDir);

			lastPartnerQuadOffset.transformPositionInverse(selfPose);
			lastPartnerQuadOffset.sub(selfPos.getX(), selfPos.getY(), selfPos.getZ());
			selfPose.transformPositionInverse(lastPartnerCenterOffset);
			lastPartnerCenterOffset.sub(selfPos.getX(), selfPos.getY(), selfPos.getZ());
			selfPose.transformNormalInverse(lastPartnerDir);

			getGangwayShape().center(getFacing(), centerOffset);

			double x = lastPartnerCenterOffset.x() - centerOffset.x();
			double y = lastPartnerCenterOffset.y() - centerOffset.y();
			double z = lastPartnerCenterOffset.z() - centerOffset.z();
			double length = getDirection().dot(x, y, z) * 0.625;

			collisionShape = getGangwayShape().getShapeForLength(getFacing(), length * 16);
		}
		else {
			collisionShape = getBlockState().getShape(level, getBlockPos());
		}
		if(partner != null != hasPartner) {
			hasPartner = partner != null;
			timer = 10 - timer;
		}
		if(timer > 0) {
			timer--;
		}
	}

	@Override
	public void lazyTick() {
		if(!level.isClientSide()) {
			double maxDist = 6;
			if(gangwayPartnerPos != null) {
				if(level.getBlockEntity(gangwayPartnerPos) instanceof GangwayFrame partner) {
					SubLevel selfSubLevel = Sable.HELPER.getContaining(this);
					SubLevel otherSubLevel = Sable.HELPER.getContaining(level, gangwayPartnerPos);
					if(selfSubLevel != otherSubLevel && Sable.HELPER.distanceSquaredWithSubLevels(level, getBlockPos().getCenter(), gangwayPartnerPos.getCenter()) > maxDist * maxDist) {
						removeGangwayPartner();
					}
					else if(partner.getGangwayPartner() != this) {
						removeGangwayPartner();
					}
				}
				else {
					removeGangwayPartner();
				}
			}
		}
	}

	public void tryConnectGangway() {
		if(isGangwayPowered()) {
			return;
		}
		GangwayFrame partner = GangwayFrame.findGangwayPartner(this, level);
		if(partner == null) {
			return;
		}
		setGangwayPartner(partner.getBlockPos());
		Set<GangwayFrame> visited = new HashSet<>();
		Couple<GangwayFrame> selfCouple = Couple.create(this, this);
		Couple<GangwayFrame> partnerCouple = Couple.create(partner, partner);
		for(int i = 0; i < 15; ++i) {
			for(boolean cw : Iterate.trueAndFalse) {
				if(selfCouple.get(cw) != null) {
					GangwayFrame cSelf = selfCouple.get(cw);
					GangwayFrame cPartner = partnerCouple.get(cw);
					GangwayFrameShape selfShape = cSelf.getGangwayShape();
					GangwayFrameShape partnerShape = cPartner.getGangwayShape();
					Direction selfOffset = selfShape.adjacentOffset(cSelf.getFacing(), cw);
					Direction partnerOffset = partnerShape.adjacentOffset(cPartner.getFacing(), !cw);
					BlockPos selfPos = cSelf.getBlockPos().relative(selfOffset);
					BlockPos partnerPos = cPartner.getBlockPos().relative(partnerOffset);
					if(level.getBlockEntity(selfPos) instanceof GangwayFrame selfNeighbor &&
							!visited.contains(selfNeighbor) &&
							selfShape.adjacentTo(cw).contains(selfNeighbor.getGangwayShape()) &&
							level.getBlockEntity(partnerPos) instanceof GangwayFrame partnerNeighbor &&
							selfNeighbor.getGangwayShape().connectsTo() == partnerNeighbor.getGangwayShape()) {
						visited.add(selfNeighbor);
						selfCouple.set(cw, selfNeighbor);
						partnerCouple.set(cw, partnerNeighbor);
						selfNeighbor.setGangwayPartner(partnerPos);
					}
					else {
						selfCouple.set(cw, null);
						partnerCouple.set(cw, null);
					}
				}
			}
		}
	}

	public void tryDisconnectGangway() {
		removeGangwayPartner();
		GangwayFrame.getNeighbors(this, level, 15).forEach(GangwayFrame::removeGangwayPartner);
	}

	@Override
	protected AABB createRenderBoundingBox() {
		return super.createRenderBoundingBox().inflate(4);
	}

	@Override
	public void setBlockState(BlockState blockState) {
		GangwayFrameShape oldShape = getGangwayShape();
		super.setBlockState(blockState);
		GangwayFrameShape newShape = getGangwayShape();
		if(newShape != oldShape) {
			removeGangwayPartner();
		}
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);

		Pair<BlockPos, UUID> gangwayPartner = SchematicContextUtil.writeTransform(gangwayPartnerPos, gangwayPartnerSubLevelID);

		if(gangwayPartner.getFirst() != null) {
			tag.put("gangway_partner", NbtUtils.writeBlockPos(gangwayPartner.getFirst()));
			if(gangwayPartner.getSecond() != null) {
				tag.putUUID("gangway_partner_id", gangwayPartner.getSecond());
			}
		}
	}

	@Override
	public void writeSafe(CompoundTag tag, Provider registries) {
		super.writeSafe(tag, registries);
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);

		Pair<BlockPos, UUID> gangwayPartner = SchematicContextUtil.readTransform(
				NbtUtils.readBlockPos(tag, "gangway_partner").orElse(null),
				tag.hasUUID("gangway_partner_id") ? tag.getUUID("gangway_partner_id") : null);

		gangwayPartnerPos = gangwayPartner.getFirst();
		gangwayPartnerSubLevelID = gangwayPartner.getSecond();
	}

	protected final Vector3d centerOffset = new Vector3d();
}
