package dbrighthd.elytracontrails.networking;

public final class PlayerConfigDefaults {
    private PlayerConfigDefaults() {}

    public static PlayerConfig defaults() {
        return new PlayerConfig(
                true,
                false,
                true,
                0.7,
                true,
                0.1,
                2.5,
                4.0,
                10.0,
                0xFFFFFFFF,
                0.0,
                ""
        );
    }
}