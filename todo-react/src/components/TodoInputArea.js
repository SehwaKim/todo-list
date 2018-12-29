import React, { Component } from 'react';
import axios from 'axios';

class TodoInputArea extends Component{
    constructor(props) {
        super(props);
        this.state = {
            chooseTasks: false,
            taskNumber: [100, 101, 102],
            taskNumberForShow: [' @1',' @2',' @3']
        };
        this.switchChooseTasksMode = this.switchChooseTasksMode.bind(this);
        this.createNewTodo = this.createNewTodo.bind(this);
    }

    createNewTodo(e) {
        axios.post('/tasks',
            {
                content: this._inputElement.value,
                idGroupOfTasksToBeParent: this.state.taskNumber
            })
            .then(function (response) {
                console.log(response);
            })
            .catch(function (error) {
                console.log(error);
            });

        this._inputElement.value = "";

        e.preventDefault();//?
    }

    switchChooseTasksMode() {
        this.setState((prevState) => {
            return {
                chooseTasks: !prevState.chooseTasks
            }
        });
    }

    render() {
        var outerDivStyle = {
            // marginBottom: '20px'
        };

        var inputAreaStyle = {
            textAlign: 'center'
        };

        var buttonStyle = {
            padding: '10px',
            fontSize: '20px',
            margin: '10px',
            marginLeft: '15px',
            backgroundColor: '#0066FF',
            color: '#FFF',
            border: 'thin solid #0066FF'
        };

        var inputStyle = {
            width: '35%',
            fontSize: '20px',
            padding: '15px',
            border: '1px solid lightGray'
        };

        var smallSizeFont = {
            fontSize: '17px',
            marginRight: '30px'
        };

        var taskNumbers = {
            // fontWeight: 'bold'
        };

        var taskSelectionArea = {
            marginTop: '20px',
            marginLeft: '20px',
            marginBottom: '10px'
        };

        return (
            <div style={outerDivStyle}>
                <div style={inputAreaStyle}>
                    <form onSubmit={this.createNewTodo}>
                        <input type="text" style={inputStyle}
                               placeholder="Todo Item"
                               ref={(i) => this._inputElement = i}/>
                        <button type="submit" style={buttonStyle}>Add Todo</button>
                    </form>
                </div>
                <div style={taskSelectionArea}>
                    <input className="check-box" type="checkbox" onChange={this.switchChooseTasksMode}/>
                    <span style={smallSizeFont}>사전조건설정</span>
                    <span style={{...smallSizeFont, ...taskNumbers}}><mark>{this.state.taskNumberForShow}</mark></span>
                </div>
            </div>
        );
    }
}

export default TodoInputArea;