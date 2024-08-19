import React, { useContext } from 'react'
import { Dropdown, DropdownContext } from '@navikt/ds-react-internal'
import { useLocation, useNavigate } from 'react-router-dom'
import { FingerButtonIcon, TenancyIcon } from '@navikt/aksel-icons'

const DropdownToggle = () => {
	const context = useContext(DropdownContext)
	const { isOpen } = context
	const location = useLocation()
	const isAdmin =
		location?.pathname === '/admin/orgtilgang' ||
		location?.pathname === '/admin/levendearbeidsforhold'

	return (
		<Dropdown.Toggle className={isOpen || isAdmin ? 'dropdown-toggle active' : 'dropdown-toggle'}>
			<a className={isAdmin ? 'active' : ''} style={{ margin: 0, padding: '19px 10px' }}>
				Admin
			</a>
		</Dropdown.Toggle>
	)
}

export const AdminDropdown = () => {
	const navigate = useNavigate()

	return (
		<Dropdown>
			<DropdownToggle />
			<Dropdown.Menu placement="bottom-start">
				<Dropdown.Menu.List>
					<Dropdown.Menu.List.Item
						onClick={() => navigate('/admin/orgtilgang')}
						style={{ color: '#212529' }}
					>
						<TenancyIcon title="a11y-title" fontSize="1.5rem" />
						Organisasjon-tilgang
					</Dropdown.Menu.List.Item>
					<Dropdown.Menu.List.Item
						onClick={() => navigate('/admin/levendearbeidsforhold')}
						style={{ color: '#212529' }}
					>
						<FingerButtonIcon title="a11y-title" fontSize="1.5rem" />
						Levende arbeidsforhold
					</Dropdown.Menu.List.Item>
				</Dropdown.Menu.List>
			</Dropdown.Menu>
		</Dropdown>
	)
}
