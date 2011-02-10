/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.facebook.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * <p>
 * Web argument resolver that resolves arguments annotated with
 * {@link FacebookAccessToken} or {@link FacebookUserId}.
 * </p>
 * 
 * <p>
 * After a user has authenticated with Facebook via the XFBML
 * &lt;fb:login-button&gt; tag, their user ID and an access token are stored in
 * a cookie whose name is "fbs_{application key}". This web argument resolver
 * extracts that information from the cookie (if available) and supplies it to a
 * controller handler method as String values.
 * </p>
 * 
 * <p>
 * Both {@link FacebookAccessToken} and {@link FacebookUserId} are required by
 * default. If the access token or user ID cannot be resolved and if the
 * annotation is set to be required, an exception will be thrown indicating an
 * illegal state. If the annotation is set to not be required, a null will be
 * returned.
 * </p>
 * 
 * @author Craig Walls
 */
public class FacebookWebArgumentResolver implements WebArgumentResolver {

	private final String apiKey;
	private final String appSecret;

	public FacebookWebArgumentResolver(String apiKey, String appSecret) {
		this.apiKey = apiKey;
		this.appSecret = appSecret;
	}
	
	public Object resolveArgument(MethodParameter parameter, NativeWebRequest request) throws Exception {
		FacebookCookieValue annotation = parameter.getParameterAnnotation(FacebookCookieValue.class);
		if (annotation == null) {
			return WebArgumentResolver.UNRESOLVED;
		}

		HttpServletRequest nativeRequest = (HttpServletRequest) request.getNativeRequest();
		Map<String, String> cookieData = FacebookCookieParser.getFacebookCookieData(nativeRequest.getCookies(), apiKey, appSecret);
		String key = annotation.value();
		if (!cookieData.containsKey(key) && annotation.required()) {
			throw new IllegalStateException("Missing Facebook cookie value '" + key + "'");
		}

		return cookieData.get(key);
	}

}