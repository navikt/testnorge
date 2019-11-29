import React, { useState } from 'react'
import Button from '~/components/ui/button/Button'

export const BeskrivelseEditor = ({ beskrivelse, handleSubmit, turnOffEditing }) => {
	const [value, setValue] = useState(beskrivelse || '')

	const handleChange = e => setValue(e.target.value)

	const focusEndOfStringHack = e => {
		e.target.value = ''
		e.target.value = value
	}

	return (
		<div className="beskrivelse-editor">
			<textarea
				className="beskrivelse-editor-textarea"
				type="text"
				autoFocus
				onFocus={focusEndOfStringHack}
				value={value}
				placeholder="Skriv inn en beskrivelse"
				onChange={handleChange}
			/>
			<br />

			<div className="beskrivelse-button-container">
				<Button onClick={turnOffEditing} className="beskrivelse-button">
					Avbryt
				</Button>
				<Button onClick={() => handleSubmit(value)} className="beskrivelse-button">
					Legg til
				</Button>
			</div>
		</div>
	)
}
