package com.sixsq.slipstream.connector.stratuslab;

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

import com.sixsq.slipstream.connector.CredentialsBase;
import com.sixsq.slipstream.credentials.Credentials;
import com.sixsq.slipstream.exceptions.InvalidElementException;
import com.sixsq.slipstream.exceptions.SlipStreamRuntimeException;
import com.sixsq.slipstream.exceptions.ValidationException;
import com.sixsq.slipstream.persistence.User;

public class StratusLabCredentials extends CredentialsBase implements Credentials {

	public StratusLabCredentials(User user, String connectorInstanceName) {
		super(user);
		try {
			cloudParametersFactory = new StratusLabUserParametersFactory(connectorInstanceName);
		} catch (ValidationException e) {
			e.printStackTrace();
			throw (new SlipStreamRuntimeException(e));
		}
	}

	public String getKey() throws InvalidElementException {
		return getParameterValue(StratusLabUserParametersFactory.KEY_PARAMETER_NAME);
	}

	public String getSecret() throws InvalidElementException {
		return getParameterValue(StratusLabUserParametersFactory.SECRET_PARAMETER_NAME);
	}

}
