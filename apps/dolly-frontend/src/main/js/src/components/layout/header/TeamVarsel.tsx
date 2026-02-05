import React from 'react'
import { Button } from '@navikt/ds-react'
import { InformationSquareIcon } from '@navikt/aksel-icons'
import { useDailyComponent } from '@/utils/hooks/useDailyComponent'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import styled from 'styled-components'

const TeamVarselPanel = styled.div`
	position: fixed;
	top: 80px;
	right: 20px;
	background-color: var(--ax-bg-accent-strong);
	color: white;
	padding: 20px 20px 25px 20px;
	border-radius: 8px;
	width: 300px;
	box-shadow: var(--ax-shadow-dialog);
	z-index: 8;

	p {
		margin-bottom: 20px;
		margin-top: 10px;
		line-height: normal;
	}

	&& {
		.aksel-button {
			background-color: var(--ax-bg-default);
			color: var(--ax-bg-accent-strong);
			&:hover {
				background-color: var(--ax-bg-accent-moderate-hover);
			}
		}
	}
`

export const teamVarslingLocalStorageKey = 'team-varsling'

export const TeamVarsel = () => {
	const { isVisible, dismiss } = useDailyComponent(teamVarslingLocalStorageKey)
	const { currentBruker } = useCurrentBruker()

	if (!isVisible || !currentBruker?.representererTeam) {
		return null
	}

	return (
		<TeamVarselPanel>
			<div
				style={{
					justifyContent: 'center',
					display: 'grid',
				}}
			>
				<InformationSquareIcon fontSize="2.5rem" style={{ justifySelf: 'center' }} />
				<p>
					Du representer nå teamet {currentBruker.representererTeam?.navn}, og alle data du
					oppretter vil tilhøre dette teamet. Hvem du vil represente velges i menyen over.
				</p>
				<Button onClick={dismiss}>OK</Button>
			</div>
		</TeamVarselPanel>
	)
}
