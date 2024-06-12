package studio.magemonkey.blueprint.nms.providers;

// TODO Verify if this even works properly
public class NMSProvider_1_20 extends NMSProvider {

    @Override
    public String[] getNBTTagCompound_getAllKeysMethodNames() {
        return new String[]{"e", "getAllKeys"};
    }
}
