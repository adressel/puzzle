package rules;

/**
 *
 * @author Andreas Dressel
 * @param <T>
 */
public class Constant<T>
{
    private final String name;
    private final T value;
    
    public Constant( String name, T value )
    {
        this.name = name;
        this.value = value;
    }
    
    public String getName()
    {
        return name;
    }

    public T getValue()
    {
        return value;
    }
    
    public Constant deepCopy()
    {
        // although this is not a deep copy, it should be sufficient
        return new Constant<T>( this.name, this.value );
    }
    
    public void print() 
    {
        System.out.println( "Constant( name:" + name + " , value:" + value + " )");
    }
}
