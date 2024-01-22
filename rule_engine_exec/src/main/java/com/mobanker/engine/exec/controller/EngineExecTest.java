package com.mobanker.engine.exec.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mobanker.engine.common.EngineConst;
import com.mobanker.engine.framkwork.cpnt.container.EngineCpntContainer;
import com.mobanker.engine.framkwork.data.entity.EngineTaskInfo;
import com.mobanker.engine.framkwork.entry.EngineTaskLauncher;
import com.mobanker.engine.framkwork.promanager.EngineRuntimeProductManager;
import com.mobanker.framework.contract.dto.ResponseEntityDto;
import com.mobanker.framework.tracking.EETransaction;

/**
 * 规则模型设计交互
 * <p>
 * Company: mobanker.com
 * </p>
 * 
 * @author taojinn
 * @date 2016年9月4日
 * @version 1.0
 */

@Controller
@RequestMapping("execTest")
public class EngineExecTest {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private EngineTaskLauncher engineTaskLauncher;

	@Autowired
	private EngineRuntimeProductManager engineRuntimeProductManager;

	/**
	 * http://localhost:8081/execTest/testOneTask
	 * 
	 * @param operatorId
	 * @param productType
	 * @return
	 */
	@EETransaction(type = "URL", name = "测试运行服务")
	@RequestMapping(value = "/testOneTask", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntityDto<String> testOneTask() {
		ResponseEntityDto<String> ret = new ResponseEntityDto<String>();
		try {
			EngineCpntContainer container = engineRuntimeProductManager.getFlow("shoujidai");						
			
			EngineTaskInfo taskInfo = new EngineTaskInfo();
			taskInfo.setId(1L);
			taskInfo.setProductType("shoujidai");
			taskInfo.setAppName("test");
			taskInfo.setAppRequestId("1");
			taskInfo.setAssignStep(1);
			taskInfo.setStatus("0");
			taskInfo.setAppParams("{'userId':'23434','borrowNid':'TEST33422321333'}");
			// 记录操作员日志 FIXME
			taskInfo.setRuleVersion(container.getRuleVersion());
			engineTaskLauncher.runTask(taskInfo, container.getRoot());
			ret = new ResponseEntityDto<String>(EngineConst.RET_STATUS.SUCCESS,
					EngineConst.ERROR_CODE.SUCCESS, EngineConst.RET_MSG.OK,
					"执行完了");
		} catch (Exception e) {
			logger.error("登录异常", e);
			ret = new ResponseEntityDto<String>(EngineConst.RET_STATUS.FAIL,
					EngineConst.ERROR_CODE.UNKOWN, e.getMessage(), null);
		}

		return ret;
	}

}
