import React, { Component } from 'react';
import axios from 'axios';

class TodoInputArea extends Component{
    constructor(props) {
        super(props);
        this.state = {
            chooseTasks: false,
            updateTasks: false,
            idForUpdate: 0,
            taskNumber: [],
            taskNumberForShow: []
        };
        this.switchChooseTasksMode = this.switchChooseTasksMode.bind(this);
        this.createNewTodo = this.createNewTodo.bind(this);
        this.updateTodo = this.updateTodo.bind(this);
        this.addOrRemoveTaskNumber = this.addOrRemoveTaskNumber.bind(this);
        this.switchUpdateTaskMode = this.switchUpdateTaskMode.bind(this);
        this.isTemporarilySelected = this.isTemporarilySelected.bind(this);
    }

    isTemporarilySelected(id) {
        return this.state.taskNumber.includes(id);
    }

    addOrRemoveTaskNumber(id, isChosen) {
        let str = ' @'+id;
        if (isChosen) {
            this.setState((prevState) => {
                return {
                    taskNumber: prevState.taskNumber.concat(id),
                    taskNumberForShow: prevState.taskNumberForShow.concat(str)
                }
            });
        } else {
            this.setState((prevState) => {
                return {
                    taskNumber: prevState.taskNumber.filter(num => num !== id),
                    taskNumberForShow: prevState.taskNumberForShow.filter(numStr => numStr !== str)
                };
            });
        }
    }

    createNewTodo(e) {
        let content = this._inputElement.value;

        if (content === "") {
            alert("내용을 입력해주십시오");
            e.preventDefault();
            return;
        }

        axios.post('/tasks',
            {
                content: content,
                idGroupOfTasksToBeParent: this.state.taskNumber
            })
            .then(function (response) {
                console.log(response);
            })
            .catch(function (error) {
                console.log(error);
            }).then(() => {
                this.switchChooseTasksMode();
            });

        // this.inputBox._inputElement.value = "";
        // e.preventDefault();
    }

    updateTodo(e) {
        axios.put('/tasks/' + this.state.idForUpdate,
            {
                content: this._inputElement.value,
                idGroupOfTasksToBeParent: this.state.taskNumber
            })
            .then(function (response) {
                console.log(response.data);
            })
            .catch(function (error) {
                alert("이 TODO를 참조하고 있는 TODO는 조건으로 추가할 수 없습니다.");
            }).then(() => {
            this.switchChooseTasksMode();
        });
        // e.preventDefault();
    }

    switchChooseTasksMode() {
        if (this.state.chooseTasks) {
            this.setState((prevState) => {
                return {
                    chooseTasks: !prevState.chooseTasks,
                    taskNumber: [],
                    taskNumberForShow: []
                }
            });
        } else {
            this.setState((prevState) => {
                return {
                    chooseTasks: !prevState.chooseTasks
                }
            });
        }

        this.props.chooseTasksModeOnOff();
    }

    switchUpdateTaskMode = (content, id) => {
        this._inputElement.value = content;
        this.setState((prevState) => {
            return {
                chooseTasks: !prevState.chooseTasks,
                updateTasks: !prevState.updateTasks,
                idForUpdate: this.state.idForUpdate === id ? 0 : id,
                taskNumber: prevState.updateTasks ? [] : prevState.taskNumber,
                taskNumberForShow: prevState.updateTasks ? [] : prevState.taskNumberForShow
            }
        });
    };

    render() {
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

        var updateButtonStyle = {
            padding: '10px',
            fontSize: '20px',
            margin: '10px',
            marginLeft: '15px',
            backgroundColor: '#ff9928',
            color: '#FFF',
            border: 'thin solid #ff9928'
        };

        var inputStyle = {
            width: '35%',
            fontSize: '20px',
            padding: '15px',
            border: '1px solid lightGray'
        };

        var smallSizeFont = {
            fontSize: '17px',
            marginRight: '30px',
            cursor: 'pointer'
        };

        var taskNumbers = {
            fontWeight: 'bold'
        };

        var taskNumbersArea = {
            marginLeft: '15px',
            marginBottom: '5px',
            textAlign: 'left'

        };

        var taskSelectionArea = {
            marginLeft: '480px',
            marginBottom: '5px',
            textAlign: 'left'
        };

        return (
            <div>
                <div style={inputAreaStyle}>
                    <form onSubmit={this.state.updateTasks ? this.updateTodo : this.createNewTodo}>
                        <input type="text" style={inputStyle}
                               placeholder="Todo Item"
                               ref={(i) => this._inputElement = i}/>
                        {
                            this.state.updateTasks ?
                                <button type="submit" style={updateButtonStyle}>수정하기</button>
                                : <button type="submit" style={buttonStyle}>일정추가</button>
                        }
                    </form>
                    <div>
                        <div style={taskSelectionArea}>
                            <label style={smallSizeFont}>
                                <input className="check-box" type="checkbox"
                                       onChange={this.switchChooseTasksMode}
                                        disabled={this.state.updateTasks}/>
                                참조 Todo 추가
                            </label>
                        </div>
                        <div style={taskNumbersArea}>
                            <span style={{...smallSizeFont, ...taskNumbers}}><mark>{this.state.taskNumberForShow}</mark></span>
                        </div>
                    </div>
                </div>

            </div>
        );
    }
}

export default TodoInputArea;