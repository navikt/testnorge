import * as React from 'react'
import Tooltip from 'rc-tooltip'
import { TooltipProps } from 'rc-tooltip/es/Tooltip'
import { CypressSelector } from '../../../../cypress/mocks/Selectors'

const DollyTooltip = ({
	overlay,
	children,
	dataCy = null as unknown as CypressSelector,
	...rest
}: TooltipProps) => {
	if (!overlay) {
		return <>{children}</>
	}
	return (
		<span data-cy={dataCy}>
			<Tooltip
				overlay={overlay}
				placement="top"
				mouseEnterDelay={0.1}
				mouseLeaveDelay={0.1}
				{...rest}
			>
				{children}
			</Tooltip>
		</span>
	)
}

DollyTooltip.displayname = 'DollyTooltip'

export default DollyTooltip
