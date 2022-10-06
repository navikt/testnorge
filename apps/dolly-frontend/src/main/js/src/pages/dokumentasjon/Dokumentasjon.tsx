import React from 'react'
import Title from '~/components/Title'
import './Dokumentasjon.less'
import { LinkPanel } from '@navikt/ds-react'
import { useCurrentBruker } from '~/utils/hooks/useBruker'

export default () => {
	const {
		currentBruker: { brukertype },
	} = useCurrentBruker()

	return (
		<div>
			<Title title={'Dokumentasjon'} />
			<div className={'dokumentasjon-tekst'}>
				<p>Trykk på knappen under for å gå direkte til Dolly brukerdokumentasjon.</p>
				<div className={'linkPanel-container'}>
					<LinkPanel
						className={'linkPanel'}
						target="_blank"
						href="https://navikt.github.io/testnorge/applications/dolly/"
					>
						<LinkPanel.Title>Brukerdokumentasjon</LinkPanel.Title>
					</LinkPanel>
				</div>
			</div>
			{brukertype === 'AZURE' && (
				<div className={'dokumentasjon-tekst'} style={{ marginTop: '10px' }}>
					<p>Trykk på knappen under for å gå direkte til Dolly API-dokumentasjon.</p>
					<div className={'linkPanel-container'}>
						<LinkPanel
							className={'linkPanel'}
							target="_blank"
							href={
								window.location.hostname.includes('frontend')
									? 'https://dolly-backend-dev.dev.intern.nav.no/swagger'
									: 'https://dolly-backend.dev.intern.nav.no/swagger'
							}
						>
							<LinkPanel.Title>API-dokumentasjon</LinkPanel.Title>
						</LinkPanel>
					</div>
				</div>
			)}
		</div>
	)
}
