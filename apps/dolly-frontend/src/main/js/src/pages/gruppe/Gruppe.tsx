import React, { BaseSyntheticEvent, useEffect, useState } from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import StatusListeConnector from '~/components/bestilling/statusListe/StatusListeConnector'
import Loading from '~/components/ui/loading/Loading'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import PersonListeConnector from './PersonListe/PersonListeConnector'
import BestillingListeConnector from './BestillingListe/BestillingListeConnector'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import { BestillingsveilederModal } from '~/components/bestillingsveileder/startModal/StartModal'
import Icon from '~/components/ui/icon/Icon'
import { useNavigate, useParams } from 'react-router-dom'
import { useDispatch } from 'react-redux'
import FinnPersonBestillingConnector from '~/pages/gruppeOversikt/FinnPersonBestillingConnector'
import { resetNavigering } from '~/ducks/finnPerson'
import GruppeHeaderConnector from '~/pages/gruppe/GruppeHeader/GruppeHeaderConnector'

export type GruppeProps = {
	visBestilling: string
	getGruppe: (arg0: string, arg1: number, arg2: number) => void
	getBestillinger: (arg0: string) => void
	selectGruppe: (arg0: Object[], arg1: string) => any
	grupper: Object[]
	isFetching: boolean
	visning: string
	setVisning: Function
	bestillingStatuser: any
	brukerBilde: Object
	brukerProfil: Object
	brukertype: string
	brukernavn: string
	antallFjernet: number
}

export enum VisningType {
	VISNING_PERSONER = 'personer',
	VISNING_BESTILLING = 'bestilling',
}

export default function Gruppe({
	bestillingStatuser,
	brukerBilde,
	brukerProfil,
	brukernavn,
	brukertype,
	getBestillinger,
	getGruppe,
	grupper,
	visning,
	setVisning,
	isFetching,
	selectGruppe,
	antallFjernet,
}: GruppeProps) {
	const [startBestillingAktiv, visStartBestilling, skjulStartBestilling] = useBoolean(false)
	const [redirectToSoek, setRedirectToSoek] = useState(false)
	const [gruppe, setGruppe] = useState(null)

	const dispatch = useDispatch()
	const navigate = useNavigate()
	const { gruppeId } = useParams()

	useEffect(() => {
		getGruppe(gruppeId, 0, 10)
		getBestillinger(gruppeId)
	}, [gruppeId])

	useEffect(() => {
		setGruppe(selectGruppe(grupper, gruppeId))
	}, [grupper])

	if (isFetching || !gruppe) {
		return <Loading label="Laster personer" panel />
	}

	const byttVisning = (event: BaseSyntheticEvent) => {
		dispatch(resetNavigering())
		setVisning(typeof event === 'string' ? event : event.target.value)
	}

	const startBestilling = (values: {}) =>
		navigate(`/gruppe/${gruppeId}/bestilling`, { state: values })

	if (redirectToSoek) {
		return navigate(`/soek`)
	}

	const erLaast = gruppe.erLaast

	return (
		<div className="gruppe-container">
			<GruppeHeaderConnector gruppe={gruppe} bestillingStatuser={bestillingStatuser} />

			<StatusListeConnector
				gruppeId={gruppe.id}
				brukerBilde={brukerBilde}
				brukerProfil={brukerProfil}
			/>

			<div className="toolbar" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', marginBottom: '0' }}>
				<div style={{ display: 'flex', flexDirection: 'row', alignItems: 'center', width: '100%' }}>
					{brukertype === 'AZURE' && (
						<NavButton
							type="hoved"
							onClick={visStartBestilling}
							disabled={erLaast}
							title={erLaast ? 'Denne gruppen er låst, og du kan ikke legge til flere personer.' : ''}
							style={{ marginTop: '5px', marginBottom: '5px', marginRight: '10px' }}
						>
							Opprett personer
						</NavButton>
					)}

					<NavButton
						type="standard"
						onClick={() => navigate(`/testnorge`)}
						disabled={erLaast}
						title={erLaast ? 'Denne gruppen er låst, og du kan ikke legge til flere personer.' : ''}
						style={{ marginTop: '5px', marginBottom: '5px', }}
					>
						Importer personer
					</NavButton>

					<div style={{ flexGrow: '2' }}></div>

					<FinnPersonBestillingConnector />
				</div>
				<div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', marginTop: '20px', marginBottom: '10px' }}>
						<ToggleGruppe onChange={byttVisning} name="toggler">
							<ToggleKnapp
								value={VisningType.VISNING_PERSONER}
								checked={visning === VisningType.VISNING_PERSONER}
							>
								<Icon
									size={13}
									kind={visning === VisningType.VISNING_PERSONER ? 'manLight' : 'man'}
								/>
								{`Personer (${gruppe.antallIdenter - antallFjernet})`}
							</ToggleKnapp>
							<ToggleKnapp
								value={VisningType.VISNING_BESTILLING}
								checked={visning === VisningType.VISNING_BESTILLING}
							>
								<Icon
									size={13}
									kind={visning === VisningType.VISNING_BESTILLING ? 'bestillingLight' : 'bestilling'}
								/>
								{`Bestillinger (${Object.keys(bestillingStatuser).length})`}
							</ToggleKnapp>
						</ToggleGruppe>
				</div>
			</div>

			{startBestillingAktiv && (
				<BestillingsveilederModal
					onSubmit={startBestilling}
					onAvbryt={skjulStartBestilling}
					brukernavn={brukernavn}
				/>
			)}

			{visning === VisningType.VISNING_PERSONER && (
				<PersonListeConnector iLaastGruppe={erLaast} brukertype={brukertype} />
			)}
			{visning === VisningType.VISNING_BESTILLING && (
				<BestillingListeConnector iLaastGruppe={erLaast} brukertype={brukertype} />
			)}
		</div>
	)
}
