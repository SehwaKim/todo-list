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
            unRemovable: false
        };
        this.deleteTask = this.deleteTask.bind(this);
        this.changeTaskStatus = this.changeTaskStatus.bind(this);
        this.addOrRemoveChosenTask = this.addOrRemoveChosenTask.bind(this);
        this.toggleRemoveButtonDisability = this.toggleRemoveButtonDisability.bind(this);
    }

    componentWillMount() {
        if (this.props.updatedAt != null) {
            this.setState({update: true});
        }
        if (this.props.status === 'DONE') {
            this.setState({done: true});
        }
    }

    changeTaskStatus() {
        axios.put('/tasks/' + this.props.id,
            {
                status: this.state.done ? 'TODO' : 'DONE',
                updateOnlyForStatus: true
            })
            .then((response) => {
                this.setState({
                    done: !this.state.done,
                    updated: true,
                    updatedAt: moment().format("YYYY-MM-DD")
                });
                this.statusCheckbox.toggleCheckboxChange();
            })
            .catch((error) => {

                // 메세지 띄우기 - "참조하는 TODO가 모두 완료되어야 합니다."
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
                console.log(error);
            }).then(function () {
                // always executed
            });
    }

    toggleRemoveButtonDisability() {
        this.setState({unRemovable: !this.state.unRemovable});
    }

    render() {
        var boxStyle = {
            margin: '10px',
            padding: '18px',
            borderStyle: 'solid',
            borderRadius: '5px',
            borderColor: 'lightGray',
            borderWidth: 'thin',
            textAlign: 'center'
        };

        var contentStyle = {
            fontSize: '23px'
        };

        var doneContentStyle = {
            fontSize: '23px',
            textDecoration: 'line-through'
        };

        var referenceStyle = {
            fontSize: '18px'
        };

        var smallWord = {
            fontSize: '13px',
            color: '#7e7e7e'
        };

        var XButtonStyle = {
            fontSize: '26px',
            backgroundColor: 'white',
            color: '#DD4132',
            border: '0px'
        };

        var tableStyle = {
            width: '100%'
        };

        var checkboxCell = {
            width: '3%'
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

        var forthCell = {
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
                                <Checkbox checked={this.state.done}
                                          isDisable={false}
                                          forStatus={true}
                                          changeTaskStatus={this.changeTaskStatus}
                                          ref={checkbox => this.statusCheckbox = checkbox}/>
                            </div>
                        </td>
                        <td style={checkboxCell}>
                            <div>
                                <Checkbox checked={false}
                                          isDisable={true}
                                          forStatus={false}
                                          addOrRemoveChosenTask={this.addOrRemoveChosenTask}
                                          ref={checkbox => this.taskChoosingCheckbox = checkbox}/>
                            </div>
                        </td>
                        <td style={forthCell}>
                            <div>
                                <span>no.{this.props.id}</span>
                            </div>
                        </td>
                        <td style={secondCell}>
                            <div>
                                <div style={this.state.done ? doneContentStyle : contentStyle}>
                                    <span>{this.props.content}</span>
                                </div>
                                <div style={referenceStyle}>{this.props.parentTaskIds}</div>
                            </div>
                        </td>
                        <td style={dateCell}>
                            <div>
                                <div>{this.props.createdAt} <span style={smallWord}>(created)</span></div>
                                {this.state.updated ?
                                    <div>{this.state.updatedAt} <span style={smallWord}>(updated)</span></div> : null}
                            </div>
                        </td>
                        <td style={forthCell}>
                            <button style={XButtonStyle}
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