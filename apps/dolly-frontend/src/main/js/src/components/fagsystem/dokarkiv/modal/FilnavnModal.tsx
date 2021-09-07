import React from 'react'
import {ErrorBoundary} from '~/components/ui/appError/ErrorBoundary'
import {DollyTextInput} from '~/components/ui/form/inputs/textInput/TextInput'
import {Vedlegg} from '~/components/fagsystem/dokarkiv/form/scanning/DokarkivForm'

type Data = {
	filer: Vedlegg[]
	closeModal: Function
	handleChange: Function
}
export const FilnavnModal = ({ filer, closeModal, handleChange }: Data) => {
	if (!filer || filer.length < 1) {
		return null
	}
	const aktivIndex = filer.findIndex(fil => {
		return !fil.dokNavn
	})

	const MAX_LENGTH = 50
	const [filnavn, setFilnavn] = React.useState(
		filer[aktivIndex] && filer[aktivIndex].name ? filer[aktivIndex].name.replace('.pdf', '') : ''
	)
	if (aktivIndex === -1) {
		return null
	}

	function handleSubmit() {
		handleChange(
			filer.map((fil, index) =>
				index === aktivIndex
					? {
							...fil,
							dokNavn: filnavn
					  }
					: fil
			)
		)
	}

	return (
		<ErrorBoundary>
			<DollyTextInput
				name={null}
				value={filnavn}
				onChange={(event: { target: { value: string } }) => setFilnavn(event.target.value)}
				label={`Tittel på dokument #`}
			/>
		</ErrorBoundary>
	)
}
