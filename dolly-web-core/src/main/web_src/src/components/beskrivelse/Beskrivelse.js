import React, { useState } from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { BeskrivelseEditor } from './BeskrivelseEditor'
import { BeskrivelseFelt } from './BeskrivelseFelt'
import Loading from '~/components/ui/loading/Loading'

import './Beskrivelse.less'

export default function Beskrivelse({ gruppe, updateBeskrivelse, isUpdatingBeskrivelse }) {
	const [isEditing, turnOnEditing, turnOffEditing] = useBoolean(false)

	const { beskrivelse } = gruppe

	if (isUpdatingBeskrivelse) return <Loading label="oppdaterer beskrivelse" />

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
