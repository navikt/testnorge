import React from 'react'
import Button from '~/components/ui/button/Button'

export const BeskrivelseFelt = ({
	beskrivelse = 'Fant ingen beskrivelser for denne testpersonen',
	turnOnEditing
}) => {
	return (
		<div className="beskrivelse-felt" onDoubleClick={turnOnEditing}>
			{beskrivelse}
			<div className="beskrivelse-button-container">
				<Button onClick={turnOnEditing} className="beskrivelse-button">
					Rediger
				</Button>
			</div>
		</div>
	)
}
