package ru.prplhd.currencyexchange.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Currency {
    Long id;
    String code;
    String name;
    String sign;

    public Currency(String sign, String name, String code) {
        this.sign = sign;
        this.name = name;
        this.code = code;
    }
}