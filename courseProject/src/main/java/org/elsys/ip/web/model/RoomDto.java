package org.elsys.ip.web.model;

import org.elsys.ip.web.model.validator.PasswordMatches;
import org.elsys.ip.web.model.validator.ValidEmail;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class RoomDto {
    private String id;

    @NotNull
    @NotEmpty
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
