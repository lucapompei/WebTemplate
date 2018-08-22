package lp.web.webtemplate.controller.api;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lp.web.utils.RestUtils;
import lp.web.webtemplate.constants.EndpointConstants;
import lp.web.webtemplate.service.InfoService;

/**
 * This class exposes api to check the status of the web service
 * 
 * @author lucapompei
 *
 */
@RestController
@RequestMapping(EndpointConstants.API_BASE)
public class InfoAPI {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(InfoAPI.class);

	/**
	 * The info service
	 */
	@Autowired
	private InfoService infoService;

	/**
	 * This method exposes API to show the main application info
	 * 
	 * @return the main application info
	 */
	@RequestMapping(value = EndpointConstants.ABOUT, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseEntity<String> getAbout() {
		LOGGER.info("Requesting for " + EndpointConstants.ABOUT);
		Date beginTime = new Date();
		String response = infoService.getAppInfo();
		Date endTime = new Date();
		LOGGER.info("Returned response for " + EndpointConstants.ABOUT + " request in "
				+ String.format("%s ms", endTime.getTime() - beginTime.getTime()));
		return RestUtils.getResponseEntity(response);
	}

	/**
	 * This method exposes API to check if the server is alive
	 * 
	 * @return the server status
	 */
	@RequestMapping(value = EndpointConstants.IS_ALIVE, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseEntity<String> isAlive() {
		LOGGER.info("Requesting for " + EndpointConstants.IS_ALIVE);
		Date beginTime = new Date();
		Date endTime = new Date();
		LOGGER.info("Returned true response for " + EndpointConstants.IS_ALIVE + " request in "
				+ String.format("%s ms", endTime.getTime() - beginTime.getTime()));
		return RestUtils.getResponseEntity(true);
	}

}
