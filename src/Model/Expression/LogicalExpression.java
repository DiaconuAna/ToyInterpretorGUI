package Model.Expression;

import Exceptions.ExpressionException;
import Model.ADTs.HeapInterface;
import Model.Expression.ExpressionInterface;
import Model.Types.BoolType;
import Model.Types.TypeInterface;
import Model.ADTs.DictInterface;
import Model.Value.BoolValue;
import Model.Value.ValueInterface;

import java.util.Objects;

public class LogicalExpression implements ExpressionInterface{
    ExpressionInterface exp1, exp2;
    int operation;

    public LogicalExpression(String op, ExpressionInterface e1, ExpressionInterface e2){
        this.exp1 = e1;
        this.exp2 = e2;

        if(Objects.equals(op, "and"))
            this.operation = 1;
        else if(Objects.equals(op, "or"))
            this.operation = 2;

    }

    @Override
    public ValueInterface eval(DictInterface<String, ValueInterface> table, HeapInterface<ValueInterface> heap) throws ExpressionException {
        ValueInterface v1,v2;
        v1 = this.exp1.eval(table, heap);

        if(v1.getType().equals(new BoolType())){
            v2 = this.exp2.eval(table, heap);
            if(v2.getType().equals(new BoolType())){
                BoolValue b1 = (BoolValue)v1;
                BoolValue b2 = (BoolValue)v2;

                boolean n1, n2;
                n1 = b1.getValue();
                n2 = b2.getValue();

                if(this.operation == 1)
                    return new BoolValue(n1 && n2);
                if(this.operation == 2)
                    return new BoolValue(n1 || n2);
                throw new ExpressionException("Invalid operation. Please try again!");

            }
            else
                throw new ExpressionException("Right side cannot be evaluated");

        }
        else
            throw new ExpressionException("Left side cannot be evaluated");
    }

    @Override
    public ExpressionInterface deepCopy() {
        return new LogicalExpression(getOperand(), this.exp1.deepCopy(), this.exp2.deepCopy());
    }

    @Override
    public TypeInterface typecheck(DictInterface<String, TypeInterface> typeEnv) throws ExpressionException {
        TypeInterface type1, type2;
        type1 = this.exp1.typecheck(typeEnv);
        type2 = this.exp2.typecheck(typeEnv);

        if(type1.equals(new BoolType())){
            if(type2.equals(new BoolType())){
                return new BoolType();
            }
            else
                throw new ExpressionException("2nd operand is not a boolean");

        }
        else
            throw new ExpressionException("1st operand is not a boolean");
    }

    @Override
    public String toString(){
        if(this.operation == 1)
            return this.exp1.toString() + " and " + this.exp2.toString();
        if(this.operation == 2)
            return this.exp1.toString() + " or " + this.exp2.toString();
        return "";
    }

    public ExpressionInterface getFirst(){
        return this.exp1;
    }

    public ExpressionInterface getSecond(){
        return this.exp2;
    }

    public String getOperand(){
        if(this.operation == 1)
            return "and";
        if(this.operation == 2)
            return "or";
        return "";
    }
}
