package org.petrowich.gallerychecker.models.checks.enums;

public enum CheckAim {
    ALL(1, "all") {
        @Override
        public String toString() {
            return "check all galleries";
        }
    },
    UNCHECKED(2, "unchecked") {
        @Override
        public String toString() {
            return "check unchecked galleries";
        }
    },
    AVAILABLE(3, "available") {
        @Override
        public String toString() {
            return "check available galleries";
        }
    };

    private final int aim;
    private final String shortName;

    CheckAim(int aim, String shortName) {
        this.aim = aim;
        this.shortName = shortName;
    }

    public int getAim() {
        return aim;
    }

    public static CheckAim toCheckAim(int aim) {
        for (CheckAim checkAim : CheckAim.values()) {
            if (checkAim.getAim() == aim) {
                return checkAim;
            }
        }
        return null;
    }

    public String getShortName() {
        return shortName;
    }
}
