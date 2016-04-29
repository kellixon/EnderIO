package crazypants.enderio.machine.gauge;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cofh.api.energy.IEnergyHandler;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.BlockEio;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.render.ISmartRenderAwareBlock;
import crazypants.enderio.render.SmartModelAttacher;
import crazypants.enderio.render.TextureRegistry;
import crazypants.enderio.render.TextureRegistry.TextureSupplier;

public class BlockGauge extends BlockEio<TileGauge> implements IResourceTooltipProvider, ISmartRenderAwareBlock {

  public static final TextureSupplier gaugeIcon = TextureRegistry.registerTexture("blocks/blockGaugeOverlay");

  public static BlockGauge create() {
    BlockGauge result = new BlockGauge();
    result.init();
    return result;
  }

  private BlockGauge() {
    super(ModObject.blockGauge.getUnlocalisedName(), TileGauge.class, Material.glass);
    setLightOpacity(255);
    useNeighborBrightness = true;
  }

  @Override
  protected void init() {
    super.init();
    SmartModelAttacher.registerItemOnly(this);
  }

  @Override
  public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
    if (tab != null) {
      super.getSubBlocks(itemIn, tab, list);
    }
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public boolean isFullCube() {
    return false;
  }

  @Override
  public int getRenderType() {
    return -1;
  }

  private static final double px = 1d / 16d;

  @Override
  public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
    minY = 2 * px;
    maxY = 14 * px;

    Map<EnumFacing, IEnergyHandler> sides = getDisplays(world, pos);
    if (sides.isEmpty()) {
      minX = 0 * px;
      maxX = 1;
      minZ = 0;
      maxZ = 1;
      return;
    }

    minX = 16 * px;
    maxX = 0;
    minZ = 16 * px;
    maxZ = 0;
    if (sides.containsKey(EnumFacing.NORTH) || sides.containsKey(EnumFacing.SOUTH)) {
      minX = Math.min(minX, 6 * px);
      maxX = Math.max(maxX, 10 * px);
      if (sides.containsKey(EnumFacing.NORTH)) {
        minZ = Math.min(minZ, 0 * px);
        maxZ = Math.max(maxZ, .5 * px);
      }
      if (sides.containsKey(EnumFacing.SOUTH)) {
        minZ = Math.min(minZ, 15.5 * px);
        maxZ = Math.max(maxZ, 16 * px);
      }
    }
    if (sides.containsKey(EnumFacing.EAST) || sides.containsKey(EnumFacing.WEST)) {
      minZ = Math.min(minZ, 6 * px);
      maxZ = Math.max(maxZ, 10 * px);
      if (sides.containsKey(EnumFacing.WEST)) {
        minX = Math.min(minX, 0 * px);
        maxX = Math.max(maxX, 1.5 * px);
      }
      if (sides.containsKey(EnumFacing.EAST)) {
        minX = Math.min(minX, 15.5 * px);
        maxX = Math.max(maxX, 16 * px);
      }
    }

  }

  @Override
  public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {
    setBlockBoundsBasedOnState(world, pos);
    return super.getSelectedBoundingBox(world, pos);
  }

  protected static Map<EnumFacing, IEnergyHandler> getDisplays(IBlockAccess world, BlockPos pos) {
    Map<EnumFacing, IEnergyHandler> sides = new EnumMap<EnumFacing, IEnergyHandler>(EnumFacing.class);
    for (EnumFacing face : EnumFacing.Plane.HORIZONTAL) {
      BlockPos neighbor = pos.offset(face);
      TileEntity tile = world.getTileEntity(neighbor);
      if (tile instanceof IEnergyHandler && !(tile instanceof TileCapBank) && !(tile instanceof IConduitBundle)) {
        IEnergyHandler eh = (IEnergyHandler) tile;
        if (eh.canConnectEnergy(face.getOpposite())) {
          sides.put(face, eh);
        }
      }
    }
    return sides;
  }

  @Override
  public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
    return null;
  }

  @Override
  public IItemRenderMapper getItemRenderMapper() {
    return RenderMapperGauge.instance;
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName();
  }

}
