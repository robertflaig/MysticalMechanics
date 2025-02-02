package mysticalmechanics.util;

import mysticalmechanics.api.IGearBehavior;
import mysticalmechanics.api.IGearData;
import mysticalmechanics.api.IMechCapability;
import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;
import java.util.LinkedList;

public class VarGearBehavior implements IGearBehavior {
    static class Data implements IGearData {
        LinkedList<Double> lastPower = new LinkedList<>();

        public double sum() {
            double sum = 0;
            for (double power : lastPower) {
                sum += power;
            }
            return sum;
        }

        public int size() {
            return lastPower.size();
        }

        public void add(double power) {
            lastPower.add(power);
            if(lastPower.size() > 60)
                lastPower.removeFirst();
        }

        @Override
        public void readFromNBT(CompoundNBT tag) {
            ListNBT tagPowers = tag.getTagList("lastPower", 6);
            lastPower.clear();
            for (INBT base : tagPowers) {
                lastPower.add(((NBTTagDouble)base).getDouble());
            }
        }

        @Override
        public CompoundNBT writeToNBT(CompoundNBT tag) {
            ListNBT tagPowers = new ListNBT();
            for (double power : lastPower) {
                tagPowers.add(new NBTTagDouble(power));
            }
            tag.setTag("lastPower", tagPowers);
            return tag;
        }

        @Override
        public boolean isDirty() {
            return true;
        }
    }

    @Override
    public double transformPower(TileEntity tile, @Nullable Direction facing, ItemStack gear, IGearData data, double power) {
        if(data instanceof Data) {
            Data lastPower = (Data) data;
            if(lastPower.size() == 0)
                return 0;
            double sum = lastPower.sum();
            return sum / (lastPower.size());
        }
        return 0;
    }

    @Override
    public double transformVisualPower(TileEntity tile, @Nullable Direction facing, ItemStack gear, IGearData data, double power) {
        if(data instanceof Data) {
            Data lastPower = (Data) data;
            if(lastPower.size() == 0)
                return 0;
            double sum = lastPower.sum();
            return sum / (lastPower.size());
        }
        return 0;
    }

    @Override
    public void tick(TileEntity tile, @Nullable Direction facing, ItemStack gear, IGearData data, double powerIn, double powerOut) {
        if(data instanceof Data) {
            ((Data) data).add(powerIn);
        }
    }

    @Override
    public boolean hasData() {
        return true;
    }

    @Override
    public IGearData createData() {
        return new Data();
    }
}
