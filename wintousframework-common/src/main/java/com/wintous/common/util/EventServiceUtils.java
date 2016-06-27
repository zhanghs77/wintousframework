package com.ctg.itrdc.event.utils;

import com.ctg.itrdc.event.EventManager;
import com.ctg.itrdc.event.base.BaseEvent;
import com.ctg.itrdc.event.base.BaseEventResult;
import com.ctg.itrdc.event.core.EventContext;
import com.ctg.itrdc.event.core.IEventConstants;
import com.ctg.itrdc.event.domain.repository.IEventLogRepository;
import com.ctg.itrdc.event.dto.EventDTO;
import com.ctg.itrdc.event.dto.HandleDTO;
import com.ctg.itrdc.event.dto.HandleImplDTO;
import com.ctg.itrdc.event.dto.query.QryEventSourceDTO;
import com.ctg.itrdc.event.handlers.JavaHandler;
import com.ctg.itrdc.event.service.IEventPersistenceService;

import java.util.Map;

/**
 * 事件service工具类
 * .
 *
 * @author wubo
 * @version Revision 1.0.0
 * @版权：中国电信 版权所有 (c) 2014
 * @see:
 * @创建日期：2015年3月6日
 * @功能说明：
 */
public class EventServiceUtils {
    
    private static final String SOURCE_PREFIX = "SOURCE_"; //应用传输的事件请求编号
    private static final String CENTER_PREFIX = "CENTER_"; //事件框架自动生成的事件请求编号
    private static final String PAGE_PREFIX   = "PAGE_";  //页面事件发起的动作请求编号
                                                           
    /**
     * 发布事件
     * .
     *
     * @param event
     * @return
     * @author wubo
     * 2015年3月6日 wubo
     */
    public static <T extends BaseEventResult> T publishEvent(BaseEvent event) {
        /*************  添加鹰眼监控   **************/
//        TrackUtils trackFacade = null;
//        if (!"$eventTrackFacade".equals(event.getQryEventSrcDto().getSourcePath())) {
//            trackFacade = new TrackUtils();
//        }
        T result = null;
        try {
            if (event != null && event.getQryEventSrcDto() != null) {
                if (StringUtils.isNotBlank(event.getQryEventSrcDto().getBusiCode())) {
                    event.getQryEventSrcDto().setRequestId(
                        SOURCE_PREFIX + event.getQryEventSrcDto().getBusiCode());
                } else {
                    IEventLogRepository eventLogRepository = EventContextUtil
                        .getBean("eventLogRepository");
                    if (eventLogRepository != null) {
                        event.getQryEventSrcDto().setRequestId(
                            CENTER_PREFIX + eventLogRepository.genSeq());
                    }
                }
                EventContext.getContext().setEventBusiCode(event.getQryEventSrcDto().getBusiCode());
                EventContext.getContext()
                    .setEventGroupSeq(event.getQryEventSrcDto().getRequestId());
            }
            result = EventManager.getInstance().publicEvent(event);
//            if (trackFacade != null) {
//                trackFacade.addTrack(event, result);
//            }
        } catch (Exception e) {
            e.printStackTrace();
            //            if (trackFacade != null) {
            //                trackFacade.addTrack(event, result, e);
            //            }
        }
        return result;
    }
    
    /**
     * 发布事件
     * </p>
     * 对服务事件发布做一层封装
     * </p>
     * <note>备注:</note>如无事件动作配置则返回空
     * .
     * @author hehuang
     * @param <T>
     * @return
     */
    public static <T extends BaseEventResult> T publishEvent(final Object eventSource,
        final String methodName) {
        QryEventSourceDTO qryEventSourceDTO = new QryEventSourceDTO();
        qryEventSourceDTO.setObjName(eventSource.getClass().getName());
        qryEventSourceDTO.setSourcePath(methodName);
        BaseEvent baseEvent = new BaseEvent(eventSource);
        baseEvent.setQryEventSrcDto(qryEventSourceDTO);
        return EventServiceUtils.publishEvent(baseEvent);
    }
    
    /**
    *
    * 发布事件，添加事件上下文因子
    * .
    * @param eventSource
    *          事件源实例
    * @param modelPath
    *          事件源
    * @param objName
    *          事件作用对象
    * @param param
    *          事件上下文参数
    * @param <T>
    *          事件结果
    *
    * @author hehuang
    * @return
    */
    public static <T extends BaseEventResult> T publishEvent(final Object eventSource,
        final String modelPath, final String objName, final Map<String, Object> param) {
        QryEventSourceDTO qryEventSourceDTO = new QryEventSourceDTO();
        qryEventSourceDTO.setObjName(objName);
        qryEventSourceDTO.setSourcePath(modelPath);
        BaseEvent baseEvent = new BaseEvent(eventSource);
        baseEvent.setMap(param);
        baseEvent.setQryEventSrcDto(qryEventSourceDTO);
        return EventServiceUtils.publishEvent(baseEvent);
    }
    
    /**
     * 发布事件
     * </p>
     * 对服务事件发布做一层封装
     * </p>
     * <note>备注:</note>如无事件动作配置则返回空
     * .
     * @author hehuang
     * @param <T>
     * @return
     */
    public static <T extends BaseEventResult> T publishEvent(final Object eventSource,
        final String modelPath, final String objName) {
        QryEventSourceDTO qryEventSourceDTO = new QryEventSourceDTO();
        qryEventSourceDTO.setObjName(objName);
        qryEventSourceDTO.setSourcePath(modelPath);
        BaseEvent baseEvent = new BaseEvent(eventSource);
        baseEvent.setQryEventSrcDto(qryEventSourceDTO);
        return EventServiceUtils.publishEvent(baseEvent);
    }
    
    /**
     * 发布事件
     * </p>
     * 对服务事件发布做一层封装
     * </p>
     * <note>备注:</note>如无事件动作配置则返回空
     * .
     * @author hehuang
     * @param <T>
     * @return
     */
    public static <T extends BaseEventResult> T publishEvent(final Object eventSource,
        final Object sourceObj, final String sourcePath, final String objName) {
        QryEventSourceDTO qryEventSourceDTO = new QryEventSourceDTO();
        qryEventSourceDTO.setObjName(objName);
        qryEventSourceDTO.setSourcePath(sourcePath);
        BaseEvent baseEvent = new BaseEvent(eventSource);
        baseEvent.setQryEventSrcDto(qryEventSourceDTO);
        return EventServiceUtils.publishEvent(baseEvent);
    }
    
    /**
     * 执行指定的handle
     * .
     *
     * @param eventDTO  执行handle的实现时，用来获取对应事件的处理结果类型
     * @param handleDto 用来获取对应此handle的实现（handleimpl）以及对应 事件处理实现参数
     * @return
     * @author wubo
     * 2015年3月11日 wubo
     */
    @SuppressWarnings("rawtypes")
    public static BaseEventResult executeHandle(EventDTO eventDTO, HandleDTO handleDto, HandleImplDTO handleImplDTO) {
        /*************  添加鹰眼监控   **************/
//        TrackUtils trackFacade = new TrackUtils();
        BaseEventResult eventResult = null;
        try {
            IEventLogRepository eventLogRepository = EventContextUtil.getBean("eventLogRepository");
            if (eventLogRepository != null) {
                EventContext.getContext().setEventGroupSeq(
                    PAGE_PREFIX + eventLogRepository.genSeq());
            }
            if (IEventConstants.BASE_EVENT_HANDLER_TYPE.JAVA_HANDLE.equals(handleDto
                .getHandleType())) {
                IEventPersistenceService eventPersistenceService = EventContextUtil
                    .getBean("eventPersistenceService");
                if (eventPersistenceService != null && handleDto != null) {
                    if (handleDto.getHandleId() == null) {
                        System.out.println("异常：handleDTO的handleId为空");
                    } else {
                        eventPersistenceService.handleStartDo(null, handleDto,
                            handleDto.getHandleId(),
                            IEventConstants.EVENT_HANDLE_STATUS.PROCESS_HANDLE);
                    }
                    
                }
                BaseEvent baseEvent = new BaseEvent(new Object()) {
                };
                JavaHandler handler = new JavaHandler(EventContextUtil.getContext(), baseEvent, eventDTO,
                    handleDto, handleImplDTO);
                eventResult = handler.execute(baseEvent);
                
                if (eventPersistenceService != null && handleDto != null) {
                    if (handleDto.getHandleId() == null) {
                        System.out.println("异常：handleDTO的handleId为空");
                    } else {
                        eventPersistenceService.handleEndDo(null, eventResult,
                            handleDto.getHandleId(),
                            IEventConstants.EVENT_HANDLE_STATUS.SUCCESS_HANDLE);
                    }
                    
                }
            }
//            trackFacade.addTrack(new Object[] {eventDTO, handleDto }, eventResult);
        } catch (Exception e) {
            e.printStackTrace();
//            trackFacade.addTrack(new Object[] {eventDTO, handleDto }, eventResult, e);
        }
        return eventResult;
    }
    
}
