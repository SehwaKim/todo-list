import React, { Component } from 'react';

class TodoAddButton extends Component{
    render() {
        var buttonStyle = {
            padding: '10px',
            fontSize: '16px',
            margin: '15px',
            backgroundColor: '#0066FF',
            color: '#FFF',
            border: '2px solid #0066FF'

    };
        return (
            <div>
                <button style={buttonStyle}>Add Todo</button>
            </div>
        );
    }
}

export default TodoAddButton;