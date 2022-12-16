import React, { useContext } from 'react'
import './Header.less'
import { useCurrentBruker } from '~/utils/hooks/useBruker'
import { Dropdown, DropdownContext } from '@navikt/ds-react-internal'
import Icon from '~/components/ui/icon/Icon'
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
	color: #0067c5 !important;
	text-decoration: none;
	font-size: 1em !important;
	&:hover {
		background-color: #ebfcff !important;
	}
	padding: 0 !important;
`

export const DokumentasjonDropdown = () => {
	const {
		currentBruker: { brukertype },
	} = useCurrentBruker()

	return (
		<div style={{ color: 'white', fontSize: '1.2em' }}>
			<Dropdown>
				<DropdownToggle />
				<Dropdown.Menu>
					<Dropdown.Menu.List>
						<Dropdown.Menu.List.Item>
							<Icon kind="fileNew2" size={16} />
							<StyledA
								target="_blank"
								href="https://navikt.github.io/testnorge/applications/dolly/"
							>
								Brukerdokumentasjon
							</StyledA>
						</Dropdown.Menu.List.Item>
						{brukertype === 'AZURE' && (
							<Dropdown.Menu.List.Item>
								<Icon kind="fileCode" size={16} />
								<StyledA
									target="_blank"
									href={
										window.location.hostname.includes('frontend')
											? 'https://dolly-backend-dev.dev.intern.nav.no/swagger'
											: 'https://dolly-backend.dev.intern.nav.no/swagger'
									}
								>
									API-dokumentasjon
								</StyledA>
							</Dropdown.Menu.List.Item>
						)}
					</Dropdown.Menu.List>
				</Dropdown.Menu>
			</Dropdown>
		</div>
	)
}
