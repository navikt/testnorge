import React, { useState } from 'react'
import { useMount } from 'react-use'
import useBoolean from '~/utils/hooks/useBoolean'
import StatusListeConnector from '~/components/bestilling/statusListe/StatusListeConnector'
import Loading from '~/components/ui/loading/Loading'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import PersonListeConnector from './PersonListe/PersonListeConnector'
import BestillingListeConnector from './BestillingListe/BestillingListeConnector'
import GruppeHeader from './GruppeHeader/GruppeHeader'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
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

	const byttVisning = event => setVisning(event.target.value)

	const identArray = Object.values(identer)

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

			<div className="toolbar">
				<NavButton type="hoved" onClick={visStartBestilling}>
					Opprett personer
				</NavButton>

				<ToggleGruppe onChange={byttVisning} name="toggler">
					<ToggleKnapp value={VISNING_PERSONER} checked={visning === VISNING_PERSONER}>
						<Icon size={13} kind={visning === VISNING_PERSONER ? 'manLight' : 'man'} />
						{`Personer (${identArray.length})`}
					</ToggleKnapp>
					<ToggleKnapp value={VISNING_BESTILLING} checked={visning === VISNING_BESTILLING}>
						<Icon
							size={13}
							kind={visning === VISNING_BESTILLING ? 'bestillingLight' : 'bestilling'}
						/>
						{`Bestillinger (${identArray.map(b => b.bestillingId).flat().length})`}
					</ToggleKnapp>
				</ToggleGruppe>

				<SearchField placeholder={searchfieldPlaceholderSelector()} />
			</div>

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
