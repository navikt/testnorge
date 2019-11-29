import React, { useState, useEffect } from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import Knapp from 'nav-frontend-knapper'
import HjelpeTekst from 'nav-frontend-hjelpetekst'
import Overskrift from '~/components/ui/overskrift/Overskrift'
import SearchFieldConnector from '~/components/searchField/SearchFieldConnector'
import RedigerGruppeConnector from '~/components/redigerGruppe/RedigerGruppeConnector'
import Toolbar from '~/components/ui/toolbar/Toolbar'
import Liste from './Liste'

export default function GruppeOversikt({
	getGrupper,
	fetchMineGrupper,
	isFetching,
	gruppeListe,
	mineIds,
	history,
	searchActive
}) {
	const [visning, setVisning] = useState('mine')
	const [visNyGruppeState, visNyGruppe, skjulNyGruppe] = useBoolean(false)

	useEffect(() => {
		visning === 'mine' ? fetchMineGrupper() : getGrupper()
	}, [visning])

	const byttVisning = event => setVisning(event.target.value)

	const items = visning === 'mine' ? gruppeListe.filter(v => mineIds.includes(v.id)) : gruppeListe

	return (
		<div className="oversikt-container">
			<div className="page-header flexbox--align-center--justify-start">
				<Overskrift label="Testdatagrupper" />
				<HjelpeTekst>
					Testdatagruppen inneholder alle testpersonene dine (FNR/DNR/BOST).
				</HjelpeTekst>
			</div>

			<Toolbar
				toggleOnChange={byttVisning}
				toggleCurrent={visning}
				searchField={<SearchFieldConnector />}
			>
				<Knapp type="hoved" onClick={visNyGruppe}>
					Ny gruppe
				</Knapp>
			</Toolbar>

			{visNyGruppeState && <RedigerGruppeConnector onCancel={skjulNyGruppe} />}

			<Liste items={items} history={history} isFetching={isFetching} searchActive={searchActive} />
		</div>
	)
}
