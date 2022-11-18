import React, { useState } from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import RedigerGruppeConnector from '~/components/redigerGruppe/RedigerGruppeConnector'
import Icon from '~/components/ui/icon/Icon'
import Liste from './Liste'
import FinnPersonBestillingConnector from '~/pages/gruppeOversikt/FinnPersonBestillingConnector'
import { useCurrentBruker } from '~/utils/hooks/useBruker'
import { useGrupper } from '~/utils/hooks/useGruppe'
import { useDispatch } from 'react-redux'
import { setSidetall } from '~/ducks/finnPerson'
import { Hjelpetekst } from '~/components/hjelpetekst/Hjelpetekst'
import { bottom } from '@popperjs/core'
import { ToggleGroup } from '@navikt/ds-react'
import styled from 'styled-components'

type GruppeOversiktProps = {
	importerteZIdenter: any
	sidetall: number
	sideStoerrelse: number
	gruppeInfo: any
	searchActive: boolean
}

enum VisningType {
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
		visning === VisningType.ALLE ? null : brukerId
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
				<div className="page-header flexbox--align-center--justify-start">
					<h1>Grupper</h1>
					<Hjelpetekst placement={bottom}>
						Gruppene inneholder alle personene dine (FNR/DNR/NPID).
					</Hjelpetekst>
				</div>
			</div>
			<div className="toolbar gruppe--full">
				<StyledNavButton variant="primary" onClick={visNyGruppe}>
					Ny gruppe
				</StyledNavButton>
				{!bankIdBruker && <FinnPersonBestillingConnector />}
			</div>
			{!bankIdBruker && (
				<StyledDiv className="gruppe--flex-column-center">
					<ToggleGroup value={visning} onChange={byttVisning} size={'small'}>
						<StyledToggleItem value={VisningType.MINE}>
							<Icon size={16} kind={visning === VisningType.MINE ? 'man2Light' : 'man2'} />
							Mine
						</StyledToggleItem>
						<StyledToggleItem value={VisningType.FAVORITTER}>
							<Icon
								size={16}
								kind={visning === VisningType.FAVORITTER ? 'starLight' : 'starDark'}
							/>
							Favoritter
						</StyledToggleItem>
						<StyledToggleItem value={VisningType.ALLE}>
							<Icon size={16} kind={visning === VisningType.ALLE ? 'groupLight' : 'groupDark'} />
							Alle
						</StyledToggleItem>
					</ToggleGroup>
				</StyledDiv>
			)}

			{visNyGruppeState && <RedigerGruppeConnector onCancel={skjulNyGruppe} />}

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
			/>
		</div>
	)
}
export default GruppeOversikt
