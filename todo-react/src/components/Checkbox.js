import React, {Component} from 'react';

class Checkbox extends Component {
    constructor(props) {
        super(props);
        this.state = {
            isChecked: this.props.checked,
            isDisable: this.props.isDisable
        };
        this.toggleCheckboxChange = this.toggleCheckboxChange.bind(this);
        this.selectOrUnselectForParentTask = this.selectOrUnselectForParentTask.bind(this);
        this.toggleCheckboxDisabled = this.toggleCheckboxDisabled.bind(this);
    }

    takeActionOnClick = () => {
        this.selectOrUnselectForParentTask();
    };

    toggleCheckboxChange() {
        this.setState((prev) => ({
                isChecked: !prev.isChecked
        }));
    }

    selectOrUnselectForParentTask() {
        this.props.addOrRemoveChosenTask(!this.state.isChecked);
        this.toggleCheckboxChange();
    }

    toggleCheckboxDisabled() {
        if (!this.state.isDisable) {
            this.setState((prev) => ({
                isDisable: !prev.isDisable,
                isChecked: false
            }));
        } else {
            this.setState((prev) => ({
                isDisable: !prev.isDisable
            }));
        }
    }

    render() {
        var labelStyle = {
            fontSize: '18px',
            cursor: 'pointer'
        };

        return (
            <div className="checkbox">
                <label style={labelStyle}>
                    <input
                        type="checkbox"
                        checked={this.state.isChecked}
                        onChange={this.takeActionOnClick}
                        disabled={this.state.isDisable}
                    />
                    no.{this.props.id}
                </label>
            </div>
        );
    }
}

export default Checkbox;