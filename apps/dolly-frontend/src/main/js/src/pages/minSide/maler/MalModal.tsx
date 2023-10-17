import DollyModal from '@/components/ui/modal/DollyModal'
import React, { useState } from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { TextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import ModalActionKnapper from '@/components/ui/modal/ModalActionKnapper'
import { Label } from '@/components/ui/form/inputs/label/Label'
import { DollyApi } from '@/service/Api'
import {
	REGEX_BACKEND_BESTILLINGER,
	REGEX_BACKEND_ORGANISASJONER,
	useMatchMutate,
} from '@/utils/hooks/useMutate'

export const MalModal = ({ id, erOrganisasjon, closeModal }) => {
	const [nyttMalnavn, setMalnavn] = useState('')
	const matchMutate = useMatchMutate()
	const lagreMal = () => {
		erOrganisasjon
			? DollyApi.lagreOrganisasjonMalFraBestillingId(id, nyttMalnavn).then(() =>
					matchMutate(REGEX_BACKEND_ORGANISASJONER)
			  )
			: DollyApi.lagreMalFraBestillingId(id, nyttMalnavn).then(() =>
					matchMutate(REGEX_BACKEND_BESTILLINGER)
			  )
		closeModal()
	}

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
						onSubmit={lagreMal}
						onAvbryt={closeModal}
						center
					/>
				</div>
			</DollyModal>
		</ErrorBoundary>
	)
}
