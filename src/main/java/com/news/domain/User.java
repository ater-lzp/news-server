package com.news.domain;

import lombok.Data;


@Data
public class User {
private Integer user_id;
private String user_name;
private String user_password;
private Integer gender;
private String introduction;
private Integer role;
private String avatar;
}
