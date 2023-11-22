import * as _ from 'lodash'
import { DollyTable } from '@/components/ui/dollyTable/DollyTable'
import ContentContainer from '@/components/ui/contentContainer/ContentContainer'
import BestillingDetaljer from '@/components/bestilling/detaljer/BestillingDetaljer'
import { BestillingIconItem } from '@/components/ui/icon/IconItem'

import Icon from '@/components/ui/icon/Icon'
import Spinner from '@/components/ui/loading/Spinner'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import bestillingStatusMapper from '@/ducks/bestillingStatus/bestillingStatusMapper'
import { CypressSelector } from '../../../../cypress/mocks/Selectors'
import { useGruppeById } from '@/utils/hooks/useGruppe'

const ikonTypeMap = {
	Ferdig: 'feedback-check-circle',
	Avvik: 'report-problem-circle',
	Feilet: 'report-problem-triangle',
	Stoppet: 'report-problem-triangle',
}

export default function BestillingListe({
	searchActive,
	iLaastGruppe,
	brukertype,
	bestillingerById,
	navigerBestillingId,
	visBestilling,
	sidetall,
	gruppeId,
	sideStoerrelse,
}) {
	if (!bestillingerById) {
		return null
	}
	const { gruppe: gruppeInfo } = useGruppeById(gruppeId)
	const bestillingListe = Object.values(bestillingerById)

	if (bestillingListe.length === 0) {
		let infoTekst =
			'Trykk på "Opprett personer"-knappen for å starte en bestilling eller "Importer personer"-knappen å kunne ' +
			'søke opp og importere identer til gruppen.'
		if (searchActive) infoTekst = 'Søket gav ingen resultater.'
		else if (brukertype === 'BANKID')
			infoTekst =
				'Trykk på "Importer personer"-knappen for å kunne søke opp og importere identer til gruppen.'

		return <ContentContainer>{infoTekst}</ContentContainer>
	}

	const sortedBestillinger = _.orderBy(bestillingListe, ['id'], ['desc'])

	const statusBestillinger = bestillingStatusMapper(Object.values(sortedBestillinger))

	const columns = [
		{
			text: 'ID',
			width: '15',
			dataField: 'listedata[0]',
			unique: true,
		},
		{
			text: 'Antall personer',
			width: '15',
			dataField: 'listedata[1]',
		},
		{
			text: 'Sist oppdatert',
			width: '20',
			dataField: 'listedata[2]',
		},
		{
			text: 'Miljø',
			width: '30',
			dataField: 'listedata[3]',
		},
		{
			text: 'Status',
			width: '10',
			dataField: 'listedata[4]',
			formatter: (cell) => {
				return cell === 'Pågår' ? (
					<Spinner size={24} />
				) : (
					<Icon
						data-cy={CypressSelector.BUTTON_OPEN_BESTILLING}
						kind={ikonTypeMap[cell]}
						title={cell}
					/>
				)
			},
		},
	]

	return (
		<ErrorBoundary>
			<DollyTable
				pagination
				gruppeDetaljer={{
					antallElementer: gruppeInfo.antallBestillinger,
					pageSize: sideStoerrelse,
				}}
				data={statusBestillinger}
				columns={columns}
				iconItem={<BestillingIconItem />}
				visBestilling={visBestilling || navigerBestillingId}
				visSide={sidetall}
				onExpand={(bestilling) => (
					<BestillingDetaljer
						bestilling={bestilling}
						iLaastGruppe={iLaastGruppe}
						brukertype={brukertype}
					/>
				)}
			/>
		</ErrorBoundary>
	)
}
