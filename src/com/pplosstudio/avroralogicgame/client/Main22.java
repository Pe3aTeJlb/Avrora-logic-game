package com.pplosstudio.avroralogicgame.client;

public class Main22 {

    static boolean MDNF = true;

    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();

        LogicFunctionGenerator gen = new LogicFunctionGenerator(3,1);
        BasisConverter converter = new BasisConverter();
        Factorisator_V_2 f = new Factorisator_V_2();

        Solver sol = new Solver(
                                               gen.VectorFunctions,
                                    3,
                                                    gen.VarNames,
                                   1,
                                                    gen.OutNames,
                                                            MDNF,
                            false,
                true
        );
        sol.run();

        System.out.println(sol.getSolution());

        /*Базис конвертер может самостоятельно создавать схему построения схемы
        Общаяя идея схемы построения - элементы, операция, название нового элемента
        */

        //converter.ToZhegalkinPolynomial(gen.VectorFunctions,0,gen.VarNames);
        //converter.ToNand(sol.getSolution(), MDNF);
        converter.ToNor(sol.getSolution(), MDNF);


        //f.PrepareData(sol.getSolution());
        //f.PrepareData("x0 * x1 * ~x2 + x0 * ~x1 * x2 + ~x0 * x1 * x2");
        //ShuntingYard y = new ShuntingYard(f.output, MDNF);
        //ShuntingYard y = new ShuntingYard(sol.getSolution(),MDNF);
        //ShuntingYard y = new ShuntingYard("(a+b+c+e+z)*(d+f+g)*(n+k+l)");

        System.out.println(f.output);
        System.out.println(sol.getSolution());
        System.out.println(System.currentTimeMillis() - startTime);
    }

}