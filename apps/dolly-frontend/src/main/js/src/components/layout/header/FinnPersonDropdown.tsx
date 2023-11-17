import { Dropdown, DropdownContext } from '@navikt/ds-react-internal'
import { useContext } from 'react'
import { useNavigate } from 'react-router-dom'
import Icon from '@/components/ui/icon/Icon'

const DropdownToggle = () => {
	const context = useContext(DropdownContext)
	const { isOpen } = context

	return (
		<Dropdown.Toggle
			className={isOpen ? 'dropdown-toggle active' : 'dropdown-toggle'}
			style={{ margin: '0 10px', padding: '20px 10px' }}
		>
			Finn person
		</Dropdown.Toggle>
	)
}
export const FinnPersonDropdown = () => {
	const navigate = useNavigate()

	return (
		<div style={{ color: 'white', fontSize: '1.2em', margin: '0 10px' }}>
			<Dropdown>
				<DropdownToggle />
				<Dropdown.Menu placement="bottom-start">
					<Dropdown.Menu.List>
						<Dropdown.Menu.List.Item
							onClick={() => navigate('/dollysoek')}
							style={{ color: '#212529' }}
						>
							<Icon kind="search" fontSize="1.5rem" />
							Søk i Dolly
						</Dropdown.Menu.List.Item>
						<Dropdown.Menu.List.Item
							onClick={() => navigate('/testnorge')}
							style={{ color: '#212529' }}
						>
							<Icon kind="search" fontSize="1.5rem" />
							Søk i Test-Norge
						</Dropdown.Menu.List.Item>
					</Dropdown.Menu.List>
				</Dropdown.Menu>
			</Dropdown>
		</div>
	)
}
