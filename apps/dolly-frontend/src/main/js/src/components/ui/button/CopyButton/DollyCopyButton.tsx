import { CopyButton, Tooltip } from '@navikt/ds-react'

import styled from 'styled-components'
import { CSSProperties } from 'react'

type Props = {
	copyText: string
	displayText?: string
	tooltipText: string
	style?: CSSProperties
}

const StyledTooltip = styled(Tooltip)`
	cursor: pointer;
`

const StyledDiv = styled.div`
	display: flex;
	align-items: center;
`

export const DollyCopyButton = ({ copyText, displayText, tooltipText, style }: Props) => {
	return (
		<StyledDiv style={style}>
			{displayText}
			<StyledTooltip content={tooltipText}>
				<CopyButton
					onClick={(event) => event.stopPropagation()}
					size={'xsmall'}
					copyText={copyText}
					variant={'action'}
				/>
			</StyledTooltip>
		</StyledDiv>
	)
}
