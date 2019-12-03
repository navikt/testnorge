import React, { useState } from 'react'
import { useMount } from 'react-use'
import useBoolean from '~/utils/hooks/useBoolean'
import Knapp from 'nav-frontend-knapper'
import StatusListeConnector from '~/components/bestilling/statusListe/StatusListeConnector'
import Loading from '~/components/ui/loading/Loading'
import PersonListeConnector from './PersonListe/PersonListeConnector'
import BestillingListeConnector from './BestillingListe/BestillingListeConnector'
import GruppeHeader from './GruppeHeader/GruppeHeader'
import Toolbar from '~/components/ui/toolbar/Toolbar'
import { SearchField } from '~/components/searchField/SearchField'
import { BestillingsveilederModal } from '~/components/bestillingsveileder/startModal/StartModal'

const VISNING_PERSONER = 'personer'
const VISNING_BESTILLING = 'bestilling'

export default function Gruppe({
	getGruppe,
	deleteGruppe,
	getBestillinger,
	gruppe,
	identer,
	isFetching,
	isDeletingGruppe,
	match,
	history
}) {
	const [visning, setVisning] = useState(VISNING_PERSONER)
	const [startBestillingAktiv, visStartBestilling, skjulStarBestilling] = useBoolean(false)
	useMount(() => {
		getGruppe()
		getBestillinger()
	})

	if (isFetching) return <Loading label="Laster personer" panel />

	if (!gruppe) return null

	const byttVisning = event => setVisning(event.target.value)

	const identArray = Object.values(identer)

	const toggleValues = [
		{
			value: VISNING_PERSONER,
			label: `Personer (${identArray.length})`
		},
		{
			value: VISNING_BESTILLING,
			label: `Bestillinger (${identArray.map(b => b.bestillingId).flat().length})`
		}
	]

	const startBestilling = values =>
		history.push(`/gruppe/${match.params.gruppeId}/bestilling`, values)

	const searchfieldPlaceholderSelector = () => {
		if (visning === VISNING_BESTILLING) return 'Søk i bestillinger'
		return 'Søk etter personer'
	}

	return (
		<div className="gruppe-container">
			<GruppeHeader
				gruppe={gruppe}
				identArray={identArray}
				deleteGruppe={deleteGruppe}
				isDeletingGruppe={isDeletingGruppe}
			/>

			<StatusListeConnector gruppeId={gruppe.id} />

			<Toolbar
				searchField={<SearchField placeholder={searchfieldPlaceholderSelector()} />}
				toggleOnChange={byttVisning}
				toggleCurrent={visning}
				toggleValues={toggleValues}
			>
				<Knapp type="hoved" onClick={visStartBestilling}>
					Opprett personer
				</Knapp>
			</Toolbar>
			{startBestillingAktiv && (
				<BestillingsveilederModal onSubmit={startBestilling} onAvbryt={skjulStarBestilling} />
			)}

			{visning === VISNING_PERSONER && <PersonListeConnector />}
			{visning === VISNING_BESTILLING && <BestillingListeConnector />}
		</div>
	)
}
