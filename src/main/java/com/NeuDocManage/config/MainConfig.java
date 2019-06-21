package com.NeuDocManage.config;

/**
 * 一些最基础的配置
 */
public class MainConfig {
    public final static int BLOCKSIZE=1024;//盘块大小
    public final static int BLOCKNUM=5;//盘块数量
    public final static int DISKSIZE=BLOCKNUM*BLOCKSIZE;//盘块大小乘盘块数量就是磁盘大小
}
