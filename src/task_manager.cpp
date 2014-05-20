// Andreas Dressel

#include <cstdlib>
#include <cstdio>
#include <iostream>
#include <fstream>
#include <cstring>
#include <string>
#include <queue>
#include <map>
#include <set>

#include <time.h>

#include <sys/types.h>
#include <sys/wait.h>

#include <boost/lexical_cast.hpp>
#include <boost/algorithm/string/split.hpp>
#include <boost/algorithm/string/classification.hpp>


using namespace std;

// forward declaration of methods
void find_next_tile( int &next_tile, int &distance, std::set<string> &previous_tiles, std::set<string> state, std::map<int, string> dictionary );
void calculate_distance( std::set<string> state, int tile, std::map<int, string> dictionary, int &distance );
void encode_initial_state( int _11, int _12, int _13, int _14, int _21, int _22, int _23, int _24, int _31, int _32, int _33, int _34, int _41, int _42, int _43, int _44,
                std::map<string, int> &dictionary_si, std::map<int, string> &dictionary_is, string cnf_file_name, int &next_tile, int &distance, std::set<string> &previous_tiles ,int &number_of_clauses, std::set<string> objective );



void populate_dictionary( string file_name, std::map<string, int> &variable_dictionary_string_int, std::map<int, string> &variable_dictionary_int_string )
{
    ifstream in( file_name.c_str() );
    
    if( !in.is_open() ) 
    {
        cout << "cannot open file containing dictionary" << endl;
        exit( 0 );
    }
    
    string line;
    
    while( in.good() )
    {
        getline( in, line );
        int len = line.find( " : " );
        
        if( len != string::npos ) {
            string s = line.substr( 0, len );
            int i = atoi( line.substr( len+3, line.size()-len ).c_str() );
            
            variable_dictionary_string_int.insert( std::pair<string, int>( s, i ) );
            variable_dictionary_int_string.insert( std::pair<int, string>( i, s ) );
            
        }
    }
    
    in.close();
}


void collect_operations_from_solution( std::set<string> &all_operations, std::set<string> &ops, int &number_of_operations )
{
    std::set<string>::iterator it;
    for( it = ops.begin(); it != ops.end(); ++it )
    {
        int pos1 = (*it).find( "step:" );
        int pos2 = (*it).find( " )" );
        
        int step = atoi( (*it).substr( pos1+5, pos2-pos1-5 ).c_str() );
        string s = *it;
        all_operations.insert( s.replace( pos1+5, pos2-pos1-5, boost::lexical_cast<string>( step+number_of_operations ).c_str() ) );
    }
    number_of_operations += ops.size();
}


void modify_cnf_file( string file_name, std::set<string> state, std::set<string> objective, int &number_of_clauses )
{
    ifstream in( file_name.c_str() );
    
    std::vector<string> content;
    
    if( !in.is_open() )
    {
        cout << "cannot open cnf file" << endl;
        exit( 0 );
    }
    
    string line;
    
    int num_clauses_local = 0;
    while( in.good() && num_clauses_local == 0 )
    {
        getline( in, line );
        if( line.find( "p cnf " ) != string::npos )
        {
            // get number of clauses during first iteration, then overwrite number 
            // of clauses in cnf file.
            int pos = line.find_last_of( ' ' );
            if( number_of_clauses == 0 )
            {
                number_of_clauses = atoi( line.substr( pos+1, line.size()-pos-1).c_str() );
            }
            line.replace( pos+1, line.size()-pos-1, boost::lexical_cast<string>( number_of_clauses+state.size()+objective.size() ) );
            
            num_clauses_local = number_of_clauses;
        }
        
        content.push_back( line );
    }
    
    while( in.good() && ( num_clauses_local ) > 0 ) 
    {
        getline( in, line );
        content.push_back( line );
        num_clauses_local -= 1;        
    }
    in.close();
    
    ofstream out( file_name.c_str() );
    
    std::vector<string>::iterator it_vec;
    for( it_vec = content.begin(); it_vec != content.end(); ++it_vec )
    {
        out << *it_vec << "\n";
    }
    
    std::set<string>::iterator it_state;
    for( it_state = state.begin(); it_state != state.end(); ++it_state )
    {
        out << *it_state << " 0\n";
    }
    
    std::set<string>::iterator it_objective;
    for( it_objective = objective.begin(); it_objective != objective.end(); ++it_objective )
    {
        out << *it_objective << " 0\n";
    }
    
    out.close();
}


void calculate_new_state( std::set<string> outcome_state_as_string, std::set<string> &input_state_as_int, std::map<string, int> dictionary )
{    
    std::set<string>::iterator it;
    for( it = outcome_state_as_string.begin(); it != outcome_state_as_string.end(); ++it )
    {
        string tile_position = *it;
        int pos1 = tile_position.find( "step:" );
        int pos2 = tile_position.find( " tile:" );
        //if( tile_position.substr( pos+5, 2 ).compare( "11" ) == 0 )
        //{
            tile_position.replace( pos1+5, pos2-pos1-5 , "1");
            input_state_as_int.insert( boost::lexical_cast<string>( dictionary[tile_position] ) );
        //}
    }
}


void decode_solution( string file_name, std::set<string> &outcome_state, std::set<string> &operations, std::map<int, string> &dictionary )
{
    ifstream in( file_name.c_str() );
    
    if( !in.is_open() )
    {
        cout << "cannot open solution file" << endl;
        exit( 0 );
    }
    
    string line;

    // siege.results should only contain a single line of output    
    if( in.good() )
    {
        if( file_name.find( "siege" ) != std::string::npos ) 
        {
            getline( in, line );
            line = line.substr( line.find_first_of( "{" ), line.find_first_of( "}" )-line.find_first_of( "{" ) );

        } else if( file_name.find( "zchaff" ) != std::string::npos )
        {
            while( line.find( "Instance Satisfiable" ) == std::string::npos )
            {
                getline( in, line );
            }
            
            getline( in, line );
            
        }
        
        std::vector<string> tokens;
        split( tokens, line, boost::is_any_of( " " ) );

        string step = "";
        std::vector<string>::iterator it;
        for( it = tokens.begin(); it != tokens.end(); ++it )
        {
            if( (*it).find( "-" ) == string::npos )
            {
                string variable = dictionary[atoi( (*it).c_str() )];
                if( variable.substr( 0, 6 ).compare( "helper" ) == 0 ) 
                {
                    step = variable.substr( 14, variable.find( " )")-14 );
                    break;
                }
            }
        }
            
        for( it = tokens.begin(); it != tokens.end(); ++it )
        {
            if( (*it).find( "-" ) == string::npos )
            {
                string variable = dictionary[atoi( (*it).c_str() )];
                if( variable.substr( 0,2 ).compare( "EO" ) == 0 )
                {
                    operations.insert( variable );

                } else if( variable.substr( 0,2 ).compare( "TP" ) == 0 )
                {
                    if( variable.find( "step:" + step + " " ) != string::npos )
                    {
                        outcome_state.insert( variable );
                    }
                }
            }
        }        
    }
    
    in.close();
}


void encode_objective( int steps, int next_tile, int distance, std::map<string, int> dictionary, std::set<string> previous_tiles, std::set<string> &objective )
{        
    
    string helper_variables = "";
    for( int step = 1; step <= steps; step++ )
    {
        // helper variables:
        if( step != 1 )
        {
            helper_variables += " ";
        }
        
        helper_variables += boost::lexical_cast<string>( dictionary["helper( index:" + boost::lexical_cast<string>( step ) + " )"] );
        
        
        // previous tiles:
        std::set<string>::iterator it;
        for( it = previous_tiles.begin(); it != previous_tiles.end(); ++it )
        {
            string clause = boost::lexical_cast<string>( dictionary["DIST( distance:0 step:" + boost::lexical_cast<string>( step ) + " tile:" + *it + " )"] );
            clause += " -";
            clause += boost::lexical_cast<string>( dictionary["helper( index:" + boost::lexical_cast<string>( step ) + " )"] );
            
            objective.insert( clause );
        }
        
        // next tile:
        string clause = boost::lexical_cast<string>( dictionary["DIST( distance:" + boost::lexical_cast<string>( distance-1 ) 
                + " step:" + boost::lexical_cast<string>( step ) + " tile:" + boost::lexical_cast<string>( next_tile ) + " )"] );
        clause += " -";
        clause += boost::lexical_cast<string>( dictionary["helper( index:" + boost::lexical_cast<string>( step ) + " )"] );
        
        objective.insert( clause );
        
    }
    
    objective.insert( helper_variables );
}


int main( int argc, char** argv )
{
    if( argc != 5 )
    {
        cout << "Usage: ./task_manager <cnf_file> <dictionary_file> <solver_binary> <solver_output_file>" << endl;
        exit( 0 );
    }
   
    clock_t start = clock();
    

    string cnf_file = *(argv+1);
    string dictionary_file = *(argv+2);
    
    
    // dictionary
    std::map<string, int> variable_dictionary_string_int;
    std::map<int, string> variable_dictionary_int_string;
    populate_dictionary( dictionary_file, variable_dictionary_string_int, variable_dictionary_int_string);
    
    
    // operations
    std::set<string> operations;
    int number_of_operations = 0;


    // next_tile, distance, number_of_clauses
    int next_tile = 0;
    int distance = -1;
    int number_of_clauses = 0;
    
    
    // solved tiles:
    std::set<string> previous_tiles;
    
    // state
    std::set<string> state;
    
    // objective
    std::set<string> objective;
    
    // 1. write initial state to end of file
    encode_initial_state( 11,  6, 16, 8,
                          15, 4,  12, 7,
                          5,  9,  3,  2,
                          1,  14, 10, 13,
            
                          variable_dictionary_string_int,
                          variable_dictionary_int_string,
                          cnf_file,
                          next_tile,
                          distance,
                          previous_tiles,
                          number_of_clauses,
                          objective );
    
    
    // while final state is not reached:
    while( true )
    {        
        
        state.clear();
        objective.clear();
        
        if( next_tile == 16 )
        {
            remove( *(argv+4) );
            break;
        }
        
        
        // 2. run sat solver
        
        char *solver_args[4];
        pid_t pid;
  
        solver_args[0] = *(argv+3);
        solver_args[1] = *(argv+1);
        solver_args[2] = NULL;
    
        switch ( ( pid = fork() ) )
        {
            case -1:
                cout << "Fork() has failed" << endl;
                break;
            case 0:
                execv ( *(argv+3), solver_args );
                
                exit( EXIT_FAILURE );
                break;
            default:
                break;
        }
  
        waitpid( pid, NULL, 0 );
        
            
        // 3. decode solution
        
        // operations for this iteration
        std::set<string> ops;
        decode_solution( *(argv+4), state, ops, variable_dictionary_int_string );
        
        distance--;
        if( distance == 0 )
        {
            find_next_tile( next_tile, distance, previous_tiles, state, variable_dictionary_int_string );
        }
        
        
        /* output for testing:
        std::set<string>::iterator it_1;
        for( it_1 = ops.begin(); it_1 != ops.end(); ++it_1 )
        {
            cout << *it_1 << endl;
        }
    
        std::set<string>::iterator it_2;
        for( it_2 = state.begin(); it_2 != state.end(); ++it_2 )
        {
            cout << *it_2 << endl;
        }
        
        cout << endl << endl;
        */
        
        // 4. add operations to set
        collect_operations_from_solution( operations, ops, number_of_operations );
        
        // 5. calculate new objective
        encode_objective( 12, next_tile, distance, variable_dictionary_string_int, previous_tiles, objective );
            
        // 5. write new state and new objective to end of file
        std::set<string> new_state;
        calculate_new_state( state, new_state, variable_dictionary_string_int );
        
        modify_cnf_file( cnf_file, new_state, objective, number_of_clauses );
        remove( *(argv+4) );        
        
    }

    clock_t end = clock();

    cout << "Operations: " << endl << endl;
    
    for( std::set<string>::iterator it = operations.begin(); it != operations.end(); ++it )
    {
        cout << *it << endl;
    }

    cout << "\nRuntime task_manager: " << ( (double)(end - start) ) / CLOCKS_PER_SEC << "s" << endl;
    cout << "Number of Operations: " << operations.size() << endl;
    
    
    return 0;
}










// ugly stuff:


void find_next_tile( int &next_tile, int &distance, std::set<string> &previous_tiles, std::set<string> state, std::map<int, string> dictionary )
{
    previous_tiles.insert( boost::lexical_cast<string>( next_tile ) );
    
    if( next_tile == 1 || next_tile == 2 || next_tile == 3 || next_tile == 4 || next_tile == 6 || next_tile == 7
            || next_tile == 10 || next_tile == 11 || next_tile == 14 || next_tile == 15 )
    {
        next_tile += 1;
    } else if( next_tile == 5 )
    {
        next_tile = 9;
    } else if( next_tile == 9 )
    {
        next_tile = 13;
    } else if( next_tile == 13 )
    {
        next_tile = 6;
    } else if( next_tile == 8 || next_tile == 12 ) 
    {
        next_tile += 2;
    }
    
    calculate_distance( state, next_tile, dictionary, distance );
    
    if( next_tile == 16 )
    {
        return;
    }
    
        
    if( distance == 0 )
    {
        find_next_tile( next_tile, distance, previous_tiles, state, dictionary );
    }
}


void calculate_distance( std::set<string> state, int tile, std::map<int, string> dictionary, int &distance )
{
    int position = 0;
    
    std::set<string>::iterator it;
    for( it = state.begin(); it != state.end(); ++it )
    {
        if( (*it).find( "tile:" + boost::lexical_cast<string>( tile ) + " " ) != string::npos )
        {
            int pos1 = (*it).find( "position:" );
            int pos2 = (*it).find( " step:" );

            position = atoi( (*it).substr( pos1+9, pos2-pos1-9).c_str() );
        }
    }
    
    if( position == 0 ) 
    {
        cout << "error: position cannot be zero." << endl;
        exit( 0 );
    }

    
    if( tile < 5 ) 
    {
        if( position < 5 ) 
        {
            distance = 0;
        } else if( position < 9 && position > 4) 
        {
            distance = 1;
        } else if( position < 13 && position > 8) 
        {
            distance = 2;
        } else 
        {
            distance = 3;
        }
                        
    } else if( tile > 4 && tile < 9 ) 
    {
        if( position < 5 ) 
        {
            distance = 1;
        } else if( position < 9 && position > 4 ) 
        {
            distance = 0;
        } else if( position < 13 && position > 8 ) 
        {
            distance = 1;
        } else {
            distance = 2;
        }
                        
    } else if( tile > 8 && tile < 13 ) 
    {
        if( position < 5 ) 
        {
            distance = 2;
        } else if( position < 9 && position > 4 ) 
        {
            distance = 1;
        } else if( position < 13 && position > 8 ) 
        {
            distance = 0;
        } else 
        {
            distance = 1;
        }
                        
    } else {
        if( position < 5 ) {
            distance = 3;
        } else if( position < 9 && position > 4 ) 
        {
            distance = 2;
        } else if( position < 13 && position > 8 ) 
        {
            distance = 1;
        } else {
            distance = 0;
        }
                        
    }
                    
    if( tile%4 == 1 ) 
    {
        if( position%4 == 1 ) 
        {
            distance += 0;
        } else if( position%4 == 2 ) 
        {
            distance += 1;
        } else if( position%4 == 3 ) 
        {
            distance += 2;
        } else 
        {
            distance += 3;
        }
                        
    } else if( tile%4 == 2 ) 
    {
        if( position%4 == 1 ) 
        {
            distance += 1;
        } else if( position%4 == 2 ) 
        {
            distance += 0;
        } else if( position%4 == 3 ) 
        {
            distance += 1;
        } else 
        {
            distance += 2;
        }
                        
    } else if( tile%4 == 3 ) 
    {
        if( position%4 == 1 ) 
        {
            distance += 2;
        } else if( position%4 == 2 ) 
        {
            distance += 1;
        } else if( position%4 == 3 ) 
        {
            distance += 0;
        } else 
        {
            distance += 1;
        }
                        
    } else 
    {
        if( position%4 == 1 ) 
        {
            distance += 3;
        } else if( position%4 == 2 ) 
        {
            distance += 2;
        } else if( position%4 == 3 ) 
        {
            distance += 1;
        } else 
        {
            distance += 0;
        }
    }

}


void encode_initial_state( int _11, int _12, int _13, int _14,
                           int _21, int _22, int _23, int _24,
                           int _31, int _32, int _33, int _34,
                           int _41, int _42, int _43, int _44,
                           std::map<string, int> &dictionary_si,
                           std::map<int, string> &dictionary_is,
                           string cnf_file_name,
                           int &next_tile,
                           int &distance,
                           std::set<string> &previous_tiles,
                           int &number_of_clauses,
                           std::set<string> objective )
{
    // write state
    std::set<string> state;
    state.insert( boost::lexical_cast<string>( dictionary_si["TP( position:1 step:1 tile:" + boost::lexical_cast<string>( _11 ) + " )"] ) );
    state.insert( boost::lexical_cast<string>( dictionary_si["TP( position:2 step:1 tile:" + boost::lexical_cast<string>( _12 ) + " )"] ) );
    state.insert( boost::lexical_cast<string>( dictionary_si["TP( position:3 step:1 tile:" + boost::lexical_cast<string>( _13 ) + " )"] ) );
    state.insert( boost::lexical_cast<string>( dictionary_si["TP( position:4 step:1 tile:" + boost::lexical_cast<string>( _14 ) + " )"] ) );
    state.insert( boost::lexical_cast<string>( dictionary_si["TP( position:5 step:1 tile:" + boost::lexical_cast<string>( _21 ) + " )"] ) );
    state.insert( boost::lexical_cast<string>( dictionary_si["TP( position:6 step:1 tile:" + boost::lexical_cast<string>( _22 ) + " )"] ) );
    state.insert( boost::lexical_cast<string>( dictionary_si["TP( position:7 step:1 tile:" + boost::lexical_cast<string>( _23 ) + " )"] ) );
    state.insert( boost::lexical_cast<string>( dictionary_si["TP( position:8 step:1 tile:" + boost::lexical_cast<string>( _24 ) + " )"] ) );
    state.insert( boost::lexical_cast<string>( dictionary_si["TP( position:9 step:1 tile:" + boost::lexical_cast<string>( _31 ) + " )"] ) );
    state.insert( boost::lexical_cast<string>( dictionary_si["TP( position:10 step:1 tile:" + boost::lexical_cast<string>( _32 ) + " )"] ) );
    state.insert( boost::lexical_cast<string>( dictionary_si["TP( position:11 step:1 tile:" + boost::lexical_cast<string>( _33 ) + " )"] ) );
    state.insert( boost::lexical_cast<string>( dictionary_si["TP( position:12 step:1 tile:" + boost::lexical_cast<string>( _34 ) + " )"] ) );
    state.insert( boost::lexical_cast<string>( dictionary_si["TP( position:13 step:1 tile:" + boost::lexical_cast<string>( _41 ) + " )"] ) );
    state.insert( boost::lexical_cast<string>( dictionary_si["TP( position:14 step:1 tile:" + boost::lexical_cast<string>( _42 ) + " )"] ) );
    state.insert( boost::lexical_cast<string>( dictionary_si["TP( position:15 step:1 tile:" + boost::lexical_cast<string>( _43 ) + " )"] ) );
    state.insert( boost::lexical_cast<string>( dictionary_si["TP( position:16 step:1 tile:" + boost::lexical_cast<string>( _44 ) + " )"] ) );
    
    std::set<string> state_string;
    state_string.insert( "TP( position:1 step:1 tile:" + boost::lexical_cast<string>( _11 ) + " )" );
    state_string.insert( "TP( position:2 step:1 tile:" + boost::lexical_cast<string>( _12 ) + " )" );
    state_string.insert( "TP( position:3 step:1 tile:" + boost::lexical_cast<string>( _13 ) + " )" );
    state_string.insert( "TP( position:4 step:1 tile:" + boost::lexical_cast<string>( _14 ) + " )" );
    state_string.insert( "TP( position:5 step:1 tile:" + boost::lexical_cast<string>( _21 ) + " )" );
    state_string.insert( "TP( position:6 step:1 tile:" + boost::lexical_cast<string>( _22 ) + " )" );
    state_string.insert( "TP( position:7 step:1 tile:" + boost::lexical_cast<string>( _23 ) + " )" );
    state_string.insert( "TP( position:8 step:1 tile:" + boost::lexical_cast<string>( _24 ) + " )" );
    state_string.insert( "TP( position:9 step:1 tile:" + boost::lexical_cast<string>( _31 ) + " )" );
    state_string.insert( "TP( position:10 step:1 tile:" + boost::lexical_cast<string>( _32 ) + " )" );
    state_string.insert( "TP( position:11 step:1 tile:" + boost::lexical_cast<string>( _33 ) + " )" );
    state_string.insert( "TP( position:12 step:1 tile:" + boost::lexical_cast<string>( _34 ) + " )" );
    state_string.insert( "TP( position:13 step:1 tile:" + boost::lexical_cast<string>( _41 ) + " )" );
    state_string.insert( "TP( position:14 step:1 tile:" + boost::lexical_cast<string>( _42 ) + " )" );
    state_string.insert( "TP( position:15 step:1 tile:" + boost::lexical_cast<string>( _43 ) + " )" );
    state_string.insert( "TP( position:16 step:1 tile:" + boost::lexical_cast<string>( _44 ) + " )" );
    
    // set next_tile
    if( _11 != 1 )
    {
        next_tile = 1;
    } else {
        previous_tiles.insert( "1" );
        if( _12 != 2 ) 
        {
                next_tile = 2;
        } else 
        {
            previous_tiles.insert( "2" );
            if( _13 != 3 )
            {
                next_tile = 3;
            } else
            {
                previous_tiles.insert( "3" );
                if( _14 != 4 )
                {
                    next_tile = 4;
                } else 
                {
                    previous_tiles.insert( "4" );
                    if( _21 != 5 )
                    {
                        next_tile = 5;
                    } else
                    {
                        previous_tiles.insert( "5" );
                        if( _31 != 9 )
                        {
                            next_tile = 9;
                        } else
                        {
                            previous_tiles.insert( "9" );
                            if( _41 != 13 )
                            {
                                next_tile = 13;
                            } else
                            {
                                previous_tiles.insert( "13" );
                                if( _22 != 6 )
                                {
                                    next_tile = 6;
                                } else
                                {
                                    previous_tiles.insert( "6" );
                                    if( _23 != 7 )
                                    {
                                        next_tile = 7;
                                    } else
                                    {    
                                        previous_tiles.insert( "7" );
                                        if( _24 != 8 )
                                        {
                                            next_tile = 8;
                                        } else
                                        {
                                            previous_tiles.insert( "8" );
                                            if( _32 != 10 )
                                            {
                                                next_tile = 10;
                                            } else
                                            {
                                                previous_tiles.insert( "10" );
                                                if( _33 != 11 )
                                                {
                                                    next_tile = 11;
                                                } else
                                                {
                                                    previous_tiles.insert( "11" );
                                                    if( _34 != 12 )
                                                    {
                                                        next_tile = 12;
                                                    } else
                                                    {
                                                        previous_tiles.insert( "12" );
                                                        if( _42 != 14 )
                                                        {
                                                            next_tile = 14;
                                                        } else
                                                        {
                                                            previous_tiles.insert( "14" );
                                                            if( _43 != 15 )
                                                            {
                                                                next_tile = 15;
                                                            } else 
                                                            {
                                                                previous_tiles.insert( "15" );
                                                                next_tile = 16;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    
    // distance
    calculate_distance( state_string, next_tile, dictionary_is, distance );
    
    
    encode_objective( 12, next_tile, distance, dictionary_si, previous_tiles, objective );
    modify_cnf_file( cnf_file_name, state, objective, number_of_clauses );
    
}
