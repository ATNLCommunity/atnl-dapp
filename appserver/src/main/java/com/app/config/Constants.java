package com.app.config;

import com.jfinal.kit.PropKit;

public class Constants {
	public static final String UPLOAD_DIR = PropKit.use("config.properties").get("upload.dir");
	public static final String IMAGE_URL = PropKit.use("config.properties").get("image.url");
	public static final String BASE_URL = PropKit.use("config.properties").get("base.url");
	

}
