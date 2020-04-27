package com.pplosstudio.avroralogicgame.client;

import java.util.Stack;

//import com.google.gwt.core.client.GWT;

import java.util.*;

/**
 * Класс содержит утилиты для разбора и обработки математических выражений.
 *
 * @author Борис Марченко
 * @version $Revision$ $Date$
 */
public class ShuntingYard {

    public ArrayList<ArrayList<String>> list = new ArrayList<>();
    public ArrayList<String> out = new ArrayList<>();

    public ShuntingYard(){
    }

    /**
     * Основные математические операции и их приоритеты.
     *
     * @see #sortingStation(String, java.util.Map)
     */
    public static final Map<String, Integer> MAIN_MATH_OPERATIONS;

    static {
        MAIN_MATH_OPERATIONS = new HashMap<String, Integer>();

        //MAIN_MATH_OPERATIONS.put("~", 1);
        MAIN_MATH_OPERATIONS.put("*", 2);
        MAIN_MATH_OPERATIONS.put("+", 3);
        MAIN_MATH_OPERATIONS.put("@", 3);
    }

    /**
     * Преобразует выражение из инфиксной нотации в обратную польскую нотацию (ОПН) по алгоритму <i>Сортировочная
     * станция</i> Эдскера Дейкстры. Отличительной особенностью обратной польской нотации является то, что все
     * аргументы (или операнды) расположены перед операцией. Это позволяет избавиться от необходимости использования
     * скобок. Например, выражение, записаное в инфиксной нотации как 3 * (4 + 7), будет выглядеть как 3 4 7 + *
     * в ОПН. Символы скобок могут быть изменены.
     * <a href="http://ru.wikipedia.org/wiki/Обратная_польская_запись">Подробнее об ОПЗ</a>.
     *
     * @param expression выражение в инфиксной форме.
     * @param operations операторы, использующиеся в выражении (ассоциированные, либо лево-ассоциированные).
     * Значениями карты служат приоритеты операции (самый высокий приоритет - 1). Например, для 5
     * основных математических операторов карта будет выглядеть так:
     * <pre>
     *      *   ->   1
     *      /   ->   1
     *      +   ->   2
     *      -   ->   2
     * </pre>
     * Приведенные операторы определены в константе {@link #MAIN_MATH_OPERATIONS}.
     * @param leftBracket открывающая скобка.
     * @param rightBracket закрывающая скобка.
     * @return преобразованное выражение в ОПН.
     */
    public static String sortingStation(String expression, Map<String, Integer> operations, String leftBracket,
                                        String rightBracket) {

        if (expression == null || expression.length() == 0)
            throw new IllegalStateException("Expression isn't specified.");
        if (operations == null || operations.isEmpty())
            throw new IllegalStateException("Operations aren't specified.");

        // Выходная строка, разбитая на "символы" - операции и операнды..
        List<String> out = new ArrayList<String>();
        // Стек операций.
        Stack<String> stack = new Stack<String>();

        // Удаление пробелов из выражения.
        expression = expression.replace(" ", "");

        // Множество "символов", не являющихся операндами (операции и скобки).
        Set<String> operationSymbols = new HashSet<String>(operations.keySet());
        operationSymbols.add(leftBracket);
        operationSymbols.add(rightBracket);

        // Индекс, на котором закончился разбор строки на прошлой итерации.
        int index = 0;
        // Признак необходимости поиска следующего элемента.
        boolean findNext = true;

        while (findNext) {
            int nextOperationIndex = expression.length();
            String nextOperation = "";
            // Поиск следующего оператора или скобки.
            for (String operation : operationSymbols) {
                int i = expression.indexOf(operation, index);
                if (i >= 0 && i < nextOperationIndex) {
                    nextOperation = operation;
                    nextOperationIndex = i;
                }
            }
            // Оператор не найден.
            if (nextOperationIndex == expression.length()) {
                findNext = false;
            } else {
                // Если оператору или скобке предшествует операнд, добавляем его в выходную строку.
                if (index != nextOperationIndex) {
                    out.add(expression.substring(index, nextOperationIndex));
                }
                // Обработка операторов и скобок.
                // Открывающая скобка.
                if (nextOperation.equals(leftBracket)) {
                    stack.push(nextOperation);
                }
                // Закрывающая скобка.
                else if (nextOperation.equals(rightBracket)) {
                    while (!stack.peek().equals(leftBracket)) {
                        out.add(stack.pop());
                        if (stack.empty()) {
                            throw new IllegalArgumentException("Unmatched brackets");
                        }
                    }
                    stack.pop();
                }
                // Операция.
                else {
                    while (!stack.empty() && !stack.peek().equals(leftBracket) &&
                            (operations.get(nextOperation) >= operations.get(stack.peek()))) {
                        out.add(stack.pop());
                    }
                    stack.push(nextOperation);
                }
                index = nextOperationIndex + nextOperation.length();
            }
        }

        // Добавление в выходную строку операндов после последнего операнда.
        if (index != expression.length()) {
            out.add(expression.substring(index));
        }

        // Пробразование выходного списка к выходной строке.
        while (!stack.empty()) {
            out.add(stack.pop());
        }
        StringBuffer result = new StringBuffer();
        if (!out.isEmpty())
            result.append(out.remove(0));
        while (!out.isEmpty())
            result.append(" ").append(out.remove(0));

        return result.toString();
    }

    /**
     * Преобразует выражение из инфиксной нотации в обратную польскую нотацию (ОПН) по алгоритму <i>Сортировочная
     * станция</i> Эдскера Дейкстры. Отличительной особенностью обратной польской нотации является то, что все
     * аргументы (или операнды) расположены перед операцией. Это позволяет избавиться от необходимости использования
     * скобок. Например, выражение, записаное в инфиксной нотации как 3 * (4 + 7), будет выглядеть как 3 4 7 + *
     * в ОПН.
     * <a href="http://ru.wikipedia.org/wiki/Обратная_польская_запись">Подробнее об ОПЗ</a>.
     *
     * @param expression выражение в инфиксной форме.
     * @param operations операторы, использующиеся в выражении (ассоциированные, либо лево-ассоциированные).
     * Значениями карты служат приоритеты операции (самый высокий приоритет - 1). Например, для 5
     * основных математических операторов карта будет выглядеть так:
     * <pre>
     *      *   ->   1
     *      /   ->   1
     *      +   ->   2
     *      -   ->   2
     * </pre>
     * Приведенные операторы определены в константе {@link #MAIN_MATH_OPERATIONS}.
     * @return преобразованное выражение в ОПН.
     */
    public static String sortingStation(String expression, Map<String, Integer> operations) {
        return sortingStation(expression, operations, "(", ")");
    }

    public void calculateMDNF(String input){

        ArrayList<String> data = new ArrayList<>();
        ArrayList<String> lastTerm = new ArrayList<>();

        input = input.replace(" ", "");
        input = input.replace("(", "");
        input = input.replace(")", "");
        String[] terms = input.split("\\+");

        for(int i = 0; i <terms.length; i++){

            String[] operands = terms[i].split("\\*");

            for(int j = 0; j<operands.length; j++){
                data.add(operands[j]);
            }
            
            data.add("*");
            data.add(terms[i]);
            list.add(data);
            lastTerm.add(terms[i]);
            data = new ArrayList<>();
        }

        lastTerm.add("+");
        lastTerm.add(input);
        list.add(lastTerm);
        
    }
    
    public void calculateMKNF(String input){

        ArrayList<String> data = new ArrayList<>();
        ArrayList<String> lastTerm = new ArrayList<>();

        input = input.replace(" ", "");
        input = input.replace("(", "");
        input = input.replace(")", "");
        String[] terms = input.split("\\*");

        for(int i = 0; i <terms.length; i++){

            String[] operands = terms[i].split("\\+");

            for(int j = 0; j<operands.length; j++){

                data.add(operands[j]);

            }
            data.add("+");
            data.add(terms[i]);
            list.add(data);
            lastTerm.add(terms[i]);
            data = new ArrayList<>();
        }

        lastTerm.add("*");
        lastTerm.add(input);
        list.add(lastTerm);
        
    }   

    
    
    
    /**
     * Вычисляет значение выражения, записанного в инфиксной нотации. Выражение может содержать скобки, числа с
     * плавающей точкой, четыре основных математических операндов.
     *
     * @param expression выражение.
     * @return результат вычисления.
     */
    public void calculateExpression(String expression) {

        String rpn = sortingStation(expression, MAIN_MATH_OPERATIONS);
        StringTokenizer tokenizer = new StringTokenizer(rpn, " ");
        Stack<String> stack = new Stack<String>();

        ArrayList<String> operands = new ArrayList<>();
        int newTerm = 0;
        String prevOperation = "";
        String term = "";
        
        while (tokenizer.hasMoreTokens()) {

            String token = tokenizer.nextToken();
            // Операнд.
            if (!MAIN_MATH_OPERATIONS.keySet().contains(token)) {

                stack.push(new String(token));
                newTerm++;

            } else {

                if(prevOperation.equals("")){
                    prevOperation = token;
                }
                String operand2 = stack.pop();
                String operand1 = stack.empty() ? "" : stack.pop();

                if(token.equals(prevOperation)){


                    if(operands.size()>0 && !list.contains(operands))list.add(operands);
                    if(newTerm>=2){

                    	if(term.charAt(term.length()-1) == '*' || term.charAt(term.length()-1) == '+')term = removeByIndex(term, term.length()-1);
                        if(!term.equals("")) {
                            operands.add(token);
                            operands.add(term);
                          
                        }

                        operands=new ArrayList<>();
                        list.add(operands);
                        operands.add(operand1);

                        term = "";
                        term += operand1 + token;

                    }
                    operands.add(operand2);
                    if(term.length()>2)if(term.charAt(term.length()-1) == '*' || term.charAt(term.length()-1) == '+'){term += operand2+token;}
                    else {term += token+operand2;}

                    String NewOperand = operand1.concat(token+operand2);
                    stack.push(NewOperand);

                    newTerm=0;
                    prevOperation = token;

                }
                else{

                    System.out.println("Not Like prev");
                    //System.out.println(term);

                    if(term.length()>2){if(term.charAt(term.length()-1) == '*' || term.charAt(term.length()-1) == '+')term = removeByIndex(term, term.length()-1);}
                    if(!operands.contains(prevOperation) && operands.size()>=2)operands.add(prevOperation);
                    if(!operands.contains(term) && operands.size()>=2)operands.add(term);

                    term = "";
                    System.out.println(operands);

                    //if(operands.size()>0)list.add(operands);
                    operands = new ArrayList<>();
                    list.add(operands);

                    System.out.println("new slot");
                    //fromSlot = true;
                    String NewOperand = "";

                    if(stack.size()<=2){

                        if(!operand1.equals(""))operands.add(operand1);
                        operands.add(operand2);

                       // System.out.println("!!!");
                        NewOperand=operand1.concat(token + operand2);

                        term = NewOperand;
                        //operands.add(token);
                        //operands.add(term);
                        //term = "";

                        stack.push(NewOperand);
                        newTerm=0;
                        prevOperation = token;
                        System.out.println(operands);
                    }
                    else {

                        if(!operand1.equals(""))operands.add(operand1);
                        operands.add(operand2);

                        if (!operand1.equals("")) {
                            NewOperand = operand1.concat(token + operand2);
                        } else {
                            NewOperand = token + operand2;
                        }
                        term = NewOperand;
                        stack.push(NewOperand);
                        newTerm=0;
                        prevOperation = token;
                        System.out.println(term);
                    }
                    

                }


            }
        }

        operands.add(prevOperation);
        operands.add(term);

        /*
        if(list.size() == 1 && list.get(0).get(list.get(0).size()-2) != "*"){
            list.get(0).add("*");
            term = removeByIndex(term, term.length()-1);
            list.get(0).add(term);
        }
*/
    }
    
    
    private String removeByIndex(String str, int index) {
        return str.substring(0,index)+str.substring(index+1);
    }
    
}