package com.mobanker.democenter.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author lait.zhang@gmail.com
 * @Date 2016年8月19日-下午5:38:22
 * @Description:<br>
 * </br>
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMessage {
    int id;
    String name;
}
