/*
 * Copyright 2019-2021 CloudNetService team & contributors
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

package de.dytanic.cloudnet.common.language;

import de.dytanic.cloudnet.common.Properties;
import de.dytanic.cloudnet.common.StringUtil;
import java.util.Map.Entry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LanguageManagerTest {

  @Test
  public void test() {
    Properties properties = new Properties();
    properties.put("test_message", "Test_Message");

    LanguageManager.setLanguage("en");
    LanguageManager.addLanguageFile("en", properties);

    Assertions.assertNotNull(LanguageManager.getMessage("test_message"));
    Assertions.assertEquals("Test_Message", LanguageManager.getMessage("test_message"));

    properties = new Properties();
    for (int i = 0; i < 100; i++) {
      properties.put(StringUtil.generateRandomString(5), StringUtil.generateRandomString(5));
    }

    LanguageManager.addLanguageFile("en", properties);
    for (Entry<String, String> entry : properties.entrySet()) {
      Assertions.assertEquals(entry.getValue(), LanguageManager.getMessage(entry.getKey()));
    }
  }
}
