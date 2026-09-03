package dbrighthd.elytracontrails.networking;

/**
 * I used female gender mod as a base for this class,, thank you winnpixie and Stigstille
 */
public final class ElytraTrailConstants {
    /**
     * The payload namespace for elytratrails
     */
    public static final String MOD_ID = "elytratrails";

    /**
     * Payload for sending configs to server
     */
    public static final String GET_CONFIGS_REQUEST = MOD_ID + ":get_all_configs";

    public static final String PLAYER_CONFIG = MOD_ID + ":player_config_tag";

    public static final String PLAYER_CONFIG_DEPRECATED = MOD_ID + ":player_config";


    public static final String REMOVE_CONFIG = MOD_ID + ":remove_from_store";

    public static final String TWIRL_STATE = MOD_ID + ":twirl_state";

    public static final String TWIRL_DATA = MOD_ID + ":twirl_data";

    public static final String TRAIL_STATUS = MOD_ID + ":trails_enabled";


    private ElytraTrailConstants() {
    }
}