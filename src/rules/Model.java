package rules;

import java.util.Set;

/**
 *
 * @author Andreas Dressel
 */
public class Model
{
    private Set<Rule> rules;
    private final VariableDictionary dictionary;
    
    public Model()
    {
        this.dictionary = VariableDictionary.getInstance();
    }
    
    
    public void addRule( String rule )
    {
        String[] temp = rule.split( "=>" );
    }
}
