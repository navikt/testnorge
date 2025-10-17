import React, { useCallback, useEffect, useMemo, useState } from 'react'
import useBoolean from '@/utils/hooks/useBoolean'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import Icon from '@/components/ui/icon/Icon'
import Liste from './Liste'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { useGrupper } from '@/utils/hooks/useGruppe'
import { useDispatch, useSelector } from 'react-redux'
import { setSidetall, setVisning } from '@/ducks/finnPerson'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { bottom } from '@popperjs/core'
import { ToggleGroup } from '@navikt/ds-react'
import styled from 'styled-components'
import { TestComponentSelectors } from '#/mocks/Selectors'
import FinnPersonBestilling from '@/pages/gruppeOversikt/FinnPersonBestilling'
import { RedigerGruppe } from '@/components/redigerGruppe/RedigerGruppe'

type RootState = {
	search: any
	finnPerson: {
		sidetall: number
		sideStoerrelse: number
	}
}

export const enum VisningType {
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

const GruppeOversikt: React.FC = () => {
	const searchActive = useSelector((state: RootState) => Boolean(state.search))
	const sidetall = useSelector((state: RootState) => state.finnPerson.sidetall)
	const sideStoerrelse = useSelector((state: RootState) => state.finnPerson.sideStoerrelse)

	const dispatch = useDispatch()
	const { currentBruker } = useCurrentBruker()
	const { brukerId, brukertype } = currentBruker
	const teamId = currentBruker?.representererTeam?.brukerId

	const [visningType, setVisningType] = useState<VisningType>(VisningType.MINE)
	const [visNyGruppeState, visNyGruppe, skjulNyGruppe] = useBoolean(false)

	const { grupper, loading } = useGrupper(
		sidetall,
		sideStoerrelse,
		visningType === VisningType.ALLE ? null : (teamId ?? brukerId),
	)

	useEffect(() => {
		dispatch(setVisning('personer'))
	}, [dispatch])

	const isBankIdBruker = useMemo(() => brukertype === 'BANKID', [brukertype])

	const gruppeDetaljer = useMemo(
		() => ({
			pageSize: sideStoerrelse,
			antallPages:
				visningType === VisningType.FAVORITTER
					? Math.ceil((grupper?.favoritter?.length || 0) / sideStoerrelse)
					: grupper?.antallPages,
			antallElementer:
				visningType === VisningType.FAVORITTER
					? grupper?.favoritter?.length
					: grupper?.antallElementer,
		}),
		[grupper, sideStoerrelse, visningType],
	)

	const items = useMemo(
		() => (visningType === VisningType.FAVORITTER ? grupper?.favoritter : grupper?.contents),
		[grupper, visningType],
	)

	const handleVisningChange = useCallback(
		(value: VisningType) => {
			setVisningType(value)
			dispatch(setSidetall(0))
		},
		[dispatch],
	)

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
				{!isBankIdBruker && <FinnPersonBestilling />}
			</div>

			{visNyGruppeState && <RedigerGruppe onCancel={skjulNyGruppe} />}

			<StyledDiv className="gruppe--flex-column-center">
				<ToggleGroup
					value={visningType}
					onChange={handleVisningChange}
					size="small"
					style={{ backgroundColor: '#ffffff' }}
				>
					<StyledToggleItem
						data-testid={TestComponentSelectors.TOGGLE_MINE}
						value={VisningType.MINE}
					>
						<Icon kind="man-silhouette" />
						Mine
					</StyledToggleItem>
					<StyledToggleItem
						data-testid={TestComponentSelectors.TOGGLE_FAVORITTER}
						value={VisningType.FAVORITTER}
					>
						<Icon kind="star-light" />
						Favoritter
					</StyledToggleItem>
					<StyledToggleItem
						data-testid={TestComponentSelectors.TOGGLE_ALLE}
						value={VisningType.ALLE}
					>
						<Icon kind="group-light" />
						Alle
					</StyledToggleItem>
				</ToggleGroup>
			</StyledDiv>

			<Liste
				gruppeDetaljer={gruppeDetaljer}
				items={items}
				isFetching={loading}
				searchActive={searchActive}
				visSide={sidetall}
				visningType={visningType}
			/>
		</div>
	)
}

export default GruppeOversikt
