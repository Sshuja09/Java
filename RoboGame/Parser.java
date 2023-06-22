import java.util.*;
import java.util.regex.*;

/**
 * See assignment handout for the grammar.
 * You need to implement the parse(..) method and all the rest of the parser.
 * There are several methods provided for you:
 * - several utility methods to help with the parsing
 * See also the TestParser class for testing your code.
 */

public class Parser {

    static HashMap<String, Integer> variables = new HashMap<String, Integer>(); //Holds variables


    // Useful Patterns
    static final Pattern NUMPAT = Pattern.compile("-?[1-9][0-9]*|0"); 
    static final Pattern OPENPAREN = Pattern.compile("\\(");
    static final Pattern CLOSEPAREN = Pattern.compile("\\)");
    static final Pattern OPENBRACE = Pattern.compile("\\{");
    static final Pattern CLOSEBRACE = Pattern.compile("\\}");
    // STATEMENT PATTERNS
    static final Pattern STMT = Pattern.compile("loop|if|while|\\$[A-Za-z][A-Za-z0-9]*");
    static final Pattern ACT = Pattern.compile("move|turnL|turnR|turnAround|shieldOn|shieldOff|takeFuel|wait");
    static final Pattern VARIABLE = Pattern.compile("\\$[A-Za-z][A-Za-z0-9]*");
    static final Pattern SEMICOLON = Pattern.compile(";");
    static final Pattern LOOP = Pattern.compile("loop");
    static final Pattern IF_ELIF = Pattern.compile("if|elif");
    static final Pattern IF = Pattern.compile("if");
    static final Pattern ELIF = Pattern.compile("elif");
    static final Pattern ELSE = Pattern.compile("else");
    static final Pattern WHILE = Pattern.compile("while");
    // ACT PATTERNS
    static final Pattern MOVE = Pattern.compile("move");
    static final Pattern LEFT = Pattern.compile("turnL");
    static final Pattern RIGHT = Pattern.compile("turnR");
    static final Pattern TURNAROUND = Pattern.compile("turnAround");
    static final Pattern SHIELDON = Pattern.compile("shieldOn");
    static final Pattern SHIELDOFF = Pattern.compile("shieldOff");
    static final Pattern TAKEFUEL = Pattern.compile("takeFuel");
    static final Pattern WAIT = Pattern.compile("wait");
    // RELOP PATTERNS
    static final Pattern RELOP = Pattern.compile("lt|gt|eq");
    static final Pattern LT = Pattern.compile("lt");
    static final Pattern GT = Pattern.compile("gt");
    static final Pattern EQ = Pattern.compile("eq");
    // OPERATIONS PATTERNS
    static final Pattern ADD = Pattern.compile("add");
    static final Pattern SUB = Pattern.compile("sub");
    static final Pattern MUL = Pattern.compile("mul");
    static final Pattern DIV = Pattern.compile("div");

    static final Pattern SENSOR= Pattern.compile("fuelLeft|oppLR|oppFB|numBarrels|barrelLR|barrelFB|wallDist");
    static final Pattern OPERATION = Pattern.compile("add|sub|mul|div");
    static final Pattern COMMA= Pattern.compile(",");
    static final Pattern CONDITIONAL = Pattern.compile("and|or|not");
    //----------------------------------------------------------------
    /**
     * The top of the parser, which is handed a scanner containing
     * the text of the program to parse.
     * Returns the parse tree.
     */
    ProgramNode parse(Scanner s) {
        // Set the delimiter for the scanner.
        s.useDelimiter("\\s+|(?=[{}(),;])|(?<=[{}(),;])");
        // THE PARSER GOES HERE
        // Call the parseProg method for the first grammar rule (PROG) and return the node

        // Checks if it is an empty program or not
        if(!s.hasNext()){fail("Empty program!", s);}

        ProgNode prog = new ProgNode(new ArrayList<ProgramNode>());

        while(s.hasNext()){
            prog.addStatement(parseStatement(s));
            }

        return prog;
    }

    /**
     * Parses through the different statements depending on their type
     */
    ProgramNode parseStatement(Scanner s){
        if(!s.hasNext()){ fail("Empty statement.", s); } //Fail if there is nothing to parse
        StatementNode statement = null;

        if(s.hasNext(ACT)){ statement = new StatementNode(parseAct(s)); require(";", "Missing ';'.", s);}
        else if(s.hasNext(LOOP)){ statement = new StatementNode(parseLoop(s)); }
        else if(s.hasNext(IF)){ statement = new StatementNode(parseIf(s)); }
        else if(s.hasNext(WHILE)){ statement = new StatementNode(parseWhile(s)); }
        else if(s.hasNext(VARIABLE)){ statement = new StatementNode(parseAssign(s)); require(SEMICOLON, "Missing ';'.", s);}
        else { fail("Not a valid statement", s); } // Fail if it is not a valid statement
        return statement;
    }

    /**
     * Parses through different actions
     */
    ProgramNode parseAct(Scanner s){
        if(!s.hasNext()){ fail("Empty Action.", s);} // Fail if there is nothing to parse
        if(s.hasNext(MOVE)){ return parseMove(s); }
        if(s.hasNext(LEFT)){ return parseLeft(s); }
        if(s.hasNext(RIGHT)){ return parseRight(s); }
        if(s.hasNext(TURNAROUND)){ return parseTurnAround(s); }
        if(s.hasNext(SHIELDON)){ return parseShieldOn(s); }
        if(s.hasNext(SHIELDOFF)){ return parseShieldOff(s); }
        if(s.hasNext(TAKEFUEL)){ return parseFuel(s); }
        if(s.hasNext(WAIT)){ return parseWait(s); }
        fail("Not a valid Action.", s); // Fail if it is not a valid action
        return null;
    }

    /**
     * Parses the move action and returns an empty/with expression move node
     */
    ProgramNode parseMove(Scanner s){
        require(MOVE, "Missing 'move'.", s);
        if(s.hasNext(OPENPAREN)){ // If move action contains expression
            require(OPENPAREN, "Missing open parenthesis.", s);
            ExpressionNode num = parseExpr(s);
            require(CLOSEPAREN, "Missing closing parenthesis.", s);
            return new MoveNode(num);
        }
        return new MoveNode();
    }

    /**
     * Parses the turn left action and returns a turn left node (LeftNode)
     */
    ProgramNode parseLeft(Scanner s){
        require(LEFT, "Missing 'turnL'.", s);
        return new LeftNode();
    }

    /**
     * Parses the turn right action and returns a turn right node (RightNode)
     */
    ProgramNode parseRight(Scanner s){
        require(RIGHT, "Missing 'turnR'.", s);
        return new RightNode();
    }

    /**
     * Parses the turn around action and returns a turn around node (TurnAroundNode)
     */
    ProgramNode parseTurnAround(Scanner s){
        require(TURNAROUND, "Missing 'turnAround'.", s);
        return new TurnAroundNode();
    }

    /**
     * Parses the shield on action and returns a shield on node (ShieldOnNode)
     */
    ProgramNode parseShieldOn(Scanner s){
        require(SHIELDON, "Missing 'shieldOn'.", s);
        return new ShieldOnNode();
    }

    /**
     * Parses the shield off action and returns a shield off node (ShieldOffNode)
     */
    ProgramNode parseShieldOff(Scanner s){
        require(SHIELDOFF, "Missing 'shieldOff'.", s);
        return new ShieldOffNode();
    }

    /**
     * Parses the take fuel action and returns a take fuel node (FuelNode)
     */
    ProgramNode parseFuel(Scanner s){
        require(TAKEFUEL, "Missing 'takeFuel'.", s);
        return new FuelNode();
    }

    /**
     * Parses the wait action and returns an empty/with expression wait node
     */
    ProgramNode parseWait(Scanner s){
        require(WAIT, "Missing 'wait'.", s);
        if(s.hasNext(OPENPAREN)){ // If wait action contains expression
            require(OPENPAREN, "Missing open parenthesis.", s);
            ExpressionNode num = parseExpr(s);
            require(CLOSEPAREN, "Missing closing parenthesis.", s);
            return new WaitNode(num);
        }
        return new WaitNode();
    }

    /**
     * Parses through the different expression depending on their type
     */
    // ERROR
    ExpressionNode parseExpr(Scanner s){
        if(!s.hasNext()){fail("Empty expr", s);}
        if(s.hasNext(NUMPAT)){ // It is a number
            int num = requireInt(NUMPAT, "Not an integer", s);
            return new NumNode(num);
        }else if(s.hasNext(SENSOR)){ // It is a sensor
            return parseSensor(s);
        }else if(s.hasNext(OPERATION)){ // It is an operator
            return parseOp(s);
        }else if(s.hasNext(VARIABLE)){
            String v = s.next();
            if(variables.containsKey(v)){
                return new VariableNode(v);
            }else{
                variables.put(v, 0);
                return new VariableNode(v);
            }
        }
        fail("Not a valid expr.", s);
        return null;
    }

    /**
     * Parses loop action
     */
    //ERROR
    ProgramNode parseLoop(Scanner s){
        if(!s.hasNext()){fail("Empty loop", s);}
        require(LOOP, "Missing 'loop'", s);
        return new LoopNode(parseBlock(s));
    }

    /**
     * Parses block of different statements
     */
    ProgramNode parseBlock(Scanner s){
        require(OPENBRACE, "Missing open brace.", s);
        BlockNode block = new BlockNode();
        while(!s.hasNext(CLOSEBRACE) && (s.hasNext(STMT) || s.hasNext(ACT))){
            block.addStatement(parseStatement(s));
        }

        if(block.getSize() == 0){
            fail("Empty block.", s);
        }

        require(CLOSEBRACE, "Missing closing brace.", s);
        return block;
    }

    /**
     * Parses if statements
     */
    // ERROR
    ProgramNode parseIf(Scanner s){
        if(!s.hasNext()){fail("Empty If.", s);}
        if(s.hasNext(IF_ELIF)){
            require(IF_ELIF, "Missing 'if' or 'elif'.", s);
            require(OPENPAREN, "Missing open parenthesis.", s);
            IfNode ifNode = new IfNode(parseCond(s));
            require(CLOSEPAREN, "Missing closing parenthesis.", s);
            ifNode.setBlock((BlockNode) parseBlock(s));

            while(s.hasNext(ELIF)){ ifNode.addElif((IfNode) parseIf(s));}

            if(s.hasNext(ELSE)){
                require(ELSE, "Missing 'else'.", s);
                ifNode.setElseBlock((BlockNode)parseBlock(s));
            }
            return ifNode;
        }

        return null;
    }

    /**
     * Parses while statements
     */
    ProgramNode parseWhile(Scanner s){
        if(!s.hasNext()){fail("Empty while", s);}
        require(WHILE, "Missing 'while'.", s);
        require(OPENPAREN, "Missing open parenthesis.", s);
        WhileNode whileNode = new WhileNode(parseCond(s));
        require(CLOSEPAREN, "Missing closing parenthesis.", s);
        whileNode.setBlock((BlockNode) parseBlock(s));
        return whileNode;
    }

    /**
     * Parses through different condition
     */
    ConditionNode parseCond(Scanner s) {
        if (!s.hasNext()) { fail("Empty condition", s); }
        if (s.hasNext(RELOP)) { return parseRelop(s); }
        else if(s.hasNext(CONDITIONAL)){ return parseCondOp(s); }
        fail("Not a valid Condition.", s);
        return null;
    }

    /**
     * Parses through different RELOP
     */
    ConditionNode parseRelop(Scanner s){
            if (s.hasNext(LT)) {
                require(LT, "Missing 'lt'.", s);
                require(OPENPAREN, "Missing open parenthesis.", s);
                ExpressionNode expr1 = parseExpr(s);
                require(COMMA, "Missing ','.", s);
                ExpressionNode expr2 = parseExpr(s);
                require(CLOSEPAREN, "Missing closing parenthesis.", s);
                return new LtNode(expr1, expr2);
            } else if (s.hasNext(GT)) {
                require(GT, "Missing 'gt'.", s);
                require(OPENPAREN, "Missing open parenthesis.", s);
                ExpressionNode expr1 = parseExpr(s);
                require(COMMA, "Missing ','.", s);
                ExpressionNode expr2 = parseExpr(s);
                require(CLOSEPAREN, "Missing closing parenthesis.", s);
                return new GtNode(expr1, expr2);
            } else if (s.hasNext(EQ)) {
                require(EQ, "Missing 'eq'.", s);
                require(OPENPAREN, "Missing open parenthesis.", s);
                ExpressionNode expr1 = parseExpr(s);
                require(COMMA, "Missing ','.", s);
                ExpressionNode expr2 = parseExpr(s);
                require(CLOSEPAREN, "Missing closing parenthesis.", s);
                return new EqNode(expr1, expr2);
            }
            fail("Not a valid RELOP.", s);
            return null;
    }

    /**
     * Parses through different conditionals
     */
    ConditionNode parseCondOp(Scanner s){
        if(!s.hasNext()){fail("Empty Conditional", s);}

        if(s.hasNext("and")){
            require("and", "Missing 'and'.", s);
            require(OPENPAREN, "Missing open parenthesis", s);
            ConditionNode cond1 = parseCond(s);
            require(",", "Missing comma", s);
            ConditionNode cond2 = parseCond(s);
            require(CLOSEPAREN, "Missing closing parenthesis", s);
            return new andNode(cond1, cond2);
        }else if(s.hasNext("or")){
            require("or", "Missing 'or'.", s);
            require(OPENPAREN, "Missing open parenthesis", s);
            ConditionNode cond1 = parseCond(s);
            require(",", "Missing comma", s);
            ConditionNode cond2 = parseCond(s);
            require(CLOSEPAREN, "Missing closing parenthesis", s);
            return new orNode(cond1, cond2);
        }else if(s.hasNext("not")){
            require("not", "Missing 'not'.", s);
            require(OPENPAREN, "Missing open parenthesis", s);
            ConditionNode cond = parseCond(s);
            require(CLOSEPAREN, "No closing parenthesis", s);
            return new notNode(cond);
        }
        fail("Invalid Conditional", s);
        return null;
    }

    /**
     * Parses through different sensors
     */
    ExpressionNode parseSensor(Scanner s){
        if(!s.hasNext()){fail("Empty Sensor", s);}

        if(s.hasNext("fuelLeft")){ s.next(); return new fuelLeftNode(); }
        else if(s.hasNext("oppLR")){ s.next(); return new oppLRNode(); }
        else if(s.hasNext("oppFB")){ s.next(); return new oppFBNode(); }
        else if(s.hasNext("numBarrels")){ s.next(); return new numBarrelsNode(); }
        else if(s.hasNext("barrelLR")){ s.next(); return parseBarrelLr(s); }
        else if(s.hasNext("barrelFB")){ s.next(); return parseBarrelfb(s); }
        else if(s.hasNext("wallDist")){ s.next(); return new wallDistNode(); }

        fail("Invalid sensor", s);
        return null;
    }

    /**
     * Parses through BarrelFB sensor
     */
    ExpressionNode parseBarrelfb(Scanner s) {
        if(s.hasNext(OPENPAREN)){ // If barrelFB action contains expression
            require(OPENPAREN, "Missing open parenthesis.", s);
            ExpressionNode expr = parseExpr(s);
            require(CLOSEPAREN, "Missing closing parenthesis.", s);
            return new barrelFBNode(expr);

        }
        return new barrelFBNode();
    }

    /**
     * Parses through BarrelRL sensor
     */
    ExpressionNode parseBarrelLr(Scanner s) {
        if(s.hasNext(OPENPAREN)){ // If barrelLR action contains expression
            require(OPENPAREN, "Missing open parenthesis.", s);
            ExpressionNode expr = parseExpr(s);
            require(CLOSEPAREN, "Missing closing parenthesis.", s);
            return new barrelLRNode(expr);

        }
        return new barrelLRNode();
    }

    /**
     * Parses through different operations
     */
    ExpressionNode parseOp(Scanner s){
        if(!s.hasNext()){fail("Empty expr", s);}

        if(s.hasNext(ADD)){
            s.next();
            require(OPENPAREN, "No open parenthesis.", s);
            ExpressionNode condOne = parseExpr(s);
            require(",", "No comma", s);
            ExpressionNode condTwo = parseExpr(s);
            require(CLOSEPAREN, "No close parenthesis.", s);
            return new addNode(condOne,condTwo);
        }else if(s.hasNext(SUB)){
            s.next();
            require(OPENPAREN, "No open parenthesis.", s);
            ExpressionNode condOne = parseExpr(s);
            require(",", "No comma at sub", s);
            ExpressionNode condTwo = parseExpr(s);
            require(CLOSEPAREN, "No close parenthesis.", s);
            return new subNode(condOne,condTwo);
        }else if(s.hasNext(MUL)){
            s.next();
            require(OPENPAREN, "No open parenthesis.", s);
            ExpressionNode condOne = parseExpr(s);
            require(",", "No comma", s);
            ExpressionNode condTwo = parseExpr(s);
            require(CLOSEPAREN, "No close parenthesis.", s);
            return new mulNode(condOne,condTwo);
        }else if(s.hasNext(DIV)){
            s.next();
            require(OPENPAREN, "No open parenthesis.", s);
            ExpressionNode condOne = parseExpr(s);
            require(",", "No comma", s);
            ExpressionNode condTwo = parseExpr(s);
            require(CLOSEPAREN, "No close parenthesis.", s);
            return new divNode(condOne,condTwo);
        }else{
            fail("Not a valid OP", s);
        }
        return null;
    }

    ProgramNode parseAssign(Scanner s){
        if(s.hasNext(VARIABLE)){
            String v = s.next();
            require("=", "Missing '='.", s);
            ExpressionNode expr = parseExpr(s);
            return new AssignNode(v, expr);
        }
        fail("Not a valid variable name.", s);
        return null;
    }

    //----------------------------------------------------------------
    // utility methods for the parser
    // - fail(..) reports a failure and throws exception
    // - require(..) consumes and returns the next token as long as it matches the pattern
    // - requireInt(..) consumes and returns the next token as an int as long as it matches the pattern
    // - checkFor(..) peeks at the next token and only consumes it if it matches the pattern

    /**
     * Report a failure in the parser.
     */
    static void fail(String message, Scanner s) {
        String msg = message + "\n   @ ...";
        for (int i = 0; i < 5 && s.hasNext(); i++) {
            msg += " " + s.next();
        }
        throw new ParserFailureException(msg + "...");
    }

    /**
     * Requires that the next token matches a pattern if it matches, it consumes
     * and returns the token, if not, it throws an exception with an error
     * message
     */
    static String require(String p, String message, Scanner s) {
        if (s.hasNext(p)) {return s.next();}
        fail(message, s);
        return null;
    }

    static String require(Pattern p, String message, Scanner s) {
        if (s.hasNext(p)) {return s.next();}
        fail(message, s);
        return null;
    }

    /**
     * Requires that the next token matches a pattern (which should only match a
     * number) if it matches, it consumes and returns the token as an integer
     * if not, it throws an exception with an error message
     */
    static int requireInt(String p, String message, Scanner s) {
        if (s.hasNext(p) && s.hasNextInt()) {return s.nextInt();}
        fail(message, s);
        return -1;
    }

    static int requireInt(Pattern p, String message, Scanner s) {
        if (s.hasNext(p) && s.hasNextInt()) {return s.nextInt();}
        fail(message, s);
        return -1;
    }

    /**
     * Checks whether the next token in the scanner matches the specified
     * pattern, if so, consumes the token and return true. Otherwise, returns
     * false without processing anything.
     */
    static boolean checkFor(String p, Scanner s) {
        if (s.hasNext(p)) {s.next(); return true;}
        return false;
    }

    static boolean checkFor(Pattern p, Scanner s) {
        if (s.hasNext(p)) {s.next(); return true;} 
        return false;
    }

}

// You could add the node classes here or as separate java files.
// (if added here, they must not be declared public or private)
// For example:
//  class BlockNode implements ProgramNode {.....
//     with fields, a toString() method and an execute() method
//

class ProgNode implements ProgramNode{
    ArrayList<ProgramNode> statements;

    public ProgNode(ArrayList<ProgramNode> statements){
        this.statements = statements;
    }

    public void addStatement(ProgramNode n){
        this.statements.add(n);
    }

    public void execute(Robot robot) {
        for(ProgramNode statement: statements){
            statement.execute(robot);
        }
    }

    public ArrayList<ProgramNode> getStatements(){
        return this.statements;
    }

    public String toString(){
        /*public String toString(){
        String s = "";
        for(ProgramNode statement: statements){
            s = s + statement.toString() + " ";
        }

        return s;
    }*/
        StringBuilder s = new StringBuilder();
        for(ProgramNode statement: statements){
            s.append(statement.toString()).append("\n");
        }

        return s.toString();
    }
}
class StatementNode implements ProgramNode{
    ProgramNode statement;

    public StatementNode(ProgramNode n){ this.statement = n; }
    public String toString(){ return this.statement.toString(); }

    public void execute(Robot robot){ this.statement.execute(robot); }
}
class VariableNode implements ExpressionNode{
    public String name;
    public ExpressionNode expr;

    public VariableNode(String name){ this.name = name; }

    public int evaluate(Robot robot){ return Parser.variables.get(this.name); }

    public String toString() { return this.name; }
}
class AssignNode implements ProgramNode{
    public String name;
    public ExpressionNode expr;

    public AssignNode(String name, ExpressionNode expr){
        this.name = name;
        this.expr = expr;
    }

    public void setExpr(ExpressionNode expr){ this.expr = expr; }

    public void setName(String name){ this.name = name; }

    public void execute(Robot robot){Parser.variables.put(this.name, this.expr.evaluate(robot));}

    public String toString(){
        return this.name + " = " + this.expr.toString();
    }
}
//-- ACT CLASSES ---------------------------------------------------------------------------------------------------------------------
class MoveNode implements ProgramNode{
    ExpressionNode num = null;
    public MoveNode(){}
    public MoveNode(ExpressionNode n){this.num = n;}

    public void execute(Robot robot) {
        // move the robot multiple times
        if(num != null){
            for(int i = 0; i < num.evaluate(robot); i++){
                robot.move();
            }
        }else{robot.move();}
    }

    public void setNum(NumNode i){this.num = i;}

    public String toString() {
        if(num != null){
            return "move(" + num.toString() + ")";
        }
        return "move";
    }
}
class LeftNode implements ProgramNode{
    public LeftNode(){}
    public void execute(Robot robot) {robot.turnLeft();}
    public String toString() {return "turnL";}
}
class RightNode implements ProgramNode{
    public RightNode(){}
    public void execute(Robot robot) {robot.turnRight();}
    public String toString() {return "turnR";}
}
class TurnAroundNode implements ProgramNode{
    public TurnAroundNode(){}
    public void execute(Robot robot) {robot.turnAround();}
    public String toString() {return "turnAround";}
}
class ShieldOnNode implements ProgramNode{
    public ShieldOnNode(){}
    public void execute(Robot robot) {robot.setShield(true);}
    public String toString() {return "shieldOn";}
}
class ShieldOffNode implements ProgramNode{
    public ShieldOffNode(){}
    public void execute(Robot robot) {robot.setShield(false);}
    public String toString() {return "shieldOff";}
}
class FuelNode implements ProgramNode{
    public FuelNode(){}
    public void execute(Robot robot) {robot.takeFuel();}
    public String toString() {return "takeFuel";}
}
class WaitNode implements ProgramNode{
    ExpressionNode num = null;

    public WaitNode(){}
    public WaitNode(ExpressionNode n){this.num = n;}

    public void execute(Robot robot) {
        if(num != null){
            for(int i = 0; i < num.evaluate(robot); i++){
                robot.idleWait();
            }
        }
        robot.idleWait();
    }

    public void setNum(NumNode n){this.num = n;}

    public String toString() {
        if(num != null){
            return "wait(" + num.toString() + ")";
        }
        return "wait";
    }
}
//-- LOOP CLASSES ---------------------------------------------------------------------------------------------------------------------
class LoopNode implements ProgramNode{
    public ProgramNode block;

    public LoopNode(ProgramNode blockNode){ this.block = blockNode; }

    public void execute(Robot robot) {
        while (true) {
            this.block.execute(robot);
        }
    }

    public String toString(){
        return "loop \n{" + block.toString() + "}";
    }
}
class IfNode implements ProgramNode {
    ConditionNode cond;
    ProgramNode block;
    ArrayList<IfNode> elifList;
    ProgramNode elseBlock;

    public IfNode(ConditionNode n) {
        this.cond = n;
    }

    public void setBlock(BlockNode block) {
        this.block = block;
    }

    public void setElseBlock(BlockNode elseBlock) {
        this.elseBlock = elseBlock;
    }

    public void addElif(IfNode n) {
        if (this.elifList == null) {
            this.elifList = new ArrayList<IfNode>();
        }
        this.elifList.add(n);
    }

    public void execute(Robot robot) {
        if (cond.evaluate(robot)) {
            block.execute(robot);
        } else {

            if (this.elifList != null) {
                for (IfNode ifnode : this.elifList) {
                    ifnode.execute(robot);
                    return;
                }
            }

            if (elseBlock != null) {
                elseBlock.execute(robot);
            }
        }
    }

    public String toString() {
        StringBuilder str = new StringBuilder();

        str.append("if (").append(cond.toString()).append(") {").append(this.block.toString()).append("}");

        if (elifList != null) {
            for (IfNode ifnode : elifList) {
                str.append("el").append(ifnode.toString());
            }
        }

        if (elseBlock != null) {
            str.append("else").append(this.elseBlock.toString());
        }

        return str.toString();
    }
}
class WhileNode implements ProgramNode{
    ConditionNode cond;
    ProgramNode block;

    public WhileNode(ConditionNode n){this.cond = n;}

    public void setBlock(BlockNode block){this.block = block;}

    public void execute(Robot robot){
        while(cond.evaluate(robot)){
            block.execute(robot);
        }
    }

    public String toString() {
        return ("while (" + cond.toString() + ") " + block.toString());

    }
}
class BlockNode implements ProgramNode {
    ArrayList<ProgramNode> statements;

    public BlockNode() {
        this.statements = new ArrayList<ProgramNode>();
    }

    public void addStatement(ProgramNode statement) {
        this.statements.add(statement);
    }

    public void execute(Robot robot) {
        for (ProgramNode statement : this.statements) {
            statement.execute(robot);
        }
    }

    public String toString() {
        StringBuilder str = new StringBuilder("\n");

        for(ProgramNode n: this.statements){
            str.append("\t").append(n.toString()).append("\n");
        }
        return str.toString();
    }

    public int getSize() {
        return this.statements.size();
    }
}
//-- RELOP CLASSES ---------------------------------------------------------------------------------------------------------------------
class LtNode implements ConditionNode{
    ExpressionNode e1;
    ExpressionNode e2;

    public LtNode(ExpressionNode e1, ExpressionNode e2){
        this.e1 = e1;
        this.e2 = e2;
    }
    public boolean evaluate(Robot robot) { return (e1.evaluate(robot) < e2.evaluate(robot)); }

    public String toString() {return "lt( " + e1.toString() + ", " + e2.toString() + " )";}
}
class GtNode implements ConditionNode{
    ExpressionNode e1;
    ExpressionNode e2;

    public GtNode(ExpressionNode e1, ExpressionNode e2){
        this.e1 = e1;
        this.e2 = e2;
    }
    public boolean evaluate(Robot robot) { return (e1.evaluate(robot) > e2.evaluate(robot)); }

    public String toString() {return "gt( " + e1.toString() + ", " + e2.toString() + " )";}
}
class EqNode implements ConditionNode{
    ExpressionNode e1;
    ExpressionNode e2;

    public EqNode(ExpressionNode e1, ExpressionNode e2){
        this.e1 = e1;
        this.e2 = e2;
    }
    public boolean evaluate(Robot robot) { return (e1.evaluate(robot) == e2.evaluate(robot)); }

    public String toString() {return "eq( " + e1.toString() + ", " + e2.toString() + " )";}
}
//-- SENSORS CLASSES ---------------------------------------------------------------------------------------------------------------------
class fuelLeftNode implements ExpressionNode{
    public fuelLeftNode(){}
    public int evaluate(Robot robot){return robot.getFuel();}
    public String toString() {return "fuelLeft";}
}
class oppLRNode implements ExpressionNode{
    public oppLRNode(){}
    public int evaluate(Robot robot){return robot.getOpponentLR();}
    public String toString() {return "oppLR";}
}
class oppFBNode implements ExpressionNode{
    public oppFBNode(){}
    public int evaluate(Robot robot){return robot.getOpponentFB();}
    public String toString() {return "oppLR";}
}
class barrelLRNode implements ExpressionNode{
    ExpressionNode expr;

    public barrelLRNode(){}
    public barrelLRNode(ExpressionNode expr){this.expr = expr;}

    public int execute(Robot robot){
        if(expr == null){
            return robot.getClosestBarrelLR();
        }else{
            return robot.getBarrelLR(this.expr.evaluate(robot));
        }
    }

    public int evaluate(Robot robot){return execute(robot);}

    public String toString()  {
        if (this.expr == null) {
            return "LR distance to closest barrel";
        } else {
            return "LR distance to barrel no. 'argument'";
        }
    }
}
class barrelFBNode implements ExpressionNode{
    ExpressionNode expr;

    public barrelFBNode(){}
    public barrelFBNode(ExpressionNode expr){this.expr = expr;}

    public int execute(Robot robot){
        if(expr == null){
            return robot.getClosestBarrelFB();
        }else{
            return robot.getBarrelFB(this.expr.evaluate(robot));
        }
    }

    public int evaluate(Robot robot){return execute(robot);}

    public String toString()  {
        if (expr == null) {
            return "FB distance to closest barrel";
        }
        else {
            return "FB distance to barrel no. 'argument'";
        }
    }
}
class numBarrelsNode implements ExpressionNode{
    public numBarrelsNode(){}
    public int evaluate(Robot robot){return robot.numBarrels();}
    public String toString() {return "numBarrels";}
}
class wallDistNode implements ExpressionNode{
    public wallDistNode(){}
    public int evaluate(Robot robot){return robot.getDistanceToWall();}
    public String toString() {return "wallDist";}
}
// -- OP CLASSES ------------------------------------------------------------------------------------------------------------------------------
class NumNode implements ExpressionNode{
    int num;
    public NumNode(int n){

        this.num = n;
    }

    public void setNum(int n){this.num = n;}
    public int evaluate(Robot robot){
        return this.num;
    }

    public String toString(){
        return String.valueOf(this.num);
    }
}
class addNode implements ExpressionNode{
    ExpressionNode conditionOne;
    ExpressionNode conditionTwo;

    public addNode(ExpressionNode one, ExpressionNode two){
        this.conditionOne = one;
        this.conditionTwo = two;
    }

    public void setConOne(ExpressionNode n){this.conditionOne = n;}
    public void setConTwo(ExpressionNode n){this.conditionTwo = n;}

    public int evaluate(Robot robot){
        return conditionOne.evaluate(robot) + conditionTwo.evaluate(robot);
    }

    public String toString() {
        return "add(" + conditionOne.toString() + "+" + conditionTwo.toString() + ")";
    }
}
class subNode implements ExpressionNode{
    ExpressionNode conditionOne;
    ExpressionNode conditionTwo;

    public subNode(ExpressionNode one, ExpressionNode two){
        this.conditionOne = one;
        this.conditionTwo = two;
    }

    public void setConOne(ExpressionNode n){this.conditionOne = n;}
    public void setConTwo(ExpressionNode n){this.conditionTwo = n;}

    public int evaluate(Robot robot){
        return conditionOne.evaluate(robot) - conditionTwo.evaluate(robot);
    }

    public String toString() {
        return "sub(" + conditionOne.toString() + ", " + conditionTwo.toString() + ")";
    }
}
class mulNode implements ExpressionNode{
    ExpressionNode conditionOne;
    ExpressionNode conditionTwo;

    public mulNode(ExpressionNode one, ExpressionNode two){
        this.conditionOne = one;
        this.conditionTwo = two;
    }

    public void setConOne(ExpressionNode n){this.conditionOne = n;}
    public void setConTwo(ExpressionNode n){this.conditionTwo = n;}

    public int evaluate(Robot robot){
        return conditionOne.evaluate(robot) * conditionTwo.evaluate(robot);
    }

    public String toString() {
        return "mul(" + conditionOne.toString() + "*" + conditionTwo.toString() + ")";
    }
}
class divNode implements ExpressionNode{
    ExpressionNode conditionOne;
    ExpressionNode conditionTwo;

    public divNode(ExpressionNode one, ExpressionNode two){
        this.conditionOne = one;
        this.conditionTwo = two;
    }

    public void setConOne(ExpressionNode n){this.conditionOne = n;}
    public void setConTwo(ExpressionNode n){this.conditionTwo = n;}

    public int evaluate(Robot robot){
        return conditionOne.evaluate(robot) / conditionTwo.evaluate(robot);
    }

    public String toString() {
        return "div(" + conditionOne.toString() + "/" + conditionTwo.toString() + ")";
    }
}
// -- CONDITIONAL CLASSES ------------------------------------------------------------------------------------------------------------------------------
class andNode implements ConditionNode{
    ConditionNode conditionOne;
    ConditionNode conditionTwo;
    public andNode(ConditionNode one, ConditionNode two){
        this.conditionOne = one;
        this.conditionTwo = two;
    }

    public boolean evaluate(Robot robot){
        return conditionOne.evaluate(robot) && conditionTwo.evaluate(robot);
    }

    public String toString() {
        return "(" + conditionOne.toString() + ") AND (" + conditionTwo.toString() + ")";
    }
}
class orNode implements ConditionNode{
    ConditionNode conditionOne;
    ConditionNode conditionTwo;
    public orNode(ConditionNode one, ConditionNode two){
        this.conditionOne = one;
        this.conditionTwo = two;
    }

    public boolean evaluate(Robot robot){
        return conditionOne.evaluate(robot) || conditionTwo.evaluate(robot);
    }

    public String toString() {
        return "(" + conditionOne.toString() + ") OR (" + conditionTwo.toString() + ")";
    }
}
class notNode implements ConditionNode{
    ConditionNode condition;
    public notNode(ConditionNode c){
        this.condition = c;
    }

    public boolean evaluate(Robot robot){
        return !condition.evaluate(robot);
    }

    public String toString() {
        return "NOT (" + condition.toString() + " )";
    }
}