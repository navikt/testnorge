import React, { useRef, useState } from 'react'
import { DollyApi } from '@/service/Api'
import {
	REGEX_BACKEND_BESTILLINGER,
	REGEX_BACKEND_GRUPPER,
	REGEX_BACKEND_ORGANISASJONER,
	useMatchMutate,
} from '@/utils/hooks/useMutate'
import { FormProvider, useForm } from 'react-hook-form'
import { Button, Dialog, TextField } from '@navikt/ds-react'

export const malTyper = {
	ORGANISASJON: 'ORGANISASJON',
	BESTILLING: 'BESTILLING',
	PERSON: 'PERSON',
}

export const MalModal = ({ id, malType, open, setOpen }) => {
	const [isLoading, setIsLoading] = useState(false)
	const [nyttMalnavn, setMalnavn] = useState('')
	const matchMutate = useMatchMutate()
	const formMethods = useForm()

	const inputRef = useRef<HTMLInputElement>(null)

	const lagreMal = () => {
		setIsLoading(true)
		switch (malType) {
			case malTyper.ORGANISASJON:
				DollyApi.lagreOrganisasjonMalFraBestillingId(id, nyttMalnavn)
					.then(() => matchMutate(REGEX_BACKEND_ORGANISASJONER))
					.then(setIsLoading(false))
					.then(setOpen(false))

				break
			case malTyper.BESTILLING:
				DollyApi.lagreMalFraBestillingId(id, nyttMalnavn)
					.then(() => matchMutate(REGEX_BACKEND_BESTILLINGER))
					.then(setIsLoading(false))
					.then(setOpen(false))
				break
			case malTyper.PERSON:
				DollyApi.opprettMalFraPerson(id, nyttMalnavn)
					.then(() => matchMutate(REGEX_BACKEND_GRUPPER))
					.then(setIsLoading(false))
					.then(setOpen(false))
				break
			default:
				setIsLoading(false)
				setOpen(false)
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
		<Dialog open={open} onOpenChange={setOpen}>
			<Dialog.Popup initialFocusTo={inputRef}>
				<Dialog.Header>
					<Dialog.Title>Opprett mal fra {topic}</Dialog.Title>
				</Dialog.Header>
				<Dialog.Body>
					<FormProvider {...formMethods}>
						<form id="mal-form" onSubmit={formMethods.handleSubmit(lagreMal)}>
							<TextField
								label="Navn på mal"
								onChange={(e) => setMalnavn(e.target.value)}
								ref={inputRef}
							/>
						</form>
					</FormProvider>
				</Dialog.Body>
				<Dialog.Footer>
					<Dialog.CloseTrigger>
						<Button type="button" variant="secondary">
							Avbryt
						</Button>
					</Dialog.CloseTrigger>
					<Button form="mal-form" loading={isLoading} disabled={nyttMalnavn === ''}>
						Lagre mal
					</Button>
				</Dialog.Footer>
			</Dialog.Popup>
		</Dialog>
	)
}
