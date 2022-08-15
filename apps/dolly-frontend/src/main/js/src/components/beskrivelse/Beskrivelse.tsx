import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import Loading from '~/components/ui/loading/Loading'

import { TextEditor } from '~/components/ui/form/inputs/textEditor/TextEditor'
import { REGEX_BACKEND_GRUPPER, useMatchMutate } from '~/utils/hooks/useMutate'

type BeskrivelseProps = {
	ident: { beskrivelse: string; ident: number }
	isUpdatingBeskrivelse: boolean
	closeModal: () => void
	updateBeskrivelse: (arg0: any, arg1: string) => void
}
export const Beskrivelse = ({
	closeModal,
	ident: { beskrivelse: besk, ident },
	isUpdatingBeskrivelse,
	updateBeskrivelse,
}: BeskrivelseProps) => {
	const matchMutate = useMatchMutate()
	const [beskrivelse, setBeskrivelse] = React.useState(besk)

	if (isUpdatingBeskrivelse) {
		closeModal && closeModal()
		return <Loading label="oppdaterer beskrivelse" />
	}

	const handleSubmit = (value: string) => {
		matchMutate(REGEX_BACKEND_GRUPPER)
		updateBeskrivelse(ident, value)
		setBeskrivelse(value)
	}

	return (
		<React.Fragment>
			{beskrivelse && <SubOverskrift label="Kommentarer" iconKind="kommentar" />}
			<TextEditor
				text={beskrivelse}
				handleSubmit={handleSubmit}
				placeholder="Skriv inn kommentar"
			/>
		</React.Fragment>
	)
}
