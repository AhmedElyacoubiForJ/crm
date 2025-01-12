package edu.yacoubi.crm.security.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class UserCustomerKey implements Serializable {
    private Long userId;
    private Long customerId;
}
