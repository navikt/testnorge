import React, { useState } from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { BeskrivelseEditor } from './BeskrivelseEditor'
import { BeskrivelseFelt } from './BeskrivelseFelt'

import './Beskrivelse.less'

export default function Beskrivelse({ gruppe, updateBeskrivelse }) {
	const [isEditing, turnOnEditing, turnOffEditing] = useBoolean(false)

	const { beskrivelse } = gruppe

	return (
		<div className="beskrivelse-visning">
			<SubOverskrift label="Kommentarer" />
			{isEditing ? (
				<BeskrivelseEditor
					turnOffEditing={turnOffEditing}
					beskrivelse={beskrivelse}
					updateBeskrivelse={updateBeskrivelse}
				/>
			) : (
				<BeskrivelseFelt turnOnEditing={turnOnEditing} beskrivelse={beskrivelse} />
			)}
		</div>
	)
}
