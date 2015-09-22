package expansion;

/**
 * Created by murray on 23/08/15.
 */
public class DownloaderService extends com.google.android.vending.expansion.downloader.impl.DownloaderService {
    public static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnn9Aq3T3NSgLnx3zoBU/cqFk/wqR+7IkHiFRK3OiYVo+5O2TyNcaJ/Wlb/zN78kR1T2hTHxXO8OCz6/+MTHxjZCzrfFTX7DadK0vtWcQsliKTJKRAWh+hwa96Elwt3OgeRjU+KoHo6m+Q3W5Ftx0hXv3G/uSlH3ZQc8zw+KApGEB9NJIUVZP+LUQz1GUs3Xko9Ol/BiBZS5HBpCS9lM39nKks0A0yPgsR0UtMK9G8eTNchDgeL0zzBfKi/k9lBpl5HK0xp4dLyd/USqgVYA7Jr6aEw6P952oEhn/MU8zw5jcwCcJ+i1YWHt9FEdRlHq7IXKsuzQBLwIipEkxtO4FqQIDAQAB"; // TODO Add public key
    private static final byte[] SALT = new byte[]{1, 4, -1, -1, 14, 42, -79, -21, 13, 2, -8, -11, 62, 1, -10, -101, -19, 41, -12, 18};

    @Override
    public String getPublicKey() {
        return BASE64_PUBLIC_KEY;
    }

    @Override
    public byte[] getSALT() {
        return SALT;
    }

    @Override
    public String getAlarmReceiverClassName() {
        return DownloaderServiceBroadcastReceiver.class.getName();
    }
}