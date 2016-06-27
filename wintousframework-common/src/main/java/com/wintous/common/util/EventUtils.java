package com.ctg.itrdc.event.utils;

import com.ctg.itrdc.event.base.BaseEventResult;
import com.ctg.itrdc.event.base.IBaseEventHandler;
import com.ctg.itrdc.event.core.EventException;
import com.ctg.itrdc.event.core.IEventConstants;
import com.ctg.itrdc.event.dto.BehaviorDTO;
import com.ctg.itrdc.event.dto.BehaviorFactorDTO;
import com.ctg.itrdc.event.dto.EventDTO;
import com.ctg.itrdc.event.dto.EventParamDTO;
import com.ctg.itrdc.event.dto.EventSourceDTO;
import com.ctg.itrdc.event.dto.HandleDTO;
import com.ctg.itrdc.event.dto.HandleFactorDTO;
import com.ctg.itrdc.event.dto.HandleFactorExtendDTO;
import com.ctg.itrdc.event.dto.HandleImplDTO;
import com.ctg.itrdc.event.dto.HandleParamRelationDTO;
import com.ctg.itrdc.event.dto.query.QryEventSourceDTO;
import com.ctg.itrdc.event.factor.EventFactor;
import com.ctg.itrdc.event.handlers.impl.param.Parameter;
import com.ctg.itrdc.event.rule.RuleContext;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 事件工具类 .
 * 
 * @版权：中国电信 版权所有 (c) 2014
 * @author wubo
 * @version Revision 1.0.0
 * @see:
 * @创建日期：2015年3月6日
 * @功能说明：
 * 
 */
public class EventUtils {
    
    /**
     * 在Action/Filter中，从EventActionUtil中获取基本的因子属性。
     * <P>
     *     只返回本地线程因子，未返回全部的因子信息，不推荐使用。推荐使用EventDrivenUtil.getMergedEventFactor()
     * </P>
     * 
     * @return 因子集合
     */
    @Deprecated
    public static HandleFactorDTO getHandleFactor() {
        // 统一使用EventFactor对象，进行默认优先级的筛选
        Map<String, String> factorMap = EventDrivenUtil.getEventFactor();
        HandleFactorDTO factorDto = new HandleFactorDTO();
        factorDto.setTenantId(factorMap.get(EventDrivenUtil.EVENT_FACTOR_KEY_TENANT_ID));
        factorDto.setAreaId(factorMap.get(EventDrivenUtil.EVENT_FACTOR_KEY_AREA_ID));
        factorDto.setRegionId(factorMap.get(EventDrivenUtil.EVENT_FACTOR_KEY_REGION_ID));
        factorDto.setChannelId(factorMap.get(EventDrivenUtil.EVENT_FACTOR_KEY_CHANNEL_ID));
        factorDto.setSystemId(factorMap.get(EventDrivenUtil.EVENT_FACTOR_KEY_SYSTEM_ID));
        if (!com.ctg.itrdc.event.utils.StringUtils.isNullOrEmpty(factorMap
            .get(EventDrivenUtil.EVENT_FACTOR_KEY_SERVICE_OFFER_ID))) {
            factorDto.setServiceOfferId(Long.valueOf(factorMap
                .get(EventDrivenUtil.EVENT_FACTOR_KEY_SERVICE_OFFER_ID)));
        }
        String mixinId = factorMap.get(EventDrivenUtil.EVENT_FACTOR_KEY_MIXIN_ID);
        if (!StringUtils.isEmpty(mixinId)) {
            factorDto.setMixinId(Long.parseLong(mixinId));
        }
        return factorDto;
    }
    
    /**
     * 匹配事件处理参数 .
     *
     * @param handleDto
     * @param map
     *            带有上下文参数的map.
     * @return
     * @author wubo 2015年3月13日 wubo
     */
    public static HandleDTO matchHandleParams(HandleDTO handleDto, Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            // TODO 提示上下文的map为空的信息
            return handleDto;
        } else {
            List<HandleParamRelationDTO> list = handleDto.getHandleParamRelDTOList();
            if (list == null) {
                // TODO 提示没有参数list的信息
                return handleDto;
            }

            EventParamDTO eventParamDto;
            HandleParamRelationDTO handleParamRelationDto;
            JexlContext context = new MapContext(map);
            JexlEngine engine = new JexlEngine();
            Object inputValue;
            for (int i = 0; i < list.size(); i++) {
                handleParamRelationDto = list.get(i);
                if (handleParamRelationDto == null) {
                    // TODO 事件处理参数关联DTO不存在的提示信息
                    continue;
                }
                eventParamDto = handleParamRelationDto.getEventParamDTO();
                if (eventParamDto == null) {
                    // TODO 事件参数DTO不存在的提示信息
                    continue;
                }
                if (StringUtils.isEmpty(eventParamDto.getFetchValue())) {
                    // TODO 事件参数取值不存在的提示信息
                    continue;
                }

                // 先清空缓存数据在计算
                eventParamDto.setInputValue(null);

                inputValue = null;
                inputValue = engine.createExpression(eventParamDto.getFetchValue()).evaluate(
                    context);
                if (inputValue == null) {
                    // TODO 未找到匹配参数的提示信息
                    continue;
                } else {
                    eventParamDto.setInputValue(inputValue);
                }
                handleParamRelationDto.setEventParamDTO(eventParamDto);
            }
            return handleDto;
        }
    }

    public static void getHandleParams(IBaseEventHandler handler, HandleDTO handleDto, BaseEventResult result) {

        // 判断是否有前置依赖和参数关联
        if (handleDto.getHandleBeforeRelyDTOList() != null
                && handleDto.getHandleBeforeRelyDTOList().size() > 0
                && handleDto.getHandleParamRelDTOList() != null
                && handleDto.getHandleParamRelDTOList().size() > 0) {
            Map<String, Object> map = result.getResultMap();
            Map<String, Object> params = new HashMap<String, Object>();

            if (map != null) {

                List<HandleParamRelationDTO> list = handleDto.getHandleParamRelDTOList();

                if (list != null
                        && list.size() > 0) {
                    EventParamDTO eventParamDto;
                    HandleParamRelationDTO handleParamRelationDto;
                    JexlContext context = new MapContext(map);
                    JexlEngine engine = new JexlEngine();
                    for (int i = 0; i < list.size(); i++) {
                        handleParamRelationDto = list.get(i);

                        eventParamDto = handleParamRelationDto.getEventParamDTO();
                        if (eventParamDto != null
                                && IEventConstants.PARAM_TYPE.PARAM_TYPE_HANDLE_PARAM.equals(eventParamDto.getParamType())) {
                            if (!StringUtils.isEmpty(
                                    eventParamDto.getFetchValue())) {
                                Object inputValue = engine.createExpression(eventParamDto.getFetchValue()).evaluate(
                                        context);
                                params.put(eventParamDto.getParamName(), inputValue);

                                Parameter parameter = new Parameter(handleParamRelationDto.getParamOrder().intValue(), inputValue);
                                handler.addParameter(parameter);

                            }
                        }

                    }
                }
            }

            handler.addHandleParams(params);
        }



    }

//    public static Map<Long, Object> getEventParams(EventDTO eventDTO, Map<String, Object> map) {
//
//        Map<Long, Object> params = new HashMap<Long, Object>();
//
//        if (map != null) {
//            List<EventParamDTO> paramList = eventDTO.getEventParamDTO();
//
//            JexlContext context = new MapContext(map);
//            JexlEngine engine = new JexlEngine();
//
//            if (paramList != null
//                    && paramList.size() > 0) {
//                for (EventParamDTO param
//                        : paramList) {
//                    // 动作参数类型延迟到动作执行完毕后加载
//                    if (IEventConstants.PARAM_TYPE.PARAM_TYPE_HANDLE_PARAM
//                            .equals(param.getParamType())) {
//                        continue;
//                    }
//
//                    if (!StringUtils.isEmpty(param.getFetchValue())) {
//                        Object value  = engine.createExpression(param.getFetchValue()).evaluate(
//                                context);
//
//                        params.put(param.getParamId(), value);
//                    }
//                }
//            }
//        }
//
//        return params;
//    }
    
    /**
     * 因子二次收集
     * <P>不推荐使用，只返回本地线程因子。推荐使用EventDrivenUtil.matchFactorParams()
     * </P>
     * .
     * 
     * @param map 因子上下文map
     * @return
     * @author wubo
     * 2015年5月12日 wubo
     */
    @Deprecated
    public static HandleFactorDTO matchFactorParams(Map<String, Object> map) {
        // 获取上下文因子
        HandleFactorDTO factorDTO = EventUtils.getHandleFactor();
        if (map == null || map.size() < 1) {
            return factorDTO;
        }
        // 从参数中获取因子信息
        for (String key : map.keySet()) {
            if (map.get(key) != null && map.get(key).toString().trim().length() > 0) {
                if (StringUtils.equals(key, EventDrivenUtil.EVENT_FACTOR_KEY_REGION_ID)) {
                    factorDTO.setRegionId(map.get(key).toString());
                } else if (StringUtils.equals(key, EventDrivenUtil.EVENT_FACTOR_KEY_TENANT_ID)) {
                    factorDTO.setTenantId(map.get(key).toString());
                } else if (StringUtils.equals(key, EventDrivenUtil.EVENT_FACTOR_KEY_CHANNEL_ID)) {
                    factorDTO.setChannelId(map.get(key).toString());
                } else if (StringUtils.equals(key, EventDrivenUtil.EVENT_FACTOR_KEY_SYSTEM_ID)) {
                    factorDTO.setSystemId(map.get(key).toString());
                } else if (StringUtils.equals(key, EventDrivenUtil.EVENT_FACTOR_KEY_AREA_ID)) {
                    factorDTO.setAreaId(map.get(key).toString());
                } else if (StringUtils.equals(key,
                    EventDrivenUtil.EVENT_FACTOR_KEY_SERVICE_OFFER_ID)) {
                    factorDTO.setServiceOfferId(Long.valueOf(map.get(key).toString()));
                }
            }
        }
        return factorDTO;
    }

    /**
     * 匹配因子配置信息
     * <P>如果配置的因子项不存在，也设置，跟着用户配置走</P>
     * .
     * @param config
     * @param defaultFactor
     * @return
     * @author hehuang
     */
    public static EventFactor matchFactorConfig(final HandleFactorDTO config, final EventFactor defaultFactor) {
        EventFactor factor = new EventFactor();

        try {
            EventDrivenUtil.CopyUtils.copyProperties(defaultFactor, factor);

            EventFactor sessionFactor =
                    (EventFactor) RuleContext.getContext().getAttachment(EventDrivenUtil.SESSION_EVENT_FACTOR);
            EventFactor requestFactor =
                    (EventFactor) RuleContext.getContext().getAttachment(EventDrivenUtil.REQUEST_EVENT_FACTOR);
            Map<String, String> localFactorMap =
                    EventDrivenUtil.getEventFactor();

            if (config != null) {
                // 从配置数据中获取因子匹配类型
                // 此处直接使用if判断，性能最高，不使用反射
                if (!StringUtils.isEmpty(config.getTenantFactorType())) {
                    factor.setTenantId(
                            getConfigTenantId(config.getTenantFactorType(), sessionFactor, requestFactor, localFactorMap));
                }

                if (!StringUtils.isEmpty(config.getSystemFactorType())) {
                    factor.setSystemId(
                            getConfigSystemId(config.getSystemFactorType(), sessionFactor, requestFactor, localFactorMap));
                }

                if (!StringUtils.isEmpty(config.getChannelFactorType())) {
                    factor.setChannelId(
                            getConfigChannelId(config.getChannelFactorType(), sessionFactor, requestFactor, localFactorMap));
                }

                if (!StringUtils.isEmpty(config.getAreaFactorType())) {
                    factor.setAreaId(
                            getConfigAreaId(config.getAreaFactorType(), sessionFactor, requestFactor, localFactorMap));
                }

                if (!StringUtils.isEmpty(config.getRegionFactorType())) {
                    factor.setCommonRegionId(
                            getConfigRegionId(config.getRegionFactorType(), sessionFactor, requestFactor, localFactorMap));
                }

                if (!StringUtils.isEmpty(config.getMixinFactorType())) {
                    factor.setMixinId(
                            getConfigMixinId(config.getMixinFactorType(), sessionFactor, requestFactor, localFactorMap));
                }

                if (!StringUtils.isEmpty(config.getServiceOfferFactorType())) {
                    factor.setServiceOfferIds(
                            getConfigServiceOfferIds(config.getServiceOfferFactorType(), sessionFactor, requestFactor, localFactorMap));
                }
            }

            return factor;

        } catch (Exception e) {

            e.printStackTrace();
            throw new EventException("匹配因子配置信息失败！", e);
        }
    }

    private static String getConfigTenantId(final String factorType
                                 , final EventFactor sessionFactor
                                 , final EventFactor requestFactor
                                 , final Map<String, String> localFactorMap) {
        String tenantId = "";
        switch (factorType) {
            case EventFactor.FACTOR_TYPE_SESSION:
                tenantId = sessionFactor.getTenantId();
                break;
            case EventFactor.FACTOR_TYPE_REQUEST:
                tenantId = requestFactor.getTenantId();
                break;
            case EventFactor.FACTOR_TYPE_LOCAL:
                tenantId = localFactorMap.get(EventDrivenUtil.EVENT_FACTOR_KEY_TENANT_ID);
                break;
            default:
                tenantId = "";
                break;
        }
        return tenantId;
    }

    private static String getConfigSystemId(final String factorType
            , final EventFactor sessionFactor
            , final EventFactor requestFactor
            , final Map<String, String> localFactorMap) {
        String systemId = "";
        switch (factorType) {
            case EventFactor.FACTOR_TYPE_SESSION:
                systemId = sessionFactor.getSystemId();
                break;
            case EventFactor.FACTOR_TYPE_REQUEST:
                systemId = requestFactor.getSystemId();
                break;
            case EventFactor.FACTOR_TYPE_LOCAL:
                systemId = localFactorMap.get(EventDrivenUtil.EVENT_FACTOR_KEY_SYSTEM_ID);
                break;
            default:
                systemId = "";
                break;
        }
        return systemId;
    }

    private static String getConfigAreaId(final String factorType
            , final EventFactor sessionFactor
            , final EventFactor requestFactor
            , final Map<String, String> localFactorMap) {
        String areaId = "";
        switch (factorType) {
            case EventFactor.FACTOR_TYPE_SESSION:
                areaId = sessionFactor.getAreaId();
                break;
            case EventFactor.FACTOR_TYPE_REQUEST:
                areaId = requestFactor.getAreaId();
                break;
            case EventFactor.FACTOR_TYPE_LOCAL:
                areaId = localFactorMap.get(EventDrivenUtil.EVENT_FACTOR_KEY_AREA_ID);
                break;
            default:
                areaId = "";
                break;
        }
        return areaId;
    }

    private static String getConfigChannelId(final String factorType
            , final EventFactor sessionFactor
            , final EventFactor requestFactor
            , final Map<String, String> localFactorMap) {
        String channelId = "";
        switch (factorType) {
            case EventFactor.FACTOR_TYPE_SESSION:
                channelId = sessionFactor.getChannelId();
                break;
            case EventFactor.FACTOR_TYPE_REQUEST:
                channelId = requestFactor.getChannelId();
                break;
            case EventFactor.FACTOR_TYPE_LOCAL:
                channelId = localFactorMap.get(EventDrivenUtil.EVENT_FACTOR_KEY_CHANNEL_ID);
                break;
            default:
                channelId = "";
                break;
        }
        return channelId;
    }

    private static String getConfigRegionId(final String factorType
            , final EventFactor sessionFactor
            , final EventFactor requestFactor
            , final Map<String, String> localFactorMap) {
        String regionId = "";
        switch (factorType) {
            case EventFactor.FACTOR_TYPE_SESSION:
                regionId = sessionFactor.getCommonRegionId();
                break;
            case EventFactor.FACTOR_TYPE_REQUEST:
                regionId = requestFactor.getCommonRegionId();
                break;
            case EventFactor.FACTOR_TYPE_LOCAL:
                regionId = localFactorMap.get(EventDrivenUtil.EVENT_FACTOR_KEY_REGION_ID);
                break;
            default:
                regionId = "";
                break;
        }
        return regionId;
    }

    private static String getConfigMixinId(final String factorType
            , final EventFactor sessionFactor
            , final EventFactor requestFactor
            , final Map<String, String> localFactorMap) {
        String mixinId = "";
        switch (factorType) {
            case EventFactor.FACTOR_TYPE_SESSION:
                mixinId = sessionFactor.getMixinId();
                break;
            case EventFactor.FACTOR_TYPE_REQUEST:
                mixinId = requestFactor.getMixinId();
                break;
            case EventFactor.FACTOR_TYPE_LOCAL:
                mixinId = localFactorMap.get(EventDrivenUtil.EVENT_FACTOR_KEY_MIXIN_ID);
                break;
            default:
                mixinId = "";
                break;
        }
        return mixinId;
    }

    private static List<Long> getConfigServiceOfferIds(final String factorType
            , final EventFactor sessionFactor
            , final EventFactor requestFactor
            , final Map<String, String> localFactorMap) {
        List<Long> serviceOfferIds = new ArrayList<Long>();
        switch (factorType) {
            case EventFactor.FACTOR_TYPE_SESSION:
                serviceOfferIds = sessionFactor.getServiceOfferIds();
                break;
            case EventFactor.FACTOR_TYPE_REQUEST:
                serviceOfferIds = requestFactor.getServiceOfferIds();
                break;
            case EventFactor.FACTOR_TYPE_LOCAL:
                // TODO serviceOfferIds = localFactorMap.get(EventDrivenUtil.EVENT_FACTOR_KEY_SERVICE_OFFER_ID);
                break;
            default:
                // mixinId = "";
                break;
        }
        return serviceOfferIds;
    }
    
    /**
     * 校验和设置因子和参数
     * .
     * 
     * @param eventDto
     * @param map
     * @return
     * @author wubo 
     * 2015年6月9日 wubo
     */
    public static EventDTO checkSetFactorsAndParams(EventDTO eventDto, Map<String, Object> map) {
        if (eventDto != null) {
            for (HandleDTO handleDto : eventDto.getHandleDTOList()) {
                handleDto = EventUtils.matchHandleParams(handleDto, map);
                List<HandleFactorDTO> listHandleFactor = handleDto.getHandleFactorDTOList();
                if (listHandleFactor != null && listHandleFactor.size() > 0) {
                    HandleImplDTO handleImplDto = EventUtils.getHandleImplDTO(listHandleFactor,
                        handleDto, map);
                    if (handleImplDto != null) {
                        handleDto.setHandleImplDTO(handleImplDto);
                    }
                }
            }
        }
        return eventDto;
    }
    
    /**
     * 比较值
     * 
     * @param inputval
     *            第一个值
     * @param factorOperation
     *            操作符
     * @param agoval
     *            第二个值
     * @return
     */
    private static boolean compareValue(Integer inputval, String factorOperation, Integer agoval) {
        boolean istrue = false;
        // 因子操作
	    if (factorOperation.equals("1")) {
		    if (inputval == agoval) {
			    istrue = true;
		    }

	    } else if (factorOperation.equals("2")) {
		    if (inputval >= agoval) {
			    istrue = true;
		    }

	    } else if (factorOperation.equals("3")) {
		    if (inputval <= agoval) {
			    istrue = true;
		    }

	    } else if (factorOperation.equals("4")) {
		    if (inputval != agoval) {
			    istrue = true;
		    }

	    } else if (factorOperation.equals("5")) {
		    if (inputval > agoval) {
			    istrue = true;
		    }

	    } else if (factorOperation.equals("6")) {
		    if (inputval < agoval) {
			    istrue = true;
		    }

	    }
        return istrue;
    }
    
    /**
     * 根据事件因子过滤设置事件处理对象中对应的处理实现对象.
     * 
     * @param handleDTO
     *            事件处理dto对象
     * @param factorDtos
     *            事件因子对象集合
     * @param map
     *            事件上下文map
     */
    public static HandleImplDTO getHandleImplDTO(List<HandleFactorDTO> factorDtos,
        HandleDTO handleDTO, Map<String, Object> map) {
        // 处理事件因子 TODO 获取默认因子
//        HandleFactorDTO factorDTO = matchFactorParams(map);
        // 2次因子匹配
        EventFactor defaultEventFactor = EventDrivenUtil.matchFactorParams(map);
        
        // 1表示关联因子情况下，若线程变量获取的因子与配置库因子不匹配，则返回当前handle的处理实现
        // 匹配优先级：因子匹配成功 > 默认 > 不执行
        if (factorDtos != null
                && handleDTO != null
                && "1".equals(handleDTO.getFactorSelection()) // TODO "1" 定义成常量 by hehuang
                && factorDtos.size() > 0) {
            for (HandleFactorDTO factor : factorDtos) {
                EventFactor eventFactor = matchFactorConfig(factor, defaultEventFactor);
                // TODO 做一次配置因子的筛选
                // TODO 使用黑盒测试方案
                // 根据因子扩展进行匹配；
                List<HandleFactorExtendDTO> hlextendlist =
                        factor.getHandleFactorExtendDTOList();

                // 原配置中因子不为空且线程量获取的因子不为空，因子过滤
                if (eventFactor != null && factor != null) {
                    // 原配置 地区标识 与线程变量获取的因子是否匹配
                    boolean isRegid = factor.getRegionId() == null
                        || factor.getRegionId().equals(eventFactor.getCommonRegionId());
                    // 原配置 区域标识 与线程变量获取的因子是否匹配
                    boolean isAreid = factor.getAreaId() == null
                        || factor.getAreaId().equals(eventFactor.getAreaId());
                    // 原配置 渠道标识 与线程变量获取的因子是否匹配
                    boolean isChid = factor.getChannelId() == null
                        || factor.getChannelId().equals(eventFactor.getChannelId());
                    // 原配置 系统标识 与线程变量获取的因子是否匹配
                    boolean isSysid = factor.getSystemId() == null
                        || factor.getSystemId().equals(eventFactor.getSystemId());
                    // 原配置 租房标识 与线程变量获取的因子是否匹配
                    boolean isTeid = factor.getTenantId() == null
                        || factor.getTenantId().equals(eventFactor.getTenantId());
                    // 业务类型 id 集合
                    List<Long> listSerOffids = eventFactor.getServiceOfferIds();
                    Long serid = factor.getServiceOfferId();
                    boolean isSeroffID = false;
                    isSeroffID = serid == null; // 业务类型 id
                    if (serid != null && listSerOffids != null && listSerOffids.size() > 0) {
                        for (Long seroffId : listSerOffids) {
                            isSeroffID = serid.equals(seroffId); // 比较业务因子与原配置因子是否相等
                            if (isSeroffID)
                                break;
                        }
                    }
                    // factorDTO对应属性值是否匹配因子
                    if (isAreid || isChid || isSysid || isTeid || isRegid || isSeroffID) { // 存在事件因子，匹配
                        if (hlextendlist != null && hlextendlist.size() > 0) { // 因子扩展
                            // 处理动作实现集合遍历
                            for (HandleFactorExtendDTO handleFactorExtendDTO : hlextendlist) {
                                // 获取匹配因子扩展比较的数据
                                String factortype = handleFactorExtendDTO.getFactorType(); // 因子类型
                                Long factorOperationago = handleFactorExtendDTO
                                    .getFactorOperation(); // 因子操作
                                String factorOperation = "";
                                if (factorOperationago != null)
                                    factorOperation = factorOperationago.toString();
                                String factorValue = handleFactorExtendDTO.getFactorValue(); // 因子值
                                boolean istrue = false;
                                // 或者匹配因子扩展，则表示满足
	                            if (factortype.equals("1000")) {// 区域标识 factorDTO.setAreaId("3");
		                            if (eventFactor.getAreaId() != null)
			                            istrue = compareValue(
					                            Integer.parseInt(eventFactor.getAreaId()),
					                            factorOperation, Integer.parseInt(factorValue));

	                            } else if (factortype.equals("2000")) {// 系统标识
		                            if (eventFactor.getSystemId() != null)
			                            istrue = compareValue(
					                            Integer.parseInt(eventFactor.getSystemId()),
					                            factorOperation, Integer.parseInt(factorValue));

	                            } else if (factortype.equals("3000")) {// 渠道标识
		                            if (eventFactor.getChannelId() != null)
			                            istrue = compareValue(
					                            Integer.parseInt(eventFactor.getChannelId()),
					                            factorOperation, Integer.parseInt(factorValue));

	                            } else if (factortype.equals("4000")) {// 租户标识
		                            if (eventFactor.getTenantId() != null)
			                            istrue = compareValue(
					                            Integer.parseInt(eventFactor.getTenantId()),
					                            factorOperation, Integer.parseInt(factorValue));

	                            } else if (factortype.equals("5000")) {// 地区标识
		                            if (eventFactor.getCommonRegionId() != null)
			                            istrue = compareValue(
					                            Integer.parseInt(eventFactor.getCommonRegionId()),
					                            factorOperation, Integer.parseInt(factorValue));

	                            } else if (factortype.equals("6000")) {
                                    // TODO ?
		                            if (eventFactor.getServiceOfferIds().get(0) != null)
			                            istrue = compareValue(
					                            (eventFactor.getServiceOfferIds().get(0).intValue()),
					                            factorOperation, Integer.parseInt(factorValue));

	                            }
                                // 只要匹配一次 因子扩展 // 事件因子对应实现处理dto;
                                if (istrue)
                                    return factor.getHandleImplDTO();
                            } // 因子扩展循环结束
                        } // 因子扩展 if 结束
                          // 因子匹配， 返回因子对应处理实现
                        return factor.getHandleImplDTO();
                    } // 存在事件因子，if 匹配 结束
                    else {
                        handleDTO.setHandleImplDTO(null);
                    }
                }
            }
        }
        return handleDTO.getHandleImplDTO(); // 返回当前handle的处理实现
    }
    
    /**
     * 获取事件处理行为.
     * 
     * @author yihe 2015年4月28日
     * @param bhfactordtos
     *            事件行为因子dto
     * @param handleDTO
     *            事件处理dto
     * @return 事件处理行为dto
     */
    public static BehaviorDTO getbehaviorDTO(List<BehaviorFactorDTO> bhfactordtos,
        HandleDTO handleDTO) {
        // 处理事件因子
        HandleFactorDTO factorDTO = EventUtils.getHandleFactor();
        // 1表示关联因子情况下，若线程变量获取的因子与配置库因子不匹配，则返回当前handle的处理行为对象
        if (bhfactordtos != null && handleDTO != null && handleDTO.getFactorSelection().equals("1")
            && bhfactordtos.size() > 0) {
            for (BehaviorFactorDTO bhfactor : bhfactordtos) {
                // 根据因子扩展进行匹配；
                List<HandleFactorExtendDTO> hlextendlist = bhfactor.getHandleFactorExtendDTOList();
                // 原配置中因子不为空且线程量获取的因子不为空，因子过滤
                if (factorDTO != null && bhfactor != null) {
                    // 原配置 地区标识 与线程变量获取的因子是否匹配
                    boolean isRegid = bhfactor.getRegionId() == null
                        || bhfactor.getRegionId().equals(factorDTO.getRegionId());
                    // 原配置 区域标识 与线程变量获取的因子是否匹配
                    boolean isAreid = bhfactor.getAreaId() == null
                        || bhfactor.getAreaId().equals(factorDTO.getAreaId());
                    // 原配置 渠道标识 与线程变量获取的因子是否匹配
                    boolean isChid = bhfactor.getChannelId() == null
                        || bhfactor.getChannelId().equals(factorDTO.getChannelId());
                    // 原配置 系统标识 与线程变量获取的因子是否匹配
                    boolean isSysid = bhfactor.getSystemId() == null
                        || bhfactor.getSystemId().equals(factorDTO.getSystemId());
                    // 原配置 租房标识 与线程变量获取的因子是否匹配
                    boolean isTeid = bhfactor.getTenantId() == null
                        || bhfactor.getTenantId().equals(factorDTO.getTenantId());
                    
                    // 业务类型 id 集合
                    List<Long> listSerOffids = factorDTO.getServiceOfferIds();
                    Long serid = bhfactor.getServiceOfferId(); // 业务类型 id
                    boolean isSeroffID = false; // 标识业务因子匹配是否为真
                    isSeroffID = serid == null;
                    if (serid != null && listSerOffids != null && listSerOffids.size() > 0) {
                        for (Long seroffId : listSerOffids) {
                            isSeroffID = serid.equals(seroffId); // 比较业务因子与原配置因子是否相等
                            if (isSeroffID)
                                break;
                        }
                    }
                    // factorDTO对应属性值是否匹配因子
                    if (isAreid || isChid || isSysid || isTeid || isRegid || isSeroffID) { // 存在事件行为因子，匹配
                        if (hlextendlist != null && hlextendlist.size() > 0) { // 因子扩展
                            // 处理动作实现集合遍历
                            for (HandleFactorExtendDTO handleFactorExtendDTO : hlextendlist) {
                                // 获取匹配因子扩展比较的数据
                                String factortype = handleFactorExtendDTO.getFactorType(); // 因子类型
                                Long factorOperationago = handleFactorExtendDTO
                                    .getFactorOperation(); // 因子操作
                                String factorOperation = "";
                                if (factorOperationago != null)
                                    factorOperation = factorOperationago.toString();
                                String factorValue = handleFactorExtendDTO.getFactorValue(); // 因子值
                                boolean istrue = false;
                                // 或者匹配因子扩展，则表示满足
	                            if (factortype.equals("1000")) {// 区域标识 factorDTO.setAreaId("3");
		                            if (factorDTO.getAreaId() != null)
			                            istrue = compareValue(
					                            Integer.parseInt(factorDTO.getAreaId()),
					                            factorOperation, Integer.parseInt(factorValue));

	                            } else if (factortype.equals("2000")) {// 系统标识
		                            if (factorDTO.getSystemId() != null)
			                            istrue = compareValue(
					                            Integer.parseInt(factorDTO.getSystemId()),
					                            factorOperation, Integer.parseInt(factorValue));

	                            } else if (factortype.equals("3000")) {// 渠道标识
		                            if (factorDTO.getChannelId() != null)
			                            istrue = compareValue(
					                            Integer.parseInt(factorDTO.getChannelId()),
					                            factorOperation, Integer.parseInt(factorValue));

	                            } else if (factortype.equals("4000")) {// 租户标识
		                            if (factorDTO.getTenantId() != null)
			                            istrue = compareValue(
					                            Integer.parseInt(factorDTO.getTenantId()),
					                            factorOperation, Integer.parseInt(factorValue));

	                            } else if (factortype.equals("5000")) {// 地区标识
		                            if (factorDTO.getRegionId() != null)
			                            istrue = compareValue(
					                            Integer.parseInt(factorDTO.getRegionId()),
					                            factorOperation, Integer.parseInt(factorValue));

	                            } else if (factortype.equals("6000")) {
		                            if (factorDTO.getServiceOfferId() != null)
			                            istrue = compareValue(
					                            (factorDTO.getServiceOfferId().intValue()),
					                            factorOperation, Integer.parseInt(factorValue));

	                            }
                                // 只要匹配一次 因子扩展 // 事件行为因子对应实现处理行为dto;
                                if (istrue)
                                    return bhfactor.getBehaviorDTO();
                            } // 因子扩展循环结束
                        } // 因子扩展 if 结束
                          // 因子匹配， 返回因子对应处理实现
                        return bhfactor.getBehaviorDTO();
                        
                    } // 存在事件行为因子，if 匹配 结束
                }
            }
        }
        return handleDTO.getBehaviorDTO(); // 返回当前handle的处理行为对象
    }
    
    /**
     * 存储事件源值,根据查询条件拼成key
     * 
     * @param qryEventSourceDTO
     *            查询条件dto
     * @param eventSourceDTO
     */
    public static void setEventSourceDTOInfo(QryEventSourceDTO qryEventSourceDTO,
        EventSourceDTO eventSourceDTO) {
        // 存储值
        GlobalEventCache.setObject(
            qryEventSourceDTO.getSourcePath() + qryEventSourceDTO.getObjName(), eventSourceDTO);
    }
    
    /**
     * 获取事件源值,根据查询条件拼成key来获取值
     * 
     * @param qryEventSourceDTO
     *            查询条件dto
     * @return 事件源dto
     */
    
    public static EventSourceDTO getEventSourceDTOInfo(QryEventSourceDTO qryEventSourceDTO) {
        // 获取存储值
        return (EventSourceDTO) GlobalEventCache.getObject(qryEventSourceDTO.getSourcePath()
            + qryEventSourceDTO.getObjName());
    }
    
}
