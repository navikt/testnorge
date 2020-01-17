import React, { useState } from 'react'
import Button from '~/components/ui/button/Button'
import { Textarea } from 'nav-frontend-skjema'

export const BeskrivelseEditor = ({ beskrivelse, handleSubmit, turnOffEditing }) => {
	const [value, setValue] = useState(beskrivelse || '')

	const handleChange = e => setValue(e.target.value)

	const focusEndOfStringHack = e => {
		e.target.value = ''
		e.target.value = value
	}

	return (
		<div className="beskrivelse-editor">
			<Textarea
				label=""
				placeholder="Skriv inn en kommentar"
				autoFocus
				onFocus={focusEndOfStringHack}
				value={value}
				onChange={e => setValue(e.target.value)}
				maxLength={1000}
			/>

			<div className="beskrivelse-button-container-editor">
				<Button onClick={turnOffEditing} className="beskrivelse-button">
					Avbryt
				</Button>
				<Button onClick={() => handleSubmit(value)} className="beskrivelse-button">
					Lagre
				</Button>
			</div>
		</div>
	)
}
