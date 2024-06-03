import * as React from 'react'
import * as RcTooltip from 'rc-tooltip'
import { CypressSelector } from '../../../../cypress/mocks/Selectors'
import { Tooltip, TooltipProps } from '@navikt/ds-react'

type DollyTooltipProps = TooltipProps & {
	useExternalTooltip?: boolean
	dataCy?: string
	align?: any
	overlayStyle?: any
}

const DollyTooltip = ({
	useExternalTooltip = false,
	content,
	children,
	dataCy = null as unknown as CypressSelector,
	...rest
}: DollyTooltipProps) => {
	if (!content) {
		return <>{children}</>
	}
	return useExternalTooltip ? (
		<span data-testid={dataCy}>
			<RcTooltip.default overlay={content} placement="top" {...rest}>
				{children}
			</RcTooltip.default>
		</span>
	) : (
		<span data-testid={dataCy}>
			<Tooltip content={content} placement="top" delay={0.1} {...rest}>
				{children}
			</Tooltip>
		</span>
	)
}

DollyTooltip.displayname = 'DollyTooltip'

export default DollyTooltip
