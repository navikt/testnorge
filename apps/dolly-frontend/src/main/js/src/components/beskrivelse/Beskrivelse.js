import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import Loading from '~/components/ui/loading/Loading'

import { TextEditor } from '~/components/ui/form/inputs/textEditor/TextEditor'
import { REGEX_BACKEND_GRUPPER, useMatchMutate } from '~/utils/hooks/useMutate'

export const Beskrivelse = ({ ident, updateBeskrivelse, isUpdatingBeskrivelse, closeModal }) => {
	const matchMutate = useMatchMutate()
	if (isUpdatingBeskrivelse) {
		closeModal && closeModal()
		return <Loading label="oppdaterer beskrivelse" />
	}

	const handleSubmit = (value) => {
		updateBeskrivelse(ident.ident, value).then(() => matchMutate(REGEX_BACKEND_GRUPPER))
	}

	return (
		<React.Fragment>
			{ident.beskrivelse && <SubOverskrift label="Kommentarer" iconKind="kommentar" />}
			<TextEditor
				text={ident.beskrivelse}
				handleSubmit={handleSubmit}
				placeholder="Skriv inn kommentar"
			/>
		</React.Fragment>
	)
}
