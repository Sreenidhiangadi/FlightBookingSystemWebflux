package com.flightapp.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

 class RoleTest {

    @Test
    void testEnumValues() {
        Role[] roles = Role.values();
        assertEquals(2, roles.length, "Role enum should have 2 constants");
        assertTrue(containsRole(roles, Role.USER), "Role enum should contain USER");
        assertTrue(containsRole(roles, Role.ADMIN), "Role enum should contain ADMIN");
    }

    @Test
    void testValueOf() {
        assertEquals(Role.USER, Role.valueOf("USER"));
        assertEquals(Role.ADMIN, Role.valueOf("ADMIN"));
    }
    private boolean containsRole(Role[] roles, Role role) {
        for (Role r : roles) {
            if (r == role) return true;
        }
        return false;
    }
}
