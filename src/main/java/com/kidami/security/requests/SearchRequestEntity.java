package com.kidami.security.requests;

import lombok.Data;

@Data
public class SearchRequestEntity {
    private String search;

    public SearchRequestEntity() {}

    public SearchRequestEntity(String search) {
        this.search = search;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
