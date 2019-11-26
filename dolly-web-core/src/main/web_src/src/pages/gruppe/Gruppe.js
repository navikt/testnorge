import React, { useState } from 'react'
import { useMount } from 'react-use'
import Knapp from 'nav-frontend-knapper'
import StatusListeConnector from '~/components/bestilling/statusListe/StatusListeConnector'
import Loading from '~/components/ui/loading/Loading'
import TestbrukerListeConnector from './TestbrukerListe/TestbrukerListeConnector'
import BestillingListeConnector from './BestillingListe/BestillingListeConnector'
import GruppeHeader from './GruppeHeader/GruppeHeader'
import Toolbar from '~/components/ui/toolbar/Toolbar'
import SearchFieldConnector from '~/components/searchField/SearchFieldConnector'

const VISNING_TESTPERSONER = 'testpersoner'
const VISNING_BESTILLING = 'bestilling'

export default function Gruppe({
	getGruppe,
	getBestillinger,
	gruppeArray,
	isFetching,
	deleteGruppe,
	antallBestillinger,
	isDeletingGruppe,
	match,
	history
}) {
	const [visning, setVisning] = useState(VISNING_TESTPERSONER)
	useMount(() => {
		getGruppe()
		getBestillinger()
	})

	if (isFetching) return <Loading label="Laster testpersoner" panel />

	if (!gruppeArray) return null

	const byttVisning = event => setVisning(event.target.value)

	const gruppe = gruppeArray[0]

	const toggleValues = [
		{
			value: VISNING_TESTPERSONER,
			label: `Testpersoner (${gruppe.identer ? gruppe.identer.length : 0})`
		},
		{
			value: VISNING_BESTILLING,
			label: `Bestillinger (${antallBestillinger})`
		}
	]

	const startBestilling = () => history.push(`/gruppe/${match.params.gruppeId}/bestilling`)

	const searchfieldPlaceholderSelector = () => {
		if (visning === VISNING_BESTILLING) return 'Søk i bestillinger'
		return 'Søk etter testpersoner'
	}

	return (
		<div className="gruppe-container">
			<GruppeHeader
				gruppe={gruppe}
				deleteGruppe={deleteGruppe}
				isDeletingGruppe={isDeletingGruppe}
			/>

			<StatusListeConnector gruppeId={gruppe.id} />

			<Toolbar
				searchField={<SearchFieldConnector placeholder={searchfieldPlaceholderSelector()} />}
				toggleOnChange={byttVisning}
				toggleCurrent={visning}
				toggleValues={toggleValues}
			>
				<Knapp type="hoved" onClick={startBestilling}>
					Opprett personer
				</Knapp>
			</Toolbar>

			{visning === VISNING_TESTPERSONER && <TestbrukerListeConnector gruppeId={gruppe.id}/>}
			{visning === VISNING_BESTILLING && <BestillingListeConnector gruppeId={gruppe.id} />}
		</div>
	)
}
