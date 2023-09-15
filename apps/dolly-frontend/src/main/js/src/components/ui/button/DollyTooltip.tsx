import * as React from 'react'
import { CypressSelector } from '../../../../cypress/mocks/Selectors'
import { Tooltip, TooltipProps } from '@navikt/ds-react'

const DollyTooltip = ({
	content,
	children,
	// @ts-ignore
	dataCy = null as unknown as CypressSelector,
	...rest
}: TooltipProps) => {
	if (!content) {
		return <>{children}</>
	}
	return (
		<span data-cy={dataCy}>
			<Tooltip content={content} placement="top" delay={0.1} {...rest}>
				{children}
			</Tooltip>
		</span>
	)
}

DollyTooltip.displayname = 'DollyTooltip'

export default DollyTooltip
