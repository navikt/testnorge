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
	color: #212529;
	text-decoration: none;
	font-size: 1em;

	&:hover {
		background-color: unset;
	}

	padding: 0;

	&&& {
		margin: 0;
	}
`

interface ActionMenuProps {
	dataTestId?: string
	title: string
	children: React.ReactElement | React.ReactElement[]
	trigger?: React.ReactElement
	isActive?: boolean
}

export const ActionMenuWrapper: React.FC<ActionMenuProps> = ({
	title,
	isActive = false,
	dataTestId,
	children,
	trigger,
}) => {
	return (
		<div style={{ color: 'white', fontSize: '1.2em', margin: '0 10px' }}>
			<ActionMenu data-testid={dataTestId}>
				{trigger ? (
					trigger
				) : (
					<ActionMenu.Trigger
						className={isActive ? 'dropdown-toggle active' : 'dropdown-toggle'}
						style={{ margin: 0, padding: '19px 10px', color: 'white' }}
					>
						<InternalHeader.Button data-testid={dataTestId}>{title}</InternalHeader.Button>
					</ActionMenu.Trigger>
				)}
				<ActionMenu.Content>
					<ActionMenu.Group aria-label={title}>{children}</ActionMenu.Group>
				</ActionMenu.Content>
			</ActionMenu>
		</div>
	)
}
