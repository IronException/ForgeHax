package com.matt.forgehax.mods;

import com.matt.forgehax.Helper;
import com.matt.forgehax.asm.events.PacketEvent;
import com.matt.forgehax.events.RenderEvent;
import com.matt.forgehax.util.color.Colors;
import com.matt.forgehax.util.mod.Category;
import com.matt.forgehax.util.mod.ToggleMod;
import com.matt.forgehax.util.mod.loader.RegisterMod;
import com.matt.forgehax.util.tesselation.GeometryMasks;
import com.matt.forgehax.util.tesselation.GeometryTessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

import static com.matt.forgehax.Helper.getLocalPlayer;
import static com.matt.forgehax.Helper.getWorld;

/**
 * Created on 9/4/2016 by fr1kin
 */
@RegisterMod
public class OpenContainerESP extends ToggleMod {

  ArrayList<BlockPos> viewed = new ArrayList<>();



  public OpenContainerESP() {
    super(Category.RENDER, "OpenContainerESP", false, "Shows not yet opened or changed storage");
  }
  
  private int getTileEntityColor(TileEntity tileEntity) {
    if (tileEntity instanceof TileEntityChest
        || tileEntity instanceof TileEntityDispenser
        || tileEntity instanceof TileEntityShulkerBox) {
      return Colors.RED.toBuffer();
    }else if (tileEntity instanceof TileEntityFurnace) {
      return Colors.RED.toBuffer();
    } else if (tileEntity instanceof TileEntityHopper) {
      return Colors.RED.toBuffer();
    } else {
      return -1;
    }
  }
  
  private int getEntityColor(Entity entity) {
    if (entity instanceof EntityMinecartChest) {
      return Colors.RED.toBuffer();
    } else if (entity instanceof EntityItemFrame
        && ((EntityItemFrame) entity).getDisplayedItem().getItem() instanceof ItemShulkerBox) {
      return Colors.RED.toBuffer();
    } else {
      return -1;
    }
  }



  @SubscribeEvent
  public void event(PlayerContainerEvent e){
    int windowId = e.getContainer().windowId;
    Helper.printInform(windowId + " " + e.getContainer().toString());
  }


  @SubscribeEvent
  public void onPacketReceived(PacketEvent.Outgoing.Pre event) {
    if (!(event.getPacket() instanceof CPacketCloseWindow))
        return;
    Helper.printInform("cpacketClose event");


    RayTraceUtils.rayTraceTowards(player(), playerRotations(), playerController().getBlockReachDistance());
    getLocalPlayer().rayTrace()

  }
  
  @SubscribeEvent
  public void onRender(RenderEvent event) {
    event.getBuffer().begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
    
    for (TileEntity tileEntity : getWorld().loadedTileEntityList) {
      BlockPos pos = tileEntity.getPos();
      
      int color = Colors.RED.toBuffer();
      if (viewed.contains(pos)) {
        GeometryTessellator.drawCuboid(event.getBuffer(), pos, GeometryMasks.Line.ALL, color);
      }
    }
    
    for (Entity entity : getWorld().loadedEntityList) {
      BlockPos pos = entity.getPosition();
      int color = getEntityColor(entity);
      if (color != -1) {
        GeometryTessellator.drawCuboid(
            event.getBuffer(),
            entity instanceof EntityItemFrame ? pos.add(0, -1, 0) : pos,
            GeometryMasks.Line.ALL,
            color);
      }
    }
    
    event.getTessellator().draw();
  }
}
