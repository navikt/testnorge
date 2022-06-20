import React, { useState } from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import Loading from '~/components/ui/loading/Loading'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import PersonListeConnector from './PersonListe/PersonListeConnector'
import BestillingListeConnector from './BestillingListe/BestillingListeConnector'
import { BestillingsveilederModal } from '~/components/bestillingsveileder/startModal/StartModal'
import Icon from '~/components/ui/icon/Icon'
import { useNavigate, useParams } from 'react-router-dom'
import { useDispatch } from 'react-redux'
import FinnPersonBestillingConnector from '~/pages/gruppeOversikt/FinnPersonBestillingConnector'
import GruppeHeaderConnector from '~/pages/gruppe/GruppeHeader/GruppeHeaderConnector'
import { resetNavigering } from '~/ducks/finnPerson'
import { useCurrentBruker } from '~/utils/hooks/useBruker'
import { useGruppeById } from '~/utils/hooks/useGruppe'
import { useBestillingerGruppe } from '~/utils/hooks/useBestilling'
import StatusListeConnector from '~/components/bestilling/statusListe/StatusListeConnector'
import { ToggleGroup } from '@navikt/ds-react'

export type GruppeProps = {
	visning: string
	setVisning: Function
}

export enum VisningType {
	VISNING_PERSONER = 'personer',
	VISNING_BESTILLING = 'bestilling',
}

export default function Gruppe({ visning, setVisning }: GruppeProps) {
	const { gruppeId } = useParams()
	const {
		currentBruker: { brukernavn, brukertype },
	} = useCurrentBruker()
	const { bestillingerById, loading: loadingBestillinger } = useBestillingerGruppe(Number(gruppeId))
	const { gruppe, loading: loadingGruppe } = useGruppeById(Number(gruppeId))

	const [startBestillingAktiv, visStartBestilling, skjulStartBestilling] = useBoolean(false)
	const [redirectToSoek, setRedirectToSoek] = useState(false)

	const dispatch = useDispatch()
	const navigate = useNavigate()

	if (loadingGruppe || loadingBestillinger) {
		return <Loading label="Laster personer" panel />
	}

	const byttVisning = (value: VisningType) => {
		dispatch(resetNavigering())
		dispatch(resetPaginering())
		setVisning(value)
	}

	const startBestilling = (values: {}) =>
		navigate(`/gruppe/${gruppeId}/bestilling`, { state: values })

	if (redirectToSoek) {
		return navigate(`/soek`)
	}

	const erLaast = gruppe.erLaast

	return (
		<div className="gruppe-container">
			<GruppeHeaderConnector gruppeId={gruppe.id} />

			{bestillingerById && (
				// @ts-ignore
				<StatusListeConnector gruppeId={gruppe.id} bestillingListe={bestillingerById} />
			)}

			<div className="toolbar">
				{brukertype === 'AZURE' && (
					<NavButton
						variant={'primary'}
						onClick={visStartBestilling}
						disabled={erLaast}
						title={erLaast ? 'Denne gruppen er låst, og du kan ikke legge til flere personer.' : ''}
						style={{ marginTop: '4px' }}
					>
						Opprett personer
					</NavButton>
				)}

				{brukertype === 'BANKID' && (
					<NavButton
						variant="primary"
						onClick={() => setRedirectToSoek(true)}
						disabled={erLaast}
						title={erLaast ? 'Denne gruppen er låst, og du kan ikke legge til flere personer.' : ''}
						style={{ marginTop: '4px' }}
					>
						Importer personer
					</NavButton>
				)}

				<div style={{ marginTop: '9px' }}>
					<ToggleGroup onChange={byttVisning} defaultValue={VisningType.VISNING_PERSONER}>
						<ToggleGroup.Item value={VisningType.VISNING_PERSONER}>
							<Icon
								size={13}
								kind={visning === VisningType.VISNING_PERSONER ? 'manLight' : 'man'}
							/>
							{`Personer (${gruppe.antallIdenter})`}
						</ToggleGroup.Item>
						<ToggleGroup.Item value={VisningType.VISNING_BESTILLING}>
							<Icon
								size={13}
								kind={visning === VisningType.VISNING_BESTILLING ? 'bestillingLight' : 'bestilling'}
							/>
							{`Bestillinger (${Object.keys(bestillingerById).length})`}
						</ToggleGroup.Item>
					</ToggleGroup>
				</div>

				<FinnPersonBestillingConnector />
			</div>

			{startBestillingAktiv && (
				<BestillingsveilederModal
					onSubmit={startBestilling}
					onAvbryt={skjulStartBestilling}
					brukernavn={brukernavn}
				/>
			)}

			{visning === VisningType.VISNING_PERSONER && (
				<PersonListeConnector iLaastGruppe={erLaast} brukertype={brukertype} gruppeId={gruppeId} />
			)}
			{visning === VisningType.VISNING_BESTILLING && (
				<BestillingListeConnector
					iLaastGruppe={erLaast}
					brukertype={brukertype}
					gruppeId={gruppeId}
				/>
			)}
		</div>
	)
}
