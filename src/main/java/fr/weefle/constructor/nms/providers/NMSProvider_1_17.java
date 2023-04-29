package fr.weefle.constructor.nms.providers;

public class NMSProvider_1_17 extends NMSProvider {

    @Override
    public String[] getNBTTagCompound_getAllKeysMethodNames() {
        return new String[]{"getKeys"};
    }

    @Override
    public String[] getNBTTagCompound_putMethodNames() {
        return new String[]{"set"};
    }

    @Override
    public String[] getNBTTagCompound_putIntMethodNames() {
        return new String[]{"setInt"};
    }

    @Override
    public String[] getWorld_getBlockEntityMethodNames() {
        return new String[]{"getTileEntity"};
    }
}
