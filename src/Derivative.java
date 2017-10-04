import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Scanner;

import static java.lang.System.out;

class Derivative {
    /**
     * Calls the slope class to output 1000 values between the two user given
     * values (start-stop) displays x, f(x) , f'(x)
     *
     *
     * @param args
     *            User input arguments
     */
    public static void main(String[] args) throws ScriptException {
        Scanner in = new Scanner(System.in);
        double start, stop;
        out.print("Please enter a output file location ");
            String fileOut = in.nextLine();

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(fileOut);
            pw.close();
        } catch (FileNotFoundException e) {
            System.out.println("Output file not available");
            System.exit(0);
        }
        pw.close();

        out.print("Please enter a function press enter for y=x (for cubed root write (x^(1/3)) ect) : ");
            String function = in.nextLine();
        out.print("Start value for function: ");
            start = in.nextDouble();
        out.print("Stop value for function: ");
            stop = in.nextDouble();
        in.close();

        if(function.equals("")){
            function = "y=x";
        }
        if((stop - start) <= 0){
            System.out.println("Start and Stop values not correct retry");
            System.exit(0);
        }
        //Calls slope to print x , f(x) , f'(x)
        slope(start,stop,function,fileOut);

    }
    /**
     * Calculates the value of f'(x)
     * Then prints x f(x) and f'(x) both to the console and user given file
     *
     * @param start
     *            This is the start of the x value
     * @param stop
     *            This is where the x value stops
     * @param function
     *            The function given by user or default(y=x)
     * @param fileOut
     *            Determines what file to write to
     */
    private static void slope(double start, double stop, String function, String fileOut) throws ScriptException {
        int i = 0;
        double deltaX = (stop-start)/1000;
        DecimalFormat format = new DecimalFormat("0.00");
        while(start <= stop){
            double y = Double.parseDouble((Function(start+deltaX,function))) - Double.parseDouble((Function(start,function)));
            String func = Function(start, function);

                        /*This is the x value*/             /*This is value of y*/                              /*This is derivative*/
            String s = format.format(start) + "      " + format.format(Double.parseDouble(func)) + "        " + format.format((y/deltaX));

            try (PrintWriter fOut = new PrintWriter(new BufferedWriter(new FileWriter(fileOut, true)))) {
                if(i == 0){
                    System.out.println("x" + "          " + "f(x)" + "        " + "f'(x)");
                    fOut.println("x" + "            " + "f(x)" + "      " + "f'(x)");
                    i = 1;
                }else{
                    fOut.println(s);
                    System.out.println(s);
                }
            }catch(Exception ex){
                System.out.println("Error printing out");
                System.exit(0);
            }

            start += deltaX;
        }

    }
    /**
     * This turns the function into a function that can be
     * analyzed by a javascript engine
     * turns ^ into a number due to ^ referring to XOR
     *
     *
     * @param function
     *            function given to the program by the user
     * @param x
     *            the value of one x
     * @return
     *            A string without the ^ char to allow the javascript
     *            engine to evaluate the function properly
     *
     */
    private static String functionStr(String function, double x){
        DecimalFormat df1 = new DecimalFormat("0.########");
        double index = 0, base = 0, power = 0, base1, power1;
        boolean powerFound, parFound = false, neg = false;
        function = function.replace("f(x)", "");
        function = function.replace("y=","");
        function = function.replace("X", Double.toString(x));
        function = function.replace("x", Double.toString(x));
        //detects the base and power
        for(int i = 0; i < function.length(); i++){
            powerFound = false;
            if(function.charAt(i) == '^'){
                powerFound = true;
                index = i;
                for(int j = i-1; j >= 0; j--){
                    if(Character.isDigit(function.charAt(j)) || function.charAt(j) == '.'){
                        base = j;
                    }else if (function.charAt(j) == '-'){
                        base = j;
                        break;
                    }else{
                        break;
                    }
                }
                for(int k = i+1; k < function.length();k++){
                    if(Character.isDigit(function.charAt(k)) || function.charAt(k) == '.'){
                        power = k+1;
                    }else if(function.charAt(k) == '('){
                        parFound = true;
                        for(int l = k+2; k < function.length(); k++){
                            if(Character.isDigit(function.charAt(l)) || function.charAt(l) == '.' || function.charAt(l) == '-' || function.charAt(l) == '+' || function.charAt(l) == '*' || function.charAt(l) == '/' ) {
                                power = l + 2;
                            }else{
                                break;
                            }
                        }
                        break;
                    }else{
                        break;
                    }
                }
            }
            //detects sine
            if(function.charAt(i) == 's' && function.charAt(i+1) == 'i'){
                for(int j = i; j < function.length(); j++ ){
                    if(function.charAt(j) == ')'){
                        //System.out.println(Double.toString(Math.sin(Double.parseDouble(function.substring(i+4,j)))) + "SIN VALUE");
                        BigDecimal bd = new BigDecimal(df1.format(Math.sin(Double.parseDouble(Eval(function.substring(i+4,j))))));
                        String math = bd.toString();
                        //math = Double.parseDouble(df1.format(math));
                        //System.out.println(math + " SIN " + " " +function);
                        function = function.replace(function.substring(i,j+1),math);
                        //System.out.println(math + " FUNCTION ");
                        break;
                    }
                }
            }
            //detects cosine
            if(function.charAt(i) == 'c' && function.charAt(i+1) == 'o'){
                for(int j = i; j < function.length(); j++ ){
                    if(function.charAt(j) == ')'){
                        //System.out.println(j + " " +Double.toString(Math.cos(Double.parseDouble(function.substring(i+4,j)))) + "COS VALUE");
                        BigDecimal bd = new BigDecimal(df1.format(Math.cos(Double.parseDouble(Eval(function.substring(i+4,j))))));
                        String math = bd.toString();
                        function = function.replace(function.substring(i,j+1),(math));
                        break;
                    }
                }
            }
            //detects sqrt
            if(function.charAt(i) == 's' && function.charAt(i+1) == 'q'){
                for(int j = i; j < function.length(); j++ ){
                    if(function.charAt(j) == ')'){
                        //System.out.println(j + " " +Double.toString(Math.cos(Double.parseDouble(function.substring(i+4,j)))) + "COS VALUE");
                        //System.out.println((function.substring(i+5,j)));
                        //System.exit(0);
                        BigDecimal bd = new BigDecimal(df1.format(Math.sqrt(Double.parseDouble(Eval(function.substring(i+4,j))))));
                        String math = bd.toString();
                        function = function.replace(function.substring(i,j+1),(math));
                        break;
                    }
                }
            }
            if(powerFound){
                DecimalFormat df = new DecimalFormat("0.########");
                //System.out.println(function.substring((int)base,(int) index));
                base1 = Double.parseDouble(function.substring((int)base,(int) index));
                if(parFound){
                    power1 = Double.parseDouble(Eval(function.substring((int)index+2,(int)power)));
                    if(base1 < 0){
                        base1 = Math.abs(base1);
                        neg = true;
                    }
                    double math = Math.pow((base1), power1);
                    math = Double.parseDouble(df.format(math));
                    if(neg){
                        math *= -1;
                    }
                    math = Double.parseDouble(df.format(math));

                    function = function.replace(function.substring((int)base,(int)power+1), (Double.toString(math)));
                    try{
                        function = df.format(Double.parseDouble(function));
                    }catch (Exception ex){
                        System.out.println(math);
                        System.out.println(function);
                        System.exit(0);
                    }


                }else{
                    power1 = Double.parseDouble(function.substring((int)index+1,(int)power));
                    double math = Math.pow(base1,power1);
                    function = function.replace(function.substring((int)base,(int)power), (Double.toString(math)));
                }

            }
        }
        return function;
    }
    /**
     * Calculates the value of f(x) with a given function and x value
     *
     *
     * @param x
     *          Value of x
     * @param function
     *          The function given by the user or default (y=x)
     * @return
     *          Returns the respected y value of f(x) in string form
     */
    private static String Function(double x, String function) {
        DecimalFormat df = new DecimalFormat("#.00000");
        x = Double.parseDouble(df.format(x));
        String function1 = functionStr(function, x);
        ScriptEngineManager evalFunction = new ScriptEngineManager();
        ScriptEngine evaluate = evalFunction.getEngineByName("js");
        Object result = null;
        try {
            result = evaluate.eval(function1);
        } catch (ScriptException e) {
            System.out.println("Function not valid please ensure equation is correct " +
                    "\n Ex: 2-(-x) != 2--x ; 2x^3+3/4 != ((2x^3)+3)/4" + " " + function1);
            System.exit(0);
        }
        return result.toString();

    }
    private static String Eval(String function){
        ScriptEngineManager evalFunction = new ScriptEngineManager();
        ScriptEngine evaluate = evalFunction.getEngineByName("js");
        Object result = null;
        try {
            result = evaluate.eval(function);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        assert result != null;
        return result.toString();
    }

}
