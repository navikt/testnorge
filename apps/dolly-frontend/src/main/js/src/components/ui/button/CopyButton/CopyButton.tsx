import React from 'react'
import Tooltip from 'rc-tooltip'
import Icon from '~/components/ui/icon/Icon'
// @ts-ignore
import { CopyToClipboard } from 'react-copy-to-clipboard'
import './CopyButton.less'
import styled from 'styled-components'

type Props = {
	value: string
}

const StyledTooltip = styled(Tooltip)`
	cursor: pointer;
`

export const CopyButton = ({ value }: Props) => {
	return (
		<div className={'copyContainer'}>
			{value}
			<CopyToClipboard text={value}>
				<StyledTooltip
					overlay={'Kopier'}
					placement="top"
					destroyTooltipOnHide={true}
					mouseEnterDelay={0}
					mouseLeaveDelay={0.1}
					arrowContent={<div className="rc-tooltip-arrow-inner" />}
					align={{
						offset: [0, -10],
					}}
				>
					<div
						className="icon"
						onClick={(event) => {
							event.stopPropagation()
						}}
					>
						<Icon kind="copy" size={15} />
					</div>
				</StyledTooltip>
			</CopyToClipboard>
		</div>
	)
}
