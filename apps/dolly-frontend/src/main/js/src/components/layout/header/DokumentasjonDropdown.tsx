import React from 'react'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { ActionMenu } from '@navikt/ds-react'
import { ActionMenuWrapper, DropdownStyledLink } from './ActionMenuWrapper'
import Icon from '@/components/ui/icon/Icon'

export const DokumentasjonDropdown = () => {
	const { currentBruker } = useCurrentBruker()
	const isDevVersion =
		window.location.hostname.includes('frontend') || window.location.hostname.includes('localhost')
	const apiUrl = isDevVersion
		? 'https://dolly-backend-dev.intern.dev.nav.no/swagger'
		: 'https://dolly-backend.intern.dev.nav.no/swagger'

	return (
		<ActionMenuWrapper title="Dokumentasjon">
			<ActionMenu.Item
				onClick={() =>
					window.open(
						'https://navikt.github.io/testnorge/docs/applications/dolly/brukerveiledning',
						'_blank',
						'noopener',
					)
				}
			>
				<Icon kind="file-new" fontSize="1.5rem" style={{ color: 'black' }} />
				<DropdownStyledLink
					href="https://navikt.github.io/testnorge/docs/applications/dolly/brukerveiledning"
					target="_blank"
					rel="noopener noreferrer"
				>
					Brukerdokumentasjon
				</DropdownStyledLink>
			</ActionMenu.Item>
			{currentBruker?.brukertype === 'AZURE' && (
				<ActionMenu.Item onClick={() => window.open(apiUrl, '_blank', 'noopener')}>
					<Icon kind="file-code" fontSize="1.5rem" style={{ color: 'black' }} />
					<DropdownStyledLink href={apiUrl} target="_blank" rel="noopener noreferrer">
						API-dokumentasjon
					</DropdownStyledLink>
				</ActionMenu.Item>
			)}
		</ActionMenuWrapper>
	)
}
