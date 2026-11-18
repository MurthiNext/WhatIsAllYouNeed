package com.murthinext.wiayn.item.multi_config_card;

public enum UpgradeMode {
    BEST_EFFORT,
    REQUIRE_ALL,
    EXACT;

    public String translationKey() {
        return "screen.wiayn.multi_config_card.mode." + name().toLowerCase(java.util.Locale.ROOT);
    }
}
