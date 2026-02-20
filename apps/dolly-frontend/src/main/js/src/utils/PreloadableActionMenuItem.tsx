import React from 'react'
import { useNavigate } from 'react-router'
import { ActionMenu } from '@navikt/ds-react'
import { preloadComponentOnRoute } from '@/allRoutes'

interface PreloadableActionMenuItemProps {
	route: string
	style?: React.CSSProperties
	dataTestId?: string
	children: React.ReactNode
}

export const PreloadableActionMenuItem: React.FC<PreloadableActionMenuItemProps> = ({
	route,
	style,
	dataTestId,
	children,
}) => {
	const navigate = useNavigate()

	return (
		<ActionMenu.Item
			onClick={() => navigate(route)}
			onMouseOver={() => preloadComponentOnRoute(route)}
			style={style}
			data-testid={dataTestId}
		>
			{children}
		</ActionMenu.Item>
	)
}
