import React, {Component} from 'react';
import '../../../styles/nav-frontend.css';
import "./InputDatePicker.css";
import DatePicker from 'react-datepicker';
import moment from 'moment';
import 'react-datepicker/dist/react-datepicker.css';


export class InputDatePicker extends Component {
    constructor(props, context){
        super(props, context);
        this.state = {
            startDate: moment()
        };
        this.handleChange = this.handleChange.bind(this);
    }

    handleChange(date) {
        this.setState({
            startDate: date
        });
    }

    render() {

        return (
            <div className="skjemaelement">
                <label className="skjemaelement__label">Date</label>
                <DatePicker
                    className="skjemaelement__input"
                    selected={this.state.startDate}
                    onChange={this.handleChange}
                />
                <br/>

            </div>
        );
    }

}

export default InputDatePicker;