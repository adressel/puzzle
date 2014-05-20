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
        
        this.step_values = new Integer[11];
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
    
    private Relation createConstantTPRelation( int tile, int position, int step, boolean sign ) 
    {
        Set<Constant> constants = new HashSet<Constant>();
        constants.add( new Constant<Integer>( "tile", tile ) );
        constants.add( new Constant<Integer>( "position", position ) );
        constants.add( new Constant<Integer>( "step", step ) );
        
        return new Relation( "TP", sign, constants );
    }
    
    private Relation createConstantEORelation( Operation operation, int step, boolean sign ) 
    {
        Set<Constant> constants = new HashSet<Constant>();
        constants.add( new Constant<Operation>( "operation", operation ) );
        constants.add( new Constant<Integer>( "step", step ) );
        
        return new Relation( "EO", sign, constants );
    }
    
    private Relation createConstantDISTRelation( int tile, int distance, int step, boolean sign )
    {
        Set<Constant> constants = new HashSet<Constant>();
        constants.add( new Constant<Integer>( "tile", tile ) );
        constants.add( new Constant<Integer>( "distance", distance ) );
        constants.add( new Constant<Integer>( "step", step ) );
        
        return new Relation( "DIST", sign, constants );
    }
    
    private Relation createHelperVarRelation( int index, boolean sign )
    {
        Set<Constant> constants = new HashSet<Constant>();
        constants.add( new Constant<Integer>( "index", index ) );
        
        return new Relation( "helper", sign, constants );
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
                    conditions.add( createConstantTPRelation( tile_values.length, position, step, true) );
                    
                    Set<Relation> outcomes = new HashSet<Relation>();
                    
                    Set<Constant> constants2 = new HashSet<Constant>();
                    switch( i ) {
                        case 1:
                            outcomes.add( createConstantEORelation( Operation.UP, step, false ) ); //constants2.add( new Constant<Operation>( "operation", Operation.UP ) );
                            break;
                        case 2:
                            outcomes.add( createConstantEORelation( Operation.LEFT, step, false ) );
                            break;
                        case 3:
                            outcomes.add( createConstantEORelation( Operation.RIGHT, step, false ) );
                            break;
                        case 4:
                            outcomes.add( createConstantEORelation( Operation.DOWN, step, false ) );
                            break;
                        default:
                            System.out.println( "Error creating rule 3, count too high" );
                    }
                    
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
                conditions2.add( createConstantTPRelation( tile, tile, step, true ) );
                
                Set<Constant> constants2 = new HashSet<Constant>();
                constants2.add( new Constant<Integer>( "value", step ) );
                conditions2.add( new Relation( "helper_var", false, constants2 ) );
                
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
                            conditions.add( createConstantTPRelation( tile_values.length, position, step, true ) );
                            
                            // operation
                            conditions.add( createConstantEORelation( operation, step, true ) );
                            
                            // second affected tile
                            int pos_2 = -1;
                            switch( operation ) {
                                case UP:
                                    conditions.add( createConstantTPRelation( tile, position - 4, step, true ) );
                                    pos_2 = position - 4;
                                    break;
                                case DOWN:
                                    conditions.add( createConstantTPRelation( tile, position + 4, step, true ) );
                                    pos_2 = position + 4;
                                    break;
                                case LEFT:
                                    conditions.add( createConstantTPRelation( tile, position - 1, step, true ) );
                                    pos_2 = position - 1;
                                    break;
                                case RIGHT:
                                    conditions.add( createConstantTPRelation( tile, position + 1, step, true ) );
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
                                                        
                            Set<Relation> outcomes1 = new HashSet<Relation>();
                            
                            // modified 16-tile
                            outcomes1.add( createConstantTPRelation( tile_values.length, pos_2, step + 1, true ) );
                            rule6_1.add( new Rule( conditions, outcomes1 ) ); 
                            
                            Set<Relation> outcomes2 = new HashSet<Relation>();
                            
                            // modified second tile
                            outcomes2.add( createConstantTPRelation( tile, position, step + 1, true ) );
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
                                    conditions.add( createConstantTPRelation( tile_values.length, position, step, true ) );

                                    // operation
                                    conditions.add( createConstantEORelation( operation, step, true ) );

                                    // tile that is not affected at step s
                                    conditions.add( createConstantTPRelation( tile, remaining_position, step, i == 0 ) );

                                    Set<Relation> outcomes = new HashSet<Relation>();

                                    // not affected tile at step s+1
                                    outcomes.add( createConstantTPRelation( tile, remaining_position, step + 1, i == 0 ) );
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
                
                conditions.add( createConstantEORelation( operation, step, false ) );
            }
            
            for( Operation operation : Operation.values() ) {
                Set<Relation> outcomes = new HashSet<Relation>();
                outcomes.add( createConstantEORelation( operation, step + 1, false ) );
                
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
                        
                        conditions.add( createConstantEORelation( operation, step, false ) );
                    }

                    Set<Relation> outcomes = new HashSet<Relation>();
                    outcomes.add( createConstantTPRelation( tile, position, step + 1, false ) );
                    
                    rule8.add( new Rule( conditions, outcomes ) );
                }
            }
        }
        
        return rule8;
    }
    
    
    private int getMaxDistanceForTile( int tile )
    {
        int max_distance;
        if( tile == 1 || tile == 4 || tile == 13 || tile == 16 ) {
            max_distance = 6;
        } else if( tile == 2 || tile == 3 || tile == 5 || tile == 8 || tile == 9 || tile == 12 
                || tile == 14 || tile == 15 ) {
            max_distance = 5;
        } else {
            max_distance = 4;
        }
        return max_distance;
    }
    
    
    // Rule #9
    // prevents that the distance for a tile gets set to some low number in the
    // case that no operations were executed in the previous step. Instead, the
    // distance for every tile gets set to its maximum (-> no possible solution)
    private Set<Rule> createRule9()
    {
        Set<Rule> rule9 = new HashSet<Rule>();
        
        for( Integer step : step_values ) {
            
            if( step == step_values.length ) {
                continue;
            }
            
            for( Integer tile : tile_values ) {

                for( int distance = 0; distance <= getMaxDistanceForTile( tile ); distance++ ) {
                    
                    Set<Relation> conditions = new HashSet<Relation>();
                    for( Operation operation : Operation.values() ) {
                        conditions.add( createConstantEORelation( operation, step, false ) );
                    }
                    
                    Set<Relation> outcomes = new HashSet<Relation>();
                    outcomes.add( createConstantDISTRelation( tile, distance, step + 1, distance == getMaxDistanceForTile( tile ) ) );
                    rule9.add( new Rule( conditions, outcomes ) );
                }
            }
        }
        
        return rule9;
    }
            
    
    // Task decomposition
    
    private Set<Rule> createDistanceVariables()
    {
        Set<Rule> rule9 = new HashSet<Rule>();
        
        for( Integer step : step_values ) {
            
            
            for( int tile : this.tile_values ) {
                for( int position : this.position_values ) {
                    
                    Set<Relation> conditions = new HashSet<Relation>();
                    conditions.add( createConstantTPRelation( tile, position, step, true ) );
                    
                    Set<Relation> outcomes = new HashSet<Relation>();
                    
                    // Manhattan distance:
                    int distance;
                    if( tile < 5 ) {
                        if( position < 5 ) {
                            distance = 0;
                        } else if( position < 9 && position > 4) {
                            distance = 1;
                        } else if( position < 13 && position > 8) {
                            distance = 2;
                        } else {
                            distance = 3;
                        }
                        
                    } else if( tile > 4 && tile < 9 ) {
                        if( position < 5 ) {
                            distance = 1;
                        } else if( position < 9 && position > 4 ) {
                            distance = 0;
                        } else if( position < 13 && position > 8 ) {
                            distance = 1;
                        } else {
                            distance = 2;
                        }
                        
                    } else if( tile > 8 && tile < 13 ) {
                        if( position < 5 ) {
                            distance = 2;
                        } else if( position < 9 && position > 4 ) {
                            distance = 1;
                        } else if( position < 13 && position > 8 ) {
                            distance = 0;
                        } else {
                            distance = 1;
                        }
                        
                    } else {
                        if( position < 5 ) {
                            distance = 3;
                        } else if( position < 9 && position > 4 ) {
                            distance = 2;
                        } else if( position < 13 && position > 8 ) {
                            distance = 1;
                        } else {
                            distance = 0;
                        }
                        
                    }
                    
                    if( tile%4 == 1 ) {
                        if( position%4 == 1 ) {
                            distance += 0;
                        } else if( position%4 == 2 ) {
                            distance += 1;
                        } else if( position%4 == 3 ) {
                            distance += 2;
                        } else {
                            distance += 3;
                        }
                        
                    } else if( tile%4 == 2 ) {
                        if( position%4 == 1 ) {
                            distance += 1;
                        } else if( position%4 == 2 ) {
                            distance += 0;
                        } else if( position%4 == 3 ) {
                            distance += 1;
                        } else {
                            distance += 2;
                        }
                        
                    } else if( tile%4 == 3 ) {
                        if( position%4 == 1 ) {
                            distance += 2;
                        } else if( position%4 == 2 ) {
                            distance += 1;
                        } else if( position%4 == 3 ) {
                            distance += 0;
                        } else {
                            distance += 1;
                        }
                        
                    } else {
                        if( position%4 == 1 ) {
                            distance += 3;
                        } else if( position%4 == 2 ) {
                            distance += 2;
                        } else if( position%4 == 3 ) {
                            distance += 1;
                        } else {
                            distance += 0;
                        }
                        
                    }
                    
                    outcomes.add( createConstantDISTRelation( tile, distance, step, true ) );
                    rule9.add( new Rule( conditions, outcomes ) );
                }
            }
        }
        
        return rule9;
    }
    
    // DIST( tile, distance_1, step ) => ~DIST( tile, distance_2, step )
    // for all distance_1 != distance_2
    private Set<Rule> disambiguateDistancesRule()
    {
        Set<Rule> rules = new HashSet<Rule>();
        
        for( Integer step : step_values ) {
            
                    
            for( Integer tile : tile_values ) {
                
                int max_distance;
                if( tile == 1 || tile == 4 || tile == 13 || tile == 16 ) {
                    max_distance = 6;
                } else if( tile == 2 || tile == 3 || tile == 5 || tile == 8 || tile == 9 || tile == 12 
                        || tile == 14 || tile == 15 ) {
                    max_distance = 5;
                } else {
                    max_distance = 4;
                }
                
                for( int distance = 0; distance <= max_distance; distance++ ) {
                    Set<Relation> conditions = new HashSet<Relation>();
                    conditions.add( createConstantDISTRelation( tile, distance, step, true ) );
                    
                    for( int dist = 0; dist <= max_distance; dist++ ) {
                        
                        if( distance != dist ) {
                            
                            Set<Relation> outcomes = new HashSet<Relation>();
                            outcomes.add( createConstantDISTRelation( tile, dist, step, false ) );
                            
                            rules.add( new Rule( conditions, outcomes ) );
                            
                        }
                    }
                }
            } 
        }
        
        return rules;
    }
    
    
    private Set<Rule> disambiguateHelperVariables()
    {
        Set<Rule> rules = new HashSet<Rule>();
        
        for( Integer step : step_values ) {
            
            Set<Relation> conditions = new HashSet<Relation>();
            
            Set<Constant> constants1 = new HashSet<Constant>();
            constants1.add( new Constant<Integer>( "index", step ) );
            conditions.add( new Relation( "helper", true, constants1 ) );
            
            for( Integer s : step_values ) {
                if( s != step ) {
                    Set<Relation> outcomes = new HashSet<Relation>();

                    Set<Constant> constants2 = new HashSet<Constant>();
                    constants2.add( new Constant<Integer>( "helper", s ) );
                    outcomes.add( new Relation( "helper", false, constants2 ) );
                    rules.add( new Rule( conditions, outcomes ) );
                }
            }
        }
        
        
        return rules;
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
        // grounded_instances.addAll( puzzle.createRule5() ); // are already grounded
        
        // Rule #6
        grounded_instances.addAll( puzzle.createRule6_1() );
        grounded_instances.addAll( puzzle.createRule6_2() );
        
        // Rule #7
        grounded_instances.addAll( puzzle.createRule7() );
        
        // Rule #8
        grounded_instances.addAll( puzzle.createRule8() );
        
        
        // task decomposition
        grounded_instances.addAll( puzzle.createDistanceVariables() );
        grounded_instances.addAll( puzzle.disambiguateDistancesRule() );
        grounded_instances.addAll( puzzle.createRule9() );
        
        
        grounded_instances.addAll( puzzle.disambiguateHelperVariables() );
                
        Rule.writeGroundedInstancesToFile( grounded_instances, args[0] + ".enc");
        
        System.out.println( "Time needed for rule 1 - 8: " + ( System.currentTimeMillis() - time ) );
        System.out.println( "Variables in dictionary: " + VariableDictionary.getInstance().getVariableCount() );
        
        VariableDictionary.getInstance().writeToFile( args[0] + ".dict" );
    }
}



/* BACKUP


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
    
    
    private Set<Rule> distancesObjective()
    {
        Set<Rule> rules = new HashSet<Rule>();
        
        int helper_vars_counter = 1;
        
        for( Integer tile : tile_values ) {
            int max_distance = getMaxDistanceForTile( tile );
            
            
            // Solve puzzle step by step:
            // first tiles 1, 2, 3, 4; then 5, 9, 13; then 6, 7, 8; then 10, 11, 12, 14, 15, 16
            
            // get solved positions
            Set<Integer> first = new HashSet<Integer>();
            first.add( 1 ); first.add( 2 ); first.add( 3 ); first.add( 4 );
            
            Set<Integer> second = new HashSet<Integer>();
            second.add( 5 ); second.add( 9 ); second.add( 13 );
            
            Set<Integer> third = new HashSet<Integer>();
            third.add( 6 ); third.add( 7 ); third.add( 8 );
                
            Set<Integer> fourth = new HashSet<Integer>();
            fourth.add( 10 ); fourth.add( 11 ); fourth.add( 12 ); fourth.add( 14 ); fourth.add( 15 ); fourth.add( 16 );
                
            Set<Integer> solved_positions = new HashSet<Integer>();
            for( Integer tile2 : first ) {
                if( tile2 < tile || !first.contains( tile ) ) {
                            
                    solved_positions.add( tile2 );
                }
            }
                
            if( !first.contains( tile ) ) {
                for( Integer tile2 : second ) {
                    if( tile2 < tile || !second.contains( tile ) ) {
                            
                        solved_positions.add( tile2 );
                    }
                }
            }
                
            if( !second.contains( tile ) && !first.contains( tile ) ) {
                for( Integer tile2 : third ) {
                    if( tile2 < tile || !third.contains( tile ) ) {
                            
                        solved_positions.add( tile2 );
                    }
                }
            }
                
                
            // -----------------------------------------------------------------------------------
            // These tiles (in set 'fourth') must in fact be optimized in one step -> change implementation
            if( !third.contains( tile ) && !second.contains( tile ) && !first.contains( tile ) ) {
                for( Integer tile2 : fourth ) {
                    if( tile2 < tile || !fourth.contains( tile ) ) {
                            
                        solved_positions.add( tile2 );
                    }
                }
            }
            // -----------------------------------------------------------------------------------
        
            
            // helper variables
            Set<Relation> conditions_helper_vars = new HashSet<Relation>();

            for( int distance = 1; distance <= max_distance; distance++ ) {
                
                Set<Relation> conditions_distances1 = new HashSet<Relation>();
                conditions_distances1.add( createConstantDISTRelation( tile, distance, 1, false ) ); // tile, distance, step:1
                conditions_distances1.add( createHelperVarRelation( helper_vars_counter, false ) );
                rules.add( new Rule( conditions_distances1 ) );
                
                conditions_helper_vars.add( createHelperVarRelation( helper_vars_counter, true ) );
                helper_vars_counter++;
                
                
                // solved positions:
                for( Integer solved_position : solved_positions ) {
                    
                    Set<Relation> conditions_distances2 = new HashSet<Relation>();
                    conditions_distances2.add( createConstantDISTRelation( solved_position, 0, 1, false ) );
                    conditions_distances2.add( createHelperVarRelation( helper_vars_counter, false ) );
                    rules.add( new Rule( conditions_distances2 ) );
                    
                    conditions_helper_vars.add( createHelperVarRelation( helper_vars_counter, true ) );
                    helper_vars_counter++;
                    
                    
                }
                
                
                
                for( int step : step_values ) {
                    
                    if( step == 1 ) {
                        continue;
                    }
                    
                    // solved positions:
                    for( Integer solved_position : solved_positions ) {
            
                        // get all possible positions for solved_position that are greater than 0...
                        int max = getMaxDistanceForTile( solved_position );
                            
                        for( int d = 1; d <= max; d++ ) {
                                
                            Set<Relation> conditions_distances2 = new HashSet<Relation>();
                            conditions_distances2.add( createConstantDISTRelation( solved_position, d, step, false ) );
                            conditions_distances2.add( createHelperVarRelation( helper_vars_counter, false ) );
                            rules.add( new Rule( conditions_distances2 ) );
                                                           
                        }
                    }
                    
                    for( int dist = distance; dist <= max_distance; dist++ ) {
                        
                        // improve by min 1:
                        Set<Relation> conditions_distances2 = new HashSet<Relation>();
                        conditions_distances2.add( createConstantDISTRelation( tile, dist, step, false ) );
                        conditions_distances2.add( createHelperVarRelation( helper_vars_counter, false ) );
                        rules.add( new Rule( conditions_distances2 ) );
                        
                    }
                    conditions_helper_vars.add( createHelperVarRelation( helper_vars_counter, true ) );
                    helper_vars_counter++;
                    
                }
            }
            rules.add( new Rule( conditions_helper_vars ) );
        }
        
        
        return rules;
    }
    
    
    
    private Set<Rule> distancesOption2()
    {
        Set<Rule> rules = new HashSet<Rule>();
        
        
        // Don't shift tiles that are in their correct place.
        for( int tile : this.tile_values ) {
            int max_distance;
            if( tile == 1 || tile == 4 || tile == 13 || tile == 16 ) {
                max_distance = 6;
            } else if( tile == 2 || tile == 3 || tile == 5 || tile == 8 || tile == 9 || tile == 12 
                    || tile == 14 || tile == 15 ) {
                max_distance = 5;
            } else {
                max_distance = 4;
            }
            
            
            for( int distance = 1; distance <= max_distance; distance++ ) {
                
                // Solve puzzle step by step:
                // first tiles 1, 2, 3, 4; then 5, 9, 13; then 6, 7, 8; then 10, 11, 12, 14, 15, 16
                Set<Integer> first = new HashSet<Integer>();
                first.add( 1 ); first.add( 2 ); first.add( 3 ); first.add( 4 );
                
                Set<Integer> second = new HashSet<Integer>();
                second.add( 5 ); second.add( 9 ); second.add( 13 );
                
                Set<Integer> third = new HashSet<Integer>();
                third.add( 6 ); third.add( 7 ); third.add( 8 );
                
                Set<Integer> fourth = new HashSet<Integer>();
                fourth.add( 10 ); fourth.add( 11 ); fourth.add( 12 ); fourth.add( 14 ); fourth.add( 15 ); fourth.add( 16 );
                
                // DIST( tile:1, distance:dist!=0, step:0 ) => ~DIST( tile:1, distance:dist-x - max_dist, step:n )
                // DIST( tile:1, distance:0, step:0 ) AND DIST( tile:2, distance:dist!=0, step:0 )
                //      => ~DIST( tile:2, distance:dist-x - max_dist, step:n )
        
                Set<Relation> conditions = new HashSet<Relation>();
                conditions.add( createConstantDISTRelation( tile, distance, 1, true ) );
        
                Set<Integer> relaxed_positions = new HashSet<Integer>();
                for(Integer tile2 : first ) {
                    if( tile2 < tile || !first.contains( tile ) ) {
                            
                        relaxed_positions.add( tile2 );
                    }
                }
                
                
                if( !first.contains( tile ) ) {
                    for(Integer tile2 : second ) {
                        if( tile2 < tile || !second.contains( tile ) ) {
                            
                            relaxed_positions.add( tile2 );
                        }
                    }
                }
                
                if( !second.contains( tile ) && !first.contains( tile ) ) {
                    for(Integer tile2 : third ) {
                        if( tile2 < tile || !third.contains( tile ) ) {
                            
                            relaxed_positions.add( tile2 );
                        }
                    }
                }
                
                
                // -----------------------------------------------------------------------------------
                // These tiles (in set 'fourth') must in fact be optimized in one step -> change implementation
                if( !third.contains( tile ) && !second.contains( tile ) && !first.contains( tile ) ) {
                    for(Integer tile2 : fourth ) {
                        if( tile2 < tile || !fourth.contains( tile ) ) {
                            
                            relaxed_positions.add( tile2 );
                        }
                    }
                }
                // -----------------------------------------------------------------------------------

                
                
                // all relaxed tiles have distance zero at step 0 -> conditions
                for( Integer relaxed_position : relaxed_positions ) {
                    
                    conditions.add( createConstantDISTRelation( relaxed_position, 0, 1, true ) );
                }
                
                // distance must be zero for all relaxed positions after n steps -> outcome -> rule
                for( Integer relaxed_position : relaxed_positions ) {
                    
                    Set<Relation> outcomes = new HashSet<Relation>();
                    outcomes.add( createConstantDISTRelation( relaxed_position, 0, step_values.length, true ) );

                    rules.add( new Rule( conditions, outcomes ) );
                }
                
                
                // by how much must the distance be reduced in n steps? -> x
                int x = 0; // dependent on the current state...
                
                // all distances greater than distance - x are not allowed after n steps -> outcome -> rule
                for( int dist = distance - x; dist <= max_distance; dist++ ) {
                    
                    Set<Relation> outcomes = new HashSet<Relation>();
                    outcomes.add( createConstantDISTRelation( tile, dist, step_values.length, false ) );
                    
                    rules.add( new Rule( conditions, outcomes ) );
                }
            }
        }
        
        return rules;
    }


// ensure that at least one operation is executed
    private Set<Rule> createRule10()
    {
        Set<Rule> rule10 = new HashSet<Rule>();
        
        Set<Relation> conditions = new HashSet<Relation>();
        
        for( Integer position : position_values ) {
            
            conditions.add( createConstantTPRelation( tile_values.length, position, 2, true ) );            
        }
        
        rule10.add( new Rule( conditions ) );
        
        return rule10;
    }
    


*/
