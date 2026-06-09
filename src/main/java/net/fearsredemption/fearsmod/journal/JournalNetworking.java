package net.fearsredemption.fearsmod.journal;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fearsredemption.fearsmod.FearsMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public final class JournalNetworking {
    private JournalNetworking() {
    }

    public static void initializeServer() {
        PayloadTypeRegistry.serverboundPlay().register(OpenJournalRequestPayload.TYPE, OpenJournalRequestPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(OpenJournalPayload.TYPE, OpenJournalPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(OpenJournalRequestPayload.TYPE, (payload, context) -> {
            JournalUnlocks.refresh(context.player());
            send(context.player(), true);
        });
    }

    public static void sync(ServerPlayer player) {
        send(player, false);
    }

    private static void send(ServerPlayer player, boolean openJournal) {
        if (ServerPlayNetworking.canSend(player, OpenJournalPayload.TYPE)) {
            ServerPlayNetworking.send(player, new OpenJournalPayload(JournalUnlocks.unlockedPageIds(player), openJournal));
        }
    }

    public record OpenJournalRequestPayload() implements CustomPacketPayload {
        public static final OpenJournalRequestPayload INSTANCE = new OpenJournalRequestPayload();
        public static final Type<OpenJournalRequestPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(FearsMod.MOD_ID, "open_journal"));
        public static final StreamCodec<RegistryFriendlyByteBuf, OpenJournalRequestPayload> CODEC = StreamCodec.unit(INSTANCE);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record OpenJournalPayload(List<String> unlockedPages, boolean openJournal) implements CustomPacketPayload {
        public static final Type<OpenJournalPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(FearsMod.MOD_ID, "journal_pages"));
        public static final StreamCodec<RegistryFriendlyByteBuf, OpenJournalPayload> CODEC = StreamCodec.of(
                (buf, payload) -> {
                    buf.writeBoolean(payload.openJournal());
                    buf.writeVarInt(payload.unlockedPages().size());
                    payload.unlockedPages().forEach(buf::writeUtf);
                },
                buf -> {
                    boolean openJournal = buf.readBoolean();
                    int size = buf.readVarInt();
                    List<String> ids = new ArrayList<>();
                    for (int i = 0; i < size; i++) {
                        ids.add(buf.readUtf(128));
                    }
                    return new OpenJournalPayload(ids, openJournal);
                }
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
