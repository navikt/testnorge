import React from 'react'
import Button from '~/components/ui/button/Button'

export const BeskrivelseFelt = ({
	beskrivelse = 'Fant ingen beskrivelser for denne personen',
	turnOnEditing
}) => {
	return (
		<div className="beskrivelse-felt" onClick={turnOnEditing}>
			{beskrivelse || 'Skriv inn en kommentar (max 1000 chars)'}
			<div className="beskrivelse-button-container">
				<Button onClick={turnOnEditing} className="beskrivelse-button" label="">
					Rediger
				</Button>
			</div>
		</div>
	)
}
