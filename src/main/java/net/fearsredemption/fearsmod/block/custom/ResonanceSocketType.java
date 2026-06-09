package net.fearsredemption.fearsmod.block.custom;

public enum ResonanceSocketType {
    CORE(0xE34FA4),
    STABILIZER(0x6FE6EA),
    FOCUS(0xB987FF);

    private final int particleColor;

    ResonanceSocketType(int particleColor) {
        this.particleColor = particleColor;
    }

    public int particleColor() {
        return particleColor;
    }
}
