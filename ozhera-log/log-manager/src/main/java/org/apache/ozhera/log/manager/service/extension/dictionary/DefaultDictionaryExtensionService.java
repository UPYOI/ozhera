/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.ozhera.log.manager.service.extension.dictionary;

import com.google.common.collect.Lists;
import org.apache.ozhera.log.manager.dao.MilogMiddlewareConfigDao;
import org.apache.ozhera.log.manager.model.dto.DictionaryDTO;
import org.apache.ozhera.log.manager.model.pojo.MilogLogTailDo;
import org.apache.ozhera.log.manager.model.pojo.MilogMiddlewareConfig;
import org.apache.ozhera.log.manager.service.impl.KafkaMqConfigService;
import org.apache.ozhera.log.manager.service.impl.RocketMqConfigService;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ozhera.log.api.enums.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.apache.ozhera.log.manager.service.extension.dictionary.DictionaryExtensionService.DEFAULT_DICTIONARY_EXTENSION_SERVICE_KEY;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/4/12 10:36
 */
@Service(name = DEFAULT_DICTIONARY_EXTENSION_SERVICE_KEY)
@Slf4j
public class DefaultDictionaryExtensionService implements DictionaryExtensionService {

    @Resource
    private MilogMiddlewareConfigDao milogMiddlewareConfigDao;

    @Resource
    private RocketMqConfigService rocketMqConfigService;

    @Resource
    private KafkaMqConfigService kafkaMqConfigService;

    @Override
    public List<DictionaryDTO<?>> queryMiddlewareConfigDictionary(String monitorRoomEn) {
        List<MilogMiddlewareConfig> milogMiddlewareConfigs = milogMiddlewareConfigDao.queryCurrentMontorRoomMQ(monitorRoomEn);
        List<DictionaryDTO<?>> dictionaryDTOS = Lists.newArrayList();
        Arrays.stream(MQSourceEnum.values())
                .forEach(sourceEnum -> {
                    DictionaryDTO dictionaryDTO = new DictionaryDTO<>();
                    dictionaryDTO.setValue(sourceEnum.getCode());
                    dictionaryDTO.setLabel(sourceEnum.getName());
                    if (CollectionUtils.isNotEmpty(milogMiddlewareConfigs)) {
                        dictionaryDTO.setChildren(milogMiddlewareConfigs.stream().filter(middlewareConfig -> sourceEnum.getCode().equals(middlewareConfig.getType())).map(middlewareConfig -> {
                            DictionaryDTO<Long> childDictionaryDTO = new DictionaryDTO<>();
                            childDictionaryDTO.setValue(middlewareConfig.getId());
                            childDictionaryDTO.setLabel(middlewareConfig.getAlias());
//                            if (MQSourceEnum.ROCKETMQ.getCode().equals(middlewareConfig.getType())) {
//                                List<DictionaryDTO> existsTopic = rocketMqConfigService.queryExistsTopic(middlewareConfig.getAk(), middlewareConfig.getSk(),
//                                        middlewareConfig.getNameServer(), middlewareConfig.getServiceUrl(), middlewareConfig.getAuthorization(), middlewareConfig.getOrgId(), middlewareConfig.getTeamId());
//                                childDictionaryDTO.setChildren(existsTopic);
//                            }
                            return childDictionaryDTO;
                        }).collect(Collectors.toList()));
                    }
                    dictionaryDTOS.add(dictionaryDTO);
                });
        return dictionaryDTOS;
    }

    @Override
    public List<DictionaryDTO<?>> queryResourceDictionary() {
        return generateCommonDictionary(middlewareEnum -> Boolean.TRUE);
    }

    @Override
    public List<DictionaryDTO<?>> queryAppType() {
        return Arrays.stream(ProjectTypeEnum.values())
                .map(projectTypeEnum -> {
                    DictionaryDTO<Integer> dictionaryDTO = new DictionaryDTO<>();
                    dictionaryDTO.setValue(projectTypeEnum.getCode());
                    dictionaryDTO.setLabel(projectTypeEnum.getType());

                    dictionaryDTO.setShowDeploymentType(Boolean.TRUE);
                    dictionaryDTO.setShowEnvGroup(Boolean.TRUE);
                    dictionaryDTO.setShowServiceIp(Boolean.TRUE);
                    dictionaryDTO.setShowMqConfig(Boolean.TRUE);

                    return dictionaryDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<MilogLogTailDo> querySpecialTails() {
        return Lists.newArrayList();
    }

    @Override
    public List<DictionaryDTO<?>> queryMachineRegion() {
        List<DictionaryDTO<?>> dictionaryDTOS = Lists.newArrayList();
        for (MachineRegionEnum value : MachineRegionEnum.values()) {
            DictionaryDTO dictionaryDTO = new DictionaryDTO();
            dictionaryDTO.setLabel(value.getCn());
            dictionaryDTO.setValue(value.getEn());
            dictionaryDTOS.add(dictionaryDTO);
        }
        return dictionaryDTOS;
    }

    @Override
    public List<DictionaryDTO<?>> queryDeployWay() {
        List<DictionaryDTO<?>> dictionaryDTOS = Lists.newArrayList();
        for (DeployWayEnum value : DeployWayEnum.values()) {
            DictionaryDTO dictionaryDTO = new DictionaryDTO();
            dictionaryDTO.setLabel(value.getName());
            dictionaryDTO.setValue(value.getCode());
            dictionaryDTOS.add(dictionaryDTO);
        }
        return dictionaryDTOS;
    }

    @Override
    public List<DictionaryDTO<?>> queryResourceTypeDictionary() {
        return generateCommonDictionary(middlewareEnum -> ResourceEnum.MQ == middlewareEnum |
                ResourceEnum.STORAGE == middlewareEnum);
    }

    @Override
    public List<DictionaryDTO> queryExistsTopic(String ak, String sk, String nameServer, String serviceUrl, String authorization, String orgId, String teamId) {
        return Lists.newArrayList();
    }

    @Override
    public List<DictionaryDTO<?>> queryMQDictionary() {
        return Arrays.stream(MQSourceEnum.values()).map(mqSourceEnum -> {
            DictionaryDTO<Integer> dictionaryDTO = new DictionaryDTO<>();
            dictionaryDTO.setValue(mqSourceEnum.getCode());
            dictionaryDTO.setLabel(mqSourceEnum.getName());
            if (MQSourceEnum.ROCKETMQ == mqSourceEnum) {
                dictionaryDTO.setShowBrokerName(Boolean.TRUE);
            }
            return dictionaryDTO;
        }).collect(Collectors.toList());
    }

    private List<DictionaryDTO<?>> generateCommonDictionary(Predicate<ResourceEnum> filter) {
        List<DictionaryDTO> rDictionaryDTOS = Arrays.stream(MachineRegionEnum.values())
                .map(machineRegionEnum ->
                        DictionaryDTO.Of(machineRegionEnum.getEn(), machineRegionEnum.getCn()))
                .collect(Collectors.toList());
        return Arrays.stream(ResourceEnum.values())
                .filter(filter)
                .map(middlewareEnum -> {
                    DictionaryDTO<Integer> dictionaryDTO = new DictionaryDTO<>();
                    dictionaryDTO.setValue(middlewareEnum.getCode());
                    dictionaryDTO.setLabel(middlewareEnum.getName());
                    dictionaryDTO.setChildren(rDictionaryDTOS);
                    return dictionaryDTO;
                }).collect(Collectors.toList());
    }
}
