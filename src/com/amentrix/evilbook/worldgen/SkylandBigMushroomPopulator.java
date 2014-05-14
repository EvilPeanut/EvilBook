package com.amentrix.evilbook.worldgen;

import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;

public class SkylandBigMushroomPopulator extends BlockPopulator
{
	@Override
	public void populate(World world, Random positionRandomizer, Chunk chunk) {
    {
        int par3 = chunk.getX() * 16;
        int par5 = chunk.getZ() * 16;
        int par4 = world.getHighestBlockYAt(par3, par5);
    	if (chunk.getBlock(par3, par4, par5).getBiome() != Biome.MUSHROOM_ISLAND && chunk.getBlock(par3, par4, par5).getBiome() != Biome.MUSHROOM_SHORE) return;
    	
    	Random par2Random = new Random();
    	int var6 = par2Random.nextInt(2);
        int var7 = par2Random.nextInt(3) + 4;
        boolean var8 = true;
        
        if (par4 >= 1 && par4 + var7 + 1 < 256)
        {
            int var9;
            int var11;
            int var12;
            int var13;

            for (var9 = par4; var9 <= par4 + 1 + var7; ++var9)
            {
                byte var10 = 3;

                if (var9 <= par4 + 3)
                {
                    var10 = 0;
                }

                for (var11 = par3 - var10; var11 <= par3 + var10 && var8; ++var11)
                {
                    for (var12 = par5 - var10; var12 <= par5 + var10 && var8; ++var12)
                    {
                        if (var9 >= 0 && var9 < 256)
                        {
                            var13 = world.getBlockTypeIdAt(var11, var9, var12);
                            if (var13 != 0 && var13 != 18)
                            {
                                var8 = false;
                            }
                        }
                        else
                        {
                            var8 = false;
                        }
                    }
                }
            }

            if (!var8)
            {
                return;
            }
			var9 = world.getBlockTypeIdAt(par3, par4 - 1, par5);

			if (var9 != 3 && var9 != 2 && var9 != 110)
			{
			    return;
			}
			int var16 = par4 + var7;

			if (var6 == 1)
			{
			    var16 = par4 + var7 - 3;
			}

			for (var11 = var16; var11 <= par4 + var7; ++var11)
			{
			    var12 = 1;

			    if (var11 < par4 + var7)
			    {
			        ++var12;
			    }

			    if (var6 == 0)
			    {
			        var12 = 3;
			    }

			    for (var13 = par3 - var12; var13 <= par3 + var12; ++var13)
			    {
			        for (int var14 = par5 - var12; var14 <= par5 + var12; ++var14)
			        {
			            int var15 = 5;

			            if (var13 == par3 - var12)
			            {
			                --var15;
			            }

			            if (var13 == par3 + var12)
			            {
			                ++var15;
			            }

			            if (var14 == par5 - var12)
			            {
			                var15 -= 3;
			            }

			            if (var14 == par5 + var12)
			            {
			                var15 += 3;
			            }

			            if (var6 == 0 || var11 < par4 + var7)
			            {
			                if ((var13 == par3 - var12 || var13 == par3 + var12) && (var14 == par5 - var12 || var14 == par5 + var12))
			                {
			                    continue;
			                }

			                if (var13 == par3 - (var12 - 1) && var14 == par5 - var12)
			                {
			                    var15 = 1;
			                }

			                if (var13 == par3 - var12 && var14 == par5 - (var12 - 1))
			                {
			                    var15 = 1;
			                }

			                if (var13 == par3 + (var12 - 1) && var14 == par5 - var12)
			                {
			                    var15 = 3;
			                }

			                if (var13 == par3 + var12 && var14 == par5 - (var12 - 1))
			                {
			                    var15 = 3;
			                }

			                if (var13 == par3 - (var12 - 1) && var14 == par5 + var12)
			                {
			                    var15 = 7;
			                }

			                if (var13 == par3 - var12 && var14 == par5 + (var12 - 1))
			                {
			                    var15 = 7;
			                }

			                if (var13 == par3 + (var12 - 1) && var14 == par5 + var12)
			                {
			                    var15 = 9;
			                }

			                if (var13 == par3 + var12 && var14 == par5 + (var12 - 1))
			                {
			                    var15 = 9;
			                }
			            }

			            if (var15 == 5 && var11 < par4 + var7)
			            {
			                var15 = 0;
			            }

			            if ((var15 != 0 || par4 >= par4 + var7 - 1))
			            {
			                world.getBlockAt(var13, var11, var14).setTypeIdAndData(99 + var6, (byte) var15, false);
			            }
			        }
			    }
			}
			for (var11 = 0; var11 < var7; ++var11)
			{
			    world.getBlockAt(par3, par4 + var11, par5).setTypeIdAndData(99 + var6, (byte) 10, false);
			}
        }
    }
	}
}
