import React, {Component} from 'react';

class Popup extends Component{

    render() {
        var backgroundStyle = {
            height: '100%',
            width: '100%',
            position: 'absolute'
        };

        var popupStyle = {
            zIndex: 1,
            height: '8%',
            width: '20%',
            padding: '25px',
            margin: 'auto',
            position: 'absolute',
            borderRadius: '5px',
            border: '1px solid black',
            backgroundColor: 'white',
            top: 0,
            left: 0,
            bottom: 0,
            right: 0,
            fontSize: '20px',
            textAlign: 'center'
        };

        var buttonStyle = {
            paddingLeft: '10px',
            paddingRight: '10px',
            paddingTop: '7px',
            paddingBottom: '7px',
            fontSize: '17px',
            marginTop: '15px',
            backgroundColor: '#0066FF',
            color: '#FFF',
            border: 'thin solid #0066FF',
            cursor: 'pointer'
        };

        return (
            <div style={backgroundStyle}>
                <div style={popupStyle}>
                    <div>
                        <span>참조하는 TODO가 모두 완료되어야 합니다.</span>
                    </div>
                    <div>
                        <button style={buttonStyle} onClick={this.props.togglePopup}>확인</button>
                    </div>
                </div>
            </div>
        );
    }
}

export default Popup;