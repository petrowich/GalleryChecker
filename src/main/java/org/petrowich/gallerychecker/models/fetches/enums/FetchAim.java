package org.petrowich.gallerychecker.models.fetches.enums;

public enum FetchAim {
    NEW(1, "new") {
        @Override
        public String toString() {
            return "fetch new galleries";
        }
    },
    DELETED(2, "deleted") {
        @Override
        public String toString() {
            return "fetch deleted galleries";
        }
    };

    private final int aim;
    private final String shortName;

    FetchAim(int aim, String shortName) {
        this.aim = aim;
        this.shortName = shortName;
    }

    public int getAim() {
        return aim;
    }

    public static FetchAim toFetchAim(int aim) {
        for (FetchAim fetchAim : FetchAim.values()) {
            if (fetchAim.getAim() == aim) {
                return fetchAim;
            }
        }
        return null;
    }

    public String getShortName() {
        return shortName;
    }
}
