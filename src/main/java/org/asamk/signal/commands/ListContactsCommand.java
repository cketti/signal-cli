package org.asamk.signal.commands;

import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

import org.asamk.signal.JsonWriter;
import org.asamk.signal.OutputWriter;
import org.asamk.signal.PlainTextWriter;
import org.asamk.signal.manager.Manager;

import java.util.stream.Collectors;

import static org.asamk.signal.util.Util.getLegacyIdentifier;

public class ListContactsCommand implements JsonRpcLocalCommand {

    @Override
    public String getName() {
        return "listContacts";
    }

    @Override
    public void attachToSubparser(final Subparser subparser) {
        subparser.help("Show a list of known contacts with names.");
    }

    @Override
    public void handleCommand(final Namespace ns, final Manager m, final OutputWriter outputWriter) {
        var contacts = m.getContacts();

        if (outputWriter instanceof PlainTextWriter) {
            final var writer = (PlainTextWriter) outputWriter;
            for (var c : contacts) {
                final var contact = c.second();
                writer.println("Number: {} Name: {} Blocked: {} Message expiration: {}",
                        getLegacyIdentifier(m.resolveSignalServiceAddress(c.first())),
                        contact.getName(),
                        contact.isBlocked(),
                        contact.getMessageExpirationTime() == 0
                                ? "disabled"
                                : contact.getMessageExpirationTime() + "s");
            }
        } else {
            final var writer = (JsonWriter) outputWriter;
            final var jsonContacts = contacts.stream().map(contactPair -> {
                final var address = m.resolveSignalServiceAddress(contactPair.first());
                final var contact = contactPair.second();
                return new JsonContact(address.getNumber().orNull(),
                        address.getUuid().toString(),
                        contact.getName(),
                        contact.isBlocked(),
                        contact.getMessageExpirationTime());
            }).collect(Collectors.toList());

            writer.write(jsonContacts);
        }
    }

    private static final class JsonContact {

        public final String number;
        public final String uuid;
        public final String name;
        public final boolean isBlocked;
        public final int messageExpirationTime;

        private JsonContact(
                final String number,
                final String uuid,
                final String name,
                final boolean isBlocked,
                final int messageExpirationTime
        ) {
            this.number = number;
            this.uuid = uuid;
            this.name = name;
            this.isBlocked = isBlocked;
            this.messageExpirationTime = messageExpirationTime;
        }
    }
}
