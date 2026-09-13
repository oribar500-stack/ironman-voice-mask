package com.donniebib.ironman.item;

import com.donniebib.ironman.client.IronManHelmetRenderer;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public final class IronManHelmetItem extends Item implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public IronManHelmetItem(Properties properties) {
        super(properties);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private IronManHelmetRenderer renderer;

            @Override
            public GeoArmorRenderer<?, ?> getGeoArmorRenderer(ItemStack itemStack, EquipmentSlot equipmentSlot) {
                if (equipmentSlot != EquipmentSlot.HEAD) {
                    return null;
                }

                if (renderer == null) {
                    renderer = new IronManHelmetRenderer(IronManHelmetItem.this);
                }

                return renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // The synchronized helmet state directly drives the faceplate bone.
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
