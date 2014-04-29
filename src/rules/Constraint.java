package rules;

import java.util.Set;

/**
 *
 * @author Andreas Dressel
 */
public class Constraint
{
    public enum Type {
        EQUAL, NEQUAL;
    }
    
    
    private final Set<Variable> var_1;
    private final Set<Variable> var_2;
    private final Type type;
    
    public Constraint( Set<Variable> var_1, Set<Variable> var_2, Type type )
    {
        this.var_1 = var_1;
        this.var_2 = var_2;
        this.type = type;
    }

    
    
    
    public boolean containsVar( Variable var )
    {
        return ( this.var_1.contains( var ) || this.var_2.contains( var ) );
    }
    
    public Type getType()
    {
        return this.type;
    }

    public Set<Variable> getVar2()
    {
        return var_2;
    }
    
    public Set<Variable> getVar1()
    {
        return var_1;
    }
    
}
