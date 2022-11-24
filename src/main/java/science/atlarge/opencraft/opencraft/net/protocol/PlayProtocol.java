package science.atlarge.opencraft.opencraft.net.protocol;

import science.atlarge.opencraft.opencraft.net.codec.KickCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.entity.AnimateEntityCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.entity.AttachEntityCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.entity.CollectItemCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.entity.DestroyEntitiesCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.entity.EntityEffectCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.entity.EntityEquipmentCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.entity.EntityHeadRotationCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.entity.EntityMetadataCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.entity.EntityPropertyCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.entity.EntityRemoveEffectCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.entity.EntityRotationCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.entity.EntityStatusCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.entity.EntityTeleportCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.entity.EntityVelocityCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.entity.RelativeEntityPositionCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.entity.RelativeEntityPositionRotationCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.entity.SetCooldownCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.entity.SetPassengerCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.entity.SpawnLightningStrikeCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.entity.SpawnMobCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.entity.SpawnObjectCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.entity.SpawnPaintingCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.entity.SpawnPlayerCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.entity.SpawnXpOrbCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.entity.VehicleMoveCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.BlockActionCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.BlockBreakAnimationCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.BlockChangeCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.ChatCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.ChunkDataCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.ClientSettingsCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.CraftRecipeRequestCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.CraftRecipeResponseCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.CraftingBookDataCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.ExperienceCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.ExplosionCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.HealthCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.IncomingChatCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.JoinGameCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.MapDataCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.MultiBlockChangeCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.NamedSoundEffectCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.PingCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.PlayEffectCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.PlayParticleCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.PluginMessageCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.PositionRotationCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.RespawnCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.SignEditorCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.SoundEffectCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.SpawnPositionCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.StateChangeCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.StatisticCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.TimeCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.TitleCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.UnloadChunkCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.UnlockRecipesCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.UpdateBlockEntityCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.UpdateSignCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.UserListHeaderFooterCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.UserListItemCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.game.WorldBorderCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.inv.CloseWindowCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.inv.CreativeItemCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.inv.EnchantItemCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.inv.HeldItemCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.inv.OpenWindowCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.inv.SetWindowContentsCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.inv.SetWindowSlotCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.inv.TransactionCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.inv.WindowClickCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.inv.WindowPropertyCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.player.AdvancementTabCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.player.AdvancementsCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.player.BlockPlacementCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.player.BossBarCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.player.CameraCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.player.ClientStatusCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.player.CombatEventCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.player.DiggingCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.player.InteractEntityCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.player.PlayerAbilitiesCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.player.PlayerActionCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.player.PlayerLookCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.player.PlayerPositionCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.player.PlayerPositionLookCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.player.PlayerSwingArmCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.player.PlayerUpdateCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.player.ResourcePackSendCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.player.ResourcePackStatusCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.player.ServerDifficultyCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.player.SpectateCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.player.SteerBoatCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.player.SteerVehicleCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.player.TabCompleteCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.player.TabCompleteResponseCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.player.TeleportConfirmCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.player.UseBedCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.player.UseItemCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.scoreboard.ScoreboardDisplayCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.scoreboard.ScoreboardObjectiveCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.scoreboard.ScoreboardScoreCodec;
import science.atlarge.opencraft.opencraft.net.codec.play.scoreboard.ScoreboardTeamCodec;
import science.atlarge.opencraft.opencraft.net.handler.play.game.ClientSettingsHandler;
import science.atlarge.opencraft.opencraft.net.handler.play.game.CraftRecipeRequestHandler;
import science.atlarge.opencraft.opencraft.net.handler.play.game.CraftingBookDataHandler;
import science.atlarge.opencraft.opencraft.net.handler.play.game.IncomingChatHandler;
import science.atlarge.opencraft.opencraft.net.handler.play.game.PingHandler;
import science.atlarge.opencraft.opencraft.net.handler.play.game.PluginMessageHandler;
import science.atlarge.opencraft.opencraft.net.handler.play.game.UpdateSignHandler;
import science.atlarge.opencraft.opencraft.net.handler.play.inv.CloseWindowHandler;
import science.atlarge.opencraft.opencraft.net.handler.play.inv.CreativeItemHandler;
import science.atlarge.opencraft.opencraft.net.handler.play.inv.EnchantItemHandler;
import science.atlarge.opencraft.opencraft.net.handler.play.inv.HeldItemHandler;
import science.atlarge.opencraft.opencraft.net.handler.play.inv.TransactionHandler;
import science.atlarge.opencraft.opencraft.net.handler.play.inv.VehicleMoveHandler;
import science.atlarge.opencraft.opencraft.net.handler.play.inv.WindowClickHandler;
import science.atlarge.opencraft.opencraft.net.handler.play.player.AdvancementTabHandler;
import science.atlarge.opencraft.opencraft.net.handler.play.player.BlockPlacementHandler;
import science.atlarge.opencraft.opencraft.net.handler.play.player.ClientStatusHandler;
import science.atlarge.opencraft.opencraft.net.handler.play.player.DiggingHandler;
import science.atlarge.opencraft.opencraft.net.handler.play.player.InteractEntityHandler;
import science.atlarge.opencraft.opencraft.net.handler.play.player.PlayerAbilitiesHandler;
import science.atlarge.opencraft.opencraft.net.handler.play.player.PlayerActionHandler;
import science.atlarge.opencraft.opencraft.net.handler.play.player.PlayerSwingArmHandler;
import science.atlarge.opencraft.opencraft.net.handler.play.player.PlayerUpdateHandler;
import science.atlarge.opencraft.opencraft.net.handler.play.player.ResourcePackStatusHandler;
import science.atlarge.opencraft.opencraft.net.handler.play.player.SpectateHandler;
import science.atlarge.opencraft.opencraft.net.handler.play.player.SteerBoatHandler;
import science.atlarge.opencraft.opencraft.net.handler.play.player.SteerVehicleHandler;
import science.atlarge.opencraft.opencraft.net.handler.play.player.TabCompleteHandler;
import science.atlarge.opencraft.opencraft.net.handler.play.player.TeleportConfirmHandler;
import science.atlarge.opencraft.opencraft.net.handler.play.player.UseItemHandler;
import science.atlarge.opencraft.opencraft.net.message.KickMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.AnimateEntityMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.AttachEntityMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.CollectItemMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.DestroyEntitiesMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.EntityEffectMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.EntityEquipmentMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.EntityHeadRotationMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.EntityMetadataMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.EntityPropertyMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.EntityRemoveEffectMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.EntityRotationMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.EntityStatusMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.EntityTeleportMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.EntityVelocityMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.RelativeEntityPositionMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.RelativeEntityPositionRotationMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.SetCooldownMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.SetPassengerMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.SpawnLightningStrikeMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.SpawnMobMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.SpawnObjectMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.SpawnPaintingMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.SpawnPlayerMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.SpawnXpOrbMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.VehicleMoveMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.BlockActionMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.BlockBreakAnimationMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.BlockChangeMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.ChatMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.ChunkDataMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.ClientSettingsMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.CraftRecipeRequestMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.CraftRecipeResponseMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.CraftingBookDataMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.ExperienceMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.ExplosionMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.HealthMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.IncomingChatMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.JoinGameMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.MapDataMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.MultiBlockChangeMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.NamedSoundEffectMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.PingMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.PlayEffectMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.PlayParticleMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.PluginMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.PositionRotationMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.RespawnMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.SignEditorMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.SoundEffectMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.SpawnPositionMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.StateChangeMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.StatisticMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.TimeMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.TitleMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.UnloadChunkMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.UnlockRecipesMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.UpdateBlockEntityMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.UpdateSignMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.UserListHeaderFooterMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.UserListItemMessage;
import science.atlarge.opencraft.opencraft.net.message.play.game.WorldBorderMessage;
import science.atlarge.opencraft.opencraft.net.message.play.inv.CloseWindowMessage;
import science.atlarge.opencraft.opencraft.net.message.play.inv.CreativeItemMessage;
import science.atlarge.opencraft.opencraft.net.message.play.inv.EnchantItemMessage;
import science.atlarge.opencraft.opencraft.net.message.play.inv.HeldItemMessage;
import science.atlarge.opencraft.opencraft.net.message.play.inv.OpenWindowMessage;
import science.atlarge.opencraft.opencraft.net.message.play.inv.SetWindowContentsMessage;
import science.atlarge.opencraft.opencraft.net.message.play.inv.SetWindowSlotMessage;
import science.atlarge.opencraft.opencraft.net.message.play.inv.TransactionMessage;
import science.atlarge.opencraft.opencraft.net.message.play.inv.WindowClickMessage;
import science.atlarge.opencraft.opencraft.net.message.play.inv.WindowPropertyMessage;
import science.atlarge.opencraft.opencraft.net.message.play.player.AdvancementTabMessage;
import science.atlarge.opencraft.opencraft.net.message.play.player.AdvancementsMessage;
import science.atlarge.opencraft.opencraft.net.message.play.player.BlockPlacementMessage;
import science.atlarge.opencraft.opencraft.net.message.play.player.BossBarMessage;
import science.atlarge.opencraft.opencraft.net.message.play.player.CameraMessage;
import science.atlarge.opencraft.opencraft.net.message.play.player.ClientStatusMessage;
import science.atlarge.opencraft.opencraft.net.message.play.player.CombatEventMessage;
import science.atlarge.opencraft.opencraft.net.message.play.player.DiggingMessage;
import science.atlarge.opencraft.opencraft.net.message.play.player.InteractEntityMessage;
import science.atlarge.opencraft.opencraft.net.message.play.player.PlayerAbilitiesMessage;
import science.atlarge.opencraft.opencraft.net.message.play.player.PlayerActionMessage;
import science.atlarge.opencraft.opencraft.net.message.play.player.PlayerLookMessage;
import science.atlarge.opencraft.opencraft.net.message.play.player.PlayerPositionLookMessage;
import science.atlarge.opencraft.opencraft.net.message.play.player.PlayerPositionMessage;
import science.atlarge.opencraft.opencraft.net.message.play.player.PlayerSwingArmMessage;
import science.atlarge.opencraft.opencraft.net.message.play.player.PlayerUpdateMessage;
import science.atlarge.opencraft.opencraft.net.message.play.player.ResourcePackSendMessage;
import science.atlarge.opencraft.opencraft.net.message.play.player.ResourcePackStatusMessage;
import science.atlarge.opencraft.opencraft.net.message.play.player.ServerDifficultyMessage;
import science.atlarge.opencraft.opencraft.net.message.play.player.SpectateMessage;
import science.atlarge.opencraft.opencraft.net.message.play.player.SteerBoatMessage;
import science.atlarge.opencraft.opencraft.net.message.play.player.SteerVehicleMessage;
import science.atlarge.opencraft.opencraft.net.message.play.player.TabCompleteMessage;
import science.atlarge.opencraft.opencraft.net.message.play.player.TabCompleteResponseMessage;
import science.atlarge.opencraft.opencraft.net.message.play.player.TeleportConfirmMessage;
import science.atlarge.opencraft.opencraft.net.message.play.player.UseBedMessage;
import science.atlarge.opencraft.opencraft.net.message.play.player.UseItemMessage;
import science.atlarge.opencraft.opencraft.net.message.play.scoreboard.ScoreboardDisplayMessage;
import science.atlarge.opencraft.opencraft.net.message.play.scoreboard.ScoreboardObjectiveMessage;
import science.atlarge.opencraft.opencraft.net.message.play.scoreboard.ScoreboardScoreMessage;
import science.atlarge.opencraft.opencraft.net.message.play.scoreboard.ScoreboardTeamMessage;

public class PlayProtocol extends GlowProtocol {

    /**
     * Creates the instance for the game's main network protocol.
     */
    public PlayProtocol() {
        super("PLAY", 0x4F);

        inbound(0x00, TeleportConfirmMessage.class, TeleportConfirmCodec.class,
                TeleportConfirmHandler.class);
        inbound(0x01, TabCompleteMessage.class, TabCompleteCodec.class, TabCompleteHandler.class);
        inbound(0x02, IncomingChatMessage.class, IncomingChatCodec.class,
                IncomingChatHandler.class);
        inbound(0x03, ClientStatusMessage.class, ClientStatusCodec.class,
                ClientStatusHandler.class);
        inbound(0x04, ClientSettingsMessage.class, ClientSettingsCodec.class,
                ClientSettingsHandler.class);
        inbound(0x05, TransactionMessage.class, TransactionCodec.class, TransactionHandler.class);
        inbound(0x06, EnchantItemMessage.class, EnchantItemCodec.class, EnchantItemHandler.class);
        inbound(0x07, WindowClickMessage.class, WindowClickCodec.class, WindowClickHandler.class);
        inbound(0x08, CloseWindowMessage.class, CloseWindowCodec.class, CloseWindowHandler.class);
        inbound(0x09, PluginMessage.class, PluginMessageCodec.class, PluginMessageHandler.class);
        inbound(0x0A, InteractEntityMessage.class, InteractEntityCodec.class,
                InteractEntityHandler.class);
        inbound(0x0B, PingMessage.class, PingCodec.class, PingHandler.class);
        inbound(0x0C, PlayerUpdateMessage.class, PlayerUpdateCodec.class,
                PlayerUpdateHandler.class);
        inbound(0x0D, PlayerPositionMessage.class, PlayerPositionCodec.class,
                PlayerUpdateHandler.class);
        inbound(0x0E, PlayerPositionLookMessage.class, PlayerPositionLookCodec.class,
                PlayerUpdateHandler.class);
        inbound(0x0F, PlayerLookMessage.class, PlayerLookCodec.class, PlayerUpdateHandler.class);
        inbound(0x10, VehicleMoveMessage.class, VehicleMoveCodec.class, VehicleMoveHandler.class);
        inbound(0x11, SteerBoatMessage.class, SteerBoatCodec.class, SteerBoatHandler.class);
        inbound(0x12, CraftRecipeRequestMessage.class, CraftRecipeRequestCodec.class,
                CraftRecipeRequestHandler.class);
        inbound(0x13, PlayerAbilitiesMessage.class, PlayerAbilitiesCodec.class,
                PlayerAbilitiesHandler.class);
        inbound(0x14, DiggingMessage.class, DiggingCodec.class, DiggingHandler.class);
        inbound(0x15, PlayerActionMessage.class, PlayerActionCodec.class,
                PlayerActionHandler.class);
        inbound(0x16, SteerVehicleMessage.class, SteerVehicleCodec.class,
                SteerVehicleHandler.class);
        inbound(0x17, CraftingBookDataMessage.class, CraftingBookDataCodec.class,
                CraftingBookDataHandler.class);
        inbound(0x18, ResourcePackStatusMessage.class, ResourcePackStatusCodec.class,
                ResourcePackStatusHandler.class);
        inbound(0x19, AdvancementTabMessage.class, AdvancementTabCodec.class,
                AdvancementTabHandler.class);
        inbound(0x1A, HeldItemMessage.class, HeldItemCodec.class, HeldItemHandler.class);
        inbound(0x1B, CreativeItemMessage.class, CreativeItemCodec.class,
                CreativeItemHandler.class);
        inbound(0x1C, UpdateSignMessage.class, UpdateSignCodec.class, UpdateSignHandler.class);
        inbound(0x1D, PlayerSwingArmMessage.class, PlayerSwingArmCodec.class,
                PlayerSwingArmHandler.class);
        inbound(0x1E, SpectateMessage.class, SpectateCodec.class, SpectateHandler.class);
        inbound(0x1F, BlockPlacementMessage.class, BlockPlacementCodec.class,
                BlockPlacementHandler.class);
        inbound(0x20, UseItemMessage.class, UseItemCodec.class, UseItemHandler.class);

        outbound(0x00, SpawnObjectMessage.class, SpawnObjectCodec.class);
        outbound(0x01, SpawnXpOrbMessage.class, SpawnXpOrbCodec.class);
        outbound(0x02, SpawnLightningStrikeMessage.class, SpawnLightningStrikeCodec.class);
        outbound(0x03, SpawnMobMessage.class, SpawnMobCodec.class);
        outbound(0x04, SpawnPaintingMessage.class, SpawnPaintingCodec.class);
        outbound(0x05, SpawnPlayerMessage.class, SpawnPlayerCodec.class);
        outbound(0x06, AnimateEntityMessage.class, AnimateEntityCodec.class);
        outbound(0x07, StatisticMessage.class, StatisticCodec.class);
        outbound(0x08, BlockBreakAnimationMessage.class, BlockBreakAnimationCodec.class);
        outbound(0x09, UpdateBlockEntityMessage.class, UpdateBlockEntityCodec.class);
        outbound(0x0A, BlockActionMessage.class, BlockActionCodec.class);
        outbound(0x0B, BlockChangeMessage.class, BlockChangeCodec.class);
        outbound(0x0C, BossBarMessage.class, BossBarCodec.class);
        outbound(0x0D, ServerDifficultyMessage.class, ServerDifficultyCodec.class);
        outbound(0x0E, TabCompleteResponseMessage.class, TabCompleteResponseCodec.class);
        outbound(0x0F, ChatMessage.class, ChatCodec.class);
        outbound(0x10, MultiBlockChangeMessage.class, MultiBlockChangeCodec.class);
        outbound(0x11, TransactionMessage.class, TransactionCodec.class);
        outbound(0x12, CloseWindowMessage.class, CloseWindowCodec.class);
        outbound(0x13, OpenWindowMessage.class, OpenWindowCodec.class);
        outbound(0x14, SetWindowContentsMessage.class, SetWindowContentsCodec.class);
        outbound(0x15, WindowPropertyMessage.class, WindowPropertyCodec.class);
        outbound(0x16, SetWindowSlotMessage.class, SetWindowSlotCodec.class);
        outbound(0x17, SetCooldownMessage.class, SetCooldownCodec.class);
        outbound(0x18, PluginMessage.class, PluginMessageCodec.class);
        outbound(0x19, NamedSoundEffectMessage.class, NamedSoundEffectCodec.class);
        outbound(0x1A, KickMessage.class, KickCodec.class);
        outbound(0x1B, EntityStatusMessage.class, EntityStatusCodec.class);
        outbound(0x1C, ExplosionMessage.class, ExplosionCodec.class);
        outbound(0x1D, UnloadChunkMessage.class, UnloadChunkCodec.class);
        outbound(0x1E, StateChangeMessage.class, StateChangeCodec.class);
        outbound(0x1F, PingMessage.class, PingCodec.class);
        outbound(0x20, ChunkDataMessage.class, ChunkDataCodec.class);
        outbound(0x21, PlayEffectMessage.class, PlayEffectCodec.class);
        outbound(0x22, PlayParticleMessage.class, PlayParticleCodec.class);
        outbound(0x23, JoinGameMessage.class, JoinGameCodec.class);
        outbound(0x24, MapDataMessage.class, MapDataCodec.class);
        // TODO 0x25 : Entity packet
        outbound(0x26, RelativeEntityPositionMessage.class, RelativeEntityPositionCodec.class);
        outbound(0x27, RelativeEntityPositionRotationMessage.class,
                RelativeEntityPositionRotationCodec.class);
        outbound(0x28, EntityRotationMessage.class, EntityRotationCodec.class);
        outbound(0x29, VehicleMoveMessage.class, VehicleMoveCodec.class);
        outbound(0x2A, SignEditorMessage.class, SignEditorCodec.class);
        outbound(0x2B, CraftRecipeResponseMessage.class, CraftRecipeResponseCodec.class);
        outbound(0x2C, PlayerAbilitiesMessage.class, PlayerAbilitiesCodec.class);
        outbound(0x2D, CombatEventMessage.class, CombatEventCodec.class);
        outbound(0x2E, UserListItemMessage.class, UserListItemCodec.class);
        outbound(0x2F, PositionRotationMessage.class, PositionRotationCodec.class);
        outbound(0x30, UseBedMessage.class, UseBedCodec.class);
        outbound(0x31, UnlockRecipesMessage.class, UnlockRecipesCodec.class);
        outbound(0x32, DestroyEntitiesMessage.class, DestroyEntitiesCodec.class);
        outbound(0x33, EntityRemoveEffectMessage.class, EntityRemoveEffectCodec.class);
        outbound(0x34, ResourcePackSendMessage.class, ResourcePackSendCodec.class);
        outbound(0x35, RespawnMessage.class, RespawnCodec.class);
        outbound(0x36, EntityHeadRotationMessage.class, EntityHeadRotationCodec.class);
        // TODO 0x37 : Select Advancement Tab
        outbound(0x38, WorldBorderMessage.class, WorldBorderCodec.class);
        outbound(0x39, CameraMessage.class, CameraCodec.class);
        outbound(0x3A, HeldItemMessage.class, HeldItemCodec.class);
        outbound(0x3B, ScoreboardDisplayMessage.class, ScoreboardDisplayCodec.class);
        outbound(0x3C, EntityMetadataMessage.class, EntityMetadataCodec.class);
        outbound(0x3D, AttachEntityMessage.class, AttachEntityCodec.class);
        outbound(0x3E, EntityVelocityMessage.class, EntityVelocityCodec.class);
        outbound(0x3F, EntityEquipmentMessage.class, EntityEquipmentCodec.class);
        outbound(0x40, ExperienceMessage.class, ExperienceCodec.class);
        outbound(0x41, HealthMessage.class, HealthCodec.class);
        outbound(0x42, ScoreboardObjectiveMessage.class, ScoreboardObjectiveCodec.class);
        outbound(0x43, SetPassengerMessage.class, SetPassengerCodec.class);
        outbound(0x44, ScoreboardTeamMessage.class, ScoreboardTeamCodec.class);
        outbound(0x45, ScoreboardScoreMessage.class, ScoreboardScoreCodec.class);
        outbound(0x46, SpawnPositionMessage.class, SpawnPositionCodec.class);
        outbound(0x47, TimeMessage.class, TimeCodec.class);
        outbound(0x48, TitleMessage.class, TitleCodec.class);
        outbound(0x49, SoundEffectMessage.class, SoundEffectCodec.class);
        outbound(0x4A, UserListHeaderFooterMessage.class, UserListHeaderFooterCodec.class);
        outbound(0x4B, CollectItemMessage.class, CollectItemCodec.class);
        outbound(0x4C, EntityTeleportMessage.class, EntityTeleportCodec.class);
        outbound(0x4D, AdvancementsMessage.class, AdvancementsCodec.class);
        outbound(0x4E, EntityPropertyMessage.class, EntityPropertyCodec.class);
        outbound(0x4F, EntityEffectMessage.class, EntityEffectCodec.class);
    }
}
