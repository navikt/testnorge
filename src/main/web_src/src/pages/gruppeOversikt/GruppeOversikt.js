import React, { useState, useEffect } from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import Hjelpetekst from '~/components/hjelpetekst'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import { SearchField } from '~/components/searchField/SearchField'
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
	mineIds,
	history,
	searchActive,
	importerteZIdenter
}) {
	const [visning, setVisning] = useState('mine')
	const [importerte, setImporterte] = useState(importerteZIdenter)
	const [visNyGruppeState, visNyGruppe, skjulNyGruppe] = useBoolean(false)

	useEffect(() => {
		visning === 'mine' ? fetchMineGrupper() : getGrupper()
	}, [visning])

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
				<FinnPerson naviger={navigerTilPerson} />
			</div>

			<div className="toolbar">
				<NavButton type="hoved" onClick={visNyGruppe}>
					Ny gruppe
				</NavButton>
				<ToggleGruppe onChange={byttVisning} name="toggler">
					<ToggleKnapp value="mine" checked={visning === 'mine'}>
						<Icon size={14} kind={visning === 'mine' ? 'man2Light' : 'man2'} />
						Mine
					</ToggleKnapp>
					<ToggleKnapp value="alle" checked={visning === 'alle'}>
						<Icon size={16} kind={visning === 'alle' ? 'groupLight' : 'groupDark'} />
						Alle
					</ToggleKnapp>
				</ToggleGruppe>
				<SearchField />
			</div>

			{visNyGruppeState && <RedigerGruppeConnector onCancel={skjulNyGruppe} />}

			<Liste items={items} history={history} isFetching={isFetching} searchActive={searchActive} />
		</div>
	)
}
