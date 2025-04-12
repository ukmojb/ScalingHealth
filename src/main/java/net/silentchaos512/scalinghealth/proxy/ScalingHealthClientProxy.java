/*
 * Scaling Health
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.scalinghealth.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.silentchaos512.lib.registry.SRegistry;
import net.silentchaos512.lib.util.Color;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.client.DifficultyDisplayHandler;
import net.silentchaos512.scalinghealth.client.HeartDisplayHandler;
import net.silentchaos512.scalinghealth.client.key.KeyTrackerSH;
import net.silentchaos512.scalinghealth.client.render.particle.ParticleSH;
import net.silentchaos512.scalinghealth.config.Config;
import net.silentchaos512.scalinghealth.event.WitEventHandler;
import net.silentchaos512.scalinghealth.init.ModEntities;
import net.silentchaos512.scalinghealth.lib.EnumModParticles;
import org.apache.commons.lang3.NotImplementedException;

public class ScalingHealthClientProxy extends ScalingHealthCommonProxy {
    @Override
    public void preInit(SRegistry registry, FMLPreInitializationEvent event) {
        super.preInit(registry, event);

        MinecraftForge.EVENT_BUS.register(HeartDisplayHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(DifficultyDisplayHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(KeyTrackerSH.INSTANCE);

        if (Loader.isModLoaded("wit") && Config.Client.enableWitSupport) {
            ScalingHealth.LOGGER.info("Registering WIT event handler (can be disabled in the config)");
            MinecraftForge.EVENT_BUS.register(new WitEventHandler());
        }

        registry.clientPreInit(event);

        ModEntities.registerRenderers(registry);
    }

    @Override
    public void init(SRegistry registry, FMLInitializationEvent event) {
        super.init(registry, event);
        registry.clientInit(event);
    }

    @Override
    public void postInit(SRegistry registry, FMLPostInitializationEvent event) {
        super.postInit(registry, event);
        registry.clientPostInit(event);
    }

    @Override
    public void spawnParticles(EnumModParticles type, Color color, World world, double x, double y,
                               double z, double motionX, double motionY, double motionZ) {
        Particle fx = null;

        float r = color.getRed();
        float g = color.getGreen();
        float b = color.getBlue();

        switch (type) {
            case CURSED_HEART:
            case ENCHANTED_HEART:
            case HEART_CONTAINER:
                fx = new ParticleSH(world, x, y, z, motionX, motionY, motionZ, 1.0f, 10, r, g, b);
                break;
            default:
                throw new NotImplementedException("Unknown particle type: " + type);
        }

        Minecraft.getMinecraft().effectRenderer.addEffect(fx);
    }

    @Override
    public EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().player;
    }

    @Override
    public int getParticleSettings() {
        return Minecraft.getMinecraft().gameSettings.particleSetting;
    }
}
