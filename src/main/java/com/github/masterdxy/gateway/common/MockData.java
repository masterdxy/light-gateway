package com.github.masterdxy.gateway.common;

import org.jfaster.mango.annotation.ID;

import java.io.Serializable;

//mock data of a endpoint should return if endpoint's isMock turned to TRUE.
public class MockData implements Serializable {

    private Long endpointId;
    private String mockData;

    //todo random data logic

    @ID private Long id;

    public Long getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(Long endpointId) {
        this.endpointId = endpointId;
    }

    public String getMockData() {
        return mockData;
    }

    public void setMockData(String mockData) {
        this.mockData = mockData;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
