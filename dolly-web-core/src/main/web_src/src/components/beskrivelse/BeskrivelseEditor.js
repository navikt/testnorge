import React, { useState } from 'react'
import Button from '~/components/ui/button/Button'

export const BeskrivelseEditor = ({ beskrivelse, updateBeskrivelse, turnOffEditing }) => {
	const [value, setValue] = useState(beskrivelse || '')

	const handleChange = event => {
		setValue(event.target.value)
	}

	const handleSubmit = () => {
		updateBeskrivelse(value)
		turnOffEditing()
	}

	return (
		<div className="beskrivelse-editor">
			<form>
				<textarea
					className="beskrivelse-editor-textarea"
					type="text"
					value={value}
					placeholder="Skriv inn en beskrivelse"
					onChange={handleChange}
				/>
				<br />

				<Button onClick={handleSubmit} className="beskrivelse-button-leggtil">
					Legg til
				</Button>
			</form>
		</div>
	)
}
