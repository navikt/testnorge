import React, { useContext } from 'react'
import './Header.less'
import { Dropdown, DropdownContext } from '@navikt/ds-react-internal'
import Icon from '@/components/ui/icon/Icon'
import styled from 'styled-components'
import { useNavigate } from 'react-router-dom'
import { CypressSelector } from '../../../../cypress/mocks/Selectors'

const DropdownToggle = () => {
	const context = useContext(DropdownContext)
	const { isOpen } = context

	return (
		<Dropdown.Toggle
			className={isOpen ? 'dropdown-toggle active' : 'dropdown-toggle'}
			style={{ margin: 0, padding: '19px 10px' }}
			data-cy={CypressSelector.BUTTON_HEADER_ORGANISASJONER}
		>
			Organisasjoner
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

export const OrganisasjonDropdown = () => {
	const navigate = useNavigate()

	return (
		<div style={{ color: 'white', fontSize: '1.2em', margin: '0 10px' }}>
			<Dropdown>
				<DropdownToggle />
				<Dropdown.Menu placement="bottom-start">
					<Dropdown.Menu.List>
						<Dropdown.Menu.List.Item
							data-cy={CypressSelector.BUTTON_HEADER_OPPRETT_ORGANISASJONER}
							onClick={() => navigate('/organisasjoner')}
							style={{ color: '#212529' }}
						>
							<Icon kind="organisasjon" fontSize="1.5rem" />
							<StyledA>Mine organisasjoner</StyledA>
						</Dropdown.Menu.List.Item>
						<Dropdown.Menu.List.Item
							onClick={() => navigate('/tenor/organisasjoner')}
							data-cy={CypressSelector.BUTTON_HEADER_TENOR_ORGANISASJONER}
							style={{ color: '#212529' }}
						>
							<Icon kind="search" fontSize="1.5rem" />
							<StyledA>Søk i Tenor</StyledA>
						</Dropdown.Menu.List.Item>
					</Dropdown.Menu.List>
				</Dropdown.Menu>
			</Dropdown>
		</div>
	)
}
