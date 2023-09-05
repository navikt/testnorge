import React, { useState } from 'react'
import useBoolean from '@/utils/hooks/useBoolean'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import RedigerGruppeConnector from '@/components/redigerGruppe/RedigerGruppeConnector'
import Icon from '@/components/ui/icon/Icon'
import Liste from './Liste'
import FinnPersonBestillingConnector from '@/pages/gruppeOversikt/FinnPersonBestillingConnector'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { useGrupper } from '@/utils/hooks/useGruppe'
import { useDispatch } from 'react-redux'
import { setSidetall } from '@/ducks/finnPerson'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { bottom } from '@popperjs/core'
import { ToggleGroup } from '@navikt/ds-react'
import styled from 'styled-components'
import { CypressSelector } from '../../../cypress/mocks/Selectors'

type GruppeOversiktProps = {
	importerteZIdenter: any
	sidetall: number
	sideStoerrelse: number
	gruppeInfo: any
	searchActive: boolean
}

export enum VisningType {
	MINE = 'mine',
	ALLE = 'alle',
	FAVORITTER = 'favoritter',
}

const StyledToggleItem = styled(ToggleGroup.Item)`
	&& {
		padding-right: 13px;
	}
`

const StyledNavButton = styled(NavButton)`
	&& {
		min-width: 200px;
	}
`

const StyledDiv = styled.div`
	&& {
		margin-bottom: 10px;
	}
`

const GruppeOversikt = ({ searchActive, sideStoerrelse, sidetall }: GruppeOversiktProps) => {
	const {
		currentBruker: { brukerId, brukertype },
	} = useCurrentBruker()
	const [visning, setVisning] = useState(VisningType.MINE)
	const [visNyGruppeState, visNyGruppe, skjulNyGruppe] = useBoolean(false)
	const { grupper, loading } = useGrupper(
		sidetall,
		sideStoerrelse,
		visning === VisningType.ALLE ? null : brukerId,
	)
	const dispatch = useDispatch()

	const byttVisning = (value: VisningType) => {
		setVisning(value)
		dispatch(setSidetall(0))
	}

	const bankIdBruker = brukertype === 'BANKID'

	return (
		<div className="oversikt-container">
			<div className="toolbar">
				<div
					data-cy={CypressSelector.TITLE_VISNING}
					className="page-header flexbox--align-center--justify-start"
				>
					<h1>Grupper</h1>
					<Hjelpetekst placement={bottom}>
						Gruppene inneholder alle personene dine (FNR/DNR/NPID).
					</Hjelpetekst>
				</div>
			</div>
			<div className="toolbar gruppe--full">
				<StyledNavButton
					data-cy={CypressSelector.BUTTON_NY_GRUPPE}
					variant="primary"
					onClick={visNyGruppe}
				>
					Ny gruppe
				</StyledNavButton>
				{!bankIdBruker && <FinnPersonBestillingConnector />}
			</div>

			{visNyGruppeState && <RedigerGruppeConnector onCancel={skjulNyGruppe} />}

			{!bankIdBruker && (
				<StyledDiv className="gruppe--flex-column-center">
					<ToggleGroup
						value={visning}
						onChange={byttVisning}
						size={'small'}
						style={{ backgroundColor: '#ffffff' }}
					>
						<StyledToggleItem data-cy={CypressSelector.TOGGLE_MINE} value={VisningType.MINE}>
							<Icon kind={'designsystem-man-silhouette'} />
							Mine
						</StyledToggleItem>
						<StyledToggleItem
							data-cy={CypressSelector.TOGGLE_FAVORITTER}
							value={VisningType.FAVORITTER}
						>
							<Icon kind={'designsystem-star-light'} />
							Favoritter
						</StyledToggleItem>
						<StyledToggleItem data-cy={CypressSelector.TOGGLE_ALLE} value={VisningType.ALLE}>
							<Icon kind={'designsystem-group-light'} />
							Alle
						</StyledToggleItem>
					</ToggleGroup>
				</StyledDiv>
			)}

			<Liste
				gruppeDetaljer={{
					pageSize: sideStoerrelse,
					antallPages:
						visning === VisningType.FAVORITTER
							? grupper?.favoritter?.length / sideStoerrelse
							: grupper?.antallPages,
					antallElementer:
						visning === VisningType.FAVORITTER
							? grupper?.favoritter?.length
							: grupper?.antallElementer,
				}}
				items={visning === VisningType.FAVORITTER ? grupper?.favoritter : grupper?.contents}
				isFetching={loading}
				searchActive={searchActive}
				visSide={sidetall}
				visningType={visning}
			/>
		</div>
	)
}
export default GruppeOversikt
