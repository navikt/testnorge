import React, { useEffect, useState } from 'react'
import useBoolean from '@/utils/hooks/useBoolean'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import Icon from '@/components/ui/icon/Icon'
import Liste from './Liste'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { useGrupper } from '@/utils/hooks/useGruppe'
import { useDispatch } from 'react-redux'
import { setSidetall, setVisning } from '@/ducks/finnPerson'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { bottom } from '@popperjs/core'
import { ToggleGroup } from '@navikt/ds-react'
import styled from 'styled-components'
import { TestComponentSelectors } from '#/mocks/Selectors'
import FinnPersonBestilling from '@/pages/gruppeOversikt/FinnPersonBestilling'
import { RedigerGruppeNy } from '@/components/redigerGruppe/RedigerGruppeNy'

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
	const [visningType, setVisningType] = useState(VisningType.MINE)
	const [visNyGruppeState, visNyGruppe, skjulNyGruppe] = useBoolean(false)
	const { grupper, loading } = useGrupper(
		sidetall,
		sideStoerrelse,
		visningType === VisningType.ALLE ? null : brukerId,
	)
	const dispatch = useDispatch()

	useEffect(() => {
		dispatch(setVisning('personer'))
	}, [])

	const byttVisning = (value: VisningType) => {
		setVisningType(value)
		dispatch(setSidetall(0))
	}

	const bankIdBruker = brukertype === 'BANKID'

	return (
		<div className="oversikt-container">
			<div className="toolbar">
				<div
					data-testid={TestComponentSelectors.TITLE_VISNING}
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
					data-testid={TestComponentSelectors.BUTTON_NY_GRUPPE}
					variant="primary"
					onClick={visNyGruppe}
				>
					Ny gruppe
				</StyledNavButton>
				{!bankIdBruker && <FinnPersonBestilling />}
			</div>

			{visNyGruppeState && <RedigerGruppeNy onCancel={skjulNyGruppe} />}

			{!bankIdBruker && (
				<StyledDiv className="gruppe--flex-column-center">
					<ToggleGroup
						value={visningType}
						onChange={byttVisning}
						size={'small'}
						style={{ backgroundColor: '#ffffff' }}
					>
						<StyledToggleItem
							data-testid={TestComponentSelectors.TOGGLE_MINE}
							value={VisningType.MINE}
						>
							<Icon kind={'man-silhouette'} />
							Mine
						</StyledToggleItem>
						<StyledToggleItem
							data-testid={TestComponentSelectors.TOGGLE_FAVORITTER}
							value={VisningType.FAVORITTER}
						>
							<Icon kind={'star-light'} />
							Favoritter
						</StyledToggleItem>
						<StyledToggleItem
							data-testid={TestComponentSelectors.TOGGLE_ALLE}
							value={VisningType.ALLE}
						>
							<Icon kind={'group-light'} />
							Alle
						</StyledToggleItem>
					</ToggleGroup>
				</StyledDiv>
			)}

			<Liste
				gruppeDetaljer={{
					pageSize: sideStoerrelse,
					antallPages:
						visningType === VisningType.FAVORITTER
							? grupper?.favoritter?.length / sideStoerrelse
							: grupper?.antallPages,
					antallElementer:
						visningType === VisningType.FAVORITTER
							? grupper?.favoritter?.length
							: grupper?.antallElementer,
				}}
				items={visningType === VisningType.FAVORITTER ? grupper?.favoritter : grupper?.contents}
				isFetching={loading}
				searchActive={searchActive}
				visSide={sidetall}
				visningType={visningType}
			/>
		</div>
	)
}
export default GruppeOversikt
