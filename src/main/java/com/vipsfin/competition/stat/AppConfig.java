package com.vipsfin.competition.stat;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppConfig {

    private String branchPath;
    private Integer splitFileCount = 30;

    public String getBranchPath() {
        return branchPath;
    }

    public AppConfig setBranchPath(String branchPath) {
        this.branchPath = branchPath;
        return this;
    }

    public Integer getSplitFileCount() {
        return splitFileCount;
    }

    public AppConfig setSplitFileCount(Integer splitFileCount) {
        this.splitFileCount = splitFileCount;
        return this;
    }
}