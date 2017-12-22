package com.vipsfin.competition.stat;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppConfig {

    private String brandPath;
    private Integer splitFileCount = 30;
    private Integer writerBufferLength = 50000;

    public String getBrandPath() {
        return brandPath;
    }

    public AppConfig setBrandPath(String brandPath) {
        this.brandPath = brandPath;
        return this;
    }

    public Integer getSplitFileCount() {
        return splitFileCount;
    }

    public AppConfig setSplitFileCount(Integer splitFileCount) {
        this.splitFileCount = splitFileCount;
        return this;
    }

    public Integer getWriterBufferLength() {
        return writerBufferLength;
    }

    public AppConfig setWriterBufferLength(Integer writerBufferLength) {
        this.writerBufferLength = writerBufferLength;
        return this;
    }
}