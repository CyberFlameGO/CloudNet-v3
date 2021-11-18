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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@ToString
@EqualsAndHashCode
public class SyncProxyTabListConfiguration {

  protected String targetGroup;

  protected List<SyncProxyTabList> entries;
  protected double animationsPerSecond;

  protected transient AtomicInteger currentEntry;

  public SyncProxyTabListConfiguration(
    @NotNull String targetGroup,
    @NotNull List<SyncProxyTabList> entries,
    double animationsPerSecond
  ) {
    this.targetGroup = targetGroup;
    this.entries = entries;
    this.animationsPerSecond = animationsPerSecond;

    this.currentEntry = new AtomicInteger(-1);
  }

  public static @NotNull SyncProxyTabListConfiguration createDefault(String targetGroup) {
    return new SyncProxyTabListConfiguration(
      targetGroup,
      Collections.singletonList(
        new SyncProxyTabList(
          " \n &b&o■ &8┃ &3&lCloudNet &8● &cBlizzard &8&l» &7&o%online_players%&8/&7&o%max_players% &8┃ &b&o■ "
            + "\n &8► &7Current server &8● &b%server% &8◄ \n ",
          " \n &7Discord &8&l» &bdiscord.cloudnetservice.eu \n &7&onext &3&l&ogeneration &7&onetwork \n "
        )
      ),
      1
    );
  }

  public @NotNull String getTargetGroup() {
    return this.targetGroup;
  }

  public void setTargetGroup(@NotNull String targetGroup) {
    this.targetGroup = targetGroup;
  }

  public @NotNull List<SyncProxyTabList> getEntries() {
    return this.entries;
  }

  public void setEntries(@NotNull List<SyncProxyTabList> entries) {
    this.entries = entries;
  }

  public double getAnimationsPerSecond() {
    return this.animationsPerSecond;
  }

  public void setAnimationsPerSecond(double animationsPerSecond) {
    this.animationsPerSecond = animationsPerSecond;
  }

  public @NotNull SyncProxyTabList tick() {
    if (this.currentEntry.incrementAndGet() >= this.entries.size()) {
      this.currentEntry.set(0);
    }

    return this.getCurrentEntry();
  }

  public @NotNull SyncProxyTabList getCurrentEntry() {
    return this.getEntries().get(this.getCurrentTick());
  }

  public int getCurrentTick() {
    return this.currentEntry.get();
  }

}
