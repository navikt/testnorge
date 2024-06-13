import * as React from 'react'
import * as RcTooltip from 'rc-tooltip'
import { Tooltip, TooltipProps } from '@navikt/ds-react'
import { TestComponentSelectors } from '#/mocks/Selectors'

type DollyTooltipProps = TooltipProps & {
	useExternalTooltip?: boolean
	testLocator?: string
	align?: any
	overlayStyle?: any
}

const DollyTooltip = ({
	useExternalTooltip = false,
	content,
	children,
	testLocator = null as unknown as TestComponentSelectors,
	...rest
}: DollyTooltipProps) => {
	if (!content) {
		return <>{children}</>
	}
	return useExternalTooltip ? (
		<span data-testid={testLocator}>
			<RcTooltip.default overlay={content} placement="top" {...rest}>
				{children}
			</RcTooltip.default>
		</span>
	) : (
		<span data-testid={testLocator}>
			<Tooltip content={content} placement="top" delay={0.1} {...rest}>
				{children}
			</Tooltip>
		</span>
	)
}

DollyTooltip.displayname = 'DollyTooltip'

export default DollyTooltip
