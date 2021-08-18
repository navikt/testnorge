import React, { useEffect, useState } from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import Hjelpetekst from '~/components/hjelpetekst'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import RedigerGruppeConnector from '~/components/redigerGruppe/RedigerGruppeConnector'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import Icon from '~/components/ui/icon/Icon'
import Liste from './Liste'
import FinnPerson from './FinnPerson'

export default function GruppeOversikt({
	getGrupper,
	navigerTilPerson,
	fetchMineGrupper,
	isFetching,
	gruppeListe,
	gruppeInfo,
	mineIds,
	history,
	searchActive,
	importerteZIdenter
}) {
	const [visning, setVisning] = useState('mine')
	const [sidetall, setSidetall] = useState(0)
	const [sideStoerrelse, setSideStoerrelse] = useState(10)
	const [importerte, setImporterte] = useState(importerteZIdenter)
	const [visNyGruppeState, visNyGruppe, skjulNyGruppe] = useBoolean(false)

	useEffect(() => {
		visning === 'mine' ? fetchMineGrupper() : getGrupper(sidetall, sideStoerrelse)
	}, [visning, sidetall, sideStoerrelse])

	const byttVisning = event => setVisning(event.target.value)

	if (importerteZIdenter !== importerte) {
		fetchMineGrupper()
		setImporterte(importerteZIdenter)
	}

	const items = visning === 'mine' ? gruppeListe.filter(v => mineIds.includes(v.id)) : gruppeListe

	return (
		<div className="oversikt-container">
			<div className="toolbar">
				<div className="page-header flexbox--align-center--justify-start">
					<h1>Testdatagrupper</h1>
					<Hjelpetekst hjelpetekstFor="Testdatagrupper">
						Testdatagruppen inneholder alle personene dine (FNR/DNR/BOST).
					</Hjelpetekst>
				</div>
			</div>
			<div className="toolbar">
				<NavButton type="hoved" onClick={visNyGruppe}>
					Ny gruppe
				</NavButton>
				<ToggleGruppe onChange={byttVisning} name="toggler">
					<ToggleKnapp value="mine" checked={visning === 'mine'}>
						<Icon size={16} kind={visning === 'mine' ? 'man2Light' : 'man2'} />
						Mine
					</ToggleKnapp>
					<ToggleKnapp value="alle" checked={visning === 'alle'}>
						<Icon size={16} kind={visning === 'alle' ? 'groupLight' : 'groupDark'} />
						Alle
					</ToggleKnapp>
				</ToggleGruppe>
				<FinnPerson naviger={navigerTilPerson} />
			</div>

			{visNyGruppeState && <RedigerGruppeConnector onCancel={skjulNyGruppe} />}

			<Liste
				gruppeDetaljer={visning === 'alle' ? gruppeInfo : { pageSize: sideStoerrelse }}
				items={items}
				history={history}
				isFetching={isFetching}
				searchActive={searchActive}
				visSide={sidetall}
				setSidetall={setSidetall}
				sideStoerrelse={sideStoerrelse}
				setSideStoerrelse={setSideStoerrelse}
			/>
		</div>
	)
}
