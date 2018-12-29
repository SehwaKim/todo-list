import React, {Component} from 'react';
import TodoInputArea from './TodoInputArea';
import TodoList from './TodoList';
import Pagination from './Pagination';

class TodoArea extends Component{
    constructor(props) {
        super(props);
        this.state = {
            showAddTodoButton: true,
            showTodoInput: false
        };
    }

    render() {
        var areaStyle = {
            height: '85%',
            width: '40%',
            padding: '15px',
            margin: 'auto',
            overflow: 'hidden',
            borderRadius: '5px',
            position: 'absolute',
            borderStyle: 'dotted',
            borderColor: 'rgba(255,255,255,.5)',
            top: 0,
            left: 0,
            bottom: 0,
            right: 0
        };
        return (
            <div className="todoArea" style={areaStyle}>
                <TodoInputArea/>
                <TodoList/>
                <Pagination/>
            </div>
        );
    }
}

export default TodoArea;