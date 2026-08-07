import React, { useRef, useState } from 'react'
import { useAlleBrukere } from '@/utils/hooks/useBruker'
import { DollyApi } from '@/service/Api'
import { REGEX_BACKEND_GRUPPER, useMatchMutate } from '@/utils/hooks/useMutate'
import { Button, Dialog, InlineMessage, UNSAFE_Combobox, VStack } from '@navikt/ds-react'

export const EndreTilknytningModal = ({ gruppe, open, setOpen }) => {
	const { brukere, loading: loadingBrukere } = useAlleBrukere()
	const [valgtBruker, setValgtBruker] = useState(gruppe.opprettetAv?.brukerId)
	const [feilmelding, setFeilmelding] = useState('')
	const [isLoading, setIsLoading] = useState(false)

	const brukerOptions = brukere?.map((bruker) => {
		const erTeam = bruker?.brukertype === 'TEAM'
		return {
			value: bruker?.brukerId,
			label: bruker?.brukernavn + (erTeam ? ' (team)' : ''),
		}
	})

	const inputRef = useRef<HTMLInputElement>(null)

	const matchMutate = useMatchMutate()

	const handleSubmit = (event: any) => {
		event.preventDefault()
		setFeilmelding('')
		setIsLoading(true)
		DollyApi.endreTilknytningGruppe(gruppe.id, valgtBruker)
			.then(() => matchMutate(REGEX_BACKEND_GRUPPER))
			.then(() => setOpen(false))
			.catch((error: any) => {
				setFeilmelding('Feil ved endring av eier: ' + error.message)
			})
			.finally(() => setIsLoading(false))
	}

	const handleOpenChange = (isOpen: boolean) => {
		if (!isLoading) {
			setOpen(isOpen)
			setFeilmelding('')
		}
	}

	return (
		<Dialog open={open} onOpenChange={handleOpenChange}>
			<Dialog.Popup initialFocusTo={inputRef}>
				<Dialog.Header>
					<Dialog.Title>
						Bytt eier for gruppe #{gruppe.id} - {gruppe.navn}
					</Dialog.Title>
				</Dialog.Header>
				<Dialog.Body>
					<form id="bytt-eier-form" onSubmit={handleSubmit}>
						<VStack gap="space-16">
							<UNSAFE_Combobox
								label="Ny eier"
								options={brukerOptions ?? []}
								isLoading={loadingBrukere}
								onToggleSelected={(option, isSelected) => {
									setValgtBruker(isSelected ? option : null)
								}}
								ref={inputRef}
								placeholder={loadingBrukere ? 'Laster brukere ...' : 'Velg bruker ...'}
							/>
							{feilmelding && <InlineMessage status="error">{feilmelding}</InlineMessage>}
						</VStack>
					</form>
				</Dialog.Body>
				<Dialog.Footer>
					<Dialog.CloseTrigger>
						<Button type="button" variant="secondary">
							Avbryt
						</Button>
					</Dialog.CloseTrigger>
					<Button form="bytt-eier-form" loading={isLoading}>
						Bytt eier
					</Button>
				</Dialog.Footer>
			</Dialog.Popup>
		</Dialog>
	)
}
