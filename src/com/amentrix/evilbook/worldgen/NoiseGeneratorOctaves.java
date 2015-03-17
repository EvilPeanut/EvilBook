package com.amentrix.evilbook.worldgen;

import java.util.Random;

class NoiseGeneratorOctaves
{
    private NoiseGeneratorPerlin[] generatorCollection;
    private int octaves;

    NoiseGeneratorOctaves(Random par1Random, int par2)
    {
        this.octaves = par2;
        this.generatorCollection = new NoiseGeneratorPerlin[par2];

        for (int var3 = 0; var3 < par2; ++var3)
        {
            this.generatorCollection[var3] = new NoiseGeneratorPerlin(par1Random);
        }
    }

    double[] generateNoiseOctaves(double[] par1ArrayOfDouble, int par2, int par3, int par4, int par5, int par6, int par7, double par8, double par10, double par12)
    {
    	double[] p1ad = par1ArrayOfDouble;
        if (p1ad == null)
        {
        	p1ad = new double[par5 * par6 * par7];
        }
        else
        {
            for (int var14 = 0; var14 < p1ad.length; ++var14)
            {
            	p1ad[var14] = 0.0D;
            }
        }

        double var27 = 1.0D;

        for (int var16 = 0; var16 < this.octaves; ++var16)
        {
            double var17 = par2 * var27 * par8;
            double var19 = par3 * var27 * par10;
            double var21 = par4 * var27 * par12;
            long var23 = floor_double_long(var17);
            long var25 = floor_double_long(var21);
            var17 -= var23;
            var21 -= var25;
            var23 %= 16777216L;
            var25 %= 16777216L;
            var17 += var23;
            var21 += var25;
            this.generatorCollection[var16].populateNoiseArray(p1ad, var17, var19, var21, par5, par6, par7, par8 * var27, par10 * var27, par12 * var27, var27);
            var27 /= 2.0D;
        }

        return p1ad;
    }

    double[] generateNoiseOctaves(double[] par1ArrayOfDouble, int par2, int par3, int par4, int par5, double par6, double par8)
    {
        return this.generateNoiseOctaves(par1ArrayOfDouble, par2, 10, par3, par4, 1, par5, par6, 1.0D, par8);
    }
    
    private static long floor_double_long(double par0)
    {
        long var2 = (long)par0;
        return par0 < var2 ? var2 - 1L : var2;
    }
}
