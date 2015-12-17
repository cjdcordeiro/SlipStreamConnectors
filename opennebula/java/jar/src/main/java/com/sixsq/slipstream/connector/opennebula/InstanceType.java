package com.sixsq.slipstream.connector.opennebula;

/*
 * +=================================================================+
 * SlipStream Server (WAR)
 * =====
 * Copyright (C) 2013 SixSq Sarl (sixsq.com)
 * =====
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
 * -=================================================================-
 */

import java.util.ArrayList;
import java.util.List;

public enum InstanceType {
	MICRO("micro"), SMALL("small"), MEDIUM("medium"), LARGE("large");

	public static final String INHERITED = "inherited";
	
	private final String value;

	InstanceType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
	public static List<String> getValues() {
		List<String> types = new ArrayList<String>();
	
		for (InstanceType type : InstanceType.values()) {
			types.add(type.getValue());
		}
		return types;
	}
}
