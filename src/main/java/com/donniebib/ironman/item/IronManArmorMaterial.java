package com.donniebib.ironman.item;

import com.donniebib.ironman.IronManMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.Map;

public final class IronManArmorMaterial {
    public static final int BASE_DURABILITY = 25;

    public static final ResourceKey<EquipmentAsset> ASSET_KEY = ResourceKey.create(
            EquipmentAssets.ROOT_ID,
            Identifier.fromNamespaceAndPath(IronManMod.MOD_ID, "iron_man")
    );

    public static final TagKey<Item> REPAIRS_IRON_MAN = TagKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath(IronManMod.MOD_ID, "repairs_iron_man")
    );

    public static final ArmorMaterial INSTANCE = new ArmorMaterial(
            BASE_DURABILITY,
            Map.of(
                    ArmorType.HELMET, 3,
                    ArmorType.CHESTPLATE, 0,
                    ArmorType.LEGGINGS, 0,
                    ArmorType.BOOTS, 0
            ),
            9,
            SoundEvents.ARMOR_EQUIP_IRON,
            1.0F,
            0.0F,
            REPAIRS_IRON_MAN,
            ASSET_KEY
    );

    private IronManArmorMaterial() {}
}
