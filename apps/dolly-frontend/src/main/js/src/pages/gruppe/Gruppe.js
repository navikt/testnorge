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
import { useNavigate, useParams } from 'react-router-dom'
import { useDispatch } from 'react-redux'
import { resetSearch } from '~/ducks/search'
import { resetNavigation } from '~/ducks/finnPerson'
import FinnPersonBestillingConnector from '~/pages/gruppeOversikt/FinnPersonBestillingConnector'

const VISNING_PERSONER = 'personer'
const VISNING_BESTILLING = 'bestilling'

export default function Gruppe({
	getGruppe,
	deleteGruppe,
	visPerson,
	visBestilling,
	laasGruppe,
	getBestillinger,
	grupper,
	selectGruppe,
	brukernavn,
	brukerBilde,
	brukerProfil,
	brukertype,
	isFetching,
	isDeletingGruppe,
	sendTags,
	isSendingTags,
	isLockingGruppe,
	getGruppeExcelFil,
	isFetchingExcel,
	bestillingStatuser,
}) {
	const [visning, setVisning] = useState(VISNING_PERSONER)
	const [startBestillingAktiv, visStartBestilling, skjulStartBestilling] = useBoolean(false)
	const [redirectToSoek, setRedirectToSoek] = useState(false)
	const [gruppe, setGruppe] = useState(null)
	const slettedeIdenter = useState([])

	const dispatch = useDispatch()
	const navigate = useNavigate()
	const { gruppeId } = useParams()

	useEffect(
		() => setVisning(visBestilling ? VISNING_BESTILLING : VISNING_PERSONER),
		[visBestilling]
	)

	useEffect(() => {
		getGruppe(gruppeId, 0, 10)
		getBestillinger(gruppeId)
	}, [gruppeId])

	useEffect(() => {
		setGruppe(selectGruppe(grupper, gruppeId))
	}, [grupper])

	if (isFetching || !gruppe) return <Loading label="Laster personer" panel />

	const byttVisning = (event) => {
		if (event?.target?.value === 'personer') {
			dispatch(resetSearch())
		}
		dispatch(resetNavigation())
		setVisning(typeof event === 'string' ? event : event.target.value)
	}

	const startBestilling = (values) => navigate(`/gruppe/${gruppeId}/bestilling`, { state: values })

	if (redirectToSoek) return navigate(`/soek`)

	const erLaast = gruppe.erLaast

	return (
		<div className="gruppe-container">
			<GruppeHeader
				gruppe={gruppe}
				slettedeIdenter={slettedeIdenter}
				deleteGruppe={deleteGruppe}
				isDeletingGruppe={isDeletingGruppe}
				sendTags={sendTags}
				isSendingTags={isSendingTags}
				laasGruppe={laasGruppe}
				isLockingGruppe={isLockingGruppe}
				bestillingStatuser={bestillingStatuser}
				getGruppeExcelFil={getGruppeExcelFil}
				isFetchingExcel={isFetchingExcel}
			/>

			<StatusListeConnector
				gruppeId={gruppe.id}
				brukerBilde={brukerBilde}
				brukerProfil={brukerProfil}
			/>

			<div className="toolbar">
				{brukertype === 'AZURE' && (
					<NavButton
						type="hoved"
						onClick={visStartBestilling}
						disabled={erLaast}
						title={erLaast ? 'Denne gruppen er låst, og du kan ikke legge til flere personer.' : ''}
					>
						Opprett personer
					</NavButton>
				)}

				{brukertype === 'BANKID' && (
					<NavButton
						type="hoved"
						onClick={() => setRedirectToSoek(true)}
						disabled={erLaast}
						title={erLaast ? 'Denne gruppen er låst, og du kan ikke legge til flere personer.' : ''}
					>
						Importer personer
					</NavButton>
				)}

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

				<FinnPersonBestillingConnector />
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
					slettedeIdenter={slettedeIdenter}
					brukertype={brukertype}
					visPerson={visPerson}
				/>
			)}
			{visning === VISNING_BESTILLING && (
				<BestillingListeConnector iLaastGruppe={erLaast} brukertype={brukertype} />
			)}
		</div>
	)
}
