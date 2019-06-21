package model;

public class UserInfo {
    //用来存储用户信息的块
    String UserName; //用户名称
    String Pwd; //密码
    int Mod; //权限
    int DirBlockId; //用户根目录id

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPwd() {
        return Pwd;
    }

    public void setPwd(String pwd) {
        Pwd = pwd;
    }

    public int getMod() {
        return Mod;
    }

    public void setMod(int mod) {
        Mod = mod;
    }

    public int getDirBlockId() {
        return DirBlockId;
    }

    public void setDirBlockId(int dirBlockId) {
        DirBlockId = dirBlockId;
    }
}
