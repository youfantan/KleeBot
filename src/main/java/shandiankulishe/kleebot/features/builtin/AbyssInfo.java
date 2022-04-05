package shandiankulishe.kleebot.features.builtin;

import java.io.Serializable;

public class AbyssInfo implements Serializable {

    public int getTotal_battle_times() {
        return total_battle_times;
    }

    public void setTotal_battle_times(int total_battle_times) {
        this.total_battle_times = total_battle_times;
    }

    public int getTotal_win_times() {
        return total_win_times;
    }

    public void setTotal_win_times(int total_win_times) {
        this.total_win_times = total_win_times;
    }

    public String getMax_floor() {
        return max_floor;
    }

    public void setMax_floor(String max_floor) {
        this.max_floor = max_floor;
    }

    public int getTotal_star() {
        return total_star;
    }

    public void setTotal_star(int total_star) {
        this.total_star = total_star;
    }

    public boolean isIs_unlock() {
        return is_unlock;
    }

    public void setIs_unlock(boolean is_unlock) {
        this.is_unlock = is_unlock;
    }

    public long getMax_damaged() {
        return max_damaged;
    }

    public void setMax_damaged(long max_damaged) {
        this.max_damaged = max_damaged;
    }

    public String getMax_damaged_avatar() {
        return max_damaged_avatar;
    }

    public void setMax_damaged_avatar(String max_damaged_avatar) {
        this.max_damaged_avatar = max_damaged_avatar;
    }

    public int getMax_defeat() {
        return max_defeat;
    }

    public void setMax_defeat(int max_defeat) {
        this.max_defeat = max_defeat;
    }

    public String getMax_defeat_avatar() {
        return max_defeat_avatar;
    }

    public void setMax_defeat_avatar(String max_defeat_avatar) {
        this.max_defeat_avatar = max_defeat_avatar;
    }

    public int getMax_skill() {
        return max_skill;
    }

    public void setMax_skill(int max_skill) {
        this.max_skill = max_skill;
    }

    public String getMax_skill_avatar() {
        return max_skill_avatar;
    }

    public void setMax_skill_avatar(String max_skill_avatar) {
        this.max_skill_avatar = max_skill_avatar;
    }

    public int getMax_energy_skill() {
        return max_energy_skill;
    }

    public void setMax_energy_skill(int max_energy_skill) {
        this.max_energy_skill = max_energy_skill;
    }

    public String getMax_energy_skill_avatar() {
        return max_energy_skill_avatar;
    }

    public void setMax_energy_skill_avatar(String max_energy_skill_avatar) {
        this.max_energy_skill_avatar = max_energy_skill_avatar;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    private int total_battle_times;
    private int total_win_times;
    private String max_floor;
    private int total_star;
    private boolean is_unlock;
    private long max_damaged;
    private String max_damaged_avatar;
    private int max_defeat;
    private String max_defeat_avatar;
    private int max_skill;
    private String max_skill_avatar;
    private int max_energy_skill;
    private String max_energy_skill_avatar;
    private int status;
    private String errorMsg;
    private AbyssFloor[] floors;

    public AbyssFloor[] getFloors() {
        return floors;
    }

    public void setFloors(AbyssFloor[] floors) {
        this.floors = floors;
    }
}
