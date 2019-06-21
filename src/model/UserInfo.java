package model;

public class UserInfo {
    //用来存储用户信息的块
    String userName; //用户名称
    String pwd; //密码
    int mod; //权限
    int dirBlockId; //用户根目录id

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        userName = userName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        pwd = pwd;
    }

    public int getMod() {
        return mod;
    }

    public void setMod(int mod) {
        mod = mod;
    }

    public int getDirBlockId() {
        return dirBlockId;
    }

    public void setDirBlockId(int dirBlockId) {
        dirBlockId = dirBlockId;
    }
}
