import React, {Component} from 'react';
import '../../../styles/nav-frontend.css';
import "./InputDatePicker.css";
import DatePicker from 'react-datepicker';
import moment from 'moment';
import 'react-datepicker/dist/react-datepicker.css';


export class InputDatePicker extends Component {
    constructor(props, context){
        super(props, context);

        this.handleChange = this.handleChange.bind(this);
    }

    handleChange(date) {
        this.props.onChange(date);
    }

    render() {

        const {id, dateFormat, label, statePropToChange} = this.props;

        return (
            <div className="skjemaelement">
                <label className="skjemaelement__label">{label}</label>
                <DatePicker
                    id={id}
                    className="skjemaelement__input"
                    dateFormat={dateFormat}
                    selected={statePropToChange}
                    onChange={this.handleChange}
                />
                <br/>

            </div>
        );
    }

}

export default InputDatePicker;