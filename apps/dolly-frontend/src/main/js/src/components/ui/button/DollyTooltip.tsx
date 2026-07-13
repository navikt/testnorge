import * as React from 'react'
import type { TooltipProps as RcTooltipProps } from 'rc-tooltip'
import * as RcTooltip from 'rc-tooltip'
import { Tooltip, TooltipProps } from '@navikt/ds-react'
import { TestComponentSelectors } from '#/mocks/Selectors'

type DollyTooltipProps = TooltipProps & {
	useExternalTooltip?: boolean
	testLocator?: string
	align?: any
	overlayStyle?: any
	externalTrigger?: RcTooltipProps['trigger']
	externalMouseEnterDelay?: RcTooltipProps['mouseEnterDelay']
	externalMouseLeaveDelay?: RcTooltipProps['mouseLeaveDelay']
	externalPopupVisible?: RcTooltipProps['popupVisible']
	externalOnVisibleChange?: RcTooltipProps['onVisibleChange']
}

const DollyTooltip = ({
	useExternalTooltip = false,
	content,
	children,
	testLocator = null as unknown as TestComponentSelectors,
	externalTrigger,
	externalMouseEnterDelay,
	externalMouseLeaveDelay,
	externalPopupVisible,
	externalOnVisibleChange,
	...rest
}: DollyTooltipProps) => {
	if (!content) {
		return <>{children}</>
	}
	return useExternalTooltip ? (
		<span data-testid={testLocator}>
			<RcTooltip.default
				overlay={content}
				placement="top"
				trigger={externalTrigger}
				mouseEnterDelay={externalMouseEnterDelay}
				mouseLeaveDelay={externalMouseLeaveDelay}
				popupVisible={externalPopupVisible}
				onVisibleChange={externalOnVisibleChange}
				{...rest}
			>
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
