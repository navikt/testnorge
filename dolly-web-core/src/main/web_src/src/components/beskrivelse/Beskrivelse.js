import React, { useState } from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import BeskrivelseEditor from './BeskrivelseEditor'
import BeskrivelseFelt from './BeskrivelseFelt'

import './Beskrivelse.less'

export default function Beskrivelse({ beskrivelse, updateBeskrivelse }) {
	const [isEditing, setIsEditing] = useState(false)

	const toggleIsEditing = () => {
		setIsEditing(!isEditing)
	}

	return (
		<div className="beskrivelse-visning">
			<SubOverskrift label="Kommentarer" />
			{isEditing ? (
				<BeskrivelseEditor
					toggleIsEditing={toggleIsEditing}
					beskrivelse={beskrivelse}
					updateBeskrivelse={updateBeskrivelse}
				/>
			) : (
				<BeskrivelseFelt
					toggleIsEditing={toggleIsEditing}
					beskrivelse={beskrivelse ? beskrivelse : 'Fant ingen beskrivelser for denne testpersonen'}
				/>
			)}
		</div>
	)
}
