package com.mobanker.engine.design.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class EngineScriptSyntaxErr {
	private String id;
    private String raw;
    private String code;
    private String evidence;
    private int line;
    private int character;
    private String scope;
    private String reason;
}
