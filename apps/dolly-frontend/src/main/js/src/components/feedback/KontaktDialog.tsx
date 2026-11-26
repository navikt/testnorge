import React, { useState } from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { v4 as _uuid } from 'uuid'
import { useToggle } from 'react-use'
import dolly from '@/favicon.ico'
import Icon from '@/components/ui/icon/Icon'
import { useBrukerProfil, useBrukerProfilBilde, useCurrentBruker } from '@/utils/hooks/useBruker'
import { BodyLong, Button, Checkbox, Dialog, Textarea, VStack } from '@navikt/ds-react'
import { Logger } from '@/logger/Logger'
import { TestComponentSelectors } from '#/mocks/Selectors'

type Props = {
	closeDialog: () => void
}

export const KontaktDialog = ({ closeDialog }: Props) => {
	const { brukerBilde } = useBrukerProfilBilde()
	const { brukerProfil } = useBrukerProfil()
	const { currentBruker } = useCurrentBruker()

	const MAX_LENGTH = 2000
	const [uuid] = useState(_uuid())
	const [melding, setMelding] = useState('')
	const [isAnonym, toggleAnonym] = useToggle(false)

	const sendMelding = () => {
		Logger.log({
			event: `Melding fra Dollybruker`,
			message: melding,
			uuid: uuid,
			isAnonym: isAnonym,
			brukerType: currentBruker.brukertype,
			brukernavn: isAnonym ? null : brukerProfil?.visningsNavn,
			tilknyttetOrganisasjon: isAnonym ? null : brukerProfil?.organisasjon,
		})
		closeDialog()
	}

	return (
		<ErrorBoundary>
			<Dialog open onOpenChange={(open) => !open && closeDialog()}>
				<Dialog.Popup width="large" closeOnOutsideClick={false}>
					<Dialog.Header>
						<Dialog.Title>Kontakt team Dolly</Dialog.Title>
						<Dialog.Description>
							<BodyLong>
								Her kan du sende oss tilbakemeldinger, komme med ønsker eller forslag til ny
								funksjonalitet, eller melde fra om feil og mangler. NB! Om du ønsker svar på
								meldingen kan du ikke velge å være anonym.
							</BodyLong>
						</Dialog.Description>
					</Dialog.Header>
					<Dialog.Body>
						<div className="dialog-content">
							{isAnonym ? (
								<Icon kind="user" className="bruker-ikon" />
							) : (
								<img alt="Profilbilde" src={brukerBilde || dolly} />
							)}
							<div className="dialog-input">
								<VStack gap="space-12">
									<Textarea
										data-testid={TestComponentSelectors.INPUT_KONTAKT_DIALOG}
										value={melding}
										label="Melding"
										placeholder="Forsøk å være så spesifikk som mulig"
										maxLength={MAX_LENGTH}
										onChange={(event) => setMelding(event.target.value)}
										error={melding.length > MAX_LENGTH && 'Meldingen inneholder for mange tegn'}
										autoFocus
									/>
									<Checkbox
										data-testid={TestComponentSelectors.CHECKBOX_KONTAKT_ANONYM}
										checked={isAnonym}
										onChange={toggleAnonym}
									>
										Jeg ønsker å være anonym
									</Checkbox>
								</VStack>
							</div>
						</div>
					</Dialog.Body>
					<Dialog.Footer>
						<Dialog.CloseTrigger>
							<Button variant="secondary">Avbryt</Button>
						</Dialog.CloseTrigger>
						<Button
							data-testid={TestComponentSelectors.BUTTON_SEND_MELDING}
							variant="primary"
							disabled={melding === '' || melding.length > MAX_LENGTH}
							onClick={sendMelding}
						>
							Send melding
						</Button>
					</Dialog.Footer>
				</Dialog.Popup>
			</Dialog>
		</ErrorBoundary>
	)
}
