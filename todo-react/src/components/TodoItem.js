import React, {Component} from 'react';
import axios from "axios/index";
import moment from 'moment';
import 'moment-timezone';
import Checkbox from './Checkbox';

class TodoItem extends Component{
    constructor(props) {
        super(props);
        this.state = {
            done: false,
            updated: false,
            updatedAt: this.props.updatedAt,
            updateMode: false,
            unRemovable: this.props.chooseTaskMode,
            // modifyDisability: this.props.chooseTaskMode && !this.props.currentUpdating,
            modifyDisability: this.props.chooseTaskMode,
            modifyStatusDisability: this.props.chooseTaskMode
        };
        this.deleteTask = this.deleteTask.bind(this);
        this.changeTaskStatus = this.changeTaskStatus.bind(this);
        this.addOrRemoveChosenTask = this.addOrRemoveChosenTask.bind(this);
        this.toggleRemoveButtonDisability = this.toggleRemoveButtonDisability.bind(this);
        this.toggleUpdateTaskMode = this.toggleUpdateTaskMode.bind(this);
        this.toggleModifyDisability = this.toggleModifyDisability.bind(this);
        this.toggleStatus = this.toggleStatus.bind(this);
    }

    componentWillMount() {
        if (this.props.updatedAt != null) {
            this.setState({update: true});
        }
        if (this.props.status === 'DONE') {
            this.setState({done: true, modifyDisability: true});
        }
    }

    changeTaskStatus(e) {
        axios.put('/tasks/' + this.props.id,
            {
                status: this.state.done ? 'TODO' : 'DONE',
                updateOnlyForStatus: true
            })
            .then((response) => {
                let updatedTask = response.data;
                if (updatedTask.status === 'TODO') {
                    if (updatedTask.idGroupOfChildTasksTodo.length > 0) {
                        for(let id of updatedTask.idGroupOfChildTasksTodo) {
                            this.props.forceSetItemTodo(id);
                        }
                    }
                }
                this.toggleStatus();
            })
            .catch((error) => {
                this.props.togglePopup();
            });
        e.preventDefault();
    }

    toggleStatus() {
        this.setState({
            done: !this.state.done,
            updated: true,
            updatedAt: moment().format("YYYY-MM-DD"),
            modifyDisability: !this.state.done
        });
    }

    addOrRemoveChosenTask(isChosen) {
        this.props.addOrRemoveChosenTask(this.props.id, isChosen);
    }

    deleteTask() {
        axios.delete('/tasks/' + this.props.id)
            .then((response) => {
                this.props.removeTodoItem(this.props.id);
            }).catch(function (error) {
                alert("다른 TODO로부터 참조되고 있는 TODO는 삭제할 수 없습니다.");
            }).then(function () {
                // always executed
            });
    }

    toggleRemoveButtonDisability() {
        this.setState({unRemovable: !this.state.unRemovable});
    }

    toggleModifyDisability() {
        if(this.state.done) return;
        this.setState({modifyDisability: !this.state.modifyDisability});
    }

    toggleModifyStatusDisability() {
        this.setState({modifyStatusDisability: !this.state.modifyStatusDisability});
    }

    toggleUpdateTaskMode() {
        if (this.props.parentTaskIds.length > 0) {
            this.props.checkAllParentTasks(this.props.parentTaskIds); // 근데 수정하기로 들어갈때만 표시해야됨...
        }
        this.props.toggleCheckboxDisability(this.props.id);
        var content = this.state.updateMode ? "" : this.props.content;
        this.props.toggleUpdateMode(content, this.props.id);
        this.setState({updateMode: !this.state.updateMode});
        // 기존 정보 가져올 필요는없다 이미 있는 id 들 가지고 체크박스 표시해주면 된다

        // 주인공 아닌애들은 회색으로 될수없나???

        // 모조리 todo로 바뀐애들은 put 메소드 리턴값으로 바뀐 아이디들만 가져와서 현재화면에 있는애들중에 일치하는애 있으면 다시 빠꾸시키기
    }

    render() {
        var boxStyle = {
            margin: '10px',
            padding: '15px',
            borderStyle: 'solid',
            borderRadius: '5px',
            borderColor: 'lightGray',
            borderWidth: 'thin',
            textAlign: 'center'
        };

        var contentStyle = {
            fontSize: '23px',
            cursor: 'pointer'
        };

        var doneContentStyle = {
            fontSize: '23px',
            cursor: 'pointer',
            textDecoration: 'line-through'
        };

        var referenceStyle = {
            fontSize: '18px'
        };

        var smallWord = {
            fontSize: '13px',
            color: '#7e7e7e'
        };

        var XButtonStyleActive = {
            fontSize: '26px',
            backgroundColor: 'white',
            color: '#DD4132',
            border: '0px',
            cursor: 'pointer'
        };

        var XButtonStyleInActive = {
            fontSize: '26px',
            backgroundColor: 'white',
            color: '#ffa2a2',
            border: '0px'
        };

        var tableStyle = {
            width: '100%'
        };

        var checkboxCell = {
            width: '11%'
        };

        var secondCell = {
            width: '59%'
        };

        var dateCell = {
            width: '25%',
            fontSize: '18px',
            color: '#343434',
            textAlign: 'left'
        };

        var modifyModeSvg = {
            marginLeft: '10px'
        };

        var unModifyModeSvg = {
            marginLeft: '10px',
            opacity: 0.1
        };

        var buttonCellStyle = {
            width: '5%',
            fontSize: '18px',
            textAlign: 'right'
        };

        return (
            <div style={boxStyle} className="todoItem">
                <table style={tableStyle}>
                    <tbody>
                    <tr>
                        <td style={checkboxCell}>
                            <div>
                                <Checkbox checked={false}
                                          isDisable={!this.props.chooseTaskMode}
                                          forStatus={false}
                                          addOrRemoveChosenTask={this.addOrRemoveChosenTask}
                                          ref={checkbox => this.taskChoosingCheckbox = checkbox}
                                          id={this.props.id}/>
                            </div>
                        </td>
                        <td style={secondCell}>
                            <div>
                                <div style={this.state.done ? doneContentStyle : contentStyle}>
                                    <span
                                        onDoubleClick={this.state.modifyStatusDisability ? null : this.changeTaskStatus}>
                                        {this.props.content}</span>
                                    <span style={this.state.modifyDisability ? unModifyModeSvg : modifyModeSvg}
                                          onClick={this.state.modifyDisability ? null : this.toggleUpdateTaskMode}>
                                        <svg xmlns="http://www.w3.org/2000/svg" width="13" height="13" viewBox="0 0 8 8">
                                            <path d="M6 0l-1 1 2 2 1-1-2-2zm-2 2l-4 4v2h2l4-4-2-2z" />
                                        </svg>
                                    </span>
                                </div>
                                <div style={referenceStyle}>{this.props.parentTaskIdsString}</div>
                            </div>
                        </td>
                        <td style={dateCell}>
                            <div>
                                <div>{this.props.createdAt} <span style={smallWord}>(created)</span></div>
                                {this.state.updated ?
                                    <div>{this.state.updatedAt} <span style={smallWord}>(updated)</span></div> : null}
                            </div>
                        </td>
                        <td style={buttonCellStyle}>
                            <button style={this.state.unRemovable ? XButtonStyleInActive : XButtonStyleActive}
                                    onClick={this.deleteTask}
                                    disabled={this.state.unRemovable}>x</button>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        );
    }
}

export default TodoItem;