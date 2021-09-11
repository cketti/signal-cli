/*
 * Copyright (C) 2021 Finn Herzfeld
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.finn.signald.clientprotocol.v1;

import org.asamk.signal.manager.Manager;

import java.util.stream.Collectors;

import io.finn.signald.annotations.Doc;
import io.finn.signald.annotations.ExampleValue;
import io.finn.signald.annotations.ProtocolType;
import io.finn.signald.annotations.Required;
import io.finn.signald.clientprotocol.Request;
import io.finn.signald.clientprotocol.RequestType;

@ProtocolType("get_linked_devices")
@Doc("list all linked devices on a Signal account")
public class GetLinkedDevicesRequest implements RequestType<LinkedDevices> {
  @ExampleValue(ExampleValue.LOCAL_PHONE_NUMBER) @Doc("The account to interact with") @Required public String account;

  @Override
  public LinkedDevices run(Request request) throws Exception {
    Manager manager = Utils.getManager(account);
    var deviceInfoList = manager.getLinkedDevices()
            .stream()
            .map((device) -> new DeviceInfo(device.getId(), device.getName(), device.getCreated(), device.getLastSeen()))
            .collect(Collectors.toList());

    return new LinkedDevices(deviceInfoList);
  }
}
