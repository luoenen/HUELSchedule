package com.luosenen.huelschedule;

public class ClassBean {
    private String One;
    private String Two;
    private String Three;
    private String Four;
    private String Five;
    private String Six;
    private String seven;

    public String getOne() {
        return One;
    }

    public void setOne(String one) {
        One = one;
    }

    public String getTwo() {
        return Two;
    }

    public void setTwo(String two) {
        Two = two;
    }

    public String getThree() {
        return Three;
    }

    public void setThree(String three) {
        Three = three;
    }

    public String getFour() {
        return Four;
    }

    public void setFour(String four) {
        Four = four;
    }

    public String getFive() {
        return Five;
    }

    public void setFive(String five) {
        Five = five;
    }

    public String getSix() {
        return Six;
    }

    public void setSix(String six) {
        Six = six;
    }

    public String getSeven() {
        return seven;
    }

    public void setSeven(String seven) {
        this.seven = seven;
    }

    public ClassBean() {
    }

    public ClassBean(String one, String two, String three, String four, String five, String six, String seven) {
        One = one;
        Two = two;
        Three = three;
        Four = four;
        Five = five;
        Six = six;
        this.seven = seven;
    }
}
