import React, { useContext } from 'react'
import './Header.less'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { Dropdown, DropdownContext } from '@navikt/ds-react-internal'
import Icon from '@/components/ui/icon/Icon'
import styled from 'styled-components'

const DropdownToggle = () => {
	const context = useContext(DropdownContext)
	const { isOpen } = context

	return (
		<Dropdown.Toggle
			className={isOpen ? 'dropdown-toggle active' : 'dropdown-toggle'}
			style={{ margin: 0, padding: '19px 10px' }}
		>
			Dokumentasjon
		</Dropdown.Toggle>
	)
}

const StyledA = styled.a`
	color: #212529 !important;
	text-decoration: none;
	font-size: 1em !important;

	&:hover {
		background-color: #ebfcff !important;
	}

	padding: 0 !important;

	&&& {
		margin: 0;
	}
`

export const DokumentasjonDropdown = () => {
	const { currentBruker } = useCurrentBruker()

	return (
		<div style={{ color: 'white', fontSize: '1.2em', margin: '0 10px' }}>
			<Dropdown>
				<DropdownToggle />
				<Dropdown.Menu placement="bottom-start">
					<Dropdown.Menu.List>
						<Dropdown.Menu.List.Item
							onClick={() =>
								window.open(
									'https://navikt.github.io/testnorge/docs/applications/dolly/brukerveiledning',
									'_blank',
									'noopener',
								)
							}
						>
							<Icon kind="file-new" fontSize={'1.5rem'} style={{ color: 'black' }} />
							<StyledA>Brukerdokumentasjon</StyledA>
						</Dropdown.Menu.List.Item>
						{currentBruker?.brukertype === 'AZURE' && (
							<Dropdown.Menu.List.Item
								onClick={() =>
									window.open(
										window.location.hostname.includes('frontend') ||
											window.location.hostname.includes('localhost')
											? 'https://dolly-backend-dev.intern.dev.nav.no/swagger'
											: 'https://dolly-backend.intern.dev.nav.no/swagger',
										'_blank',
										'noopener',
									)
								}
							>
								<Icon kind="file-code" fontSize={'1.5rem'} style={{ color: 'black' }} />
								<StyledA>API-dokumentasjon</StyledA>
							</Dropdown.Menu.List.Item>
						)}
					</Dropdown.Menu.List>
				</Dropdown.Menu>
			</Dropdown>
		</div>
	)
}
