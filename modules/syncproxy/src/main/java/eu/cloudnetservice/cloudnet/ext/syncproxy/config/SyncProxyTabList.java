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

package eu.cloudnetservice.cloudnet.ext.syncproxy.config;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.IPermissionManagement;
import de.dytanic.cloudnet.driver.permission.PermissionGroup;
import de.dytanic.cloudnet.driver.permission.PermissionUser;
import de.dytanic.cloudnet.wrapper.Wrapper;
import eu.cloudnetservice.cloudnet.ext.syncproxy.SyncProxyConstants;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@ToString
@EqualsAndHashCode
public class SyncProxyTabList {

  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");

  protected String header;
  protected String footer;

  public SyncProxyTabList(@NotNull String header, @NotNull String footer) {
    this.header = header;
    this.footer = footer;
  }

  public static String replaceTabListItem(
    @NotNull String input,
    @NotNull UUID playerUniqueId,
    int onlinePlayers,
    int maxPlayers
  ) {
    input = input
      .replace("%proxy%", Wrapper.getInstance().getServiceId().getName())
      .replace("%proxy_uniqueId%", Wrapper.getInstance().getServiceId().getUniqueId().toString())
      .replace("%proxy_task_name%", Wrapper.getInstance().getServiceId().getTaskName())
      .replace("%time%", DATE_FORMAT.format(System.currentTimeMillis()))
      .replace("%online_players%", String.valueOf(onlinePlayers))
      .replace("%max_players%", String.valueOf(maxPlayers));

    if (SyncProxyConstants.CLOUD_PERMS_ENABLED) {
      IPermissionManagement permissionManagement = CloudNetDriver.getInstance().getPermissionManagement();

      PermissionUser permissionUser = permissionManagement.getUser(playerUniqueId);

      if (permissionUser != null) {
        PermissionGroup group = permissionManagement.getHighestPermissionGroup(permissionUser);

        if (group != null) {
          input = input.replace("%prefix%", group.getPrefix())
            .replace("%suffix%", group.getSuffix())
            .replace("%display%", group.getDisplay())
            .replace("%color%", group.getColor())
            .replace("%group%", group.getName());
        }
      }
    }

    return input.replace("&", "§");
  }

  public @NotNull String getHeader() {
    return this.header;
  }

  public void setHeader(@NotNull String header) {
    this.header = header;
  }

  public @NotNull String getFooter() {
    return this.footer;
  }

  public void setFooter(@NotNull String footer) {
    this.footer = footer;
  }
}
