package com.donniebib.ironman.item;

import com.donniebib.ironman.IronManMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.function.Function;

public final class ModItems {
    public static final ResourceKey<Item> IRON_MAN_HELMET_KEY = ResourceKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath(IronManMod.MOD_ID, "iron_man_helmet")
    );

    public static final IronManHelmetItem IRON_MAN_HELMET = (IronManHelmetItem) register(
            IRON_MAN_HELMET_KEY,
            IronManHelmetItem::new,
            new Item.Properties()
                    .humanoidArmor(IronManArmorMaterial.INSTANCE, ArmorType.HELMET)
                    .durability(ArmorType.HELMET.getDurability(IronManArmorMaterial.BASE_DURABILITY))
    );

    private static Item register(ResourceKey<Item> key, Function<Item.Properties, Item> factory, Item.Properties properties) {
        Item item = factory.apply(properties.setId(key));
        Registry.register(BuiltInRegistries.ITEM, key, item);
        return item;
    }

    public static void initialize() {}
    private ModItems() {}
}
