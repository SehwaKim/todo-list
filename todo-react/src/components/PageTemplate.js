import React, { Component } from 'react';
import TodoArea from './TodoArea';
import Popup from './Popup';

class PageTemplate extends Component{
    constructor(props) {
        super(props);
        this.state = {
            showAllTodoNeedToBeDone: false
        };
        this.togglePopup = this.togglePopup.bind(this);
    }

    togglePopup() {
        this.setState({
            showAllTodoNeedToBeDone: !this.state.showAllTodoNeedToBeDone
        });
        this.todoArea.toggleShowAllTodoNeedToBeDonePopup();
    }

    render() {
        var pageStyle = {
            height: '100%',
            width: '100%',
            position: 'fixed',
            top: 0,
            left: 0,
            overflow: 'hidden',
            verticalAlign: 'middle'
        };

        return (
            <div style={pageStyle}>
                <TodoArea ref={t => this.todoArea = t}
                          togglePopup={this.togglePopup}/>
                {this.state.showAllTodoNeedToBeDone ?
                    <Popup togglePopup={this.togglePopup}/>
                    : null}
            </div>
        );
    }
}

export default PageTemplate;