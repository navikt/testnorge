import DollyModal from '@/components/ui/modal/DollyModal'
import React, { useState } from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { TextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import ModalActionKnapper from '@/components/ui/modal/ModalActionKnapper'
import { Label } from '@/components/ui/form/inputs/label/Label'
import { DollyApi } from '@/service/Api'
import { useCurrentBruker } from '@/utils/hooks/useBruker'

export const MalModal = ({ id, closeModal }) => {
	const lagreEndring = () => {
		DollyApi.endreMalNavn(id, nyttMalnavn).then(closeModal())
	}
	const { currentBruker } = useCurrentBruker()
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
							onChange={(e) => setMalnavn(e.target.value)}
							className="input--fullbredde"
						/>
					</Label>
					<ModalActionKnapper
						submitknapp="Lagre mal"
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
