package com.common.home.domain;

import lombok.Data;

@Data
public class CommonLog {
	int cl_id;
	String type;
	String ip;
	String content;
	String create_date;
}
