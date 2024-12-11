import React from 'react'
import { ActionMenu, InternalHeader } from '@navikt/ds-react'
import Icon from '@/components/ui/icon/Icon'
import styled from 'styled-components'

export const DropdownStyledIcon = styled(Icon)`
	&& {
		g {
			path,
			ellipse {
				stroke: #212529;
				stroke-width: 2;
			}
		}
	}
`
export const DropdownStyledLink = styled.a`
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

interface ActionMenuProps {
	title: string
	children: React.ReactElement
	trigger?: React.ReactElement
	isActive?: boolean
}

export const ActionMenuWrapper: React.FC<ActionMenuProps> = ({
	title,
	isActive = false,
	children,
	trigger,
}) => {
	return (
		<div style={{ color: 'white', fontSize: '1.2em', margin: '0 10px' }}>
			<ActionMenu>
				{trigger ? (
					trigger
				) : (
					<ActionMenu.Trigger
						className={isActive ? 'dropdown-toggle active' : 'dropdown-toggle'}
						style={{ margin: 0, padding: '19px 10px' }}
					>
						<InternalHeader.Button>{title}</InternalHeader.Button>
					</ActionMenu.Trigger>
				)}
				<ActionMenu.Content placement="bottom-start">
					<ActionMenu.Group aria-label={title}>{children}</ActionMenu.Group>
				</ActionMenu.Content>
			</ActionMenu>
		</div>
	)
}
