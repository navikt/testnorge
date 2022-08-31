import React, { BaseSyntheticEvent, useState } from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import Hjelpetekst from '~/components/hjelpetekst'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import RedigerGruppeConnector from '~/components/redigerGruppe/RedigerGruppeConnector'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import Icon from '~/components/ui/icon/Icon'
import Liste from './Liste'
import FinnPersonBestillingConnector from '~/pages/gruppeOversikt/FinnPersonBestillingConnector'
import { useCurrentBruker } from '~/utils/hooks/useBruker'
import { useGrupper } from '~/utils/hooks/useGruppe'
import { useDispatch } from 'react-redux'
import { setSidetall } from '~/ducks/finnPerson'
import { PopoverOrientering } from 'nav-frontend-popover'

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
}

export default function GruppeOversikt({
	searchActive,
	sideStoerrelse,
	sidetall,
}: GruppeOversiktProps) {
	const {
		currentBruker: { brukerId, brukertype },
	} = useCurrentBruker()
	const [visning, setVisning] = useState(VisningType.MINE)
	const [visNyGruppeState, visNyGruppe, skjulNyGruppe] = useBoolean(false)
	const { grupper, loading } = useGrupper(visning === VisningType.MINE ? brukerId : null)
	const dispatch = useDispatch()

	const byttVisning = (event: BaseSyntheticEvent) => {
		setVisning(event.target.value)
		dispatch(setSidetall(0))
	}

	return (
		<div className="oversikt-container">
			<div className="toolbar">
				<div className="page-header flexbox--align-center--justify-start">
					<h1>Grupper</h1>
					<Hjelpetekst hjelpetekstFor="Grupper" type={PopoverOrientering.Under}>
						Gruppene inneholder alle personene dine (FNR/DNR/NPID).
					</Hjelpetekst>
				</div>
			</div>
			<div className="toolbar">
				<NavButton type="hoved" onClick={visNyGruppe} style={{ marginTop: '4px' }}>
					Ny gruppe
				</NavButton>
				<div style={{ marginTop: '9px' }}>
					<ToggleGruppe onChange={byttVisning} name="toggler">
						<ToggleKnapp value="mine" checked={visning === VisningType.MINE}>
							<Icon size={16} kind={visning === VisningType.MINE ? 'man2Light' : 'man2'} />
							Mine
						</ToggleKnapp>
						<ToggleKnapp value="alle" checked={visning === VisningType.ALLE}>
							<Icon size={16} kind={visning === VisningType.ALLE ? 'groupLight' : 'groupDark'} />
							Alle
						</ToggleKnapp>
					</ToggleGruppe>
				</div>
				{brukertype !== 'BANKID' && <FinnPersonBestillingConnector />}
			</div>

			{visNyGruppeState && <RedigerGruppeConnector onCancel={skjulNyGruppe} />}

			<Liste
				gruppeDetaljer={{ pageSize: sideStoerrelse }}
				items={grupper}
				isFetching={loading}
				searchActive={searchActive}
				visSide={sidetall}
				brukertype={brukertype}
			/>
		</div>
	)
}
