package com.ctg.itrdc.event.utils;

import com.ctg.itrdc.event.base.BaseEvent;
import com.ctg.itrdc.event.core.EventContext;
import com.ctg.itrdc.event.dto.EventInstanceDTO;
import com.ctg.itrdc.event.dto.HandleDTO;
import com.ctg.itrdc.event.dto.HandleInstanceDTO;
import com.ctg.itrdc.event.dto.HandleStrategyDTO;
import com.ctg.itrdc.event.service.IEventPersistenceService;

/**
 * 事件策略工具类
 * .
 * Created by hehuang on 2015/6/9.
 */
public class EventStrategyUtils {

    private static IEventPersistenceService eventPersistenceService
            = EventContextUtil.getBean("eventPersistenceService");                       // 事件持久化服务类

    public static HandleStrategyDTO getHandleStrategy(final BaseEvent baseEvent, final HandleDTO handleDTO) {
        return eventPersistenceService.getHandleStrategyDTO(baseEvent, handleDTO.getHandleId());
    }

}
