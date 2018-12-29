import React, { Component } from 'react';

class Pagination extends Component{
    render() {
        var pageStyle = {
            width: '100%',
            height: '5%',
            textAlign: 'center',
            borderStyle: 'dotted',
            borderColor: 'rgba(255,255,255,.5)'
        };
        return (
            <div style={pageStyle}>
                1 | 2 | 3 | 4 | 5
            </div>
        );
    }
}

export default Pagination;