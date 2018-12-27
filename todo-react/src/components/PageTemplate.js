import React, { Component } from 'react';
import TodoArea from './TodoArea';

class PageTemplate extends Component{
    render() {
        var pageStyle = {
            backgroundColor: '#66CCFF',
            height: '100%',
            width: '100%',
            position: 'fixed',
            top: 0,
            left: 0,
            overflow: 'hidden',
            verticalAlign: 'middle',
            // textAlign: 'center'
        };
        return (
            <div style={pageStyle}>
                <TodoArea/>
            </div>
        );
    }
}

export default PageTemplate;