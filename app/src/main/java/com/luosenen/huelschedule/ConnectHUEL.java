package com.luosenen.huelschedule;

import android.content.Context;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ConnectHUEL {
    private Context context;
    private final String url = "http://xk.huel.edu.cn";
    private Map<String,String> cookies = new HashMap<>();
    private String modulus;
    private String exponent;
    private String csrftoken;
    private Connection connection;
    private Connection.Response response;
    private Document document;
    private String stuNum;
    private String password;

    public ConnectHUEL(String stuNum,String password){
        this.stuNum = stuNum;
        this.password = password;
    }

    public void init() throws Exception{
        getCsrftoken();
        getRSApublickey();
    }

    private void getCsrftoken(){
        try{
            connection = Jsoup.connect(url+ "/jwglxt/xtgl/login_slogin.html?language=zh_CN&_t="+new Date().getTime());
            connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
            response = connection.execute();
            cookies = response.cookies();
            //保存csrftoken
            document = Jsoup.parse(response.body());
            csrftoken = document.getElementById("csrftoken").val();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void getRSApublickey() throws Exception{
        connection = Jsoup.connect(url+ "/jwglxt/xtgl/login_getPublicKey.html?" +
                "time="+ new Date().getTime());
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        response = connection.cookies(cookies).ignoreContentType(true).execute();
        JSONObject jsonObject = JSON.parseObject(response.body());
        modulus = jsonObject.getString("modulus");
        exponent = jsonObject.getString("exponent");
        password = RSAEncoder.RSAEncrypt(password, B64.b64tohex(modulus), B64.b64tohex(exponent));
        password = B64.hex2b64(password);
    }

    public boolean beginLogin() throws Exception{

        connection = Jsoup.connect(url+ "/jwglxt/xtgl/login_slogin.html");
        connection.header("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");

        connection.data("csrftoken",csrftoken);
        connection.data("yhm",stuNum);
        connection.data("mm",password);
        connection.data("mm",password);
        connection.cookies(cookies).ignoreContentType(true)
                .method(Connection.Method.POST).execute();

        response = connection.execute();
        document = Jsoup.parse(response.body());
        if(document.getElementById("tips") == null){
            System.out.println("欢迎登陆");
            Toast.makeText(context,"欢迎登陆",Toast.LENGTH_SHORT).show();
            return true;
        }else{
            System.out.println(document.getElementById("tips").text());
            Toast.makeText(context,document.getElementById("tips").text(),Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // 查询学生信息
    public String getStudentInformaction() throws Exception {
        connection = Jsoup.connect(url+ "/jwglxt/xsxxxggl/xsxxwh_cxCkDgxsxx.html?gnmkdm=N100801&su="+ stuNum);
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        response = connection.cookies(cookies).ignoreContentType(true).execute();
        JSONObject jsonObject = JSON.parseObject(response.body());
        String account = jsonObject.getString("xh_id");
        String sex = jsonObject.getString("xbm");
        String nation = jsonObject.getString("mzm");
        String academic = jsonObject.getString("jg_id");
        String classRoom = jsonObject.getString("bh_id");
        String major = jsonObject.getString("zszyh_id");
        String state = jsonObject.getString("xjztdm");
        String studyYear = jsonObject.getString("njdm_id");
        String number = jsonObject.getString("zjhm");
        String sovereign = jsonObject.getString("zzmmm");
        return account+'\n'+sex+"\n"+nation+"\n"+academic+"\n"+classRoom+"\n"+major+"\n"+state+"\n"+studyYear+"\n"+number+"\n"+sovereign;
    }

    public List<ClassBean> getStudentTimetable(int year , int term) throws Exception {
        connection = Jsoup.connect(url+ "/jwglxt/kbcx/xskbcx_cxXsKb.html?gnmkdm=N2151");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        connection.data("xnm",String.valueOf(year));
        connection.data("xqm",String.valueOf(term * term * 3));
        response = connection.cookies(cookies).method(Connection.Method.POST).ignoreContentType(true).execute();
        JSONObject jsonObject = JSON.parseObject(response.body());
        if(jsonObject.get("kbList") == null){
            System.out.println("暂时没有安排课程");
            return null;
        }
        JSONArray timeTable = JSON.parseArray(jsonObject.getString("kbList"));
        System.out.println(String.valueOf(year) + " -- " + String.valueOf(year + 1) + "学年 " + "第" + term + "学期");
        List<ClassBean> list = new ArrayList<>();
        for (Iterator iterator = timeTable.iterator(); iterator.hasNext();) {
            JSONObject lesson = (JSONObject) iterator.next();
            ClassBean bean = new ClassBean();
            bean.setOne(lesson.getString("xqjmc"));
            bean.setTwo(lesson.getString("jc"));
            bean.setThree(lesson.getString("kcmc"));
            bean.setFour(lesson.getString("xm"));
            bean.setFive(lesson.getString("xqmc"));
            bean.setSix(lesson.getString("cdmc"));
            bean.setSeven(lesson.getString("zcd"));
            list.add(bean);
        }
        return list;
    }

    public List<ScoreBean> getStudentGrade(int year , int term) throws Exception {
        Map<String,String> datas = new HashMap<>();
        datas.put("xnm",String.valueOf(year));
        datas.put("xqm",String.valueOf(term * term * 3));
        datas.put("_search","false");
        datas.put("nd",String.valueOf(new Date().getTime()));
        datas.put("queryModel.showCount","20");
        datas.put("queryModel.currentPage","1");
        datas.put("queryModel.sortName","");
        datas.put("queryModel.sortOrder","asc");
        datas.put("queryModel.sortName","");
        datas.put("time","0");

        connection = Jsoup.connect(url+ "/jwglxt/cjcx/cjcx_cxDgXscj.html?gnmkdm=N305005&layout=default&su=" + stuNum);
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        response = connection.cookies(cookies).method(Connection.Method.POST)
                .data(datas).ignoreContentType(true).execute();
        connection = Jsoup.connect(url+ "/jwglxt/cjcx/cjcx_cxDgXscj.html?doType=query&gnmkdm=N305005");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        response = connection.cookies(cookies).method(Connection.Method.POST)
                .data(datas).ignoreContentType(true).execute();
        JSONObject jsonObject = JSON.parseObject(response.body());
        JSONArray gradeTable = JSON.parseArray(jsonObject.getString("items"));
        List<ScoreBean> list = new ArrayList<>();
        for (Iterator iterator = gradeTable.iterator(); iterator.hasNext();) {
            JSONObject lesson = (JSONObject) iterator.next();
            ScoreBean bean = new ScoreBean();
            bean.setOne(lesson.getString("kcmc"));
            bean.setTwo(lesson.getString("jsxm"));
            bean.setThree(lesson.getString("bfzcj"));
            bean.setFour(lesson.getString("jd"));
            list.add(bean);
        }

        return list;
    }

    public void logout() throws Exception {
        connection = Jsoup.connect(url+ "/jwglxt/logout");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        response = connection.cookies(cookies).ignoreContentType(true).execute();
    }
}
