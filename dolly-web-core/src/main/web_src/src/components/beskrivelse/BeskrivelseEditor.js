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

	const handleCancel = () => {
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

				<div className="beskrivelse-button-container">
					<Button onClick={handleCancel} className="beskrivelse-button">
						Avbryt
					</Button>
					<Button onClick={handleSubmit} className="beskrivelse-button">
						Legg til
					</Button>
				</div>
			</form>
		</div>
	)
}
