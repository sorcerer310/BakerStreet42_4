package com.bsu.bk42.com.bsu.bk42.screen;

/**
 * 用于接收PLC命令的接口
 * Created by fengchong on 16/4/3.
 */
public interface IPlcCommandListener {
    public void receivePlcCommand(int cmdi);
}
