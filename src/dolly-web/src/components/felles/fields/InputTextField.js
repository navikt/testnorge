import React, {Component} from 'react';
import '../../../styles/nav-frontend.css';
import './InputTextField.css';

export default class InputTextField extends Component {
    constructor(props, context){
        super(props, context);

        this.changeHandler = this.changeHandler.bind(this);
    }

    changeHandler(event){
        this.props.onChange(this.props.statePropToChange, event.target.value);
    }

    render() {

        const {id, value, label, pattern} = this.props;

        return(
            <div className="skjemaelement">
                <label className="skjemaelement__label">{label} </label>
                <input className="skjemaelement__input"
                       id={id}
                       value={value}
                       onChange={this.changeHandler}
                       pattern={pattern}
                       type="text" />
            </div>
        )
    }
}
