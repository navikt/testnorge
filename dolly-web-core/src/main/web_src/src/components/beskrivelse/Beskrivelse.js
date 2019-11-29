import React from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { BeskrivelseEditor } from './BeskrivelseEditor'
import { BeskrivelseFelt } from './BeskrivelseFelt'
import Loading from '~/components/ui/loading/Loading'

import './Beskrivelse.less'

export const Beskrivelse = ({ ident, updateBeskrivelse, isUpdatingBeskrivelse }) => {
	const [isEditing, turnOnEditing, turnOffEditing] = useBoolean(false)

	if (isUpdatingBeskrivelse) return <Loading label="oppdaterer beskrivelse" />

	return (
		<div className="beskrivelse-visning">
			<SubOverskrift label="Kommentarer" />
			{isEditing ? (
				<BeskrivelseEditor
					turnOffEditing={turnOffEditing}
					beskrivelse={ident.beskrivelse}
					updateBeskrivelse={updateBeskrivelse}
				/>
			) : (
				<BeskrivelseFelt turnOnEditing={turnOnEditing} beskrivelse={ident.beskrivelse} />
			)}
		</div>
	)
}
