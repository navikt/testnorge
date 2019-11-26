import React, { useState } from 'react'
import Button from '~/components/ui/button/Button'

export default function BeskrivelseEditor({ beskrivelse, updateBeskrivelse, toggleIsEditing }) {
	const [value, setValue] = useState(beskrivelse ? beskrivelse : '')

	const handleChange = event => {
		setValue(event.target.value)
	}

	const handleSubmit = () => {
		console.log('value :', value)
		console.log('toggleIsEditing :', toggleIsEditing)
		console.log('updateBeskrivelse :', updateBeskrivelse)
		updateBeskrivelse(value)
		toggleIsEditing()
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
