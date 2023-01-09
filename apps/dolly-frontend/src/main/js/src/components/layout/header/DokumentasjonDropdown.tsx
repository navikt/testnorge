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
			style={{ margin: '0 10px', padding: '20px 10px' }}
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
	const {
		currentBruker: { brukertype },
	} = useCurrentBruker()

	return (
		<div style={{ color: 'white', fontSize: '1.2em', margin: '0 10px' }}>
			<Dropdown>
				<DropdownToggle />
				<Dropdown.Menu placement="bottom-end">
					<Dropdown.Menu.List>
						<Dropdown.Menu.List.Item
							onClick={() =>
								window.open(
									'https://navikt.github.io/testnorge/applications/dolly/',
									'_blank',
									'noopener'
								)
							}
						>
							<Icon kind="fileNew2" size={16} />
							<StyledA>Brukerdokumentasjon</StyledA>
						</Dropdown.Menu.List.Item>
						{brukertype === 'AZURE' && (
							<Dropdown.Menu.List.Item
								onClick={() =>
									window.open(
										window.location.hostname.includes('frontend')
											? 'https://dolly-backend-dev.dev.intern.nav.no/swagger'
											: 'https://dolly-backend.dev.intern.nav.no/swagger',
										'_blank',
										'noopener'
									)
								}
							>
								<Icon kind="fileCode" size={16} />
								<StyledA>API-dokumentasjon</StyledA>
							</Dropdown.Menu.List.Item>
						)}
					</Dropdown.Menu.List>
				</Dropdown.Menu>
			</Dropdown>
		</div>
	)
}
