package waves.client.particle;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import waves.client.ClientHelpers;
import waves.client.WaveSounds;
import waves.config.Config;
import waves.util.WaveHelpers;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("null")
public class WaveParticle extends TextureSheetParticle
{
    public Vec3 startPos;
    public Vec3 shorePos;
    public Vec3 direction;
    public float size;
    public float speed;
    public double initialDistance;
    public double angleToShore;
    public int waveSize;
    public static double minDistance = Double.MAX_VALUE;
    public int waveSpriteOld;
    public double distanceOld;
    public double waveSpeedOld;
    public ResourceLocation waveTexture;
    public boolean hasReachedShore = false;

    public WaveParticle(ClientLevel level, Vec3 startPos, Vec3 shorePos, Vec3 direction, float scale, float size, float speed, int waveSize)
    {
        super(level, startPos.x(), startPos.y(), startPos.z());

        this.waveTexture = WaveHelpers.identifier("textures/particle/waves/" + WaveHelpers.toSize(waveSize).toLowerCase() + "_0.png");
        this.scale(scale);
        this.setSize(size, size);
        this.xd = direction.x() * speed;
        this.zd = direction.z() * speed;
        this.quadSize = size * 0.5F;
        this.lifetime = Integer.MAX_VALUE;
        this.gravity = 0.0F;
        this.alpha = 0.0F;
        this.hasPhysics = false;
        this.startPos = startPos;
        this.shorePos = shorePos;
        this.direction = direction;
        this.size = size;
        this.speed = speed;
        this.initialDistance = startPos.distanceTo(shorePos);
        this.angleToShore = WaveHelpers.calculateAngleToTarget(startPos, shorePos);
        this.waveSize = waveSize;
        this.waveSpriteOld = 0;
        this.distanceOld = Double.MAX_VALUE;
    }

    @Override
    public void tick()
    {
        final Vec3 currentPos = new Vec3(this.x, this.y, this.z);
        final BlockPos blockPos = WaveHelpers.toBlockPos(currentPos);
        double distance = currentPos.distanceTo(shorePos);
        boolean hasFluidBelow = level.getFluidState(blockPos.below()).isEmpty();

        int waveSprite = WaveHelpers.updateSprite(initialDistance, distance, 3.0D);
        if (waveSpriteOld < waveSprite)
        {
            this.waveTexture = WaveHelpers.identifier("textures/particle/waves/" + WaveHelpers.toSize(waveSize).toLowerCase() + "_" + waveSprite + ".png");
        }

        double vecSpeed = new Vec3(xd, 0.0D, zd).length();
        boolean shouldDecay = hasReachedShore || vecSpeed == 0 || waveSprite >= Config.COMMON.waveSpriteCount.get() - 1;

        if (!shouldDecay)
        {
            this.setAlpha(Mth.clamp(this.alpha + 0.025F, 0.0F, 1.0F));
        }

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (!hasReachedShore && hasFluidBelow)
        {
            this.xd *= 0.95D;
            this.zd *= 0.95D;
            if (vecSpeed <= 0.01D)
            {
                this.hasReachedShore = true;
                this.setAlpha(1.0F);
                Vec3 vecInverse = WaveHelpers.inverse(direction);
                this.zd = vecInverse.z() * 0.015D;
                this.xd = vecInverse.x() * 0.015D;
            }
        }
        if (hasReachedShore && currentPos.distanceTo(shorePos) <= 2.0D)
        {
            double speedFactor = 1.0D + (0.05D - WaveHelpers.applyEaseOutQuart(direction, distance, 5.0D, 0.5D, 1.0D).length() * 0.02D);
            this.xd *= speedFactor;
            this.zd *= speedFactor;
        }
        if ((!hasFluidBelow || waveSpeedOld == vecSpeed || distance > distanceOld) && hasReachedShore)
        {
            this.setAlpha(Mth.clamp(this.alpha - 0.1F, 0.0F, 1.0F));
        }

        this.move(xd, 0.0D, zd);

        if (this.alpha <= 0.01F || this.age > 600)
        {
            this.remove();
        }

        this.age += 1;

        waveSpeedOld = vecSpeed;
        distanceOld = distance;
        if (waveSpriteOld <= waveSprite)
        {
            waveSpriteOld = waveSprite;
        }

        if (random.nextFloat() < Config.COMMON.waveBreakingSoundChance.get().floatValue())
        {
            float soundMod = (float) WaveHelpers.soundDistanceMod(Minecraft.getInstance().player, shorePos, 60.0D, 10.0D);
            if (soundMod > 0.0F)
            {
                float rainfall = 1.0F + level.getRainLevel(1.0F);
                WaveHelpers.playSound(level, random, WaveHelpers.toBlockPos(shorePos), WaveSounds.WAVES_BREAKING.get(), SoundSource.AMBIENT, true, rainfall * 0.7F * soundMod);
            }
        }

        if (Config.COMMON.debug.get())
        {
            if (level.getGameTime() % (level.getRandom().nextInt(10) + 1) == 0)
            {
                Player player = Minecraft.getInstance().player;
                Vec3 lookAngle = player.getLookAngle().scale(10.0D);
                Vec3 playerPos = player.getEyePosition().add(lookAngle);
                level.addParticle(ParticleTypes.FLAME, playerPos.x(), playerPos.y(), playerPos.z(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public ParticleRenderType getRenderType()
    {
        return ClientHelpers.PARTICLE_SHEET_TEX_TRANSLUCENT;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        Vec3 wavePos = new Vec3(this.x, this.y, this.z);

        if (WaveHelpers.isWithinAngle(player.getLookAngle(), wavePos, minecraft.options.fov().get() + 5) && player.getEyePosition().distanceTo(wavePos) <= minecraft.options.renderDistance().get().doubleValue() * 16.0D)
        {
            ProfilerFiller profiler = Minecraft.getInstance().level.getProfiler();
            profiler.push("wave");
            PoseStack poseStack = new PoseStack();

            poseStack.pushPose();
            Matrix4f matrix = poseStack.last().pose();

            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.depthMask(true);
            RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
            RenderSystem.setShaderTexture(0, waveTexture);

            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);

            Vec3 vec3 = camera.getPosition();
            float x = (float) (Mth.lerp(partialTicks, this.xo, this.x) - vec3.x());
            float y = (float) (Mth.lerp(partialTicks, this.yo, this.y) - vec3.y());
            float z = (float) (Mth.lerp(partialTicks, this.zo, this.z) - vec3.z());

            Quaternionf quaternion = new Quaternionf(0, 0, 0, 1).mul(Axis.YN.rotation((float) (angleToShore - (Math.PI / 2.0D)))).mul(Axis.XP.rotationDegrees(90));

            Vector3f[] avector3 = new Vector3f[]{
                new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F)
            };
            float quadSize = this.getQuadSize(partialTicks);

            for (int i = 0; i < 4; ++i)
            {
                Vector3f vector3 = avector3[i];
                vector3.rotate(quaternion);
                vector3.mul(quadSize);
                vector3.add(x, y, z);
            }

            float u0 = 0.0F;
            float u1 = 1.0F;
            float v0 = 0.0F;
            float v1 = 1.0F;

            double dayTime = level.getDayTime() * Config.COMMON.waveBioluminescenceFrequency.get();
            double speed = new Vec3(xd, 0.0D, zd).length() * 10.0D;
            double skyBrightness = WaveHelpers.getSkyBrightness(this.level, partialTicks);
            double bioluminescenceNoise = Mth.clamp(WaveHelpers.bioluminescenceNoise(level, 10, Config.COMMON.waveBioluminescenceChange.get()).noise(dayTime + this.x, dayTime + this.z) * speed, 0.0D, 1.0D) * (1.0D - skyBrightness);
            bioluminescenceNoise = bioluminescenceNoise < 0.05D ? 0.0D : bioluminescenceNoise;

            float r = (float) Mth.clamp(skyBrightness, 0.08D, 1.0D);
            float g = (float) Mth.clamp(skyBrightness + (bioluminescenceNoise * 0.8D), 0.08D, 1.0D);
            float b = (float) Mth.clamp(skyBrightness + bioluminescenceNoise, 0.08D, 1.0D);
            float a = Mth.clamp(this.alpha, 0.0F, 1.0F);

            BlockPos posBehind = WaveHelpers.toBlockPos(WaveHelpers.getPositionOffset(wavePos, angleToShore, 180.0D, 6.0D));
            int j = (int) Math.round(Mth.clamp((level.isLoaded(posBehind) ? LevelRenderer.getLightColor(level, posBehind) : this.getLightColor(partialTicks)) + (10000000.0D * bioluminescenceNoise), 0.0D, 15728880.0D));

            builder.addVertex(matrix, avector3[0].x(), avector3[0].y(), avector3[0].z()).setColor(r, g, b, a).setUv(u1, v1).setLight(j);
            builder.addVertex(matrix, avector3[1].x(), avector3[1].y(), avector3[1].z()).setColor(r, g, b, a).setUv(u1, v0).setLight(j);
            builder.addVertex(matrix, avector3[2].x(), avector3[2].y(), avector3[2].z()).setColor(r, g, b, a).setUv(u0, v0).setLight(j);
            builder.addVertex(matrix, avector3[3].x(), avector3[3].y(), avector3[3].z()).setColor(r, g, b, a).setUv(u0, v1).setLight(j);

            BufferUploader.drawWithShader(builder.buildOrThrow());
            poseStack.popPose();
            profiler.pop();
        }
    }

    public static ParticleProvider<WaveParticleOption> provider(SpriteSet set)
    {
        return (type, level, x, y, z, dx, dy, dz) -> {
            WaveParticle particle = new WaveParticle(level, type.getStartPos(), type.getShorePos(), type.getDirection(), type.getScale(), type.getSize(), type.getSpeed(), type.getWaveSize());
            particle.setPos(type.getStartPos().x, type.getStartPos().y, type.getStartPos().z);
            return particle;
        };
    }
}
