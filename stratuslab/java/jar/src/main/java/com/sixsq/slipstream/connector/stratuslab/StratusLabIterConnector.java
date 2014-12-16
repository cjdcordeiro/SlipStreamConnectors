package com.sixsq.slipstream.connector.stratuslab;

/*
 * +=================================================================+
 * SlipStream Server (WAR)
 * =====
 * Copyright (C) 2014 SixSq Sarl (sixsq.com)
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

import com.sixsq.slipstream.connector.Connector;

public class StratusLabIterConnector extends StratusLabConnector {

	public static final String CLOUD_SERVICE_NAME = "stratuslabiter";
	public static final String CLOUDCONNECTOR_PYTHON_MODULENAME = "slipstream_stratuslab.StratusLabIterClientCloud";

	public StratusLabIterConnector() {
		this(CLOUD_SERVICE_NAME);
	}

	public StratusLabIterConnector(String instanceName) {
		super(instanceName != null ? instanceName : CLOUD_SERVICE_NAME);
	}

	public Connector copy() {
		return new StratusLabIterConnector(getConnectorInstanceName());
	}

	public String getCloudServiceName() {
		return CLOUD_SERVICE_NAME;
	}

	public String getCloudConnectorPythonModuleName() {
		return CLOUDCONNECTOR_PYTHON_MODULENAME;
	}

}
