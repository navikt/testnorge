import React, { useState } from 'react'
import { useMount } from 'react-use'
import useBoolean from '~/utils/hooks/useBoolean'
import StatusListeConnector from '~/components/bestilling/statusListe/StatusListeConnector'
import Loading from '~/components/ui/loading/Loading'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import PersonListeConnector from './PersonListe/PersonListeConnector'
import BestillingListeConnector from './BestillingListe/BestillingListeConnector'
import GruppeHeader from './GruppeHeader/GruppeHeader'
import Toolbar from '~/components/ui/toolbar/Toolbar'
import { SearchField } from '~/components/searchField/SearchField'
import { BestillingsveilederModal } from '~/components/bestillingsveileder/startModal/StartModal'
import Icon from '~/components/ui/icon/Icon'

const VISNING_PERSONER = 'personer'
const VISNING_BESTILLING = 'bestilling'

export default function Gruppe({
	getGruppe,
	deleteGruppe,
	getBestillinger,
	gruppe,
	identer,
	zBruker,
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

	if (visning == 'personer') {
		var iconTypeMan = 'manLight'
		var iconTypeBestilling = 'bestilling'
	} else {
		iconTypeMan = 'man'
		iconTypeBestilling = 'bestillingLight'
	}

	const byttVisning = event => setVisning(event.target.value)

	const identArray = Object.values(identer)

	const toggleValues = [
		{
			value: VISNING_PERSONER,
			label: `Personer (${identArray.length})`,
			icon: <Icon size={13} kind={iconTypeMan} className="toggleIcon" />
		},
		{
			value: VISNING_BESTILLING,
			label: `Bestillinger (${identArray.map(b => b.bestillingId).flat().length})`,
			icon: <Icon size={15} kind={iconTypeBestilling} className="toggleIcon" />
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
				<NavButton type="hoved" onClick={visStartBestilling}>
					Opprett personer
				</NavButton>
			</Toolbar>
			{startBestillingAktiv && (
				<BestillingsveilederModal
					onSubmit={startBestilling}
					onAvbryt={skjulStarBestilling}
					zBruker={zBruker}
				/>
			)}

			{visning === VISNING_PERSONER && <PersonListeConnector />}
			{visning === VISNING_BESTILLING && <BestillingListeConnector />}
		</div>
	)
}
