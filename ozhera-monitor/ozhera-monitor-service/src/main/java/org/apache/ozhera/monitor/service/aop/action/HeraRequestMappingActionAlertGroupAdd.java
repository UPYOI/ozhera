/*
 *  Copyright (C) 2020 Xiaomi Corporation
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ozhera.monitor.service.aop.action;

import org.apache.ozhera.monitor.bo.AlertGroupInfo;
import org.apache.ozhera.monitor.bo.AlertGroupParam;
import org.apache.ozhera.monitor.bo.HeraReqInfo;
import org.apache.ozhera.monitor.bo.OperLogAction;
import org.apache.ozhera.monitor.dao.model.HeraOperLog;
import org.apache.ozhera.monitor.result.Result;
import org.apache.ozhera.monitor.service.aop.helper.HeraRequestMappingActionAlertGroupHelper;
import lombok.extern.slf4j.Slf4j;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: zgf1
 * @date: 2022/1/13 16:01
 */
@Slf4j
@Service
public class HeraRequestMappingActionAlertGroupAdd extends HeraRequestMappingActionArg2<HttpServletRequest, AlertGroupParam, Result<AlertGroupInfo>>{

    @Autowired
    private HeraRequestMappingActionAlertGroupHelper heraRequestMappingActionAlertGroupHelper;

    @Override
    public void beforeAction(HttpServletRequest arg1, AlertGroupParam arg2, HeraReqInfo heraReqInfo) {
        HeraOperLog operLog = new HeraOperLog();
        operLog.setOperName(heraReqInfo.getUser());
        operLog.setModuleName(heraReqInfo.getModuleName());
        operLog.setInterfaceName(heraReqInfo.getInterfaceName());
        operLog.setInterfaceUrl(heraReqInfo.getReqUrl());
        operLog.setAction(OperLogAction.ALERT_GROUP_ADD.getAction());
        heraRequestMappingActionAlertGroupHelper.saveHeraOperLogs(null, operLog, heraReqInfo);
        if (operLog.getId() != null) {
            heraReqInfo.setOperLog(operLog);
        }
    }

    @Override
    public void afterAction(HttpServletRequest arg1, AlertGroupParam arg2, HeraReqInfo heraReqInfo, Result<AlertGroupInfo> result) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", result.getCode());
        resultMap.put("message", result.getMessage());
        heraReqInfo.getOperLog().setResultDesc(Json.toJson(resultMap));
        heraRequestMappingActionAlertGroupHelper.saveHeraOperLogs(result, heraReqInfo.getOperLog(), heraReqInfo);
    }
}