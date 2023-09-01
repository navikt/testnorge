import { CopyButton, Tooltip } from '@navikt/ds-react'

import styled from 'styled-components'

type Props = {
	copyText: string
	displayText?: string
	tooltipText: string
}

const StyledTooltip = styled(Tooltip)`
	cursor: pointer;
`

const StyledDiv = styled.div`
	display: flex;
	align-items: center;
`

export const DollyCopyButton = ({ copyText, displayText, tooltipText }: Props) => {
	return (
		<StyledDiv>
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
