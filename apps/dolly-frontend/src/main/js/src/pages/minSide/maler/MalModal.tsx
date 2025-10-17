import { DollyModal } from '@/components/ui/modal/DollyModal'
import React, { useState } from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { TextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import ModalActionKnapper from '@/components/ui/modal/ModalActionKnapper'
import { Label } from '@/components/ui/form/inputs/label/Label'
import { DollyApi } from '@/service/Api'
import {
	REGEX_BACKEND_BESTILLINGER,
	REGEX_BACKEND_GRUPPER,
	REGEX_BACKEND_ORGANISASJONER,
	useMatchMutate,
} from '@/utils/hooks/useMutate'
import { FormProvider, useForm } from 'react-hook-form'
import { TestComponentSelectors } from '#/mocks/Selectors'

export const malTyper = {
	ORGANISASJON: 'ORGANISASJON',
	BESTILLING: 'BESTILLING',
	PERSON: 'PERSON',
}

export const MalModal = ({ id, malType, closeModal }) => {
	const [nyttMalnavn, setMalnavn] = useState('')
	const matchMutate = useMatchMutate()
	const formMethods = useForm()

	const lagreMal = () => {
		switch (malType) {
			case malTyper.ORGANISASJON:
				DollyApi.lagreOrganisasjonMalFraBestillingId(id, nyttMalnavn).then(() =>
					matchMutate(REGEX_BACKEND_ORGANISASJONER),
				)
				closeModal()
				break
			case malTyper.BESTILLING:
				DollyApi.lagreMalFraBestillingId(id, nyttMalnavn).then(() =>
					matchMutate(REGEX_BACKEND_BESTILLINGER),
				)
				closeModal()
				break
			case malTyper.PERSON:
				DollyApi.opprettMalFraPerson(id, nyttMalnavn).then(() => matchMutate(REGEX_BACKEND_GRUPPER))
				closeModal()
				break
			default:
				closeModal()
				break
		}
	}

	let topic
	switch (malType) {
		case malTyper.ORGANISASJON:
			topic = 'organisasjon'
			break
		case malTyper.BESTILLING:
			topic = 'bestilling'
			break
		case malTyper.PERSON:
			topic = 'person'
			break
	}

	return (
		<ErrorBoundary>
			<FormProvider {...formMethods}>
				<DollyModal isOpen closeModal={closeModal} width="40%" overflow="auto">
					<div className="modal">
						<h1>Opprett mal fra {topic}</h1>
						<br />
						<Label name={'MalNavn'} label={'Navn på mal'}>
							<TextInput
								name="malnavn"
								onChange={(e) => setMalnavn(e.target.value)}
								className="input--fullbredde"
								autoFocus
								useControlled
							/>
						</Label>
						<ModalActionKnapper
							data-testid={TestComponentSelectors.BUTTON_MALMODAL_LAGRE}
							submitknapp="Lagre mal"
							disabled={nyttMalnavn === ''}
							onSubmit={formMethods.handleSubmit(lagreMal)}
							onAvbryt={closeModal}
							center
						/>
					</div>
				</DollyModal>
			</FormProvider>
		</ErrorBoundary>
	)
}
