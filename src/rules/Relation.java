package rules;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author Andreas Dressel
 */
public class Relation
{
    private final String name;
    private final boolean sign;
    private final Set<Variable> variables;
    private final SortedSet<Constant> constants;
    private final VariableDictionary dictionary = VariableDictionary.getInstance();
    
    public Relation( String name, boolean sign, Set<Variable> variables, Set<Constant> constants )
    {
        this.name = name;
        this.sign = sign;
        this.variables = variables;
        this.constants = new TreeSet<Constant>( new Comparator<Constant>()
        {

            @Override
            public int compare( Constant o1, Constant o2 )
            {
                return o1.getName().compareTo( o2.getName() );
            }
        });
        
        this.constants.addAll( constants );
    }
    
    public Relation( String name, boolean sign, Set<Constant> constants )
    {
        this.name = name;
        this.sign = sign;
        this.variables = new HashSet<Variable>();
        this.constants = new TreeSet<Constant>( new Comparator<Constant>()
        {

            @Override
            public int compare( Constant o1, Constant o2 )
            {
                return o1.getName().compareTo( o2.getName() );
            }
        });
        
        this.constants.addAll( constants );
    }
    
    public long generateVariableInDictionary() throws Exception
    {
        if( !this.variables.isEmpty() ) {
            throw new Exception( "Not all variables have been replaced by constants!" );
        }
        
        // generate the variable's name in the form: 
        // <name>( <const1_name>:<const1_value> <const2_name>: ... <var_part_name>: <value> )
        StringBuilder builder = new StringBuilder( this.name );
        builder.append( "( " );
        for ( Constant c : this.constants ) {
            builder.append( c.getName() );
            builder.append( ":" );
            builder.append( c.getValue().toString() );
            builder.append( " " );
        }

        builder.append( ")" );

        return dictionary.getSymbolForVariable( builder.toString() );

    }
    
    public void replaceVariable( Variable original, Variable replacement ) 
    {
        // adds the replacement if it has successfully removed the original
        // -> must contain the original variable
        if( this.variables.remove( original ) ) {
            this.variables.add( replacement );
        }
    }
    
    public Relation replaceVariableByConstant( Variable variable, Object value )
    {
        Variable v = containsVar( variable );
        if( v != null ) {
            // can't remove here, must be done later (after completing iteration for variable)
            Relation r = deepCopy();
            r.getVariables().removeAll( r.getVariables() );
            for( Variable var : this.variables ) {
                if( !var.equals( v ) ) {
                    r.variables.add( var );
                }
            }
            r.constants.add( new Constant<Object>( v.getName(), value ) );
            return r;
        }
        return this;
    }
        
    public Variable containsVar( Variable var ) 
    {
        for( Variable v : this.variables ) {
            if( var.getName().equals( v.getName() ) ) {
                return v;
            }
        }
        return null;
    }

    public Set<Variable> getVariables()
    {
        return variables;
    }

    public Set<Constant> getConstants()
    {
        return constants;
    }

    public String getName()
    {
        return name;
    }
    
    public boolean getSign()
    {
        return this.sign;
    }
    
    public Relation deepCopy() 
    {
        Set<Variable>  variables = new HashSet<Variable>();
        for( Variable var : this.variables ) {
            variables.add( var.deepCopy() );
        }
        
        SortedSet<Constant> constants = new TreeSet<Constant>( new Comparator<Constant>()
        {
            @Override
            public int compare( Constant o1, Constant o2 )
            {
                return o1.getName().compareTo( o2.getName() );
            }
        });
        for( Constant constant : this.constants ) {
            constants.add( constant.deepCopy() );
        }
        
        // name and sign don't need to be deep copies
        return new Relation( this.name, this.sign, variables, constants);
        
    }
    
    public void print()
    {
        StringBuilder builder = new StringBuilder();
        
        if( !this.sign ) {
            builder.append( "~");
        }
        
        builder.append( this.name );
        builder.append( "( " );
        for ( Constant c : this.constants ) {
            builder.append( c.getName() );
            builder.append( ":" );
            builder.append( c.getValue().toString() );
            builder.append( " " );
        }

        builder.append( ")" );
        
        System.out.print( builder.toString() );
    }
}
