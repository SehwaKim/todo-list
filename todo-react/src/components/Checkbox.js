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
        if (this.props.forStatus) {
            this.props.changeTaskStatus();
            return;
        }
        this.selectOrUnselectForParentTask();
    }

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
        return (
            <div className="checkbox">
                <label>
                    <input
                        type="checkbox"
                        checked={this.state.isChecked}
                        onChange={this.takeActionOnClick}
                        disabled={this.state.isDisable}
                    />
                </label>
            </div>
        );
    }
}

export default Checkbox;