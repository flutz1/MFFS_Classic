package dev.su5ed.mffs.render.model;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.component.MapItemColor;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelLoader;
import org.jetbrains.annotations.Nullable;

public class PlayerHeadItemModel implements ItemModel {
    private final ItemModel originalModel;

    public PlayerHeadItemModel(ItemModel originalModel) {
        this.originalModel = originalModel;
    }

    @Override
    public void update(ItemStackRenderState itemStackRenderState, ItemStack itemStack, ItemModelResolver itemModelResolver, ItemDisplayContext itemDisplayContext, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int i) {
        this.originalModel.update(itemStackRenderState, itemStack, itemModelResolver, itemDisplayContext, clientLevel, livingEntity, i);

        if (livingEntity instanceof Player player) {
            // player ist jetzt verfügbar
            GameProfile profile = player.getGameProfile();
            // Player Info vom Client holen
            ClientPacketListener connection = Minecraft.getInstance().getConnection();
            if (connection != null) {
                PlayerInfo info = connection.getPlayerInfo(profile.getId());
                if (info != null) {
                    ResourceLocation skin = info.getSkin().texture();

                    // 1) TEST-Textur holen (z.B. Stone)
                    ResourceLocation texId = ResourceLocation.fromNamespaceAndPath(skin.getNamespace(), skin.getPath());
                    System.out.println("text id"+texId.toDebugFileName());
                    TextureAtlas textureAtlas = Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS);
                    TextureAtlasSprite sprite2 = textureAtlas.getSprite(MissingTextureAtlasSprite.getLocation());

                    // 2) Einen einfachen TEST-Quad erzeugen (2D)
                    BakedQuad quad = createTestQuad(sprite2);
                    // skin kannst du jetzt auf dein Item legen+++
                    itemStackRenderState.appendModelIdentityElement(this);
                    // Create a new layer
                    ItemStackRenderState.LayerRenderState layerState = itemStackRenderState.newLayer();
                    layerState.clear();
                    layerState.prepareQuadList().add(quad);
                    System.out.println("Kontext "+ itemDisplayContext);

                    layerState.setFoilType(ItemStackRenderState.FoilType.SPECIAL);
                    MapItemColor
                  //  this.originalModel.update(itemStackRenderState,itemStack,itemModelResolver,itemDisplayContext,clientLevel,livingEntity,i);
                }
            }

        }
    }
    private BakedQuad createTestQuad(TextureAtlasSprite sprite) {
        int[] vertices = new int[28]; // 4 Vertices × 7 ints
        int packedLight = 0xF000F0;

        // Einfache 2D Fläche von 0..1
        float x1 = 0f, x2 = 1f;
        float y1 = 0f, y2 = 1f;

        // UV aus Sprite
        float u1 = sprite.getU0();
        float u2 = sprite.getU1();
        float v1 = sprite.getV0();
        float v2 = sprite.getV1();

        int i = 0;
        i = putVertex(vertices, i, x1, y1, 0, u1, v1, packedLight);
        i = putVertex(vertices, i, x2, y1, 0, u2, v1, packedLight);
        i = putVertex(vertices, i, x2, y2, 0, u2, v2, packedLight);
        i = putVertex(vertices, i, x1, y2, 0, u1, v2, packedLight);

        return new BakedQuad(vertices, -1, Direction.NORTH, sprite, true, 0, true);
    }

    private int putVertex(int[] data, int index,
                          float x, float y, float z,
                          float u, float v,
                          int light) {

        data[index++] = Float.floatToRawIntBits(x);
        data[index++] = Float.floatToRawIntBits(y);
        data[index++] = Float.floatToRawIntBits(z);
        data[index++] = Float.floatToRawIntBits(u);
        data[index++] = Float.floatToRawIntBits(v);
        data[index++] = light;
        data[index++] = 0; // normal (nicht verwendet)
        return index;
    }

}
