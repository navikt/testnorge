import { DollyModal } from '@/components/ui/modal/DollyModal'
import React, { useState } from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { v4 as _uuid } from 'uuid'
import ModalActionKnapper from '@/components/ui/modal/ModalActionKnapper'
import { DollyCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { useToggle } from 'react-use'
import dolly from '@/favicon.ico'
import Icon from '@/components/ui/icon/Icon'
import { useBrukerProfil, useBrukerProfilBilde, useCurrentBruker } from '@/utils/hooks/useBruker'
import { Textarea } from '@navikt/ds-react'
import { Logger } from '@/logger/Logger'
import { TestComponentSelectors } from '#/mocks/Selectors'

export const KontaktModal = ({ closeModal }) => {
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
		closeModal()
	}

	return (
		<ErrorBoundary>
			<DollyModal isOpen closeModal={closeModal} width="70%">
				<h1>Kontakt team Dolly</h1>
				<br />
				<p style={{ fontSize: '1.125rem', margin: '5px 0 25px 0', lineHeight: 'normal' }}>
					Her kan du sende oss tilbakemeldinger, komme med ønsker eller forslag til ny
					funksjonalitet, eller melde fra om feil og mangler. NB! Om du ønsker svar på meldingen kan
					du ikke velge å være anonym.
				</p>
				<div className="modal-content">
					{isAnonym ? (
						<Icon kind="user" className="bruker-ikon" />
					) : (
						<img alt="Profilbilde" src={brukerBilde || dolly} />
					)}

					<div className="modal-input">
						<Textarea
							data-testid={TestComponentSelectors.INPUT_KONTAKT_MODAL}
							value={melding}
							label={'Melding'}
							placeholder={'Forsøk å være så spesifikk som mulig'}
							maxLength={MAX_LENGTH}
							onChange={(event) => setMelding(event.target.value)}
							error={melding.length > MAX_LENGTH && 'Meldingen inneholder for mange tegn'}
							autoFocus
						/>
						<div className="skjemaelement textarea__container">
							<DollyCheckbox
								data-testid={TestComponentSelectors.CHECKBOX_KONTAKT_ANONYM}
								label="Jeg ønsker å være anonym"
								onChange={toggleAnonym}
							/>
						</div>
					</div>
				</div>
				<ModalActionKnapper
					data-testid={TestComponentSelectors.BUTTON_SEND_MELDING}
					submitknapp="Send melding"
					disabled={melding === '' || melding.length > MAX_LENGTH}
					onSubmit={sendMelding}
					onAvbryt={closeModal}
					center
				/>
			</DollyModal>
		</ErrorBoundary>
	)
}
