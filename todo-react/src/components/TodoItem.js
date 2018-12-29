import React, { Component } from 'react';
import axios from "axios/index";

class TodoItem extends Component{
    constructor(props) {
        super(props);
        this.state = {
            updated: false
        };
        this.deleteTask = this.deleteTask.bind(this);
    }

    componentDidMount() {
        if (this.props.updatedAt != null) {
            this.setState({update: true});
        }
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
                                    <input className="check-box" type="checkbox"/>
                                </div>
                            </td>
                            <td style={checkboxCell}>
                                <div>
                                    <input className="check-box" type="checkbox"/>
                                </div>
                            </td>
                            <td style={forthCell}>
                                <div>
                                    <span>no.{this.props.id}</span>
                                </div>
                            </td>
                            <td style={secondCell}>
                                <div>
                                    <div style={contentStyle}>{this.props.content}</div>
                                    <div style={referenceStyle}>{this.props.parentTaskIds}</div>
                                </div>
                            </td>
                            <td style={dateCell}>
                                <div>
                                    <div>{this.props.createdAt} <span style={smallWord}>(created)</span></div>
                                    {this.state.updated ?
                                        <div>{this.props.updatedAt} <span style={smallWord}>(updated)</span></div> : null}
                                </div>
                            </td>
                            <td style={forthCell}>
                                <button style={XButtonStyle} onClick={this.deleteTask}>x</button>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        );
    }
}

export default TodoItem;