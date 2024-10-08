import { Dropdown, DropdownContext } from '@navikt/ds-react-internal'
import { useContext } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import Icon from '@/components/ui/icon/Icon'
import { TestComponentSelectors } from '#/mocks/Selectors'

const DropdownToggle = () => {
	const context = useContext(DropdownContext)
	const { isOpen } = context
	const location = useLocation()
	const isFinnPerson =
		location?.pathname === '/dollysoek' ||
		location?.pathname === '/testnorge' ||
		location?.pathname === '/tenor/personer/'

	return (
		<Dropdown.Toggle
			data-testid={TestComponentSelectors.BUTTON_HEADER_FINNPERSON}
			className={isOpen || isFinnPerson ? 'dropdown-toggle active' : 'dropdown-toggle'}
		>
			<a className={isFinnPerson ? 'active' : ''} style={{ margin: 0, padding: '19px 10px' }}>
				Finn&nbsp;person
			</a>
		</Dropdown.Toggle>
	)
}

export const FinnPersonDropdown = () => {
	const navigate = useNavigate()

	return (
		<Dropdown>
			<DropdownToggle />
			<Dropdown.Menu placement="bottom-start">
				<Dropdown.Menu.List>
					<Dropdown.Menu.List.Item
						data-testid={TestComponentSelectors.BUTTON_HEADER_DOLLYSOEK}
						onClick={() => navigate('/dollysoek')}
						style={{ color: '#212529' }}
					>
						<Icon kind="search" fontSize="1.5rem" />
						Søk i Dolly
					</Dropdown.Menu.List.Item>
					<Dropdown.Menu.List.Item
						data-testid={TestComponentSelectors.BUTTON_HEADER_TESTNORGE}
						onClick={() => navigate('/testnorge')}
						style={{ color: '#212529' }}
					>
						<Icon kind="search" fontSize="1.5rem" />
						Søk i Test-Norge
					</Dropdown.Menu.List.Item>
					<Dropdown.Menu.List.Item
						data-testid={TestComponentSelectors.BUTTON_HEADER_TENOR}
						onClick={() => navigate('/tenor/personer/')}
						style={{ color: '#212529' }}
					>
						<Icon kind="search" fontSize="1.5rem" />
						Søk i Tenor
					</Dropdown.Menu.List.Item>
					<Dropdown.Menu.List.Item
						onClick={() => navigate('/nyansettelser/')}
						style={{ color: '#212529' }}
					>
						<Icon kind="ansettelse" fontSize="1.5rem" />
						Vis nyansettelser
					</Dropdown.Menu.List.Item>
				</Dropdown.Menu.List>
			</Dropdown.Menu>
		</Dropdown>
	)
}
