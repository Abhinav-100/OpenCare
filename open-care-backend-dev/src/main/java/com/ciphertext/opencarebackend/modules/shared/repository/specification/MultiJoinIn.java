package com.ciphertext.opencarebackend.modules.shared.repository.specification;

import java.util.List;

public record MultiJoinIn<T>(String joinColumn, String joinTable, String inColumn, List<T> joinValues) {

}