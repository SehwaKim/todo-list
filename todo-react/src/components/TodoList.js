import React, { Component } from 'react';
import axios from 'axios';
import TodoItem from './TodoItem';
import Pagination from './Pagination';

class TodoList extends Component{
    constructor(props) {
        super(props);
        this.state = {
            items: [],
            itemRefs: new Map(),
            chooseTaskMode: false
        };
        this.removeTodoItem = this.removeTodoItem.bind(this);
        this.toggleCheckboxDisability = this.toggleCheckboxDisability.bind(this);
        this.checkAllParentTasks = this.checkAllParentTasks.bind(this);
        this.forceSetItemTodo = this.forceSetItemTodo.bind(this);
        this.getTasksByPage = this.getTasksByPage.bind(this);
        this.toggleUpdateMode = this.toggleUpdateMode.bind(this);
    }

    componentDidMount() {
        this.getTasksByPage(1);
    }

    getTasksByPage(page) {
        axios.get('/tasks', {
            params: {
                page: page
            }
        }).then((response) => {
            this.pagination.setPaginationInfo(response.data);

            let tasks = [];
            let refSet = new Map();

            let currentlyUpdatingId = this.props.getCurrentlyUpdatingId();

            for (let task of response.data.content) {
                let temporarySelection = this.props.isTemporarilySelected(task.id);

                tasks.push(<TodoItem id={task.id} content={task.content} status={task.status}
                                     updated={task.updatedAt !== null}
                                     createdAt={task.createdAt} updatedAt={task.updatedAt}
                                     parentTaskIds={task.parentTaskIds}
                                     parentTaskIdsString={task.parentTaskIdsString}
                                     chooseTaskMode={this.state.chooseTaskMode}
                                     currentlyUpdating={currentlyUpdatingId === task.id}
                                     temporarySelection={temporarySelection}
                                     removeTodoItem={this.removeTodoItem}
                                     addOrRemoveChosenTask={this.props.addOrRemoveChosenTask}
                                     toggleCheckboxDisability={this.toggleCheckboxDisability}
                                     checkAllParentTasks={this.checkAllParentTasks}
                                     toggleUpdateMode={this.toggleUpdateMode}
                                     forceSetItemTodo={this.forceSetItemTodo}
                                     togglePopup={this.props.togglePopup}
                                     key={Date.now() + '@' + task.id}
                                     ref={(el => refSet.set(task.id, el))}/>);
            }
            this.setState({items: tasks, itemRefs: refSet});

        }).catch(function (response) {
            console.log(response);
        }).then(() => {
        });
    }

    toggleUpdateMode(content, id) {
        this.props.toggleUpdateMode(content, id);
    }

    forceSetItemTodo(id) {
        if (this.state.itemRefs.has(id) && this.state.itemRefs.get(id) !== null) {
            this.state.itemRefs.get(id).toggleStatus();
        }
    }

    toggleCheckboxDisability(exceptId) {
        this.setState({chooseTaskMode: !this.state.chooseTaskMode});

        for(let id of this.state.itemRefs.keys()) {
            if(this.state.itemRefs.get(id) === null) continue;
            let todoItem = this.state.itemRefs.get(id);
            todoItem.toggleRemoveButtonDisability();
            todoItem.toggleModifyStatusDisability();
            if(id === exceptId) continue;
            todoItem.toggleModifyDisability();
            todoItem.taskChoosingCheckbox.toggleCheckboxDisabled();
        }
    }

    checkAllParentTasks(parentTaskIds) {
        for(let id of parentTaskIds){
            if (this.state.itemRefs.has(id) && this.state.itemRefs.get(id) !== null) {
                this.state.itemRefs.get(id).taskChoosingCheckbox.selectOrUnselectForParentTask();
            } else {
                this.props.addOrRemoveChosenTask(id, true);
            }
        }
    }

    removeTodoItem(id) {
        this.setState((prevState) => {
            return {
                items: prevState.items.filter(item => item.props.id !== id)
            };
        });
    }

    render() {
        var listStyle = {
            height: '83%',
            width: '100%',
            margin: 'auto',
            overflow: 'hidden',
            borderRadius: '5px',
            top: 0,
            left: 0,
            bottom: 0,
            right: 0
        };

        return (
            <div style={listStyle}>
                {this.state.items}
                <Pagination ref={p => this.pagination = p}
                            getTasksByPage={this.getTasksByPage}/>
            </div>
        );
    }
}

export default TodoList;