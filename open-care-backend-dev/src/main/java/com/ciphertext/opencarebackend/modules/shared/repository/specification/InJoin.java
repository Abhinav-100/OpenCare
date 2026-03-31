package com.ciphertext.opencarebackend.modules.shared.repository.specification;

import java.util.List;

public record InJoin<T>(String joinColumn, String joinTable, String inColumn, List<T> values) {

}