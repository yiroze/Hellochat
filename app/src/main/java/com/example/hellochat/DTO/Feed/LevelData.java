package com.example.hellochat.DTO.Feed;

public class LevelData {
    public String Level_name;
    public String Level_info;
    public int Level;
    public LevelData(String name , String info , int level){
        Level_name = name;
        Level_info =info;
        Level =level;
    }

    @Override
    public String toString() {
        return "LevelData{" +
                "Level_name='" + Level_name + '\'' +
                ", Level_info='" + Level_info + '\'' +
                ", Level=" + Level +
                '}';
    }

    public String getLevel_name() {
        return Level_name;
    }

    public void setLevel_name(String level_name) {
        Level_name = level_name;
    }

    public String getLevel_info() {
        return Level_info;
    }

    public void setLevel_info(String level_info) {
        Level_info = level_info;
    }

    public int getLevel() {
        return Level;
    }

    public void setLevel(int level) {
        Level = level;
    }
}
