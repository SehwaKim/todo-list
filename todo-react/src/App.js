import React, { Component } from 'react';
import PageTemplate from './components/PageTemplate';

class App extends Component {
    constructor(props) {
        super(props);
        this.state = {
            items: [],
            showTodoInput: false
        };
    }

    render() {
        return (
            <div>
                <PageTemplate/>
            </div>
        );
    }
}

export default App;
