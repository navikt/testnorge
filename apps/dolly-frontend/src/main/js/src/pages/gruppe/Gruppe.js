import React, { useEffect, useState } from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import StatusListeConnector from '~/components/bestilling/statusListe/StatusListeConnector'
import Loading from '~/components/ui/loading/Loading'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import PersonListeConnector from './PersonListe/PersonListeConnector'
import BestillingListeConnector from './BestillingListe/BestillingListeConnector'
import GruppeHeader from './GruppeHeader/GruppeHeader'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import { BestillingsveilederModal } from '~/components/bestillingsveileder/startModal/StartModal'
import Icon from '~/components/ui/icon/Icon'
import FinnPerson from '~/pages/gruppeOversikt/FinnPerson'

const VISNING_PERSONER = 'personer'
const VISNING_BESTILLING = 'bestilling'

export default function Gruppe({
	getGruppe,
	deleteGruppe,
	navigerTilPerson,
	laasGruppe,
	getBestillinger,
	gruppe,
	identer,
	brukernavn,
	isFetching,
	isDeletingGruppe,
	isLockingGruppe,
	match,
	history,
	bestillingStatuser
}) {
	const [visning, setVisning] = useState(VISNING_PERSONER)
	const [startBestillingAktiv, visStartBestilling, skjulStartBestilling] = useBoolean(false)
	const [sidetall, setSidetall] = useState(0)
	const [sideStoerrelse, setSideStoerrelse] = useState(10)

	useEffect(() => {
		getBestillinger()
	}, [])

	useEffect(() => {
		getGruppe(sidetall, sideStoerrelse)
	}, [sidetall, sideStoerrelse])

	if (isFetching && !gruppe) return <Loading label="Laster personer" panel />
	if (!gruppe) {
		getGruppe(sidetall, sideStoerrelse)
		getBestillinger()
		return null
	}

	const byttVisning = event => setVisning(event.target.value)

	const identArray = Object.values(identer).filter(
		ident => ident.bestillingId != null && ident.bestillingId.length > 0
	)

	const startBestilling = values =>
		history.push(`/gruppe/${match.params.gruppeId}/bestilling`, values)

	const erLaast = gruppe.erLaast

	return (
		<div className="gruppe-container">
			<GruppeHeader
				gruppe={gruppe}
				identArray={identArray}
				deleteGruppe={deleteGruppe}
				isDeletingGruppe={isDeletingGruppe}
				laasGruppe={laasGruppe}
				isLockingGruppe={isLockingGruppe}
				bestillingStatuser={bestillingStatuser}
			/>

			<StatusListeConnector gruppeId={gruppe.id} />

			<div className="toolbar">
				<NavButton
					type="hoved"
					onClick={visStartBestilling}
					disabled={erLaast}
					title={erLaast ? 'Denne gruppen er låst, og du kan ikke legge til flere personer.' : ''}
				>
					Opprett personer
				</NavButton>

				<ToggleGruppe onChange={byttVisning} name="toggler">
					<ToggleKnapp value={VISNING_PERSONER} checked={visning === VISNING_PERSONER}>
						<Icon size={13} kind={visning === VISNING_PERSONER ? 'manLight' : 'man'} />
						{`Personer (${gruppe.antallIdenter})`}
					</ToggleKnapp>
					<ToggleKnapp value={VISNING_BESTILLING} checked={visning === VISNING_BESTILLING}>
						<Icon
							size={13}
							kind={visning === VISNING_BESTILLING ? 'bestillingLight' : 'bestilling'}
						/>
						{`Bestillinger (${Object.keys(bestillingStatuser).length})`}
					</ToggleKnapp>
				</ToggleGruppe>

				<FinnPerson naviger={navigerTilPerson} />
			</div>

			{startBestillingAktiv && (
				<BestillingsveilederModal
					onSubmit={startBestilling}
					onAvbryt={skjulStartBestilling}
					brukernavn={brukernavn}
				/>
			)}

			{visning === VISNING_PERSONER && (
				<PersonListeConnector
					iLaastGruppe={erLaast}
					sidetall={sidetall}
					sideStoerrelse={sideStoerrelse}
					setSidetall={setSidetall}
					setSideStoerrelse={setSideStoerrelse}
				/>
			)}
			{visning === VISNING_BESTILLING && <BestillingListeConnector iLaastGruppe={erLaast} />}
		</div>
	)
}
