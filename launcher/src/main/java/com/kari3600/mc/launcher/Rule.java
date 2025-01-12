package com.kari3600.mc.launcher;

import java.util.List;

public class Rule {
    enum Action{allow,disallow}
    Action action;
    class OS {
        OSName name;
    }
    OS os;
    public boolean check(boolean value) {
        if (os != null && !os.name.isCurrentSystem()) return value;
        return action == Action.allow;
    }
    public static boolean checkRules(List<Rule> rules) {
        boolean val = false;
        for (Rule rule : rules) {
            val = rule.check(val);
        }
        return val;
    }
}
