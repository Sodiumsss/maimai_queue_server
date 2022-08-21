package com.maimai.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
public class maimai {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @CrossOrigin
    @PostMapping("/login")
    public String login(String acc,String pw)
    {
        if (acc==null || pw==null){return "-1";}
        if (acc.equals("") || pw.equals("")){return "-1";}

        System.out.println("[TryLogin]"+acc);
        String sql = "select pw,kind from user_info where acc=?";

        List<String> info=jdbcTemplate.execute(sql, (PreparedStatementCallback<List<String>>) ps -> {
            ps.setString(1,acc);
            ResultSet rs = ps.executeQuery();
            List<String> info1 = new ArrayList<>();
            if (rs.next())
            {
                info1.add(rs.getString("pw"));
                info1.add(rs.getString("kind"));
            }
            return info1;
        });
        String kind="-1";
        if (info!= null && info.size()==2)
        {
            kind = info.get(1);

            if (!pw.equals(info.get(0)))
            {
                kind="-1";
            }
            System.out.println("[Login]"+acc+"[kind]"+kind);
        }
        return kind;
    }

    @CrossOrigin
    @PostMapping("/register")
    public String register(String acc,String pw)
    {
        String sql1="insert into user_info values(?,?,?)";
        try {
            jdbcTemplate.update(sql1, acc, pw, 1);

        }catch (Exception e)
        {
            e.printStackTrace();
            return "-1";
        }
        return "1";
    }

    public String get_kind(String name)
    {
        if (name.equals(""))
        {
            return "-1";
        }

        String sql1="select kind from user_info where acc=?";


        return jdbcTemplate.execute(sql1, (PreparedStatementCallback<String>) ps -> {
            ps.setString(1,name);
            ResultSet rs =ps.executeQuery();
            String tmp=null;
            if (rs.next())
            {
                tmp=rs.getString("kind");
            }
            return tmp;
        });
    }

    @CrossOrigin
    @RequestMapping("/op_add")
    public String op_add(String name)
    {

        String kind = get_kind(name);
        if (kind==null || kind.equals("2") || kind.equals("3"))
        {
            return "-1";
        }

        String sql2="update user_info set kind = 2 where acc=?";
        try {
            jdbcTemplate.update(sql2,name);
            return "1";
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return "-1";
    }

    @CrossOrigin
    @RequestMapping("/op_delete")
    public String op_delete(String name)
    {
        String kind =get_kind(name);
        if (kind==null || kind.equals("1") || kind.equals("3"))
        {
            return "-1";
        }
        String sql2="update user_info set kind = 1 where acc=?";
        try {
            jdbcTemplate.update(sql2,name);
            return "1";
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return "-1";
    }



}
