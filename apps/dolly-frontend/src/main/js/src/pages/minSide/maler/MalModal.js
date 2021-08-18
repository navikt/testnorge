import DollyModal from '~/components/ui/modal/DollyModal'
import React, { useState } from 'react'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { TextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { api } from '~/pages/minSide/maler/api'
import ModalActionKnapper from '~/components/ui/modal/ModalActionKnapper'
import { Label } from '~/components/ui/form/inputs/label/Label'

export const MalModal = ({ id, closeModal }) => {
	const lagreEndring = () => {
		api.endreMalNavn(id, nyttMalnavn).then(closeModal())
	}

	const [nyttMalnavn, setMalnavn] = useState('')
	return (
		<ErrorBoundary>
			<DollyModal isOpen closeModal={closeModal} width="40%" overflow="auto">
				<div className="modal">
					<h1>Opprett ny mal</h1>

					<br />
					<Label name={'MalNavn'} label={'Navn på mal'}>
						<TextInput
							name="malnavn"
							onChange={e => setMalnavn(e.target.value)}
							className="input--fullbredde"
						/>
					</Label>
					<ModalActionKnapper
						submitknapp="Lagre Mal"
						disabled={nyttMalnavn === ''}
						onSubmit={lagreEndring}
						onAvbryt={closeModal}
						center
					/>
				</div>
			</DollyModal>
		</ErrorBoundary>
	)
}
