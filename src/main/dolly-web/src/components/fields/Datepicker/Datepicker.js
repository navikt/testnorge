import React, { Component } from 'react'
import DayPickerInput from 'react-day-picker/DayPickerInput'
import { DateUtils } from 'react-day-picker'
import YearSelector from './YearMonthForm'
import dateFnsFormat from 'date-fns/format'
import dateFnsParse from 'date-fns/parse'
import _isNil from 'lodash/isNil'
import DateValidation, { defaultDateFormat } from './DateValidation'
import {
	MONTHS,
	WEEKDAYS_LONG,
	WEEKDAYS_SHORT,
	FIRST_DAY_OF_WEEK,
	LABELS
} from './DatepickerLocalization'
import Input from '~/components/fields/Input/Input'

import 'react-day-picker/lib/style.css'
import './Datepicker.less'

const currentDate = new Date()
const currentYear = currentDate.getFullYear()
const fromMonth = new Date(currentYear - 100, 0)
const toMonth = new Date(currentYear + 5, 11)

export default class Datepicker extends Component {
	// State keeps track of
	// - active (focused) input field
	// - validDate: hver gang vi har en valid date (date object), lagres dette for å oppdatere calender GUI (overlay)
	state = {
		active: false,
		validDate: new Date()
	}

	componentWillMount() {
		document.addEventListener('mousedown', this.handleDocumentEventClick, false)
	}

	componentWillUnmount() {
		document.removeEventListener('mousedown', this.handleDocumentEventClick, false)
	}

	// Saving - calling actual onchange
	save = str => {
		if (_isNil(str)) return undefined

		// Prevent saving actual date (må lagre"01.01.2018" format)
		let date = str
		if (DateUtils.isDate(date)) {
			this.setState({ validDate: date })
			date = dateFnsFormat(date, defaultDateFormat, new Date())
		}
		return this.props.onChange(date)
	}

	// FOCUS
	setActive = (active, cb) => this.setState({ active }, cb)
	handleDayPickerHide = e => this.setActive(false)
	handleFocus = e => this.setActive(true)

	// Save date when selecting from popup
	handleOnDayChange = dato => this.save(dato)

	// Special case, saving when value is cleared (empty)
	handleKeyUp = e => {
		const { value } = e.target
		if (value === '') this.save('')
	}

	// Format date when clicking on a date in popup
	formatDate = (str, format) => dateFnsFormat(str, format, new Date())

	// Parse all inputs
	parseDate = (str, format) => {
		// Ingenting har endret seg
		if (this.props.value === str) return undefined

		// Simple validation for better performance
		// ikke en valid dato, men må mellomlagres i formik
		if (str.length !== 8 && str.length !== 10) {
			this.save(str)
			return undefined
		}

		// Do regex date validation (more expensive)
		const valid = DateValidation().isValidSync(str)
		if (!valid) return undefined

		// Parse date if all is good
		return dateFnsParse(str, format, new Date())
	}

	// Handle bug hvor overlay ikke lukkes automatisk dersom det får fokus
	handleDocumentEventClick = e => {
		if (!this.containerNode.contains(e.target)) this.dayPickerInputNode.hideDayPicker()
	}

	render() {
		const { label, error, value, disabled, form } = this.props

		return (
			<div className="dolly-datepicker" ref={node => (this.containerNode = node)}>
				<DayPickerInput
					ref={node => (this.dayPickerInputNode = node)}
					format={defaultDateFormat}
					value={value}
					formatDate={this.formatDate}
					parseDate={this.parseDate}
					onDayChange={this.handleOnDayChange}
					onDayPickerHide={this.handleDayPickerHide}
					placeholder="Ikke spesifisert"
					inputProps={{
						onKeyUp: this.handleKeyUp,
						onFocus: this.handleFocus,
						onBlur: () => form.setFieldTouched(this.props.name, true),
						disabled: disabled,
						feil: this.state.active ? null : error,
						label: label
					}}
					dayPickerProps={{
						month: this.state.validDate,
						toMonth: toMonth,
						fromMonth: fromMonth,
						months: MONTHS,
						weekdaysLong: WEEKDAYS_LONG,
						weekdaysShort: WEEKDAYS_SHORT,
						firstDayOfWeek: FIRST_DAY_OF_WEEK,
						labels: LABELS,
						captionElement: ({ date, localeUtils }) => (
							<YearSelector
								date={date}
								localeUtils={localeUtils}
								fromMonth={fromMonth}
								toMonth={toMonth}
								onChange={this.save}
							/>
						)
					}}
					component={Input}
				/>
			</div>
		)
	}
}

export const FormikDatepicker = props => {
	const { field, form, ...restProps } = props
	let child, parent, grandparent, idx, jdx, feilmelding

	const arr = field.name.split('[')
	console.log('form :', form)
	console.log('field :', field)
	console.log('arr :', arr)
	if (arr.length === 1) {
		if (form.touched[field.name] && form.errors[field.name]) {
			feilmelding = form.errors[field.name]
		}
	} else if (arr.length === 2) {
		parent = arr[0]
		child = arr[1].split(']')[1]
		idx = arr[1].split(']')[0]
		console.log('child :', child)
		console.log('idx :', idx)
		if (
			form.touched[parent] &&
			form.touched[parent][idx] &&
			form.touched[parent][idx][child] &&
			form.errors[parent] &&
			form.errors[parent][idx] &&
			form.errors[parent][idx][child]
		) {
			feilmelding = form.errors[parent][idx][child]
		}
	} else if (arr.length === 3) {
		grandparent = arr[0]
		parent = arr[1].split(']')[1]
		idx = arr[1].split(']')[0]
		child = arr[2].split(']')[1]
		jdx = arr[1].split(']')[0]
		console.log('jdx :', jdx)
		if (
			form.touched[grandparent] &&
			form.touched[grandparent][idx] &&
			form.touched[grandparent][idx][parent] &&
			form.touched[grandparent][idx][parent][jdx] &&
			form.touched[grandparent][idx][parent][jdx][child] &&
			form.errors[grandparent] &&
			form.errors[grandparent][idx] &&
			form.errors[grandparent][idx][parent] &&
			form.errors[grandparent][idx][parent][jdx][child]
		) {
			feilmelding = form.errors[grandparent][idx][parent][jdx][child]
		}
	}

	return (
		<Datepicker
			name={field.name}
			childname={child}
			value={field.value}
			form={form}
			onChange={dato => {
				form.setFieldValue(field.name, dato)
				form.setFieldTouched(field.name, true)
			}}
			error={feilmelding ? { feilmelding: feilmelding } : null}
			{...restProps}
		/>
	)
}
