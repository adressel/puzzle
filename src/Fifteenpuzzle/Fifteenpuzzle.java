package Fifteenpuzzle;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import rules.Constant;
import rules.Relation;
import rules.Rule;
import rules.Variable;
import rules.VariableDictionary;


/**
 *
 * @author Andreas Dressel
 */
public class Fifteenpuzzle
{
    private final int[][] state;
    
    private final Integer[] position_values;
    private final Integer[] step_values;
    private final Integer[] tile_values;
    
    private enum Operation {
        UP, DOWN, LEFT, RIGHT
    }
    

    
    public Fifteenpuzzle()
    {
        this.position_values = new Integer[16];
        for( int i = 0; i < position_values.length; i++ ) {
            this.position_values[i] = i + 1;
        }
        
        this.step_values = new Integer[81];
        for( int i = 0; i < step_values.length; i++ ) {
            this.step_values[i] = i + 1;
        }
        
        this.tile_values = new Integer[16];
        for( int i = 0; i < tile_values.length; i++ ) {
            this.tile_values[i] = i + 1;
        }
        
        this.state = new int[][]{
            { 2, 3, 4, 7 },
            { 5, 6, 1, 16 },
            { 10, 11, 12, 8 },
            { 9, 13, 14, 15 }
        };
    }
        
        
    // Rule #1:
    private Set<Rule> createRule1()
    {
        Set<Rule> rule1 = new HashSet<Rule>();
        
        
        for( Integer tile1 : tile_values ) {
            
            Set<Variable> variables = new HashSet<Variable>();
            
            variables.add( new Variable<Integer>( "position", new HashSet<Integer>(
                    Arrays.asList( position_values) ) ) );
            
            variables.add( new Variable<Integer>( "step", new HashSet<Integer>(
                    Arrays.asList( step_values ) ) ) );
            
            
            Set<Relation> conditions = new HashSet<Relation>();
            Set<Relation> outcomes = new HashSet<Relation>();
            
            Set<Constant> constants = new HashSet<Constant>();
            constants.add( new Constant<Integer>( "tile", tile1 ) );
            
            conditions.add( new Relation( "TP", true, variables, constants ) );
            
            for( Integer tile2 : tile_values ) {
                if( tile1 != tile2 ) {
                    
                    Set<Variable> v = new HashSet<Variable>();
            
                    v.add( new Variable<Integer>( "position", new HashSet<Integer>(
                            Arrays.asList( position_values) ) ) );
            
                    v.add( new Variable<Integer>( "step", new HashSet<Integer>(
                            Arrays.asList( step_values ) ) ) );
                    
                    Set<Constant> c = new HashSet<Constant>();
                    c.add( new Constant<Integer>( "tile", tile2 ) );
                    outcomes.add( new Relation( "TP", false, v, c ) );
                }
            }
            
            rule1.add( new Rule( conditions, outcomes ) );
        }
        
        return rule1;
    }
    
    // Rule #2:
    private Set<Rule> createRule2()
    {
        Set<Rule> rule2 = new HashSet<Rule>();
        
        
        for( Integer position1 : position_values ) {
            
            Set<Variable> variables = new HashSet<Variable>();
            
            variables.add( new Variable<Integer>( "tile", new HashSet<Integer>(
                    Arrays.asList( position_values) ) ) );
            
            variables.add( new Variable<Integer>( "step", new HashSet<Integer>(
                    Arrays.asList( step_values ) ) ) );
            
            
            Set<Relation> conditions = new HashSet<Relation>();
            Set<Relation> outcomes = new HashSet<Relation>();
            
            Set<Constant> constants = new HashSet<Constant>();
            constants.add( new Constant<Integer>( "position", position1 ) );
            
            conditions.add( new Relation( "TP", true, variables, constants ) );
            
            for( Integer position2 : position_values ) {
                if( position1 != position2 ) {
                    
                    Set<Variable> v = new HashSet<Variable>();
            
                    v.add( new Variable<Integer>( "tile", new HashSet<Integer>(
                            Arrays.asList( position_values) ) ) );
            
                    v.add( new Variable<Integer>( "step", new HashSet<Integer>(
                            Arrays.asList( step_values ) ) ) );
                    
                    Set<Constant> c = new HashSet<Constant>();
                    c.add( new Constant<Integer>( "position", position2 ) );
                    outcomes.add( new Relation( "TP", false, v, c ) );
                }
            }
            
            rule2.add( new Rule( conditions, outcomes ) );
        }
        
        return rule2;     
    }
    
    // Rule #3
    private Set<Rule> createRule3()
    {
        Set<Rule> rule3 = new HashSet<Rule>();
        
        for( Integer step : step_values ) {
        
            for( int i = 1; i <= 4; i++ ) {

                Set<Integer> positions= new HashSet<Integer>();
                switch( i ) {
                    case 1:
                        // pos 1, 2, 3, 4 => ~UP
                        positions.add( 1 ); positions.add( 2 ); positions.add( 3 ); positions.add( 4 );
                        break;
                    case 2:
                        // pos 1, 5, 9, 13 => ~LEFT
                        positions.add( 1 ); positions.add( 5 ); positions.add( 9 ); positions.add( 13 );
                        break;
                    case 3:
                        // pos 4, 8, 12, 16 => ~RIGHT
                        positions.add( 4 ); positions.add( 8 ); positions.add( 12 ); positions.add( 16 );
                        break;
                    case 4:
                        // pos 13, 14, 15, 16 => ~DOWN
                        positions.add( 13 ); positions.add( 14 ); positions.add( 15 ); positions.add( 16 );
                        break;
                    default:
                        System.out.println( "Error creating rule 3, count too high" );
                }
                
                for( Integer position : positions ) {
                    
                    Set<Relation> conditions = new HashSet<Relation>();
                    
                    Set<Constant> constants1 = new HashSet<Constant>();
                    constants1.add( new Constant<Integer>( "tile", tile_values.length ) );
                    constants1.add( new Constant<Integer>( "position", position ) ) ;
                    constants1.add( new Constant<Integer>( "step", step ) );
                    conditions.add( new Relation( "TP", true, constants1 ) );
                    
                    Set<Relation> outcomes = new HashSet<Relation>();
                    
                    Set<Constant> constants2 = new HashSet<Constant>();
                    switch( i ) {
                        case 1:
                            constants2.add( new Constant<Operation>( "operation", Operation.UP ) );
                            break;
                        case 2:
                            constants2.add( new Constant<Operation>( "operation", Operation.LEFT ) );
                            break;
                        case 3:
                            constants2.add( new Constant<Operation>( "operation", Operation.RIGHT ) );
                            break;
                        case 4:
                            constants2.add( new Constant<Operation>( "operation", Operation.DOWN ) );
                            break;
                        default:
                            System.out.println( "Error creating rule 3, count too high" );
                    }
                    constants2.add( new Constant<Integer>( "step", step ) );
                    outcomes.add( new Relation( "EO", false, constants2 ) );
                    
                    rule3.add( new Rule( conditions, outcomes ) );
                }
            }
        
        }
        
        return rule3;
    }
    
    // Rule #4
    private Set<Rule> createRule4()
    {
        Set<Rule> rule4 = new HashSet<Rule>();        
        
        for( Operation operation : Operation.values() ) {
            
            Set<Relation> conditions = new HashSet<Relation>();
            Set<Relation> outcomes = new HashSet<Relation>();
            
            Set<Variable> variables1 = new HashSet<Variable>();
            variables1.add( new Variable<Integer>( "step", new HashSet<Integer>(
                    Arrays.asList( step_values ) ) ) );
            
            Set<Constant> constants1 = new HashSet<Constant>();
            constants1.add( new Constant<Operation>( "operation", operation ) );
            
            conditions.add( new Relation( "EO", true, variables1, constants1 ) );
            
            for( Operation op : Operation.values() ) {
                if( !op.equals( operation ) ) {
                    
                    Set<Variable> variables2 = new HashSet<Variable>();
                    variables2.add( new Variable<Integer>( "step", new HashSet<Integer>(
                            Arrays.asList( step_values ) ) ) );
                    
                    Set<Constant> constants2 = new HashSet<Constant>();
                    constants2.add( new Constant<Operation>( "operation", op ) );
                    
                    outcomes.add( new Relation( "EO", false, variables2, constants2 ) );
                }
            }
            rule4.add( new Rule( conditions, outcomes ) );
        }
        
        return rule4;
    }
    
    // Rule #5
    private Set<Rule> createRule5()
    {
        Set<Rule> rule5 = new HashSet<Rule>();
        
        Set<Relation> conditions1 = new HashSet<Relation>();
        for( Integer i : step_values ) {
            Set<Constant> constants1 = new HashSet<Constant>();
            constants1.add( new Constant<Integer>( "value", i ) );
            
            conditions1.add( new Relation( "helper_var", true, constants1 ) );
        }
        
        rule5.add( new Rule( conditions1 ) );
        
        
        for( Integer step : step_values ) {
            
            for( Integer tile : tile_values ) {
                
                Set<Relation> conditions2 = new HashSet<Relation>();
                
                Set<Constant> constants2a = new HashSet<Constant>();
                constants2a.add( new Constant<Integer>( "tile", tile ) );
                constants2a.add( new Constant<Integer>( "position", tile ) );
                constants2a.add( new Constant<Integer>( "step", step ) );
                
                conditions2.add( new Relation( "TP", true, constants2a ) );
                
                Set<Constant> constants2b = new HashSet<Constant>();
                constants2b.add( new Constant<Integer>( "value", step ) );
                
                conditions2.add( new Relation( "helper_var", false, constants2b ) );
                
                rule5.add( new Rule( conditions2 ) );
            }
        }
        
        return rule5;
    }
    
    // Rule #6.1
    private Set<Rule> createRule6_1()
    {
        Set<Rule> rule6_1 = new HashSet<Rule>();
        
        
        for( Integer step : step_values ) {
            
            for( Operation operation : Operation.values() ) {
            
                for( Integer position : position_values ) {
                
                    for( Integer tile : tile_values ) {
                        
                        if( tile != tile_values.length && step < step_values.length ) {
                            
                            if( operation == Operation.UP && position < 5 ) {
                                continue;
                            } else if( operation == Operation.DOWN && position > 12 ) {
                                continue;
                            } else if( operation == Operation.LEFT && 
                                    ( position == 1 || position == 5 || position == 9 || position == 13 )) {
                                continue;
                            } else if( operation == Operation.RIGHT &&
                                    ( position == 4 || position == 8 || position == 12 || position == 16 )) {
                                continue;
                            }
                            
                            Set<Relation> conditions = new HashSet<Relation>();
                            
                            // 16-tile
                            Set<Constant> constants1 = new HashSet<Constant>();
                            constants1.add( new Constant<Integer>( "tile", tile_values.length ) );
                            constants1.add( new Constant<Integer>( "position", position ) );
                            constants1.add( new Constant<Integer>( "step", step ) );
                            conditions.add( new Relation( "TP", true, constants1 ) );
                            
                            // operation
                            Set<Constant> constants2 = new HashSet<Constant>();
                            constants2.add( new Constant<Operation>( "operation", operation ) );
                            constants2.add( new Constant<Integer>( "step", step) );
                            conditions.add( new Relation( "EO", true, constants2 ) );
                            
                            // second affected tile
                            Set<Constant> constants3 = new HashSet<Constant>();
                            constants3.add( new Constant<Integer>( "tile", tile ) );
                            constants3.add( new Constant<Integer>( "step", step ) );
                            
                            int pos_2 = -1;
                            switch( operation ) {
                                case UP:
                                    constants3.add( new Constant<Integer>( "position", position - 4 ) );
                                    pos_2 = position - 4;
                                    break;
                                case DOWN:
                                    constants3.add( new Constant<Integer>( "position", position + 4 ) );
                                    pos_2 = position + 4;
                                    break;
                                case LEFT:
                                    constants3.add( new Constant<Integer>( "position", position - 1 ) );
                                    pos_2 = position - 1;
                                    break;
                                case RIGHT:
                                    constants3.add( new Constant<Integer>( "position", position + 1 ) );
                                    pos_2 = position + 1;
                                    break;
                                default:
                                    System.out.println( "Operation not supported" );
                                    break;
                            }
                            if( pos_2 == -1 ) {
                                System.out.println( "Error: pos_2 == -1" );
                                System.exit( 0 );
                            }
                            
                            conditions.add( new Relation( "TP", true, constants3 ) );
                            
                            Set<Relation> outcomes1 = new HashSet<Relation>();
                            
                            // modified 16-tile
                            Set<Constant> constants4 = new HashSet<Constant>();
                            constants4.add( new Constant<Integer>( "tile", tile_values.length ) );
                            constants4.add( new Constant<Integer>( "position", pos_2 ) );
                            constants4.add( new Constant<Integer>( "step", step + 1 ) );
                            outcomes1.add( new Relation( "TP", true, constants4 ) );
                            
                            rule6_1.add( new Rule( conditions, outcomes1 ) ); 
                            
                            // careful: usually would have to create new Constant objects
                            Set<Relation> outcomes2 = new HashSet<Relation>();
                            
                            // modified second tile
                            Set<Constant> constants5 = new HashSet<Constant>();
                            constants5.add( new Constant<Integer>( "tile", tile ) );
                            constants5.add( new Constant<Integer>( "position", position ) );
                            constants5.add( new Constant<Integer>( "step", step + 1 ) );
                            outcomes2.add( new Relation( "TP", true, constants5 ) );
                            
                            rule6_1.add( new Rule( conditions, outcomes2 ) );
                            
                        }
                    }
                }
            }
        }

        return rule6_1;
    }
    
    // Rule #6.2
    private Set<Rule> createRule6_2()
    {
        Set<Rule> rule6_2 = new HashSet<Rule>();
        
        
        for( Integer step : step_values ) {
            
            for( Operation operation : Operation.values() ) {
            
                for( Integer position : position_values ) { // position of tile #16
                
                    for( Integer tile : tile_values ) {
                        
                        if( tile != tile_values.length && step < step_values.length ) {
                            
                            if( operation == Operation.UP && position < 5 ) {
                                continue;
                            } else if( operation == Operation.DOWN && position > 12 ) {
                                continue;
                            } else if( operation == Operation.LEFT && 
                                    (position == 1 || position == 5 || position == 9 || position == 13 )) {
                                continue;
                            } else if( operation == Operation.RIGHT &&
                                    (position == 4 || position == 8 || position == 12 || position == 16 )) {
                                continue;
                            }
                            
                            Set<Integer> remaining_positions = new HashSet<Integer>(
                                    Arrays.asList( position_values ) );
                            remaining_positions.remove( position );
                            
                            int pos_2 = -1;
                            switch( operation ) {
                                case UP:
                                    pos_2 = position - 4;
                                    break;
                                case DOWN:
                                    pos_2 = position + 4;
                                    break;
                                case LEFT:
                                    pos_2 = position - 1;
                                    break;
                                case RIGHT:
                                    pos_2 = position + 1;
                                    break;
                                default:
                                    System.out.println( "Operation not supported" );
                                    break;
                            }
                            if( pos_2 == -1 ) {
                                System.out.println( "Error: pos_2 == -1" );
                                System.exit( 0 );
                            }
                            remaining_positions.remove( pos_2 ); // is this correct? also: what happens to ~TP(x,y,z)?
                            
                            for(int i = 0; i < 2; i++ ) {
                            
                                for(Integer remaining_position : remaining_positions ) {

                                    Set<Relation> conditions = new HashSet<Relation>();

                                    // 16-tile
                                    Set<Constant> constants1 = new HashSet<Constant>();
                                    constants1.add( new Constant<Integer>( "tile", tile_values.length ) );
                                    constants1.add( new Constant<Integer>( "position", position ) );
                                    constants1.add( new Constant<Integer>( "step", step ) );
                                    conditions.add( new Relation( "TP", true, constants1 ) );

                                    // operation
                                    Set<Constant> constants2 = new HashSet<Constant>();
                                    constants2.add( new Constant<Operation>( "operation", operation ) );
                                    constants2.add( new Constant<Integer>( "step", step) );
                                    conditions.add( new Relation( "EO", true, constants2 ) );

                                    // not affected tile at step s
                                    Set<Constant> constants3 = new HashSet<Constant>();
                                    constants3.add( new Constant<Integer>( "tile", tile ) );
                                    constants3.add( new Constant<Integer>( "position", remaining_position ) );
                                    constants3.add( new Constant<Integer>( "step", step ) );
                                    conditions.add( new Relation( "TP", i == 0, constants3 ) );

                                    Set<Relation> outcomes = new HashSet<Relation>();

                                    // not affected tile at step s+1
                                    Set<Constant> constants4 = new HashSet<Constant>();
                                    constants4.add( new Constant<Integer>( "tile", tile ) );
                                    constants4.add( new Constant<Integer>( "position", remaining_position ) );
                                    constants4.add( new Constant<Integer>( "step", step + 1 ) );
                                    outcomes.add( new Relation( "TP", i == 0, constants4 ) );

                                    rule6_2.add( new Rule( conditions, outcomes ) );

                                }
                            }
                        }
                    }
                }
            }
        }
        
        
        return rule6_2;
    }
    
    // Rule #7
    private Set<Rule> createRule7()
    {
        Set<Rule> rule7 = new HashSet<Rule>();
        
        for( Integer step : step_values ) {
            
            if( step == step_values.length ) {
                continue;
            }
            
            Set<Relation> conditions = new HashSet<Relation>();
            for( Operation operation : Operation.values() ) {
                Set<Constant> constants = new HashSet<Constant>();
                constants.add( new Constant<Integer>( "step", step ) );
                constants.add( new Constant<Operation>( "operation", operation ) );
                conditions.add( new Relation( "EO", false, constants ) );
            }
            
            for( Operation operation : Operation.values() ) {
                Set<Relation> outcomes = new HashSet<Relation>();
                
                Set<Constant> constants = new HashSet<Constant>();
                constants.add( new Constant<Integer>( "step", step + 1 ) );
                constants.add( new Constant<Operation>( "operation", operation ) );
                outcomes.add( new Relation( "EO", false, constants ) );
                
                rule7.add( new Rule( conditions, outcomes ) );
            }
        }
        
        return rule7;
    }
    
    // Rule #8
    private Set<Rule> createRule8()
    {
        Set<Rule> rule8 = new HashSet<Rule>();
        
        for( Integer step : step_values ) {
            
            if( step == step_values.length ) {
                continue;
            }
            
            for( Integer tile : tile_values ) {
                
                for( Integer position : position_values ) {
            
                    Set<Relation> conditions = new HashSet<Relation>();
                    for( Operation operation : Operation.values() ) {
                        Set<Constant> constants = new HashSet<Constant>();
                        constants.add( new Constant<Operation>( "operation", operation ) );
                        constants.add( new Constant<Integer>( "step", step ) );

                        conditions.add( new Relation( "EO", false, constants ) );
                    }

                    Set<Relation> outcomes = new HashSet<Relation>();
                    
                    Set<Constant> constants = new HashSet<Constant>();
                    constants.add( new Constant<Integer>( "tile", tile) );
                    constants.add( new Constant<Integer>( "position", position ) );
                    constants.add( new Constant<Integer>( "step", step + 1 ) );
                    
                    outcomes.add( new Relation( "TP", false, constants ) );
                    
                    rule8.add( new Rule( conditions, outcomes ) );
                }
            }
        }
        
        return rule8;
    }
    
    
    private Set<Rule> encodeInitialState()
    {
        Set<Rule> initial_state = new HashSet<Rule>();
        
        int position = 1;
        for( int[] row : state ) {
            for( int element : row ) {
                Set<Relation> conditions1 = new HashSet<Relation>();
                
                Set<Constant> constants1 = new HashSet<Constant>();
                constants1.add( new Constant<Integer>( "tile", element ) );
                constants1.add( new Constant<Integer>( "position", position ) );
                constants1.add( new Constant<Integer>( "step", 1 ) );
                conditions1.add( new Relation( "TP", true, constants1 ) );
                
                initial_state.add( new Rule( conditions1 ) );
                
                
                for( Integer tile : tile_values ) {
                    if( tile != element ) {
                        Set<Relation> conditions2 = new HashSet<Relation>();
                        
                        Set<Constant> constants2 = new HashSet<Constant>();
                        constants2.add( new Constant<Integer>( "tile", tile ) );
                        constants2.add( new Constant<Integer>( "position", position ) );
                        constants2.add( new Constant<Integer>( "step", 1 ) );
                        conditions2.add( new Relation( "TP", false, constants2 ) );
                        
                        initial_state.add( new Rule( conditions2 ) );
                    }
                }
                
                position++;
            }
        }
        
        return initial_state;
    }
            
    
    
    public static void main( String[] args )
    {
        Fifteenpuzzle puzzle = new Fifteenpuzzle();
        
        long time = System.currentTimeMillis();
        
        Set<Rule> grounded_instances = new HashSet<Rule>();
        
        // Rule #1
        Set<Rule> rule1 = puzzle.createRule1();                
        for( Rule rule : rule1 ) {
            grounded_instances.addAll( rule.getGroundedInstances() );
        }
        
        // Rule #2
        Set<Rule> rule2 = puzzle.createRule2();
        for( Rule rule : rule2 ) {
            grounded_instances.addAll( rule.getGroundedInstances() );
        }
        
        // Rule #3
        grounded_instances.addAll( puzzle.createRule3() );
        
        // Rule #4
        Set<Rule> rule4 = puzzle.createRule4();
        for( Rule rule : rule4 ) {
            grounded_instances.addAll( rule.getGroundedInstances() );
        }
        
        // Rule #5
        grounded_instances.addAll( puzzle.createRule5() ); // are already grounded
        
        // Rule #6
        grounded_instances.addAll( puzzle.createRule6_1() );
        grounded_instances.addAll( puzzle.createRule6_2() );
        
        // Rule #7
        grounded_instances.addAll( puzzle.createRule7() );
        
        // Rule #8
        grounded_instances.addAll( puzzle.createRule8() );
        
        
        // task decomposition
        //grounded_instances.addAll( puzzle.createDistanceVariables() );
        //grounded_instances.addAll( puzzle.distancesOption2() );
        //grounded_instances.addAll( puzzle.disambiguateDistancesRule() );
        //grounded_instances.addAll( puzzle.createRule9() );
        
        
        // Initial State
        // grounded_instances.addAll( puzzle.encodeInitialState() );
        // write state to end of file instead, for each iteration
        
        
                
        Rule.writeGroundedInstancesToFile( grounded_instances, args[0] + ".enc");
        
        System.out.println( "Time needed for rule 1 - 8: " + ( System.currentTimeMillis() - time ) );
        System.out.println( "Variables in dictionary: " + VariableDictionary.getInstance().getVariableCount() );
        
        VariableDictionary.getInstance().writeToFile( args[0] + ".dict" );
    }
}
