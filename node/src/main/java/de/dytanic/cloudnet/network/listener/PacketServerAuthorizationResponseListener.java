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

package de.dytanic.cloudnet.network.listener;

import de.dytanic.cloudnet.CloudNet;
import de.dytanic.cloudnet.cluster.IClusterNodeServer;
import de.dytanic.cloudnet.common.language.LanguageManager;
import de.dytanic.cloudnet.common.log.LogManager;
import de.dytanic.cloudnet.common.log.Logger;
import de.dytanic.cloudnet.driver.channel.ChannelMessage;
import de.dytanic.cloudnet.driver.network.INetworkChannel;
import de.dytanic.cloudnet.driver.network.def.NetworkConstants;
import de.dytanic.cloudnet.driver.network.protocol.IPacket;
import de.dytanic.cloudnet.driver.network.protocol.IPacketListener;
import de.dytanic.cloudnet.network.NodeNetworkUtils;
import java.util.Arrays;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public final class PacketServerAuthorizationResponseListener implements IPacketListener {

  private static final Logger LOGGER = LogManager.getLogger(PacketServerAuthorizationResponseListener.class);

  @Override
  public void handle(@NotNull INetworkChannel channel, @NotNull IPacket packet) {
    // check if the auth was successful
    if (packet.getContent().readBoolean()) {
      // search for the node to which the auth succeeded
      IClusterNodeServer server = CloudNet.getInstance().getConfig().getClusterConfig().getNodes().stream()
        .filter(node -> Arrays.stream(node.getListeners()).anyMatch(host -> channel.getServerAddress().equals(host)))
        .map(node -> CloudNet.getInstance().getClusterNodeServerProvider().getNodeServer(node.getUniqueId()))
        .filter(Objects::nonNull)
        .filter(node -> node.isAcceptableConnection(channel, node.getNodeInfo().getUniqueId()))
        .findFirst()
        .orElse(null);
      if (server != null) {
        server.setChannel(channel);
        // add the packet listeners
        channel.getPacketRegistry().removeListeners(NetworkConstants.INTERNAL_AUTHORIZATION_CHANNEL);
        NodeNetworkUtils.addDefaultPacketListeners(channel.getPacketRegistry(), CloudNet.getInstance());
        // send all cloud services of this node to the other node
        ChannelMessage.builder()
          .targetNode(server.getNodeInfo().getUniqueId())
          .channel(NetworkConstants.INTERNAL_MSG_CHANNEL)
          .message("request_initial_service_list_information")
          .build()
          .send();
        // we are good to go :)
        return;
      }
    }

    channel.close();
    LOGGER.warning(LanguageManager.getMessage("cluster-server-networking-authorization-failed"));
  }
}
