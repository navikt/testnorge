import * as React from 'react'
import Tooltip from 'rc-tooltip'
import { TooltipProps } from 'rc-tooltip/es/Tooltip'

const DollyTooltip = ({ overlay, children, ...rest }: TooltipProps) => (
	<Tooltip overlay={overlay} placement="top" mouseEnterDelay={0.1} mouseLeaveDelay={0.1} {...rest}>
		{children}
	</Tooltip>
)

DollyTooltip.displayname = 'DollyTooltip'

export default DollyTooltip
