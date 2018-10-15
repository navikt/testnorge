import React from 'react'
import { MONTHS } from './DatepickerLocalization'

export default function YearMonthForm({ date, localeUtils, onChange, fromMonth, toMonth }) {
	let years = []
	for (let i = fromMonth.getFullYear(); i <= toMonth.getFullYear(); i += 1) {
		years.push(i)
	}

	years = years.reverse()

	const save = (year, month) => onChange(new Date(year, month))

	const handleMonthChange = e => save(date.getFullYear(), e.target.value)
	const handleYearChange = e => save(e.target.value, date.getMonth())

	return (
		<div className="DayPicker-Caption">
			<select name="month" onChange={handleMonthChange} value={date.getMonth()}>
				{MONTHS.map((month, i) => (
					<option key={month} value={i}>
						{month}
					</option>
				))}
			</select>
			<select name="year" onChange={handleYearChange} value={date.getFullYear()}>
				{years.map(year => (
					<option key={year} value={year}>
						{year}
					</option>
				))}
			</select>
		</div>
	)
}
