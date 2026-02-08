package peick.scathapro.overlay;

import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import peick.scathapro.Constants;
import peick.scathapro.ScathaPro;
import peick.scathapro.managers.Config;
import peick.scathapro.miscellaneous.OverlayIconEyePositions;
import peick.scathapro.miscellaneous.enums.WormStatsType;
import peick.scathapro.overlay.elements.*;
import peick.scathapro.overlay.elements.DynamicOverlayContainer.Direction;
import peick.scathapro.overlay.elements.OverlayElement.Alignment;
import peick.scathapro.overlay.enums.OverlayPosition;
import peick.scathapro.util.TextUtil;
import peick.scathapro.util.TimeUtil;
import peick.scathapro.util.Util;

public class Overlay {

    // ============================================================
    // STATE
    // ============================================================

    private final ScathaPro scathaPro;
    private final MinecraftClient mc;

    private final DynamicOverlayContainer mainContainer;

    private OverlayElement highlightedElement = null;
    private Alignment contentAlignment = Alignment.LEFT;
    private WormStatsType statsType = WormStatsType.PER_LOBBY;

    // ============================================================
    // UI ELEMENTS
    // ============================================================

    private final OverlayText titleText;
    private final AnimatedOverlayImage scathaIcon;
    private final OverlayImage scathaIconOverlay;

    private final OverlayText regularWormKillsText;
    private final OverlayText secondaryRegularWormKillsText;

    private final OverlayText scathaKillsTitleText;
    private final OverlayText scathaKillsText;
    private final OverlayText secondaryScathaKillsText;

    private final OverlayProgressBar spawnCooldownProgressBar;
    private final AnomalousDesireEffectProgressBar anomalousDesireEffectProgressBar;

    private final OverlayText totalKillsText;
    private final OverlayText secondaryTotalKillsText;

    private final OverlayText wormStreakText;
    private final OverlayText coordsText;
    private final OverlayText lobbyTimeText;

    private final OverlayText rarePetDropsText;
    private final OverlayText epicPetDropsText;
    private final OverlayText legendaryPetDropsText;

    private final OverlayText scathaKillsSinceLastDropText;
    private final OverlayText spawnCooldownTimerText;
    private final OverlayText anomalousDesireStatusText;
    private final OverlayText wormSpawnTimerText;
    private final OverlayText profileStatsText;
    private final OverlayText realTimeClockText;

    private final OverlayContainer googlyEyeLeftContainer;
    private final OverlayImage googlyEyeLeftInnerImage;
    private final OverlayContainer googlyEyeRightContainer;
    private final OverlayImage googlyEyeRightInnerImage;

    public final List<ToggleableOverlayElement> toggleableOverlayElements = Lists.newArrayList();

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public Overlay(ScathaPro scathaPro) {
        this.scathaPro = scathaPro;
        this.mc = MinecraftClient.getInstance();

        // Load stats mode
        statsType = scathaPro.getConfig().getEnum(Config.Key.statsType, WormStatsType.class);
        if (statsType == null) statsType = WormStatsType.PER_LOBBY;

        // Root container
        mainContainer = new DynamicOverlayContainer(0, 0, 1f, Direction.VERTICAL);
        mainContainer.padding = 5;

        // Constructor body fortsætter i DEL 2...
    }
        //
        // ============================================================
        // LAYOUT: HEADER
        // ============================================================
        //

        OverlayContainer headerContainer = new OverlayContainer(0, 0, 1f)
                .setMargin(0, 5);

        // Ikon + overlay
        OverlayContainer iconContainer = new OverlayContainer(0, 0, 0.25f);
        scathaIcon = new AnimatedOverlayImage(0, 0, 0.688f);
        scathaIconOverlay = new OverlayImage(0, 0, 0.688f);

        iconContainer.add(scathaIcon);
        iconContainer.add(scathaIconOverlay);

        //
        // GOOGLY EYES (optimeret)
        //
        googlyEyeLeftContainer = new OverlayContainer(0, 0, 0.44f);
        googlyEyeLeftContainer.expandsContainerSize = false;

        OverlayImage leftOuter = new OverlayImage(0, 0, 1f);
        leftOuter.setImage("overlay/googly_eye_outer.png", 32, 32);
        googlyEyeLeftContainer.add(leftOuter);

        googlyEyeLeftInnerImage = new OverlayImage(0, 0, 1f);
        googlyEyeLeftInnerImage.setImage("overlay/googly_eye_inner.png", 32, 32);
        googlyEyeLeftInnerImage.expandsContainerSize = false;
        googlyEyeLeftContainer.add(googlyEyeLeftInnerImage);

        googlyEyeRightContainer = new OverlayContainer(0, 0, 0.4f);
        googlyEyeRightContainer.expandsContainerSize = false;

        OverlayImage rightOuter = new OverlayImage(0, 0, 1f);
        rightOuter.setImage("overlay/googly_eye_outer.png", 32, 32);
        googlyEyeRightContainer.add(rightOuter);

        googlyEyeRightInnerImage = new OverlayImage(0, 0, 1f);
        googlyEyeRightInnerImage.setImage("overlay/googly_eye_inner.png", 32, 32);
        googlyEyeRightInnerImage.expandsContainerSize = false;
        googlyEyeRightContainer.add(googlyEyeRightInnerImage);

        iconContainer.add(googlyEyeLeftContainer);
        iconContainer.add(googlyEyeRightContainer);

        // Titel
        titleText = new OverlayText(null, Util.Color.GOLD, 16, 0, 1.3f);

        headerContainer.add(iconContainer);
        headerContainer.add(titleText);

        mainContainer.add(headerContainer);
        addToggleableElement("header", "Title", headerContainer, true);


        //
        // ============================================================
        // LAYOUT: COUNTERS (PET DROPS + KILLS)
        // ============================================================
        //

        DynamicOverlayContainer countersContainer =
                new DynamicOverlayContainer(0, 0, 1f, Direction.HORIZONTAL)
                        .setMargin(0, 4);

        //
        // PET DROPS
        //
        OverlayContainer petDrops = new OverlayContainer(0, 0, 1f);
        petDrops.add(new OverlayText("Pets", Util.Color.GREEN, 0, 0, 1f));

        petDrops.add(new OverlayImage("overlay/scatha_pet_rare.png", 64, 64, 0, 10, 0.145f));
        rarePetDropsText = new OverlayText(null, Util.Color.BLUE, 12, 11, 1f);
        petDrops.add(rarePetDropsText);

        petDrops.add(new OverlayImage("overlay/scatha_pet_epic.png", 64, 64, 0, 21, 0.145f));
        epicPetDropsText = new OverlayText(null, Util.Color.DARK_PURPLE, 12, 22, 1f);
        petDrops.add(epicPetDropsText);

        petDrops.add(new OverlayImage("overlay/scatha_pet_legendary.png", 64, 64, 0, 32, 0.145f));
        legendaryPetDropsText = new OverlayText(null, Util.Color.GOLD, 12, 33, 1f);
        petDrops.add(legendaryPetDropsText);

        countersContainer.add(petDrops);
        addToggleableElement("petDrops", "Pet Drop Counters", petDrops, true);


        //
        // WORM + SCATHA KILLS
        //
        OverlayContainer kills = new OverlayContainer(8, 0, 1f);

        anomalousDesireEffectProgressBar =
                new AnomalousDesireEffectProgressBar(0, 10, 77, 21, 1f);
        spawnCooldownProgressBar =
                new OverlayProgressBar(0, 10, 77, 21, 1f, 0x50FFFFFF, -1);

        kills.add(anomalousDesireEffectProgressBar);
        kills.add(spawnCooldownProgressBar);

        kills.add(new OverlayText("Worms", Util.Color.YELLOW, 15, 0, 1f)
                .setAlignment(Alignment.CENTER));
        kills.add(new OverlayImage("overlay/worm.png", 512, 256, -5, 10, 0.08f));

        regularWormKillsText =
                new OverlayText(null, Util.Color.WHITE, 15, 11, 1f)
                        .setAlignment(Alignment.CENTER);
        secondaryRegularWormKillsText =
                new OverlayText(null, Util.Color.GRAY, 15, 22, 1f)
                        .setAlignment(Alignment.CENTER);

        kills.add(regularWormKillsText);
        kills.add(secondaryRegularWormKillsText);

        scathaKillsTitleText =
                new OverlayText(null, Util.Color.YELLOW, 58, 0, 1f)
                        .setAlignment(Alignment.CENTER);
        kills.add(scathaKillsTitleText);

        kills.add(new OverlayImage("overlay/scatha.png", 512, 256, 38, 10, 0.08f));

        scathaKillsText =
                new OverlayText(null, Util.Color.WHITE, 58, 11, 1f)
                        .setAlignment(Alignment.CENTER);
        secondaryScathaKillsText =
                new OverlayText(null, Util.Color.GRAY, 58, 22, 1f)
                        .setAlignment(Alignment.CENTER);

        kills.add(scathaKillsText);
        kills.add(secondaryScathaKillsText);

        kills.add(new OverlayText("Total", Util.Color.WHITE, 86, 0, 1f));
        totalKillsText = new OverlayText(null, Util.Color.WHITE, 86, 11, 1f);
        secondaryTotalKillsText = new OverlayText(null, Util.Color.GRAY, 86, 22, 1f);

        kills.add(totalKillsText);
        kills.add(secondaryTotalKillsText);

        wormStreakText = new OverlayText(null, Util.Color.GRAY, 0, 33, 1f);
        kills.add(wormStreakText);

        countersContainer.add(kills);
        addToggleableElement("wormStats", "Worm Stats", kills, true);

        mainContainer.add(countersContainer);


        //
        // ============================================================
        // LAYOUT: MISC TEXT ELEMENTS
        // ============================================================
        //

        scathaKillsSinceLastDropText = new OverlayText(null, Util.Color.WHITE, 0, 2, 1f);
        spawnCooldownTimerText = new OverlayText(null, Util.Color.WHITE, 0, 2, 1f);
        anomalousDesireStatusText = new OverlayText(null, Util.Color.WHITE, 0, 2, 1f);
        wormSpawnTimerText = new OverlayText(null, Util.Color.GRAY, 0, 2, 1f);
        lobbyTimeText = new OverlayText(null, Util.Color.WHITE, 0, 2, 1f);
        coordsText = new OverlayText(null, Util.Color.WHITE, 0, 2, 1f);
        profileStatsText = new OverlayText(null, Util.Color.WHITE, 0, 2, 1f);
        realTimeClockText = new OverlayText(null, Util.Color.WHITE, 0, 2, 1f);

        mainContainer.add(scathaKillsSinceLastDropText);
        addToggleableElement("scathaKillsSinceLastPetDrop", "Scathas Since Pet Drop", scathaKillsSinceLastDropText, true);

        mainContainer.add(spawnCooldownTimerText);
        addToggleableElement("spawnCooldownTimer", "Spawn Cooldown Status", spawnCooldownTimerText, false);

        mainContainer.add(anomalousDesireStatusText);
        addToggleableElement("anomalousDesireStatusText", "Anomalous Desire Status", anomalousDesireStatusText, false);

        mainContainer.add(wormSpawnTimerText);
        addToggleableElement("timeSinceWormSpawn", "Time Since Last Spawn", wormSpawnTimerText, false);

        mainContainer.add(lobbyTimeText);
        addToggleableElement("time", "Lobby Time", lobbyTimeText, true);

        mainContainer.add(coordsText);
        addToggleableElement("coords", "Coordinates/Orientation", coordsText, true);

        mainContainer.add(profileStatsText);
        addToggleableElement("profileStats", "Scatha Farming Profile Stats (MF, PL)", profileStatsText, false);

        mainContainer.add(realTimeClockText);
        addToggleableElement("realTimeClock", "Real Time Clock", realTimeClockText, false);

        updateContentAlignment();
    // ============================================================
    // RENDERING
    // ============================================================

    public boolean isOverlayDrawAllowed() {
        // Ét samlet, stabilt check
        return scathaPro.isInCrystalHollows()
                && !mc.options.debugEnabled
                && !Util.isPlayerListOpened()
                && mainContainer.isVisible();
    }

    public void drawOverlayIfAllowed(MatrixStack matrices) {
        if (isOverlayDrawAllowed()) {
            drawOverlay(matrices);
        }
    }

    public void drawOverlay(MatrixStack matrices) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Tegn hele overlayet
        mainContainer.draw(matrices);

        // Tegn highlight hvis et element er valgt
        if (highlightedElement != null) {
            drawHighlight(matrices, highlightedElement);
        }

        RenderSystem.disableBlend();
    }

    // ============================================================
    // HIGHLIGHT (separeret for klarhed)
    // ============================================================

    private void drawHighlight(MatrixStack matrices, OverlayElement element) {
        int x = element.getAbsoluteX();
        int y = element.getAbsoluteY();
        int w = element.getScaledWidth();
        int h = element.getScaledHeight();

        int color = 0x80FFFF00; // Lys gul

        DrawContext ctx = new DrawContext(mc, mc.getBufferBuilders().getEntityVertexConsumers());
        ctx.fill(x - 2, y - 2, x + w + 2, y + h + 2, color);
    }
    // ============================================================
    // POSITION & ALIGNMENT
    // ============================================================

    public void updatePosition() {
        OverlayPosition pos =
                scathaPro.getConfig().getEnum(Config.Key.overlayPosition, OverlayPosition.class);

        if (pos == null) pos = OverlayPosition.TOP_LEFT;

        int screenW = mc.getWindow().getScaledWidth();
        int screenH = mc.getWindow().getScaledHeight();

        int w = mainContainer.getScaledWidth();
        int h = mainContainer.getScaledHeight();

        int x = 0;
        int y = 0;

        switch (pos) {
            case TOP_LEFT -> {
                x = 10;
                y = 10;
            }
            case TOP_CENTER -> {
                x = (screenW - w) / 2;
                y = 10;
            }
            case TOP_RIGHT -> {
                x = screenW - w - 10;
                y = 10;
            }

            case MIDDLE_LEFT -> {
                x = 10;
                y = (screenH - h) / 2;
            }
            case MIDDLE_CENTER -> {
                x = (screenW - w) / 2;
                y = (screenH - h) / 2;
            }
            case MIDDLE_RIGHT -> {
                x = screenW - w - 10;
                y = (screenH - h) / 2;
            }

            case BOTTOM_LEFT -> {
                x = 10;
                y = screenH - h - 10;
            }
            case BOTTOM_CENTER -> {
                x = (screenW - w) / 2;
                y = screenH - h - 10;
            }
            case BOTTOM_RIGHT -> {
                x = screenW - w - 10;
                y = screenH - h - 10;
            }
        }

        mainContainer.setPosition(x, y);
    }

    public void updateContentAlignment() {
        contentAlignment =
                scathaPro.getConfig().getEnum(Config.Key.overlayAlignment, Alignment.class);

        if (contentAlignment == null) contentAlignment = Alignment.LEFT;

        // Hvis du senere vil lave auto‑alignment af alle tekstelementer,
        // kan du gøre det her. For nu holder vi det simpelt.
    }

    // ============================================================
    // SCALE & VISIBILITY
    // ============================================================

    public void updateScale() {
        float scale = (float) scathaPro.getConfig().getDouble(Config.Key.overlayScale);
        mainContainer.setScale(scale);
    }

    public void updateVisibility() {
        boolean visible = scathaPro.getConfig().getBoolean(Config.Key.overlayEnabled);
        mainContainer.setVisible(visible);
    }
    // ============================================================
    // UPDATE METHODS (PART 2 – OPTIMIZED)
    // ============================================================

    //
    // PET DROPS
    //
    public void updatePetDrops() {
        rarePetDropsText.setText(Integer.toString(scathaPro.variables.rarePetDrops));
        epicPetDropsText.setText(Integer.toString(scathaPro.variables.epicPetDrops));
        legendaryPetDropsText.setText(Integer.toString(scathaPro.variables.legendaryPetDrops));
    }


    //
    // WORM KILLS
    //
    public void updateWormKills() {
        World world = mc.world;

        int secondary = (world != null) ? statsType.regularWormKills : 0;

        secondaryRegularWormKillsText.setText(TextUtil.numberToString(secondary));
        regularWormKillsText.setText(TextUtil.getObfNrStr(scathaPro.variables.regularWormKills));

        updateTotalKills();
    }


    //
    // SCATHA KILLS (with percentage cycling)
    //
    public void updateScathaKills() {
        World world = mc.world;

        int cycleAmountMs =
                Math.max(scathaPro.getConfig().getInt(Config.Key.scathaPercentageCycleAmountDuration), 1) * 1000;

        int cyclePctMs =
                Math.max(scathaPro.getConfig().getInt(Config.Key.scathaPercentageCyclePercentageDuration), 0) * 1000;

        boolean showAmount =
                scathaPro.getConfig().getBoolean(Config.Key.scathaPercentageAlternativePosition)
                        || cyclePctMs == 0
                        || TimeUtil.getAnimationState(cycleAmountMs, cyclePctMs);

        if (showAmount) {
            scathaKillsText.setText(TextUtil.getObfNrStr(scathaPro.variables.scathaKills));
            secondaryScathaKillsText.setText(
                    TextUtil.numberToString(world != null ? statsType.scathaKills : 0)
            );
        } else {
            updateScathaPercentageDisplay();
        }

        updateTotalKills();
        updateScathaKillsSinceLastDrop();
    }


    //
    // SCATHA PERCENTAGE (helper)
    //
    private void updateScathaPercentageDisplay() {
        int total = scathaPro.variables.regularWormKills + scathaPro.variables.scathaKills;
        int secondaryTotal = statsType.regularWormKills + statsType.scathaKills;

        float pct = (total > 0) ? (scathaPro.variables.scathaKills * 100f / total) : -1f;
        float pctSecondary = (secondaryTotal > 0) ? (statsType.scathaKills * 100f / secondaryTotal) : -1f;

        int digits = scathaPro.getConfig().getInt(Config.Key.scathaPercentageDecimalDigits);

        String pctStr =
                pct < 0 ? Formatting.OBFUSCATED + "?" :
                pct >= 100 ? "100" :
                TextUtil.numberToString(pct, digits, true);

        String pctSecondaryStr =
                pctSecondary < 0 ? "0" :
                pctSecondary >= 100 ? "100" :
                TextUtil.numberToString(pctSecondary, digits, true);

        scathaKillsText.setText(pctStr + "%");
        secondaryScathaKillsText.setText(TextUtil.contrastableGray() + pctSecondaryStr + "%");
    }


    //
    // TOTAL KILLS
    //
    public void updateTotalKills() {
        World world = mc.world;

        int secondaryTotal =
                (world != null) ? statsType.regularWormKills + statsType.scathaKills : 0;

        int total =
                (scathaPro.variables.regularWormKills >= 0 && scathaPro.variables.scathaKills >= 0)
                        ? scathaPro.variables.regularWormKills + scathaPro.variables.scathaKills
                        : -1;

        Formatting gray = TextUtil.contrastableGray();

        String pctText = "";
        String pctSecondaryText = "";

        if (scathaPro.getConfig().getBoolean(Config.Key.scathaPercentageAlternativePosition)) {
            float pct = (total > 0) ? (scathaPro.variables.scathaKills * 100f / total) : -1f;
            float pctSecondary = (secondaryTotal > 0) ? (statsType.scathaKills * 100f / secondaryTotal) : -1f;

            int digits = scathaPro.getConfig().getInt(Config.Key.scathaPercentageDecimalDigits);

            String pctStr =
                    pct < 0 ? "?" :
                    pct >= 100 ? "100" :
                    TextUtil.numberToString(pct, digits, true);

            String pctSecondaryStr =
                    pctSecondary < 0 ? "?" :
                    pctSecondary >= 100 ? "100" :
                    TextUtil.numberToString(pctSecondary, digits, true);

            pctText = gray + " (" + pctStr + "%)";
            pctSecondaryText = gray + " (" + pctSecondaryStr + "%)";
        }

        totalKillsText.setText(
                total >= 0
                        ? Formatting.RESET + TextUtil.numberToString(total) + pctText
                        : Formatting.OBFUSCATED + "?"
        );

        secondaryTotalKillsText.setText(
                Formatting.RESET + TextUtil.numberToString(secondaryTotal) + pctSecondaryText
        );
    }


    //
    // WORM STREAK
    //
    public void updateWormStreak() {
        int streak = statsType.scathaSpawnStreak;
        String name = scathaPro.isScappaModeActive() ? "Scappa" : "Scatha";

        if (streak > 0) {
            wormStreakText.setText(name + " spawn streak: " + TextUtil.numberToString(streak));
        } else if (streak < 0) {
            wormStreakText.setText(
                    "No " + name + " for " + TextUtil.numberToString(-streak)
                            + " " + (-streak == 1 ? "spawn" : "spawns")
            );
        } else {
            wormStreakText.setText("No worms spawned yet");
        }
    }


    //
    // LOBBY TIME
    //
    public void updateLobbyTime() {
        World world = mc.world;

        long worldTime = (world != null) ? world.getTimeOfDay() : -1L;
        int day = (worldTime >= 0) ? (int) (worldTime / 24000L) : 0;
        float dayPct = (worldTime >= 0) ? ((worldTime % 24000L) / 24000f) : 0f;

        long lobbyTime =
                (world != null && scathaPro.isInCrystalHollows())
                        ? TimeUtil.now() - scathaPro.variables.lastWorldJoinTime
                        : 0L;

        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss");
        fmt.setTimeZone(TimeZone.getTimeZone("GMT"));

        Formatting gray = TextUtil.contrastableGray();

        lobbyTimeText.setText(
                Formatting.RESET + "Day " + day + " "
                        + gray + "(" + TextUtil.numberToString(dayPct * 100f, 0, false, RoundingMode.DOWN)
                        + Formatting.RESET + gray + "%) / "
                        + fmt.format(lobbyTime)
        );
    }


    //
    // COORDS + WALL PROGRESS
    //
    public void updateCoords() {
        PlayerEntity player = mc.player;

        String coords;
        String facingAxis;
        double wallProgress;

        if (player != null) {
            BlockPos pos = Util.entityBlockPos(player);
            coords = pos.getX() + " " + pos.getY() + " " + pos.getZ();

            int facing = Util.getDirection(player);

            int min = Constants.crystalHollowsBoundsMin + 1;
            int max = Constants.crystalHollowsBoundsMax;
            double wallLength = max - min;

            double dist = switch (facing) {
                case 0 -> max - player.getZ();
                case 1 -> player.getX() - min;
                case 2 -> player.getZ() - min;
                case 3 -> max - player.getX();
                default -> 0;
            };

            facingAxis = switch (facing) {
                case 0 -> "-Z";
                case 1 -> "+X";
                case 2 -> "+Z";
                case 3 -> "-X";
                default -> "?";
            };

            wallProgress = Math.min(Math.max((dist - 1) / (wallLength - 2), 0), 1);

        } else {
            coords = "0 0 0";
            facingAxis = "+Z";
            wallProgress = 0;
        }

        float pct = (float) Math.floor(wallProgress * 1000) / 10f;
        String pctStr =
                pct >= 100 ? "100" :
                pct <= 0 ? "0" :
                TextUtil.numberToString(pct, 1, true);

        Formatting gray = TextUtil.contrastableGray();

        coordsText.setText(
                Formatting.RESET + coords
                        + gray + " / " + Formatting.WHITE + facingAxis
                        + gray + " (" + pctStr + Formatting.RESET + gray + "% to wall)"
        );
    }


    //
    // SCATHA KILLS SINCE LAST DROP
    //
    public void updateScathaKillsSinceLastDrop() {
        int dry = -1;

        if (!scathaPro.variables.dropDryStreakInvalidated) {
            if (scathaPro.variables.scathaKillsAtLastDrop < 0) {
                dry = scathaPro.variables.scathaKills;
            } else {
                dry = scathaPro.variables.scathaKills - scathaPro.variables.scathaKillsAtLastDrop;
                if (dry < 0) dry = -1;
            }
        }

        String dryStr =
                dry >= 0 ? Integer.toString(dry) : Formatting.OBFUSCATED + "?" + Formatting.RESET;

        scathaKillsSinceLastDropText.setText(
                Formatting.RESET
                        + (scathaPro.isScappaModeActive() ? "Scappas" : "Scathas")
                        + " since last pet drop: "
                        + dryStr
        );
    }


    //
    // SPAWN COOLDOWN
    //
    public void updateSpawnCooldown() {
        long elapsed = TimeUtil.now() - scathaPro.variables.wormSpawnCooldownStartTime;

        if (scathaPro.variables.wormSpawnCooldownStartTime >= 0
                && elapsed < Constants.wormSpawnCooldown) {

            float progress = 1f - (elapsed / (float) Constants.wormSpawnCooldown);

            spawnCooldownProgressBar.setVisible(true);
            spawnCooldownProgressBar.setProgress(progress);

            spawnCooldownTimerText.setText(
                    Formatting.YELLOW + "Spawn cooldown: "
                            + TimeUtil.getHMSTimeString(Constants.wormSpawnCooldown - elapsed, true)
            );

        } else {
            spawnCooldownProgressBar.setVisible(false);
            spawnCooldownTimerText.setText(Formatting.GREEN + "Worms ready to spawn");
        }
    }


    //
    // ANOMALOUS DESIRE
    //
    public void updateAnomalousDesire() {
        final String prefix = Formatting.GOLD + "Anomalous Desire " + Formatting.RESET;

        long now = TimeUtil.now();
        long start = scathaPro.variables.anomalousDesireStartTime;

        boolean showBar = false;
        float progress = 0f;

        if (start >= 0) {
            long elapsed = now - start;

            if (elapsed < Constants.anomalousDesireEffectDuration) {
                progress = 1f - (elapsed / (float) Constants.anomalousDesireEffectDuration);

                boolean cooldownBlocking =
                        scathaPro.variables.wormSpawnCooldownStartTime >= 0
                                && now - scathaPro.variables.wormSpawnCooldownStartTime < Constants.wormSpawnCooldown;

                if (!cooldownBlocking) showBar = true;

                anomalousDesireStatusText.setText(
                        prefix + Formatting.YELLOW + "active: "
                                + TimeUtil.getHMSTimeString(Constants.anomalousDesireEffectDuration - elapsed, true)
                );

            } else if (now < scathaPro.variables.anomalousDesireCooldownEndTime) {
                anomalousDesireStatusText.setText(
                        prefix + Formatting.RED + "cooldown: "
                                + TimeUtil.getHMSTimeString(
                                scathaPro.variables.anomalousDesireCooldownEndTime - now, true)
                );

            } else {
                anomalousDesireStatusText.setText(prefix + Formatting.GREEN + "ready");
            }

        } else {
            anomalousDesireStatusText.setText(prefix + Formatting.GREEN + "ready");
        }

        anomalousDesireEffectProgressBar.setVisible(showBar);
        if (showBar) anomalousDesireEffectProgressBar.setProgress(progress);
    }


    //
    // TIME SINCE LAST WORM SPAWN
    //
    public void updateTimeSinceLastWormSpawn() {
        String text;

        if (scathaPro.variables.lastWormSpawnTime >= 0 && mc.world != null) {
            text = TimeUtil.getHMSTimeString(
                    TimeUtil.now() - scathaPro.variables.lastWormSpawnTime,
                    false
            );
        } else {
            text = Formatting.OBFUSCATED + "?" + Formatting.RESET;
        }

        wormSpawnTimerText.setText(
                Formatting.RESET + "Time since last spawn: " + text
        );
    }


    //
    // PROFILE STATS
    //
    public void updateProfileStats() {
        Formatting gray = TextUtil.contrastableGray();

        profileStatsText.setText(
                scathaPro.variables.getTotalMagicFindString()
                        + " "
                        + scathaPro.variables.getPetLuckString()
                        + gray + " / "
                        + scathaPro.variables.getEffectiveMagicFindString()
                        + " EMF"
        );
    }


    //
    // REAL TIME CLOCK
    //
    public void updateRealTimeClock() {
        Formatting gray = TextUtil.contrastableGray();

        String clock = DateTimeFormatter
                .ofPattern("HH:mm'" + gray + "':ss", Locale.ENGLISH)
                .format(LocalDateTime.now());

        realTimeClockText.setText(
                gray + "Real Time: " + Formatting.RESET + clock
        );
    }
    // ============================================================
    // TOGGLE SYSTEM
    // ============================================================

    /**
     * Registrerer et element som toggleable i settings-menuen.
     * Dette gør det muligt at slå elementet til/fra dynamisk.
     */
    private void addToggleableElement(
            String id,
            String elementName,
            OverlayElement element,
            boolean defaultVisibility
    ) {
        toggleableOverlayElements.add(
                new ToggleableOverlayElement(
                        id,
                        elementName,
                        element,
                        defaultVisibility,
                        elementName // description = name for now
                )
        );
    }


    // ============================================================
    // TOGGLEABLE ELEMENT CLASS
    // ============================================================

    public static class ToggleableOverlayElement {

        public final String id;
        public final String elementName;
        public final OverlayElement element;
        public final boolean defaultVisibility;
        public final String description;

        public ToggleableOverlayElement(
                String id,
                String elementName,
                OverlayElement element,
                boolean defaultVisibility,
                String description
        ) {
            this.id = id;
            this.elementName = elementName;
            this.element = element;
            this.defaultVisibility = defaultVisibility;
            this.description = description;
        }
    }
}