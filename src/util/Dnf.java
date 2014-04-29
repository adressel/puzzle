/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import rules.VariableDictionary;
import java.util.HashSet;
import java.util.Set;

/**
 * A DNF represents a set of unique clauses (unique by their contained literals)
 * that are combined by the logic OR operator.
 *
 * @author Andreas Dressel
 */
public class Dnf
{
    
    private final Set<Clause> clauses;
    private final VariableDictionary dictionary;
    private int helper_variable_counter;
    
    public Dnf( VariableDictionary dictionary )
    {
        this.clauses = new HashSet<Clause>();
        this.dictionary = dictionary;
        this.helper_variable_counter = 0;
    }
    
    private int getNextHelperVarialbeNumber() 
    {
        this.helper_variable_counter += 1;
        return this.helper_variable_counter;
    }
    
    public void addClause( Clause clause )
    {
        // only possible to add clauses by an OR operator at the moment
        
        this.clauses.add( clause );
    }
    
    public Cnf convertToCnf()
    {
        Cnf cnf = new Cnf( this.dictionary );
        
        Clause helper_variables = new Clause();
        
        for( Clause c : this.clauses ) {
            
            long symbol = this.dictionary.getSymbolForVariable( "helper_var_dnf" + getNextHelperVarialbeNumber() );
            helper_variables.addLiteral( new Literal( symbol, true ) );
            
            for( Literal l : c.getLiterals() ) {
                
                Clause clause = new Clause( l );
                clause.addLiteral( new Literal( symbol, false ) ); // **** this seems to be a problem...
                
                cnf.addClause( clause, "AND" );
                
            }
        }
        
        // **** apparently, the clause where all the helper varialbes Z are added is not satisfiable
        cnf.addClause( helper_variables, "AND" );
        
        
        return cnf;
    }
    
    
}
