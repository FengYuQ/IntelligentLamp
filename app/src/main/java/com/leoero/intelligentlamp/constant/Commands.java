package com.leoero.intelligentlamp.constant;

import java.util.HashSet;
import java.util.Set;

/** @brief 命令集合 **/
public class Commands {
    public final static String open_command = "a";  //开
    public final static String close_command = "b"; //关
    public final static String lighting_command = "c"; //亮
    public final static String dimming_command = "d"; //暗

    /** @note 由于讯飞识别出来的有可能是相同拼音的字，所以需要建立集合提高识别率 **/
    /** ------------------------------------------------------------------- **/
    public final static HashSet<String> speech_open_commands = new HashSet<String>() {
        {
            add("开");
            add("慨");
            add("铠");
            add("恺");
            add("楷");
            add("凯");
            add("锴");
            add("他");
            add("她");
            add("它");
        }
    };

    public final static HashSet<String> speech_close_commands = new HashSet<String>() {
        {
            add("关");
            add("管");
            add("观");
            add("罐");
            add("官");
            add("馆");
            add("冠");
            add("灌");
            add("贯");
            add("惯");
            add("棺");
            add("莞");
            add("光");
            add("广");
            add("逛");
            add("咣");
            add("胱");
        }
    };

    public final static HashSet<String> speech_lighting_commands = new HashSet<String>() {
        {
            add("亮");
            add("梁");
            add("两");
            add("量");
            add("良");
            add("辆");
            add("凉");
            add("靓");
            add("粮");
            add("俩");
            add("粱");
            add("谅");
            add("晾");
        }
    };

    public final static HashSet<String> speech_dimming_commands = new HashSet<String>() {
        {
            add("暗");
            add("按");
            add("案");
            add("安");
            add("俺");
            add("岸");
            add("庵");
            add("黯");
            add("氨");
            add("嗯");
            add("恩");
            add("噢");
            add("哦");
            add("昂");
            add("唔");
            add("摁");
        }
    };

}
