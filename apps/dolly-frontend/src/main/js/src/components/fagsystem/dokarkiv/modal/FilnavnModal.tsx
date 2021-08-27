import DollyModal from '~/components/ui/modal/DollyModal'
import React from 'react'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { DollyTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import File from 'forhandsvisningsfil'
import ModalActionKnapper from '~/components/ui/modal/ModalActionKnapper'

export const FilnavnModal = ({ filer, closeModal }) => {
	if (!filer || filer.length < 1) {
		closeModal()
		return null
	}
	const MAX_LENGTH = 50
	const [filnavn, setFilnavn] = React.useState(filer[0].name)
	return (
		<ErrorBoundary>
			<DollyModal isOpen closeModal={closeModal} width="50%" overflow="auto">
				<h1>Endre Filnavn</h1>
				<File file={filer[0]} scale={2} />
				<br />
				<DollyTextInput
					name={null}
					value={filnavn}
					onChange={event => {
						filer[0].name = event.target.value
						return setFilnavn(event.target.value)
					}}
					label={'Tittel på dokument'}
				/>
				<ModalActionKnapper
					submitknapp="Lagre tittel"
					disabled={filnavn === '' || filnavn.length > MAX_LENGTH}
					onSubmit={null}
					onAvbryt={closeModal}
					center
				/>
			</DollyModal>
		</ErrorBoundary>
	)
}
