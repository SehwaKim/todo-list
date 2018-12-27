import React, { Component } from 'react';
import TodoItem from './TodoItem';
import Pagination from './Pagination';

class TodoList extends Component{
    constructor(props) {
        super(props);
        this.state = {
            items: []
        };
    }

    componentDidMount() {
        // ajax 로 get tasks?page=1 해서 TodoItem 채워놔야 함
    }

    render() {
        return (
            <div>
                {this.state.items}
                <Pagination/>
            </div>
        );
    }
}

export default TodoList;