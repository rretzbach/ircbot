package com.gmail.rretzbach.ircbot;

public class IrcBotConfig {
    String host;
    Integer portMinimum;
    Integer portMaximum;
    String password;
    String nick;
    String nickAlternative;
    String realName;
    String email;
    String channel;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPortMinimum() {
        return portMinimum;
    }

    public void setPortMinimum(Integer portMinimum) {
        this.portMinimum = portMinimum;
    }

    public Integer getPortMaximum() {
        return portMaximum;
    }

    public void setPortMaximum(Integer portMaximum) {
        this.portMaximum = portMaximum;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getNickAlternative() {
        return nickAlternative;
    }

    public void setNickAlternative(String nickAlternative) {
        this.nickAlternative = nickAlternative;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
