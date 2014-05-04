package rules;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Andreas Dressel
 */
public class Rule
{

    private final Set<Relation> conditions;
    private final Set<Relation> outcomes;
    private final Set<Constraint> constraints;

    public Rule( Set<Relation> conditions )
    {
        this.conditions = conditions;
        this.outcomes = new HashSet<Relation>();
        this.constraints = null;
    }

    public Rule( Set<Relation> conditions, Set<Relation> outcome )
    {
        this.conditions = conditions;
        this.outcomes = outcome;
        this.constraints = null;
    }

    public Rule( Set<Relation> conditions, Set<Relation> outcome, Set<Constraint> constraints )
    {
        this.conditions = conditions;
        this.outcomes = outcome;
        this.constraints = constraints;
        
        applyConstraints();
    }
    
    private Rule( Set<Relation> conditions, Relation outcome )
    {
        this.conditions = conditions;
        this.outcomes = new HashSet<Relation>(1);
        this.outcomes.add( outcome );
        this.constraints = null;
    }
    
    private void applyConstraints() 
    {
        if( this.conditions == null ) {
            //return;
        }
        /*
        for( Constraint constraint : this.constraints ) {
            
            switch( constraint.getType() ) {
                case EQUAL:
                    
                    Variable replacement = constraint.getVar1().iterator().next();
                    
                    for( Relation rel : this.conditions ) {
                        for( Variable  original : constraint.getVar1()) {
                            rel.replaceVariable( original, replacement);
                        }
                    }
                    
                    break;
                // handle other cases here
                default:
                    System.err.println( "Constraint type not supported" );
            }
            
        }*/
    }
    
    public static void writeGroundedInstancesToFile( Set<Rule> rules, String file_name )
    {
        // assumption: rules are in 2. NF
        
        BufferedWriter writer = null;
        
        try {
            writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( new File( file_name ) ) ) );

            // write comment lines including the file name and one empty line
            writer.write( "c  " + file_name );
            writer.newLine();
            writer.write( "c  " );
            writer.newLine();
            
            // write the problem form cnf, the number of variables and the number of clauses
            writer.write( "p cnf " + 3060 + " " + ( rules.size() + 16 ) ); // @Todo: how to get correct number of varialbes before calling method
            writer.newLine();
            
            // write clauses to file, each of them followed by 0
            for( Rule rule : rules ) {
                for( Relation relation : rule.conditions ) {
                    if( ( relation.getSign() && !rule.outcomes.isEmpty() ) // due to de morgan -> if !(!sign) == true
                            || ( !relation.getSign() && rule.outcomes.isEmpty() ) ) {
                        writer.write( "-" );
                    }
                    try {
                        writer.write( Long.toString( relation.generateVariableInDictionary() ) + " " );
                    } catch ( Exception ex ) {
                        ex.printStackTrace();
                        System.exit( 0 );
                    }
                }
                for( Relation relation : rule.outcomes ) {
                    if( !relation.getSign() ) {
                        writer.write( "-" );
                    }
                    try {
                        writer.write( Long.toString( relation.generateVariableInDictionary() ) + " " );
                    } catch( Exception ex ) {
                        ex.printStackTrace();
                        System.exit( 0 );
                    }
                }
                
                writer.write( "0" );
                writer.newLine();
                writer.flush();
            }
            
            
        } catch( FileNotFoundException ex ) {
            ex.printStackTrace();
        } catch( IOException ex ) {
            ex.printStackTrace();
        } finally {
            if( writer != null ) {
                try {
                    writer.close();
                } catch( IOException ex ) {
                    ex.printStackTrace();
                }
            }
        }
        
    }
    
    public Set<Rule> getGroundedInstances()
    {
        // handle constraints first by:
        // case EQUAL: replace all affected variable by the same object
        
        
        
        
        // 1. normal form: C1 * C2 * ... * Cn => Outcome
        // - conditions may still contain variables
        // - only one outcome which may contain variables
        
        Set<Rule> nf_1 = new HashSet<Rule>();
        
        // need new objects here...
        for( Relation outcome : this.outcomes ) {
            
            // need deep copy of conditions and outcome
            Set<Relation> conditions = new HashSet<Relation>();
            for( Relation relation : this.conditions ) {
                conditions.add( relation.deepCopy() );
            }
            
            nf_1.add( new Rule( conditions, outcome.deepCopy() ) );
        }
        
        
        // 2. normal form: c1 * c2 * c3 ... * cn => outcome
        // - conditions only contain constants
        // - only one outcome which does not contain variables

                
        // Assuumptions:
        // - each variable has only one constraint on it (per rule)
        // - for the first (dumb) solution, the program is limited to the EQUAL constraint
        //   since it is necessary to make the program work logically
        
        Set<Rule> nf_2 = new HashSet<Rule>();
        
        // create list of variables that have not been replaced by constants yet
        List<Variable> todo_list = new ArrayList<Variable>();
        for( Relation r : this.conditions ) {
            todo_list.addAll( r.getVariables() );
        }
        for( Relation r : this.outcomes ) {
            todo_list.addAll( r.getVariables() );
        }
        
        while( !todo_list.isEmpty() ) {
                        
            Variable var = todo_list.remove( 0 ); // also remove all variables that must be the same
            
            Set<Variable> to_be_removed = new HashSet<Variable>();
            for( Variable variable : todo_list ) {
                if( var.equals( variable ) ) {
                    to_be_removed.add( variable );
                }
            }
            todo_list.removeAll( to_be_removed );
            
            
            Set<Rule> to_be_added_to_nf_1 = new HashSet<Rule>();
            
            // iterate through all possible values and replace variable by constants
            for( Object value : var.getPossible_values() ) {
                
                for( Rule rule : nf_1 ) {
                    
                    // only add to nf_2 if they don't contain any more varialbes
                    // otherwise remove old rule from nf_1 and add all the new rules to it
                    
                    Rule r = getRuleReplacingVariableWithConstant( rule, var, value );
                    
                    if( r.containsNoVariables() ) {
                        nf_2.add( r );
                    } else {
                        to_be_added_to_nf_1.add( r );
                    }
                    
                }
            }
            
            nf_1.removeAll( nf_1 );
            
            // @Todo: remove variable var from all relations in to_be_added_to_nf_1
            
            nf_1.addAll( to_be_added_to_nf_1 );
            to_be_added_to_nf_1.removeAll( to_be_added_to_nf_1 );
            
            //System.out.println( "Todo list size: " + todo_list.size() );
            //System.out.println( "NF 1 size: " + nf_1.size() );
        }

        
        // result: set of rules which only contains constants == grounded instances
        
        return nf_2;
    }
    
    
    private Rule getRuleReplacingVariableWithConstant( Rule rule, Variable variable, Object value )
    {
        Set<Relation> conditions = new HashSet<Relation>();
                
        // this will only work, if different objects are used each time a variable is declared!!
        for( Relation rel : rule.conditions ) {
            conditions.add( rel.replaceVariableByConstant( variable, value ) );
        }
        
        Relation outcome = rule.outcomes.iterator().next().replaceVariableByConstant( variable, value );
        
        return new Rule( conditions, outcome );
    }
    
    private boolean containsNoVariables()
    {
        for( Relation relation : this.conditions ) {
            if( !relation.getVariables().isEmpty() ) {
                return false;
            }
        }
        for( Relation relation : this.outcomes ) {
            if( ! relation.getVariables().isEmpty() ) {
                return false;
            }
        }
        return true;
    }
    
    public void print()
    {
        for( Relation r : this.conditions ) {
            r.print();
            System.out.print( " " );
        }
        
        System.out.print( "=> " );
        
        for( Relation r : this.outcomes ) {
            r.print();
            System.out.print( " " );
        }
        
        System.out.println( "" );
    }
}

/*
public static Set<Clause> getClauses( Set<Rule> grounded_instances )
    {
        Set<Clause> clauses = new HashSet<Clause>( grounded_instances.size() );
        
        // important assumption: the grounded instances are in 2. NF
        for( Rule instance : grounded_instances ) {
            
            Clause clause = new Clause();
            for( Relation condition : instance.conditions ) {
                try {
                    clause.addLiteral( new Literal( condition.generateVariableInDictionary(), !condition.getSign() ) );
                } catch( Exception ex ) {
                    ex.printStackTrace();
                    System.exit( 0 );
                }
            }
            for( Relation outcome : instance.outcomes ) {
                // this should only be one iteration per instance (-> test)
                try {
                    clause.addLiteral( new Literal( outcome.generateVariableInDictionary(), outcome.getSign() ) );
                } catch(Exception ex ) {
                    ex.printStackTrace();
                    System.exit( 0 );
                }
            }
            
            // this method takes forever!!
            // in fact, the grounded instances can be written to a file right away
            // (assuming again, that they're in NF 2)
            clauses.add( clause );
            System.out.println( "Clauses added: " + clauses.size() );
        }
        
        
        return clauses;
    }
*/
