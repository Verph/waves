package waves.common.entities;

import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import waves.client.particle.WaveParticleOption;
import waves.client.particle.WaveParticles;
import waves.common.WavesTags;
import waves.config.Config;
import waves.util.WaveHelpers;

public class Wave extends Entity
{
    public final Level level;
    public final Vec3 startPos;
    public final Vec3 shorePos;
    public final Vec3 direction;
    public final float scale;
    public final float size;
    public final float speed;
    public final int waveSize;

    public final double initialDistance;
    public boolean hasReachedShore = false;
    public boolean hasPlacedBlock = false;
    public int waveSpriteOld;

    public boolean onGround;
    public float bbWidth = 0.6F;
    public float bbHeight = 1.8F;
    public double x;
    public double y;
    public double z;
    public double xd;
    public double yd;
    public double zd;
    public int age;

    public Wave(EntityType<? extends Entity> entityType, Level level, Vec3 startPos, Vec3 shorePos, Vec3 direction, float scale, float size, float speed, int waveSize)
    {
        super(entityType, level);

        this.level = level;
        this.startPos = startPos;
        this.shorePos = shorePos;
        this.direction = direction;
        this.scale = scale;
        this.size = size;
        this.speed = speed;
        this.waveSize = waveSize;

        this.setPos(startPos);
        this.xd = direction.x() * speed;
        this.zd = direction.z() * speed;

        this.scale(scale);
        this.setSize(size, size);
        this.setInvulnerable(true);
        this.noPhysics = true;

        this.initialDistance = startPos.distanceTo(shorePos);
        this.waveSpriteOld = 0;
        this.age = 0;

        WaveParticleOption waveParticles = new WaveParticleOption(WaveParticles.WAVES.get(), startPos, shorePos, direction, scale, size, speed, waveSize);
        if (FMLEnvironment.dist == Dist.CLIENT && Minecraft.getInstance().level != null)
        {
            Minecraft.getInstance().level.addAlwaysVisibleParticle(waveParticles, true, startPos.x, startPos.y, startPos.z, xd, 0.0D, zd);
        }
    }

    @Override
    public void tick()
    {
        if (Config.COMMON.debug.get() && level.getGameTime() % 10 == 0)
        {
            level.addAlwaysVisibleParticle(ParticleTypes.SONIC_BOOM, true, this.getX() + (random.nextGaussian() * 0.1D), this.getY() + 0.5D + (random.nextDouble() * 0.1D), this.getZ() + (random.nextGaussian() * 0.1D), 0.0D, 0.0D, 0.0D);
        }

        final Vec3 currentPos = new Vec3(this.getX(), this.getY(), this.getZ());
        final BlockPos blockPos = WaveHelpers.toBlockPos(currentPos);
        double distance = currentPos.distanceTo(shorePos);
        boolean hasFluidBelow = level.getFluidState(blockPos.below()).isEmpty();
        int waveSprite = WaveHelpers.updateSprite(initialDistance, distance, 3.0D);

        double vecSpeed = new Vec3(xd, 0.0D, zd).length();

        if (!this.hasReachedShore && hasFluidBelow)
        {
            this.xd *= 0.95D;
            this.zd *= 0.95D;
            if (vecSpeed <= 0.01D)
            {
                this.hasReachedShore = true;
            }
        }

        this.move(MoverType.SELF, new Vec3(xd, 0.0D, zd));
        this.depositBlock(currentPos, waveSpriteOld);

        if (this.hasPlacedBlock || this.age > 600)
        {
            this.discard();
            this.kill();
        }

        if (waveSpriteOld < waveSprite)
        {
            waveSpriteOld = waveSprite;
        }
        this.age += 1;
    }

    public void depositBlock(Vec3 currentPos, int waveSprite)
    {
        if (!this.hasPlacedBlock && this.hasReachedShore && waveSprite >= Config.COMMON.waveSpriteCount.get() - 1)
        {
            this.hasPlacedBlock = true;
            RandomSource random = level.getRandom();
            if (random.nextInt(Config.COMMON.waveBlockDepositChance.get()) == 0)
            {
                BlockPos posDeposit = WaveHelpers.getRandomBlockPosAlongWave(level, random, currentPos, direction, size);
                BlockState stateDeposit = level.getBlockState(posDeposit);
                BlockState stateDepositBelow = level.getBlockState(posDeposit.below());
                Optional<Block> depositBlock = WaveHelpers.randomBlock(WavesTags.Blocks.TIDE_POOL_BLOCKS, random);
                if (!depositBlock.isEmpty())
                {
                    BlockState depositState = depositBlock.get().defaultBlockState();
                    if ((stateDeposit.isAir() || stateDeposit.canBeReplaced()) && stateDepositBelow.getFluidState().isEmpty() && depositState.canSurvive(level, posDeposit))
                    {
                        level.setBlockAndUpdate(posDeposit, depositState);
                    }
                }
            }
        }
    }

    public Wave scale(float scale)
    {
        this.setSize(0.2F * scale, 0.2F * scale);
        return this;
    }

    public void setSize(float width, float height)
    {
        if (width != this.bbWidth || height != this.bbHeight)
        {
            this.bbWidth = width;
            this.bbHeight = height;
            AABB aabb = this.getBoundingBox();
            double d0 = (aabb.minX + aabb.maxX - (double)width) / 2.0;
            double d1 = (aabb.minZ + aabb.maxZ - (double)width) / 2.0;
            this.setBoundingBox(new AABB(d0, aabb.minY, d1, d0 + (double)this.bbWidth, aabb.minY + (double)this.bbHeight, d1 + (double)this.bbWidth));
        }
    }

    @Override
    public Entity.MovementEmission getMovementEmission()
    {
        return Entity.MovementEmission.NONE;
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {}

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {}
}
