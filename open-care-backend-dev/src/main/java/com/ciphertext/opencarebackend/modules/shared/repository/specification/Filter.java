package com.ciphertext.opencarebackend.modules.shared.repository.specification;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
public class Filter {

    private String field;
    private String joinField;
    private String childJoinField; //new added
    private QueryOperator operator;
    private Object value;
    private Object rangeSecondValue;
    private List<?> values;
    private Set<MultiJoin> multiJoins;
    private InJoin<?> inJoin;
    private Set<MultiJoinIn<?>> multiJoinIns;
}