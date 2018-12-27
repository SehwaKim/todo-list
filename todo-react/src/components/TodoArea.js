import React, { Component } from 'react';
import TodoAddButton from './TodoAddButton';
import TodoList from './TodoList';

class TodoArea extends Component{
    render() {
        var areaStyle = {
            height: '80%',
            width: '40%',
            padding: '15px',
            margin: 'auto',
            overflow: 'hidden',
            borderRadius: '5px',
            position: 'absolute',
            top: 0,
            left: 0,
            bottom: 0,
            right: 0
        };
        return (
            <div className="todoArea" style={areaStyle}>
                <TodoAddButton/>
                <TodoList/>
            </div>
        );
    }
}

export default TodoArea;