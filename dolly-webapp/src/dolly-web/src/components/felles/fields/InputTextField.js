import React from 'react';
import '../../../styles/nav-frontend.css';
import './InputTextField.css';
import {pure} from 'recompose';

const InputTextFeild = ({id, name, value, label, pattern, disabled, onChange, patternFeilmelding, statePropToChange}) => {

    const changeHandler = (event) => {
        onChange(statePropToChange, event.target.value);
    };

    const patternMismatch = () => {
        let input = document.getElementById(id);
        if (input) {
            return input.validity.patternMismatch;
        }

        return false;
    };

    let patternError = false;
    let feilmeldingHtml;

    if (patternMismatch()) {
        patternError = true;
        feilmeldingHtml = (
            <div className="skjemaelement__feilmelding"
                 aria-live="assertive"
                 role="alert"
            >
                {patternFeilmelding}
            </div>
        )
    }

    return (
        <div className="skjemaelement input-text-field-container">
            <label className="skjemaelement__label">{label} </label>
            <input className={"skjemaelement__input " + (patternError ? "skjemaelement__input--harFeil" : "")}
                   id={id}
                   name={name}
                   value={value}
                   onChange={changeHandler}
                   pattern={pattern}
                   disabled={disabled}
                   type="text"
            />
            {feilmeldingHtml}
        </div>
    )
};

export default pure(InputTextFeild);
