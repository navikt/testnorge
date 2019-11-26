import React from 'react'
import Button from '~/components/ui/button/Button'

export const BeskrivelseFelt = ({
	beskrivelse = 'Fant ingen beskrivelser for denne testpersonen',
	turnOnEditing
}) => {
	return (
		<div className="beskrivelse-felt">
			<div>
				{beskrivelse}
				<Button onClick={turnOnEditing} className="beskrivelse-button-leggtil">
					Rediger
				</Button>
			</div>
		</div>
	)
}
