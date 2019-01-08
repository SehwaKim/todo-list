import React, {Component} from 'react';
import TodoInputArea from './TodoInputArea';
import TodoList from './TodoList';

class TodoArea extends Component{
    constructor(props) {
        super(props);
        this.state = {
            showAddTodoButton: true,
            showTodoInput: false,
            showAllTodoNeedToBeDonePopup: false
        };
        this.addOrRemoveChosenTask = this.addOrRemoveChosenTask.bind(this);
        this.chooseTasksModeOnOff = this.chooseTasksModeOnOff.bind(this);
        this.toggleUpdateMode = this.toggleUpdateMode.bind(this);
        this.toggleShowAllTodoNeedToBeDonePopup = this.toggleShowAllTodoNeedToBeDonePopup.bind(this);
        this.isTemporarilySelected = this.isTemporarilySelected.bind(this);
    }

    isTemporarilySelected(id) {
        return this.todoInput.isTemporarilySelected(id);
    }

    addOrRemoveChosenTask(id, isChosen) {
        this.todoInput.addOrRemoveTaskNumber(id, isChosen);
    }

    chooseTasksModeOnOff() {
        this.todoList.toggleCheckboxDisability();
    }

    toggleUpdateMode(content, id) {
        this.todoInput.switchUpdateTaskMode(content, id);
    }

    toggleShowAllTodoNeedToBeDonePopup() {
        this.setState({showAllTodoNeedToBeDonePopup: !this.state.showAllTodoNeedToBeDonePopup});
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

        var areaStyleWithOpacity = {
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
            right: 0,
            opacity: 0.4
        };

        return (
            <div className="todoArea"
                 style={this.state.showAllTodoNeedToBeDonePopup ? areaStyleWithOpacity : areaStyle}>
                <TodoInputArea chooseTasksModeOnOff={this.chooseTasksModeOnOff} ref={t => this.todoInput = t}/>
                <TodoList addOrRemoveChosenTask={this.addOrRemoveChosenTask}
                          toggleUpdateMode={this.toggleUpdateMode}
                          togglePopup={this.props.togglePopup}
                          isTemporarilySelected={this.isTemporarilySelected}
                          ref={t => this.todoList = t}/>
            </div>
        );
    }
}

export default TodoArea;