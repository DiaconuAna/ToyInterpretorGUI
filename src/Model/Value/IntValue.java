package Model.Value;

import Model.Types.TypeInterface;
import Model.Types.IntType;


public class IntValue implements ValueInterface {
    int val;

    public IntValue() {this.val = 0;}
    public IntValue(int v){this.val = v;}

    @Override
    public TypeInterface getType() {
        return new IntType();
    }

    public int getVal(){
        return this.val;
    }

    @Override
    public ValueInterface deepCopy() {
        return new IntValue(this.val);
    }

    @Override
    public String toString(){
        return Integer.toString(this.val);
    }

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof IntValue))
            return false;

        IntValue o_val = (IntValue) obj;
        return o_val.val == this.val;
    }
}
