package com.donniebib.ironman.client;

import com.donniebib.ironman.item.IronManHelmetItem;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoArmorRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

public final class IronManHelmetRenderer extends GeoArmorRenderer<IronManHelmetItem, HumanoidRenderState> {
    private static final DataTicket<Float> MASK_PROGRESS =
            DataTicket.create("ironman_mask_progress", Float.class);

    public IronManHelmetRenderer(IronManHelmetItem item) {
        super(item);
    }

    @Override
    public void captureDefaultRenderState(IronManHelmetItem animatable, RenderData renderData,
                                          HumanoidRenderState renderState, float partialTick) {
        super.captureDefaultRenderState(animatable, renderData, renderState, partialTick);

        // GeckoLib injects GeoRenderState into Minecraft render-state classes at runtime.
        // Using the explicit interface cast keeps this consumer code robust even when the
        // compiler does not expose injected methods directly on HumanoidRenderState.
        GeoRenderState geoState = (GeoRenderState) (Object) renderState;
        geoState.addGeckolibData(MASK_PROGRESS,
                ClientHelmetStateManager.getOpenProgress(renderData.entity().getUUID(), partialTick));
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<HumanoidRenderState> renderPassInfo,
                                          BoneSnapshots snapshots) {
        super.adjustModelBonesForRender(renderPassInfo, snapshots);

        float p = renderPassInfo.getOrDefaultGeckolibData(MASK_PROGRESS, 0.0F);
        Pose pose = poseFor(clamp(p));

        snapshots.get("faceplate").ifPresent(snapshot -> snapshot
                .setTranslateX(pose.x)
                .setTranslateY(pose.y)
                .setTranslateZ(pose.z)
                .setRotX((float) Math.toRadians(pose.rotXDegrees)));
    }

    private static Pose poseFor(float p) {
        if (p <= 0.125F) {
            float t = smooth(p / 0.125F);
            return lerp(new Pose(0, 0, 0, 0), new Pose(0, 0, -0.8F, 0), t);
        }
        if (p <= 0.375F) {
            float t = smooth((p - 0.125F) / 0.25F);
            return lerp(new Pose(0, 0, -0.8F, 0), new Pose(0, 0.8F, -1.2F, -20F), t);
        }
        if (p <= 0.75F) {
            float t = smooth((p - 0.375F) / 0.375F);
            return lerp(new Pose(0, 0.8F, -1.2F, -20F), new Pose(0, 3.4F, -0.2F, -70F), t);
        }

        float t = smooth((p - 0.75F) / 0.25F);
        return lerp(new Pose(0, 3.4F, -0.2F, -70F), new Pose(0, 5.4F, 2.7F, -105F), t);
    }

    private static Pose lerp(Pose a, Pose b, float t) {
        return new Pose(
                a.x + (b.x - a.x) * t,
                a.y + (b.y - a.y) * t,
                a.z + (b.z - a.z) * t,
                a.rotXDegrees + (b.rotXDegrees - a.rotXDegrees) * t
        );
    }

    private static float smooth(float t) {
        t = clamp(t);
        return t * t * (3.0F - 2.0F * t);
    }

    private static float clamp(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }

    private record Pose(float x, float y, float z, float rotXDegrees) {}
}
