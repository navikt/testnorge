import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import Loading from '@/components/ui/loading/Loading'

import { TextEditor } from '@/components/ui/form/inputs/textEditor/TextEditor'
import { REGEX_BACKEND_GRUPPER, useMatchMutate } from '@/utils/hooks/useMutate'
import React from 'react'

type BeskrivelseProps = {
	ident: { beskrivelse: string; ident: number }
	isUpdatingBeskrivelse: boolean
	closeModal: () => void
	updateBeskrivelse: (arg0: any, arg1: string) => Promise<any>
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
		return <Loading label="oppdaterer kommentar" />
	}

	const handleSubmit = (value: string) => {
		updateBeskrivelse(ident, value).then(() => matchMutate(REGEX_BACKEND_GRUPPER))
		setBeskrivelse(value)
	}

	return (
		<React.Fragment>
			{beskrivelse && <SubOverskrift label="Kommentarer" iconKind="designsystem-kommentar" />}
			<TextEditor
				text={beskrivelse}
				handleSubmit={handleSubmit}
				placeholder="Skriv inn kommentar (notat)"
			/>
		</React.Fragment>
	)
}
