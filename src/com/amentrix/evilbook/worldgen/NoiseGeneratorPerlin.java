package com.amentrix.evilbook.worldgen;

import java.util.Random;

class NoiseGeneratorPerlin
{
    private int[] permutations;
    private double xCoord, yCoord, zCoord;

    public NoiseGeneratorPerlin()
    {
        this(new Random());
    }

    NoiseGeneratorPerlin(Random par1Random)
    {
        this.permutations = new int[512];
        this.xCoord = par1Random.nextDouble() * 256.0D;
        this.yCoord = par1Random.nextDouble() * 256.0D;
        this.zCoord = par1Random.nextDouble() * 256.0D;
        int var2;

        for (var2 = 0; var2 < 256; var2++)
        {
        	this.permutations[var2] = var2;
        }

        for (var2 = 0; var2 < 256; ++var2)
        {
            int var3 = par1Random.nextInt(256 - var2) + var2;
            int var4 = this.permutations[var2];
            this.permutations[var2] = this.permutations[var3];
            this.permutations[var3] = var4;
            this.permutations[var2 + 256] = this.permutations[var2];
        }
    }

    private final static double lerp(double par1, double par3, double par5)
    {
        return par3 + par1 * (par5 - par3);
    }

    private final static double func_76309_a(int par1, double par2, double par4)
    {
        int var6 = par1 & 15;
        double var7 = (1 - ((var6 & 8) >> 3)) * par2;
        double var9 = var6 < 4 ? 0.0D : (var6 != 12 && var6 != 14 ? par4 : par2);
        return ((var6 & 1) == 0 ? var7 : -var7) + ((var6 & 2) == 0 ? var9 : -var9);
    }

    private final static double grad(int par1, double par2, double par4, double par6)
    {
        int var8 = par1 & 15;
        double var9 = var8 < 8 ? par2 : par4;
        double var11 = var8 < 4 ? par4 : (var8 != 12 && var8 != 14 ? par6 : par2);
        return ((var8 & 1) == 0 ? var9 : -var9) + ((var8 & 2) == 0 ? var11 : -var11);
    }

    void populateNoiseArray(double[] par1ArrayOfDouble, double par2, double par4, double par6, int par8, int par9, int par10, double par11, double par13, double par15, double par17)
    {
        int var19;
        int var22;
        double var31;
        double var35;
        double var38;
        int var37;
        double var42;
        int var40;
        int var41;
        int var10001;
        int var77;

        if (par9 == 1)
        {
            double var72 = 0.0D;
            double var71 = 0.0D;
            var77 = 0;
            double var74 = 1.0D / par17;

            for (int var30 = 0; var30 < par8; ++var30)
            {
                var31 = par2 + var30 * par11 + this.xCoord;
                int var78 = (int)var31;

                if (var31 < var78)
                {
                    --var78;
                }

                int var34 = var78 & 255;
                var31 -= var78;
                var35 = var31 * var31 * var31 * (var31 * (var31 * 6.0D - 15.0D) + 10.0D);

                for (var37 = 0; var37 < par10; ++var37)
                {
                    var38 = par6 + var37 * par15 + this.zCoord;
                    var40 = (int)var38;

                    if (var38 < var40)
                    {
                        --var40;
                    }

                    var41 = var40 & 255;
                    var38 -= var40;
                    var42 = var38 * var38 * var38 * (var38 * (var38 * 6.0D - 15.0D) + 10.0D);
                    var19 = this.permutations[var34] + 0;
                    int var64 = this.permutations[var19] + var41;
                    int var69 = this.permutations[var34 + 1] + 0;
                    var22 = this.permutations[var69] + var41;
                    var72 = NoiseGeneratorPerlin.lerp(var35, NoiseGeneratorPerlin.func_76309_a(this.permutations[var64], var31, var38), NoiseGeneratorPerlin.grad(this.permutations[var22], var31 - 1.0D, 0.0D, var38));
                    var71 = NoiseGeneratorPerlin.lerp(var35, NoiseGeneratorPerlin.grad(this.permutations[var64 + 1], var31, 0.0D, var38 - 1.0D), NoiseGeneratorPerlin.grad(this.permutations[var22 + 1], var31 - 1.0D, 0.0D, var38 - 1.0D));
                    double var79 = NoiseGeneratorPerlin.lerp(var42, var72, var71);
                    var10001 = var77++;
                    par1ArrayOfDouble[var10001] += var79 * var74;
                }
            }
        }
        else
        {
            var19 = 0;
            double var20 = 1.0D / par17;
            var22 = -1;
            double var29 = 0.0D;
            var31 = 0.0D;
            double var33 = 0.0D;
            var35 = 0.0D;

            for (var37 = 0; var37 < par8; ++var37)
            {
                var38 = par2 + var37 * par11 + this.xCoord;
                var40 = (int)var38;

                if (var38 < var40)
                {
                    --var40;
                }

                var41 = var40 & 255;
                var38 -= var40;
                var42 = var38 * var38 * var38 * (var38 * (var38 * 6.0D - 15.0D) + 10.0D);

                for (int var44 = 0; var44 < par10; ++var44)
                {
                    double var45 = par6 + var44 * par15 + this.zCoord;
                    int var47 = (int)var45;

                    if (var45 < var47)
                    {
                        --var47;
                    }

                    int var48 = var47 & 255;
                    var45 -= var47;
                    double var49 = var45 * var45 * var45 * (var45 * (var45 * 6.0D - 15.0D) + 10.0D);

                    for (int var51 = 0; var51 < par9; ++var51)
                    {
                        double var52 = par4 + var51 * par13 + this.yCoord;
                        int var54 = (int)var52;

                        if (var52 < var54)
                        {
                            --var54;
                        }

                        int var55 = var54 & 255;
                        var52 -= var54;
                        double var56 = var52 * var52 * var52 * (var52 * (var52 * 6.0D - 15.0D) + 10.0D);

                        if (var51 == 0 || var55 != var22)
                        {
                            var22 = var55;
                            int var68 = this.permutations[var41] + var55;
                            int var73 = this.permutations[var68] + var48;
                            int var70 = this.permutations[var68 + 1] + var48;
                            int var76 = this.permutations[var41 + 1] + var55;
                            var77 = this.permutations[var76] + var48;
                            int var75 = this.permutations[var76 + 1] + var48;
                            var29 = NoiseGeneratorPerlin.lerp(var42, NoiseGeneratorPerlin.grad(this.permutations[var73], var38, var52, var45), NoiseGeneratorPerlin.grad(this.permutations[var77], var38 - 1.0D, var52, var45));
                            var31 = NoiseGeneratorPerlin.lerp(var42, NoiseGeneratorPerlin.grad(this.permutations[var70], var38, var52 - 1.0D, var45), NoiseGeneratorPerlin.grad(this.permutations[var75], var38 - 1.0D, var52 - 1.0D, var45));
                            var33 = NoiseGeneratorPerlin.lerp(var42, NoiseGeneratorPerlin.grad(this.permutations[var73 + 1], var38, var52, var45 - 1.0D), NoiseGeneratorPerlin.grad(this.permutations[var77 + 1], var38 - 1.0D, var52, var45 - 1.0D));
                            var35 = NoiseGeneratorPerlin.lerp(var42, NoiseGeneratorPerlin.grad(this.permutations[var70 + 1], var38, var52 - 1.0D, var45 - 1.0D), NoiseGeneratorPerlin.grad(this.permutations[var75 + 1], var38 - 1.0D, var52 - 1.0D, var45 - 1.0D));
                        }

                        double var58 = NoiseGeneratorPerlin.lerp(var56, var29, var31);
                        double var60 = NoiseGeneratorPerlin.lerp(var56, var33, var35);
                        double var62 = NoiseGeneratorPerlin.lerp(var49, var58, var60);
                        var10001 = var19++;
                        par1ArrayOfDouble[var10001] += var62 * var20;
                    }
                }
            }
        }
    }
}
