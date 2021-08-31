import DollyModal from '~/components/ui/modal/DollyModal'
import React from 'react'
import {ErrorBoundary} from '~/components/ui/appError/ErrorBoundary'
import {DollyTextInput} from '~/components/ui/form/inputs/textInput/TextInput'
import File from 'forhandsvisningsfil'
import ModalActionKnapper from '~/components/ui/modal/ModalActionKnapper'
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
		return fil.name == fil.id
	})
	const MAX_LENGTH = 50
	const [dokumenter, setDokumenter] = React.useState(filer)
	const [filnavn, setFilnavn] = React.useState(filer[aktivIndex] ? filer[aktivIndex].name : '')
	console.log('aktivIndex: ', aktivIndex) //TODO - SLETT MEG
	if (aktivIndex === -1) {
		return null
	}
	return (
		<ErrorBoundary>
			<DollyModal isOpen closeModal={closeModal} width="50%" overflow="auto">
				<h1>Endre dokument tittel</h1>
				<File file={filer[aktivIndex]} scale={1.5} />
				<br />
				<DollyTextInput
					name={null}
					value={filnavn}
					onChange={(event: { target: { value: string } }) => {
						console.log('dokumenter: ', dokumenter) //TODO - SLETT MEG
						setDokumenter(
							filer.map((fil, index) => {
								return index === aktivIndex
									? {
											...fil,
											name: filnavn
									  }
									: fil
							})
						)
						return setFilnavn(event.target.value)
					}}
					label={'Tittel på dokument'}
				/>
				<ModalActionKnapper
					submitknapp="Lagre tittel"
					disabled={filnavn === '' || filnavn.length > MAX_LENGTH}
					onSubmit={() => {
						handleChange(dokumenter)
						closeModal()
					}}
					// @ts-ignore
					onAvbryt={closeModal}
					center
				/>
			</DollyModal>
		</ErrorBoundary>
	)
}
