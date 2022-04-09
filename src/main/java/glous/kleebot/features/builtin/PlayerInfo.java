package glous.kleebot.features.builtin;

import java.io.Serializable;

public class PlayerInfo implements Serializable {
    public PlayerInfo(){};

    public Role[] getRoles() {
        return roles;
    }

    public void setRoles(Role[] roles) {
        this.roles = roles;
    }

    public World[] getWorlds() {
        return worlds;
    }

    public void setWorlds(World[] worlds) {
        this.worlds = worlds;
    }

    public int getElectroculus_number() {
        return electroculus_number;
    }

    public void setElectroculus_number(int electroculus_number) {
        this.electroculus_number = electroculus_number;
    }

    public int getGeoculus_number() {
        return geoculus_number;
    }

    public void setGeoculus_number(int geoculus_number) {
        this.geoculus_number = geoculus_number;
    }

    public int getAnemoculus_number() {
        return anemoculus_number;
    }

    public void setAnemoculus_number(int anemoculus_number) {
        this.anemoculus_number = anemoculus_number;
    }

    public int getAvatar_number() {
        return avatar_number;
    }

    public void setAvatar_number(int avatar_number) {
        this.avatar_number = avatar_number;
    }

    public int getDomain_number() {
        return domain_number;
    }

    public void setDomain_number(int domain_number) {
        this.domain_number = domain_number;
    }

    public int getCommon_chest_number() {
        return common_chest_number;
    }

    public void setCommon_chest_number(int common_chest_number) {
        this.common_chest_number = common_chest_number;
    }

    public int getExquisite_chest_number() {
        return exquisite_chest_number;
    }

    public void setExquisite_chest_number(int exquisite_chest_number) {
        this.exquisite_chest_number = exquisite_chest_number;
    }

    public int getPrecious_chest_number() {
        return precious_chest_number;
    }

    public void setPrecious_chest_number(int precious_chest_number) {
        this.precious_chest_number = precious_chest_number;
    }

    public int getLuxurious_chest_number() {
        return luxurious_chest_number;
    }

    public void setLuxurious_chest_number(int luxurious_chest_number) {
        this.luxurious_chest_number = luxurious_chest_number;
    }

    public int getMagic_chest_number() {
        return magic_chest_number;
    }

    public void setMagic_chest_number(int magic_chest_number) {
        this.magic_chest_number = magic_chest_number;
    }

    public int getActive_day_number() {
        return active_day_number;
    }

    public void setActive_day_number(int active_day_number) {
        this.active_day_number = active_day_number;
    }

    public int getAchievement_number() {
        return achievement_number;
    }

    public void setAchievement_number(int achievement_number) {
        this.achievement_number = achievement_number;
    }

    public String getSpiral_abyss() {
        return spiral_abyss;
    }

    public void setSpiral_abyss(String spiral_abyss) {
        this.spiral_abyss = spiral_abyss;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    private String errorMsg;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private int status;
    private Role[] roles;
    private World[] worlds;
    private int electroculus_number;
    private int geoculus_number;
    private int anemoculus_number;
    private int avatar_number;
    private int domain_number;
    private int common_chest_number;
    private int exquisite_chest_number;
    private int precious_chest_number;
    private int luxurious_chest_number;
    private int magic_chest_number;
    private int active_day_number;
    private int achievement_number;
    private String spiral_abyss;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    private String uid;
}
