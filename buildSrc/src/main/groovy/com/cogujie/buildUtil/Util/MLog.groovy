package com.cogujie.buildUtil.Util

class MLog {

    public static final String ANSI_YELLOW = "\u001B[33m"
    public static final String ANSI_RESET = "\u001B[0m"

    public static final boolean IF_SHOW_LOG = true

    public static void i(Object arg) {
        if (!IF_SHOW_LOG) return
        println(ANSI_YELLOW + arg + ANSI_RESET)
    }
}