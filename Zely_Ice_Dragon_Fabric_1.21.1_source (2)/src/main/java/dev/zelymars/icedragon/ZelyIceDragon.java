package dev.zelymars.icedragon;

import com.mojang.brigadier.Command;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;

import static net.minecraft.server.command.CommandManager.literal;

public class ZelyIceDragon implements ModInitializer {
    public static final String MOD_ID = "zely_ice_dragon";

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("zely")
                .then(literal("ice").executes(ctx -> ice(ctx.getSource().getPlayerOrThrow())))
                .then(literal("flight").executes(ctx -> flight(ctx.getSource().getPlayerOrThrow())))
                .then(literal("ground").executes(ctx -> ground(ctx.getSource().getPlayerOrThrow())))
                .then(literal("dragonfire").executes(ctx -> dragonfire(ctx.getSource().getPlayerOrThrow())))
                .then(literal("invisibility").executes(ctx -> invisibility(ctx.getSource().getPlayerOrThrow())))
                .then(literal("ancient").executes(ctx -> ancient(ctx.getSource().getPlayerOrThrow())))
                .then(literal("help").executes(ctx -> help(ctx.getSource().getPlayerOrThrow())))
            );
        });
    }

    private static int ice(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        Box area = player.getBoundingBox().expand(6.0);
        for (LivingEntity target : world.getEntitiesByClass(LivingEntity.class, area, e -> e != player)) {
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 4));
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 100, 1));
        }
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 60, 0));
        player.sendMessage(Text.literal("§b❄ Ледяная волна! Враги рядом замедлены."), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int flight(ServerPlayerEntity player) {
        player.getAbilities().allowFlying = true;
        player.getAbilities().flying = true;
        player.getAbilities().setFlySpeed(0.08f);
        player.sendAbilitiesUpdate();
        player.sendMessage(Text.literal("§b🐉 Драконьи крылья раскрыты! Используй прыжок дважды, чтобы летать."), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int ground(ServerPlayerEntity player) {
        player.getAbilities().flying = false;
        player.getAbilities().allowFlying = false;
        player.sendAbilitiesUpdate();
        player.sendMessage(Text.literal("§7Ты приземлился."), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int dragonfire(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        Box area = player.getBoundingBox().expand(5.0);
        int affected = 0;
        for (LivingEntity target : world.getEntitiesByClass(LivingEntity.class, area, e -> e != player)) {
            target.setOnFireFor(5);
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 0));
            affected++;
        }
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 80, 1));
        player.sendMessage(Text.literal("§5🔥 Фиолетовое драконье пламя! Затронуто целей: " + affected), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int invisibility(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 160, 0, false, false, true));
        player.sendMessage(Text.literal("§8Ты растворяешься в воздухе… Будь осторожен: рядом тебя могут заметить!"), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int ancient(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 240, 2));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 240, 1));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 240, 1));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 240, 2));
        player.sendMessage(Text.literal("§5✦ ДРЕВНЯЯ ДРАКОНЬЯ ЭНЕРГИЯ! Все силы усилены на 12 секунд."), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int help(ServerPlayerEntity player) {
        player.sendMessage(Text.literal("§b§lZELY: ICE & DRAGON MAGIC"), false);
        player.sendMessage(Text.literal("§b/zely ice §7— ледяная волна вокруг тебя"), false);
        player.sendMessage(Text.literal("§b/zely flight §7— включить полёт"), false);
        player.sendMessage(Text.literal("§b/zely ground §7— выключить полёт"), false);
        player.sendMessage(Text.literal("§5/zely dragonfire §7— фиолетовое драконье пламя"), false);
        player.sendMessage(Text.literal("§8/zely invisibility §7— невидимость на 8 секунд"), false);
        player.sendMessage(Text.literal("§d/zely ancient §7— древняя энергия на 12 секунд"), false);
        return Command.SINGLE_SUCCESS;
    }
}
