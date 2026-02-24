package dbrighthd.elytracontrails.networking;

public record PlayerConfig(
        boolean enableTrail,
        boolean enableRandomWidth,
        boolean speedDependentTrail,
        double trailMinSpeed,
        boolean trailMovesWithElytraAngle,
        double maxWidth,
        double trailLifetime,
        double startRampDistance,
        double endRampDistance,
        int color,
        double randomWidthVariation,
        String prideTrail
) {}