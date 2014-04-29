package rules;

import java.util.Set;

/**
 *
 * @author Andreas Dressel
 * @param <T>
 */
public class Variable<T>
{
    private final String name;
    private final Set<T> possible_values;
    
    public Variable( String name, Set<T> possible_values )
    {
        this.name = name;
        this.possible_values = possible_values;
    }

    // consider overriding hashcode()
    
    @Override
    public boolean equals( Object other )
    {
        if( other instanceof Variable ) {
            // when should two varialbes be seen as equal?
            // -> same name, same possible values
            // @Todo: implementation
            if( ( (Variable)other).name.equals( this.name ) ) {
                return true;
            }
            
            
        }
        return false;
    }
    
    public String getName()
    {
        return name;
    }

    public Set<T> getPossible_values()
    {
        return possible_values;
    }
    
    public Variable deepCopy() 
    {
        //Set<T> possible_values = new HashSet<T>();
        
        //for( T value : this.possible_values ) {
            //possible_values.add( value );
        //}
        
        // although this is not a deep copy, it should be sufficient
        return new Variable( this.name, this.possible_values );
    }
}
