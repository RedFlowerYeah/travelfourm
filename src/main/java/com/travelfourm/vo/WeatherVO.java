package com.travelfourm.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author 34612
 * @CreateTime 2021/4/1 15:24
 */

@Data
public class WeatherVO implements Serializable {

    private static final long serialVersionUID = 4089597935549696545L;

    private String city;

    private String ganmao;

    private String wendu;

    private YesterdayVO yesterday;

    private List<ForecastVO> forecast;
}
