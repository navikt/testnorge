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
				<p>Dokumentasjon</p>
				<div className={'linkPanel-container'}>
					<LinkPanel
						className={'linkPanel'}
						target="_blank"
						href="https://navikt.github.io/testnorge/applications/dolly/"
					>
						<LinkPanel.Title>Dokumentasjon</LinkPanel.Title>
					</LinkPanel>
				</div>
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
						<LinkPanel.Title>API-dok</LinkPanel.Title>
					</LinkPanel>
				</div>
			</div>
		</div>
	)
}
