import React, { Component } from 'react'
import '~/styles/nav-frontend.less'
import './Datepicker.less'
import ReactDatePicker from 'react-datepicker'
import 'react-datepicker/dist/react-datepicker.css'

export default class Datepicker extends Component {
	handleChange = date => {
		this.props.onChange(date)
	}

	render() {
		const { id, dateFormat, label, statePropToChange } = this.props

		return (
			<div className="skjemaelement">
				<label className="skjemaelement__label">{label}</label>
				<ReactDatePicker
					id={id}
					className="skjemaelement__input"
					dateFormat={dateFormat}
					selected={statePropToChange}
					onChange={this.handleChange}
				/>
				<br />
			</div>
		)
	}
}
