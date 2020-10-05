/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.magnum.dataup;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;
import org.magnum.dataup.model.VideoStatus.VideoState;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class VideoController {

	/**
	 * You will need to create one or more Spring controllers to fulfill the
	 * requirements of the assignment. If you use this file, please rename it to
	 * something other than "AnEmptyController"
	 * 
	 * 
	 * ________ ________ ________ ________ ___ ___ ___ ________ ___ __ |\ ____\|\ __
	 * \|\ __ \|\ ___ \ |\ \ |\ \|\ \|\ ____\|\ \|\ \ \ \ \___|\ \ \|\ \ \ \|\ \ \
	 * \_|\ \ \ \ \ \ \ \\\ \ \ \___|\ \ \/ /|_ \ \ \ __\ \ \\\ \ \ \\\ \ \ \ \\ \ \
	 * \ \ \ \ \\\ \ \ \ \ \ ___ \ \ \ \|\ \ \ \\\ \ \ \\\ \ \ \_\\ \ \ \ \____\ \
	 * \\\ \ \ \____\ \ \\ \ \ \ \_______\ \_______\ \_______\ \_______\ \ \_______\
	 * \_______\ \_______\ \__\\ \__\ \|_______|\|_______|\|_______|\|_______|
	 * \|_______|\|_______|\|_______|\|__| \|__|
	 * 
	 * 
	 */
	
	private Map<Long, Video> videoMap = new HashMap<>();
	
	public static long videoID = 0L;	
	
	@RequestMapping(value = "/video", method = RequestMethod.GET)
	public @ResponseBody Collection<Video> getVideoCollection() throws Exception{
		return videoMap.values();
	}
	
	@RequestMapping(value = "/video", method = RequestMethod.POST)
	public @ResponseBody Video addVideoMetadata(@RequestBody Video video, HttpServletRequest request) throws Exception {
		video.setId(++videoID);
		String base = "http://"+request.getServerName()+((request.getServerPort() != 80) ? ":"+request.getServerPort() : "");
		video.setDataUrl(base + "/video" + video.getId() + "/data");
		videoMap.put(videoID, video);
		return video;
	}
	
	@RequestMapping(value = "/video/{id}/data", method = RequestMethod.GET)
	public void getVideoData(@PathVariable("id") long videoID, HttpServletResponse response) throws Exception{
		VideoFileManager manager = VideoFileManager.get();
		try {
			manager.copyVideoData(videoMap.get(videoID), response.getOutputStream());
		} catch (Exception e) {
			response.sendError(404);
		}
	}
	
	  @RequestMapping(value = "/video/{id}/data", method = RequestMethod.POST)
	  public @ResponseBody VideoStatus addVideoData(@PathVariable("id") long id, @RequestParam MultipartFile data, HttpServletResponse response) throws Exception {
	    VideoFileManager videoData = VideoFileManager.get();
	    try {
	      videoData.saveVideoData(videoMap.get(id), data.getInputStream());
	    } catch (Exception e) {
	    	response.sendError(404);
	    }
	    return new VideoStatus(VideoState.READY);
	  }

}
