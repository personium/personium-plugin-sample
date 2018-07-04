/**
 * personium.io
 * Copyright 2018 FUJITSU LIMITED
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
 */
package io.personium.plugin.auth.sample;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;

import io.personium.plugin.base.PluginException;
import io.personium.plugin.base.auth.AuthConst;
import io.personium.plugin.base.auth.AuthPlugin;
import io.personium.plugin.base.auth.AuthenticatedIdentity;

/**
 * Sample auth plugin.
 * Grant type : urn:x-personium:auth:sample
 * Only when "personium" is specified for sample_password,
 * the result is returned as authenticated with the account sample_account.
 *
 * Example of calling this plugin:
 * curl "https://unit.com/cellname/__token" -X POST -i -d 'grant_type=urn:x-personium:auth:sample&sample_account=sample&sample_password=personium'
 */
public class SampleAuthPlugin implements AuthPlugin {

    /** Target grant type. */
    private static final String PLUGIN_GRANT_TYPE = "urn:x-personium:auth:sample";
    /** Target account type. */
    private static final String PLUGIN_ACCOUNT_TYPE = "auth:sample";

    /** Mapkey:account. */
    private static final String KEY_ACCOUNT = "sample_account";
    /** Mapkey:password. */
    private static final String KEY_PASSWORD = "sample_password";

    /** Error message key:Required parameter missing. */
    private static final String ERROR_REQUIRED_PARAM_MISSING = "error.required.param.missing";

    /** Error messages properties. */
    private final Properties errorMessagesProp = loadProperties("plugin-error-messages.properties");

    /**
     * Load properties file.
     * @param file Properties file
     * @return Properties file object
     */
    private static Properties loadProperties(String file) {
        Properties prop = new Properties();
        prop.clear();
        try (InputStream is = SampleAuthPlugin.class.getClassLoader().getResourceAsStream(file)) {
            prop.load(is);
        } catch (IOException e) {
            throw new PluginException(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Failed to load properties.");
        }
        return prop;
    }

    /**
     * Plugin type.
     * Currently it is only "auth".
     */
    @Override
    public String getType() {
        return AuthConst.PLUGIN_TYPE;
    }

    /**
     * Target grant type.
     * This corresponds to "grant_type" specified on the __token endpoint.
     */
    @Override
    public String getGrantType() {
        return PLUGIN_GRANT_TYPE;
    }

    /**
     * Target account type.
     * This is the type of account to authenticate with the plugin.
     * The returned value can be specified as "Type" in the account creation endpoint.
     */
    @Override
    public String getAccountType() {
        return PLUGIN_ACCOUNT_TYPE;
    }

    /**
     * Behavior when plugin is called.
     * If authentication succeeds, set "AccountName", "AccountType" and return.
     */
    @Override
    public AuthenticatedIdentity authenticate(Map<String, List<String>> body) throws PluginException {
        if (body == null || body.isEmpty()) {
            throw createPluginException(HttpStatus.SC_BAD_REQUEST, ERROR_REQUIRED_PARAM_MISSING, "Body");
        }

        String account = getSingleValue(body, KEY_ACCOUNT);
        String password = getSingleValue(body, KEY_PASSWORD);

        if (!password.equals("personium")) {
            // If it returns null, it is regarded as authentication failure.
            return null;
        }

        AuthenticatedIdentity authnIdentity = new AuthenticatedIdentity();
        authnIdentity.setAccountName(account);
        authnIdentity.setAccountType(PLUGIN_ACCOUNT_TYPE);

        return authnIdentity;
    }

    /**
     * Get single value in the body.
     * @param body request body
     * @param key map key
     * @return Value corresponding to key
     * @throws PluginException Value does not exist
     */
    private String getSingleValue(Map<String, List<String>> body, String key) throws PluginException {
        List<String> valueList = body.get(key);
        if (valueList == null) {
            throw createPluginException(HttpStatus.SC_BAD_REQUEST, ERROR_REQUIRED_PARAM_MISSING, key);
        }
        String value = valueList.get(0);
        if (StringUtils.isEmpty(value)) {
            throw createPluginException(HttpStatus.SC_BAD_REQUEST, ERROR_REQUIRED_PARAM_MISSING, key);
        }
        return value;
    }

    /**
     * Create and return PluginException.
     * @param statusCode Response status code
     * @param messageKey message key
     * @param messageParams message args
     * @return PluginException
     */
    private PluginException createPluginException(int statusCode, String messageKey, Object... messageParams) {
        return new PluginException(statusCode, getErrorMessage(messageKey, messageParams));
    }

    /**
     * Get error message.
     * @param message key
     * @param message args
     * @return Error message
     */
    private String getErrorMessage(String key, Object... params) {
        String message = errorMessagesProp.getProperty(key);
        if (params.length > 0) {
            return MessageFormat.format(message, params);
        }
        return message;
    }
}
