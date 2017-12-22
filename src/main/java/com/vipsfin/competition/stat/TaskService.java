package com.vipsfin.competition.stat;

import java.util.List;

public interface TaskService {

    List<String> run(String recordPath) throws Exception;
}